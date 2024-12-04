package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.entity.Reaction;
import ftn.siit.project.isspoject.entity.Status;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.interfaces.ProviderService;
import ftn.siit.project.isspoject.service.interfaces.ReactionService;
import ftn.siit.project.isspoject.service.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("")
    public ResponseEntity<Reaction> addReaction(@RequestBody Reaction reaction) {
        //comment is by default pending
        if(reaction.getText()!=null && reaction.getText().length()>0) {
            reaction.setStatus(Status.PENDING);
        }
        Reaction savedReaction = reactionService.save(reaction);

//        if (reaction.getEvent() != null) {
//            User organizer = reaction.getEvent().getOrganizer();
//            String message = "A new reaction has been added to your event: " + reaction.getEvent().getName();
//            notificationService.notifyUser(organizer, message);
//        } else if (reaction.getOffer() != null) {
//            User provider = providerService.findByOffer(reaction.getOffer());
//            String message = "A new reaction has been added to your offer: " + reaction.getOffer().getName();
//            notificationService.notifyUser(provider, message);
//        }

        return ResponseEntity.ok(savedReaction);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<Reaction> acceptReaction(@PathVariable Integer id) {
        Reaction reaction = reactionService.findById(id);
        if (reaction == null) {
            throw new NotFoundException("Reaction not found");
        }

        reaction.setStatus(Status.ACCEPTED);
        Reaction updatedReaction = reactionService.save(reaction);

        return ResponseEntity.ok(updatedReaction);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Reaction> deleteReaction(@PathVariable Integer id) {
        Reaction reaction = reactionService.findById(id);
        if (reaction == null) {
            throw new NotFoundException("Reaction not found");
        }

        reaction.setDeleted(true);
        Reaction updatedReaction = reactionService.save(reaction);

        return ResponseEntity.ok(updatedReaction);
    }

    @GetMapping("pending")
    public ResponseEntity<List<Reaction>> getPendingReactions() {
        List<Reaction> pendingReactions = reactionService.getPendingReactions();

        return ResponseEntity.ok(pendingReactions);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Reaction> updateReaction(@PathVariable Integer id, @RequestBody Reaction updatedReaction) {
        Reaction existingReaction = reactionService.findById(id);
        if (existingReaction == null) {
            throw new NotFoundException("Reaction not found");
        }

        updatedReaction.setId(id);

        Reaction savedReaction = reactionService.save(updatedReaction);

        return ResponseEntity.ok(savedReaction);
    }
}
