package baekgwa.sbb.model.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    TEMPORARY("ROLE_TEMP")
    ;

    private final String value;
}
