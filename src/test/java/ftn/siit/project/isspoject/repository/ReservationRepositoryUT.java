package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Reservation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/reservation_test_data.sql")
class ReservationRepositoryUT {

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void findByEventId_returns() {
        var results = reservationRepository.findReservationsByEventId(7000);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEvent().getId()).isEqualTo(7000);
    }

    @Test
    void findByServiceId_returns() {
        var results = reservationRepository.findReservationsByServiceId(5000);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getOfferService().getId()).isEqualTo(5000);
    }

    @Test
    void findByEventId_empty() {
        var results = reservationRepository.findReservationsByEventId(9999);
        assertThat(results).isEmpty();
    }

    @Test
    void findByServiceId_empty() {
        var results = reservationRepository.findReservationsByServiceId(9999);
        assertThat(results).isEmpty();
    }

    @Test
    void findByEventId_multipleReservations() {
        // given: insert another reservation for the same eventId = 7000
        var existingReservation = reservationRepository.findReservationsByEventId(7000).get(0);

        Reservation another = new Reservation();
        another.setEvent(existingReservation.getEvent());
        another.setOfferService(existingReservation.getOfferService());
        another.setStartTime(LocalDateTime.now().plusHours(3));
        another.setEndTime(LocalDateTime.now().plusHours(4));
        another.setCanceled(false);

        reservationRepository.saveAndFlush(another);

        // when
        var results = reservationRepository.findReservationsByEventId(7000);

        // then
        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(r -> r.getEvent().getId())
                .containsOnly(7000);
    }
    @Test
    void findByServiceId_multipleReservations() {
        // given: reuse an existing reservation for serviceId = 5000
        var existingReservation = reservationRepository.findReservationsByServiceId(5000).get(0);

        Reservation another = new Reservation();
        another.setEvent(existingReservation.getEvent());
        another.setOfferService(existingReservation.getOfferService());
        another.setStartTime(LocalDateTime.now().plusHours(5));
        another.setEndTime(LocalDateTime.now().plusHours(6));
        another.setCanceled(false);

        reservationRepository.saveAndFlush(another);

        // when
        var results = reservationRepository.findReservationsByServiceId(5000);

        // then
        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(r -> r.getOfferService().getId())
                .containsOnly(5000);
    }
    @Test
    void findByEventId_excludesCanceled() {
        var existingReservation = reservationRepository.findReservationsByEventId(7000).get(0);

        Reservation canceled = new Reservation();
        canceled.setEvent(existingReservation.getEvent());
        canceled.setOfferService(existingReservation.getOfferService());
        canceled.setStartTime(LocalDateTime.now().plusHours(7));
        canceled.setEndTime(LocalDateTime.now().plusHours(8));
        canceled.setCanceled(true);

        reservationRepository.saveAndFlush(canceled);

        var results = reservationRepository.findReservationsByEventId(7000);

        // should only return the non-canceled one
        assertThat(results).allMatch(r -> !r.isCanceled());
    }

    @Test
    void findByServiceId_excludesCanceled() {
        var existingReservation = reservationRepository.findReservationsByServiceId(5000).get(0);

        Reservation canceled = new Reservation();
        canceled.setEvent(existingReservation.getEvent());
        canceled.setOfferService(existingReservation.getOfferService());
        canceled.setStartTime(LocalDateTime.now().plusHours(9));
        canceled.setEndTime(LocalDateTime.now().plusHours(10));
        canceled.setCanceled(true);

        reservationRepository.saveAndFlush(canceled);

        var results = reservationRepository.findReservationsByServiceId(5000);

        assertThat(results).allMatch(r -> !r.isCanceled());
    }

    @Test
    void findByEventId_onlyCanceled_shouldReturnEmpty() {
        // create event-only with canceled reservation
        Reservation canceled = new Reservation();
        canceled.setEvent(reservationRepository.findReservationsByEventId(7000).get(0).getEvent());
        canceled.setOfferService(reservationRepository.findReservationsByServiceId(5000).get(0).getOfferService());
        canceled.setStartTime(LocalDateTime.now().plusDays(1));
        canceled.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        canceled.setCanceled(true);

        reservationRepository.deleteAll();
        reservationRepository.saveAndFlush(canceled);

        var results = reservationRepository.findReservationsByEventId(canceled.getEvent().getId());

        assertThat(results).isEmpty();
    }

    @Test
    void findByServiceId_onlyCanceled_shouldReturnEmpty() {
        // create service-only with canceled reservation
        Reservation canceled = new Reservation();
        canceled.setEvent(reservationRepository.findReservationsByEventId(7000).get(0).getEvent());
        canceled.setOfferService(reservationRepository.findReservationsByServiceId(5000).get(0).getOfferService());
        canceled.setStartTime(LocalDateTime.now().plusDays(2));
        canceled.setEndTime(LocalDateTime.now().plusDays(2).plusHours(1));
        canceled.setCanceled(true);

        reservationRepository.deleteAll();
        reservationRepository.saveAndFlush(canceled);

        var results = reservationRepository.findReservationsByServiceId(canceled.getOfferService().getId());

        assertThat(results).isEmpty();
    }
    @Test
    void findByEventId_mixedActiveAndCanceled_onlyActiveReturned() {
        var existingReservation = reservationRepository.findReservationsByEventId(7000).get(0);

        // add another active
        Reservation active = new Reservation();
        active.setEvent(existingReservation.getEvent());
        active.setOfferService(existingReservation.getOfferService());
        active.setStartTime(LocalDateTime.now().plusHours(2));
        active.setEndTime(LocalDateTime.now().plusHours(3));
        active.setCanceled(false);

        // add a canceled
        Reservation canceled = new Reservation();
        canceled.setEvent(existingReservation.getEvent());
        canceled.setOfferService(existingReservation.getOfferService());
        canceled.setStartTime(LocalDateTime.now().plusHours(4));
        canceled.setEndTime(LocalDateTime.now().plusHours(5));
        canceled.setCanceled(true);

        reservationRepository.saveAndFlush(active);
        reservationRepository.saveAndFlush(canceled);

        var results = reservationRepository.findReservationsByEventId(7000);

        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(r -> !r.isCanceled());
        assertThat(results).anyMatch(r -> r.getStartTime().equals(active.getStartTime()));
        assertThat(results).noneMatch(r -> r.getStartTime().equals(canceled.getStartTime()));
    }

    @Test
    void findByServiceId_mixedActiveAndCanceled_onlyActiveReturned() {
        var existingReservation = reservationRepository.findReservationsByServiceId(5000).get(0);

        // add another active
        Reservation active = new Reservation();
        active.setEvent(existingReservation.getEvent());
        active.setOfferService(existingReservation.getOfferService());
        active.setStartTime(LocalDateTime.now().plusHours(6));
        active.setEndTime(LocalDateTime.now().plusHours(7));
        active.setCanceled(false);

        // add a canceled
        Reservation canceled = new Reservation();
        canceled.setEvent(existingReservation.getEvent());
        canceled.setOfferService(existingReservation.getOfferService());
        canceled.setStartTime(LocalDateTime.now().plusHours(8));
        canceled.setEndTime(LocalDateTime.now().plusHours(9));
        canceled.setCanceled(true);

        reservationRepository.saveAndFlush(active);
        reservationRepository.saveAndFlush(canceled);

        var results = reservationRepository.findReservationsByServiceId(5000);

        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(r -> !r.isCanceled());
        assertThat(results).anyMatch(r -> r.getStartTime().equals(active.getStartTime()));
        assertThat(results).noneMatch(r -> r.getStartTime().equals(canceled.getStartTime()));
    }



}
