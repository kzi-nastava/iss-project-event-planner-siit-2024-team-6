package ftn.siit.project.isspoject.dto;

import ftn.siit.project.isspoject.entity.User;
import lombok.Data;

@Data
public class UserReportDTO {
    String reason;
    User reporter;
    User reported;
}
