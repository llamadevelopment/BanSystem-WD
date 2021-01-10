package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CheckmuteCommand extends Command {

    private final BanSystemWD instance;

    public CheckmuteCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.CheckmuteCommand.Name"), config.getString("Commands.CheckmuteCommand.Permission"), config.getStringList("Commands.CheckmuteCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                final String player = args[0];
                this.instance.getProvider().playerIsMuted(player, isMuted -> {
                    if (isMuted) {
                        this.instance.getProvider().getMute(player, mute -> {
                            sender.sendMessage(Language.get("checkmute.info", player));
                            sender.sendMessage(Language.get("checkmute.reason", mute.getReason()));
                            sender.sendMessage(Language.get("checkmute.id", mute.getMuteID()));
                            sender.sendMessage(Language.get("checkmute.muter", mute.getMuter()));
                            sender.sendMessage(Language.get("checkmute.date", mute.getDate()));
                            sender.sendMessage(Language.get("checkmute.time", this.instance.getProvider().getRemainingTime(mute.getTime())));
                        });
                    } else {
                        this.instance.getProvider().muteIdExists(player, false, exists -> {
                            if (exists) {
                                this.instance.getProvider().getMuteById(player, false, mute -> {
                                    sender.sendMessage(Language.get("checkmuteid.info", mute.getMuteID()));
                                    sender.sendMessage(Language.get("checkmuteid.player", mute.getPlayer()));
                                    sender.sendMessage(Language.get("checkmuteid.reason", mute.getReason()));
                                    sender.sendMessage(Language.get("checkmuteid.muter", mute.getMuter()));
                                    sender.sendMessage(Language.get("checkmuteid.date", mute.getDate()));
                                    sender.sendMessage(Language.get("checkmuteid.time", this.instance.getProvider().getRemainingTime(mute.getTime())));
                                });
                                return;
                            }
                            sender.sendMessage(Language.get("player.not.muted"));
                        });
                    }
                });
            } else sender.sendMessage(Language.get("usage.checkmutecommand", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
