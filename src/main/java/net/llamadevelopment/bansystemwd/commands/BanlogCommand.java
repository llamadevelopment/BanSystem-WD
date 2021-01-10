package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BanlogCommand extends Command {

    private final BanSystemWD instance;

    public BanlogCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.BanlogCommand.Name"), config.getString("Commands.BanlogCommand.Permission"), config.getStringList("Commands.BanlogCommand.Aliases").toArray(new String[]{}));
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
                    sender.sendMessage(Language.get("banlog.info", player, i));
                    banlog.forEach(ban -> {
                        sender.sendMessage(Language.get("banlog.placeholder"));
                        sender.sendMessage(Language.get("banlog.reason", ban.getReason()));
                        sender.sendMessage(Language.get("banlog.id", ban.getBanID()));
                        sender.sendMessage(Language.get("banlog.banner", ban.getBanner()));
                        sender.sendMessage(Language.get("banlog.date", ban.getDate()));
                    });
                });
            } else sender.sendMessage(Language.get("usage.banlogcommand", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
