package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.budget.BudgetItemDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetItemDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.repository.BudgetRepository;
import ftn.siit.project.isspoject.repository.EventRepository;
import ftn.siit.project.isspoject.repository.CategoryRepository;
import ftn.siit.project.isspoject.repository.EventTypeRepository;
import ftn.siit.project.isspoject.service.interfaces.BudgetService;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import ftn.siit.project.isspoject.service.interfaces.OrganizerService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import ftn.siit.project.isspoject.util.TokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz",
        "jwt.expiresIn=86400"
})
@Transactional
class OrganizerBudgetEndpointsIT {

    @LocalServerPort
    int port;

    private String baseUrl;

    private static final String EMAIL = "organizer.it@example.com";

    // Real beans
    @Autowired
    private TestRestTemplate rest;
    @org.springframework.beans.factory.annotation.Autowired private TokenUtils tokenUtils;

    @org.springframework.beans.factory.annotation.Autowired private UserService userService;
    @org.springframework.beans.factory.annotation.Autowired private OrganizerService organizerService;
    @org.springframework.beans.factory.annotation.Autowired private EventService eventService;
    @org.springframework.beans.factory.annotation.Autowired private BudgetService budgetService;

    @org.springframework.beans.factory.annotation.Autowired private EventRepository eventRepository;
    @org.springframework.beans.factory.annotation.Autowired private BudgetRepository budgetRepository;
    @org.springframework.beans.factory.annotation.Autowired private CategoryRepository categoryRepository;
    @Autowired
    private EventTypeRepository eventTypeRepository;

    // Seeded objects per test
    private Organizer organizer;
    private Event event;
    private Budget budget;
    private Category venueCategory;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/organizers";

        // 1) Seed Organizer (Organizer extends/implements your User type in this project)
        organizer = new Organizer();
        organizer.setName("IT");                 // first/given name if that's what 'name' is
        organizer.setLastname("Organizer");      // <-- REQUIRED (was missing)
        organizer.setEmail(EMAIL);
        organizer.setPassword("test");           // <-- if @Column(nullable=false)
        organizer.setUserType("ORGANIZER");

        // 2) Seed Budget
        budget = new Budget();
        budget.setAvailable(0.0);
        budget.setTotal(0.0);
        budget = budgetService.save(budget);

        // 3) Seed Event (date set later per test)
        event = new Event();
        event.setName("IT Event");
        event.setDescription("Budget endpoints IT");
        event.setPlace("Test Place");
        event.setMaxParticipants(100);
        event.setParticipants(0);
        event.setIsPublic(true);
        event.setIsDeleted(false);
        event.setBudget(budget);
        event.setEventActivities(new java.util.ArrayList<>());
        event = eventService.save(event);

        EventType t = new EventType(1, "DEFAULT_TYPE", "Default Type", false);
        eventTypeRepository.save(t);
        event.setEventType(t);
        organizer.getMyEvents().add(event);
        userService.save(organizer); // persist relation

