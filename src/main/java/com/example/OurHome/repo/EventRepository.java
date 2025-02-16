package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {


    @Query("SELECT COUNT(e) FROM events e where e.residentialEntity.id=:residentialEntityId")
    Long countReportsByRE(@Param("residentialEntityId") Long id);

    @Query("SELECT e FROM events e where e.residentialEntity.id=:residentialEntityId")
    List<Event> findEventsByRE(@Param("residentialEntityId") Long id);

    @Query("SELECT COUNT(e) FROM events e where e.residentialEntity.id=:residentialEntityId and e.date < CAST(NOW() AS DATE)")
    Long countPastReportsByRe(@Param("residentialEntityId") Long id);

    @Query("SELECT COUNT(e) FROM events e where e.residentialEntity.id=:residentialEntityId and e.date >= CAST(NOW() AS DATE)")
    Long countFutureReportsByRe(@Param("residentialEntityId") Long id);

    @Query("SELECT e FROM events e WHERE e.residentialEntity.id = :residentialEntityId AND e.date < CAST(NOW() AS DATE) ORDER BY e.date DESC")
    List<Event> findPastReportsByRe(@Param("residentialEntityId") Long id);

    @Query("SELECT e FROM events e WHERE e.residentialEntity.id = :residentialEntityId AND e.date >= CAST(NOW() AS DATE) ORDER BY e.date ASC")
    List<Event> findFutureReportsByRe(@Param("residentialEntityId") Long id);

}
