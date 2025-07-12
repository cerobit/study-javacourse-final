package co.com.bancolombia.mongo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.PropertiesMongoConnectionDetails;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public MongoDBSecret dbSecret(@Value("${spring.data.mongodb.uri}") String uri) {
        return MongoDBSecret.builder()
                .uri(uri)
                .build();
    }

    @Bean
    public MongoConnectionDetails mongoProperties(MongoDBSecret secret, SslBundles sslBundles) {
        MongoProperties properties = new MongoProperties();
        properties.setUri(secret.getUri());
        return new PropertiesMongoConnectionDetails(properties, sslBundles);
    }
}
