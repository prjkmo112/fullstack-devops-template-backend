package io.github.prjkmo112.commonembeddeddata;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Service
@ConditionalOnBean(DatabaseReader.class)
public class MaxMindService {
    private final DatabaseReader databaseReader;

    public MaxMindService(DatabaseReader databaseReader) {
        this.databaseReader = databaseReader;
    }

    public CityResponse geoIpInfo(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            return databaseReader.city(ipAddress);
        } catch (Exception e) {
            return null;
        }
    }
}
