package com.example.payments;

import com.example.payments.engine.CoreEngine;
import com.example.payments.config.ConfigLoader;
import com.example.payments.config.RunConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception {
        // Simple arg support: --config=...
        String configPath = "src/main/resources/config.yaml";
        for (String a : args) {
            if (a.startsWith("--config=")) configPath = a.substring("--config=".length());
        }

        RunConfig config = ConfigLoader.load(configPath);
        CoreEngine engine = new CoreEngine();
        engine.run(config);

        // If you want Spring context, enable below
        // SpringApplication.run(Application.class, args);
    }
}
