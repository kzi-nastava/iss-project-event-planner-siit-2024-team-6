package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.category.NewCategoryDTO;
import ftn.siit.project.isspoject.dto.category.NewCategorySuggestionDTO;
import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.event.EventTypeDTO;
import ftn.siit.project.isspoject.dto.user.UserDTO;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.interfaces.*;
import ftn.siit.project.isspoject.service.external.PDFGeneratorService;
import ftn.siit.project.isspoject.dto.category.CategorySuggestionDTO;
import ftn.siit.project.isspoject.service.interfaces.OfferService;
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
    @Autowired
    private CategorySuggestionService categorySuggestionService;

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
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        if (categories.isEmpty()) {
          return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody NewCategoryDTO dto) {
        Category category = new Category();
        // add transfer of data from dto
        Category savedCategory = categoryService.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @PutMapping("{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable int id, @RequestBody NewCategoryDTO dto) {
        Category oldCategory = categoryService.findById(id);
        if (oldCategory == null) {
           throw new NotFoundException("Category with id " + id + " not found, can't be updated");
        }
        oldCategory.setName(dto.getName());
        oldCategory.setDescription(dto.getDescription());
        Category updated = categoryService.update(oldCategory);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable int id) {
        Category oldCategory = categoryService.findById(id);
        if (oldCategory == null) {
           throw new NotFoundException("Category with id " + id + " not found, can't be deleted");
        }
        if (!offerService.allOffersWithCategory(oldCategory).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The category must not have any offers using it.");
        }
        categoryService.delete(oldCategory);
        return ResponseEntity.ok("Category deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<CategorySuggestionDTO>> getAllCategorySuggestions() {
        List<CategorySuggestion> suggestions = categorySuggestionService.getPending();
        if (suggestions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(suggestions.stream().map(CategorySuggestionDTO::new).collect(Collectors.toList()));
    }

    @PutMapping("{id}")
    public ResponseEntity<CategorySuggestionDTO> updateCategorySuggestion(@PathVariable int id, @RequestBody NewCategorySuggestionDTO dto) {
        CategorySuggestion cs = categorySuggestionService.findById(id);
        if (cs == null) {
            throw new NotFoundException("CategorySuggestion with id " + id + " not found, can't be updated");
        }
        cs.setSuggestion(dto.getSuggestion());
        cs.setStatus(Status.valueOf(dto.getStatus()));
        CategorySuggestion updated = categorySuggestionService.update(cs);
        return ResponseEntity.ok(new CategorySuggestionDTO(updated));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCategorySuggestion(@PathVariable int id) {
        CategorySuggestion cs = categorySuggestionService.findById(id);
        if (cs == null) {throw new NotFoundException("Category suggestion not found");}
        categorySuggestionService.delete(cs);
        return ResponseEntity.noContent().build();
    }
}
