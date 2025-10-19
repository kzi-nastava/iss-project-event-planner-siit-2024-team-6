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
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

        // Default behavior: findById returns our in-memory budget; save echoes input
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

    @Test
    @DisplayName("addItemToBudget: adds new category item and recalculates totals")
    void addItemToBudget_success() {
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
        verifyNoInteractions(categoryRepository); // should not hit repository after duplicate detected
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

    @Test
    @DisplayName("addItemToBudget: rejects non-positive price (<= 0)")
    void addItemToBudget_nonPositivePrice_throws() {
        IllegalArgumentException ex1 = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.addItemToBudget(BUDGET_ID, "STAGE", 0.0)
        );
        assertTrue(ex1.getMessage().toLowerCase().contains("greater than zero"));

        IllegalArgumentException ex2 = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.addItemToBudget(BUDGET_ID, "STAGE", -50.0)
        );
        assertTrue(ex2.getMessage().toLowerCase().contains("greater than zero"));
    }

    @Test
    @DisplayName("addItemToBudget: rejects null/blank category name")
    void addItemToBudget_blankCategory_throws() {
        IllegalArgumentException ex1 = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.addItemToBudget(BUDGET_ID, null, 100.0)
        );
        assertTrue(ex1.getMessage().toLowerCase().contains("category"));

        IllegalArgumentException ex2 = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.addItemToBudget(BUDGET_ID, "   ", 100.0)
        );
        assertTrue(ex2.getMessage().toLowerCase().contains("category"));
    }

    @Test
    @DisplayName("addItemToBudget: throws when budget with given id does not exist (pre-check in findById)")
    void addItemToBudget_budgetDoesNotExist_throws() {
        when(budgetRepository.findById(eq(BUDGET_ID))).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.addItemToBudget(BUDGET_ID, "VENUE", 123.0)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("budget not found"));
        verifyNoInteractions(categoryRepository);
        verify(budgetRepository, never()).save(any());
    }

    @Test
    @DisplayName("addItemToBudget: rejects null category with IllegalArgumentException")
    void addItemToBudget_nullCategory_throwsIAE() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.addItemToBudget(BUDGET_ID, null, 100.0)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("category"));
        // Should fail fast before touching category repo; no save should occur
        verifyNoInteractions(categoryRepository);
        verify(budgetRepository, never()).save(any());
    }

    @Test
    @DisplayName("addItemToBudget: rejects blank category with IllegalArgumentException")
    void addItemToBudget_blankCategory_throwsIAE() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.addItemToBudget(BUDGET_ID, "   ", 100.0)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("category"));
        verifyNoInteractions(categoryRepository);
        verify(budgetRepository, never()).save(any());
    }

    @Test
    @DisplayName("addItemToBudget: duplicate detection is case-insensitive")
    void addItemToBudget_duplicate_caseInsensitive_throws() {
        Category venue = cat("VeNuE");
        budget.getBudgetItems().add(item(42, venue, 0.0, 10.0));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.addItemToBudget(BUDGET_ID, "venue", 50.0)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("already"));
        verifyNoInteractions(categoryRepository);
    }

    @Test
    @DisplayName("addItemToBudget: NotFoundException if repository returns budget without the new item after save")
    void addItemToBudget_missingAfterSave_throws() {
        Category venue = cat("VENUE");
        when(categoryRepository.findByNameIgnoreCaseAndIsDeletedIsFalse("VENUE")).thenReturn(venue);

        // Return a different Budget instance with NO items (simulating a persistence anomaly)
        when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> {
            Budget incoming = inv.getArgument(0);
            Budget returned = new Budget();
            returned.setId(incoming.getId());
            returned.setBudgetItems(new ArrayList<>()); // <-- missing
            returned.setTotal(incoming.getTotal());
            returned.setAvailable(incoming.getAvailable());
            return returned;
        });

        assertThrows(
            ftn.siit.project.isspoject.exceptions.NotFoundException.class,
            () -> budgetService.addItemToBudget(BUDGET_ID, "VENUE", 500.0)
        );
    }

    @Test
    @DisplayName("addItemToBudget: throws during update() if budget disappears between read and write")
    void addItemToBudget_budgetMissingDuringUpdate_throws() {
        Category stage = cat("STAGE");
        when(categoryRepository.findByNameIgnoreCaseAndIsDeletedIsFalse("STAGE")).thenReturn(stage);

        // First findById for findById(budgetId) -> ok (from @BeforeEach)
        // Second findById in update(b): return empty to simulate disappeared budget
        when(budgetRepository.findById(eq(BUDGET_ID)))
            .thenReturn(Optional.of(budget))      // initial find
            .thenReturn(Optional.empty());         // update() presence check

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.addItemToBudget(BUDGET_ID, "STAGE", 200.0)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("budget not found"));
        // We attempted save? update() throws before save; ensure save not called
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    @DisplayName("updateBudgetItem: updates max price when valid")
    void updateBudgetItem_success() {
        Category c = cat("MUSIC");
        budget.getBudgetItems().add(item(7, c, 100.0, 150.0)); // curr <= price OK

        BudgetItem updated = budgetService.updateBudgetItem(BUDGET_ID, 7, 200.0);

        assertEquals(200.0, updated.getMaxPrice(), 1e-6);
        assertEquals(200.0, budget.getTotal(), 1e-6);
        assertEquals(100.0, budget.getAvailable(), 1e-6);
    }

    @Test
    @DisplayName("updateBudgetItem: rejects non-positive price")
    void updateBudgetItem_nonPositivePrice_throws() {
        IllegalArgumentException ex1 = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.updateBudgetItem(BUDGET_ID, 1, 0.0)
        );
        assertTrue(ex1.getMessage().toLowerCase().contains("greater than zero"));

        IllegalArgumentException ex2 = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.updateBudgetItem(BUDGET_ID, 1, -50.0)
        );
        assertTrue(ex2.getMessage().toLowerCase().contains("greater than zero"));
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
    @DisplayName("updateBudgetItem: new price equal to spent is allowed (>) check only")
    void updateBudgetItem_equalToSpent_success() {
        Category c = cat("MUSIC");
        budget.getBudgetItems().add(item(7, c, 150.0, 200.0));
        BudgetItem updated = budgetService.updateBudgetItem(BUDGET_ID, 7, 150.0);
        assertEquals(150.0, updated.getMaxPrice(), 1e-6);
        assertEquals(150.0, budget.getTotal(), 1e-6);
        assertEquals(0.0, budget.getAvailable(), 1e-6);
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

    @Test
    @DisplayName("updateBudgetItem: NotFoundException if saved budget does not contain updated item")
    void updateBudgetItem_missingAfterSave_throws() {
        Category c = cat("MUSIC");
        budget.getBudgetItems().add(item(7, c, 10.0, 50.0));

        // Save returns a Budget without any items
        when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> {
            Budget src = inv.getArgument(0);
            Budget returned = new Budget();
            returned.setId(src.getId());
            returned.setBudgetItems(new ArrayList<>());
            returned.setTotal(src.getTotal());
            returned.setAvailable(src.getAvailable());
            return returned;
        });

        assertThrows(
            ftn.siit.project.isspoject.exceptions.NotFoundException.class,
            () -> budgetService.updateBudgetItem(BUDGET_ID, 7, 60.0)
        );
    }

    @Test
    @DisplayName("updateBudgetItem: only targeted item changes; others preserved")
    void updateBudgetItem_onlyOneChanges_totalsReflect() {
        Category c1 = cat("C1");
        Category c2 = cat("C2");
        budget.getBudgetItems().add(item(1, c1, 20.0, 100.0));
        budget.getBudgetItems().add(item(2, c2, 30.0, 60.0));

        BudgetItem updated = budgetService.updateBudgetItem(BUDGET_ID, 2, 90.0);

        assertEquals(90.0, updated.getMaxPrice(), 1e-6);
        // c1 unchanged
        BudgetItem still = budget.getBudgetItems().stream().filter(bi -> bi.getId()==1).findFirst().orElseThrow();
        assertEquals(100.0, still.getMaxPrice(), 1e-6);
        assertEquals(20.0, still.getCurrPrice(), 1e-6);

        // totals: 100 + 90 = 190; spent: 20 + 30 = 50; available: 140
        assertEquals(190.0, budget.getTotal(), 1e-6);
        assertEquals(140.0, budget.getAvailable(), 1e-6);
    }

    @Test
    @DisplayName("removeBudgetItem: removes item when nothing is purchased under it")
    void removeBudgetItem_success() {
        Category c = cat("DECOR");
        budget.getBudgetItems().add(item(5, c, 0.0, 300.0));

        budgetService.removeBudgetItem(BUDGET_ID, 5);

        assertTrue(budget.getBudgetItems().stream().noneMatch(bi -> bi.getId() == 5));
        assertEquals(0.0, budget.getTotal(), 1e-6);
        assertEquals(0.0, budget.getAvailable(), 1e-6);
        verify(budgetRepository, atLeastOnce()).save(budget);
    }

    @Test
    @DisplayName("removeBudgetItem: fails if something has been already purchased (currPrice > 0)")
    void removeBudgetItem_purchased_throws() {
        Category c = cat("DECOR");
        budget.getBudgetItems().add(item(5, c, 10.0, 300.0));

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
    @DisplayName("removeBudgetItem: does not call save() when item id not in budget")
    void removeBudgetItem_notFound_noSave() {
        int initialSize = budget.getBudgetItems().size();
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.removeBudgetItem(BUDGET_ID, 321)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
        assertEquals(initialSize, budget.getBudgetItems().size());
        // The default lenient save behaviour shouldn't be triggered
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    @DisplayName("removeBudgetItem: recomputes totals correctly when removing one of multiple items")
    void removeBudgetItem_recomputesTotals_amongMany() {
        Category c1 = cat("A");
        Category c2 = cat("B");
        budget.getBudgetItems().add(item(1, c1, 0.0, 100.0)); // avail +100
        budget.getBudgetItems().add(item(2, c2, 10.0, 50.0));  // avail +40

        budgetService.removeBudgetItem(BUDGET_ID, 1);

        assertTrue(budget.getBudgetItems().stream().noneMatch(bi -> bi.getId() == 1));
        // Only item 2 remains: total=50, spent=10 -> available=40
        assertEquals(50.0, budget.getTotal(), 1e-6);
        assertEquals(40.0, budget.getAvailable(), 1e-6);
        verify(budgetRepository, atLeastOnce()).save(budget);
    }

    @Test
    @DisplayName("totals: recomputed across multiple items after updates")
    void totals_recompute_multipleItems() {
        Category c1 = cat("C1");
        Category c2 = cat("C2");
        budget.getBudgetItems().add(item(1, c1, 20.0, 100.0)); // available = 80
        budget.getBudgetItems().add(item(2, c2, 10.0, 50.0));  // available = 40

        budgetService.updateBudgetItem(BUDGET_ID, 2, 70.0);

        assertEquals(170.0, budget.getTotal(), 1e-6);
        assertEquals(140.0, budget.getAvailable(), 1e-6); // (100-20)+(70-10)=80+60
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


    @Test
    @DisplayName("addNewItem: increases currPrice on existing item within max limit")
    void addNewItem_existingWithinLimit_success() {
        Category c = cat("PHOTO");
        BudgetItem existing = item(20, c, 40.0, 100.0);
        budget.getBudgetItems().add(existing);

        Budget updatedBudget = budgetService.addNewItem(c, 50.0, BUDGET_ID);

        assertEquals(90.0, existing.getCurrPrice(), 1e-6);
        assertEquals(100.0, updatedBudget.getTotal(), 1e-6);
        assertEquals(10.0, updatedBudget.getAvailable(), 1e-6);
    }

    @Test
    @DisplayName("addNewItem: exactly fills remaining budget succeeds (== max)")
    void addNewItem_equalToMax_success() {
        Category c = cat("PHOTO");
        budget.getBudgetItems().add(item(20, c, 40.0, 100.0)); // remaining = 60
        Budget updated = budgetService.addNewItem(c, 60.0, BUDGET_ID);
        BudgetItem bi = updated.getBudgetItems().stream().filter(x -> x.getCategory()==c).findFirst().orElseThrow();
        assertEquals(100.0, bi.getCurrPrice(), 1e-6);
        assertEquals(100.0, updated.getTotal(), 1e-6);
        assertEquals(0.0, updated.getAvailable(), 1e-6);
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
            .filter(bi -> bi.getCategory() == c) // match same instance for this test
            .findFirst()
            .orElseThrow();

        assertEquals(75.0, created.getCurrPrice(), 1e-6);
        assertEquals(0.0, created.getMaxPrice(), 1e-6);
        assertEquals(0.0, updated.getTotal(), 1e-6);      // sum of max
        assertEquals(-75.0, updated.getAvailable(), 1e-6);// total - spent
    }

    @Test
    @DisplayName("addNewItem: unlimited max (0) allows any spend")
    void addNewItem_unlimitedMax_allowsLargeSpend() {
        Category c = cat("UNLIMITED");
        budget.getBudgetItems().add(item(33, c, 10.0, 0.0)); // max=0 => unlimited

        Budget updated = budgetService.addNewItem(c, 10_000.0, BUDGET_ID);

        BudgetItem bi = updated.getBudgetItems().stream()
            .filter(x -> x.getCategory() == c)
            .findFirst()
            .orElseThrow();

        assertEquals(10_010.0, bi.getCurrPrice(), 1e-6);
        // total is sum of max (0), available = total - spent
        assertEquals(0.0, updated.getTotal(), 1e-6);
        assertEquals(-10_010.0, updated.getAvailable(), 1e-6);
    }

    @Test
    @DisplayName("addNewItem: negative spend is currently allowed (available becomes more negative)")
    void addNewItem_negativeSpend_currentBehavior() {
        Category c = cat("NEG");
        budget.getBudgetItems().add(item(100, c, 0.0, 0.0)); // unlimited

        Budget updated = budgetService.addNewItem(c, -25.0, BUDGET_ID);

        BudgetItem bi = updated.getBudgetItems().stream().filter(x -> x.getCategory()==c).findFirst().orElseThrow();
        assertEquals(-25.0, bi.getCurrPrice(), 1e-6);
        // total sum of max is 0; available = total - spent = 0 - (-25) = +25
        assertEquals(0.0, updated.getTotal(), 1e-6);
        assertEquals(25.0, updated.getAvailable(), 1e-6);
    }


}
