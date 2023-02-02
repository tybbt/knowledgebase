package com.tybbt.knowledgebase.config;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

// 使用SpringBootApplication 注解中使用了@ComponentScan注解，在启动时会扫描当前包和所有子包中的所有组件，
// 但当未处于同一个包，则需额外配置参数设定访问域("com.xxx..."),参数支持列表方式({"com.tybbt", "com.jie"})
@ComponentScan("com.tybbt.knowledgebase")
@SpringBootApplication
@MapperScan("com.tybbt.knowledgebase.mapper")
@EnableScheduling
public class KnowledgebaseApplication {
    private static final Logger LOG = LoggerFactory.getLogger(KnowledgebaseApplication.class);
    public static void main(String[] args) {

        //SpringApplication.run(KnowledgebaseApplication.class, args);
        SpringApplication app = new SpringApplication(KnowledgebaseApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        LOG.info("地址: \thttp://127.0.0.1:{}", env.getProperty("server.port"));
    }
}
