package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class WarnCommand extends Command {

    private final BanSystemWD instance;

    public WarnCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.WarnCommand.Name"), config.getString("Commands.WarnCommand.Permission"), config.getStringList("Commands.WarnCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length >= 2) {
                final String player = args[0];
                final StringBuilder reason = new StringBuilder();
                for (int i = 1; i < args.length; ++i) reason.append(args[i]).append(" ");
                this.instance.getProvider().warnPlayer(player, reason.toString(), sender.getName());
                sender.sendMessage(Language.get("player.warned", player));
                this.instance.getProvider().getWarnLog(player, warnlog -> {
                    final int i = warnlog.size();
                    final Config c = this.instance.getConfig();
                    if (c.getBoolean("WarnSystem.EnableBan")) {
                        if (i >= c.getInt("WarnSystem.MaxWarningCount")) {
                            this.instance.getProvider().banPlayer(player, c.getString("WarnSystem.BanReason"), "BanSystem/WarnSystem", c.getInt("WarnSystem.BanSeconds"));
                        }
                    }
                });
            } else sender.sendMessage(Language.get("usage.warncommand", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
