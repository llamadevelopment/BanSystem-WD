package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.data.BanReason;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BanCommand extends Command {

    private final BanSystemWD instance;

    public BanCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.BanCommand.Name"), config.getString("Commands.BanCommand.Permission"), config.getStringList("Commands.BanCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 2) {
                final String player = args[0];
                final String reason = args[1];
                this.instance.getProvider().playerIsBanned(player, isBanned -> {
                    if (isBanned) {
                        sender.sendMessage(Language.get("player.is.banned"));
                        return;
                    }
                    if (this.instance.getProvider().banReasons.get(reason) == null) {
                        sender.sendMessage(Language.get("reason.not.found"));
                        return;
                    }
                    final BanReason banReason = this.instance.getProvider().banReasons.get(reason);
                    this.instance.getProvider().banPlayer(player, banReason.getReason(), sender.getName(), banReason.getSeconds());
                    sender.sendMessage(Language.get("player.banned", player));
                });
            } else {
                this.instance.getProvider().banReasons.values().forEach(reason -> sender.sendMessage(Language.get("reason.format", reason.getId(), reason.getReason())));
                sender.sendMessage(Language.get("usage.bancommand", this.getName()));
            }
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
