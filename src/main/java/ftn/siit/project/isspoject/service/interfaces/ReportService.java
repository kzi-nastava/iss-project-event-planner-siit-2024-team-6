package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.Report;

import java.util.List;

public interface ReportService {
    Report save(Report report);
    Report update(Report report);
    void delete(int id);
    void acceptReport(int id);
    void rejectReport(int id);
    List<Report> findAll();
    List<Report> findAllCategorySuggestions();
    Report findById(Integer id);
}

