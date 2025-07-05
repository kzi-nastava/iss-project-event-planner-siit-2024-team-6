package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.dto.activity.NewActivityDTO;
import ftn.siit.project.isspoject.entity.Activity;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.ActivityRepository;
import ftn.siit.project.isspoject.service.interfaces.ActivityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ActivityServiceUT {

    @Autowired
    private ActivityService activityService;

    @MockBean
    private ActivityRepository activityRepository;

    @Test
    @DisplayName("Should find activity by ID")
    public void shouldFindById() {
        Activity activity = new Activity();
        activity.setId(101);
        activity.setName("Workshop");

        when(activityRepository.findById(101)).thenReturn(Optional.of(activity));

        Activity result = activityService.findById(101);

        assertNotNull(result);
        assertEquals("Workshop", result.getName());
        verify(activityRepository, times(1)).findById(101);
    }

    @Test
    @DisplayName("Should throw NotFoundException if activity not found")
    public void shouldThrowIfActivityNotFound() {
        when(activityRepository.findById(404)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> activityService.findById(404));
        assertEquals("Activity not found with ID: 404", ex.getMessage());
    }

    @Test
    @DisplayName("Should save new activity")
    public void shouldSaveActivity() {
        Activity activity = new Activity();
        activity.setName("Hackathon");

        when(activityRepository.save(activity)).thenReturn(activity);

        Activity result = activityService.save(activity);

        assertNotNull(result);
        assertEquals("Hackathon", result.getName());
        verify(activityRepository).save(activity);
    }

    @Test
    @DisplayName("Should throw exception when saving null activity")
    public void shouldThrowWhenSavingNullActivity() {
        assertThrows(IllegalArgumentException.class, () -> activityService.save(null));
    }

    @Test
    @DisplayName("Should delete existing activity")
    public void shouldDeleteActivity() {
        Activity activity = new Activity();
        activity.setId(202);

        when(activityRepository.existsById(202)).thenReturn(true);

        activityService.delete(activity);

        verify(activityRepository).delete(activity);
    }

    @Test
    @DisplayName("Should throw when deleting null or non-existing activity")
    public void shouldThrowWhenDeletingInvalidActivity() {
        Activity nullActivity = null;
        assertThrows(NotFoundException.class, () -> activityService.delete(nullActivity));

        Activity notFound = new Activity();
        notFound.setId(404);
        when(activityRepository.existsById(404)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> activityService.delete(notFound));
    }

    @Test
    @DisplayName("Should update activity successfully")
    public void shouldUpdateActivity() {
        Activity activity = new Activity();
        activity.setId(303);

        NewActivityDTO dto = new NewActivityDTO();
        dto.setName("Updated Name");
        dto.setDescription("Updated Description");
        dto.setLocation("New Hall");
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now().plusHours(2));

        when(activityRepository.save(activity)).thenReturn(activity);

        Activity updated = activityService.update(activity, dto);

        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
        assertEquals("New Hall", updated.getLocation());
        verify(activityRepository).save(activity);
    }
}
