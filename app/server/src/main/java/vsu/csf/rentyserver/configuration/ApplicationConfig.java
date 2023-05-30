package vsu.csf.rentyserver.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import vsu.csf.rentyserver.configuration.properties.SecurityProperties;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public record ApplicationConfig (

        @NotNull SecurityProperties security,

        @NotNull Duration rentCheckDelay

) {

    @Bean
    public SecurityProperties securityProperties() {
        return security;
    }

    @Bean(name = "rentCheckDelay")
    public Duration rentCheckDelay() {
        return rentCheckDelay;
    }

}
