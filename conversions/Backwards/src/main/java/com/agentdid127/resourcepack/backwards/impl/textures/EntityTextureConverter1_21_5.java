package com.agentdid127.resourcepack.backwards.impl.textures;

import com.agentdid127.resourcepack.library.Converter;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.pack.Pack;
import com.agentdid127.resourcepack.library.utilities.Logger;
import com.agentdid127.resourcepack.library.utilities.Util;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Backwards equivalent of EntityTextureConverter1_21_5.
 * Reverts entity texture renames back to the pre-1.21.5 format.
 */
public class EntityTextureConverter1_21_5 extends Converter {

    public EntityTextureConverter1_21_5(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public boolean shouldConvert(Gson gson, int from, int to) {
        return from >= Util.getVersionProtocol(gson, "1.21.5")
                && to < Util.getVersionProtocol(gson, "1.21.5");
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path entityPath = pack.getWorkingPath()
                .resolve("assets/minecraft/textures/entity".replace("/", File.separator));
        if (!entityPath.toFile().exists()) {
            return;
        }

        // sheep_wool -> sheep_fur
        revertSheepFur(entityPath);

        // pig/temperate_pig.png -> pig.png
        moveFromSubfolder(entityPath, "pig", "temperate_pig.png", "pig.png");
        moveFromSubfolder(entityPath, "pig", "temperate_pig.png.mcmeta", "pig.png.mcmeta");

        // cow/temperate_cow.png -> cow.png
        moveFromSubfolder(entityPath, "cow", "temperate_cow.png", "cow.png");
        moveFromSubfolder(entityPath, "cow", "temperate_cow.png.mcmeta", "cow.png.mcmeta");

        // chicken/temperate_chicken.png -> chicken.png
        moveFromSubfolder(entityPath, "chicken", "temperate_chicken.png", "chicken.png");
        moveFromSubfolder(entityPath, "chicken", "temperate_chicken.png.mcmeta", "chicken.png.mcmeta");

        // Revert saddle equipment textures back into mob folders
        revertSaddle(entityPath, "pig_saddle", "pig", "pig_saddle.png");
        revertSaddle(entityPath, "strider_saddle", "strider", "strider_saddle.png");
    }

    private void revertSheepFur(Path entityPath) throws IOException {
        Path sheepFolder = entityPath.resolve("sheep");
        if (!sheepFolder.toFile().exists()) return;

        Path newWool = sheepFolder.resolve("sheep_wool.png");
        Path oldFur = sheepFolder.resolve("sheep_fur.png");
        if (newWool.toFile().exists()) {
            if (oldFur.toFile().exists()) oldFur.toFile().delete();
            Files.move(newWool, oldFur);
            Logger.log("Reverted: entity/sheep/sheep_wool.png -> sheep_fur.png");
        }
        Path newMeta = sheepFolder.resolve("sheep_wool.png.mcmeta");
        Path oldMeta = sheepFolder.resolve("sheep_fur.png.mcmeta");
        if (newMeta.toFile().exists()) {
            if (oldMeta.toFile().exists()) oldMeta.toFile().delete();
            Files.move(newMeta, oldMeta);
        }
    }

    private void moveFromSubfolder(Path entityPath, String subfolder, String fileName, String newName) throws IOException {
        Path subFile = entityPath.resolve(subfolder).resolve(fileName);
        if (!subFile.toFile().exists()) return;

        Path dest = entityPath.resolve(newName);
        if (dest.toFile().exists()) dest.toFile().delete();
        Files.move(subFile, dest);
        Logger.log("Reverted: entity/" + subfolder + "/" + fileName + " -> entity/" + newName);
    }

    private void revertSaddle(Path entityPath, String equipmentFolder, String mobFolder, String saddleFile) throws IOException {
        Path equipSaddle = entityPath.resolve("equipment").resolve(equipmentFolder).resolve("saddle.png");
        if (!equipSaddle.toFile().exists()) return;

        Path mobPath = entityPath.resolve(mobFolder);
        if (!mobPath.toFile().exists()) mobPath.toFile().mkdirs();

        Path dest = mobPath.resolve(saddleFile);
        if (dest.toFile().exists()) dest.toFile().delete();
        Files.move(equipSaddle, dest);
        Logger.log("Reverted saddle: entity/equipment/" + equipmentFolder + "/saddle.png -> entity/" + mobFolder + "/" + saddleFile);
    }
}
