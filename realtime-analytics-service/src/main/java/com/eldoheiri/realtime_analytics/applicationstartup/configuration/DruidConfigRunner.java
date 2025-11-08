package com.eldoheiri.realtime_analytics.applicationstartup.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DruidConfigRunner implements CommandLineRunner {
    private final DruidConfigService druidConfigService;

    public DruidConfigRunner(DruidConfigService druidConfigService) {
        this.druidConfigService = druidConfigService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==============================================");
        System.out.println("  Druid Configuration Startup Initiated");
        System.out.println("==============================================");

        // ADDED: Wait for 15 seconds to ensure Docker containers (especially Druid Router) are fully ready
        System.out.println("Waiting 15 seconds for Druid Router service (localhost:8888) to start...");
        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Startup delay interrupted.");
        }

        // 1. Set the retention policy (Coordinator API)
        druidConfigService.configureRetentionPolicy();

        // 2. Start the Kafka Supervisor (Overlord API)
        druidConfigService.configureSupervisor();

        System.out.println("==============================================");
        System.out.println("  Druid Configuration Startup Complete");
        System.out.println("==============================================");
    }
}
