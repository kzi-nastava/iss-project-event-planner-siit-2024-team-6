package ftn.siit.project.isspoject.dto;

import ftn.siit.project.isspoject.entity.Report;
import ftn.siit.project.isspoject.entity.Status;

public class CategorySuggestionDTO {
    Integer id;
    String suggestion;
    Status status;

    public CategorySuggestionDTO(Report report) {
        this.id = report.getId();
        this.suggestion = report.getReason();
        this.status = report.getStatus();
    }

    public Integer getId() {
        return id;
    }
}
