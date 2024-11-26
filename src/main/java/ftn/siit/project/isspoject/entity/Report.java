package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.UserReportDTO;
import lombok.Data;

@Data
public class Report {
    Integer id;
    String reason;
    User reported;
    User reporter;
    Status status;
    Reaction reactions;

    public Report(){}
    public Report(UserReportDTO userReportDTO) {
        if (userReportDTO == null) {
            throw new IllegalArgumentException("UserReportDTO cannot be null");
        }
        this.reason = userReportDTO.getReason();
        this.reported = userReportDTO.getReported();
        this.reporter = userReportDTO.getReporter();
        this.status = Status.PENDING;
        this.reactions = null;
    }
}
