package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Reaction;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.service.interfaces.ReactionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReactionServiceImpl implements ReactionService {
    @Override
    public List<Reaction> findAll() {
        return List.of();
    }

    @Override
    public Reaction findById(Integer eventId) {
        return null;
    }

    @Override
    public List<Reaction> findByUser(User user) {
        return List.of();
    }

    @Override
    public List<Reaction> findByOffer(Offer offer) {
        return List.of();
    }

    @Override
    public List<Reaction> findByEvent(Event event) {
        return List.of();
    }

    @Override
    public Reaction save(Reaction reaction) {
        return null;
    }

    @Override
    public void delete(Reaction reaction) {

    }
    public List<Reaction> getPendingReactions() {
        return List.of();
        //return reactionRepository.findByStatus(Status.PENDING);
    }
}
