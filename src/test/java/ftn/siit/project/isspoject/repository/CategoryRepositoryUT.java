package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryUT {

    @Autowired
    private CategoryRepository repository;

    private Category cat(String name, boolean deleted) {
        Category c = new Category();
        c.setName(name);
        c.setIsDeleted(deleted);
        return c;
    }

    @Test
    @DisplayName("findByNameIgnoreCaseAndIsDeletedIsFalse: finds by name (case-insensitive), ignores deleted")
    void findByName_caseInsensitive_excludesDeleted() {
        repository.save(cat("Venue", false));
        repository.save(cat("DeletedOne", true));

        Category found = repository.findByNameIgnoreCaseAndIsDeletedIsFalse("VENUE");
        assertNotNull(found);
        assertEquals("Venue", found.getName());

        Category shouldBeNull = repository.findByNameIgnoreCaseAndIsDeletedIsFalse("DeletedOne");
        assertNull(shouldBeNull, "Obrisane kategorije ne smeju biti vraćene");
    }

    @Test
    @DisplayName("findAllNames: returns only the names of non-deleted categories; empty when there is no data")
    void findAllNames_filtersDeleted_andEmptyOK() {
        List<String> empty = repository.findAllNames();
        assertNotNull(empty);
        assertTrue(empty.isEmpty());

        repository.save(cat("Music", false));
        repository.save(cat("Decor", false));
        repository.save(cat("Old", true));

        List<String> names = repository.findAllNames();
        assertEquals(2, names.size());
        assertTrue(names.containsAll(List.of("Music", "Decor")));
        assertFalse(names.contains("Old"));
    }

    @Test
    @DisplayName("findAllByIsDeletedIsFalse: pagination and exclusion of deleted categories")
    void findAllByIsDeletedIsFalse_pagination() {
        repository.save(cat("A", false));
        repository.save(cat("B", false));
        repository.save(cat("C", true)); // treba da bude isključen

        PageRequest page0 = PageRequest.of(0, 2);
        Page<Category> page = repository.findAllByIsDeletedIsFalse(page0);

        assertEquals(2, page.getTotalElements());       // A,B
        assertEquals(2, page.getContent().size());
        assertTrue(page.getContent().stream().allMatch(c -> !Boolean.TRUE.equals(c.getIsDeleted())));
    }
}
