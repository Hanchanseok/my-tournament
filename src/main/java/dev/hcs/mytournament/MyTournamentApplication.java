package dev.hcs.mytournament;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class MyTournamentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyTournamentApplication.class, args);
    }

}
