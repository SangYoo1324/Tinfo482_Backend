package tinfo.project.tinfo482.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tinfo.project.tinfo482.config.S3Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Config s3Config;
//    private String localLocation = "C:\\Users\\samue\\Downloads\\";
    @Value("${s3.local.path}")
    private String localLocation;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;


    public String imageUpload(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.indexOf("."));
        log.info("File Extention = " + ext);

        String uuidFileName = UUID.randomUUID() +ext;
        String localPath = localLocation + uuidFileName;

        File localFile = new File(localPath);
        file.transferTo(localFile);

        s3Config.amazonS3Client()
                .putObject(new PutObjectRequest(bucketName,uuidFileName,localFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead));// ACL setup public readable
        // get uploaded image url
        String s3Url = s3Config.amazonS3Client().getUrl(bucketName, uuidFileName).toString();

        localFile.delete();

        return s3Url;
    }

}
