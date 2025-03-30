package pastelito.dev.bossBarNotify.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import pastelito.dev.bossBarNotify.BossBarNotify;
import pastelito.dev.bossBarNotify.managers.BossBarManager;
import pastelito.dev.bossBarNotify.managers.ConfigManager;

public class PlayerListener implements Listener {
    
    private final ConfigManager configManager;
    private final BossBarManager bossBarManager;
    
    public PlayerListener(BossBarNotify plugin) {
        this.configManager = plugin.getConfigManager();
        this.bossBarManager = plugin.getBossBarManager();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!configManager.isBossBarEnabled()) return;
        
        Player player = event.getPlayer();
        
        if (bossBarManager.hasActiveBossBar()) {
            if (configManager.isWorldWhitelistEnabled() &&
                    !configManager.getWorldWhitelist().contains(player.getWorld().getName())) {
                return;
            }
            
            bossBarManager.addPlayerToBossBar(player);
        }
    }
    
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        if (!configManager.isBossBarEnabled() || !configManager.isWorldWhitelistEnabled()) return;
        
        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();
        
        if (configManager.getWorldWhitelist().contains(worldName)) {
            if (bossBarManager.hasActiveBossBar()) {
                bossBarManager.addPlayerToBossBar(player);
            }
        } else {
            bossBarManager.removePlayerFromBossBar(player);
        }
    }
}
