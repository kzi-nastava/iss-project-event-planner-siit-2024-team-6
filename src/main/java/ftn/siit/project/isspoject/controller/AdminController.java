package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.event.EventTypeDTO;
import ftn.siit.project.isspoject.dto.user.UserDTO;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.interfaces.EventTypeService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import ftn.siit.project.isspoject.service.external.PDFGeneratorService;
import ftn.siit.project.isspoject.dto.category.CategorySuggestionDTO;
import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.entity.Report;
import ftn.siit.project.isspoject.service.interfaces.CategoryService;
import ftn.siit.project.isspoject.service.interfaces.OfferService;
import ftn.siit.project.isspoject.service.interfaces.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/admins/")
public class AdminController {

    @Autowired
    private EventTypeService eventTypeService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;
    @Autowired
    private PDFGeneratorService pdfGeneratorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OfferService offerService;
    @Autowired
    private ReportService reportService;

    @PostMapping("event-types")
    public ResponseEntity<String> addEventType(@RequestBody EventTypeDTO eventTypeDTO) {
        if (eventTypeDTO == null || eventTypeDTO.getName() == null || eventTypeDTO.getDescription() == null) {
            return new ResponseEntity<>("Invalid event type data", HttpStatus.BAD_REQUEST);
        }

        EventType eventType = new EventType();
        eventType.setName(eventTypeDTO.getName());
        eventType.setDescription(eventTypeDTO.getDescription());
        eventType.setIsDeleted(false);

        eventTypeService.save(eventType);
        return new ResponseEntity<>("Event type added successfully", HttpStatus.CREATED);
    }

