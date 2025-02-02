package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.PropertyType;
import com.example.OurHome.model.Entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT COUNT(r) FROM reports r where r.residentialEntity.id=:id")
    Long countReports(@Param("id") Long id);

    @Query("SELECT COUNT(r) FROM reports r where r.residentialEntity.id=:id and r.publicReport=true")
    Long countPublicReports(@Param("id") Long id);

    @Query("SELECT COUNT(r) FROM reports r where r.residentialEntity.id=:condominiumId and r.creator.id=:loggedUserId")
    Long countMyReports(@Param("condominiumId") Long condominiumId, @Param("loggedUserId") Long loggedUserId);

    @Query("SELECT r FROM reports r where r.residentialEntity.id=:id ORDER BY r.creationDateTime DESC")
    List<Report> findReportsByRE(@Param("id") Long id);

    @Query("SELECT r FROM reports r where r.residentialEntity.id=:id and r.publicReport=true ORDER BY r.creationDateTime DESC")
    List<Report> findPublicReportsByRE(@Param("id") Long id);

    @Query("SELECT r FROM reports r where r.residentialEntity.id=:residentialEntityId and r.creator.id=:creatorId ORDER BY r.creationDateTime DESC")
    List<Report> findReportsByREAndCreatorId(@Param("creatorId") Long creatorId, @Param("residentialEntityId") Long residentialEntityId);
}
