package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportService {
    Report save(Report report);
    Report update(Report report);
    void delete(int id);
    void acceptReport(int id);
    void rejectReport(int id);
    List<Report> findAll();
    Page<Report> findAllPending(Pageable pageable);
    List<Report> findAllCategorySuggestions();
    Report findById(Integer id);
}

