package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class DeletewarnCommand extends Command {

    private final BanSystemWD instance;

    public DeletewarnCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.DeletewarnCommand.Name"), config.getString("Commands.DeletewarnCommand.Permission"), config.getStringList("Commands.DeletewarnCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                final String id = args[0];
                this.instance.getProvider().warnIdExists(id, exists -> {
                    if (exists) {
                        this.instance.getProvider().deleteWarn(id, sender.getName());
                        sender.sendMessage(Language.get("warn.deleted", id));
                        return;
                    }
                    sender.sendMessage(Language.get("id.not.found", id));
                });
            } else sender.sendMessage(Language.get("usage.deletewarncommand", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
