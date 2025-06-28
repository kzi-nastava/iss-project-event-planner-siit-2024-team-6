package ftn.siit.project.isspoject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrganizerControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private EventRepository eventRepository;

    private String token;
    @BeforeAll
    void setup() {
        Organizer organizer = new Organizer();
        organizer.setEmail("test@test.com");
        organizer.setPassword("123");
        organizer.setName("Test");
        organizer.setLastname("Organizer");
        organizer.setMyEvents(new ArrayList<>());
        userService.save(organizer);

//        if (eventTypeService.findByName("Seminar") == null) {
            EventType type = new EventType();
            type.setName("Seminar");
            eventTypeService.save(type);
//        }

        token = "Bearer " + tokenUtils.generateToken(organizer);
    }

    @Test
    void testCreateEvent_success() throws Exception {
        NewEventDTO dto = new NewEventDTO();
        dto.setName("Test Event");
        dto.setDescription("Test description");
        dto.setMaxParticipants(100);
        dto.setIsPublic(true);
        dto.setPlace("Novi Sad");
        dto.setDate(LocalDateTime.now().plusDays(5));
        dto.setEventType("Seminar");
        dto.setPhotos(new ArrayList<>());
        dto.setEmails(new ArrayList<>());

        mockMvc.perform(post("/api/organizers/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Auth-Token", token)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Event"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.place").value("Novi Sad"));
    }
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private OrganizerRepository organizerRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private TokenUtils tokenUtils;
//
//    private String validToken;
//    private Organizer testOrganizer;
//
//    @BeforeEach
//    void setUp() {
//        // Очистить БД перед тестом
//        userRepository.deleteAll();
//        organizerRepository.deleteAll();
//
//        // Создать роль ORGANIZER, если её нет
//        Role organizerRole = roleRepository.findByName("ORGANIZER");
//        if (organizerRole == null) {
//            organizerRole = new Role("ORGANIZER");
//            roleRepository.save(organizerRole);
//        }
//
//        // Создать тестового организатора
//        testOrganizer = new Organizer();
//        testOrganizer.setEmail("organizer@test.com");
//        testOrganizer.setPassword(passwordEncoder.encode("password123"));
//        testOrganizer.setName("Test");
//        testOrganizer.setLastname("Organizer");
//        testOrganizer.setRoles(Collections.singletonList(organizerRole));
//        testOrganizer.setActive(true);
//
//        organizerRepository.save(testOrganizer);
//
//        // Сгенерировать JWT токен
//        validToken = tokenUtils.generateToken(testOrganizer.getEmail(), testOrganizer.getRoles());
//    }
//
//    @Test
//    void createEvent_shouldReturnCreatedEvent() throws Exception {
//        // Создать NewEventDTO
//        NewEventDTO newEventDTO = new NewEventDTO();
//        newEventDTO.setName("Integration Test Event");
//        newEventDTO.setDescription("This is a test event.");
//        newEventDTO.setMaxParticipants(100);
//        newEventDTO.setIsPublic(true);
//        newEventDTO.setPlace("Belgrade");
//        newEventDTO.setDate(LocalDateTime.now().plusDays(7)); // Событие через неделю
//        newEventDTO.setEventType("Birthday");
//
//        // Выполнить POST запрос
//        mockMvc.perform(post("/api/organizers/events")
//                        .header("Authorization", "Bearer " + validToken)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newEventDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.name").value("Integration Test Event"))
//                .andExpect(jsonPath("$.description").value("This is a test event."))
//                .andExpect(jsonPath("$.maxParticipants").value(100))
//                .andExpect(jsonPath("$.isPublic").value(true))
//                .andExpect(jsonPath("$.place").value("Belgrade"));
//    }
//
//    @Test
//    void createEvent_withoutToken_shouldReturn401() throws Exception {
//        NewEventDTO newEventDTO = new NewEventDTO("No Auth Event", "Desc", 50, true, "Novi Sad", LocalDateTime.now().plusDays(1), "Wedding", null);
//
//        mockMvc.perform(post("/api/organizers/events")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newEventDTO)))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void createEvent_withEmptyName_shouldReturn4xx() throws Exception {
//        NewEventDTO newEventDTO = new NewEventDTO("", "Empty name", 50, true, "Novi Sad", LocalDateTime.now().plusDays(1), "Conference", null);
//
//        mockMvc.perform(post("/api/organizers/events")
//                        .header("Authorization", "Bearer " + validToken)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newEventDTO)))
//                .andExpect(status().is4xxClientError());
//    }
}
