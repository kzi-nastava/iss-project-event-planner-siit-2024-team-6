package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.activity.ActivityDTO;
import ftn.siit.project.isspoject.dto.activity.NewActivityDTO;
import ftn.siit.project.isspoject.dto.event.NewClosedEventDTO;
import ftn.siit.project.isspoject.entity.Activity;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;

import java.util.List;

public interface ActivityService {

    List<Activity> findAll();
    //Page<Activity> findAll(Pageable page);
    Activity findById(Integer id);
    Activity save(Activity activity);
    void delete(Activity activity);

    Activity update(Activity activity, NewActivityDTO activityDTO);
}
