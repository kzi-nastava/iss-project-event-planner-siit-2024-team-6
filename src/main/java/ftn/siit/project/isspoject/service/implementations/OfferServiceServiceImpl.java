package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.repository.OfferServiceRepository;
import ftn.siit.project.isspoject.entity.OfferService;
import ftn.siit.project.isspoject.service.interfaces.OfferServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfferServiceServiceImpl implements OfferServiceService {
    @Autowired
    private OfferServiceRepository offerServiceRepository;
    @Override
    public OfferService findById(int id) {
        return offerServiceRepository.findById(id);
    }
}
