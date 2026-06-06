package com.agentdid127.resourcepack.forwards.impl.v1_21.textures;

import com.agentdid127.resourcepack.library.Converter;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.pack.Pack;
import com.agentdid127.resourcepack.library.utilities.ImageConverter;
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
        // 1.21.10 is pack format 75
        return from < Util.getVersionProtocol(gson, "1.21.10")
                && to >= Util.getVersionProtocol(gson, "1.21.10");
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path envPath = pack.getWorkingPath()
                .resolve("assets/minecraft/textures/environment".replace("/", File.separator));
        if (!envPath.toFile().exists()) {
            return;
        }

        Path celestialPath = envPath.resolve("celestial");
        if (!celestialPath.toFile().exists()) {
            celestialPath.toFile().mkdirs();
        }

        // Move sun and end_flash
        moveFile(envPath, celestialPath, "sun.png");
        moveFile(envPath, celestialPath, "sun.png.mcmeta");
        moveFile(envPath, celestialPath, "end_flash.png");
        moveFile(envPath, celestialPath, "end_flash.png.mcmeta");

        // Slice moon phases
        sliceMoonPhases(envPath, celestialPath);
    }

    private void moveFile(Path sourceDir, Path destDir, String fileName) throws IOException {
        Path oldPath = sourceDir.resolve(fileName);
        Path newPath = destDir.resolve(fileName);
        if (oldPath.toFile().exists()) {
            if (newPath.toFile().exists()) {
                newPath.toFile().delete();
            }
            Files.move(oldPath, newPath);
            Logger.log("Moved: environment/" + fileName + " -> environment/celestial/" + fileName);
        }
    }

    private void sliceMoonPhases(Path envPath, Path celestialPath) throws IOException {
        Path moonPhasesPath = envPath.resolve("moon_phases.png");
        if (!moonPhasesPath.toFile().exists()) {
            return;
        }

        Path moonDir = celestialPath.resolve("moon");
        if (!moonDir.toFile().exists()) {
            moonDir.toFile().mkdirs();
        }

        try {
            ImageConverter converter = new ImageConverter(null, null, moonPhasesPath);
            int width = converter.getWidth();
            int height = converter.getHeight();
            int phaseWidth = width / 4;
            int phaseHeight = height / 2;

            String[] phases = {
                    "full_moon.png", "waning_gibbous.png", "third_quarter.png", "waning_crescent.png",
                    "new_moon.png", "waxing_crescent.png", "first_quarter.png", "waxing_gibbous.png"
            };

            int i = 0;
            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 4; x++) {
                    Path destPath = moonDir.resolve(phases[i]);
                    converter.saveSlice(x * phaseWidth, y * phaseHeight, phaseWidth, phaseHeight, destPath);
                    Logger.log("Sliced moon phase: " + phases[i]);
                    i++;
                }
            }

            moonPhasesPath.toFile().delete();
            Path moonPhasesMeta = envPath.resolve("moon_phases.png.mcmeta");
            if (moonPhasesMeta.toFile().exists()) {
                moonPhasesMeta.toFile().delete();
            }
        } catch (Exception e) {
            Logger.log("[ERROR] Failed to slice moon_phases.png: " + e.getMessage());
        }
    }
}
