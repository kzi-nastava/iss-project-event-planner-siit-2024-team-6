package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(locations = "classpath:application-test.properties")
@DataJpaTest
@ActiveProfiles("test")
public class EventRepositoryUT {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Test
    public void shouldFindTop5PublicEventsOrderedByDate() {
        List<Event> events = eventRepository.findTop5ByIsDeletedFalseAndIsPublicTrueOrderByDateDesc();
        assertThat(events).isNotEmpty();
        assertThat(events).hasSizeLessThanOrEqualTo(5);
        assertThat(events.get(0).getDate()).isAfterOrEqualTo(events.get(events.size() - 1).getDate());
    }

    @Test
    public void shouldFindEventsByUserId() {
        List<Event> events = eventRepository.findEventsByUserId(999999);
        assertThat(events).isNotEmpty();
    }

    @Test
    public void shouldFindFutureEventsByOrganizer() {
        Organizer organizer = organizerRepository.findById(999998).orElseThrow();
        List<Event> futureEvents = eventRepository.findFutureEventsByOrganizer(organizer, LocalDateTime.now());

        assertThat(futureEvents).isNotEmpty();
        assertThat(futureEvents).allMatch(e -> e.getDate().isAfter(LocalDateTime.now()));
    }

    @Test
    public void shouldFindEventsByOrganizer() {
        Organizer organizer = organizerRepository.findById(999998).orElseThrow();
        List<Event> events = eventRepository.findByOrganizer(organizer);

        assertThat(events).isNotEmpty();
    }

    @Test
    public void shouldFindPublicUpcomingEvents() {
        List<Event> events = eventRepository.findByIsDeletedFalseAndIsPublicTrueAndDateAfter(LocalDateTime.now());

        assertThat(events).isNotEmpty();
        assertThat(events).allMatch(e -> e.getIsPublic() && !e.getIsDeleted() && e.getDate().isAfter(LocalDateTime.now()));
    }

}
