package kahoot.clabs.kahoot_clabs.identity.infrastructure.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.identity.application.event.UserReadModelDeletedEvent;
import kahoot.clabs.kahoot_clabs.identity.application.event.UserReadModelUpsertedEvent;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.UserReadModels;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.mapper.UserPersistenceMapper;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.jpa.UserJpaRepository;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public JpaUserRepositoryAdapter(
            UserJpaRepository jpaRepository,
            ApplicationEventPublisher eventPublisher) {
        this.jpaRepository = jpaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User save(User user) {
        User saved = UserPersistenceMapper.toDomain(jpaRepository.save(UserPersistenceMapper.toEntity(user)));
        eventPublisher.publishEvent(new UserReadModelUpsertedEvent(UserReadModels.from(saved)));
        return saved;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmailIgnoreCase(email).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public void delete(User user) {
        jpaRepository.deleteById(user.getId());
        eventPublisher.publishEvent(new UserReadModelDeletedEvent(user.getId()));
    }
}
