package io.github.prjkmo112.commonembeddeddata;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Service
@RequiredArgsConstructor
public class MaxMindService {
    private final DatabaseReader databaseReader;

    public CityResponse geoIpInfo(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            return databaseReader.city(ipAddress);
        } catch (Exception e) {
            return null;
        }
    }
}
