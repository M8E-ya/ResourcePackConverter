package com.agentdid127.resourcepack.backwards.impl.textures;

import com.agentdid127.resourcepack.library.Converter;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.pack.Pack;
import com.agentdid127.resourcepack.library.utilities.Logger;
import com.agentdid127.resourcepack.library.utilities.Util;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class HorseArmorTextureConverter1_21_10 extends Converter {

    public HorseArmorTextureConverter1_21_10(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public boolean shouldConvert(Gson gson, int from, int to) {
        return from >= Util.getVersionProtocol(gson, "1.21.10")
                && to < Util.getVersionProtocol(gson, "1.21.10");
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path entityPath = pack.getWorkingPath()
                .resolve("assets/minecraft/textures/entity".replace("/", File.separator));
        if (!entityPath.toFile().exists()) {
            return;
        }

        Path horseArmorPath = entityPath.resolve("horse".replace("/", File.separator)).resolve("armor");
        if (horseArmorPath.toFile().exists() && horseArmorPath.resolve("horse_armor_leather_overlay.png").toFile().exists()) {
            Logger.log("[WARNING] When downgrading from 1.21.10+, leather horse armor is merged into one file.");
            Logger.log("[WARNING] You must manually merge horse_armor_leather.png and horse_armor_leather_overlay.png.");
        }
    }
}
