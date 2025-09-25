package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.dto.offer.NewReservationDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.ReservationRepository;
import ftn.siit.project.isspoject.service.external.EmailService;
import ftn.siit.project.isspoject.service.implementations.ReservationServiceImpl;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceUT {

    @InjectMocks
    private ReservationServiceImpl reservationService; // konkretna implementacija

    @Mock private ReservationRepository reservationRepository;
    @Mock private EmailService emailService;
    @Mock
    private UserService userService;
    // ---------- helpers ----------
    private Service serviceVarDuration() {
        Service s = new Service();
        s.setId(10);
        s.setName("Photography");
        s.setDescription("Photo package");
        s.setPreciseDuration(0);
        s.setMinDuration(30);
        s.setMaxDuration(180);
        Category c = new Category(); c.setId(3); c.setName("PHOTO");
        s.setCategory(c);
        Provider p = new Provider();
        p.setId(5); p.setName("Mika"); p.setCompanyName("Mika Studio"); p.setEmail("mika@studio.com");
        s.setProvider(p);
        s.setPrice(100.0); s.setSale(0.0);
        return s;
    }


    private Service serviceFixedDuration(int minutes) {
        Service s = serviceVarDuration();
        s.setPreciseDuration(minutes);
        return s;
    }

    private Organizer organizer() {
        Organizer o = new Organizer();
        o.setId(7); o.setName("Ana"); o.setEmail("ana@event.com");
        return o;
    }

    private Event event() {
        Event e = new Event();
        e.setId(77); e.setName("TechConf"); e.setDescription("Annual conf");
        e.setDate(LocalDateTime.of(2025, 12, 20, 9, 0));
        Budget b = new Budget(); b.setId(321); e.setBudget(b);
        return e;
    }

    private NewReservationDTO dto(Integer serviceId, Integer eventId, LocalDateTime start, LocalDateTime end) {
        NewReservationDTO d = new NewReservationDTO();
        d.setServiceId(serviceId);
        d.setEventId(eventId);
        d.setStartTime(start);
        d.setEndTime(end);
        return d;
    }


    @Test
    @DisplayName("addReservation: booking deadline — throws if made too late")
    void addReservation_bookingDeadline_missed_throws() {
        Service s = serviceVarDuration();
        s.setLatestReservation(24); // 24h in advance
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        // Start time only 12h from now
        LocalDateTime start = LocalDateTime.now().plusHours(12);
        LocalDateTime end = start.plusHours(1);

        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        assertThatThrownBy(() -> reservationService.addReservation(e, s, p, o, d, userService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("hours before its start time");

        verify(reservationRepository, never()).save(any());
        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("addReservation: booking deadline — passes when booked early enough")
    void addReservation_bookingDeadline_respected_ok() {
        Service s = serviceVarDuration();
        s.setLatestReservation(24); // 24h in advance
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);
        when(reservationRepository.findReservationsByServiceId(s.getId())).thenReturn(List.of());
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(555);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        // Start time 48h from now
        LocalDateTime start = LocalDateTime.now().plusHours(48);
        LocalDateTime end = start.plusHours(1);

        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(555);
        verify(reservationRepository).save(any());
        verify(emailService, times(2)).sendMail(any(), eq("Service reservation confirmation!"), any());
    }
    @Test
    @DisplayName("addReservation: booking deadline — exactly at deadline succeeds")
    void addReservation_bookingDeadline_exact_ok() {
        Service s = serviceVarDuration();
        s.setLatestReservation(24); // 24h in advance
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);
        when(reservationRepository.findReservationsByServiceId(s.getId())).thenReturn(List.of());
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(303);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        // exactly 24h before start
        LocalDateTime start = LocalDateTime.now().plusHours(24);
        LocalDateTime end   = start.plusHours(1);
        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(303);
        verify(reservationRepository).save(any());
    }

    @Test
    @DisplayName("addReservation: latestReservation=null — deadline check skipped")
    void addReservation_latestReservationNull_ok() {
        Service s = serviceVarDuration();
        s.setLatestReservation(null); // no deadline
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);
        when(reservationRepository.findReservationsByServiceId(s.getId())).thenReturn(List.of());
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(306);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end   = start.plusHours(1);
        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(306);
        verify(reservationRepository).save(any());
    }

    @Test
    @DisplayName("addReservation: rejects when overlapping with provider's closed hours")
    void addReservation_closedHours_throws() {
        Service s = serviceVarDuration();
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        LocalDateTime start = LocalDateTime.of(2025, 9, 20, 8, 0);
        LocalDateTime end   = LocalDateTime.of(2025, 9, 20, 9, 0);
        when(userService.overlapsWithClosedHours(eq(start), eq(end), any(), any())).thenReturn(true);

        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        assertThatThrownBy(() -> reservationService.addReservation(e, s, p, o, d, userService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("closed hours");

        verify(reservationRepository, never()).save(any());
        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("addReservation: var duration — below minimum duration is rejected")
    void addReservation_varDuration_belowMin_throws() {
        Service s = serviceVarDuration();
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);

        // 20 < 30
        NewReservationDTO shortD = dto(s.getId(), e.getId(),
                LocalDateTime.of(2025, 9, 20, 10, 0),
                LocalDateTime.of(2025, 9, 20, 10, 20));

        assertThatThrownBy(() -> reservationService.addReservation(e, s, p, o, shortD, userService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be at least 30 minutes");
    }

    @Test
    @DisplayName("addReservation: var duration — above maximum duration is rejected")
    void addReservation_varDuration_aboveMax_throws() {
        Service s = serviceVarDuration();
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);

        // 240 > 180
        NewReservationDTO longD = dto(s.getId(), e.getId(),
                LocalDateTime.of(2025, 9, 20, 10, 0),
                LocalDateTime.of(2025, 9, 20, 14, 0));

        assertThatThrownBy(() -> reservationService.addReservation(e, s, p, o, longD, userService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be at most 180 minutes");
    }

    @Test
    @DisplayName("addReservation: var duration — exactly minDuration succeeds")
    void addReservation_varDuration_exactMin_ok() {
        Service s = serviceVarDuration();
        s.setMinDuration(30);
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);
        when(reservationRepository.findReservationsByServiceId(s.getId())).thenReturn(List.of());
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(301);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        // exactly 30 minutes
        LocalDateTime start = LocalDateTime.of(2025, 9, 20, 10, 0);
        LocalDateTime end   = start.plusMinutes(30);
        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(301);
        verify(reservationRepository).save(any());
    }

    @Test
    @DisplayName("addReservation: var duration — exactly maxDuration succeeds")
    void addReservation_varDuration_exactMax_ok() {
        Service s = serviceVarDuration();
        s.setMaxDuration(180);
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);
        when(reservationRepository.findReservationsByServiceId(s.getId())).thenReturn(List.of());
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(302);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        // exactly 180 minutes
        LocalDateTime start = LocalDateTime.of(2025, 9, 20, 10, 0);
        LocalDateTime end   = start.plusMinutes(180);
        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(302);
        verify(reservationRepository).save(any());
    }

    @Test
    @DisplayName("addReservation: null min/max durations — skips validation and succeeds")
    void addReservation_nullMinMax_ok() {
        Service s = serviceVarDuration();
        s.setMinDuration(null);
        s.setMaxDuration(null);
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);
        when(reservationRepository.findReservationsByServiceId(s.getId())).thenReturn(List.of());
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(305);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        // 3h reservation (no min/max to check)
        LocalDateTime start = LocalDateTime.of(2025, 9, 20, 10, 0);
        LocalDateTime end   = start.plusHours(3);
        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(305);
        verify(reservationRepository).save(any());
    }

    @Test
    @DisplayName("addReservation: only minDuration set — enforces lower bound, ignores max")
    void addReservation_onlyMinDuration_ok() {
        Service s = serviceVarDuration();
        s.setMinDuration(45);
        s.setMaxDuration(null); // no max
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);
        when(reservationRepository.findReservationsByServiceId(s.getId())).thenReturn(List.of());
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(401);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        // exactly 45 minutes
        LocalDateTime start = LocalDateTime.of(2025, 9, 20, 10, 0);
        LocalDateTime end   = start.plusMinutes(45);
        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(401);
        verify(reservationRepository).save(any());
    }

    @Test
    @DisplayName("addReservation: only maxDuration set — enforces upper bound, ignores min")
    void addReservation_onlyMaxDuration_ok() {
        Service s = serviceVarDuration();
        s.setMinDuration(null); // no min
        s.setMaxDuration(120);
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);
        when(reservationRepository.findReservationsByServiceId(s.getId())).thenReturn(List.of());
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(402);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        // 2h = exactly maxDuration
        LocalDateTime start = LocalDateTime.of(2025, 9, 20, 14, 0);
        LocalDateTime end   = start.plusHours(2);
        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(402);
        verify(reservationRepository).save(any());
    }
    @Test
    @DisplayName("addReservation: only minDuration set — below min rejected")
    void addReservation_onlyMinDuration_belowMin_throws() {
        Service s = serviceVarDuration();
        s.setMinDuration(45);
        s.setMaxDuration(null);
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);

        // 30 < 45
        LocalDateTime start = LocalDateTime.of(2025, 9, 20, 10, 0);
        LocalDateTime end   = start.plusMinutes(30);
        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        assertThatThrownBy(() -> reservationService.addReservation(e, s, p, o, d, userService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be at least 45 minutes");
    }

    @Test
    @DisplayName("addReservation: only minDuration set — exactly at min succeeds")
    void addReservation_onlyMinDuration_exact_ok() {
        Service s = serviceVarDuration();
        s.setMinDuration(45);
        s.setMaxDuration(null);
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);
        when(reservationRepository.findReservationsByServiceId(s.getId())).thenReturn(List.of());
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(501);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        // 45 exactly
        LocalDateTime start = LocalDateTime.of(2025, 9, 20, 11, 0);
        LocalDateTime end   = start.plusMinutes(45);
        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(501);
        verify(reservationRepository).save(any());
    }
    @Test
    @DisplayName("addReservation: only maxDuration set — above max rejected")
    void addReservation_onlyMaxDuration_aboveMax_throws() {
        Service s = serviceVarDuration();
        s.setMinDuration(null);
        s.setMaxDuration(120);
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);

        // 150 > 120
        LocalDateTime start = LocalDateTime.of(2025, 9, 20, 14, 0);
        LocalDateTime end   = start.plusMinutes(150);
        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        assertThatThrownBy(() -> reservationService.addReservation(e, s, p, o, d, userService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("120");
    }

    @Test
    @DisplayName("addReservation: only maxDuration set — exactly at max succeeds")
    void addReservation_onlyMaxDuration_exact_ok() {
        Service s = serviceVarDuration();
        s.setMinDuration(null);
        s.setMaxDuration(120);
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);
        when(reservationRepository.findReservationsByServiceId(s.getId())).thenReturn(List.of());
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(502);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        // 120 exactly
        LocalDateTime start = LocalDateTime.of(2025, 9, 20, 14, 0);
        LocalDateTime end   = start.plusMinutes(120);
        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(502);
        verify(reservationRepository).save(any());
    }

    @Test
    @DisplayName("addReservation: fixed duration service — rejects if duration doesn't match preciseDuration")
    void addReservation_fixedDuration_invalidLength_throws() {
        Service s = serviceFixedDuration(90);
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);

        // 60m != 90m
        NewReservationDTO d = dto(s.getId(), e.getId(),
                LocalDateTime.of(2025, 9, 20, 10, 0),
                LocalDateTime.of(2025, 9, 20, 11, 0));

        assertThatThrownBy(() -> reservationService.addReservation(e, s, p, o, d, userService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be exactly 90 minutes");
    }

    @Test
    @DisplayName("addReservation: fixed duration service — accepts when duration matches preciseDuration")
    void addReservation_fixedDuration_validLength_ok() {
        Service s = serviceFixedDuration(90);
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);
        when(reservationRepository.findReservationsByServiceId(s.getId())).thenReturn(List.of());
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(222);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        // 90m = 90m
        NewReservationDTO d = dto(s.getId(), e.getId(),
                LocalDateTime.of(2025, 9, 20, 10, 0),
                LocalDateTime.of(2025, 9, 20, 11, 30));

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(222);
        verify(reservationRepository).save(any());
        verify(emailService, times(2)).sendMail(any(), eq("Service reservation confirmation!"), any());
    }


    @Test
    @DisplayName("addReservation: new reservation overlaps at the start — rejected")
    void addReservation_overlap_startOverlap_throws() {
        Service s = serviceVarDuration();
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);

        // existing: 10:00–11:00
        Reservation existing = new Reservation();
        existing.setOfferService(s);
        existing.setStartTime(LocalDateTime.of(2025, 9, 20, 10, 0));
        existing.setEndTime(LocalDateTime.of(2025, 9, 20, 11, 0));
        when(reservationRepository.findReservationsByServiceId(s.getId()))
                .thenReturn(List.of(existing));

        // new: 10:30–11:30
        NewReservationDTO d = dto(s.getId(), e.getId(),
                LocalDateTime.of(2025, 9, 20, 10, 30),
                LocalDateTime.of(2025, 9, 20, 11, 30));

        assertThatThrownBy(() -> reservationService.addReservation(e, s, p, o, d, userService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("isn't available");
    }
    @Test
    @DisplayName("addReservation: new overlaps with two existing reservations — rejected")
    void addReservation_overlap_twoExisting_throws() {
        Service s = serviceVarDuration();
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);

        // existing : 10-11 and 12-13
        Reservation r1 = new Reservation();
        r1.setOfferService(s);
        r1.setStartTime(LocalDateTime.of(2025, 9, 20, 10, 0));
        r1.setEndTime(LocalDateTime.of(2025, 9, 20, 11, 0));

        Reservation r2 = new Reservation();
        r2.setOfferService(s);
        r2.setStartTime(LocalDateTime.of(2025, 9, 20, 12, 0));
        r2.setEndTime(LocalDateTime.of(2025, 9, 20, 13, 0));

        when(reservationRepository.findReservationsByServiceId(s.getId()))
                .thenReturn(List.of(r1, r2));

        // new: 10:30–12:30
        NewReservationDTO d = dto(s.getId(), e.getId(),
                LocalDateTime.of(2025, 9, 20, 10, 30),
                LocalDateTime.of(2025, 9, 20, 12, 30));

        assertThatThrownBy(() -> reservationService.addReservation(e, s, p, o, d, userService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("isn't available");
    }


    @Test
    @DisplayName("addReservation: boundary case — touching point (end == start) is not overlap, should succeed")
    void addReservation_startAtExistignEnd_ok() {
        Service s = serviceVarDuration();
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);

        // existing: 10:00–11:00
        Reservation existing = new Reservation();
        existing.setOfferService(s);
        existing.setStartTime(LocalDateTime.of(2025, 9, 20, 10, 0));
        existing.setEndTime(LocalDateTime.of(2025, 9, 20, 11, 0));
        when(reservationRepository.findReservationsByServiceId(s.getId()))
                .thenReturn(List.of(existing));

        // new: 11:00–12:00
        NewReservationDTO d = dto(s.getId(), e.getId(),
                LocalDateTime.of(2025, 9, 20, 11, 0),
                LocalDateTime.of(2025, 9, 20, 12, 0));

        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(999);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(999);
        verify(reservationRepository).save(any(Reservation.class));
        verify(emailService, times(2)).sendMail(any(), eq("Service reservation confirmation!"), any());
    }

    @Test
    @DisplayName("addReservation: boundary case — touching point (start == existing end) is not overlap, should succeed")
    void addReservation_endAtExistingStart_ok() {
        Service s = serviceVarDuration();
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);

        // existing: 12:00–13:00
        Reservation existing = new Reservation();
        existing.setOfferService(s);
        existing.setStartTime(LocalDateTime.of(2025, 9, 20, 12, 0));
        existing.setEndTime(LocalDateTime.of(2025, 9, 20, 13, 0));
        when(reservationRepository.findReservationsByServiceId(s.getId()))
                .thenReturn(List.of(existing));

        // new: 11:00–12:00
        NewReservationDTO d = dto(s.getId(), e.getId(),
                LocalDateTime.of(2025, 9, 20, 11, 0),
                LocalDateTime.of(2025, 9, 20, 12, 0));

        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(1002);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(1002);
        verify(reservationRepository).save(any(Reservation.class));
        verify(emailService, times(2)).sendMail(any(), eq("Service reservation confirmation!"), any());
    }

    @Test
    @DisplayName("addReservation: new reservation fully contains existing — rejected")
    void addReservation_overlap_fullContainment_throws() {
        Service s = serviceVarDuration();
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);

        // existing: 10:00–11:00
        Reservation existing = new Reservation();
        existing.setOfferService(s);
        existing.setStartTime(LocalDateTime.of(2025, 9, 20, 10, 0));
        existing.setEndTime(LocalDateTime.of(2025, 9, 20, 11, 0));
        when(reservationRepository.findReservationsByServiceId(s.getId()))
                .thenReturn(List.of(existing));

        // new: 9:30–11:30
        NewReservationDTO d = dto(s.getId(), e.getId(),
                LocalDateTime.of(2025, 9, 20, 9, 30),
                LocalDateTime.of(2025, 9, 20, 11, 30));

        assertThatThrownBy(() -> reservationService.addReservation(e, s, p, o, d, userService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("isn't available");
    }

    @Test
    @DisplayName("addReservation: new reservation overlaps at the end — rejected")
    void addReservation_overlap_endOverlap_throws() {
        Service s = serviceVarDuration();
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);

        // existing: 10:00–11:00
        Reservation existing = new Reservation();
        existing.setOfferService(s);
        existing.setStartTime(LocalDateTime.of(2025, 9, 20, 10, 0));
        existing.setEndTime(LocalDateTime.of(2025, 9, 20, 11, 0));
        when(reservationRepository.findReservationsByServiceId(s.getId()))
                .thenReturn(List.of(existing));

        // new: 9:30–10:30
        NewReservationDTO d = dto(s.getId(), e.getId(),
                LocalDateTime.of(2025, 9, 20, 9, 30),
                LocalDateTime.of(2025, 9, 20, 10, 30));

        assertThatThrownBy(() -> reservationService.addReservation(e, s, p, o, d, userService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("isn't available");
    }

    @Test
    @DisplayName("addReservation: new reservation exactly matches existing — rejected")
    void addReservation_overlap_exactMatch_throws() {
        Service s = serviceVarDuration();
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);

        // existing: 10:00–11:00
        Reservation existing = new Reservation();
        existing.setOfferService(s);
        existing.setStartTime(LocalDateTime.of(2025, 9, 20, 10, 0));
        existing.setEndTime(LocalDateTime.of(2025, 9, 20, 11, 0));
        when(reservationRepository.findReservationsByServiceId(s.getId()))
                .thenReturn(List.of(existing));

        // new: 10:00–11:00
        NewReservationDTO d = dto(s.getId(), e.getId(),
                LocalDateTime.of(2025, 9, 20, 10, 0),
                LocalDateTime.of(2025, 9, 20, 11, 0));

        assertThatThrownBy(() -> reservationService.addReservation(e, s, p, o, d, userService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("isn't available");
    }


    @Test
    @DisplayName("addReservation: successful reservation — saves and sends 2 emails (to organizer and provider")
    void addReservation_success_savesAndEmails() {
        Service s = serviceVarDuration();
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);
        when(reservationRepository.findReservationsByServiceId(s.getId())).thenReturn(List.of());
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(1001);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        NewReservationDTO d = dto(s.getId(), e.getId(),
                LocalDateTime.of(2025, 9, 20, 12, 0),
                LocalDateTime.of(2025, 9, 20, 13, 30));

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(1001);
        verify(reservationRepository).save(any(Reservation.class));
        verify(emailService, times(2)).sendMail(any(), eq("Service reservation confirmation!"), any());
    }
    @Test
    @DisplayName("addReservation: fits between two existing reservations succeeds")
    void addReservation_betweenExisting_ok() {
        Service s = serviceVarDuration();
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(userService.overlapsWithClosedHours(any(), any(), any(), any())).thenReturn(false);

        // existing: 9–10 and 12–13
        Reservation r1 = new Reservation();
        r1.setOfferService(s);
        r1.setStartTime(LocalDateTime.of(2025, 9, 20, 9, 0));
        r1.setEndTime(LocalDateTime.of(2025, 9, 20, 10, 0));

        Reservation r2 = new Reservation();
        r2.setOfferService(s);
        r2.setStartTime(LocalDateTime.of(2025, 9, 20, 12, 0));
        r2.setEndTime(LocalDateTime.of(2025, 9, 20, 13, 0));

        when(reservationRepository.findReservationsByServiceId(s.getId()))
                .thenReturn(List.of(r1, r2));
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(304);
            return r;
        });
        when(emailService.sendMail(any(), any(), any())).thenReturn("SENT");

        // new: 10–12
        NewReservationDTO d = dto(s.getId(), e.getId(),
                LocalDateTime.of(2025, 9, 20, 10, 0),
                LocalDateTime.of(2025, 9, 20, 12, 0));

        Reservation created = reservationService.addReservation(e, s, p, o, d, userService);

        assertThat(created.getId()).isEqualTo(304);
        verify(reservationRepository).save(any(Reservation.class));
        verify(emailService, times(2)).sendMail(any(), eq("Service reservation confirmation!"), any());

    }


    @Test
    @DisplayName("addReservation: rejects when start time is in the past")
    void addReservation_startInPast_throws() {
        Service s = serviceVarDuration();
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        LocalDateTime start = LocalDateTime.now().minusHours(2);
        LocalDateTime end = LocalDateTime.now().minusHours(1);
        NewReservationDTO d = dto(s.getId(), e.getId(), start, end);

        assertThatThrownBy(() -> reservationService.addReservation(e, s, p, o, d, userService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot start in the past");

        verify(reservationRepository, never()).save(any());
        verifyNoInteractions(emailService);
    }


    @Test
    @DisplayName("findAll: throws NotFound when empty")
    void findAll_empty_NotFound() {
        when(reservationRepository.findAll()).thenReturn(List.of());
        assertThatThrownBy(() -> reservationService.findAll())
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("No reservations");
    }

    @Test
    @DisplayName("findById: returns entity when found")
    void findById_success() {
        Reservation r = new Reservation(); r.setId(123);
        when(reservationRepository.findById(123)).thenReturn(Optional.of(r));
        assertThat(reservationService.findById(123).getId()).isEqualTo(123);
    }

    @Test
    @DisplayName("findById: throws NotFound when not found")
    void findById_NotFound() {
        when(reservationRepository.findById(999)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reservationService.findById(999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("findByEventId: throws NotFound when no reservations")
    void findByEventId_empty_NotFound() {
        when(reservationRepository.findReservationsByEventId(77)).thenReturn(List.of());
        assertThatThrownBy(() -> reservationService.findByEventId(77))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("No reservations found for event ID: 77");
    }

    @Test
    @DisplayName("findByServiceId: returns list (may be empty)")
    void findByServiceId_returnsList() {
        when(reservationRepository.findReservationsByServiceId(10)).thenReturn(List.of());
        assertThat(reservationService.findByServiceId(10)).isEmpty();
        verify(reservationRepository).findReservationsByServiceId(10);
    }

    @Test
    @DisplayName("save(Reservation): null throws IAE")
    void save_nullReservation_throws() {
        assertThatThrownBy(() -> reservationService.save((Reservation) null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("save(NewReservationDTO): maps start/end and persists")
    void save_dto_mapsAndPersists() {
        NewReservationDTO d = dto(10, 77,
                LocalDateTime.of(2025, 9, 20, 10, 0),
                LocalDateTime.of(2025, 9, 20, 11, 30));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        Reservation saved = reservationService.save(d);

        assertThat(saved.getStartTime()).isEqualTo(LocalDateTime.of(2025, 9, 20, 10, 0));
        assertThat(saved.getEndTime()).isEqualTo(LocalDateTime.of(2025, 9, 20, 11, 30));
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("save(NewReservationDTO): null throws IAE")
    void save_nullDto_throws() {
        assertThatThrownBy(() -> reservationService.save((NewReservationDTO) null))
                .isInstanceOf(IllegalArgumentException.class);
    }


}
