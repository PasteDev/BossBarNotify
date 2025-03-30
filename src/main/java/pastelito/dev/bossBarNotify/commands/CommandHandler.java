package pastelito.dev.bossBarNotify.commands;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import pastelito.dev.bossBarNotify.BossBarNotify;
import pastelito.dev.bossBarNotify.managers.ConfigManager;
import pastelito.dev.bossBarNotify.managers.LocaleManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final BossBarNotify plugin;
    private final LocaleManager localeManager;
    private final ConfigManager configManager;

    public CommandHandler(BossBarNotify plugin) {
        this.plugin = plugin;
        this.localeManager = plugin.getLocaleManager();
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bossbarnotify.command")) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("general.no-permission"));
            return true;
        }

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                showHelp(sender);
                break;

            case "reload":
                handleReload(sender);
                break;

            case "create":
                handleCreate(sender, args);
                break;

            case "edit":
                handleEdit(sender, args);
                break;

            case "delete":
                handleDelete(sender, args);
                break;

            case "list":
                handleList(sender);
                break;

            default:
                sender.sendMessage(localeManager.getMessage("general.prefix") +
                        localeManager.getMessage("general.invalid-command"));
                break;
        }

        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(localeManager.getMessage("commands.help.title"));

        if (sender.hasPermission("bossbarnotify.reload")) {
            sender.sendMessage(localeManager.getMessage("commands.help.reload"));
        }

        if (sender.hasPermission("bossbarnotify.create")) {
            sender.sendMessage(localeManager.getMessage("commands.help.create"));
        }

        if (sender.hasPermission("bossbarnotify.edit")) {
            sender.sendMessage(localeManager.getMessage("commands.help.edit"));
        }

        if (sender.hasPermission("bossbarnotify.delete")) {
            sender.sendMessage(localeManager.getMessage("commands.help.delete"));
        }

        if (sender.hasPermission("bossbarnotify.list")) {
            sender.sendMessage(localeManager.getMessage("commands.help.list"));
        }
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("bossbarnotify.reload")) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("general.no-permission"));
            return;
        }

        plugin.reload();
        sender.sendMessage(localeManager.getMessage("general.prefix") +
                localeManager.getMessage("general.reloaded"));
    }

    private void handleCreate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("bossbarnotify.create")) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("general.no-permission"));
            return;
        }

        if (args.length < 6) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("commands.create.usage"));
            return;
        }

        String id = args[1];
        String color = args[2].toUpperCase();
        String style = args[3].toUpperCase();
        int time;

        try {
            time = Integer.parseInt(args[4]);
            if (time <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("commands.edit.invalid-time"));
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 5, args.length));

        try {
            BarColor.valueOf(color);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("commands.edit.invalid-color"));
            return;
        }

        try {
            BarStyle.valueOf(style);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("commands.edit.invalid-style"));
            return;
        }

        if (configManager.hasMessage(id)) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("commands.create.exists", "id", id));
            return;
        }

        configManager.createMessage(id, message, color, style, time);
        plugin.reload();

        sender.sendMessage(localeManager.getMessage("general.prefix") +
                localeManager.getMessage("commands.create.success", "id", id));
    }

    private void handleEdit(CommandSender sender, String[] args) {
        if (!sender.hasPermission("bossbarnotify.edit")) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("general.no-permission"));
            return;
        }

        if (args.length < 4) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("commands.edit.usage"));
            return;
        }

        String id = args[1];
        String property = args[2].toLowerCase();

        if (!configManager.hasMessage(id)) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("commands.edit.not-found", "id", id));
            return;
        }

        StringBuilder valueBuilder = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            valueBuilder.append(args[i]).append(" ");
        }
        String value = valueBuilder.toString().trim();

        boolean success = false;

        switch (property) {
            case "message":
                configManager.updateMessageText(id, value);
                success = true;
                break;

            case "color":
                try {
                    BarColor.valueOf(value.toUpperCase());
                    configManager.updateMessageColor(id, value.toUpperCase());
                    success = true;
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(localeManager.getMessage("general.prefix") +
                            localeManager.getMessage("commands.edit.invalid-color"));
                }
                break;

            case "style":
                try {
                    if (value.matches("\\d+")) {
                        int styleNum = Integer.parseInt(value);
                        switch (styleNum) {
                            case 0:
                            case 6:
                                configManager.updateMessageStyle(id, BarStyle.SOLID.name());
                                success = true;
                                break;
                            case 1:
                                configManager.updateMessageStyle(id, BarStyle.SEGMENTED_6.name());
                                success = true;
                                break;
                            case 2:
                                configManager.updateMessageStyle(id, BarStyle.SEGMENTED_10.name());
                                success = true;
                                break;
                            case 3:
                                configManager.updateMessageStyle(id, BarStyle.SEGMENTED_12.name());
                                success = true;
                                break;
                            case 4:
                                configManager.updateMessageStyle(id, BarStyle.SEGMENTED_20.name());
                                success = true;
                                break;
                            default:
                                sender.sendMessage(localeManager.getMessage("general.prefix") +
                                        localeManager.getMessage("commands.edit.invalid-style"));
                                break;
                        }
                    } else {
                        BarStyle.valueOf(value.toUpperCase());
                        configManager.updateMessageStyle(id, value.toUpperCase());
                        success = true;
                    }
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(localeManager.getMessage("general.prefix") +
                            localeManager.getMessage("commands.edit.invalid-style"));
                }
                break;

            case "time":
                try {
                    int time = Integer.parseInt(value);
                    if (time <= 0) {
                        sender.sendMessage(localeManager.getMessage("general.prefix") +
                                localeManager.getMessage("commands.edit.invalid-time"));
                        return;
                    }
                    configManager.updateMessageTime(id, time);
                    success = true;
                } catch (NumberFormatException e) {
                    sender.sendMessage(localeManager.getMessage("general.prefix") +
                            localeManager.getMessage("commands.edit.invalid-time"));
                }
                break;

            default:
                sender.sendMessage(localeManager.getMessage("general.prefix") +
                        localeManager.getMessage("commands.edit.invalid-property"));
                break;
        }

        if (success) {
            plugin.reload();
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("commands.edit.success",
                            "property", property,
                            "id", id));
        }
    }

    private void handleDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("bossbarnotify.delete")) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("general.no-permission"));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("commands.delete.usage"));
            return;
        }

        String id = args[1];

        if (!configManager.hasMessage(id)) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("commands.delete.not-found", "id", id));
            return;
        }

        configManager.deleteMessage(id);
        plugin.reload();

        sender.sendMessage(localeManager.getMessage("general.prefix") +
                localeManager.getMessage("commands.delete.success", "id", id));
    }

    private void handleList(CommandSender sender) {
        if (!sender.hasPermission("bossbarnotify.list")) {
            sender.sendMessage(localeManager.getMessage("general.prefix") +
                    localeManager.getMessage("general.no-permission"));
            return;
        }

        List<String> messageIds = configManager.getMessageIds();

        sender.sendMessage(localeManager.getMessage("commands.list.title"));

        if (messageIds.isEmpty()) {
            sender.sendMessage(localeManager.getMessage("commands.list.none"));
            return;
        }

        for (String id : messageIds) {
            String message = configManager.getMessageText(id);
            String color = configManager.getMessageColor(id);
            int time = configManager.getMessageTime(id);

            sender.sendMessage(localeManager.getMessage("commands.list.entry",
                    "id", id,
                    "message", message,
                    "color", color,
                    "time", String.valueOf(time)));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();

            if (sender.hasPermission("bossbarnotify.reload"))
                completions.add("reload");
            if (sender.hasPermission("bossbarnotify.create"))
                completions.add("create");
            if (sender.hasPermission("bossbarnotify.edit"))
                completions.add("edit");
            if (sender.hasPermission("bossbarnotify.delete"))
                completions.add("delete");
            if (sender.hasPermission("bossbarnotify.list"))
                completions.add("list");
            completions.add("help");

            return filterCompletions(completions, args[0]);
        }
        else if (args[0].equalsIgnoreCase("create") && sender.hasPermission("bossbarnotify.create")) {
            switch (args.length) {
                case 2:
                    return Collections.singletonList("<id>");
                case 3:
                    return Arrays.stream(BarColor.values())
                            .map(Enum::name)
                            .collect(Collectors.toList());
                case 4:
                    return Arrays.stream(BarStyle.values())
                            .map(Enum::name)
                            .collect(Collectors.toList());
                case 5:
                    return Collections.singletonList("<time>");
                case 6:
                    return Collections.singletonList("<message>");
                default:
                    return Collections.emptyList();
            }
        }
        else if (args.length == 2) {
            if ((args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("delete")) &&
                    sender.hasPermission("bossbarnotify." + args[0].toLowerCase())) {
                return filterCompletions(configManager.getMessageIds(), args[1]);
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("edit") &&
                sender.hasPermission("bossbarnotify.edit")) {
            return filterCompletions(Arrays.asList("message", "color", "style", "time"), args[2]);
        } else if (args.length == 4 && args[0].equalsIgnoreCase("edit") &&
                args[2].equalsIgnoreCase("color") &&
                sender.hasPermission("bossbarnotify.edit")) {
            return filterCompletions(
                    Arrays.stream(BarColor.values())
                            .map(Enum::name)
                            .collect(Collectors.toList()),
                    args[3]);
        } else if (args.length == 4 && args[0].equalsIgnoreCase("edit") &&
                args[2].equalsIgnoreCase("style") &&
                sender.hasPermission("bossbarnotify.edit")) {
            return filterCompletions(
                    Arrays.stream(BarStyle.values())
                            .map(Enum::name)
                            .collect(Collectors.toList()),
                    args[3]);
        }

        return Collections.emptyList();
    }

    private List<String> filterCompletions(List<String> completions, String start) {
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(start.toLowerCase()))
                .collect(Collectors.toList());
    }
}
