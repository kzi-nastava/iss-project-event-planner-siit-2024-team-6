package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.entity.Reaction;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.service.ProviderService;
import ftn.siit.project.isspoject.service.ReactionService;
import ftn.siit.project.isspoject.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/reactions")
public class ReactionController {

    @Autowired
    private ReactionService reactionService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ProviderService providerService;

    @PostMapping("/add")
    public ResponseEntity<Reaction> addReaction(@RequestBody Reaction reaction) {
        Reaction savedReaction = reactionService.save(reaction);

        if (reaction.getEvent() != null) {
            User organizer = reaction.getEvent().getOrganizer();
            String message = "A new reaction has been added to your event: " + reaction.getEvent().getName();
            notificationService.notifyUser(organizer, message);
        } else if (reaction.getOffer() != null) {
            User provider = providerService.findByOffer(reaction.getOffer());
            String message = "A new reaction has been added to your offer: " + reaction.getOffer().getName();
            notificationService.notifyUser(provider, message);
        }

        return ResponseEntity.ok(savedReaction);
    }


}
