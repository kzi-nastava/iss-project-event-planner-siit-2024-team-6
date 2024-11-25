package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Event;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
@Service
public class PDFGeneratorService {
    public byte[] generateGuestListPDF(Event event) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

//        try (PDDocument document = new PDDocument()) {
//            PDPage page = new PDPage();
//            document.addPage(page);
//
//            PDPageContentStream contentStream = new PDPageContentStream(document, page);
//            contentStream.beginText();
//            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
//            contentStream.setLeading(14.5f);
//            contentStream.newLineAtOffset(25, 700);
//
//            contentStream.showText("Guest List for Event: " + event.getName());
//            contentStream.newLine();
//            contentStream.showText("Location: " + event.getLocation());
//            contentStream.newLine();
//            contentStream.showText("Date: " + event.getDate().toString());
//            contentStream.newLine();
//            contentStream.newLine();
//
//            contentStream.setFont(PDType1Font.HELVETICA, 10);
//            for (User guest : event.getGuests()) {
//                contentStream.showText("- " + guest.getName() + " " + guest.getLastname() + " (" + guest.getEmail() + ")");
//                contentStream.newLine();
//            }
//
//            contentStream.endText();
//            contentStream.close();
//
//            document.save(out);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return out.toByteArray();
    }

    public byte[] generateEventPDF(Event event) {
        return null;
    }
}
