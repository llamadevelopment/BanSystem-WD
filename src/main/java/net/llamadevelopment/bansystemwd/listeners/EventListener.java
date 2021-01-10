package net.llamadevelopment.bansystemwd.listeners;

import lombok.RequiredArgsConstructor;
import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.data.Mute;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@RequiredArgsConstructor
public class EventListener implements Listener {

    private final BanSystemWD instance;

    @EventHandler
    public void on(final PreLoginEvent event) {
        final String player = event.getConnection().getName();
        this.instance.getProvider().playerIsBanned(player, isBanned -> {
            if (isBanned) {
                this.instance.getProvider().getBan(player, ban -> {
                    if (ban.getTime() != -1 && ban.getTime() < System.currentTimeMillis()) {
                        this.instance.getProvider().unbanPlayer(player, "");
                        return;
                    }
                    event.setCancelReason(Language.getNP("screen.ban", ban.getReason(), ban.getBanID(), this.instance.getProvider().getRemainingTime(ban.getTime())));
                    event.setCancelled(true);
                });
            } else {
                this.instance.getProvider().playerIsMuted(player, isMuted -> {
                    if (isMuted) {
                        this.instance.getProvider().getMute(player, mute -> this.instance.getProvider().cachedMutes.put(player, mute));
                    }
                });
            }
        });
    }

    @EventHandler
    public void on(final ChatEvent event) {
        if (!event.isCommand()) {
            final ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getSender();
            if (this.instance.getProvider().cachedMutes.get(proxiedPlayer.getName()) != null) {
                final Mute mute = this.instance.getProvider().cachedMutes.get(proxiedPlayer.getName());
                if (mute.getTime() < System.currentTimeMillis()) {
                    this.instance.getProvider().unmutePlayer(mute.getPlayer(), "");
                    this.instance.getProvider().cachedMutes.remove(mute.getPlayer());
                    return;
                }
                proxiedPlayer.sendMessage(Language.getNP("screen.mute", mute.getReason(), mute.getMuteID(), this.instance.getProvider().getRemainingTime(mute.getTime())));
                event.setCancelled(true);
            }
        }
    }

}
