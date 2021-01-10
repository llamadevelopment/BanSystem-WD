package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class DeletemuteCommand extends Command {

    private final BanSystemWD instance;

    public DeletemuteCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.DeletemuteCommand.Name"), config.getString("Commands.DeletemuteCommand.Permission"), config.getStringList("Commands.DeletemuteCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                final String id = args[0];
                this.instance.getProvider().muteIdExists(id, true, exists -> {
                    if (exists) {
                        this.instance.getProvider().deleteMute(id, sender.getName());
                        sender.sendMessage(Language.get("mute.deleted", id));
                        return;
                    }
                    sender.sendMessage(Language.get("id.not.found", id));
                });
            } else sender.sendMessage(Language.get("usage.deletemutecommand", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
