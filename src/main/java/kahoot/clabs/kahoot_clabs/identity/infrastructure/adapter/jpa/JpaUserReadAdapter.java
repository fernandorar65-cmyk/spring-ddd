package kahoot.clabs.kahoot_clabs.identity.infrastructure.adapter.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.identity.application.port.UserProjectionPort;
import kahoot.clabs.kahoot_clabs.identity.application.port.UserReadPort;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.UserReadModel;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.UserReadModels;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.mapper.UserPersistenceMapper;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.jpa.UserJpaRepository;

@Repository
@Profile("test")
public class JpaUserReadAdapter implements UserReadPort, UserProjectionPort {

    private final UserJpaRepository userJpaRepository;

    public JpaUserReadAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<UserReadModel> findById(UUID id) {
        return userJpaRepository.findById(id)
                .map(UserPersistenceMapper::toDomain)
                .map(UserReadModels::from);
    }

    @Override
    public Optional<UserReadModel> findByEmail(String email) {
        return userJpaRepository.findByEmailIgnoreCase(email)
                .map(UserPersistenceMapper::toDomain)
                .map(UserReadModels::from);
    }

    @Override
    public void save(UserReadModel readModel) {
        // no-op in tests
    }

    @Override
    public void deleteById(UUID id) {
        // no-op
    }
}
