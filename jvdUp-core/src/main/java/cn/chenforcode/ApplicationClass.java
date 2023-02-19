package cn.chenforcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * @author yumu
 * @date 2023/2/17 12:29
 * @description
 */
@EnableNeo4jRepositories
@SpringBootApplication(scanBasePackages = "cn.chenforcode")
@EntityScan("cn.chenforcode.pojo.entity")
public class ApplicationClass {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationClass.class, args);
    }
}
