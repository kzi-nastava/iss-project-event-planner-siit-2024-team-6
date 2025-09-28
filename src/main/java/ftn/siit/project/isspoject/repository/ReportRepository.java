package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Report;
import ftn.siit.project.isspoject.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
//    List<Report> findAllByCategorySuggestion(boolean b);
        Page<Report> findAllByStatus(Status status, Pageable pageable);

}
