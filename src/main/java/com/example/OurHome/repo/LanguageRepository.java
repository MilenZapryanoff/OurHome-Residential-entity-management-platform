package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    Language findLanguageByDescription(String lang);

    @Query("SELECT count(l) FROM languages l where l.description=:lang")
    Long countLanguageByName(String lang);
}
