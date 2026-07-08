package id.my.hendisantika.compose;

import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootDockerComposeMysqlApplication {

    public static void main(String[] args) {
        // Run Flyway migrations explicitly before starting Spring so migrations
        // are applied prior to Hibernate/JPA schema actions.
        String url = System.getenv().getOrDefault("SPRING_DATASOURCE_URL", "jdbc:mysql://mysqldb:3306/userDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String user = System.getenv().getOrDefault("SPRING_DATASOURCE_USERNAME", System.getenv().getOrDefault("SPRING_DATASOURCE_USER", "root"));
        String pass = System.getenv().getOrDefault("SPRING_DATASOURCE_PASSWORD", System.getenv().getOrDefault("MYSQL_PASSWORD", "S3cret"));

        try {
            Flyway.configure()
                    .dataSource(url, user, pass)
                    .baselineOnMigrate(true)
                    .locations("classpath:db/migration")
                    .load()
                    .migrate();
        } catch (Exception e) {
            System.err.println("Flyway migration failed: " + e.getMessage());
            e.printStackTrace();
            // proceed to start the app; logs will show the failure
        }

        SpringApplication.run(SpringBootDockerComposeMysqlApplication.class, args);
    }

}
