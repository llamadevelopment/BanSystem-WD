package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CheckbanCommand extends Command {

    private final BanSystemWD instance;

    public CheckbanCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.CheckbanCommand.Name"), config.getString("Commands.CheckbanCommand.Permission"), config.getStringList("Commands.CheckbanCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                final String player = args[0];
                this.instance.getProvider().playerIsBanned(player, isBanned -> {
                    if (isBanned) {
                        this.instance.getProvider().getBan(player, ban -> {
                            sender.sendMessage(Language.get("checkban.info", player));
                            sender.sendMessage(Language.get("checkban.reason", ban.getReason()));
                            sender.sendMessage(Language.get("checkban.id", ban.getBanID()));
                            sender.sendMessage(Language.get("checkban.banner", ban.getBanner()));
                            sender.sendMessage(Language.get("checkban.date", ban.getDate()));
                            sender.sendMessage(Language.get("checkban.time", this.instance.getProvider().getRemainingTime(ban.getTime())));
                        });
                    } else {
                        this.instance.getProvider().banIdExists(player, false, exists -> {
                            if (exists) {
                                this.instance.getProvider().getBanById(player, false, ban -> {
                                    sender.sendMessage(Language.get("checkbanid.info", ban.getBanID()));
                                    sender.sendMessage(Language.get("checkbanid.player", ban.getPlayer()));
                                    sender.sendMessage(Language.get("checkbanid.reason", ban.getReason()));
                                    sender.sendMessage(Language.get("checkbanid.banner", ban.getBanner()));
                                    sender.sendMessage(Language.get("checkbanid.date", ban.getDate()));
                                    sender.sendMessage(Language.get("checkbanid.time", this.instance.getProvider().getRemainingTime(ban.getTime())));
                                });
                            } else sender.sendMessage(Language.get("player.not.banned"));
                        });
                    }
                });
            } else sender.sendMessage(Language.get("usage.checkbancommand", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
