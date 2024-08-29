package com.walletsystem;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WalletSystemApplication {

    public static void main(String[] args) {
        loadEnvironmentVariables();
        SpringApplication.run(WalletSystemApplication.class, args);

    }

    private static void loadEnvironmentVariables() {
        //Dotenv dotenv = Dotenv.load();
        Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
        if (dotenv.entries().isEmpty()) {
            // Fallback to the production .env file if the default one is empty
            dotenv = Dotenv.configure()
                    .directory("/var/www/html/dev") // specify the directory for production
                    .load();
        }
        String[] envVariables = {
                "SERVER_PORT",
                "DATABASE_URL",
                "DATABASE_USERNAME",
                "DATABASE_PASSWORD",
                "JWT_SECRET_KEY",
                "JWT_EXPIRATION",
                "JWT_REFRESH_TOKEN_EXPIRATION",
        };

        for (String envVar : envVariables) {
            String value = dotenv.get(envVar);
            if (value != null) {
                System.setProperty(envVar, value);
            } else {
                System.out.println("Environment variable " + envVar + " is not set in .env");
            }
        }

    }

}
