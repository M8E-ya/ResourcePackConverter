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

public class CelestialTextureConverter1_21_10 extends Converter {

    public CelestialTextureConverter1_21_10(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public boolean shouldConvert(Gson gson, int from, int to) {
        return from >= Util.getVersionProtocol(gson, "1.21.10")
                && to < Util.getVersionProtocol(gson, "1.21.10");
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path envPath = pack.getWorkingPath()
                .resolve("assets/minecraft/textures/environment".replace("/", File.separator));
        Path celestialPath = envPath.resolve("celestial");
        
        if (!celestialPath.toFile().exists()) {
            return;
        }

        // Revert sun and end_flash
        moveFile(celestialPath, envPath, "sun.png");
        moveFile(celestialPath, envPath, "sun.png.mcmeta");
        moveFile(celestialPath, envPath, "end_flash.png");
        moveFile(celestialPath, envPath, "end_flash.png.mcmeta");

        // Warn for moon phases
        Path moonDir = celestialPath.resolve("moon");
        if (moonDir.toFile().exists() && moonDir.toFile().list() != null && moonDir.toFile().list().length > 0) {
            Logger.log("[WARNING] Downward converting from 1.21.10+: The moon textures in environment/celestial/moon/ cannot be automatically merged back into a single moon_phases.png spritesheet. You will need to manually stitch them together.");
        }
    }

    private void moveFile(Path sourceDir, Path destDir, String fileName) throws IOException {
        Path oldPath = sourceDir.resolve(fileName);
        Path newPath = destDir.resolve(fileName);
        if (oldPath.toFile().exists()) {
            if (newPath.toFile().exists()) {
                newPath.toFile().delete();
            }
            Files.move(oldPath, newPath);
            Logger.log("Reverted: environment/celestial/" + fileName + " -> environment/" + fileName);
        }
    }
}
