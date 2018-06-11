package com.github.wechat.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

import com.github.common.swagger.EnableMySwagger;



@EnableMySwagger
@EnableDiscoveryClient
@EnableFeignClients
@EnableEurekaClient
@RefreshScope
@SpringBootApplication
@MapperScan({"com.github.model.dao.*"})
public class WeChatApplication 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(WeChatApplication.class, args);
    }
}
