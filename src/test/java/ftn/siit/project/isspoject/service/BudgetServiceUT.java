package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.entity.BudgetItem;
import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.repository.BudgetRepository;
import ftn.siit.project.isspoject.repository.CategoryRepository;
import ftn.siit.project.isspoject.service.implementations.BudgetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceUT {

    @InjectMocks
    private BudgetServiceImpl budgetService;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private static final int BUDGET_ID = 1;

    private Budget budget;

    @BeforeEach
    void setUp() {
        budget = new Budget();
        budget.setId(BUDGET_ID);
        budget.setBudgetItems(new ArrayList<>());
        budget.setAvailable(0.0);
        budget.setTotal(0.0);

        lenient().when(budgetRepository.findById(anyInt()))
           .thenReturn(Optional.of(budget));
        lenient().when(budgetRepository.save(any(Budget.class)))
           .thenAnswer(inv -> inv.getArgument(0));
    }

    private Category cat(String name) {
        Category c = new Category();
        c.setName(name);
        return c;
    }

    private BudgetItem item(int id, Category c, double curr, double max) {
        BudgetItem bi = new BudgetItem();
        bi.setId(id);
        bi.setCategory(c);
        bi.setCurrPrice(curr);
        bi.setMaxPrice(max);
        return bi;
    }

    // add new item to budget
    @Test
    @DisplayName("addItemToBudget: adds new category item and recalculate totals")
    void addItemToBudget_success() {
        // create new category
        Category venue = cat("VENUE");
        when(categoryRepository.findByNameIgnoreCaseAndIsDeletedIsFalse(eq("VENUE")))
                .thenReturn(venue);

        BudgetItem created = budgetService.addItemToBudget(BUDGET_ID, "VENUE", 500.0);

        assertNotNull(created);
        assertEquals("VENUE", created.getCategory().getName());
        assertEquals(500.0, created.getMaxPrice(), 1e-6);
        assertEquals(0.0, created.getCurrPrice(), 1e-6);

        // budget totals updated
        assertEquals(500.0, budget.getTotal(), 1e-6);
        assertEquals(500.0, budget.getAvailable(), 1e-6);

        verify(budgetRepository, atLeastOnce()).save(budget);
    }

    @Test
    @DisplayName("addItemToBudget: fails if category already exists in budget")
    void addItemToBudget_categoryExists_throws() {
        Category venue = cat("VENUE");
        budget.getBudgetItems().add(item(10, venue, 0.0, 100.0));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> budgetService.addItemToBudget(BUDGET_ID, "VENUE", 500.0)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("already"));
        verifyNoInteractions(categoryRepository); // repo shouldn’t be called once duplicate found
    }

    @Test
    @DisplayName("addItemToBudget: fails if category not found in master repo")
    void addItemToBudget_categoryNotFound_throws() {
        when(categoryRepository.findByNameIgnoreCaseAndIsDeletedIsFalse(eq("FOOD")))
                .thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> budgetService.addItemToBudget(BUDGET_ID, "FOOD", 120.0)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }

    // update budget item

    @Test
    @DisplayName("updateBudgetItem: updates max price when valid")
    void updateBudgetItem_success() {
        Category c = cat("MUSIC");
        budget.getBudgetItems().add(item(7, c, 100.0, 150.0)); // curr<=price OK

        BudgetItem updated = budgetService.updateBudgetItem(BUDGET_ID, 7, 200.0);

        assertEquals(200.0, updated.getMaxPrice(), 1e-6);
        // totals recomputed: only one item [curr=100, max=200]
        assertEquals(200.0, budget.getTotal(), 1e-6);
        assertEquals(100.0, budget.getAvailable(), 1e-6);
    }

    @Test
    @DisplayName("updateBudgetItem: rejects non-positive price")
    void updateBudgetItem_nonPositivePrice_throws() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> budgetService.updateBudgetItem(BUDGET_ID, 1, 0.0)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("greater than zero"));
    }

    @Test
    @DisplayName("updateBudgetItem: rejects when spent > new price")
    void updateBudgetItem_spentGreaterThanPrice_throws() {
        Category c = cat("FOOD");
        budget.getBudgetItems().add(item(11, c, 80.0, 120.0));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> budgetService.updateBudgetItem(BUDGET_ID, 11, 50.0)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("spent"));
    }

    @Test
    @DisplayName("updateBudgetItem: rejects when itemId not in budget")
    void updateBudgetItem_itemNotFound_throws() {
        Category c = cat("FOOD");
        budget.getBudgetItems().add(item(11, c, 10.0, 20.0));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> budgetService.updateBudgetItem(BUDGET_ID, 999, 50.0)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }

    // removing an item of a budget

    @Test
    @DisplayName("removeBudgetItem: removes item when not purchased")
    void removeBudgetItem_success() {
        Category c = cat("DECOR");
        budget.getBudgetItems().add(item(5, c, 0.0, 300.0));

        budgetService.removeBudgetItem(BUDGET_ID, 5);

        assertTrue(budget.getBudgetItems().stream().noneMatch(bi -> bi.getId() == 5));
        // totals recomputed (list now empty)
        assertEquals(0.0, budget.getTotal(), 1e-6);
        assertEquals(0.0, budget.getAvailable(), 1e-6);
        verify(budgetRepository, atLeastOnce()).save(budget);
    }

    @Test
    @DisplayName("removeBudgetItem: fails if already purchased (currPrice > 0)")
    void removeBudgetItem_purchased_throws() {
        Category c = cat("DECOR");
        budget.getBudgetItems().add(item(5, c, 10.0, 300.0)); // purchased

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> budgetService.removeBudgetItem(BUDGET_ID, 5)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("cannot be deleted"));
    }

    @Test
    @DisplayName("removeBudgetItem: fails if item not found")
    void removeBudgetItem_notFound_throws() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> budgetService.removeBudgetItem(BUDGET_ID, 123)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }


    @Test
    @DisplayName("addNewItem: increases currPrice on existing item within max limit")
    void addNewItem_existingWithinLimit_success() {
        Category c = cat("PHOTO");
        BudgetItem existing = item(20, c, 40.0, 100.0);
        budget.getBudgetItems().add(existing);

        Budget updatedBudget = budgetService.addNewItem(c, 50.0, BUDGET_ID);

        // existing modified in place
        assertEquals(90.0, existing.getCurrPrice(), 1e-6);
        // totals recomputed
        assertEquals(100.0, updatedBudget.getTotal(), 1e-6);
        assertEquals(10.0, updatedBudget.getAvailable(), 1e-6);
    }

    @Test
    @DisplayName("addNewItem: rejects if new spend exceeds max (and max != 0)")
    void addNewItem_insufficientFunds_throws() {
        Category c = cat("PHOTO");
        budget.getBudgetItems().add(item(20, c, 90.0, 100.0));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> budgetService.addNewItem(c, 20.0, BUDGET_ID) // 90 + 20 > 100
        );
        assertTrue(ex.getMessage().toLowerCase().contains("enough allocated funds"));
    }

    @Test
    @DisplayName("addNewItem: creates new item with max=0 when category not present")
    void addNewItem_createsNewItem_success() {
        Category c = cat("LIGHTS"); // not present in budget yet

        Budget updated = budgetService.addNewItem(c, 75.0, BUDGET_ID);

        BudgetItem created = updated.getBudgetItems().stream()
                .filter(bi -> bi.getCategory() == c) // SAME instance match by reference
                .findFirst()
                .orElseThrow();

        assertEquals(75.0, created.getCurrPrice(), 1e-6);
        assertEquals(0.0, created.getMaxPrice(), 1e-6);
        assertEquals(0.0, updated.getTotal(), 1e-6);      // sum of max
        assertEquals(-75.0, updated.getAvailable(), 1e-6);// total - spent
    }

    @Test
    @DisplayName("removeItem: removes first matching category and updates")
    void removeItem_success() {
        Category c = cat("SOUND");
        budget.getBudgetItems().add(item(1, c, 0.0, 200.0));
        budget.getBudgetItems().add(item(2, cat("OTHER"), 0.0, 50.0));

        Budget updated = budgetService.removeItem(BUDGET_ID, c);

        assertTrue(updated.getBudgetItems().stream().noneMatch(bi -> bi.getCategory() == c));
        assertEquals(50.0, updated.getTotal(), 1e-6);
        assertEquals(50.0, updated.getAvailable(), 1e-6);
    }

    @Test
    @DisplayName("removeItem: no-op when category not present (returns same budget)")
    void removeItem_categoryNotPresent_noop() {
        budget.getBudgetItems().add(item(2, cat("OTHER"), 0.0, 50.0));
        budget.setAvailable(50.0);
        budget.setTotal(50.0);

        Budget updated = budgetService.removeItem(BUDGET_ID, cat("NOPE"));

        assertEquals(1, updated.getBudgetItems().size());
        assertEquals(50.0, updated.getTotal(), 1e-6);
        assertEquals(50.0, updated.getAvailable(), 1e-6);
    }


    @Test
    @DisplayName("save: delegates to repository")
    void save_delegates() {
        Budget incoming = new Budget();
        incoming.setId(99);
        when(budgetRepository.save(incoming)).thenReturn(incoming);

        Budget saved = budgetService.save(incoming);
        assertSame(incoming, saved);
        verify(budgetRepository).save(incoming);
    }



    @Test
    @DisplayName("update path: throws if budget missing before save (consistency check)")
    void update_missingBudget_throws() {
        Budget detached = new Budget();
        detached.setId(404);
        detached.setBudgetItems(new ArrayList<>());

        when(budgetRepository.findById(eq(404))).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> budgetService.removeBudgetItem(404, 1)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }
}
