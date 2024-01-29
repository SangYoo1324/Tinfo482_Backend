package tinfo.project.tinfo482;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DockerController {
    @Value("${login.persist.time}")
    private String login_persist_time;
    @Value("${server.env}") // blue, green, local
    private String env;
    @Value("${server.port}") // 8080,8081
    private String serverPort;
    @Value("${server.serverAddress}")  // ec2 탄력적 ip 주소 , localhost
    private String serverAddress;
    @Value("${serverName}") // local_server, green_server, blue_server
    private String serverName;


    @GetMapping("/hc")
    public ResponseEntity<?> healthCheck(){

        Map<String,String> resp = new HashMap<>();
        resp.put("serverName", serverName);
        resp.put("serverAddress", serverAddress);
        resp.put("serverPort", serverPort);
        resp.put("env", env);

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @GetMapping("/env")
    public ResponseEntity<?> getEnv(){

        System.out.println("Test for common profile applied"+login_persist_time);

        return ResponseEntity.status(HttpStatus.OK).body(env);
    }

}
