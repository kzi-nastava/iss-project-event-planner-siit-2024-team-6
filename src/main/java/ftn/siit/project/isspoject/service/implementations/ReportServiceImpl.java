package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Report;
import ftn.siit.project.isspoject.entity.Status;
import ftn.siit.project.isspoject.repository.ReportRepository;
import ftn.siit.project.isspoject.service.interfaces.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report save(Report report) {
        return reportRepository.save(report);
    }

    @Override
    public Report update(Report report) {
        if (!reportRepository.existsById(report.getId())) {
            throw new IllegalArgumentException("Report with ID " + report.getId() + " does not exist");
        }
        return reportRepository.save(report);
    }

    @Override
    public void delete(int id) {
        if (!reportRepository.existsById(id)) {
            throw new IllegalArgumentException("Report with ID " + id + " does not exist");
        }
        reportRepository.deleteById(id);
    }

    @Override
    public void acceptReport(int id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report with ID " + id + " not found"));
        report.setStatus(Status.ACCEPTED);
        reportRepository.save(report);
    }

    @Override
    public void rejectReport(int id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report with ID " + id + " not found"));
        report.setStatus(Status.REJECTED);
        reportRepository.save(report);
    }

    @Override
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    @Override
    public List<Report> findAllCategorySuggestions() {
//        return reportRepository.findAllByCategorySuggestion(true);
        return null;
    }

    @Override
    public Report findById(Integer id) {
        // Поиск отчета по ID
        return reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report with ID " + id + " not found"));
    }
}
