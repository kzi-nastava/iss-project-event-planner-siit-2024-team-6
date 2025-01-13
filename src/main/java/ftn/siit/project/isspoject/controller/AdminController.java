package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.category.NewCategoryDTO;
import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.event.EventTypeDTO;
import ftn.siit.project.isspoject.dto.event.NewEventTypeDTO;
import ftn.siit.project.isspoject.dto.pagination.PagedResponse;
import ftn.siit.project.isspoject.dto.user.UserDTO;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.interfaces.*;
import ftn.siit.project.isspoject.service.external.PDFGeneratorService;
import ftn.siit.project.isspoject.dto.category.CategorySuggestionDTO;
import ftn.siit.project.isspoject.service.interfaces.OfferService;
import ftn.siit.project.isspoject.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("event-types")
    public ResponseEntity<EventTypeDTO> addEventType(@RequestBody NewEventTypeDTO eventTypeDTO, HttpServletRequest request) {

        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (!user.getUserType().equals("Admin") || eventTypeDTO == null || eventTypeDTO.getName() == null || eventTypeDTO.getDescription() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Создание нового типа события
        EventType eventType = new EventType();
        eventType.setName(eventTypeDTO.getName());
        eventType.setDescription(eventTypeDTO.getDescription());
        eventType.setIsDeleted(false);
        List<Category> categories = new ArrayList<>();
        for(Category c: eventTypeDTO.getCategories()){
            categories.add(categoryService.findById(c.getId()));
        }
        eventType.setCategories(categories);

//        eventType.setCategories(categoryService.findById(eventTypeDTO.getCategories()));
        // Сохранение типа события
        EventType savedEventType = eventTypeService.save(eventType);

        // Преобразование в DTO
        EventTypeDTO responseDTO = toEventTypeDTO(savedEventType);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO); // Возвращаем созданный тип события
    }
    private EventTypeDTO toEventTypeDTO(EventType eventType) {
        EventTypeDTO eventTypeDTO = new EventTypeDTO();
        eventTypeDTO.setId(eventType.getId());
        eventTypeDTO.setName(eventType.getName());
        eventTypeDTO.setDescription(eventType.getDescription());
        return eventTypeDTO;
    }


    @GetMapping("event-types")
    public ResponseEntity<List<EventTypeDTO>> getAllEventTypes() {
        List<EventType> eventTypes = eventTypeService.findAll();
        if(eventTypes == null || eventTypes.size() == 0) {
            throw new NotFoundException("No events found");
        }
        List<EventTypeDTO> eventTypeDTOs = eventTypes.stream().map(eventType -> {
            EventTypeDTO dto = new EventTypeDTO();
            dto.setId(eventType.getId());
            dto.setName(eventType.getName());
            dto.setDescription(eventType.getDescription());
            dto.setIsDeleted(eventType.getIsDeleted());
            dto.setCategories(eventType.getCategories());
            return dto;
        }).collect(Collectors.toList());
        return new ResponseEntity<>(eventTypeDTOs, HttpStatus.OK);
    }

    @PutMapping("event-types/{id}")
    public ResponseEntity<EventTypeDTO> updateEventType(
            @PathVariable Integer id,
            @RequestBody NewEventTypeDTO eventTypeDTO) {
        EventType eventType = eventTypeService.findById(id);
        if (eventType == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Тип события не найден
        }

        // Обновление полей типа события
        eventType.setDescription(eventTypeDTO.getDescription());
        eventType.setCategories(eventTypeDTO.getCategories());
        EventType updatedEventType = eventTypeService.save(eventType);

        // Преобразование в DTO
        EventTypeDTO responseDTO = toEventTypeDTO(updatedEventType);

        return ResponseEntity.ok(responseDTO); // Возвращаем обновлённый тип события
    }


    @PutMapping("event-types/{id}/activate")
    public ResponseEntity<EventTypeDTO> activateEventType(@PathVariable Integer id) {
        EventType eventType = eventTypeService.findById(id);
        if (eventType == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Тип события не найден
        }

        // Активация типа события
        eventType.setIsDeleted(false);
        EventType activatedEventType = eventTypeService.save(eventType);

        // Преобразование в DTO
        EventTypeDTO responseDTO = toEventTypeDTO(activatedEventType);

        return ResponseEntity.ok(responseDTO); // Возвращаем активированный тип события
    }
    @PutMapping("event-types/{id}/change-status")
    public ResponseEntity<EventTypeDTO> changeStatus(@PathVariable Integer id) {
        EventType eventType = eventTypeService.findById(id);
        if (eventType == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Тип события не найден
        }

        // Активация типа события
        eventType.setIsDeleted(!eventType.getIsDeleted());
        EventType activatedEventType = eventTypeService.save(eventType);

        // Преобразование в DTO
        EventTypeDTO responseDTO = toEventTypeDTO(activatedEventType);

        return ResponseEntity.ok(responseDTO); // Возвращаем активированный тип события
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
    public ResponseEntity<EventTypeDTO> deactivateEventType(@PathVariable Integer id) {
        // Проверка существования типа события
        EventType eventType = eventTypeService.findById(id);
        if (eventType == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Тип события не найден
        }

        // Деактивация типа события
        eventType.setIsDeleted(true);
        EventType deactivatedEventType = eventTypeService.save(eventType);

        // Преобразование в DTO
        EventTypeDTO responseDTO = toEventTypeDTO(deactivatedEventType);

        return ResponseEntity.ok(responseDTO); // Возвращаем деактивированный тип события
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

    @GetMapping("category-names")
    public ResponseEntity<List<String>> getAllCategoryNames(HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null || !userService.getUserRole(userService.findByEmail(this.tokenUtils.getUsernameFromToken(jwtToken)).getId()).equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<String> categories = categoryService.findAllNames();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("categories")
    public ResponseEntity<PagedResponse<Category>> getAllCategories(Pageable page, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null || !userService.getUserRole(userService.findByEmail(this.tokenUtils.getUsernameFromToken(jwtToken)).getId()).equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Page<Category> categories = categoryService.findAll(page);

        PagedResponse<Category> response = new PagedResponse<>(
                categories.stream().toList(),
                categories.getTotalPages(),
                categories.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("category")
    public ResponseEntity<Category> addCategory(@RequestBody NewCategoryDTO dto, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null || !userService.getUserRole(userService.findByEmail(this.tokenUtils.getUsernameFromToken(jwtToken)).getId()).equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Category savedCategory = categoryService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @PutMapping("category/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable int id, @RequestBody NewCategoryDTO dto, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null || !userService.getUserRole(userService.findByEmail(this.tokenUtils.getUsernameFromToken(jwtToken)).getId()).equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Category oldCategory = categoryService.findById(id);
        Category updated = categoryService.update(id, dto);
        notificationService.notifyUsers(userService.findByRole("Provider"), "Updated category\nOLD:\n"+oldCategory.getName()+"\n"+oldCategory.getDescription()+"\nNEW:\n"+updated.getName()+"\n"+updated.getDescription());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("category/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable int id, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null || !userService.getUserRole(userService.findByEmail(this.tokenUtils.getUsernameFromToken(jwtToken)).getId()).equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Category oldCategory = categoryService.findById(id);
        if (!offerService.allOffersWithCategory(oldCategory).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The category must not have any offers using it.");
        }
        categoryService.delete(oldCategory);
        return ResponseEntity.ok("{\"message\": \"Category deleted successfully\"}");
    }

    @GetMapping("suggestions")
    public ResponseEntity<PagedResponse<CategorySuggestionDTO>> getAllCategorySuggestions(Pageable page, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null || !userService.getUserRole(userService.findByEmail(this.tokenUtils.getUsernameFromToken(jwtToken)).getId()).equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Page<CategorySuggestion> suggestions = categorySuggestionService.getPending(page);
        List<CategorySuggestionDTO> dtos = suggestions.stream()
                .map(suggestion -> {
                    try {
                        return new CategorySuggestionDTO(suggestion);
                    } catch (Exception e) {
                        System.err.println("Error converting suggestion to DTO: " + e.getMessage());
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .toList();

        PagedResponse<CategorySuggestionDTO> response = new PagedResponse<>(
                dtos,
                suggestions.getTotalPages(),
                suggestions.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("suggestion/approve/{id}")
    public ResponseEntity<CategorySuggestionDTO> approveSuggestion(@PathVariable int id, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null || !userService.getUserRole(userService.findByEmail(this.tokenUtils.getUsernameFromToken(jwtToken)).getId()).equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        CategorySuggestion suggestion = categorySuggestionService.approve(id);
        Category c = categoryService.save(suggestion.getName(), suggestion.getDescription());
        serviceService.update(suggestion.getOffer().getId(), c, Status.ACCEPTED);
        notificationService.notifyUser(suggestion.getOffer().getProvider(), "Suggestion of new category ("+ suggestion.getName()+", "+suggestion.getDescription()+") has been approved");
        return ResponseEntity.ok(new CategorySuggestionDTO(suggestion));
    }

    @PutMapping("suggestion/{id}")
    public ResponseEntity<CategorySuggestionDTO> updateCategorySuggestion(@PathVariable int id, @RequestBody NewCategoryDTO dto, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null || !userService.getUserRole(userService.findByEmail(this.tokenUtils.getUsernameFromToken(jwtToken)).getId()).equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        CategorySuggestion updated = categorySuggestionService.update(id, dto);
        Category c = categoryService.save(updated.getName(), updated.getDescription());
        serviceService.update(updated.getOffer().getId(), c, Status.ACCEPTED);
        notificationService.notifyUser(updated.getOffer().getProvider(), "Your suggestion of new category has been changed to ("+ updated.getName()+", "+updated.getDescription()+") and approved");
        return ResponseEntity.ok(new CategorySuggestionDTO(updated));
    }

    @PutMapping("suggestion/reject/{id}")
    public ResponseEntity<CategorySuggestionDTO> deleteCategorySuggestion(@PathVariable int id, @RequestParam String categoryName, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null || !userService.getUserRole(userService.findByEmail(this.tokenUtils.getUsernameFromToken(jwtToken)).getId()).equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(categoryName == null || categoryName.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        CategorySuggestion cs = categorySuggestionService.reject(id);
        serviceService.update(cs.getOffer().getId(), categoryService.findByName(categoryName), Status.ACCEPTED);
        notificationService.notifyUser(cs.getOffer().getProvider(), "Your suggestion of new category has been rejected. This category is chosen instead ("+ categoryName+") ");
        return ResponseEntity.ok(new CategorySuggestionDTO(cs));
    }
}
