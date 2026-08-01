package kahoot.clabs.kahoot_clabs.shared.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Restricts JPA entity and Spring Data repository scanning to infrastructure packages.
 * Domain classes must never be annotated with {@code @Entity}.
 */
@Configuration
@EnableJpaRepositories(basePackages = {
        "kahoot.clabs.kahoot_clabs.identity.infrastructure",
        "kahoot.clabs.kahoot_clabs.organization.infrastructure",
        "kahoot.clabs.kahoot_clabs.quiz.infrastructure"
})
@EntityScan(basePackages = {
        "kahoot.clabs.kahoot_clabs.identity.infrastructure",
        "kahoot.clabs.kahoot_clabs.organization.infrastructure",
        "kahoot.clabs.kahoot_clabs.quiz.infrastructure"
})
public class JpaConfig {
}
