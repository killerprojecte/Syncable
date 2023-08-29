package dev.rgbmc.syncable.commands;

import dev.rgbmc.syncable.SyncableBukkit;
import dev.rgbmc.syncable.client.migrators.Migrator;
import dev.rgbmc.syncable.migrators.HuskSyncMigrator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SyncableCommand implements CommandExecutor, TabCompleter {
    private static void showHelp(CommandSender sender) {
        List<String> list = SyncableBukkit.getMessageUtils().getConfiguration().getStringList("commands.help");
        list.forEach(string -> {
            sender.sendMessage(color(string
                    .replace("${author}", SyncableBukkit.instance.getDescription().getAuthors().get(0))
                    .replace("${version}", SyncableBukkit.instance.getDescription().getVersion())
                    .replace("${platform}", "Bukkit")
                    .replace("${migrators}", "husksync")));
        });
    }

    private static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            String arg1 = args[0];
            if (arg1.equalsIgnoreCase("reload")) {
                SyncableBukkit.getMessageUtils().reload();
                sender.sendMessage(color(SyncableBukkit.getMessageUtils().getConfiguration().getString("commands.reload")));
            } else if (arg1.equalsIgnoreCase("help")) {
                showHelp(sender);
            } else {
                showHelp(sender);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("migrate")) {
                String migrator = args[1];
                CompletableFuture.runAsync(() -> {
                    Migrator migrateHandler = null;
                    if (migrator.equalsIgnoreCase("husksync")) {
                        if (Bukkit.getPluginManager().getPlugin("HuskSync") != null) {
                            migrateHandler = new HuskSyncMigrator();
                        }
                    }
                    if (migrateHandler == null) {
                        sender.sendMessage(color(SyncableBukkit.getMessageUtils().getConfiguration().getString("commands.migrate.invalid")));
                        return;
                    }
                    sender.sendMessage(color(SyncableBukkit.getMessageUtils().getConfiguration().getString("commands.migrate.start")
                            .replace("${migrator}", migrateHandler.getClass().getSimpleName())));
                    migrateHandler.migrate();
                });
            }
        } else {
            showHelp(sender);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if (args.length == 1) {
            commands.add("reload");
            commands.add("help");
            commands.add("migrate");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("migrate")) {
                commands.add("husksync");
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
