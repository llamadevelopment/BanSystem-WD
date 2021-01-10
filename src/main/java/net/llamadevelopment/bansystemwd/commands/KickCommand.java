package net.llamadevelopment.bansystemwd.commands;

import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.event.PlayerKickEvent;
import net.llamadevelopment.bansystemwd.components.event.PlayerMuteEvent;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class KickCommand extends Command {

    private final BanSystemWD instance;

    public KickCommand(Config config, BanSystemWD instance) {
        super(config.getString("Commands.KickCommand.Name"), config.getString("Commands.KickCommand.Permission"), config.getStringList("Commands.KickCommand.Aliases").toArray(new String[]{}));
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length >= 2) {
                final String player = args[0];
                final StringBuilder reason = new StringBuilder();
                for (int i = 1; i < args.length; ++i) reason.append(args[i]).append(" ");
                final ProxiedPlayer onlinePlayer = ProxyServer.getInstance().getPlayer(player);
                if (onlinePlayer != null) {
                    onlinePlayer.disconnect(Language.getNP("screen.kick", reason.toString(), sender.getName()));
                    sender.sendMessage(Language.get("player.kicked", player));
                    ProxyServer.getInstance().getPluginManager().callEvent(new PlayerKickEvent(player, sender.getName()));
                } else sender.sendMessage(Language.get("player.not.online"));
            } else sender.sendMessage(Language.get("usage.kickcommand", this.getName()));
        } else sender.sendMessage(Language.get("permission.insufficient"));
    }

}
