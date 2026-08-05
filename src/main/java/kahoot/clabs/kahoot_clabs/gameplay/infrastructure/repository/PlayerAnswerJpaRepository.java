package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.PlayerAnswerEntity;

public interface PlayerAnswerJpaRepository extends JpaRepository<PlayerAnswerEntity, UUID> {

    @Query("""
            select a from PlayerAnswerEntity a
            where a.sessionPlayerId in :playerIds
            """)
    List<PlayerAnswerEntity> findBySessionPlayerIdIn(@Param("playerIds") Collection<UUID> playerIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from PlayerAnswerEntity a
            where a.sessionPlayerId in :playerIds
              and a.id not in :answerIds
            """)
    void deleteBySessionPlayerIdInAndIdNotIn(
            @Param("playerIds") Collection<UUID> playerIds,
            @Param("answerIds") Collection<UUID> answerIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from PlayerAnswerEntity a
            where a.sessionPlayerId in :playerIds
            """)
    void deleteBySessionPlayerIdIn(@Param("playerIds") Collection<UUID> playerIds);
}
