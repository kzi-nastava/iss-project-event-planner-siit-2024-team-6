package ftn.siit.project.isspoject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.event.NewEventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.entity.Organizer;
//import ftn.siit.project.isspoject.entity.Role;
//import ftn.siit.project.isspoject.entity.User;
//import ftn.siit.project.isspoject.repository.OrganizerRepository;
//import ftn.siit.project.isspoject.repository.RoleRepository;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.repository.EventRepository;
import ftn.siit.project.isspoject.repository.UserRepository;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import ftn.siit.project.isspoject.service.interfaces.EventTypeService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import ftn.siit.project.isspoject.util.TokenUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class OrganizerControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;
    private NewEventDTO dto;
    private HttpHeaders headers;
    @Autowired
    private org.springframework.boot.web.client.RestTemplateBuilder restTemplateBuilder;


    @BeforeEach
    public void setUp() {
        this.restTemplate.getRestTemplate().setRequestFactory(
                new HttpComponentsClientHttpRequestFactory() // заменили!
        );
        dto = new NewEventDTO();
        dto.setName("My test event");
        dto.setDescription("Some description");
        dto.setMaxParticipants(100);
        dto.setIsPublic(true);
        dto.setPlace("Novi Sad");
        dto.setDate(LocalDateTime.now().plusDays(7));
        dto.setEventType("Conference");
        dto.setPhotos(new ArrayList<>());
        dto.setEmails(new ArrayList<>());

        String token = tokenUtils.generateToken(userService.findByEmail("organizer@test.com"));
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Auth-Token", "Bearer " + token);
    }

    @Test
    @DisplayName("POST /api/organizers/events should create event successfully using preloaded DB")
    public void testCreateEvent_Success() {
        HttpEntity<NewEventDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<EventDTO> response = restTemplate.exchange(
                "/api/organizers/events",
                HttpMethod.POST,
                request,
                EventDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("My test event", response.getBody().getName());
    }
    @Test
    @DisplayName("POST /api/organizers/events without token, 401 Unauthorized")
    public void testCreateEvent_Unauthorized_NoToken() {
        HttpHeaders noAuthHeaders = new HttpHeaders();
        noAuthHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<NewEventDTO> entity = new HttpEntity<>(dto, noAuthHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/organizers/events",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /api/organizers/events with invalid user (different role) 403 FORBIDDEN")
    public void testCreateEvent_UserNotFound() {
        String invalidToken = tokenUtils.generateToken(userService.findByEmail("admin@test.com"));

        HttpHeaders badUserHeaders = new HttpHeaders();
        badUserHeaders.setContentType(MediaType.APPLICATION_JSON);
        badUserHeaders.set("X-Auth-Token", "Bearer " + invalidToken);

        HttpEntity<NewEventDTO> request = new HttpEntity<>(dto, badUserHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/organizers/events",
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /api/organizers/events with invalid event-type. Expected 404 NOT FOUND")
    public void testCreateEvent_InvalidEventType() {
        dto.setEventType("NonexistentEventType");

        HttpEntity<NewEventDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/organizers/events",
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("POST to /api/organizers/events with the private event & invitations via email")
    public void testCreatePrivateEvent_WithInvitations() {
        dto.setIsPublic(false);
        dto.setEmails(List.of("user1@test.com", "user2@test.com"));

        HttpEntity<NewEventDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<EventDTO> response = restTemplate.exchange(
                "/api/organizers/events",
                HttpMethod.POST,
                request,
                EventDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("My test event", response.getBody().getName());
    }
    @Test
    @DisplayName("PUT /api/organizers/events/{id} - update existing event successfully")
    public void testUpdateEvent_Success() {
        Event event = eventService.findById(101);
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setName(event.getName());
        eventDTO.setDescription(event.getDescription());
        eventDTO.setMaxParticipants(event.getMaxParticipants());
        eventDTO.setParticipants(event.getParticipants());
        eventDTO.setIsPublic(event.getIsPublic());
        eventDTO.setPlace(event.getPlace());
        eventDTO.setDate(event.getDate());
        eventDTO.setIsDeleted(event.getIsDeleted());

        eventDTO.setDescription("Updated description 123");

        HttpEntity<EventDTO> updateRequest = new HttpEntity<>(eventDTO, headers);
        ResponseEntity<EventDTO> updateResponse = restTemplate.exchange(
                "/api/organizers/events/" + 101,
                HttpMethod.PUT,
                updateRequest,
                EventDTO.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("Updated description 123", updateResponse.getBody().getDescription());
    }

}

