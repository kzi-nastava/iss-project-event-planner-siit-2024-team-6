package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.CalendarItemDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.service.interfaces.*;
import ftn.siit.project.isspoject.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/calendar/")
public class CalendarController {
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;
    @Autowired
    private OfferService offerService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ServiceService serviceService;
    @GetMapping("items")
    public ResponseEntity<List<CalendarItemDTO>> getCalendarItems(HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);


        List<CalendarItemDTO> calendarItems = new ArrayList<>();
        for(Event e: user.getAttends())
            calendarItems.add(new CalendarItemDTO(e.getName(), e.getDate(), "Event"));

        switch (user.getUserType()){
            case "Organizer":
                for(Event e: ((Organizer) user).getMyEvents())
                    calendarItems.add(new CalendarItemDTO(e.getName(), e.getDate(), "MyEvent"));
                break;
            case "Provider":
                for(Service s: (serviceService.findByProvider((Provider) user))){
                    List<Reservation> rs = reservationService.findByServiceId(s.getId());
                    for(Reservation r: rs)
                        calendarItems.add(new CalendarItemDTO(s.getName(), r.getStartTime(), "MyService"));
                }
        }


        return ResponseEntity.ok(calendarItems);
    }
}

