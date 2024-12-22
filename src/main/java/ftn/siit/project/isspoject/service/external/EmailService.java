package ftn.siit.project.isspoject.service.external;
import ftn.siit.project.isspoject.dto.EmailDetails;
import ftn.siit.project.isspoject.entity.User;

public interface EmailService {

    // Method
    // To send a simple email
    String sendSimpleMail(EmailDetails details);
    String sendMail(User recipient, String subject, String text);
    // Method
    // To send an email with attachment
    String sendMailWithAttachment(EmailDetails details);
}