    @GetMapping("event-types")
    public ResponseEntity<List<EventTypeDTO>> getAllEventTypes() {
        List<EventType> eventTypes = eventTypeService.findAll();
        if(eventTypes == null || eventTypes.size() == 0) {
            throw new NotFoundException("No events found");
        }
        List<EventTypeDTO> eventTypeDTOs = eventTypes.stream().map(eventType -> {
            EventTypeDTO dto = new EventTypeDTO();
            dto.setName(eventType.getName());
            dto.setDescription(eventType.getDescription());
            dto.setIsDeleted(eventType.getIsDeleted());
            return dto;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(eventTypeDTOs, HttpStatus.OK);
    }

    @PutMapping("event-types/{id}")
    public ResponseEntity<String> updateEventType(
            @PathVariable Integer id,
            @RequestBody EventTypeDTO eventTypeDTO) {
        EventType eventType = eventTypeService.findById(id);
        if (eventType == null) {
           return new ResponseEntity<>("Event type not found", HttpStatus.NOT_FOUND);
        }
        System.out.println("Updating event type with ID " + id);
        eventType.setDescription(eventTypeDTO.getDescription());
        eventTypeService.save(eventType);

        return new ResponseEntity<>("Event type updated successfully", HttpStatus.OK);
    }

    @PutMapping("event-types/{id}/activate")
    public ResponseEntity<String> activateEventType(@PathVariable Integer id) {
        EventType eventType = eventTypeService.findById(id);
        if (eventType == null) {
            return new ResponseEntity<>("Event type not found", HttpStatus.NOT_FOUND);
        }

        eventType.setIsDeleted(false);
        eventTypeService.save(eventType);
        return new ResponseEntity<>("Event type activated", HttpStatus.OK);
    }
//    @GetMapping
//    public ResponseEntity<PagedResponse<EventTypeDTO>> getEventTypesPage(Pageable pageable) {
//        Page<EventType> eventTypePage = eventTypeService.findAll(pageable);
//
//        List<EventTypeDTO> eventTypeDTOs = eventTypePage.stream()
//        List<EventTypeDTO> eventTypeDTOs = eventTypePage.stream()
//                .map(EventTypeDTO::new)
//                .toList();
//
//        PagedResponse<EventTypeDTO> response = new PagedResponse<>(
//                eventTypeDTOs,
//                eventTypePage.getTotalPages(),
//                eventTypePage.getTotalElements()
//        );
//
//        return ResponseEntity.ok(response);
//    }

    @PutMapping("event-types/{id}/deactivate")
    public ResponseEntity<String> deactivateEventType(@PathVariable Integer id) {
        EventType eventType = eventTypeService.findById(id);
        if (eventType == null) {
            return new ResponseEntity<>("Event type not found", HttpStatus.NOT_FOUND);
        }

        eventType.setIsDeleted(true);
        eventTypeService.save(eventType);
        return new ResponseEntity<>("Event type deactivated", HttpStatus.OK);
    }

    @PutMapping("suspend/{id}")
    public ResponseEntity<UserDTO> suspendUser(@PathVariable Integer id) {
        User suspendedUser = userService.suspendUser(id);
        return ResponseEntity.ok(new UserDTO(suspendedUser));
    }
    @GetMapping("{eventId}/analytics")
    public ResponseEntity<EventDTO> getEventAnalytics(@PathVariable Integer eventId) {
        Event event = eventService.findById(eventId);
        if (event == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        EventDTO analytics = new EventDTO();
//        Some logic for analysis
//        analytics.setEventName(event.getName());
//        analytics.setTotalAttendees(event.getGuests().size());
//        analytics.setRatingsDistribution(eventService.getRatingsDistribution(event));
//        analytics.setAverageRating(eventService.getAverageRating(event));

        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("{eventId}/generate-analytics-pdf")
    public ResponseEntity<byte[]> generateAnalyticsPDF(@PathVariable Integer eventId) {
        Event event = eventService.findById(eventId);
        if (event == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        byte[] pdf = pdfGeneratorService.generateEventAnalyticsPDF(event);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=event-analytics.pdf")
                .body(pdf);
    }
    @GetMapping("categories/all")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        if (categories.isEmpty()) {
          return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }

    @PostMapping("categories/add")
    public ResponseEntity<String> createCategory(@RequestBody Category category) {
        categoryService.save(category);
        return ResponseEntity.ok("Category created successfully");
    }

    @PutMapping("categories/update")
    public ResponseEntity<Category> updateCategory(@RequestBody Category category) {
        Category oldCategory = categoryService.findById(category.getId());
        if (oldCategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        oldCategory.setName(category.getName());
        oldCategory.setDescription(category.getDescription());
        Category updated = categoryService.update(oldCategory);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("categories/delete")
    public ResponseEntity<String> deleteCategory(@RequestBody Category category) {
        Category oldCategory = categoryService.findById(category.getId());
        if (oldCategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!offerService.allOffersWithCategory(oldCategory).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The category must not have any offers using it.");
        }
        categoryService.delete(oldCategory);
        return ResponseEntity.ok("Category deleted successfully");
    }

    @GetMapping("categories/suggestions")
    public ResponseEntity<List<CategorySuggestionDTO>> getAllCategoriesSuggestions() {
        List<Report> suggestions = reportService.findAllCategorySuggestions();
        if (suggestions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(suggestions.stream().map(CategorySuggestionDTO::new).collect(Collectors.toList()));
    }

    @PutMapping("categpries/suggestions/update")
    public ResponseEntity<String> updateCategorySuggestion(@RequestBody CategorySuggestionDTO categorySuggestionDTO) {
        Report report = reportService.findById(categorySuggestionDTO.getId());
        if (report == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        reportService.update(report);
        return ResponseEntity.ok("Category suggestion updated successfully");
    }
    @DeleteMapping("categpries/suggestions/delete")
    public ResponseEntity<String> deleteCategorySuggestion(@RequestBody CategorySuggestionDTO categorySuggestionDTO) {
        Report report = reportService.findById(categorySuggestionDTO.getId());
        if (report == null) {throw new NotFoundException("Category suggestion not found");}
        reportService.delete(report.getId());
        return ResponseEntity.ok("Category suggestion deleted successfully");
    }
}
