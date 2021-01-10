package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class UnbanCommand extends Command {

    private final BanSystemWD instance;

    public UnbanCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.UnbanCommand.Name"), config.getString("Commands.UnbanCommand.Permission"), config.getStringList("Commands.UnbanCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                final String player = args[0];
                this.instance.getProvider().playerIsBanned(player, isBanned -> {
                    if (isBanned) {
                        this.instance.getProvider().unbanPlayer(player, sender.getName());
                        sender.sendMessage(Language.get("player.unbanned", player));
                    } else sender.sendMessage(Language.get("player.not.banned"));
                });
            } else sender.sendMessage(Language.get("usage.unbancommand", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
