package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class UnmuteCommand extends Command {

    private final BanSystemWD instance;

    public UnmuteCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.UnmuteCommand.Name"), config.getString("Commands.UnmuteCommand.Permission"), config.getStringList("Commands.UnmuteCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                final String player = args[0];
                this.instance.getProvider().playerIsMuted(player, isMuted -> {
                    if (isMuted) {
                        this.instance.getProvider().unmutePlayer(player, sender.getName());
                        sender.sendMessage(Language.get("player.unmuted", player));
                    } else sender.sendMessage(Language.get("player.not.muted"));
                });
            } else sender.sendMessage(Language.get("usage.unmutecommand", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
