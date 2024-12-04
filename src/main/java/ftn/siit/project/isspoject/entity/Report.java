package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.user.NewUserReportDTO;
import lombok.Data;

@Data
public class Report {
    private Integer id;
    private String reason;
    private User reported;
    private User reporter;
    private Status status;
    private Reaction reactions;

    public Report(){}
    public Report(NewUserReportDTO userReportDTO) {
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
