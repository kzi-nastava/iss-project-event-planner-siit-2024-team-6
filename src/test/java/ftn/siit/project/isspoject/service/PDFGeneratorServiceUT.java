package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Activity;
import ftn.siit.project.isspoject.service.external.PDFGeneratorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class PDFGeneratorServiceUT {

    private final PDFGeneratorService pdfService = new PDFGeneratorService();

    @Test
    @DisplayName("Should generate non-empty PDF from activity list")
    public void shouldGeneratePdfFromActivities() throws IOException {
        Activity activity = new Activity();
        activity.setName("Concert");
        activity.setDescription("Live music performance");
        activity.setLocation("Main Stage");
        activity.setStartTime(LocalDateTime.of(2025, 7, 10, 18, 0));
        activity.setEndTime(LocalDateTime.of(2025, 7, 10, 19, 30));

        byte[] pdfBytes = pdfService.generateAgendaPdf(List.of(activity));

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(100);

        String header = new String(pdfBytes, 0, 4);
        assertThat(header).isEqualTo("%PDF");
    }
}
