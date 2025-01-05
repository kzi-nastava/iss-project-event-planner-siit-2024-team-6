package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.budget.BudgetDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.dto.category.CategorySuggestionDTO;
import ftn.siit.project.isspoject.dto.category.NewCategorySuggestionDTO;
import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.event.EventTypeDTO;
import ftn.siit.project.isspoject.dto.event.NewEventTypeDTO;
import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.dto.pagination.PagedResponse;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.entity.Service;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.interfaces.*;
import ftn.siit.project.isspoject.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
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
    @Autowired
    private TokenUtils tokenUtils;

    @GetMapping("{providerId}")
    public ResponseEntity<List<OfferDTO>> getAllServices(@PathVariable int providerId) {
        Provider provider = providerService.findById(providerId);
        List<OfferDTO> dtos = serviceService.findByProvider(provider).stream().map(OfferDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("my-services")
    public ResponseEntity<PagedResponse<OfferDTO>> getAllPageServices(Pageable page, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Provider p = providerService.findByEmail(email);
        System.out.println("provider " + p.getName());
        Page<Service> services = serviceService.findByProvider(p, page);


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

    @GetMapping("{name}/category")
    public ResponseEntity<Category> findByName(@PathVariable String name, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Category category = categoryService.findByName(name);
        if (category == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(category);
    }


    @PostMapping()
    public ResponseEntity<OfferDTO> createService(@RequestBody NewOfferDTO dto, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Provider provider = providerService.findByEmail(email);
        List<EventType> eventTypes = new ArrayList<>();
        for (NewEventTypeDTO eventType : dto.getEventTypes()) {
            eventTypes.add(eventTypeService.findByName(eventType.getName()));
        }
        Offer saved;
        if (dto.getCategorySuggestion() == null) {
            saved = serviceService.save(dto, provider, eventTypes, categoryService.findByName(dto.getCategory()), Status.ACCEPTED);
        } else {
            saved = serviceService.save(dto, provider, eventTypes, null, Status.PENDING);
            categorySuggestionService.save(new CategorySuggestion(dto.getCategorySuggestion(), Status.PENDING, saved));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new OfferDTO(saved));
    }

    @PutMapping("{offerId}")
    public ResponseEntity<OfferDTO> updateService(@PathVariable int offerId, @RequestBody NewOfferDTO dto, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        providerService.findByEmail(email);
        List<EventType> eventTypes = new ArrayList<>();
        for (NewEventTypeDTO eventType : dto.getEventTypes()) {
            eventTypes.add(eventTypeService.findByName(eventType.getName()));
        }
        Offer updated = serviceService.update(offerId, dto, eventTypes);
        return ResponseEntity.ok(new OfferDTO(updated));
    }

    @DeleteMapping("{offerId}")
    public ResponseEntity<Void> deleteOffer(@PathVariable int offerId, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        providerService.findByEmail(email);
        Offer offer = offerService.findById(offerId);
        offerService.delete(offer);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{providerId}/services-filter")
    public ResponseEntity<PagedResponse<OfferDTO>> getFilteredServices(@PathVariable int providerId,
                                                                       @RequestParam(required = false) List<String> categories,
                                                                       @RequestParam(required = false) List<String> eventTypes,
                                                                       @RequestParam(required = false) Double price,
                                                                       @RequestParam(required = false) Boolean isAvailable, Pageable pageable) {
        System.out.println(" Recieved Categories: " + categories);
        System.out.println("Event Types: " + eventTypes);
        Provider provider = providerService.findById(providerId);
        Page<Service> filteredOfferServices = serviceService.getFilteredServices(provider, categories, eventTypes, price, isAvailable, pageable);
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

    @GetMapping("search")
    public ResponseEntity<PagedResponse<OfferDTO>> getFilteredServices(@RequestParam() String name, Pageable page, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Provider provider = providerService.findByEmail(email);
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

    @PostMapping("budget")
    public ResponseEntity<BudgetDTO> createBudget(@RequestBody NewBudgetDTO dto) {
        Budget budget = new Budget();
        if (budget == null) {
            throw new IllegalArgumentException("Budget is null");
        }
        Budget created = budgetService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BudgetDTO(created));
    }

    @PutMapping("budget/{id}")
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
    public ResponseEntity<List<String>> getAllCategories(HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
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
