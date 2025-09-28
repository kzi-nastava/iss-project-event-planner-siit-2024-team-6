package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReactionService {

    List<Reaction> findAll();
    Reaction findById(Integer eventId);
    List<Reaction> findByUser(User user);
    double findRatingForOffer(Offer offer);
    List<Reaction> findByEvent(Event event);


    Reaction save(Reaction reaction);
    void delete(Reaction reaction);
    Page<Reaction> getPendingReactions(Pageable pageable);
    Page<Reaction> getAcceptedReactionsForProvider(Pageable pageable, Provider provider);
}
