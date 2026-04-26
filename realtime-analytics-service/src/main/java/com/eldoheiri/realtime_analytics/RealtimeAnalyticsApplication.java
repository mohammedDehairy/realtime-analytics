package com.eldoheiri.realtime_analytics;

import java.util.Arrays;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.eldoheiri.realtime_analytics.messaging.AnalyticsEventPublisher;
import com.eldoheiri.realtime_analytics.kafka.producer.MessageQueue;
import com.eldoheiri.realtime_analytics.security.authentication.JWTUtil;
import com.eldoheiri.realtime_analytics.security.idgeneration.IdentifierUtil;
import com.eldoheiri.realtime_analytics.tinybird.producer.TinybirdMessageQueue;
import com.eldoheiri.realtime_analytics.services.DeviceService;
import com.eldoheiri.realtime_analytics.services.HeartBeatService;
import com.eldoheiri.realtime_analytics.services.SessionService;

@SpringBootApplication
public class RealtimeAnalyticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealtimeAnalyticsApplication.class, args);
	}

    @Bean
    JWTUtil jwtUtil() {
		return new JWTUtil();
	}

	@Bean
	IdentifierUtil identifierUtil() {
		return new IdentifierUtil();
	}

    @Bean
    AnalyticsEventPublisher<Map<String, Object>> heartBeatMessageQueue() {
		String backend = System.getenv().getOrDefault("EVENT_PUBLISHER_BACKEND", "kafka");
		if ("tinybird".equalsIgnoreCase(backend)) {
			return new TinybirdMessageQueue<>(
				System.getenv("TINYBIRD_EVENTS_URL"),
				System.getenv("TINYBIRD_TOKEN"),
				System.getenv().getOrDefault("TINYBIRD_DATASOURCE", "realtime_events")
			);
		}
		return new MessageQueue<>(System.getenv().getOrDefault("KAFKA_TOPIC", "realtime-events"));
    }

    @Bean
    SessionService sessionService() {
		return new SessionService();
	}

    @Bean
    HeartBeatService heartBeatService() {
		return new HeartBeatService();
	}

    @Bean
    DeviceService deviceService() {
		return new DeviceService();
	}

    @Bean
    CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			System.out.println("printing all beans");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}
		};
	}
}
