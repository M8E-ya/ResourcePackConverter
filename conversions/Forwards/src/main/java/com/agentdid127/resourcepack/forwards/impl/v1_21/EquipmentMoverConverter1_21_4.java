package com.agentdid127.resourcepack.forwards.impl.v1_21;

import com.agentdid127.resourcepack.library.Converter;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.pack.Pack;
import com.agentdid127.resourcepack.library.utilities.FileUtil;
import com.agentdid127.resourcepack.library.utilities.Logger;
import com.agentdid127.resourcepack.library.utilities.Util;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles resource pack changes introduced in Minecraft 1.21.4 (pack format 46):
 *
 * 1. The equipment model directory moved from models/equipment/ to equipment/
 *    (one level up, now a top-level sibling of models/).
 *
 * 2. The broken elytra model/texture was renamed:
 *    broken_elytra -> elytra_broken
 */
public class EquipmentMoverConverter1_21_4 extends Converter {

    public EquipmentMoverConverter1_21_4(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public boolean shouldConvert(Gson gson, int from, int to) {
        // 1.21.4 is pack format 46
        return from < Util.getVersionProtocol(gson, "1.21.4")
                && to >= Util.getVersionProtocol(gson, "1.21.4");
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path minecraftPath = pack.getWorkingPath().resolve("assets/minecraft".replace("/", File.separator));

        migrateEquipmentDirectory(minecraftPath);
        renameElytra(minecraftPath);
    }

    /**
     * Moves assets/minecraft/models/equipment/ -> assets/minecraft/equipment/
     * This was moved from inside models/ to a top-level directory in 1.21.4.
     */
    private void migrateEquipmentDirectory(Path minecraftPath) throws IOException {
        Path oldEquipmentPath = minecraftPath.resolve("models/equipment".replace("/", File.separator));
        Path newEquipmentPath = minecraftPath.resolve("equipment");

        if (!oldEquipmentPath.toFile().exists()) {
            return;
        }

        if (newEquipmentPath.toFile().exists()) {
            // Merge into existing directory rather than clobbering
            Logger.log("equipment/ already exists, merging models/equipment/ into it.");
            FileUtil.mergeDirectories(newEquipmentPath.toFile(), oldEquipmentPath.toFile());
            FileUtil.deleteDirectoryAndContents(oldEquipmentPath);
        } else {
            Logger.log("Moving models/equipment/ -> equipment/");
            Files.move(oldEquipmentPath, newEquipmentPath);
        }

        // Clean up empty models/equipment parent if nothing else is in models/
        Path modelsEquipmentParent = oldEquipmentPath.getParent(); // models/
        if (modelsEquipmentParent.toFile().exists()) {
            File[] remaining = modelsEquipmentParent.toFile().listFiles();
            if (remaining != null && remaining.length == 0) {
                modelsEquipmentParent.toFile().delete();
            }
        }
    }

    /**
     * Renames broken_elytra -> elytra_broken in both models and textures.
     * This was a straightforward file rename in 1.21.4.
     */
    private void renameElytra(Path minecraftPath) throws IOException {
        // Rename in models/item/
        Path itemModelsPath = minecraftPath.resolve("models/item".replace("/", File.separator));
        renameFile(itemModelsPath, "broken_elytra.json", "elytra_broken.json");

        // Rename in textures/item/
        Path itemTexturesPath = minecraftPath.resolve("textures/item".replace("/", File.separator));
        renameFile(itemTexturesPath, "broken_elytra.png", "elytra_broken.png");
        renameFile(itemTexturesPath, "broken_elytra.png.mcmeta", "elytra_broken.png.mcmeta");
    }

    private void renameFile(Path dir, String oldName, String newName) throws IOException {
        Path oldPath = dir.resolve(oldName);
        Path newPath = dir.resolve(newName);
        if (oldPath.toFile().exists()) {
            if (newPath.toFile().exists()) {
                newPath.toFile().delete();
            }
            Files.move(oldPath, newPath);
            Logger.log("Renamed: " + oldName + " -> " + newName);
        }
    }
}
