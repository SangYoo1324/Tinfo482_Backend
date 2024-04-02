package tinfo.project.tinfo482.functionalities.mail.service;


import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.workmail.AmazonWorkMailClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tinfo.project.tinfo482.functionalities.mail.dto.MailDto;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AWSEmailService {


    private final AmazonSimpleEmailService amazonSimpleEmailService;

    public void sendEmail(MailDto mailDto) {
        Destination destination = new Destination().withToAddresses(mailDto.getTo());
        Content subjectContent = new Content().withData(mailDto.getSubject());
        Content bodyContent = new Content().withData(mailDto.getMessage());
        Body messageBody = new Body().withText(bodyContent);
        Message message = new Message().withSubject(subjectContent).withBody(messageBody);
        SendEmailRequest request = new SendEmailRequest().withDestination(destination)
                .withMessage(message)
                .withSource("noreply@sangbeomec2.net");
        amazonSimpleEmailService.sendEmail(request);
    }
}
