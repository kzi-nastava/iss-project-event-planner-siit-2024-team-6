package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.EventRepository;
import ftn.siit.project.isspoject.service.implementations.EventServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceCheckHasPassedUT {

    @InjectMocks private EventServiceImpl service;

    @Mock private EventRepository eventRepository;

    private Organizer org(int id, String name) {
        Organizer o = new Organizer();
        o.setId(id);
        o.setName(name);
        return o;
    }

    private Event eventWithBudget(int budgetId, LocalDateTime dateTime) {
        Budget b = new Budget();
        b.setId(budgetId);

        Event e = new Event();
        e.setBudget(b);
        e.setDate(dateTime);
        e.setIsDeleted(false);
        return e;
    }

    @Test
    @DisplayName("false when matching event date is in the future")
    void future_returnsFalse() {
        Organizer organizer = org(1, "Org");

        int targetBudgetId = 101;
        Event other  = eventWithBudget(999, LocalDateTime.now().minusDays(1));
        Event future = eventWithBudget(targetBudgetId, LocalDateTime.now().plusDays(2));

        when(eventRepository.findByOrganizer(organizer))
                .thenReturn(List.of(other, future));

        Boolean result = service.checkIfEventHasPassed(targetBudgetId, organizer);

        assertFalse(result);
        verify(eventRepository).findByOrganizer(organizer);
    }

    @Test
    @DisplayName("true when matching event date is in the past")
    void past_returnsTrue() {
        Organizer organizer = org(2, "Org2");

        int targetBudgetId = 202;
        Event past = eventWithBudget(targetBudgetId, LocalDateTime.now().minusHours(3));

        when(eventRepository.findByOrganizer(organizer))
                .thenReturn(List.of(past));

        Boolean result = service.checkIfEventHasPassed(targetBudgetId, organizer);

        assertTrue(result);
        verify(eventRepository).findByOrganizer(organizer);
    }

    @Test
    @DisplayName("throws NotFoundException when no event matches budgetId")
    void notFound_throws() {
        Organizer organizer = org(3, "Org3");

        Event e1 = eventWithBudget(11, LocalDateTime.now().plusDays(1));
        Event e2 = eventWithBudget(22, LocalDateTime.now().minusDays(1));

        when(eventRepository.findByOrganizer(organizer))
                .thenReturn(List.of(e1, e2));

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.checkIfEventHasPassed(303, organizer)
        );
        assertTrue(ex.getMessage().contains("303"));
        verify(eventRepository).findByOrganizer(organizer);
    }
}
