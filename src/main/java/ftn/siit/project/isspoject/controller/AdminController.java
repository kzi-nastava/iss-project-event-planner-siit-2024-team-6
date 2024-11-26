package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.CategorySuggestionDTO;
import ftn.siit.project.isspoject.dto.EventTypeDTO;
import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.entity.Report;
import ftn.siit.project.isspoject.service.CategoryService;
import ftn.siit.project.isspoject.service.EventTypeService;
import ftn.siit.project.isspoject.service.OfferService;
import ftn.siit.project.isspoject.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/admins")
public class AdminController {
    @Autowired
    private EventTypeService eventTypeService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OfferService offerService;
    @Autowired
    private ReportService reportService;

    @PostMapping("event-types/add")
    public ResponseEntity<String> addEventType(@RequestBody EventTypeDTO eventTypeDTO) {
        if (eventTypeDTO == null || eventTypeDTO.getName() == null || eventTypeDTO.getDescription() == null) {
            return new ResponseEntity<>("Invalid event type data", HttpStatus.BAD_REQUEST);
        }

        EventType eventType = new EventType();
        eventType.setName(eventTypeDTO.getName());
        eventType.setDescription(eventTypeDTO.getDescription());
        eventType.setCategories(eventTypeDTO.getCategories());
        eventType.setIsDeleted(false);

        eventTypeService.save(eventType);
        return new ResponseEntity<>("Event type added successfully", HttpStatus.CREATED);
    }
    @GetMapping("event-types/all")
    public ResponseEntity<List<EventTypeDTO>> getAllEventTypes() {
        List<EventType> eventTypes = eventTypeService.findAll();
        List<EventTypeDTO> eventTypeDTOs = eventTypes.stream().map(eventType -> {
            EventTypeDTO dto = new EventTypeDTO();
            dto.setName(eventType.getName());
            dto.setDescription(eventType.getDescription());
            dto.setCategories(eventType.getCategories());
            dto.setIsDeleted(eventType.getIsDeleted());
            return dto;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(eventTypeDTOs, HttpStatus.OK);
    }

    @PutMapping("event-types/{id}/update")
    public ResponseEntity<String> updateEventType(
            @PathVariable Integer id,
            @RequestBody EventTypeDTO eventTypeDTO) {
        EventType eventType = eventTypeService.findById(id);
        if (eventType == null) {
            return new ResponseEntity<>("Event type not found", HttpStatus.NOT_FOUND);
        }

        eventType.setDescription(eventTypeDTO.getDescription());
        eventType.setCategories(eventTypeDTO.getCategories());
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
    @GetMapping("categories/all")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        if(categories.isEmpty()) {return ResponseEntity.noContent().build();}
        return ResponseEntity.ok(categories);
    }

    @PostMapping("categories/add")
    public ResponseEntity<String> createCategory(@RequestBody Category category){
        categoryService.save(category);
        return ResponseEntity.ok("Category created successfully");
    }

    @PutMapping("categories/update")
    public ResponseEntity<Category> updateCategory(@RequestBody Category category){
        Category oldCategory = categoryService.findById(category.getId());
        if(oldCategory == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).build();}
        oldCategory.setName(category.getName());
        oldCategory.setDescription(category.getDescription());
        Category updated = categoryService.update(oldCategory);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("categories/delete")
    public ResponseEntity<String> deleteCategory(@RequestBody Category category){
        Category oldCategory = categoryService.findById(category.getId());
        if(oldCategory == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).build();}
        if(!offerService.allOffersWithCategory(oldCategory).isEmpty()){return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The category must not have any offers using it.");}
        categoryService.delete(oldCategory);
        return ResponseEntity.ok("Category deleted successfully");
    }

    @GetMapping("categories/suggestions")
    public ResponseEntity<List<CategorySuggestionDTO>> getAllCategoriesSuggestions() {
        List<Report> suggestions = reportService.findAllCategorySuggestions();
        if(suggestions.isEmpty()) {return ResponseEntity.noContent().build();}
        return ResponseEntity.ok(suggestions.stream().map(CategorySuggestionDTO::new).collect(Collectors.toList()));
    }

    @PutMapping("categpries/suggestions/update")
    public ResponseEntity<String> updateCategorySuggestion(@RequestBody CategorySuggestionDTO categorySuggestionDTO) {
        Report report = reportService.findById(categorySuggestionDTO.getId());
        if(report == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).build();}
        reportService.update(report);
        return ResponseEntity.ok("Category suggestion updated successfully");
    }
}
