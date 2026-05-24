package pastelito.dev.bossBarNotify.managers;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import pastelito.dev.bossBarNotify.BossBarNotify;
import pastelito.dev.bossBarNotify.models.BossBarMessage;
import pastelito.dev.bossBarNotify.util.MessageParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigManager {
    
    private final BossBarNotify plugin;
    private boolean bossBarEnabled;
    private boolean worldWhitelistEnabled;
    private boolean randomOrder;
    private boolean noProgress;
    private List<String> worldWhitelist;
    private List<BossBarMessage> bossBarMessages;
    
    public ConfigManager(BossBarNotify plugin) {
        this.plugin = plugin;
        reloadConfig();
    }
    
    public void reloadConfig() {
        if (!plugin.getConfig().isConfigurationSection("BossBarMessages")) {
            plugin.getLogger().warning("BossBarMessages section not found in config.yml");
            this.bossBarEnabled = false;
            return;
        }
        
        this.bossBarEnabled = plugin.getConfig().getBoolean("BossBarMessages.Enabled", false);
        this.worldWhitelistEnabled = plugin.getConfig().getBoolean("BossBarMessages.World_Whitelist_Enabled", false);
        this.randomOrder = plugin.getConfig().getBoolean("BossBarMessages.Random_Order", false);
        this.noProgress = plugin.getConfig().getBoolean("BossBarMessages.No_Progress", false);
        this.worldWhitelist = plugin.getConfig().getStringList("BossBarMessages.World_Whitelist");
        
        loadMessages();
    }
    
    private void loadMessages() {
        bossBarMessages = new ArrayList<>();
        ConfigurationSection messagesSection = plugin.getConfig().getConfigurationSection("BossBarMessages.Messages");
        
        if (messagesSection == null) {
            plugin.getLogger().warning("No messages found in config.yml");
            return;
        }
        
        for (String key : messagesSection.getKeys(false)) {
            ConfigurationSection messageSection = messagesSection.getConfigurationSection(key);
            
            if (messageSection == null) continue;
            
            String message = MessageParser.parse(messageSection.getString("Message", ""));
            
            BarColor color;
            try {
                color = BarColor.valueOf(messageSection.getString("Color", "WHITE"));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid color for message " + key + ". Using WHITE.");
                color = BarColor.WHITE;
            }
            
            BarStyle style;
            String styleStr = messageSection.getString("Style", "SOLID");
            
            // Convert numeric styles to named styles if needed
            switch (styleStr) {
                case "0":
                case "6":
                    style = BarStyle.SOLID;
                    break;
                case "1":
                    style = BarStyle.SEGMENTED_6;
                    break;
                case "2":
                    style = BarStyle.SEGMENTED_10;
                    break;
                case "3":
                    style = BarStyle.SEGMENTED_12;
                    break;
                case "4":
                    style = BarStyle.SEGMENTED_20;
                    break;
                default:
                    try {
                        style = BarStyle.valueOf(styleStr);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid style for message " + key + ". Using SOLID.");
                        style = BarStyle.SOLID;
                    }
                    break;
            }
            
            int time = messageSection.getInt("Time", 10);
            
            bossBarMessages.add(new BossBarMessage(message, color, style, time));
        }
    }
    
    public boolean isBossBarEnabled() {
        return bossBarEnabled;
    }
    
    public boolean isWorldWhitelistEnabled() {
        return worldWhitelistEnabled;
    }
    
    public boolean isRandomOrder() {
        return randomOrder;
    }
    
    public boolean isNoProgress() {
        return noProgress;
    }
    
    public List<String> getWorldWhitelist() {
        return worldWhitelist;
    }
    
    public List<BossBarMessage> getBossBarMessages() {
        return bossBarMessages;
    }
    
    /**
     * Checks if a boss bar message with the given ID exists
     */
    public boolean hasMessage(String id) {
        return plugin.getConfig().isConfigurationSection("BossBarMessages.Messages." + id);
    }
    
    /**
     * Creates a new boss bar message
     */
    public void createMessage(String id, String message, String color, String style, int time) {
        FileConfiguration config = plugin.getConfig();
        config.set("BossBarMessages.Messages." + id + ".Message", message);
        config.set("BossBarMessages.Messages." + id + ".Color", color);
        config.set("BossBarMessages.Messages." + id + ".Style", style);
        config.set("BossBarMessages.Messages." + id + ".Time", time);
        plugin.saveConfig();
    }
    
    public void updateMessageText(String id, String message) {
        FileConfiguration config = plugin.getConfig();
        config.set("BossBarMessages.Messages." + id + ".Message", message);
        plugin.saveConfig();
    }
    
    public void updateMessageColor(String id, String color) {
        FileConfiguration config = plugin.getConfig();
        config.set("BossBarMessages.Messages." + id + ".Color", color);
        plugin.saveConfig();
    }
    
    public void updateMessageStyle(String id, String style) {
        FileConfiguration config = plugin.getConfig();
        config.set("BossBarMessages.Messages." + id + ".Style", style);
        plugin.saveConfig();
    }
    
    public void updateMessageTime(String id, int time) {
        FileConfiguration config = plugin.getConfig();
        config.set("BossBarMessages.Messages." + id + ".Time", time);
        plugin.saveConfig();
    }
    
    public void deleteMessage(String id) {
        FileConfiguration config = plugin.getConfig();
        config.set("BossBarMessages.Messages." + id, null);
        plugin.saveConfig();
    }
    
    public List<String> getMessageIds() {
        ConfigurationSection messagesSection = plugin.getConfig().getConfigurationSection("BossBarMessages.Messages");
        
        if (messagesSection == null) {
            return new ArrayList<>();
        }
        
        return messagesSection.getKeys(false).stream().collect(Collectors.toList());
    }
    
    public String getMessageText(String id) {
        return plugin.getConfig().getString("BossBarMessages.Messages." + id + ".Message");
    }
    
    public String getMessageColor(String id) {
        return plugin.getConfig().getString("BossBarMessages.Messages." + id + ".Color");
    }
    
    public String getMessageStyle(String id) {
        return plugin.getConfig().getString("BossBarMessages.Messages." + id + ".Style");
    }
    
    public int getMessageTime(String id) {
        return plugin.getConfig().getInt("BossBarMessages.Messages." + id + ".Time", 10);
    }
}
