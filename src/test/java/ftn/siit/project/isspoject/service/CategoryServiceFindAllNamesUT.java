package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.repository.CategoryRepository;
import ftn.siit.project.isspoject.service.implementations.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceFindAllNamesUT {

    @InjectMocks
    private CategoryServiceImpl service;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("returns list of names from repository")
    void findAllNames_returnsRepoResult() {
        List<String> names = List.of("Electronics", "Books", "Travel");
        when(categoryRepository.findAllNames()).thenReturn(names);

        List<String> result = service.findAllNames();

        assertEquals(names, result);
        verify(categoryRepository, times(1)).findAllNames();
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("returns empty list when repository returns empty")
    void findAllNames_emptyOK() {
        when(categoryRepository.findAllNames()).thenReturn(List.of());

        List<String> result = service.findAllNames();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository).findAllNames();
        verifyNoMoreInteractions(categoryRepository);
    }
}
