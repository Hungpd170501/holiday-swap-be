package com.example.holidayswap.config;

import com.example.holidayswap.service.lock.RedissonDistributeLocker;
import com.example.holidayswap.utils.RedissonLockUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Config.class)
public class RedissonConfig {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private String port;


    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port);
        return Redisson.create(config);
    }

//    @Bean
//    RedissonClient redissonSingle() throws IOException {
//        Config config = Config.fromYAML(new File("singleNodeConfig.yml"));
//        return Redisson.create(config);
//    }


    @Bean
    RedissonDistributeLocker redissonLocker(RedissonClient redissonClient) {
        RedissonDistributeLocker locker = new RedissonDistributeLocker(redissonClient);
        RedissonLockUtils.setLocker(locker);
        return locker;
    }
}