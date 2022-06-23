package com.tutorial.apidemo.database;

import com.tutorial.apidemo.models.Product;
import com.tutorial.apidemo.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//connect with mysql using JPA
/*
docker run -d --rm --name mysql-spring-boot-project
-e MYSQL_ROOT_PASSWORD=123456
-e MYSQL_USER=phunv
-e MYSQL_PASSWORD=123456
-e MYSQL_DATABASE=test_db
-p 3309:3306
--volume mysql-spring-boot-project-volume:/var/lib/mysql
mysql: latest

mysql -h localhost -P 3309 --protocol=tcp -u phunv -p
*/

@Configuration
public class Database {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository){
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
//                Product productA = new Product("Macbook Pro", 2020, 2000.0, "");
//                Product productB = new Product("Macbook Air", 2021, 1000.0, "");
//                logger.info("Insert data: " + productRepository.save(productA));
//                logger.info("Insert data: " + productRepository.save(productB));
            }
        };
    }
}
