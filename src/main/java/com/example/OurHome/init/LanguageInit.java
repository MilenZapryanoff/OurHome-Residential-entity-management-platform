package com.example.OurHome.init;

import com.example.OurHome.model.Entity.Language;
import com.example.OurHome.repo.LanguageRepository;
import org.springframework.stereotype.Component;

@Component
public class LanguageInit {

    private final static String LANGUAGE_BG = "bulgarian";
    private final static String LANGUAGE_EN = "english";
    private final LanguageRepository languageRepository;

    public LanguageInit(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    /**
     * Initialization method to populate DB with supported languages.
     * Currently supporting BULGARIAN and ENGLISH
     */

    public void languageInitialization() {
        if (languageRepository.countLanguageByName(LANGUAGE_BG) == 0) {
            languageRepository.save(new Language(LANGUAGE_BG));
        }
        if (languageRepository.countLanguageByName(LANGUAGE_EN) == 0) {
            languageRepository.save(new Language(LANGUAGE_EN));
        }
    }
}
