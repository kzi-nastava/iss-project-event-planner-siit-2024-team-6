package ftn.siit.project.isspoject.entity;

import lombok.Data;

@Data
public class Reservation {
    private Integer id;
//    Integer eventId;
    private Service service;
    private boolean isCanceled;
    private TimeSlot time;
}
