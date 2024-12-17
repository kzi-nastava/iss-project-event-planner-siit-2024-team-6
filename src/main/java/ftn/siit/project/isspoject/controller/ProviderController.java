package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.budget.BudgetDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.dto.category.CategorySuggestionDTO;
import ftn.siit.project.isspoject.dto.category.NewCategorySuggestionDTO;
import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.event.EventTypeDTO;
import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.dto.pagination.PagedResponse;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.entity.Service;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.interfaces.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
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
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private EventTypeService eventTypeService;

    @GetMapping("{providerId}")
    public ResponseEntity<List<OfferDTO>> getAllServices(@PathVariable int providerId) {
        Provider provider = providerService.findById(providerId);
        List<OfferDTO> dtos = serviceService.findByProvider(provider).stream().map(OfferDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("{providerId}/my-services")
    public ResponseEntity<PagedResponse<OfferDTO>> getAllPageServices(@PathVariable int providerId, Pageable page) {
        Provider provider = providerService.findById(providerId);
        Page<Service> services = serviceService.findByProvider(provider, page);

        List<OfferDTO> dtos = services.stream()
                .map(service -> {
                    try {
                        return new OfferDTO(service);
                    } catch (Exception e) {
                        System.err.println("Error converting service to DTO: " + e.getMessage());
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .toList();

        PagedResponse<OfferDTO> response = new PagedResponse<>(
                dtos,
                services.getTotalPages(),
                services.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }


    @PostMapping("{providerId}")
    public ResponseEntity<OfferDTO> createOffer(@PathVariable int providerId, @RequestBody NewOfferDTO dto) {
        Provider provider = providerService.findById(providerId);
        List<EventType> eventTypes = new ArrayList<>();
        for (EventTypeDTO eventType : dto.getEventTypes()) {
            eventTypes.add(eventTypeService.findByName(eventType.getName()));
        }
        Offer saved;
        if (dto.getCategorySuggestion() == null) {
            saved = serviceService.save(dto, provider, eventTypes, categoryService.findByName(dto.getCategory()));
        } else {
            saved = serviceService.save(dto, provider, eventTypes, null);
        }
        if (dto.getCategorySuggestion() != null) {
            CategorySuggestion categorySuggestion = new CategorySuggestion(dto.getCategorySuggestion().getSuggestion(), Status.PENDING, saved);
            categorySuggestionService.save(categorySuggestion);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new OfferDTO(saved));
    }

    @PutMapping("{providerId}/{offerId}")
    public ResponseEntity<OfferDTO> updateOffer(@PathVariable int providerId, @PathVariable int offerId, @RequestBody NewOfferDTO dto) {
        Provider provider = providerService.findById(providerId);
        List<EventType> eventTypes = new ArrayList<>();
        for (EventTypeDTO eventType : dto.getEventTypes()) {
            eventTypes.add(eventTypeService.findByName(eventType.getName()));
        }
        Offer updated = serviceService.update(offerId, dto, eventTypes);
        return ResponseEntity.ok(new OfferDTO(updated));
    }

    @DeleteMapping("{offerId}")
    public ResponseEntity<Void> deleteOffer(@PathVariable int offerId) {
        Offer offer = offerService.findById(offerId);
        offerService.delete(offer);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{providerId}/services-filter")
    public ResponseEntity<PagedResponse<OfferDTO>> getFilteredServices(@PathVariable int providerId, @RequestParam(required = false) String name,
                                                              @RequestParam(required = false) List<String> category,
                                                              @RequestParam(required = false) List<String> eventType,
                                                              @RequestParam(required = false) Double price,
                                                              @RequestParam(required = false) Boolean isAvailable, Pageable pageable) {
        Provider provider = providerService.findById(providerId);
        Page<Service> filteredOfferServices = serviceService.getFilteredServices(provider, name, category, eventType, price, isAvailable, pageable);
        List<OfferDTO> dtos = filteredOfferServices.stream()
                .map(service -> {
                    try {
                        return new OfferDTO(service);
                    } catch (Exception e) {
                        System.err.println("Error converting service to DTO: " + e.getMessage());
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .toList();

        PagedResponse<OfferDTO> response = new PagedResponse<>(
                dtos,
                filteredOfferServices.getTotalPages(),
                filteredOfferServices.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("{providerId}/search")
    public ResponseEntity<PagedResponse<OfferDTO>> getFilteredServices(@PathVariable int providerId, @RequestParam() String name, Pageable page) {
        Provider provider = providerService.findById(providerId);
        if (name != null && name.isEmpty()) {
            name = null; // Treat empty strings as null
        }
        Page<Service> filteredOfferServices = serviceService.searchByName(provider, name, page);
        List<OfferDTO> dtos = filteredOfferServices.stream()
                .map(service -> {
                    try {
                        return new OfferDTO(service);
                    } catch (Exception e) {
                        System.err.println("Error converting service to DTO: " + e.getMessage());
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .toList();

        PagedResponse<OfferDTO> response = new PagedResponse<>(
                dtos,
                filteredOfferServices.getTotalPages(),
                filteredOfferServices.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}/budget")
    public ResponseEntity<BudgetDTO> getBudget(@PathVariable int id) {
        Budget budget = budgetService.findById(id);
        if (budget == null) throw new NotFoundException("Budget not found");
        return ResponseEntity.ok(new BudgetDTO(budget));
    }

    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(@RequestBody NewBudgetDTO dto) {
        Budget budget = new Budget();
        if (budget == null) {
            throw new IllegalArgumentException("Budget is null");
        }
        Budget created = budgetService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BudgetDTO(created));
    }

    @PutMapping("{id}")
    public ResponseEntity<BudgetDTO> updateBudget(@PathVariable int id, @RequestBody NewBudgetDTO dto) {
        Budget updatedBudget = budgetService.update(id, dto);
        return ResponseEntity.ok(new BudgetDTO(updatedBudget));
    }

    @DeleteMapping("budget/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable int id) {
        budgetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = categoryService.findAllNames();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("event-types")
    public ResponseEntity<List<String>> getAllEventTypes() {
        List<String> eventTypes = eventTypeService.findAllNames();
        if (eventTypes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(eventTypes);
    }
}
