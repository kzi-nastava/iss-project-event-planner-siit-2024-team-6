package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.repository.OfferServiceRepository;
import ftn.siit.project.isspoject.service.interfaces.OrganizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizerServiceImpl implements OrganizerService {
    @Override
    public Organizer findById(Integer organizerId) {
        return null;
    }
}
