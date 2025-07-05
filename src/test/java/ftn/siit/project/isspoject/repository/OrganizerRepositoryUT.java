package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.entity.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(locations = "classpath:application-test.properties")
@DataJpaTest
@ActiveProfiles("test")
public class OrganizerRepositoryUT {
    @Autowired
    private OrganizerRepository organizerRepository;

    @Test
    public void shouldFindOrganizerByEmailAndIsActiveTrue() {
        Organizer result = organizerRepository.findOrganizerByEmailAndIsActiveIsTrue("testorg@dummy.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("testorg@dummy.com");
        assertThat(result.getIsActive()).isTrue();
    }

    @Test
    public void shouldFindOrganizerByEventId() {
        Organizer result = organizerRepository.findOrganizerByEventId(101);

        assertThat(result).isNotNull();
        assertThat(result.getMyEvents()).anyMatch(e -> e.getId().equals(101));
    }
}
