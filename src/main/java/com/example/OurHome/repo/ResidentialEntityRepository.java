package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.ResidentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResidentialEntityRepository extends JpaRepository<ResidentialEntity, Long> {

    Long countById(Long id);

    Long countByManager_Id(Long id);

    List<ResidentialEntity> findAllByManager_Id(Long managerId);

    @Query("SELECT r FROM residential_entities r where r.id=:id")
    ResidentialEntity findResidentialEntityById(Long id);

    @Query("SELECT COUNT(u) FROM residential_entities r JOIN r.moderators u WHERE r.id = :residentialEntityId AND u.id = :residentId")
    Long isUserModeratorOfResidentialEntity(@Param("residentialEntityId") Long residentialEntityId, @Param("residentId") Long residentId);

    @Query("SELECT r FROM residential_entities r JOIN r.properties p WHERE p.id = :id")
    ResidentialEntity findResidentialEntityByPropertyId(Long id);

    @Query("SELECT re FROM residential_entities re JOIN re.reports r WHERE r.id = :id")
    ResidentialEntity findResidentialEntityByReportId(Long id);

}
