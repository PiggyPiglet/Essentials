package com.nhulston.essentials.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nhulston.essentials.models.PlayerData;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class StorageManager {
    private final Path playersFolder;
    private final Gson gson;
    private final ConcurrentHashMap<UUID, PlayerData> cache;

    public StorageManager(@Nonnull Path dataFolder) {
        this.playersFolder = dataFolder.resolve("players");
        this.gson = new GsonBuilder().create();
        this.cache = new ConcurrentHashMap<>();

        try {
            Files.createDirectories(this.playersFolder);
        } catch (IOException e) {
            Log.error("Failed to create players folder: " + e.getMessage());
        }
    }

    @Nonnull
    public PlayerData getPlayerData(@Nonnull UUID playerUuid) {
        return cache.computeIfAbsent(playerUuid, this::loadPlayerData);
    }

    public void savePlayerData(@Nonnull UUID playerUuid) {
        PlayerData data = cache.get(playerUuid);
        if (data != null) {
            savePlayerDataAsync(playerUuid, data);
        }
    }

    @Nonnull
    private PlayerData loadPlayerData(@Nonnull UUID playerUuid) {
        Path file = getPlayerFile(playerUuid);
        if (Files.exists(file)) {
            try {
                String json = Files.readString(file);
                PlayerData data = gson.fromJson(json, PlayerData.class);
                if (data != null) {
                    return data;
                }
            } catch (IOException e) {
                Log.warning("Failed to load player data for " + playerUuid + ": " + e.getMessage());
            }
        }
        return new PlayerData();
    }

    private void savePlayerDataAsync(@Nonnull UUID playerUuid, @Nonnull PlayerData data) {
        CompletableFuture.runAsync(() -> {
            Path file = getPlayerFile(playerUuid);
            try {
                String json = gson.toJson(data);
                Files.writeString(file, json);
            } catch (IOException e) {
                Log.error("Failed to save player data for " + playerUuid + ": " + e.getMessage());
            }
        });
    }

    @Nonnull
    private Path getPlayerFile(@Nonnull UUID playerUuid) {
        return playersFolder.resolve(playerUuid + ".json");
    }

    public void unloadPlayer(@Nonnull UUID playerUuid) {
        cache.remove(playerUuid);
    }

    public void shutdown() {
        for (Map.Entry<UUID, PlayerData> entry : cache.entrySet()) {
            Path file = getPlayerFile(entry.getKey());
            try {
                String json = gson.toJson(entry.getValue());
                Files.writeString(file, json);
            } catch (IOException e) {
                Log.error("Failed to save player data on shutdown for " + entry.getKey() + ": " + e.getMessage());
            }
        }
        cache.clear();
    }
}
