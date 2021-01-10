package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MutelogCommand extends Command {

    private final BanSystemWD instance;

    public MutelogCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.MutelogCommand.Name"), config.getString("Commands.MutelogCommand.Permission"), config.getStringList("Commands.MutelogCommand.Aliases").toArray(new String[]{}));
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
                    sender.sendMessage(Language.get("mutelog.info", player, i));
                    mutelog.forEach(mute -> {
                        sender.sendMessage(Language.get("mutelog.placeholder"));
                        sender.sendMessage(Language.get("mutelog.reason", mute.getReason()));
                        sender.sendMessage(Language.get("mutelog.id", mute.getMuteID()));
                        sender.sendMessage(Language.get("mutelog.muter", mute.getMuter()));
                        sender.sendMessage(Language.get("mutelog.date", mute.getDate()));
                    });
                });
            } else sender.sendMessage(Language.get("usage.mutelogcommand", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
