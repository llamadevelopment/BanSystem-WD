package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class EditmuteCommand extends Command {

    private final BanSystemWD instance;

    public EditmuteCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.EditmuteCommand.Name"), config.getString("Commands.EditmuteCommand.Permission"), config.getStringList("Commands.EditmuteCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length >= 3) {
                final String player = args[0];
                if (args[1].equalsIgnoreCase("reason")) {
                    this.instance.getProvider().playerIsMuted(player, isMuted -> {
                        if (isMuted) {
                            String reason = "";
                            for (int i = 2; i < args.length; ++i) reason = reason + args[i] + " ";
                            this.instance.getProvider().setMuteReason(player, reason, sender.getName());
                            sender.sendMessage(Language.get("mute.reason.set"));
                        } else sender.sendMessage(Language.get("player.not.muted"));
                    });
                } else if (args.length == 4 && args[1].equalsIgnoreCase("time")) {
                    this.instance.getProvider().playerIsMuted(player, isMuted -> {
                        if (isMuted) {
                            try {
                                final String type = args[2];
                                final int time = Integer.parseInt(args[3]);
                                int seconds;
                                if (type.equalsIgnoreCase("days")) seconds = time * 86400;
                                else if (type.equalsIgnoreCase("hours")) seconds = time * 3600;
                                else {
                                    sender.sendMessage(Language.get("usage.editmutecommand", this.getName()));
                                    return;
                                }
                                final long end = System.currentTimeMillis() + seconds * 1000L;
                                this.instance.getProvider().setMuteTime(player, end, sender.getName());
                                sender.sendMessage(Language.get("mute.time.set"));
                            } catch (NumberFormatException exception) {
                                sender.sendMessage(Language.get("invalid.number"));
                            }
                        } else sender.sendMessage(Language.get("player.not.muted"));
                    });
                } else sender.sendMessage(Language.get("usage.editmutecommand", this.getName()));
            } else sender.sendMessage(Language.get("usage.editmutecommand", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
