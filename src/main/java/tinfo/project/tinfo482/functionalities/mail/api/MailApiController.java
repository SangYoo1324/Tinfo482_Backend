package tinfo.project.tinfo482.functionalities.mail.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tinfo.project.tinfo482.functionalities.mail.dto.MailDto;
import tinfo.project.tinfo482.functionalities.mail.service.MailService;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class MailApiController {

    private final MailService mailService;

    @PostMapping("/send")
    public String sendSimpleMail(@RequestBody MailDto mailDto){
        List<String> receivers = new ArrayList<String>();
        receivers.add(mailDto.getTo());
     mailService.sendDirectMail(mailDto, receivers);
     return "Email successfully sent to "+ mailDto.getTo();
    }

}
