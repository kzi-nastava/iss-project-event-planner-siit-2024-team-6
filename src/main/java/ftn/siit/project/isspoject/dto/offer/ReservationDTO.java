package ftn.siit.project.isspoject.dto.offer;

import ftn.siit.project.isspoject.entity.Reservation;
import ftn.siit.project.isspoject.entity.TimeSlot;
import lombok.Data;

@Data
public class ReservationDTO {
    private Integer eventId;
    private Integer serviceId;
    private TimeSlot timeSlot;

    public Reservation toReservation() {
        Reservation reservation = new Reservation();
        reservation.setEventId(eventId);
        reservation.setServiceId(serviceId);
        reservation.setTime(timeSlot);
        reservation.setCanceled(false);
        return reservation;
    }
}
