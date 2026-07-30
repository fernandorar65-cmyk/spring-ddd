package kahoot.clabs.kahoot_clabs.shared.infrastructure.seed;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(100)
@EnableConfigurationProperties(SeedProperties.class)
public class SeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedRunner.class);

    private final SeedProperties properties;
    private final List<DataSeeder> seeders;

    public SeedRunner(SeedProperties properties, List<DataSeeder> seeders) {
        this.properties = properties;
        this.seeders = seeders;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!properties.isEnabled()) {
            log.info("Data seeders disabled (app.seed.enabled=false)");
            return;
        }
        if (seeders.isEmpty()) {
            log.debug("No DataSeeder beans registered");
            return;
        }

        List<DataSeeder> ordered = seeders.stream()
                .sorted(Comparator.comparingInt(DataSeeder::order))
                .toList();

        log.info("Running {} data seeder(s)", ordered.size());
        for (DataSeeder seeder : ordered) {
            log.info("Seeding: {} (order={})", seeder.name(), seeder.order());
            seeder.seed();
        }
        log.info("Data seeders finished");
    }
}
