package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.budget.BudgetDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.dto.category.CategorySuggestionDTO;
import ftn.siit.project.isspoject.dto.category.NewCategorySuggestionDTO;
import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.entity.OfferService;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers/")
public class ProviderController {
    @Autowired
    private ProviderService providerService;
    @Autowired
    private ftn.siit.project.isspoject.service.interfaces.OfferService offerService;
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private CategorySuggestionService categorySuggestionService;
    @Autowired
    private ServiceService serviceService;

    @GetMapping("{providerId}")
    public ResponseEntity<List<OfferDTO>> getAllOffers(@PathVariable int providerId) {
        //List<OfferDTO> dtos = offerService.find.stream().map(OfferDTO::new).toList();
        //return ResponseEntity.ok(dtos);
        return null;
    }

    @PostMapping("{providerId}")
    public ResponseEntity<OfferDTO> createOffer(@PathVariable int providerId, @RequestBody NewOfferDTO dto) {
        Provider provider = providerService.findById(providerId);
        //Offer saved = offerService.save(dto, provider);
        Offer saved = new Offer();
        return ResponseEntity.status(HttpStatus.CREATED).body(new OfferDTO(saved));
    }

    @PutMapping("{providerId}/{offerId}")
    public ResponseEntity<OfferDTO> updateOffer(@PathVariable int providerId, @PathVariable int offerId, @RequestBody NewOfferDTO dto) {
        Provider provider = providerService.findById(providerId);
        if(provider == null) {throw new NotFoundException("Provider not found."); }
        Offer oldOffer = offerService.findById(offerId);
        if(oldOffer == null) {throw new NotFoundException("Offer not found."); }
        //Offer updated = offerService.update(dto);
        providerService.update(provider);
        //return ResponseEntity.ok().body(new OfferDTO(updated));
        return ResponseEntity.ok(new OfferDTO(oldOffer));
    }

    @DeleteMapping("{offerId}")
    public ResponseEntity<Void> deleteOffer(@PathVariable int offerId) {
        Offer offer = offerService.findById(offerId);
        offerService.delete(offer);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{providerId}/filter")
    public ResponseEntity<List<OfferDTO>> getFilteredServices( @PathVariable int providerId, @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String eventType,
        @RequestParam(required = false) Double price,
        @RequestParam(required = false) Boolean isAvailable){
        Provider provider = providerService.findById(providerId);
        List<OfferService> filteredOfferServices = serviceService.getFilteredServices(provider, name, category, eventType, price, isAvailable);
        List<OfferDTO> dtos = filteredOfferServices.stream().map(OfferDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("{providerId}/search")
    public ResponseEntity<List<OfferDTO>> getFilteredServices( @PathVariable int providerId, @RequestParam(required = true) String name){
        Provider provider = providerService.findById(providerId);
        List<OfferService> filteredOfferServices = serviceService.getFilteredServices(provider, name, null, null, null, null);
        List<OfferDTO> dtos = filteredOfferServices.stream().map(OfferDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("{id}/budget")
    public ResponseEntity<BudgetDTO> getBudget(@PathVariable int id) {
        Budget budget = budgetService.findById(id);
        if(budget == null) throw  new NotFoundException("Budget not found");
        return ResponseEntity.ok(new BudgetDTO(budget));
    }

    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(@RequestBody NewBudgetDTO dto) {
        Budget budget = new Budget();
        if(budget == null){ throw new IllegalArgumentException("Budget is null"); }
        Budget created = budgetService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BudgetDTO(created));
    }

    @PutMapping("{id}")
    public ResponseEntity<BudgetDTO> updateBudget(@PathVariable int id, @RequestBody NewBudgetDTO dto) {
        Budget updatedBudget = budgetService.update(id, dto);
        return ResponseEntity.ok(new BudgetDTO(updatedBudget));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable int id) {
        budgetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("suggestion")
    public ResponseEntity<CategorySuggestionDTO> createCategorySuggestion(NewCategorySuggestionDTO dto) {
        CategorySuggestion created = categorySuggestionService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CategorySuggestionDTO(created));
    }
}
