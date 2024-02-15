package tinfo.project.tinfo482.functionalities.redis.redisTest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.AbstractCache;
import com.nimbusds.jose.shaded.gson.Gson;
import io.lettuce.core.support.caching.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import tinfo.project.tinfo482.exceptions.DataNotFoundException;
import tinfo.project.tinfo482.functionalities.redis.RedisUtilService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class RedisTestService {

    private final RedisTestObjRepository redisTestObjRepository;
    private final RedisUtilService redisUtilService;

    private final CacheManager cacheManager;


    private final RedisTemplate redisTemplate;


    // getting it from cache if already exist
//    @Cacheable(cacheNames = "type", key = "'testObjList'")
    public List<RedisTestObj> fetchRedisTestObjList(String cacheName, String cacheKey){

        Cache cache = cacheManager.getCache(cacheName);
        Cache.ValueWrapper valueWrapper = cache !=null? cache.get(cacheKey):null;


        // if cache already exist, don't use db. just bring it from cache
        if(valueWrapper != null){
            log.info("cache already exist.. getting it from cache");
            // bring data from cache
            return (List<RedisTestObj>) valueWrapper.get();
        }else{
            List<RedisTestObj> dataFromDB =  redisTestObjRepository.findAll();
            log.info("cache doesn't exist.. getting it from DB");

            cache.put(cacheKey,dataFromDB);



            return redisTestObjRepository.findAll();

        }

    }

    public List<RedisTestObj> fetchRedisTestObjList_separate(String cacheName){

        Cache cache = cacheManager.getCache(cacheName);

        // cache가 이미 존재한다면,
        if(cache !=null){
            List<RedisTestObj> findAll = new ArrayList<>();
            Set<String> keys = redisTemplate.keys("type::*");
            keys.stream().forEach(e->{
                log.info("e::"+e.split("::")[1]);

               findAll.add((RedisTestObj) cache.get(e.split("::")[1]).get());
            });

            return findAll;
        }else{

            List<RedisTestObj> objs=  redisTestObjRepository.findAll();
            log.info("loading data with DB access...");
            objs.forEach(e->{
                cache.put(e.getId(), e);
            });
            return objs;
        }

    }


    @Cacheable(cacheNames = "type", key="'fetchSingle'+#id")
    public RedisTestObj fetchSingleRedisTestObj(Long id){
        return redisTestObjRepository.findById(id).orElse(null);
    }


    @Cacheable(cacheNames = "type", key= "'testObjList'")
    public List<RedisTestObj> deleteSingleRedisTestObj(Long id) throws DataNotFoundException {
      RedisTestObj redisTestObj = redisTestObjRepository.findById(id).orElseThrow(()-> new DataNotFoundException("cannot find the target data"));
        redisTestObjRepository.delete(redisTestObj);

        return redisTestObjRepository.findAll();
    }


    @CachePut(cacheNames = "type", key="'testObjList'")
    public List<RedisTestObj> updateRedisTestObjList(String update_string) throws DataNotFoundException {
       RedisTestObj target = redisTestObjRepository.findById(27l).orElseThrow(()-> new DataNotFoundException("cannot find data"));
       target.setImg_url(update_string);

       redisTestObjRepository.save(target);

       return redisTestObjRepository.findAll();
    }


    public void post_testRedisObj(Long number){
        RedisTestObj target = RedisTestObj.builder()
                .content("testRedisContent"+number)
                .price(1l)
                .stock(10)
                .delivery(false)
                .name("testRedis name"+number)
                .build();
        redisTestObjRepository.save(target);
    }
}


