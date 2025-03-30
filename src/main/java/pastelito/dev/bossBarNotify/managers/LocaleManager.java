package pastelito.dev.bossBarNotify.managers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import pastelito.dev.bossBarNotify.BossBarNotify;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LocaleManager {
    
    private final BossBarNotify plugin;
    private final Map<String, String> messages = new HashMap<>();
    private String language;
    private final File langsFolder;
    
    public LocaleManager(BossBarNotify plugin) {
        this.plugin = plugin;
        this.language = plugin.getConfig().getString("Language", "en");
        
        this.langsFolder = new File(plugin.getDataFolder(), "langs");
        if (!langsFolder.exists()) {
            langsFolder.mkdirs();
        }
        
        migrateLegacyFiles();
        loadMessages();
    }
    
    /**
     * Migrates any language files from the root plugin folder to the new langs folder
     */
    private void migrateLegacyFiles() {
        String[] supportedLanguages = {"en", "es"};
        
        for (String lang : supportedLanguages) {
            File legacyFile = new File(plugin.getDataFolder(), "messages_" + lang + ".yml");
            if (legacyFile.exists()) {
                File newFile = new File(langsFolder, lang + ".yml");
                if (!newFile.exists()) {
                    if (legacyFile.renameTo(newFile)) {
                        plugin.getLogger().info("Migrated language file: " + lang + " to langs folder");
                    } else {
                        plugin.getLogger().warning("Failed to migrate language file: " + lang);
                    }
                }
            }
        }
    }
    
    public void loadMessages() {
        messages.clear();
        
        // Load default messages first (English)
        loadLanguageFile("en");
        
        // If language isn't English, overlay with translations
        if (!language.equals("en")) {
            loadLanguageFile(language);
        }
    }
    
    private void loadLanguageFile(String lang) {
        File langFile = new File(langsFolder, lang + ".yml");
        
        if (langFile.exists()) {
            YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
            for (String key : langConfig.getKeys(true)) {
                if (langConfig.isString(key)) {
                    messages.put(key, langConfig.getString(key));
                }
            }
        } else {
            if (plugin.getResource("langs/" + lang + ".yml") != null) {
                YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(plugin.getResource("langs/" + lang + ".yml"), StandardCharsets.UTF_8));
                
                saveResourceToLangsFolder(lang + ".yml");
                
                for (String key : langConfig.getKeys(true)) {
                    if (langConfig.isString(key)) {
                        messages.put(key, langConfig.getString(key));
                    }
                }
            } else if (plugin.getResource("messages_" + lang + ".yml") != null) {
                YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(plugin.getResource("messages_" + lang + ".yml"), StandardCharsets.UTF_8));
                
                try {
                    langConfig.save(new File(langsFolder, lang + ".yml"));
                    plugin.getLogger().info("Saved language file: " + lang + " to langs folder");
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to save language file: " + lang);
                }
                
                for (String key : langConfig.getKeys(true)) {
                    if (langConfig.isString(key)) {
                        messages.put(key, langConfig.getString(key));
                    }
                }
            } else {
                plugin.getLogger().warning("Language file for " + lang + " not found.");
            }
        }
    }
    
    private void saveResourceToLangsFolder(String resourcePath) {
        File outFile = new File(langsFolder, resourcePath);
        
        try {
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            
            if (!outFile.exists()) {
                if (plugin.getResource("langs/" + resourcePath) != null) {
                    plugin.saveResource("langs/" + resourcePath, false);
                    File tempFile = new File(plugin.getDataFolder(), "langs/" + resourcePath);
                    if (tempFile.exists() && !tempFile.equals(outFile)) {
                        if (tempFile.renameTo(outFile)) {
                            File parentDir = tempFile.getParentFile();
                            if (parentDir.exists() && parentDir.isDirectory() && parentDir.list().length == 0) {
                                parentDir.delete();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save resource: " + resourcePath + " to langs folder");
            e.printStackTrace();
        }
    }
    
    public String getMessage(String key) {
        return getMessage(key, new String[0]);
    }
    
    public String getMessage(String key, String... replacements) {
        String message = messages.getOrDefault(key, key);
        
        for (int i = 0; i < replacements.length - 1; i += 2) {
            message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
        }
        
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public void setLanguage(String language) {
        this.language = language;
        loadMessages();
    }
}
