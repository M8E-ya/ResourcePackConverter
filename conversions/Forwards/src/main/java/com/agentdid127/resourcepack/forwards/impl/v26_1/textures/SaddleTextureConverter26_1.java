package com.agentdid127.resourcepack.forwards.impl.v26_1.textures;

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
 * Handles saddle and mount texture restructuring introduced in Minecraft 26.1 (pack format 84).
 *
 * In 26.1, saddle textures for camel, horse, donkey, mule, skeleton_horse, and zombie_horse
 * were split out of the entity base textures and moved to entity/equipment/<mount>_saddle/saddle.png
 *
 * Also handles:
 * - entity/armorstand/wood.png -> entity/armorstand/wooden.png (renamed)
 */
public class SaddleTextureConverter26_1 extends Converter {

    public SaddleTextureConverter26_1(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public boolean shouldConvert(Gson gson, int from, int to) {
        return from < Util.getVersionProtocol(gson, "26.1")
                && to >= Util.getVersionProtocol(gson, "26.1");
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path entityPath = pack.getWorkingPath()
                .resolve("assets/minecraft/textures/entity".replace("/", File.separator));
        if (!entityPath.toFile().exists()) {
            return;
        }

        // Saddle textures are split from base entity textures into equipment layers.
        // The base entity textures no longer include saddle graphics in 26.1.
        // Since the old textures combined saddle+body into a single PNG, we can't
        // automatically extract just the saddle portion. We log a warning instead.
        logSaddleWarning(entityPath, "camel/camel.png", "camel_saddle");
        logSaddleWarning(entityPath, "horse/horse_white.png", "horse_saddle");
        logSaddleWarning(entityPath, "horse/donkey.png", "donkey_saddle");
        logSaddleWarning(entityPath, "horse/mule.png", "mule_saddle");
        logSaddleWarning(entityPath, "horse/horse_skeleton.png", "skeleton_horse_saddle");
        logSaddleWarning(entityPath, "horse/horse_zombie.png", "zombie_horse_saddle");

        // armor stand: wood -> wooden rename
        renameArmorStand(entityPath);
    }

    private void logSaddleWarning(Path entityPath, String entityTexture, String saddleEquipmentDir) {
        Path entityTex = entityPath.resolve(entityTexture.replace("/", File.separator));
        if (entityTex.toFile().exists()) {
            Logger.log("[WARNING] " + entityTexture + ": In 26.1, the saddle graphic was split into"
                    + " entity/equipment/" + saddleEquipmentDir + "/saddle.png."
                    + " Please manually create that file from the saddle portion of your custom texture.");
        }
    }

    private void renameArmorStand(Path entityPath) throws IOException {
        Path armorstandFolder = entityPath.resolve("armorstand");
        if (!armorstandFolder.toFile().exists()) return;

        Path oldWood = armorstandFolder.resolve("wood.png");
        Path newWood = armorstandFolder.resolve("wooden.png");
        if (oldWood.toFile().exists()) {
            if (newWood.toFile().exists()) newWood.toFile().delete();
            Files.move(oldWood, newWood);
            Logger.log("Renamed: entity/armorstand/wood.png -> wooden.png");
        }
        Path oldMeta = armorstandFolder.resolve("wood.png.mcmeta");
        Path newMeta = armorstandFolder.resolve("wooden.png.mcmeta");
        if (oldMeta.toFile().exists()) {
            if (newMeta.toFile().exists()) newMeta.toFile().delete();
            Files.move(oldMeta, newMeta);
        }
    }
}
