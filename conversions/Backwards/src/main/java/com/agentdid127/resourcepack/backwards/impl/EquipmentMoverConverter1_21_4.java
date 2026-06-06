package com.agentdid127.resourcepack.backwards.impl;

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
 * Backwards equivalent of EquipmentMoverConverter1_21_4.
 * Reverts 1.21.4+ equipment layout changes back to the pre-1.21.4 format:
 *
 * 1. equipment/ -> models/equipment/
 * 2. elytra_broken -> broken_elytra
 */
public class EquipmentMoverConverter1_21_4 extends Converter {

    public EquipmentMoverConverter1_21_4(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public boolean shouldConvert(Gson gson, int from, int to) {
        return from >= Util.getVersionProtocol(gson, "1.21.4")
                && to < Util.getVersionProtocol(gson, "1.21.4");
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path minecraftPath = pack.getWorkingPath().resolve("assets/minecraft".replace("/", File.separator));

        revertEquipmentDirectory(minecraftPath);
        revertElytra(minecraftPath);
    }

    private void revertEquipmentDirectory(Path minecraftPath) throws IOException {
        Path newEquipmentPath = minecraftPath.resolve("equipment");
        Path oldEquipmentPath = minecraftPath.resolve("models/equipment".replace("/", File.separator));

        if (!newEquipmentPath.toFile().exists()) {
            return;
        }

        Path modelsPath = minecraftPath.resolve("models");
        if (!modelsPath.toFile().exists()) {
            modelsPath.toFile().mkdirs();
        }

        if (oldEquipmentPath.toFile().exists()) {
            Logger.log("models/equipment/ already exists, merging equipment/ into it.");
            FileUtil.mergeDirectories(oldEquipmentPath.toFile(), newEquipmentPath.toFile());
            FileUtil.deleteDirectoryAndContents(newEquipmentPath);
        } else {
            Logger.log("Moving equipment/ -> models/equipment/");
            Files.move(newEquipmentPath, oldEquipmentPath);
        }
    }

    private void revertElytra(Path minecraftPath) throws IOException {
        Path itemModelsPath = minecraftPath.resolve("models/item".replace("/", File.separator));
        renameFile(itemModelsPath, "elytra_broken.json", "broken_elytra.json");

        Path itemTexturesPath = minecraftPath.resolve("textures/item".replace("/", File.separator));
        renameFile(itemTexturesPath, "elytra_broken.png", "broken_elytra.png");
        renameFile(itemTexturesPath, "elytra_broken.png.mcmeta", "broken_elytra.png.mcmeta");
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
