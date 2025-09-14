package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetItemDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Status;
import ftn.siit.project.isspoject.repository.CategoryRepository;
import ftn.siit.project.isspoject.repository.OfferRepository;
import ftn.siit.project.isspoject.service.implementations.OfferServiceImpl;
import ftn.siit.project.isspoject.service.interfaces.OfferHistoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OfferServiceSearchOffersBudgetUT {

    @InjectMocks private OfferServiceImpl service;

    @Mock private OfferRepository offerRepository;
    @Mock private CategoryRepository categoryRepository;     // not used here but required by @InjectMocks
    @Mock private OfferHistoryService offerHistoryService;   // not used here but required by @InjectMocks

    private Category cat(String name) {
        Category c = new Category();
        c.setName(name);
        return c;
    }

    private Offer offer(int id, String categoryName, double price, double sale,
                        boolean visible, boolean deleted, Status status) {
        Offer o = new Offer();
        o.setId(id);
        o.setCategory(cat(categoryName));
        o.setPrice(price);
        o.setSale(sale);             // IMPORTANT: never null in these tests (method does sale != 0.0)
        o.setIsVisible(visible);
        o.setIsDeleted(deleted);
        o.setStatus(status);
        // other fields of no importance for these tests
        return o;
    }

    private NewBudgetItemDTO budgetItem(String category, double curr, double max) {
        NewBudgetItemDTO bi = new NewBudgetItemDTO();
        bi.setCategory(category);
        bi.setCurrPrice(curr);
        bi.setMaxPrice(max);
        return bi;
    }

    @Test
    @DisplayName("No budget items -> returns base offers (accepted, visible, not deleted)")
    void noBudgetItems_returnsBase() {
        Offer included  = offer(1, "VENUE", 300, 0.0, true,  false, Status.ACCEPTED);
        Offer deleted   = offer(2, "VENUE", 200, 0.0, true,  true,  Status.ACCEPTED);
        Offer invisible = offer(3, "VENUE", 200, 0.0, false, false, Status.ACCEPTED);
        Offer pending   = offer(4, "VENUE", 200, 0.0, true,  false, Status.PENDING);

        when(offerRepository.findAll()).thenReturn(List.of(included, deleted, invisible, pending));

        NewBudgetDTO dto = new NewBudgetDTO();
        Pageable pageable = PageRequest.of(0, 10);

        Page<OfferDTO> page = service.searchOffers(dto, pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        //the one expected is present
        assertEquals(included.getId(), page.getContent().get(0).getId());
        verify(offerRepository).findAll();
    }

    @Test
    @DisplayName("Respects remaining budget per category; categories not present (or negative remaining) are unlimited")
    void respectsRemainingAndUnlimited() {
        // Budget: VENUE remaining = 400 (500 - 100), DECOR remaining = -50 (unlimited by code), MUSIC absent (unlimited)
        NewBudgetDTO dto = new NewBudgetDTO();
        dto.setBudgetItems(new ArrayList<>(List.of(
                budgetItem("VENUE", 100, 500),
                budgetItem("DECOR", 200, 150)  // negative remaining => unlimited
        )));

        Offer venueOk        = offer(1, "VENUE", 350, 0.0, true, false, Status.ACCEPTED);       // <= 400 -> include
        Offer venueSaleOk    = offer(2, "VENUE", 500, 300.0, true, false, Status.ACCEPTED);     // sale 300 <= 400 -> include
        Offer venueTooHigh   = offer(3, "VENUE", 450, 0.0, true, false, Status.ACCEPTED);       // > 400 and sale=0 -> exclude
        Offer musicUnlimited = offer(4, "MUSIC", 9999, 0.0, true, false, Status.ACCEPTED);      // category not in map => include
        Offer decorUnlimited = offer(5, "DECOR", 8888, 0.0, true, false, Status.ACCEPTED);      // negative remaining => include

        when(offerRepository.findAll()).thenReturn(List.of(
                venueOk, venueSaleOk, venueTooHigh, musicUnlimited, decorUnlimited
        ));

        Page<OfferDTO> page = service.searchOffers(dto, PageRequest.of(0, 10));

        assertEquals(4, page.getTotalElements());
        assertEquals(4, page.getContent().size());
        verify(offerRepository).findAll();
    }

    @Test
    @DisplayName("Price/Sale edge: price==0 makes it MAX_VALUE; only sale can let it pass")
    void zeroPriceEdge() {
        // remaining for VENUE: 200
        NewBudgetDTO dto = new NewBudgetDTO();
        dto.setBudgetItems(List.of(budgetItem("VENUE", 0, 200)));

        Offer priceZeroSaleZero   = offer(1, "VENUE", 0.0, 0.0, true, false, Status.ACCEPTED);
        Offer priceZeroSaleSmall  = offer(2, "VENUE", 0.0, 150.0, true, false, Status.ACCEPTED);
        Offer priceSmallSaleZero  = offer(3, "VENUE", 150.0, 0.0, true, false, Status.ACCEPTED); 

        when(offerRepository.findAll()).thenReturn(List.of(priceZeroSaleZero, priceZeroSaleSmall, priceSmallSaleZero));

        Page<OfferDTO> page = service.searchOffers(dto, PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements()); // offers #2 and #3
        assertEquals(2, page.getContent().size());
        verify(offerRepository).findAll();
    }

    @Test
    @DisplayName("Pagination: returns requested page size and correct total")
    void paginationWorks() {
        NewBudgetDTO dto = new NewBudgetDTO();

        Offer a = offer(1, "VENUE", 100, 0.0, true, false, Status.ACCEPTED);
        Offer b = offer(2, "VENUE", 150, 0.0, true, false, Status.ACCEPTED);
        Offer c = offer(3, "VENUE", 200, 0.0, true, false, Status.ACCEPTED);

        when(offerRepository.findAll()).thenReturn(List.of(a, b, c));

        Page<OfferDTO> page0 = service.searchOffers(dto, PageRequest.of(0, 2));
        assertEquals(3, page0.getTotalElements());
        assertEquals(2, page0.getContent().size());

        Page<OfferDTO> page1 = service.searchOffers(dto, PageRequest.of(1, 2));
        assertEquals(3, page1.getTotalElements());
        assertEquals(1, page1.getContent().size());

        verify(offerRepository, times(2)).findAll();
    }
}
