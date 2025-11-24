package io.github.prjkmo112.commoneventlogger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maxmind.geoip2.model.CityResponse;
import io.github.prjkmo112.commonembeddeddata.MaxMindService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventLoggerBase {
    @Value("${event-log.common}")
    private String eventCommonLogUrl;

    @Value("${test-dummy-vars.remote-ip}")
    private String testDummyVarsRemoteIp;

    private final WebClient webClient;
    private final RestClient restClient;
    private final MaxMindService maxMindService;

    public void message(Dto dto) {
        try {
            setGeoInfo(dto);

            webClient
                    .post()
                    .uri(eventCommonLogUrl)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .doOnError(e -> log.warn("이벤트 전송 실패: eventType={}, key={}", dto.getEventType(), dto.getKey(), e))
                    .onErrorResume(e -> Mono.empty())
                    .subscribe();
        } catch (Exception e) {
            log.warn("이벤트 전송 실패: eventType={}, key={}", dto.getEventType(), dto.getKey(), e);
        }
    }

    public void messageSync(Dto dto) {
        try {
            setGeoInfo(dto);

            restClient
                    .post()
                    .uri(eventCommonLogUrl)
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("이벤트 전송 실패 (sync): eventType={}, key={}", dto.getEventType(), dto.getKey(), e);
        }
    }

    private void setGeoInfo(Dto dto) throws UnknownHostException {
        String ipAddress = dto.getIpAddress();
        InetAddress inetAddress = InetAddress.getByName(ipAddress);
        if (inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress()) {
            ipAddress = testDummyVarsRemoteIp;
        }

        CityResponse cityInfo = maxMindService.geoIpInfo(ipAddress);

        dto.setCountryCode(cityInfo.getCountry().getIsoCode());
        dto.setCountryName(cityInfo.getCountry().getName());
        dto.setCityName(cityInfo.getCity().getName());
        dto.setLatitude(cityInfo.getLocation().getLatitude());
        dto.setLongitude(cityInfo.getLocation().getLongitude());
        dto.setTimezone(cityInfo.getLocation().getTimeZone());
    }

    @Getter
    @Setter
    @Builder
    public static class Dto {
        @NotNull
        @NotBlank
        private String key;

        @NotNull
        @NotBlank
        @JsonProperty("event_type")
        private String eventType;

        @JsonProperty("ip_address")
        private String ipAddress;

        @JsonProperty("user_id")
        private String userId;

        @NotNull
        @NotBlank
        private String value;

        private String attrs;

        // 위치정보 필드
        @JsonProperty("country_code")
        private String countryCode;

        @JsonProperty("country_name")
        private String countryName;

        @JsonProperty("city_name")
        private String cityName;

        private Double latitude;
        private Double longitude;
        private String timezone;
    }
}
