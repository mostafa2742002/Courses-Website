// package com.web.ecommerce.configuration;

// import java.util.concurrent.TimeUnit;

// import org.springframework.cache.Cache;
// import org.springframework.cache.CacheManager;
// import org.springframework.cache.annotation.EnableCaching;
// import org.springframework.cache.concurrent.ConcurrentMapCache;
// import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import com.google.common.cache.CacheBuilder;


// @Configuration
// @EnableCaching
// public class CacheConfig {

//     @Bean
//     public CacheManager cacheManager() {
//         return new ConcurrentMapCacheManager("home", "recentlyAdded", "topSelling") {
//             @Override
//             protected Cache createConcurrentMapCache(String name) {
//                 return new ConcurrentMapCache(name, CacheBuilder.newBuilder()
//                         .expireAfterWrite(30, TimeUnit.MINUTES)
//                         .maximumSize(500)
//                         .build().asMap(), false);
//             }
//         };
//     }
// }
