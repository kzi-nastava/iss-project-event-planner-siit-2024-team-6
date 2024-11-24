package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.EventTypeDTO;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.service.EventTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/admin")
public class AdminController {
    @Autowired
    private EventTypeService eventTypeService;

    @PostMapping("event-type/add")
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
    @GetMapping("event-type/all")
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


}
