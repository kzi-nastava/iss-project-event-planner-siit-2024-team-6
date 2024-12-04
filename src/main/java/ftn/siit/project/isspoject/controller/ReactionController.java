package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.reaction.NewReactionDTO;
import ftn.siit.project.isspoject.dto.reaction.ReactionDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.interfaces.*;
import ftn.siit.project.isspoject.service.interfaces.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/reactions/")
public class ReactionController {

    @Autowired
    private ReactionService reactionService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ProviderService providerService;
    @Autowired
    private OfferService offerService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;

    @PostMapping()
    public ResponseEntity<ReactionDTO> addReaction(@RequestBody NewReactionDTO dto) {
        Reaction reaction = new Reaction(dto);
        Offer offer = offerService.findById(dto.getOfferId());
        Event event = eventService.findById(dto.getEventId());
        User user = userService.findById(dto.getUserId());
        reaction.setUser(user);
        reaction.setOffer(offer);
        reaction.setEvent(event);

        //comment is by default pending
        if(reaction.getText()!=null && reaction.getText().length()>0) {
            reaction.setStatus(Status.PENDING);
        }
        Reaction saved = reactionService.save(reaction);

//        if (reaction.getEvent() != null) {
//            User organizer = reaction.getEvent().getOrganizer();
//            String message = "A new reaction has been added to your event: " + reaction.getEvent().getName();
//            notificationService.notifyUser(organizer, message);
//        } else if (reaction.getOffer() != null) {
//            User provider = providerService.findByOffer(reaction.getOffer());
//            String message = "A new reaction has been added to your offer: " + reaction.getOffer().getName();
//            notificationService.notifyUser(provider, message);
//        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new ReactionDTO(saved));
    }

    @PutMapping("{id}/accept")
    public ResponseEntity<ReactionDTO> acceptReaction(@PathVariable Integer id) {
        Reaction reaction = reactionService.findById(id);
        if (reaction == null) {
            throw new NotFoundException("Reaction not found");
        }

        reaction.setStatus(Status.ACCEPTED);
        Reaction updated = reactionService.save(reaction);

        return ResponseEntity.ok(new ReactionDTO(updated));
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteReaction(@PathVariable Integer id) {
        Reaction reaction = reactionService.findById(id);
        if (reaction == null) {
            throw new NotFoundException("Reaction not found");
        }

        reaction.setDeleted(true);
        reactionService.save(reaction);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("pending")
    public ResponseEntity<List<ReactionDTO>> getPendingReactions() {
        List<Reaction> pending = reactionService.getPendingReactions();

        List<ReactionDTO> reactionDTOs = pending.stream()
                .map(ReactionDTO::new)
                .toList();
        return ResponseEntity.ok(reactionDTOs);
    }
    @PutMapping("{id}")
    public ResponseEntity<ReactionDTO> updateReaction(@PathVariable Integer id, @RequestBody NewReactionDTO dto) {
        Reaction existingReaction = reactionService.findById(id);
        if (existingReaction == null) {
            throw new NotFoundException("Reaction not found");
        }

        if (dto.getText() != null) {
            existingReaction.setText(dto.getText());
        }
        if (dto.getRating() != null) {
            existingReaction.setRating(dto.getRating());
        }
        if (dto.getOfferId() != null) {
            Offer offer = offerService.findById(dto.getOfferId());
            existingReaction.setOffer(offer);
        }
        if (dto.getEventId() != null) {
            Event event = eventService.findById(dto.getEventId());
            existingReaction.setEvent(event);
        }
        if (dto.getUserId() != null) {
            User user = userService.findById(dto.getUserId());
            existingReaction.setUser(user);
        }

        Reaction updated = reactionService.save(existingReaction);
        ReactionDTO responseDto = new ReactionDTO(updated);

        return ResponseEntity.ok(responseDto);
    }
}
