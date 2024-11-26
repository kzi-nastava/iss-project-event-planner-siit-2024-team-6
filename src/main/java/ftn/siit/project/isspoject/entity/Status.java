package ftn.siit.project.isspoject.entity;

import java.util.Optional;

public enum Status {
    PENDING,
    ACCEPTED,
    REJECTED;

    public static Optional fromString(String value) {
        for (Status status : Status.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
