package com.example.OurHome.service.impl;

import com.example.OurHome.service.LanguageService;
import org.springframework.stereotype.Service;

@Service
public class LanguageServiceImpl implements LanguageService {
    @Override

    public String resolveView(String lang, String page) {
        return "bg".equals(lang) ? "bg/".concat(page).concat("-bg.html") : page.concat(".html");
    }
}
