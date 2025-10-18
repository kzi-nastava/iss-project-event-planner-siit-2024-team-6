package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.budget.BudgetDTO;
import ftn.siit.project.isspoject.dto.budget.BudgetItemDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetItemDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.entity.BudgetItem;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.service.interfaces.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets/")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;
    @Autowired
    private OrganizerService organizerService;
    @Autowired
    private EventService eventService;
    @Autowired
    private OfferService offerService;

    @PostMapping("{budgetId}/items")
    public ResponseEntity<BudgetItemDTO> addItemToBudget(@PathVariable int budgetId, @RequestBody NewBudgetItemDTO budgetDTO, @AuthenticationPrincipal(expression = "username") String email) {
        Organizer o = organizerService.findByEmail(email);
        if (o == null) {
             return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(eventService.checkIfEventHasPassed(budgetId, o)){
            throw new IllegalArgumentException("Event has passed. Cannot access its budget.");
        }
        BudgetItem bi = budgetService.addItemToBudget(budgetId, budgetDTO.getCategory(), budgetDTO.getMaxPrice());
        return ResponseEntity.ok(new BudgetItemDTO(bi));
    }

    @PutMapping("{budgetId}/items/{itemId}")
    public ResponseEntity<BudgetItemDTO> updateBudgetItem(@PathVariable int budgetId, @PathVariable int itemId, @RequestBody double price, @AuthenticationPrincipal(expression = "username") String email) {
        Organizer o = organizerService.findByEmail(email);
        if (o == null) {
             return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(eventService.checkIfEventHasPassed(budgetId, o)){
            throw new IllegalArgumentException("Event has passed. Cannot access its budget.");
        }
        BudgetItem bi = budgetService.updateBudgetItem(budgetId, itemId, price);
        return ResponseEntity.ok(new BudgetItemDTO(bi));
    }

    @DeleteMapping("{budgetId}/items/{itemId}")
    public ResponseEntity<Void> deleteBudgetItem(@PathVariable int budgetId, @PathVariable int itemId, @AuthenticationPrincipal(expression = "username") String email) {
        Organizer o = organizerService.findByEmail(email);
        if (o == null) {
             return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(eventService.checkIfEventHasPassed(budgetId, o)){
            throw new IllegalArgumentException("Event has passed. Cannot access its budget.");
        }
        budgetService.removeBudgetItem(budgetId, itemId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("{budgetId}/offers:search")
    public ResponseEntity<Page<OfferDTO>> searchOffers(@PathVariable int budgetId, @RequestBody NewBudgetDTO budgetDTO,  @AuthenticationPrincipal(expression = "username") String email, @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "8") int pageSize){

        Organizer o = organizerService.findByEmail(email);
        if (o == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(eventService.checkIfEventHasPassed(budgetId, o)){
            throw new IllegalArgumentException("Cannot filter offers by budget of an event that has passed.");
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<OfferDTO> filteredOffers = offerService.searchOffers(budgetDTO, pageable);

        System.out.println("Filtered Offers: " + filteredOffers);

        return ResponseEntity.ok(filteredOffers);
    }
}
