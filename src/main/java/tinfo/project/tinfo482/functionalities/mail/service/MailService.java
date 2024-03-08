package tinfo.project.tinfo482.functionalities.mail.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import io.micrometer.observation.Observation;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import tinfo.project.tinfo482.dto.transaction.ReceiptData;
import tinfo.project.tinfo482.entity.transaction.Receipt;
import tinfo.project.tinfo482.functionalities.mail.dto.MailDto;
import tinfo.project.tinfo482.functionalities.redis.RedisUtilService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MailService {

    private final JavaMailSender javaMailSender;

    private final RedisUtilService redisUtilService;

    private final TemplateEngine templateEngine;


    public void sendMail(MailDto mailDto, MultipartFile multipartFile){
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true,"UTF-8");
            mimeMessageHelper.setTo(mailDto.getTo());
            mimeMessageHelper.setFrom("no-reply");
            mimeMessageHelper.setSubject(mailDto.getSubject());
            mimeMessageHelper.setText(mailDto.getMessage(),true);

            // file attachment(only multipartFileList not null)
            if(multipartFile !=null){
                mimeMessageHelper.addAttachment("attachment",  multipartFile);
            }
            javaMailSender.send(mimeMessage);
            log.info("Email Successfully sent to"+mimeMessage.getReplyTo());
        }catch(MessagingException e){
            log.info("fail to send email");
            throw new RuntimeException(e);
        }
    }
    // currently only accept receivers  with 1 element
    public void sendDirectMail(MailDto mailDto, List<String> receivers){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        try{




            simpleMailMessage.setTo(receivers.get(0));


            // 2. 메일 제목 설정
            simpleMailMessage.setSubject(mailDto.getSubject());

            // 3. 메일 내용 설정
            simpleMailMessage.setText(mailDto.getMessage());

            // 4. 메일 전송
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e){
            e.printStackTrace();
            log.info("direct email not sent. something went wrong");
        }

    }

    public void sendAuthEmail(String email){
        // make random verificationCode   111111~999999
        Random random = new Random();
        String authKey = String.valueOf(random.nextInt(888888) + 111111);

        String subject = "Your Verification Code for 2FA...";
        String text = "Verification Code: "+ authKey+"</br>";

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text,true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

            // set exp time for verification Code
            redisUtilService.setDataExpire(authKey, email, 60 * 5L);
    }

    public byte[] pdfGenerator(Receipt receipt){





        Context context = new Context();
        context.setVariable("products",   receipt.toReceiptDto());
        String htmlContent = templateEngine.process("receipt", context);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);

        ConverterProperties properties = new ConverterProperties();

        HtmlConverter.convertToPdf(htmlContent,pdf, properties);

        pdf.close();

        byte[] pdfBytes = baos.toByteArray();

        return pdfBytes;

    }

}
