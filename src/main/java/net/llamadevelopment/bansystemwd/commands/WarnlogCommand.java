package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class WarnlogCommand extends Command {

    private final BanSystemWD instance;

    public WarnlogCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.WarnlogCommand.Name"), config.getString("Commands.WarnlogCommand.Permission"), config.getStringList("Commands.WarnlogCommand.Aliases").toArray(new String[]{}));
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
                    sender.sendMessage(Language.get("warnlog.info", player, i));
                    warnlog.forEach(warn -> {
                        sender.sendMessage(Language.get("warnlog.placeholder"));
                        sender.sendMessage(Language.get("warnlog.reason", warn.getReason()));
                        sender.sendMessage(Language.get("warnlog.id", warn.getWarnID()));
                        sender.sendMessage(Language.get("warnlog.creator", warn.getCreator()));
                        sender.sendMessage(Language.get("warnlog.date", warn.getDate()));
                    });
                });
            } else sender.sendMessage(Language.get("usage.warnlogcommand", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
