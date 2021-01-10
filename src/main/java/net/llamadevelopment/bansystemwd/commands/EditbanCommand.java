package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class EditbanCommand extends Command {

    private final BanSystemWD instance;

    public EditbanCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.EditbanCommand.Name"), config.getString("Commands.EditbanCommand.Permission"), config.getStringList("Commands.EditbanCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length >= 3) {
                final String player = args[0];
                if (args[1].equalsIgnoreCase("reason")) {
                    this.instance.getProvider().playerIsBanned(player, isBanned -> {
                        if (isBanned) {
                            String reason = "";
                            for (int i = 2; i < args.length; ++i) reason = reason + args[i] + " ";
                            this.instance.getProvider().setBanReason(player, reason, sender.getName());
                            sender.sendMessage(Language.get("ban.reason.set"));
                        } else sender.sendMessage(Language.get("player.not.banned"));
                    });
                } else if (args.length == 4 && args[1].equalsIgnoreCase("time")) {
                    this.instance.getProvider().playerIsBanned(player, isBanned -> {
                        if (isBanned) {
                            try {
                                final String type = args[2];
                                final int time = Integer.parseInt(args[3]);
                                int seconds;
                                if (type.equalsIgnoreCase("days")) seconds = time * 86400;
                                else if (type.equalsIgnoreCase("hours")) seconds = time * 3600;
                                else {
                                    sender.sendMessage(Language.get("usage.editbancommand", this.getName()));
                                    return;
                                }
                                final long end = System.currentTimeMillis() + seconds * 1000L;
                                this.instance.getProvider().setBanTime(player, end, sender.getName());
                                sender.sendMessage(Language.get("ban.time.set"));
                            } catch (NumberFormatException exception) {
                                sender.sendMessage(Language.get("invalid.number"));
                            }
                        } else sender.sendMessage(Language.get("player.not.banned"));
                    });
                } else sender.sendMessage(Language.get("usage.editbancommand", this.getName()));
            } else sender.sendMessage(Language.get("usage.editbancommand", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
