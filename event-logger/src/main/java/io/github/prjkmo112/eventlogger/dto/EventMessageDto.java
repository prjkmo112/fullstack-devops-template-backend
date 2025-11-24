package io.github.prjkmo112.eventlogger.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class EventMessageDto {
    @NotBlank
    private String key;

    @NotBlank
    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("ip_address")
    private String ipAddress;

    @JsonProperty("user_id")
    private String userId;

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
