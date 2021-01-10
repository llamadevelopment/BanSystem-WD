package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ClearbanlogCommand extends Command {

    private final BanSystemWD instance;

    public ClearbanlogCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.ClearbanlogCommand.Name"), config.getString("Commands.ClearbanlogCommand.Permission"), config.getStringList("Commands.ClearbanlogCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                final String player = args[0];
                this.instance.getProvider().getBanLog(player, banlog -> {
                    final int i = banlog.size();
                    if (i == 0) {
                        sender.sendMessage(Language.get("data.not.found"));
                        return;
                    }
                    this.instance.getProvider().clearBanlog(player, sender.getName());
                    sender.sendMessage(Language.get("banlog.cleared", i));
                });
            } else sender.sendMessage(Language.get("usage.clearbanlog", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
