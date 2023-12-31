package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Long countByReceiver_Id(Long id);

    @Query("SELECT COUNT(m) FROM Message m where m.isRead = false AND m.receiver.id=:id")
    Long countUnreadMessages(Long id);

    @Query("SELECT COUNT(m) FROM Message m where m.isArchived = false AND m.receiver.id=:id")
    Long countNotArchivedMessages(Long id);

    @Query("SELECT COUNT(m) FROM Message m where m.isArchived = true AND m.receiver.id=:id")
    Long countArchivedMessages(Long id);

    @Query("SELECT m FROM Message m where m.isArchived = true AND m.receiver.id=:id ORDER BY m.date DESC, m.time DESC")
    List<Message> findArchivedMessagesByUserId(Long id);

    @Query("SELECT m FROM Message m where m.isArchived = false AND m.receiver.id=:id ORDER BY m.date DESC, m.time DESC")
    List<Message> findNotArchivedMessagesByUserId(Long id);

}
