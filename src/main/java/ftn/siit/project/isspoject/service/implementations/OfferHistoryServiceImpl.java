package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.OfferHistory;
import ftn.siit.project.isspoject.repository.OfferHistoryRepository;
import ftn.siit.project.isspoject.service.interfaces.OfferHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OfferHistoryServiceImpl implements OfferHistoryService {
    @Autowired
    private OfferHistoryRepository offerHistoryRepository;

    @Override
    public OfferHistory save(OfferHistory offerHistory) {
        return offerHistoryRepository.save(offerHistory);
    }

    @Override
    public OfferHistory update(OfferHistory updatedOfferHistory) {
        OfferHistory existingOfferHistory = offerHistoryRepository.findById(updatedOfferHistory.getId())
                .orElseThrow(() -> new IllegalArgumentException("OfferHistory not found with ID, cannot be updated: " + updatedOfferHistory.getId()));

        if (updatedOfferHistory.getOffers() != null) {
            existingOfferHistory.setOffers(updatedOfferHistory.getOffers());
        }
        if (updatedOfferHistory.getTimestamps() != null) {
            existingOfferHistory.setTimestamps(updatedOfferHistory.getTimestamps());
        }

        return offerHistoryRepository.save(existingOfferHistory);
    }

    @Override
    public void delete(Integer id) {
        offerHistoryRepository.deleteById(id);
    }

    @Override
    public Optional<OfferHistory> findById(Integer id) {
        return offerHistoryRepository.findById(id);
    }

    @Override
    public OfferHistory add(int id, Offer offer) {
        OfferHistory existingOfferHistory = offerHistoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("OfferHistory not found with ID: " + id));
        existingOfferHistory.getOffers().add(offer);
        existingOfferHistory.getTimestamps().add(LocalDateTime.now());
        return update(existingOfferHistory);
    }
}
