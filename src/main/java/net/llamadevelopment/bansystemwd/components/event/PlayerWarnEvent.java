package net.llamadevelopment.bansystemwd.components.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.llamadevelopment.bansystemwd.components.data.Warn;
import net.md_5.bungee.api.plugin.Event;

@AllArgsConstructor
@Getter
public class PlayerWarnEvent extends Event {

    private final Warn warn;

}
