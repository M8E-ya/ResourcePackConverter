package com.agentdid127.resourcepack.forwards.impl.v1_21.textures;

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
 * Handles entity texture renames and folder restructuring in Minecraft 1.21.5 (pack format 55).
 *
 * Changes:
 * - textures/entity/sheep/sheep_fur.png -> textures/entity/sheep/sheep_wool.png
 * - textures/entity/pig.png             -> textures/entity/pig/temperate_pig.png
 * - textures/entity/cow.png             -> textures/entity/cow/temperate_cow.png
 * - textures/entity/chicken.png         -> textures/entity/chicken/temperate_chicken.png
 * - textures/entity/pig/pig_saddle.png  -> textures/entity/equipment/pig_saddle/saddle.png
 * - textures/entity/strider/strider_saddle.png -> textures/entity/equipment/strider_saddle/saddle.png
 */
public class EntityTextureConverter1_21_5 extends Converter {

    public EntityTextureConverter1_21_5(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public boolean shouldConvert(Gson gson, int from, int to) {
        return from < Util.getVersionProtocol(gson, "1.21.5")
                && to >= Util.getVersionProtocol(gson, "1.21.5");
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path entityPath = pack.getWorkingPath()
                .resolve("assets/minecraft/textures/entity".replace("/", File.separator));
        if (!entityPath.toFile().exists()) {
            return;
        }

        // sheep_fur -> sheep_wool
        renameSheepFur(entityPath);

        // pig.png -> pig/temperate_pig.png
        moveToSubfolder(entityPath, "pig.png", "pig", "temperate_pig.png");
        moveToSubfolder(entityPath, "pig.png.mcmeta", "pig", "temperate_pig.png.mcmeta");

        // cow.png -> cow/temperate_cow.png
        moveToSubfolder(entityPath, "cow.png", "cow", "temperate_cow.png");
        moveToSubfolder(entityPath, "cow.png.mcmeta", "cow", "temperate_cow.png.mcmeta");

        // chicken.png -> chicken/temperate_chicken.png
        moveToSubfolder(entityPath, "chicken.png", "chicken", "temperate_chicken.png");
        moveToSubfolder(entityPath, "chicken.png.mcmeta", "chicken", "temperate_chicken.png.mcmeta");

        // Saddle texture splits: pig_saddle and strider saddle move to equipment/
        migrateSaddle(entityPath, "pig", "pig_saddle.png", "pig_saddle");
        migrateSaddle(entityPath, "strider", "strider_saddle.png", "strider_saddle");
    }

    private void renameSheepFur(Path entityPath) throws IOException {
        Path sheepFolder = entityPath.resolve("sheep");
        if (!sheepFolder.toFile().exists()) return;

        Path oldFur = sheepFolder.resolve("sheep_fur.png");
        Path newFur = sheepFolder.resolve("sheep_wool.png");
        if (oldFur.toFile().exists()) {
            if (newFur.toFile().exists()) newFur.toFile().delete();
            Files.move(oldFur, newFur);
            Logger.log("Renamed: entity/sheep/sheep_fur.png -> sheep_wool.png");
        }
        Path oldFurMeta = sheepFolder.resolve("sheep_fur.png.mcmeta");
        Path newFurMeta = sheepFolder.resolve("sheep_wool.png.mcmeta");
        if (oldFurMeta.toFile().exists()) {
            if (newFurMeta.toFile().exists()) newFurMeta.toFile().delete();
            Files.move(oldFurMeta, newFurMeta);
        }
    }

    /**
     * Moves a texture file from entityPath/<fileName> to entityPath/<subfolder>/<newName>.
     * If the subfolder doesn't exist yet, creates it.
     */
    private void moveToSubfolder(Path entityPath, String fileName, String subfolder, String newName) throws IOException {
        Path oldFile = entityPath.resolve(fileName);
        if (!oldFile.toFile().exists()) return;

        Path subfolderPath = entityPath.resolve(subfolder);
        if (!subfolderPath.toFile().exists()) {
            subfolderPath.toFile().mkdirs();
        }

        Path newFile = subfolderPath.resolve(newName);
        if (newFile.toFile().exists()) newFile.toFile().delete();
        Files.move(oldFile, newFile);
        Logger.log("Moved: entity/" + fileName + " -> entity/" + subfolder + "/" + newName);
    }

    /**
     * Moves a saddle texture from entityPath/<mobFolder>/<saddleFile>
     * to entityPath/equipment/<equipmentFolder>/saddle.png
     */
    private void migrateSaddle(Path entityPath, String mobFolder, String saddleFile, String equipmentFolder) throws IOException {
        Path oldSaddle = entityPath.resolve(mobFolder).resolve(saddleFile);
        if (!oldSaddle.toFile().exists()) return;

        Path equipPath = entityPath.resolve("equipment").resolve(equipmentFolder);
        if (!equipPath.toFile().exists()) {
            equipPath.toFile().mkdirs();
        }

        Path newSaddle = equipPath.resolve("saddle.png");
        if (newSaddle.toFile().exists()) newSaddle.toFile().delete();
        Files.move(oldSaddle, newSaddle);
        Logger.log("Moved saddle: entity/" + mobFolder + "/" + saddleFile
                + " -> entity/equipment/" + equipmentFolder + "/saddle.png");
    }
}
