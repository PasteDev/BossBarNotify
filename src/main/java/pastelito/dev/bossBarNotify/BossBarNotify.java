package pastelito.dev.bossBarNotify;

import org.bukkit.plugin.java.JavaPlugin;
import pastelito.dev.bossBarNotify.commands.CommandHandler;
import pastelito.dev.bossBarNotify.listeners.PlayerListener;
import pastelito.dev.bossBarNotify.managers.BossBarManager;
import pastelito.dev.bossBarNotify.managers.ConfigManager;
import pastelito.dev.bossBarNotify.managers.LocaleManager;

import java.io.File;

public final class BossBarNotify extends JavaPlugin {
    
    private ConfigManager configManager;
    private BossBarManager bossBarManager;
    private LocaleManager localeManager;
    
    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        File langsFolder = new File(getDataFolder(), "langs");
        if (!langsFolder.exists()) {
            langsFolder.mkdirs();
        }
        
        saveDefaultConfig();
        
        this.configManager = new ConfigManager(this);
        this.localeManager = new LocaleManager(this);
        this.bossBarManager = new BossBarManager(this);
        
        CommandHandler commandHandler = new CommandHandler(this);
        getCommand("bbnotify").setExecutor(commandHandler);
        getCommand("bbnotify").setTabCompleter(commandHandler);
        
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        if (configManager.isBossBarEnabled()) {
            bossBarManager.startBossBarSystem();
        } else {
            getLogger().info("BossBar messages are disabled in the config.");
        }
        
        getLogger().info("BossBarNotify has been enabled!");
    }

    @Override
    public void onDisable() {
        if (bossBarManager != null) {
            bossBarManager.stopBossBarSystem();
        }
        getLogger().info("BossBarNotify has been disabled!");
    }
    
    /**
     * Reloads the plugin configuration and restarts the boss bar system
     */
    public void reload() {
        bossBarManager.stopBossBarSystem();
        
        reloadConfig();
        configManager.reloadConfig();
        localeManager.loadMessages();
        
        if (configManager.isBossBarEnabled()) {
            bossBarManager.startBossBarSystem();
            getLogger().info("BossBarNotify configuration reloaded!");
        } else {
            getLogger().info("BossBar messages are disabled in the config.");
        }
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }
    
    public LocaleManager getLocaleManager() {
        return localeManager;
    }
}
