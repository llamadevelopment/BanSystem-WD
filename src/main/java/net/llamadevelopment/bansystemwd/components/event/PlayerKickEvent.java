package net.llamadevelopment.bansystemwd.components.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@AllArgsConstructor
@Getter
public class PlayerKickEvent extends Event {

    private final String target;
    private final String executor;

}
