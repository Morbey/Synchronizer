package com.synchronizer.service;

import com.synchronizer.model.MessageEntity;
import com.synchronizer.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for querying and managing persisted messages.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageQueryService {

    private final MessageRepository messageRepository;

    public Optional<MessageEntity> findByMessageId(String messageId) {
        return messageRepository.findByMessageId(messageId);
    }

    public List<MessageEntity> findByType(String type) {
        return messageRepository.findByType(type);
    }

    public List<MessageEntity> findBySource(String source) {
        return messageRepository.findBySource(source);
    }

    public List<MessageEntity> findByStatus(String status) {
        return messageRepository.findByStatus(status);
    }

    public List<MessageEntity> findRecentMessages(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return messageRepository.findRecentMessages(since);
    }

    public Page<MessageEntity> findAll(Pageable pageable) {
        return messageRepository.findAll(pageable);
    }

    public long countByStatus(String status) {
        return messageRepository.countByStatus(status);
    }

    public long countAll() {
        return messageRepository.count();
    }
}
