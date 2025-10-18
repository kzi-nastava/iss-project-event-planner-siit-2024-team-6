package ftn.siit.project.isspoject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ftn.siit.project.isspoject.dto.budget.NewBudgetItemDTO;
import ftn.siit.project.isspoject.entity.BudgetItem;
import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.service.external.PDFGeneratorService;
import ftn.siit.project.isspoject.service.interfaces.*;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-slice IT for ONLY the budget endpoints in OrganizerController.
 *
 * NOTE: We mock EVERY dependency autowired in OrganizerController so the
 * WebMvc slice can start cleanly.
 */
@WebMvcTest(controllers = OrganizerController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OrganizerBudgetEndpointsIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    // Mock ALL collaborators OrganizerController @Autowired's:
    @MockBean private EventService eventService;
    @MockBean private NotificationService notificationService;
    @MockBean private OrganizerService organizerService;
    @MockBean private PDFGeneratorService pdfGeneratorService;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private ftn.siit.project.isspoject.util.TokenUtils tokenUtils;
    @MockBean private UserService userService;
    @MockBean private EventTypeService eventTypeService;
    @MockBean private ActivityService activityService;
    @MockBean private BudgetService budgetService;
    @MockBean private CategoryService categoryService;

    private static final String EMAIL = "organizer1@example.com";
    private static final String BASE = "/api/organizers";

    private Organizer organizer() {
        Organizer o = new Organizer();
        o.setId(7);
        o.setEmail(EMAIL);
        o.setName("Org");
        return o;
    }

    private BudgetItem bi(int id, String cat, double max, double curr) {
        BudgetItem x = new BudgetItem();
        x.setId(id);
        Category c = new Category();
        c.setName(cat);
        x.setCategory(c);
        x.setMaxPrice(max);
        x.setCurrPrice(curr);
        return x;
    }

    // ---------- POST /api/organizers/budget/{budgetId}/items ----------

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("POST add item — 200 OK when organizer exists and event not passed")
    void addItem_ok() throws Exception {
        int budgetId = 321;

        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any(Organizer.class))).thenReturn(false);
        when(budgetService.addItemToBudget(budgetId, "VENUE", 500.0))
                .thenReturn(bi(1001, "VENUE", 500.0, 0.0));

        NewBudgetItemDTO body = new NewBudgetItemDTO();
        body.setCategory("VENUE");
        body.setMaxPrice(500.0);

        mockMvc.perform(post(BASE + "/budget/{budgetId}/items", budgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1001))
                .andExpect(jsonPath("$.category").value("VENUE"))
                .andExpect(jsonPath("$.maxPrice").value(500.0))
                .andExpect(jsonPath("$.currPrice").value(0.0));

        verify(budgetService).addItemToBudget(budgetId, "VENUE", 500.0);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("POST add item — 401 when organizer not found")
    void addItem_unauthorized() throws Exception {
        int budgetId = 321;
        when(organizerService.findByEmail(EMAIL)).thenReturn(null);

        NewBudgetItemDTO body = new NewBudgetItemDTO();
        body.setCategory("VENUE");
        body.setMaxPrice(500.0);

        mockMvc.perform(post(BASE + "/budget/{budgetId}/items", budgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(eventService, budgetService);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("POST add item — 400 when event already passed")
    void addItem_eventPassed_400() throws Exception {
        int budgetId = 321;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any(Organizer.class))).thenReturn(true);

        NewBudgetItemDTO body = new NewBudgetItemDTO();
        body.setCategory("VENUE");
        body.setMaxPrice(500.0);

        mockMvc.perform(post(BASE + "/budget/{budgetId}/items", budgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());

        verify(eventService).checkIfEventHasPassed(eq(budgetId), any(Organizer.class));
        verifyNoInteractions(budgetService);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("POST add item — 400 when category already exists in budget")
    void addItem_duplicateCategory() throws Exception {
        int budgetId = 321;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any())).thenReturn(false);
        when(budgetService.addItemToBudget(budgetId, "VENUE", 500.0))
            .thenThrow(new IllegalArgumentException("Category already exists in budget"));

        NewBudgetItemDTO body = new NewBudgetItemDTO();
        body.setCategory("VENUE");
        body.setMaxPrice(500.0);

        mockMvc.perform(post(BASE + "/budget/{budgetId}/items", budgetId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("POST add item — 400 when category not found")
    void addItem_categoryNotFound() throws Exception {
        int budgetId = 321;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any())).thenReturn(false);
        when(budgetService.addItemToBudget(budgetId, "NOPE", 200.0))
            .thenThrow(new IllegalArgumentException("Category not found"));

        NewBudgetItemDTO body = new NewBudgetItemDTO();
        body.setCategory("NOPE");
        body.setMaxPrice(200.0);

        mockMvc.perform(post(BASE + "/budget/{budgetId}/items", budgetId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest());
    }

    // ---------- PUT /api/organizers/budget/{budgetId}/items/{itemId} ----------

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("PUT update item — 200 OK")
    void update_ok() throws Exception {
        int budgetId = 321, itemId = 55;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any(Organizer.class))).thenReturn(false);
        when(budgetService.updateBudgetItem(budgetId, itemId, 300.0))
                .thenReturn(bi(itemId, "VENUE", 300.0, 50.0));

        mockMvc.perform(put(BASE + "/budget/{budgetId}/items/{itemId}", budgetId, itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(300.0))) // raw number JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.category").value("VENUE"))
                .andExpect(jsonPath("$.maxPrice").value(300.0))
                .andExpect(jsonPath("$.currPrice").value(50.0));

        verify(budgetService).updateBudgetItem(budgetId, itemId, 300.0);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("PUT update item — 401 when organizer not found")
    void update_unauthorized() throws Exception {
        int budgetId = 321, itemId = 55;
        when(organizerService.findByEmail(EMAIL)).thenReturn(null);

        mockMvc.perform(put(BASE + "/budget/{budgetId}/items/{itemId}", budgetId, itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(250.0)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(eventService, budgetService);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("PUT update item — 400 when event already passed")
    void update_eventPassed_400() throws Exception {
        int budgetId = 321, itemId = 55;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any(Organizer.class))).thenReturn(true);

        mockMvc.perform(put(BASE + "/budget/{budgetId}/items/{itemId}", budgetId, itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(250.0)))
                .andExpect(status().isBadRequest());

        verify(eventService).checkIfEventHasPassed(eq(budgetId), any(Organizer.class));
        verifyNoInteractions(budgetService);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("PUT update item — 400 when new max < already spent")
    void update_lessThanSpent_400() throws Exception {
        int budgetId = 321, itemId = 55;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any())).thenReturn(false);
        when(budgetService.updateBudgetItem(budgetId, itemId, 40.0))
            .thenThrow(new IllegalArgumentException("Spent amount cannot be greater than budget item amount"));

        mockMvc.perform(put(BASE + "/budget/{budgetId}/items/{itemId}", budgetId, itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(40.0)))
            .andExpect(status().isBadRequest());
    }


    // ---------- DELETE /api/organizers/budget/{budgetId}/items/{itemId} ----------

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("DELETE item — 200 OK")
    void delete_ok() throws Exception {
        int budgetId = 321, itemId = 88;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any(Organizer.class))).thenReturn(false);

        mockMvc.perform(delete(BASE + "/budget/{budgetId}/items/{itemId}", budgetId, itemId))
                .andExpect(status().isOk());

        verify(budgetService).removeBudgetItem(budgetId, itemId);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("DELETE item — 401 when organizer not found")
    void delete_unauthorized() throws Exception {
        int budgetId = 321, itemId = 88;
        when(organizerService.findByEmail(EMAIL)).thenReturn(null);

        mockMvc.perform(delete(BASE + "/budget/{budgetId}/items/{itemId}", budgetId, itemId))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(eventService, budgetService);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("DELETE item — 400 when event already passed")
    void delete_eventPassed_400() throws Exception {
        int budgetId = 321, itemId = 88;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any(Organizer.class))).thenReturn(true);

        mockMvc.perform(delete(BASE + "/budget/{budgetId}/items/{itemId}", budgetId, itemId))
                .andExpect(status().isBadRequest());

        verify(eventService).checkIfEventHasPassed(eq(budgetId), any(Organizer.class));
        verifyNoInteractions(budgetService);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("DELETE item — 400 when item has non-zero spent")
    void delete_nonZeroSpent_400() throws Exception {
        int budgetId = 321, itemId = 88;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any())).thenReturn(false);
        doThrow(new IllegalArgumentException("Budget item cannot be deleted. Offers already purchased for this item."))
            .when(budgetService).removeBudgetItem(budgetId, itemId);

        mockMvc.perform(delete(BASE + "/budget/{budgetId}/items/{itemId}", budgetId, itemId))
            .andExpect(status().isBadRequest());
    }


    // Map IllegalArgumentException -> 400 for this test slice
    @RestControllerAdvice
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @Profile("test")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    static class TestExceptionHandler {
        @ExceptionHandler(IllegalArgumentException.class)
        public org.springframework.http.ResponseEntity<java.util.Map<String, String>> handleIAE(IllegalArgumentException ex) {
            return org.springframework.http.ResponseEntity.badRequest()
                    .body(java.util.Map.of("message", ex.getMessage()));
        }
    }
}
