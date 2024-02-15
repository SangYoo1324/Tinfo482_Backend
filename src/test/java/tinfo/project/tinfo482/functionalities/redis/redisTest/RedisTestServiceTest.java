package tinfo.project.tinfo482.functionalities.redis.redisTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.ActiveProfiles;
import tinfo.project.tinfo482.exceptions.DataNotFoundException;
import tinfo.project.tinfo482.functionalities.redis.RedisUtilService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@EnableCaching
@Slf4j
class RedisTestServiceTest {

    @Autowired
    private RedisTestService redisTestService;
    @Autowired
    private RedisUtilService redisUtilService;

    @Test
    void post_testRedisObj() {
        redisTestService.post_testRedisObj(1l);
        redisTestService.post_testRedisObj(2l);
        redisTestService.post_testRedisObj(3l);
        redisTestService.post_testRedisObj(4l);
    }


    @Test
    void fetchSingleRedisTestObj(){
        redisTestService.fetchSingleRedisTestObj(27l);
    }


    @Test
    void fetchRedisTestObjList() {
        List<RedisTestObj> objs = redisTestService.fetchRedisTestObjList("type", "testObjList");
       log.info(String.valueOf(objs.size()));
       log.info(objs.get(0).toString());

    }

    @Test
    void fetchRedisTestObjList_separate() {
        redisTestService.fetchRedisTestObjList_separate("type");
    }


    @Test
    void deleteSingleRedisTestObj() {
        try {
            redisTestService.deleteSingleRedisTestObj(23l);
        } catch (DataNotFoundException e) {
           e.printStackTrace();
        }
    }

    @Test
    void updateRedisTestObjList() {

        try {
            redisTestService.updateRedisTestObjList("img_url_update_2");
        } catch (DataNotFoundException e) {
            e.printStackTrace();
        }
    }
}