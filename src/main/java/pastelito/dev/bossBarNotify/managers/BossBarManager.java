package pastelito.dev.bossBarNotify.managers;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import pastelito.dev.bossBarNotify.BossBarNotify;
import pastelito.dev.bossBarNotify.models.BossBarMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BossBarManager {
    
    private final BossBarNotify plugin;
    private final ConfigManager configManager;
    private BossBar activeBossBar;
    private int currentMessageIndex;
    private List<BossBarMessage> messagesQueue;
    private BukkitTask progressTask;
    
    public BossBarManager(BossBarNotify plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.currentMessageIndex = 0;
    }
    
    public void startBossBarSystem() {
        stopBossBarSystem();
        
        List<BossBarMessage> configMessages = configManager.getBossBarMessages();
        
        if (configMessages.isEmpty()) {
            plugin.getLogger().warning("No BossBar messages found in config. BossBar system not started.");
            return;
        }
        
        messagesQueue = new ArrayList<>(configMessages);
        
        if (configManager.isRandomOrder()) {
            Collections.shuffle(messagesQueue);
        }
        
        displayNextBossBar();
    }
    
    private void displayNextBossBar() {
        if (messagesQueue == null || messagesQueue.isEmpty()) return;
        
        BossBarMessage message = messagesQueue.get(currentMessageIndex);
        
        removeBossBar();
        createBossBar(message);
        
        if (!configManager.isNoProgress()) {
            scheduleBossBarProgress(message.getTime());
        }
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            removeBossBar();
            
            currentMessageIndex = (currentMessageIndex + 1) % messagesQueue.size();
            
            if (currentMessageIndex == 0 && configManager.isRandomOrder()) {
                Collections.shuffle(messagesQueue);
            }
            
            displayNextBossBar();
        }, message.getTime() * 20L);
    }
    
    private void createBossBar(BossBarMessage message) {
        activeBossBar = Bukkit.createBossBar(
                message.getMessage(),
                message.getColor(),
                message.getStyle()
        );
        
        activeBossBar.setProgress(1.0);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (configManager.isWorldWhitelistEnabled() &&
                    !configManager.getWorldWhitelist().contains(player.getWorld().getName())) {
                continue;
            }
            activeBossBar.addPlayer(player);
        }
        
        activeBossBar.setVisible(true);
    }
    
    private void scheduleBossBarProgress(int durationSeconds) {
        if (progressTask != null) {
            progressTask.cancel();
        }
        
        final int totalTicks = durationSeconds * 20;
        final double progressDecrement = 1.0 / totalTicks;
        
        progressTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            private int ticksElapsed = 0;
            
            @Override
            public void run() {
                if (activeBossBar == null || ++ticksElapsed > totalTicks) {
                    if (progressTask != null) {
                        progressTask.cancel();
                        progressTask = null;
                    }
                    return;
                }
                
                double progress = 1.0 - (progressDecrement * ticksElapsed);
                if (progress < 0) progress = 0;
                activeBossBar.setProgress(progress);
            }
        }, 1L, 1L);
    }
    
    private void removeBossBar() {
        if (progressTask != null) {
            progressTask.cancel();
            progressTask = null;
        }
        
        if (activeBossBar != null) {
            activeBossBar.setVisible(false);
            for (Player player : Bukkit.getOnlinePlayers()) {
                activeBossBar.removePlayer(player);
            }
            activeBossBar = null;
        }
    }
    
    public void stopBossBarSystem() {
        Bukkit.getScheduler().cancelTasks(plugin);
        removeBossBar();
        currentMessageIndex = 0;
        progressTask = null;
    }
    
    public boolean hasActiveBossBar() {
        return activeBossBar != null;
    }
    
    public void addPlayerToBossBar(Player player) {
        if (activeBossBar != null) {
            activeBossBar.addPlayer(player);
        }
    }
    
    public void removePlayerFromBossBar(Player player) {
        if (activeBossBar != null) {
            activeBossBar.removePlayer(player);
        }
    }
}
