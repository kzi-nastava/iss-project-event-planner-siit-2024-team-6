package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.EventRepository;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class EventServiceUT {
    @Autowired
    private EventService eventService;

    @MockBean
    private EventRepository eventRepository;

    @Test
    @DisplayName("Should save event successfully")
    public void shouldSaveEvent() {
        Event event = new Event();
        event.setId(1);

        Mockito.when(eventRepository.save(event)).thenReturn(event);

        Event saved = eventService.save(event);

        assertNotNull(saved);
        assertEquals(1, saved.getId());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    @DisplayName("Should throw exception when saving null event")
    public void shouldThrowExceptionOnNullEvent() {
        assertThrows(IllegalArgumentException.class, () -> eventService.save(null));
    }

    @Test
    @DisplayName("Should find event by ID successfully")
    public void shouldFindEventById() {
        Event event = new Event();
        event.setId(123);

        Mockito.when(eventRepository.findById(123)).thenReturn(Optional.of(event));

        Event found = eventService.findById(123);
        assertNotNull(found);
        assertEquals(123, found.getId());
    }

    @Test
    @DisplayName("Should throw exception if event not found by ID")
    public void shouldFailIfEventNotFound() {
        Mockito.when(eventRepository.findById(999)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> eventService.findById(999));
        assertEquals("Event not found with ID: 999", ex.getMessage());
    }

    @Test
    @DisplayName("Should soft-delete existing event")
    public void shouldSoftDeleteEvent() {
        Event event = new Event();
        event.setId(101);
        event.setIsDeleted(false);

        Mockito.when(eventRepository.existsById(101)).thenReturn(true);
        Mockito.when(eventRepository.save(event)).thenReturn(event);

        eventService.delete(event);

        assertTrue(event.getIsDeleted());
        verify(eventRepository).save(event);
    }

    @Test
    @DisplayName("Should throw NotFoundException if deleting null")
    public void shouldThrowExceptionIfDeletingNull() {
        assertThrows(NotFoundException.class, () -> eventService.delete(null));
    }

    @Test
    @DisplayName("Should throw NotFoundException if deleting non-existing")
    public void shouldThrowExceptionIfDeletingNonExisting() {
        Event event = new Event();
        event.setId(404);
        event.setIsDeleted(false);

        Mockito.when(eventRepository.existsById(404)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> eventService.delete(event));
    }
}
