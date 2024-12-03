package ftn.siit.project.isspoject.dto;

import ftn.siit.project.isspoject.entity.Report;
import ftn.siit.project.isspoject.entity.Status;

public class CategorySuggestionDTO {
    Integer id;
    String suggestion;
    String status;

    public CategorySuggestionDTO() {}

    public CategorySuggestionDTO(Report report) {
        this.id = report.getId();
        this.suggestion = report.getReason();
        this.status = report.getStatus().toString();
    }

    public Integer getId() {
        return id;
    }
}
