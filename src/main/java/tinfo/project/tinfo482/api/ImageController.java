package tinfo.project.tinfo482.api;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tinfo.project.tinfo482.service.S3Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final S3Service s3Service;

    @PostMapping("/api/image/upload")
    public ResponseEntity<?> imageUpload(@RequestParam("upload") MultipartFile multipartFile){
        Map<String, Object> map = new HashMap<>();
        try {
            String url = s3Service.imageUpload(multipartFile);

            log.info("url:::"+ url);
            map.put("image",url);
            return ResponseEntity.status(HttpStatus.OK).body(map);

        } catch (IOException e) {
           e.printStackTrace();
        }
        map.put("image","null");
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
    }

    @PostMapping("/api/image/ck5/upload")
    public ResponseEntity<?> ck5imageUpload(@RequestParam("upload") MultipartFile multipartFile){
        Map<String, Object> map = new HashMap<>();
        try {
            String url = s3Service.imageUpload(multipartFile);

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("uploaded",1);
            data.put("fileName", url);
            data.put("url", url);

            log.info("url:::"+ url);
            map.put("image",url);
            return ResponseEntity.status(HttpStatus.OK).body(data);

        } catch (IOException e) {
            e.printStackTrace();
        }
        map.put("image","null");
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
    }

}
