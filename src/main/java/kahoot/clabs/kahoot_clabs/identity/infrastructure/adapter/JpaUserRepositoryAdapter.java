package kahoot.clabs.kahoot_clabs.identity.infrastructure.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.identity.application.port.UserProjectionPort;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.UserReadModels;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.mapper.UserPersistenceMapper;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.jpa.UserJpaRepository;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final ObjectProvider<UserProjectionPort> userProjectionPort;

    public JpaUserRepositoryAdapter(
            UserJpaRepository jpaRepository,
            ObjectProvider<UserProjectionPort> userProjectionPort) {
        this.jpaRepository = jpaRepository;
        this.userProjectionPort = userProjectionPort;
    }

    @Override
    public User save(User user) {
        User saved = UserPersistenceMapper.toDomain(jpaRepository.save(UserPersistenceMapper.toEntity(user)));
        // userProjectionPort.ifAvailable(port -> port.save(UserReadModels.from(saved)));
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
        userProjectionPort.ifAvailable(port -> port.deleteById(user.getId()));
    }
}
