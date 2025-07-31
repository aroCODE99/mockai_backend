package com.mockAi.MOCAI.Scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

public class RefreshTokenScheduling {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenScheduling.class);
    private final JdbcTemplate jdbc;

    public RefreshTokenScheduling(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Scheduled(fixedRate = 60000)
    public void deleteExpiredToken() {

        try {
            String query = "DELETE FROM refresh_token WHERE expiry_date < NOW()";

            int rowAffected = jdbc.update(query);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
