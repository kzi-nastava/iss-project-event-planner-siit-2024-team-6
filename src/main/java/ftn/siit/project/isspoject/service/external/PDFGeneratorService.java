package ftn.siit.project.isspoject.service.external;

import ftn.siit.project.isspoject.entity.Activity;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.dto.offer.PriceListItemDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PDFGeneratorService {

    public byte[] generatePriceListPdf(List<PriceListItemDTO> items) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            try {
                float margin = 50;
                float yStart = 750;
                float rowHeight = 20;
                float cellMargin = 5;
                float nextY = yStart;

                // Column headers and widths
                String[] headers = {"#", "Name", "Price", "Discount Price", "Type"};
                float[] columnWidths = {50, 150, 80, 100, 80};

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

                // Draw header row
                for (int i = 0; i < headers.length; i++) {
                    drawCell(contentStream, margin, nextY, columnWidths[i], rowHeight, headers[i], cellMargin, PDType1Font.HELVETICA_BOLD, 10);
                    margin += columnWidths[i];
                }
                margin = 50;
                nextY -= rowHeight;

                contentStream.setFont(PDType1Font.HELVETICA, 10);
                int k = 1;
                for (PriceListItemDTO item : items) {
                    String[] row = {
                            String.valueOf(k),
                            item.getOfferName(),
                            String.format("%.2f", item.getOfferPrice()),
                            item.getOfferDiscountPrice() == 0.0 ? "None" : String.format("%.2f", item.getOfferDiscountPrice()),
                            item.isService() ? "Service" : "Product"
                    };

                    k += 1;
                    float maxCellHeight = 0;
                    for (int i = 0; i < row.length; i++) {
                        float cellHeight = calculateCellHeight(row[i], columnWidths[i], PDType1Font.HELVETICA, 10, cellMargin);
                        maxCellHeight = Math.max(maxCellHeight, cellHeight);
                    }

                    for (int i = 0; i < row.length; i++) {
                        drawCell(contentStream, margin, nextY, columnWidths[i], maxCellHeight, row[i], cellMargin, PDType1Font.HELVETICA, 10);
                        margin += columnWidths[i];
                    }

                    margin = 50;
                    nextY -= maxCellHeight;

                    if (nextY <= margin) {
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        nextY = yStart;
                    }
                }
            } finally {
                contentStream.close();
            }

            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error creating price list PDF", e);
        }
    }


    public byte[] generateAgendaPdf(List<Activity> activities) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            try {
                contentStream.setFont(PDType1Font.HELVETICA, 10);

                contentStream.beginText();
                contentStream.newLineAtOffset(50, 400); // Координаты: x = 50, y = 750

                // Добавление текста
                contentStream.showText("Festival Agenda"); // Заголовок
                contentStream.endText();

                // Начальные координаты и размеры таблицы
                float margin = 50;
                float yStart = 750;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float rowHeight = 20;
                float cellMargin = 5;

                // Заголовки столбцов
                String[] headers = {"Time", "Name", "Description", "Location"};
                float[] columnWidths = {150, 100, 200, 100};

                // Форматирование времени
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                // Рисуем заголовок таблицы
                float nextY = yStart;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                for (int i = 0; i < headers.length; i++) {
                    drawCell(contentStream, margin, nextY, columnWidths[i], rowHeight, headers[i], cellMargin, PDType1Font.HELVETICA, 10);
                    margin += columnWidths[i];
                }
                margin = 50; // Сбрасываем отступ
                nextY -= rowHeight;

                // Рисуем строки таблицы
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                for (Activity activity : activities) {
                    String time = activity.getStartTime().format(formatter) + " - " + activity.getEndTime().format(formatter);
                    String[] row = {
                            time,
                            activity.getName(),
                            activity.getDescription(),
                            activity.getLocation()
                    };

                    float maxCellHeight = 0;

                    // Первая итерация: вычисляем максимальную высоту ячейки
                    for (int i = 0; i < row.length; i++) {
                        float cellHeight = calculateCellHeight(row[i], columnWidths[i], PDType1Font.HELVETICA, 10, cellMargin);
                        maxCellHeight = Math.max(maxCellHeight, cellHeight);
                    }

                    // Вторая итерация: рисуем ячейки с одинаковой высотой
                    for (int i = 0; i < row.length; i++) {
                        drawCell(contentStream, margin, nextY, columnWidths[i], maxCellHeight, row[i], cellMargin, PDType1Font.HELVETICA, 10);
                        margin += columnWidths[i];
                    }

                    margin = 50; // Сбрасываем отступ
                    nextY -= maxCellHeight; // Уменьшаем Y с учетом высоты строки

                    // Если не хватает места на странице, добавляем новую страницу
                    if (nextY <= margin) {
                        contentStream.close(); // Закрываем текущий поток
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page); // Новый поток для новой страницы
                        nextY = yStart;
                    }

                }
            } finally {
                contentStream.close(); // Гарантированное закрытие потока
            }

            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании PDF агенды", e);
        }
    }

    private float calculateCellHeight(String text, float columnWidth, PDType1Font font, float fontSize, float cellMargin) throws IOException {
        List<String> lines = wrapText(text, font, fontSize, columnWidth - 2 * cellMargin);
        return lines.size() * (fontSize * 1.5f); // Высота строки умножается на количество строк
    }

    private float drawCell(PDPageContentStream contentStream, float x, float y, float width, float rowHeight, String text, float cellMargin, PDType1Font font, float fontSize) throws IOException {
        // Подготовка текста для отображения
        List<String> lines = wrapText(text, font, fontSize, width - 2 * cellMargin);

        // Общая высота ячейки (зависит от количества строк текста)
        float cellHeight = lines.size() * (fontSize * 1.5f);


        // Координаты для текста
        float textX = x + cellMargin;
        float textY = y - cellMargin - fontSize;

        // Рисуем текст построчно
        for (String line : lines) {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(textX, textY);
            contentStream.showText(line);
            contentStream.endText();
            textY -= fontSize * 1.5f; // Интерлиньяж
        }

        return cellHeight; // Возвращаем фактическую высоту ячейки
    }


    private List<String> wrapText(String text, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String testLine = line.length() > 0 ? line + " " + word : word;
            float textWidth = font.getStringWidth(testLine) / 1000 * fontSize;

            if (textWidth > maxWidth) {
                lines.add(line.toString().trim());
                line = new StringBuilder(word);
            } else {
                line.append(word).append(" ");
            }
        }

        if (line.length() > 0) {
            lines.add(line.toString().trim());
        }

        return lines;
    }


    private String truncateText(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }

    public byte[] generatePdf(String data) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Создание страницы
            PDPage page = new PDPage();
            document.addPage(page);

            // Запись текста на страницу
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Динамическая информация: " + data);
                contentStream.endText();
            }

            // Сохранение в ByteArrayOutputStream
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании PDF", e);
        }
    }


    public byte[] generateEventAnalyticsPDF(Event event) {
        return null;
    }

    public byte[] downloadEventStatisticsPDF(Event event) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                // Начальные координаты
                float x = 50;
                float y = 750;
                float lineHeight = 20;

                // Заголовок
                contentStream.beginText();
                contentStream.newLineAtOffset(x, y);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.showText("Event Statistics");
                contentStream.endText();

                y -= lineHeight;


                // Информация о событии
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                addText(contentStream, "Name: " + event.getName(), x, y);
                y -= lineHeight;

                addText(contentStream, "Max Participants: " + event.getMaxParticipants(), x, y);
                y -= lineHeight;

                addText(contentStream, "Participants: " + event.getParticipants(), x, y);
                y -= lineHeight;

                addText(contentStream, "Rating: " + event.getRating(), x, y);
                y -= lineHeight;

                addText(contentStream, "Public: " + (event.getIsPublic() ? "Yes" : "No"), x, y);
            }

            document.save(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании PDF для события", e);
        }
    }

    public byte[] generateEventPDF(Event event) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                // Начальные координаты
                float x = 50;
                float y = 750;
                float lineHeight = 20;

                // Заголовок
                contentStream.beginText();
                contentStream.newLineAtOffset(x, y);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.showText("Event Details");
                contentStream.endText();

                y -= lineHeight;

                // Форматирование времени
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                // Информация о событии
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                addText(contentStream, "Name: " + event.getName(), x, y);
                y -= lineHeight;

                addText(contentStream, "Description: " + event.getDescription(), x, y);
                y -= lineHeight;

                addText(contentStream, "Max Participants: " + event.getMaxParticipants(), x, y);
                y -= lineHeight;

                addText(contentStream, "Participants: " + event.getParticipants(), x, y);
                y -= lineHeight;

                addText(contentStream, "Place: " + event.getPlace(), x, y);
                y -= lineHeight;

                addText(contentStream, "Date: " + event.getDate().format(formatter), x, y);
                y -= lineHeight;

                addText(contentStream, "Public: " + (event.getIsPublic() ? "Yes" : "No"), x, y);
            }

            document.save(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании PDF для события", e);
        }
    }

    private void addText(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }
}
