package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.*;

import java.util.List;

public interface ReactionService {

    List<Reaction> findAll();
    Reaction findById(Integer eventId);
    List<Reaction> findByUser(User user);
    List<Reaction> findByOffer(Offer offer);
    List<Reaction> findByEvent(Event event);


    Reaction save(Reaction reaction);
    void delete(Reaction reaction);
    List<Reaction> getPendingReactions();
}
