package ftn.siit.project.isspoject.entity;

import java.util.Optional;

public enum NotificationType {
    INFO,
    SUGGESTION;

    public static Optional fromString(String value) {
        for (NotificationType type : NotificationType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
