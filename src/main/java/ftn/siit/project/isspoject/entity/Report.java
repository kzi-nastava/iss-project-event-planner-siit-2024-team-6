package ftn.siit.project.isspoject.entity;

import lombok.Data;

@Data
public class Report {
    Integer id;
    String reason;
    User reported;
    User reporter;
    Status status;
    Reaction reactions;
}
