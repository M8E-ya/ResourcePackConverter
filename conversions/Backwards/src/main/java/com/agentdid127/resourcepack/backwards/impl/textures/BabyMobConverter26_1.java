package com.agentdid127.resourcepack.backwards.impl.textures;

import com.agentdid127.resourcepack.library.Converter;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.pack.Pack;
import com.agentdid127.resourcepack.library.utilities.Logger;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BabyMobConverter26_1 extends Converter {

    private static final String[] BABY_MOBS = {
            "pig/piglet.png", "cow/calf.png", "wolf/wolf_pup.png",
            "baby_chicken.png", "cat/kitten.png", "baby_ocelot.png",
            "sheep/lamb.png", "rabbit/baby_rabbit.png", "hoglin/baby_hoglin.png",
            "zoglin/baby_zoglin.png", "strider/baby_strider.png", "sniffer/snifflet.png",
            "panda/baby_panda.png"
    };

    public BabyMobConverter26_1(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public boolean shouldConvert(Gson gson, int from, int to) {
        // Converting to version prior to 26.1 (pack format 84)
        return from >= 84 && to < 84;
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path entityDir = pack.getWorkingPath().resolve("assets/minecraft/textures/entity");
        if (!entityDir.toFile().exists()) {
            return;
        }

        for (String babyMob : BABY_MOBS) {
            Path babyTex = entityDir.resolve(babyMob);
            if (babyTex.toFile().exists()) {
                Logger.log("Deleting 26.1+ baby mob texture for backwards compatibility: " + babyMob);
                Files.deleteIfExists(babyTex);
            }
        }
    }
}
