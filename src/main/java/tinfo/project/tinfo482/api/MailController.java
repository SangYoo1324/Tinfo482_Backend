package tinfo.project.tinfo482.api;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tinfo.project.tinfo482.dto.transaction.ReceiptData;
import tinfo.project.tinfo482.functionalities.mail.dto.MailDto;
import tinfo.project.tinfo482.functionalities.mail.service.MailService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MailController {


   private final MailService mailService;

   @PostMapping("mime/mail")
   public ResponseEntity<?> sendMimeTypeMail(){
       ReceiptData data1 = ReceiptData.builder()
               .price(11l)
               .name("example1")
               .build();

       ReceiptData data2 = ReceiptData.builder()
               .price(11l)
               .name("example2")
               .build();

       List<ReceiptData> dataList = new ArrayList<>();
       dataList.add(data1);
       dataList.add(data2);

       HttpHeaders headers = new HttpHeaders();
       headers.setContentType(MediaType.APPLICATION_PDF);
       headers.setContentDispositionFormData("filename", "example.pdf");
       return ResponseEntity.ok().headers(headers).body(null);
   }

}
