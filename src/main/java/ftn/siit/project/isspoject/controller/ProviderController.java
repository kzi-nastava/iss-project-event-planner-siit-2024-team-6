package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.budget.BudgetDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
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
    private OfferService offerService;
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
    @Autowired
    private ProductService productService;
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
    @GetMapping("my-products")
    public ResponseEntity<PagedResponse<OfferDTO>> getAllPageProducts(Pageable page, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Provider p = providerService.findByEmail(email);
        System.out.println("provider " + p.getName());
        Page<Product> products = productService.findByProvider(p, page);


        List<OfferDTO> dtos = products.stream()
                .map(product -> {
                    try {
                        return new OfferDTO(product);
                    } catch (Exception e) {
                        System.err.println("Error converting service to DTO: " + e.getMessage());
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .toList();

        PagedResponse<OfferDTO> response = new PagedResponse<>(
                dtos,
                products.getTotalPages(),
                products.getTotalElements()
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
    @PostMapping("product")
    public ResponseEntity<OfferDTO> createProduct(@RequestBody NewOfferDTO dto, HttpServletRequest request) {
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
        Category category = null;
        if (dto.getCategorySuggestion() == null) {
            category = categoryService.findByName(dto.getCategory());
        }
        Product saved = new Product(dto, category, eventTypes, provider);
        offerService.save(saved);
        if (dto.getCategorySuggestion() != null){
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
    @PutMapping("{offerId}/product")
    public ResponseEntity<OfferDTO> updateProduct(@PathVariable int offerId, @RequestBody NewOfferDTO dto, HttpServletRequest request) {
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
        Offer updated = productService.update(offerId, dto, eventTypes);
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
