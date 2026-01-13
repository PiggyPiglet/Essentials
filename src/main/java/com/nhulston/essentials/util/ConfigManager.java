package com.nhulston.essentials.util;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final int DEFAULT_MAX_HOMES = 5;

    private final Path configPath;
    private int maxHomes = DEFAULT_MAX_HOMES;

    public ConfigManager(@Nonnull Path dataFolder) {
        this.configPath = dataFolder.resolve("config.toml");
        load();
    }

    private void load() {
        if (!Files.exists(configPath)) {
            createDefault();
        }

        try {
            TomlParseResult config = Toml.parse(configPath);

            if (config.hasErrors()) {
                config.errors().forEach(error -> Log.error("Config error: " + error.toString()));
                Log.warning("Using default config values due to errors.");
                return;
            }

            maxHomes = Math.toIntExact(config.getLong("homes.max-homes", () -> (long) DEFAULT_MAX_HOMES));

            Log.info("Config loaded: maxHomes=" + maxHomes);
        } catch (IOException e) {
            Log.error("Failed to load config: " + e.getMessage());
            Log.warning("Using default config values.");
        }
    }

    private void createDefault() {
        try {
            Files.createDirectories(configPath.getParent());

            try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.toml")) {
                if (is != null) {
                    Files.copy(is, configPath);
                    Log.info("Created default config.");
                    return;
                }
            }

            Log.error("Could not find config.toml in resources.");
        } catch (IOException e) {
            Log.error("Failed to create default config: " + e.getMessage());
        }
    }

    public int getMaxHomes() {
        return maxHomes;
    }
}
