package net.llamadevelopment.bansystemwd.components.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.llamadevelopment.bansystemwd.components.data.Mute;
import net.md_5.bungee.api.plugin.Event;

@AllArgsConstructor
@Getter
public class PlayerMuteEvent extends Event {

    private final Mute mute;

}
