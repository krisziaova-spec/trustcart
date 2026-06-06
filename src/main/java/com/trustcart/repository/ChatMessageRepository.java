package com.trustcart.repository;

import com.trustcart.model.ChatMessage;
import com.trustcart.model.ChatThread;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByThreadOrderByCreatedAtAsc(ChatThread thread);
}
