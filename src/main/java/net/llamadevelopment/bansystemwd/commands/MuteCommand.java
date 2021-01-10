package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.data.MuteReason;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MuteCommand extends Command {

    private final BanSystemWD instance;

    public MuteCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.MuteCommand.Name"), config.getString("Commands.MuteCommand.Permission"), config.getStringList("Commands.MuteCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 2) {
                final String player = args[0];
                final String reason = args[1];
                this.instance.getProvider().playerIsMuted(player, isMuted -> {
                    if (isMuted) {
                        sender.sendMessage(Language.get("player.is.muted"));
                        return;
                    }
                    if (this.instance.getProvider().muteReasons.get(reason) == null) {
                        sender.sendMessage(Language.get("reason.not.found"));
                        return;
                    }
                    final MuteReason muteReason = this.instance.getProvider().muteReasons.get(reason);
                    this.instance.getProvider().mutePlayer(player, muteReason.getReason(), sender.getName(), muteReason.getSeconds());
                    sender.sendMessage(Language.get("player.muted", player));
                });
            } else {
                this.instance.getProvider().muteReasons.values().forEach(reason -> sender.sendMessage(Language.get("reason.format", reason.getId(), reason.getReason())));
                sender.sendMessage(Language.get("usage.mutecommand", this.getName()));
            }
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
