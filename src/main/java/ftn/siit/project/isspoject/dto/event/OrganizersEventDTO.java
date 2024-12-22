package ftn.siit.project.isspoject.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrganizersEventDTO {
    private Integer id;
    private String name;
    private String place;
    private String date;
}
