package ftn.siit.project.isspoject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetItemDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.entity.BudgetItem;
import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.service.interfaces.BudgetService;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import ftn.siit.project.isspoject.service.interfaces.OfferService;
import ftn.siit.project.isspoject.service.interfaces.OrganizerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-slice IT for BudgetController.
 * - Mocks all collaborators autowired into BudgetController.
 * - Tests happy paths + boundary/exception cases (including planning constraints).
 */
@WebMvcTest(controllers = BudgetController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class BudgetControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private BudgetService budgetService;
    @MockBean private OrganizerService organizerService;
    @MockBean private EventService eventService;
    @MockBean private OfferService offerService;

    private static final String EMAIL = "organizer1@example.com";
    private static final String BASE  = "/api/budgets";

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

    // --------------------- POST /api/budgets/{budgetId}/items ---------------------

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

        mockMvc.perform(post(BASE + "/{budgetId}/items", budgetId)
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

        mockMvc.perform(post(BASE + "/{budgetId}/items", budgetId)
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

        mockMvc.perform(post(BASE + "/{budgetId}/items", budgetId)
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

        mockMvc.perform(post(BASE + "/{budgetId}/items", budgetId)
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

        mockMvc.perform(post(BASE + "/{budgetId}/items", budgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    // --------------------- PUT /api/budgets/{budgetId}/items/{itemId} ---------------------

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("PUT update item — 200 OK")
    void update_ok() throws Exception {
        int budgetId = 321, itemId = 55;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any(Organizer.class))).thenReturn(false);
        when(budgetService.updateBudgetItem(budgetId, itemId, 300.0))
                .thenReturn(bi(itemId, "VENUE", 300.0, 50.0));

        // body is raw number JSON because controller expects primitive double
        mockMvc.perform(put(BASE + "/{budgetId}/items/{itemId}", budgetId, itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(300.0)))
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

        mockMvc.perform(put(BASE + "/{budgetId}/items/{itemId}", budgetId, itemId)
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

        mockMvc.perform(put(BASE + "/{budgetId}/items/{itemId}", budgetId, itemId)
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

        mockMvc.perform(put(BASE + "/{budgetId}/items/{itemId}", budgetId, itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(40.0)))
                .andExpect(status().isBadRequest());
    }

    // --------------------- DELETE /api/budgets/{budgetId}/items/{itemId} ---------------------

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("DELETE item — 200 OK")
    void delete_ok() throws Exception {
        int budgetId = 321, itemId = 88;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any(Organizer.class))).thenReturn(false);

        mockMvc.perform(delete(BASE + "/{budgetId}/items/{itemId}", budgetId, itemId))
                .andExpect(status().isOk());

        verify(budgetService).removeBudgetItem(budgetId, itemId);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("DELETE item — 401 when organizer not found")
    void delete_unauthorized() throws Exception {
        int budgetId = 321, itemId = 88;
        when(organizerService.findByEmail(EMAIL)).thenReturn(null);

        mockMvc.perform(delete(BASE + "/{budgetId}/items/{itemId}", budgetId, itemId))
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

        mockMvc.perform(delete(BASE + "/{budgetId}/items/{itemId}", budgetId, itemId))
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

        mockMvc.perform(delete(BASE + "/{budgetId}/items/{itemId}", budgetId, itemId))
                .andExpect(status().isBadRequest());
    }

    // --------------------- POST /api/budgets/{budgetId}/offers:search ---------------------

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("POST offers:search — 200 OK with default paging")
    void searchOffers_ok() throws Exception {
        int budgetId = 999;

        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any())).thenReturn(false);

        // Return a simple page with 2 offers (fields not asserted, only page shape)
        Page<OfferDTO> page = new PageImpl<>(List.of(new OfferDTO(), new OfferDTO()), PageRequest.of(0, 8), 2);
        when(offerService.searchOffers(any(NewBudgetDTO.class), any()))
                .thenReturn(page);

        NewBudgetDTO filter = new NewBudgetDTO(); // fill if needed

        mockMvc.perform(post(BASE + "/{budgetId}/offers:search", budgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.size").value(8))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("POST offers:search — 401 when organizer not found")
    void searchOffers_unauthorized() throws Exception {
        int budgetId = 999;
        when(organizerService.findByEmail(EMAIL)).thenReturn(null);

        mockMvc.perform(post(BASE + "/{budgetId}/offers:search", budgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewBudgetDTO())))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(eventService, offerService);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("POST offers:search — 400 when event already passed")
    void searchOffers_eventPassed_400() throws Exception {
        int budgetId = 999;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any())).thenReturn(true);

        mockMvc.perform(post(BASE + "/{budgetId}/offers:search", budgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewBudgetDTO())))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(offerService);
    }

    // ------- Planning constraints (as requested): category missing / price exceeds plan -------

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("POST offers:search — 400 when offer category is not in planned budget")
    void searchOffers_categoryNotPlanned_400() throws Exception {
        int budgetId = 100;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any())).thenReturn(false);

        when(offerService.searchOffers(any(NewBudgetDTO.class), any()))
                .thenThrow(new IllegalArgumentException("Selected offer category is not present in the planned budget"));

        mockMvc.perform(post(BASE + "/{budgetId}/offers:search", budgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewBudgetDTO())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = EMAIL, roles = {"ORGANIZER"})
    @DisplayName("POST offers:search — 400 when offer price exceeds the planned budget for the category")
    void searchOffers_priceExceedsPlanned_400() throws Exception {
        int budgetId = 100;
        when(organizerService.findByEmail(EMAIL)).thenReturn(organizer());
        when(eventService.checkIfEventHasPassed(eq(budgetId), any())).thenReturn(false);

        when(offerService.searchOffers(any(NewBudgetDTO.class), any()))
                .thenThrow(new IllegalArgumentException("Offer price exceeds the planned budget for the selected category"));

        mockMvc.perform(post(BASE + "/{budgetId}/offers:search", budgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewBudgetDTO())))
                .andExpect(status().isBadRequest());
    }

    // --------------------- Test Advice: map IllegalArgumentException -> 400 ---------------------

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
