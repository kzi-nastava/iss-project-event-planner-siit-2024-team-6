package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.repository.OrganizerRepository;
import ftn.siit.project.isspoject.service.implementations.OrganizerServiceImpl;
import ftn.siit.project.isspoject.service.interfaces.OrganizerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class OrganizerServiceUT {
    @Autowired
    private OrganizerService organizerService;

    @MockBean
    private OrganizerRepository organizerRepository;

    @Test
    @DisplayName("Should find organizer by email")
    public void shouldFindByEmail() {
        Organizer organizer = new Organizer();
        organizer.setEmail("organizer@test.com");

        Mockito.when(organizerRepository.findOrganizerByEmailAndIsActiveIsTrue("organizer@test.com"))
                .thenReturn(organizer);

        Organizer result = organizerService.findByEmail("organizer@test.com");

        assertNotNull(result);
        assertEquals("organizer@test.com", result.getEmail());
    }
}
