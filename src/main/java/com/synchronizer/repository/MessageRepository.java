package com.synchronizer.repository;

import com.synchronizer.model.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Message entity operations.
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    Optional<MessageEntity> findByMessageId(String messageId);

    List<MessageEntity> findByType(String type);

    List<MessageEntity> findBySource(String source);

    List<MessageEntity> findByStatus(String status);

    List<MessageEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT m FROM MessageEntity m WHERE m.timestamp >= :since ORDER BY m.timestamp DESC")
    List<MessageEntity> findRecentMessages(LocalDateTime since);

    @Query("SELECT COUNT(m) FROM MessageEntity m WHERE m.status = :status")
    long countByStatus(String status);
}
