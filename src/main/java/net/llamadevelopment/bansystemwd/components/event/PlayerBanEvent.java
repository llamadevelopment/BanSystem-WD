package net.llamadevelopment.bansystemwd.components.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.llamadevelopment.bansystemwd.components.data.Ban;
import net.md_5.bungee.api.plugin.Event;

@AllArgsConstructor
@Getter
public class PlayerBanEvent extends Event {

    private final Ban ban;

}
