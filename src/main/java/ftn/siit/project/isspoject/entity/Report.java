package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.user.NewUserReportDTO;
import lombok.Data;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String reason;

    @ManyToOne
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reported;

    @ManyToOne
    @JoinColumn(name = "reporter_user_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reaction_id")
    private Reaction reactions;

    public Report(){}
    public Report(NewUserReportDTO userReportDTO) {
        if (userReportDTO == null) {
            throw new IllegalArgumentException("UserReportDTO cannot be null");
        }
        this.reason = userReportDTO.getReason();
        this.status = Status.PENDING;
        this.reactions = null;
    }
}
