package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class TempbanCommand extends Command {

    private final BanSystemWD instance;

    public TempbanCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.TempbanCommand.Name"), config.getString("Commands.TempbanCommand.Permission"), config.getStringList("Commands.TempbanCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length >= 4) {
                final String player = args[0];
                if (args[1].equalsIgnoreCase("days") || args[1].equalsIgnoreCase("hours")) {
                    final String timeString = args[1];
                    try {
                        final int time = Integer.parseInt(args[2]);
                        int seconds = 0;
                        String reason = "";
                        for (int i = 3; i < args.length; ++i) reason = reason + args[i] + " ";
                        if (timeString.equalsIgnoreCase("days")) seconds = time * 86400;
                        if (timeString.equalsIgnoreCase("hours")) seconds = time * 3600;
                        final String finalReason1 = reason;
                        final int finalSeconds1 = seconds;
                        this.instance.getProvider().playerIsBanned(player, isBanned -> {
                            if (isBanned) {
                                sender.sendMessage(Language.get("player.is.banned"));
                                return;
                            }
                            this.instance.getProvider().banPlayer(player, finalReason1, sender.getName(), finalSeconds1);
                            sender.sendMessage(Language.get("player.banned", player));
                        });
                    } catch (NumberFormatException exception) {
                        sender.sendMessage(Language.get("invalid.number"));
                    }
                } else sender.sendMessage(Language.get("usage.tempbancommand", this.getName()));
            } else sender.sendMessage(Language.get("usage.tempbancommand", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
