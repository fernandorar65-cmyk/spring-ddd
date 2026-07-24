package kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.users.domain.model.User;
import kahoot.clabs.kahoot_clabs.users.domain.repository.UserRepository;
import kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.jpa.UserJpaRepository;
import kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.mapper.UserPersistenceMapper;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        return UserPersistenceMapper.toDomain(jpaRepository.save(UserPersistenceMapper.toEntity(user)));
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
    }
}