        // 4) Seed Category "VENUE" (not deleted)
        venueCategory = categoryRepository.findByNameIgnoreCaseAndIsDeletedIsFalse("VENUE");
        if (venueCategory == null) {
            venueCategory = new Category();
            venueCategory.setName("VENUE");
            venueCategory.setIsDeleted(false);
            venueCategory = categoryRepository.save(venueCategory);
        }
    }

    private HttpHeaders authHeadersFor(Organizer org) {
        // Build a real JWT with subject=email (adjust if your TokenUtils expects a different principal)
        String token = tokenUtils.generateToken(org);

        // Sanity check: ensure parse → email works; if this fails you'll get 401 in endpoints
        String extracted = tokenUtils.getUsernameFromToken(token);
        assertEquals(org.getEmail(), extracted, "Token subject/email mismatch.");

        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setBearerAuth(token); // Authorization: Bearer <token>
        return h;
    }

    private void setEventDate(LocalDateTime when) {
        event.setDate(when);
        event = eventService.save(event);
        // ensure relation persists
        organizer = organizerService.findByEmail(EMAIL);
        assertTrue(eventRepository.findByOrganizer(organizer).stream().anyMatch(e -> e.getId().equals(event.getId())),
                "Seeded event must be retrievable by findByOrganizer(organizer)");
    }

    private int addItemViaApi(HttpHeaders headers, String categoryName, double maxPrice, double currPrice) {
        NewBudgetItemDTO body = new NewBudgetItemDTO();
        body.setCategory(categoryName);
        body.setMaxPrice(maxPrice);
        body.setCurrPrice(currPrice);

        ResponseEntity<BudgetItemDTO> resp = rest.exchange(
                baseUrl + "/budget/{budgetId}/items",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                BudgetItemDTO.class,
                budget.getId()
        );

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals(categoryName, resp.getBody().getCategory());
        return resp.getBody().getId();
    }

    // ---------------------------
    // Tests
    // ---------------------------

    @Nested
    @DisplayName("POST /api/organizers/budget/{budgetId}/items")
    class AddItem {

        @Test
        @DisplayName("200 OK — adds item when authorized and event not passed")
        void addItem_ok() {
            setEventDate(LocalDateTime.now().plusDays(2)); // future → not passed
            HttpHeaders headers = authHeadersFor(organizer);

            NewBudgetItemDTO body = new NewBudgetItemDTO();
            body.setCategory("VENUE");
            body.setMaxPrice(500.0);
            body.setCurrPrice(0.0);

            ResponseEntity<BudgetItemDTO> resp = rest.exchange(
                    baseUrl + "/budget/{budgetId}/items",
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    BudgetItemDTO.class,
                    budget.getId()
            );

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            assertNotNull(resp.getBody());
            assertEquals("VENUE", resp.getBody().getCategory());
            assertEquals(500.0, resp.getBody().getMaxPrice(), 1e-6);

            // Verify totals persisted by real service
            Budget reloaded = budgetRepository.findById(budget.getId()).orElseThrow();
            assertEquals(500.0, reloaded.getTotal(), 1e-6);
            assertEquals(500.0, reloaded.getAvailable(), 1e-6);
        }

        @Test
        @DisplayName("401 UNAUTHORIZED — when token missing")
        void addItem_unauthorized() {
            setEventDate(LocalDateTime.now().plusDays(1)); // future; but we won't send token
            NewBudgetItemDTO body = new NewBudgetItemDTO();
            body.setCategory("VENUE");
            body.setMaxPrice(500.0);

            ResponseEntity<String> resp = rest.exchange(
                    baseUrl + "/budget/{budgetId}/items",
                    HttpMethod.POST,
                    new HttpEntity<>(body, new HttpHeaders()),
                    String.class,
                    budget.getId()
            );

            assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        }

        @Test
        @DisplayName("400 BAD REQUEST — when event has already passed")
        void addItem_eventPassed() {
            setEventDate(LocalDateTime.now().minusDays(1)); // past → passed
            HttpHeaders headers = authHeadersFor(organizer);

            NewBudgetItemDTO body = new NewBudgetItemDTO();
            body.setCategory("VENUE");
            body.setMaxPrice(500.0);

            ResponseEntity<String> resp = rest.exchange(
                    baseUrl + "/budget/{budgetId}/items",
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    String.class,
                    budget.getId()
            );

            assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        }
    }

    @Nested
    @DisplayName("PUT /api/organizers/budget/{budgetId}/items/{itemId}")
    class UpdateItem {

        @Test
        @DisplayName("200 OK — updates item price when authorized and event not passed")
        void updateItem_ok() {
            setEventDate(LocalDateTime.now().plusDays(3));
            HttpHeaders headers = authHeadersFor(organizer);

            // First create an item so we have a real ID to update
            int itemId = addItemViaApi(headers, "VENUE", 200.0, 50.0);

            // Update price to 300.0
            Double newPrice = 300.0;

            ResponseEntity<BudgetItemDTO> resp = rest.exchange(
                    baseUrl + "/budget/{budgetId}/items/{itemId}",
                    HttpMethod.PUT,
                    new HttpEntity<>(newPrice, headers),
                    BudgetItemDTO.class,
                    budget.getId(),
                    itemId
            );

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            assertNotNull(resp.getBody());
            assertEquals(300.0, resp.getBody().getMaxPrice(), 1e-6);
            assertEquals("VENUE", resp.getBody().getCategory());

            Budget reloaded = budgetRepository.findById(budget.getId()).orElseThrow();
            assertEquals(300.0, reloaded.getTotal(), 1e-6);
            assertEquals(250.0, reloaded.getAvailable(), 1e-6); // 300 max - 50 spent
        }

        @Test
        @DisplayName("401 UNAUTHORIZED — when token missing")
        void updateItem_unauthorized() {
            setEventDate(LocalDateTime.now().plusDays(1));
            // create item with auth so it exists
            HttpHeaders headers = authHeadersFor(organizer);
            int itemId = addItemViaApi(headers, "VENUE", 120.0, 20.0);

            ResponseEntity<String> resp = rest.exchange(
                    baseUrl + "/budget/{budgetId}/items/{itemId}",
                    HttpMethod.PUT,
                    new HttpEntity<>(300.0, new HttpHeaders()),
                    String.class,
                    budget.getId(),
                    itemId
            );

            assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        }

        @Test
        @DisplayName("400 BAD REQUEST — when event has already passed")
        void updateItem_eventPassed() {
            setEventDate(LocalDateTime.now().minusHours(2)); // passed
            HttpHeaders headers = authHeadersFor(organizer);

            // Even if item does not exist, controller should short-circuit on "passed"
            ResponseEntity<String> resp = rest.exchange(
                    baseUrl + "/budget/{budgetId}/items/{itemId}",
                    HttpMethod.PUT,
                    new HttpEntity<>(300.0, headers),
                    String.class,
                    budget.getId(),
                    99999 // arbitrary; service won't be invoked
            );

            assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        }
    }

    @Nested
    @DisplayName("DELETE /api/organizers/budget/{budgetId}/items/{itemId}")
    class DeleteItem {

        @Test
        @DisplayName("200 OK — deletes item when authorized and event not passed")
        void delete_ok() {
            setEventDate(LocalDateTime.now().plusDays(1));
            HttpHeaders headers = authHeadersFor(organizer);

            int itemId = addItemViaApi(headers, "VENUE", 150.0, 0.0);

            ResponseEntity<Void> resp = rest.exchange(
                    baseUrl + "/budget/{budgetId}/items/{itemId}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(null, headers),
                    Void.class,
                    budget.getId(),
                    itemId
            );

            assertEquals(HttpStatus.OK, resp.getStatusCode());

            // Ensure it is removed
            Budget reloaded = budgetRepository.findById(budget.getId()).orElseThrow();
            List<BudgetItem> left = reloaded.getBudgetItems();
            boolean stillThere = left != null && left.stream().anyMatch(bi -> bi.getId() == itemId);
            assertFalse(stillThere, "Budget item should be deleted");
        }

        @Test
        @DisplayName("401 UNAUTHORIZED — when token missing")
        void delete_unauthorized() {
            setEventDate(LocalDateTime.now().plusDays(1));
            // create item with auth to ensure existing
            HttpHeaders headers = authHeadersFor(organizer);
            int itemId = addItemViaApi(headers, "VENUE", 100.0, 0.0);

            ResponseEntity<String> resp = rest.exchange(
                    baseUrl + "/budget/{budgetId}/items/{itemId}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(null, new HttpHeaders()),
                    String.class,
                    budget.getId(),
                    itemId
            );

            assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        }

        @Test
        @DisplayName("400 BAD REQUEST — when event has already passed")
        void delete_eventPassed() {
            setEventDate(LocalDateTime.now().minusDays(1)); // passed
            HttpHeaders headers = authHeadersFor(organizer);

            ResponseEntity<String> resp = rest.exchange(
                    baseUrl + "/budget/{budgetId}/items/{itemId}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(null, headers),
                    String.class,
                    budget.getId(),
                    12345 // arbitrary; service won’t be called
            );

            assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        }
    }

    // ---------------------------------------------------------------------
    // Test-only exception mapper so IllegalArgumentException -> 400 BAD_REQUEST
    // Does NOT affect production (active only in "test" profile).
    // ---------------------------------------------------------------------
    @RestControllerAdvice
    @Profile("test")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    static class TestExceptionHandler {
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<java.util.Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", ex.getMessage()));
        }
    }
}
