package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Activity;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.ActivityRepository;
import ftn.siit.project.isspoject.service.interfaces.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    @Override
    public Activity findById(Integer id) {
        return activityRepository.findById(id).orElseThrow(() -> new NotFoundException("Activity not found with ID: " + id));
    }

    @Override
    public Activity save(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public void delete(Activity activity) {

    }
}
