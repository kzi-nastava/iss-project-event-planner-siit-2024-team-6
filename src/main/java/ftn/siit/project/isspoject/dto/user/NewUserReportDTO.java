package ftn.siit.project.isspoject.dto.user;

import lombok.Data;

@Data
public class NewUserReportDTO {
    String reason;
    Integer reporterId;
    Integer reportedId;
}
