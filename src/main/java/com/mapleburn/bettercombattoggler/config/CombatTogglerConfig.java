package com.mapleburn.bettercombattoggler.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CombatTogglerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_DIR = "config/bettercombattoggler";
    private static final String CONFIG_FILE = "config.json";
    
    private static boolean combatEnabled = true; // Default to enabled

    public static void loadConfig() {
        try {
            Path configPath = getConfigPath();
            File configFile = configPath.toFile();
            
            if (configFile.exists()) {
                try (FileReader reader = new FileReader(configFile)) {
                    JsonObject json = GSON.fromJson(reader, JsonObject.class);
                    if (json != null && json.has("combatEnabled")) {
                        combatEnabled = json.get("combatEnabled").getAsBoolean();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load Better Combat Toggler config: " + e.getMessage());
        }
    }

    public static void saveConfig() {
        try {
            Path configDirPath = Paths.get(CONFIG_DIR);
            Files.createDirectories(configDirPath);
            
            JsonObject json = new JsonObject();
            json.addProperty("combatEnabled", combatEnabled);
            
            Path configPath = getConfigPath();
            try (FileWriter writer = new FileWriter(configPath.toFile())) {
                GSON.toJson(json, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to save Better Combat Toggler config: " + e.getMessage());
        }
    }

    public static boolean isCombatEnabled() {
        return combatEnabled;
    }

    public static void setCombatEnabled(boolean enabled) {
        combatEnabled = enabled;
        saveConfig();
    }

    private static Path getConfigPath() {
        return Paths.get(CONFIG_DIR, CONFIG_FILE);
    }
}
