package net.llamadevelopment.bansystemwd.components.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@AllArgsConstructor
@Getter
public class EditBanTimeEvent extends Event {

    private final String player;
    private final long time;
    private final String executor;

}
