package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT COUNT(n) FROM notifications n where n.user.id=:id")
    Long countAllNotificationsByUser(@Param("id") Long id);

    @Query("SELECT COUNT(n) FROM notifications n where n.user.id=:id and n.cleared=false")
    Long countActiveNotificationsByUser(@Param("id") Long id);

    @Query("SELECT COUNT(n) FROM notifications n where n.user.id=:id and n.cleared=true")
    Long countInactiveNotificationsByUser(@Param("id") Long id);

    @Query("SELECT n FROM notifications n where n.user.id=:id ORDER BY n.creationDateTime DESC")
    List<Notification> findAllNotificationsByUserId(@Param("id") Long id);

    @Query("SELECT n FROM notifications n where n.user.id=:id and n.cleared=false ORDER BY n.creationDateTime DESC")
    List<Notification> findActiveNotificationsByUserId(@Param("id") Long id);

    @Query("SELECT n FROM notifications n where n.user.id=:id and n.cleared=true ORDER BY n.creationDateTime DESC")
    List<Notification> findInactiveNotificationsByUserId(@Param("id") Long id);
}
