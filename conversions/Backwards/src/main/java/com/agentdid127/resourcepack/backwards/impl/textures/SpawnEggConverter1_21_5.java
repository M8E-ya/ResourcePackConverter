package com.agentdid127.resourcepack.backwards.impl.textures;

import com.agentdid127.resourcepack.library.Converter;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.pack.Pack;
import com.agentdid127.resourcepack.library.utilities.Logger;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpawnEggConverter1_21_5 extends Converter {

    private static final String[] MOBS = {
            "allay", "armadillo", "axolotl", "bat", "bee", "blaze", "bogged", "breeze",
            "camel", "cat", "cave_spider", "chicken", "cod", "cow", "creeper", "dolphin",
            "donkey", "drowned", "elder_guardian", "ender_dragon", "enderman", "endermite",
            "evoker", "fox", "frog", "ghast", "glow_squid", "goat", "guardian", "hoglin",
            "horse", "husk", "iron_golem", "llama", "magma_cube", "mooshroom", "mule",
            "ocelot", "panda", "parrot", "phantom", "pig", "piglin", "piglin_brute",
            "pillager", "polar_bear", "pufferfish", "rabbit", "ravager", "salmon",
            "sheep", "shulker", "silverfish", "skeleton", "skeleton_horse", "slime",
            "sniffer", "snow_golem", "spider", "squid", "stray", "strider", "tadpole",
            "trader_llama", "tropical_fish", "turtle", "vex", "villager", "vindicator",
            "wandering_trader", "warden", "witch", "wither", "wither_skeleton", "wolf",
            "zoglin", "zombie", "zombie_horse", "zombie_villager", "zombified_piglin"
    };

    public SpawnEggConverter1_21_5(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public boolean shouldConvert(Gson gson, int from, int to) {
        // Converting to version prior to 1.21.5 (pack format 55)
        return from >= 55 && to < 55;
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path itemDir = pack.getWorkingPath().resolve("assets/minecraft/textures/item");
        if (!itemDir.toFile().exists()) {
            return;
        }

        Path baseSpawnEgg = itemDir.resolve("spawn_egg.png");
        boolean baseRestored = baseSpawnEgg.toFile().exists();

        for (String mob : MOBS) {
            Path mobEgg = itemDir.resolve(mob + "_spawn_egg.png");
            if (mobEgg.toFile().exists()) {
                if (!baseRestored) {
                    Logger.log("Restoring spawn_egg.png from " + mob + "_spawn_egg.png");
                    Files.copy(mobEgg, baseSpawnEgg);
                    baseRestored = true;
                }
                Logger.log("Deleting 1.21.5+ spawn egg: " + mobEgg.getFileName());
                Files.deleteIfExists(mobEgg);
            }
        }
    }
}
