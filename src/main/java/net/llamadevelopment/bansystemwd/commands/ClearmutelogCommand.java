package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ClearmutelogCommand extends Command {

    private final BanSystemWD instance;

    public ClearmutelogCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.ClearmutelogCommand.Name"), config.getString("Commands.ClearmutelogCommand.Permission"), config.getStringList("Commands.ClearmutelogCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                final String player = args[0];
                this.instance.getProvider().getMuteLog(player, mutelog -> {
                    final int i = mutelog.size();
                    if (i == 0) {
                        sender.sendMessage(Language.get("data.not.found"));
                        return;
                    }
                    this.instance.getProvider().clearMutelog(player, sender.getName());
                    sender.sendMessage(Language.get("mutelog.cleared", i));
                });
            } else sender.sendMessage(Language.get("usage.clearmutelog", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
