package net.llamadevelopment.bansystemwd.components.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MuteReason {

    private final String reason;
    private final String id;
    private final int seconds;

}
