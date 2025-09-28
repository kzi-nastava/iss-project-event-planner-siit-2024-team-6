package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.repository.OrganizerRepository;
import ftn.siit.project.isspoject.service.interfaces.OrganizerService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

@Service
public class OrganizerServiceImpl implements OrganizerService {

    private final OrganizerRepository organizerRepository;

    @Autowired
    public OrganizerServiceImpl(OrganizerRepository organizerRepository) {
        this.organizerRepository = organizerRepository;
    }

    @Override
    public Organizer findByEmail(String email) {
        return organizerRepository.findOrganizerByEmailAndIsActiveIsTrue(email);
    }

    @Override
    public Organizer findById(Integer organizerId) {
        return organizerRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("Organizer with ID " + organizerId + " not found"));
    }

    @Override
    public Organizer findOrganizerByEventId(Integer eventId) {
        return organizerRepository.findOrganizerByEventId(eventId);
    }

    @Override
    public Organizer findByEventId(Integer eventId) {
        return organizerRepository.findOrganizerByEventId(eventId);
    }
}