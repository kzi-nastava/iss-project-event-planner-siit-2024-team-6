package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.activity.ActivityDTO;
import ftn.siit.project.isspoject.dto.activity.NewActivityDTO;
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
        List<Activity> activities = activityRepository.findAll();
        if (activities.isEmpty()) {
            throw new NotFoundException("No activities found.");
        }
        return activities;
    }

    @Override
    public Activity findById(Integer id) {
        return activityRepository.findById(id).orElseThrow(() -> new NotFoundException("Activity not found with ID: " + id));
    }

    @Override
    public Activity save(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity cannot be null while saving.");
        }
        return activityRepository.save(activity);
    }

    @Override
    public void delete(Activity activity) {
        if (activity == null || !activityRepository.existsById(activity.getId())) {
            throw new NotFoundException("Activity not found or already deleted with ID: " + activity.getId());
        }
        activityRepository.delete(activity);
    }

    @Override
    public Activity update(Activity activity, NewActivityDTO activityDTO) {
        activity.setDescription(activityDTO.getDescription());
        activity.setName(activityDTO.getName());
        activity.setLocation(activityDTO.getLocation());
        activity.setStartTime(activityDTO.getStartTime());
        activity.setEndTime(activityDTO.getEndTime());

        return activityRepository.save(activity);
    }
}
