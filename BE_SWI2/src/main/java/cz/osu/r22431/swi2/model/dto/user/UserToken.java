package cz.osu.r22431.swi2.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserToken {
    private UUID userId;
    private String username;
}
