package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.reaction.NewReactionDTO;
import ftn.siit.project.isspoject.dto.reaction.ReactionDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.interfaces.*;
import ftn.siit.project.isspoject.service.interfaces.OfferService;
import ftn.siit.project.isspoject.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    @Autowired
    private TokenUtils tokenUtils;

    @PostMapping()
    public ResponseEntity<ReactionDTO> addReaction(@RequestBody NewReactionDTO dto, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Reaction reaction = new Reaction(dto);
        if(dto.getEventId() == null && dto.getOfferId() == null){
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else if(dto.getEventId() != null){
            Event event = eventService.findById(dto.getEventId());
            reaction.setEvent(event);
        }else if(dto.getOfferId() != null){
            Offer offer = offerService.findById(dto.getOfferId());
            reaction.setOffer(offer);
        }
        reaction.setUser(user);
        //comment is by default pending
        if((reaction.getText() == null || reaction.getText().length() == 0) && (reaction.getRating() == 0 || reaction.getRating() == null)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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

    @GetMapping("provider/{id}")
    public ResponseEntity<Page<ReactionDTO>> getProvidersReactions(@PathVariable Integer id, @PageableDefault(size = 10) Pageable pageable) {
        Provider provider = providerService.findById(id);
        if (provider == null) {
            throw new NotFoundException("Provider not found");
        }
        Page<Reaction> reactions = reactionService.getAcceptedReactionsForProvider(pageable, provider);
        Page<ReactionDTO> reactionDTOPage = reactions.map(ReactionDTO::new);
        return ResponseEntity.ok(reactionDTOPage);
    }

    @GetMapping("rating-offer/{id}")
    public ResponseEntity<Double> getOfferRating(@PathVariable Integer id) {
        Offer offer = offerService.findById(id);
        if (offer == null) {
            throw new NotFoundException("Offer not found");
        }
        double rating = reactionService.findRatingForOffer(offer);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("pending")
    public ResponseEntity<Page<ReactionDTO>> getPendingReactions(
            @PageableDefault(size = 10) Pageable pageable) {

        // Assuming your service method supports Pageable
        Page<Reaction> pendingPage = reactionService.getPendingReactions(pageable);

        Page<ReactionDTO> reactionDTOPage = pendingPage.map(ReactionDTO::new);

        return ResponseEntity.ok(reactionDTOPage);
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

        Reaction updated = reactionService.save(existingReaction);
        ReactionDTO responseDto = new ReactionDTO(updated);

        return ResponseEntity.ok(responseDto);
    }
}
