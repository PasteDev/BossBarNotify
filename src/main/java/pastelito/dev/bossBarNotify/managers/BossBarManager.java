package pastelito.dev.bossBarNotify.managers;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import pastelito.dev.bossBarNotify.BossBarNotify;
import pastelito.dev.bossBarNotify.models.BossBarMessage;
import pastelito.dev.bossBarNotify.util.MessageParser;

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
        activeBossBar = BossBar.bossBar(
                MessageParser.parseComponent(message.getMessage()),
                1.0f,
                toAdventureColor(message.getColor()),
                toAdventureOverlay(message.getStyle())
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (shouldShowBossBar(player)) {
                showBossBar(player);
            }
        }
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
                activeBossBar.progress((float) progress);
            }
        }, 1L, 1L);
    }

    private void removeBossBar() {
        if (progressTask != null) {
            progressTask.cancel();
            progressTask = null;
        }

        if (activeBossBar != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                hideBossBar(player);
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
        if (activeBossBar != null && shouldShowBossBar(player)) {
            showBossBar(player);
        }
    }

    public void removePlayerFromBossBar(Player player) {
        if (activeBossBar != null) {
            hideBossBar(player);
        }
    }

    private boolean shouldShowBossBar(Player player) {
        return !configManager.isWorldWhitelistEnabled()
                || configManager.getWorldWhitelist().contains(player.getWorld().getName());
    }

    private void showBossBar(Player player) {
        ((Audience) player).showBossBar(activeBossBar);
    }

    private void hideBossBar(Player player) {
        ((Audience) player).hideBossBar(activeBossBar);
    }

    private static BossBar.Color toAdventureColor(BarColor color) {
        return switch (color) {
            case PINK -> BossBar.Color.PINK;
            case BLUE -> BossBar.Color.BLUE;
            case RED -> BossBar.Color.RED;
            case GREEN -> BossBar.Color.GREEN;
            case YELLOW -> BossBar.Color.YELLOW;
            case PURPLE -> BossBar.Color.PURPLE;
            case WHITE -> BossBar.Color.WHITE;
        };
    }

    private static BossBar.Overlay toAdventureOverlay(BarStyle style) {
        return switch (style) {
            case SOLID -> BossBar.Overlay.PROGRESS;
            case SEGMENTED_6 -> BossBar.Overlay.NOTCHED_6;
            case SEGMENTED_10 -> BossBar.Overlay.NOTCHED_10;
            case SEGMENTED_12 -> BossBar.Overlay.NOTCHED_12;
            case SEGMENTED_20 -> BossBar.Overlay.NOTCHED_20;
        };
    }
}
