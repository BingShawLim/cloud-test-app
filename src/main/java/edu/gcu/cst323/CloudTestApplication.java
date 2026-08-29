package edu.gcu.cst323;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CST-323 Cloud Test Application.
 * Entry point for the Contact Manager test application.
 */
@SpringBootApplication
public class CloudTestApplication {

    private static final Logger log = LoggerFactory.getLogger(CloudTestApplication.class);

    public static void main(String[] args) {
        log.info("CloudTestApplication.main() - ENTRY");
        SpringApplication.run(CloudTestApplication.class, args);
        log.info("CloudTestApplication.main() - EXIT - application started");
    }
}
