package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.OfferHistory;
import ftn.siit.project.isspoject.repository.OfferHistoryRepository;
import ftn.siit.project.isspoject.service.interfaces.OfferHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfferHistoryServiceImpl implements OfferHistoryService {
    @Autowired
    private OfferHistoryRepository offerHistoryRepository;
    @Override
    public OfferHistory save(OfferHistory offerHistory) {
        return null;
    }

    @Override
    public OfferHistory update(OfferHistory offerHistory) {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public OfferHistory findById(Integer id) {
        return null;
    }

    @Override
    public void add(Offer offer) {

    }
}
