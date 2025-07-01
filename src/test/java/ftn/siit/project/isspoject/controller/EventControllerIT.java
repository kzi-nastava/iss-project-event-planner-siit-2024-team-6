package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.event.NewEventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import ftn.siit.project.isspoject.util.TokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class EventControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;
    private HttpHeaders headers;
    @Autowired
    private org.springframework.boot.web.client.RestTemplateBuilder restTemplateBuilder;
    @BeforeEach
    public void setUp() {
        this.restTemplate.getRestTemplate().setRequestFactory(
                new HttpComponentsClientHttpRequestFactory()
        );

        String token = tokenUtils.generateToken(userService.findByEmail("organizer@test.com"));
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Auth-Token", "Bearer " + token);
    }

    @Test
    @DisplayName("GET /api/events/{id} succeed")
    public void getEvent_Success(){
        Event event = eventService.findById(101);

        ResponseEntity<EventDTO> response = restTemplate.exchange(
                "/api/events/" + event.getId(),
                HttpMethod.GET,
                new HttpEntity<>(null),
                EventDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(event.getName(), response.getBody().getName());

    }
    @Test
    @DisplayName("GET /api/events/{id} not found")
    public void getEvent_NotFound(){
        ResponseEntity<EventDTO> response = restTemplate.exchange(
                "/api/events/" + 500, // No event with this name
                HttpMethod.GET,
                new HttpEntity<>(null),
                EventDTO.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }
}
