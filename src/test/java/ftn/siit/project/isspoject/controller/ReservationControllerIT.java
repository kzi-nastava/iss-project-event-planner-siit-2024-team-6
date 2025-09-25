package ftn.siit.project.isspoject.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import ftn.siit.project.isspoject.dto.offer.NewReservationDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.service.interfaces.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ReservationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private ReservationService reservationService;
    @MockBean private UserService userService;
    @MockBean private ServiceService serviceService;
    @MockBean private EventService eventService;
    @MockBean private BudgetService budgetService;
    @MockBean private OrganizerService organizerService;

    // helperi
    private Service service() {
        Service s = new Service();
        s.setId(10);
        s.setPrice(100.0);
        s.setSale(0.0);
        Provider p = new Provider(); p.setId(5);
        s.setProvider(p);
        Category c = new Category(); c.setId(3); s.setCategory(c);
        return s;
    }

    private Event event() {
        Event e = new Event();
        e.setId(77);
        e.setDate(LocalDateTime.of(2025,12,20,10,0));
        Budget b = new Budget(); b.setId(321); e.setBudget(b);
        return e;
    }

    private Organizer organizer() {
        Organizer o = new Organizer();
        o.setId(7);
        return o;
    }

    @Test
    @DisplayName("POST /api/reservations - happy path returns 201 Created")
    void addReservation_success() throws Exception {
        NewReservationDTO dto = new NewReservationDTO();
        dto.setServiceId(10);
        dto.setEventId(77);
        dto.setStartTime(LocalDateTime.of(2025,12,20,12,0));
        dto.setEndTime(LocalDateTime.of(2025,12,20,13,0));

        Service service = service();
        Event event = event();
        Organizer organizer = organizer();
        Provider provider = service.getProvider();
        Reservation reservation = new Reservation();
        reservation.setId(999);
        reservation.setOfferService(service);
        reservation.setEvent(event);

        // stubbing
        when(serviceService.findById(10)).thenReturn(service);
        when(eventService.findById(77)).thenReturn(event);
        when(userService.findById(provider.getId())).thenReturn(provider);
        when(organizerService.findByEventId(77)).thenReturn(organizer);
        when(reservationService.addReservation(eq(event), eq(service), eq(provider), eq(organizer), any(), eq(userService)))
                .thenReturn(reservation);

        mockMvc.perform(post("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(999));

        verify(budgetService).addNewItem(service.getCategory(), 100.0, event.getBudget().getId());
    }

    @Test
    @DisplayName("POST /api/reservations - returns 400 when DTO is null")
    void addReservation_nullDto() throws Exception {
        mockMvc.perform(post("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(serviceService, eventService, reservationService);
    }

    @Test
    @DisplayName("POST /api/reservations - 400 when Service not found")
    void addReservation_serviceNotFound() throws Exception {
        NewReservationDTO dto = new NewReservationDTO();
        dto.setServiceId(99); dto.setEventId(77);

        when(serviceService.findById(99)).thenReturn(null);

        mockMvc.perform(post("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/reservations - 400 when Event not found")
    void addReservation_eventNotFound() throws Exception {
        NewReservationDTO dto = new NewReservationDTO();
        dto.setServiceId(10); dto.setEventId(77);

        when(serviceService.findById(10)).thenReturn(service());
        when(eventService.findById(77)).thenReturn(null);

        mockMvc.perform(post("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/reservations - 400 when Service has no provider")
    void addReservation_noProvider() throws Exception {
        NewReservationDTO dto = new NewReservationDTO();
        dto.setServiceId(10); dto.setEventId(77);

        Service s = service();
        s.setProvider(null);
        when(serviceService.findById(10)).thenReturn(s);
        when(eventService.findById(77)).thenReturn(event());

        mockMvc.perform(post("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/reservations - 400 when Provider not found")
    void addReservation_providerNotFound() throws Exception {
        NewReservationDTO dto = new NewReservationDTO();
        dto.setServiceId(10); dto.setEventId(77);

        Service s = service();
        when(serviceService.findById(10)).thenReturn(s);
        when(eventService.findById(77)).thenReturn(event());
        when(userService.findById(s.getProvider().getId())).thenReturn(null);

        mockMvc.perform(post("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/reservations - 400 when Organizer not found")
    void addReservation_organizerNotFound() throws Exception {
        NewReservationDTO dto = new NewReservationDTO();
        dto.setServiceId(10); dto.setEventId(77);

        Service s = service();
        Event e = event();
        when(serviceService.findById(10)).thenReturn(s);
        when(eventService.findById(77)).thenReturn(e);
        when(userService.findById(s.getProvider().getId())).thenReturn(s.getProvider());
        when(organizerService.findByEventId(77)).thenReturn(null);

        mockMvc.perform(post("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/reservations - 400 when start or end time is null")
    void addReservation_missingTimes() throws Exception {
        NewReservationDTO dto = new NewReservationDTO();
        dto.setServiceId(10);
        dto.setEventId(77);
        dto.setStartTime(null);
        dto.setEndTime(null);

        mockMvc.perform(post("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(serviceService, eventService, reservationService);
    }

    @Test
    @DisplayName("POST /api/reservations - 400 when endTime is before startTime")
    void addReservation_endBeforeStart() throws Exception {
        NewReservationDTO dto = new NewReservationDTO();
        dto.setServiceId(10);
        dto.setEventId(77);
        dto.setStartTime(LocalDateTime.of(2025, 12, 20, 14, 0));
        dto.setEndTime(LocalDateTime.of(2025, 12, 20, 13, 0));

        mockMvc.perform(post("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(serviceService, eventService, reservationService);
    }
    @Test
    @DisplayName("POST /api/reservations - 400 when reservationService rejects reservation")
    void addReservation_serviceThrows() throws Exception {
        NewReservationDTO dto = new NewReservationDTO();
        dto.setServiceId(10); dto.setEventId(77);
        dto.setStartTime(LocalDateTime.of(2025,12,20,12,0));
        dto.setEndTime(LocalDateTime.of(2025,12,20,13,0));

        Service s = service();
        Event e = event();
        Provider p = s.getProvider();
        Organizer o = organizer();

        when(serviceService.findById(10)).thenReturn(s);
        when(eventService.findById(77)).thenReturn(e);
        when(userService.findById(p.getId())).thenReturn(p);
        when(organizerService.findByEventId(77)).thenReturn(o);
        when(reservationService.addReservation(eq(e), eq(s), eq(p), eq(o), any(), eq(userService)))
                .thenThrow(new IllegalArgumentException("Invalid reservation"));

        mockMvc.perform(post("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/reservations - 400 when DTO is present but empty JSON")
    void addReservation_emptyJson() throws Exception {
        mockMvc.perform(post("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(serviceService, eventService, reservationService);
    }

    @Test
    @DisplayName("POST /api/reservations - 400 when DTO has serviceId but no eventId")
    void addReservation_missingEventId() throws Exception {
        NewReservationDTO dto = new NewReservationDTO();
        dto.setServiceId(10);
        dto.setEventId(null);
        dto.setStartTime(LocalDateTime.of(2025, 12, 20, 12, 0));
        dto.setEndTime(LocalDateTime.of(2025, 12, 20, 13, 0));

        mockMvc.perform(post("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(serviceService, eventService, reservationService);
    }

    @Test
    @DisplayName("POST /api/reservations - 400 when DTO has eventId but no serviceId")
    void addReservation_missingServiceId() throws Exception {
        NewReservationDTO dto = new NewReservationDTO();
        dto.setServiceId(null);
        dto.setEventId(77);
        dto.setStartTime(LocalDateTime.of(2025, 12, 20, 12, 0));
        dto.setEndTime(LocalDateTime.of(2025, 12, 20, 13, 0));

        mockMvc.perform(post("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(serviceService, eventService, reservationService);
    }


}
