package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.repository.ReactionRepository;
import ftn.siit.project.isspoject.service.interfaces.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReactionServiceImpl implements ReactionService {
    @Autowired
    private ReactionRepository reactionRepository;

    @Override
    public List<Reaction> findAll() {
        return reactionRepository.findAll();
    }

    @Override
    public Reaction findById(Integer id) {
        return reactionRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Reaction not found"));
    }

    @Override
    public List<Reaction> findByUser(User user) {
        return reactionRepository.findReactionsByUserId(user.getId());
    }

    @Override
    public List<Reaction> findByOffer(Offer offer) {
        return reactionRepository.findReactionsByOfferId(offer.getId());
    }

    @Override
    public List<Reaction> findByEvent(Event event) {
        return reactionRepository.findReactionsByEventId(event.getId());
    }

    @Override
    public Reaction save(Reaction reaction) {
        return reactionRepository.save(reaction);
    }

    @Override
    public void delete(Reaction reaction) {
        reactionRepository.delete(reaction);
    }
    public Page<Reaction> getPendingReactions(Pageable pageable) {
        return reactionRepository.findByStatusAndIsDeletedFalse(Status.PENDING,pageable);
    }
}
