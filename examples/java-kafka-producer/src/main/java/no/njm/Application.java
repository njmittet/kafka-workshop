package no.njm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class Application {

    static final String WORKSHOP_TOPIC = "workshop.messages.partitioned";

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
