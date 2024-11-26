package ftn.siit.project.isspoject.entity;

import lombok.Data;

@Data
public class Reservation {
    Integer id;
    Integer eventId;
    Integer serviceId;
    boolean isCanceled;
    TimeSlot time;
}
