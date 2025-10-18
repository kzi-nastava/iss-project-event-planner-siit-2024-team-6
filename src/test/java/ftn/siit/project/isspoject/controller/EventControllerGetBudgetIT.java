package ftn.siit.project.isspoject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.service.external.PDFGeneratorService;
import ftn.siit.project.isspoject.service.interfaces.CategoryService;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import ftn.siit.project.isspoject.service.interfaces.EventTypeService;
import ftn.siit.project.isspoject.service.interfaces.OrganizerService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import ftn.siit.project.isspoject.util.TokenUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WebMvc slice ITs for ONLY: GET /api/events/{eventId}/budget
 * Uses @WithMockUser(username=EMAIL, roles={"ORGANIZER"}) to supply the principal.
 */
@WebMvcTest(controllers = EventController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class EventControllerGetBudgetIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    // Mock ALL @Autowired deps on EventController so the slice can boot
    @MockBean private EventService eventService;
    @MockBean private PDFGeneratorService pdfGeneratorService;
    @MockBean private CategoryService categoryService;
    @MockBean private UserService userService;
    @MockBean private OrganizerService organizerService;
    @MockBean private EventTypeService eventTypeService;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private TokenUtils tokenUtils; // not used by this endpoint now, but still autowired in controller

    private static final String BASE  = "/api/events";
    private static final String EMAIL = "organizer@example.com";

    private Organizer organizer() {
        Organizer o = new Organizer();
        o.setId(42);
        o.setEmail(EMAIL);
        o.setName("Org");
        return o;
    }

    private Event futureEventWithBudget(int id) {
        Event e = new Event();
        e.setId(id);
        e.setDate(LocalDateTime.now().plusDays(3));
        e.setBudget(new Budget());
        return e;
    }

    private Event pastEvent(int id) {
        Event e = new Event();
        e.setId(id);
        e.setDate(LocalDateTime.now().minusDays(2));
        e.setBudget(new Budget());
        return e;
    }

    // ---------------------- Tests ----------------------

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("GET budget — 401 when organizer (by principal email) not found")
    void getBudget_unauthorized_whenOrganizerMissing() throws Exception {
        int eventId = 101;
        when(organizerService.findByEmail(EMAIL)).thenReturn(null);

        mockMvc.perform(get(BASE + "/{eventId}/budget", eventId))
               .andExpect(status().isUnauthorized());

        verifyNoInteractions(eventService);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("GET budget — 400 when event already passed (IllegalArgumentException mapped)")
    void getBudget_eventPassed_400() throws Exception {
        int eventId = 102;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.findById(eventId)).thenReturn(pastEvent(eventId));

        mockMvc.perform(get(BASE + "/{eventId}/budget", eventId))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Cannot access budget of an event that has passed."));

        verify(eventService).findById(eventId);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("GET budget — 200 OK when event is in the future and budget exists")
    void getBudget_ok() throws Exception {
        int eventId = 103;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.findById(eventId)).thenReturn(futureEventWithBudget(eventId));

        mockMvc.perform(get(BASE + "/{eventId}/budget", eventId)
                        .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(eventService).findById(eventId);
    }

    // ------------ Test-slice advice: map IllegalArgumentException -> 400 ------------
    @RestControllerAdvice
    @Profile("test")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    static class TestExceptionHandler {
        @ExceptionHandler(IllegalArgumentException.class)
        public org.springframework.http.ResponseEntity<Map<String, String>> handleIAE(IllegalArgumentException ex) {
            return org.springframework.http.ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage()));
        }
    }
}
