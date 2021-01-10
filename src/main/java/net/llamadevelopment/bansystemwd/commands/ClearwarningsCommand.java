package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ClearwarningsCommand extends Command {

    private final BanSystemWD instance;

    public ClearwarningsCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.ClearwarningsCommand.Name"), config.getString("Commands.ClearwarningsCommand.Permission"), config.getStringList("Commands.ClearwarningsCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                final String player = args[0];
                this.instance.getProvider().getWarnLog(player, warnlog -> {
                    final int i = warnlog.size();
                    if (i == 0) {
                        sender.sendMessage(Language.get("data.not.found"));
                        return;
                    }
                    this.instance.getProvider().clearWarns(player, sender.getName());
                    sender.sendMessage(Language.get("warnlog.cleared", i));
                });
            } else sender.sendMessage(Language.get("usage.clearwarnings", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
