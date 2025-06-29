package ftn.siit.project.isspoject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.event.NewEventDTO;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.entity.Organizer;
//import ftn.siit.project.isspoject.entity.Role;
//import ftn.siit.project.isspoject.entity.User;
//import ftn.siit.project.isspoject.repository.OrganizerRepository;
//import ftn.siit.project.isspoject.repository.RoleRepository;
import ftn.siit.project.isspoject.repository.EventRepository;
import ftn.siit.project.isspoject.repository.UserRepository;
import ftn.siit.project.isspoject.service.interfaces.EventTypeService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import ftn.siit.project.isspoject.util.TokenUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    @DisplayName("POST /api/organizers/events should create event successfully using preloaded DB")
    public void testCreateEvent() {
        NewEventDTO dto = new NewEventDTO();
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Auth-Token", "Bearer " + token);

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
}
