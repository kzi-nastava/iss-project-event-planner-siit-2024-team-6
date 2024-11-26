package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Report;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ReportServiceImpl implements ReportService {
    @Override
    public Report save(Report report) {
        return null;
    }

    @Override
    public Report update(Report report) {
        return null;
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void acceptReport(int id) {

    }

    @Override
    public void rejectReport(int id) {

    }

    @Override
    public List<Report> findAll() {
        return List.of();
    }

    @Override
    public List<Report> findAllCategorySuggestions() {
        return List.of();
    }

    @Override
    public Report findById(Integer id) {
        return null;
    }
}
