package tinfo.project.tinfo482.functionalities.redis;

import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import tinfo.project.tinfo482.exceptions.CacheNotFoundException;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisUtilService {

    private final RedisTemplate redisTemplate;

    private final CacheManager cacheManager;

    // string type  -- only for 2FA
    public String getData(String key){
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setData(String key, String value){
     ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    valueOperations.set(key, value);
    }

    public void setDataExpire(String key, String value, long duration){
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        Duration expDura = Duration.ofSeconds(duration);
        valueOperations.set(key,value,expDura);
    }
    // end: string type  -- only for 2FA 



    // register cache
    public Object registerCache(String cacheName, String cacheKey, Object dbData){
       Cache cache =  cacheManager.getCache(cacheName);
       // null if cache with current cacheName doesn't exist
       Cache.ValueWrapper valueWrapper = cache !=null? cache.get(cacheKey):null;

       // if cache already exist, don't use db. just bring it from cache
       if(valueWrapper != null){
           log.info("cache already exist.. getting it from cache...");
           return valueWrapper.get();
       }
        else{

            log.info("cache doesn't exist.. please handle this exception " +
                    "by fetching data from DB");

            cache.put(cacheKey,dbData);

            return dbData;
        }
    }

    public Object registerCache_override(String cacheName, String cacheKey, Object dbData){
        Cache cache =  cacheManager.getCache(cacheName);
        log.info("cache doesn't exist.. please handle this exception " +
                "by fetching data from DB");

        cache.put(cacheKey,dbData);

        return dbData;
    }

    // fetching Cache
    public Object fetchingCache(String cacheName, String cacheKey) throws CacheNotFoundException {
        Cache cache =  cacheManager.getCache(cacheName);
        // null if cache with current cacheName doesn't exist
        Cache.ValueWrapper valueWrapper = cache !=null? cache.get(cacheKey):null;

        // if cache already exist, don't use db. just bring it from cache
        if(valueWrapper != null){
            log.info("cache already exist.. getting it from cache...");
            return valueWrapper.get();
        }
        else{
            throw new CacheNotFoundException("Cannot find data from cache");
        }
    }




   public void deleteData(String key) {
    redisTemplate.delete(key);
    }



}
