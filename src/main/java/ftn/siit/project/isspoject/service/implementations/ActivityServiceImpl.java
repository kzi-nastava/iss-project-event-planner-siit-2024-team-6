package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.repository.ActivityRepository;
import ftn.siit.project.isspoject.service.interfaces.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;
}
