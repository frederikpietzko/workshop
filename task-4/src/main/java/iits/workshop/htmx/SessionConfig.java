package iits.workshop.htmx;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@Configuration
@EnableJdbcHttpSession
public class SessionConfig {
    // Session configuration is now handled via application.yaml
    // This class simply enables JDBC-backed HTTP sessions
}
