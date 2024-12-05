package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.repository.OfferServiceRepository;
import ftn.siit.project.isspoject.repository.OrganizerRepository;
import ftn.siit.project.isspoject.service.interfaces.OrganizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizerServiceImpl implements OrganizerService {

    private final OrganizerRepository organizerRepository;

    @Autowired
    public OrganizerServiceImpl(OrganizerRepository organizerRepository) {
        this.organizerRepository = organizerRepository;
    }

    @Override
    public Organizer findById(Integer organizerId) {
        return organizerRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("Organizer with ID " + organizerId + " not found"));
    }
}
