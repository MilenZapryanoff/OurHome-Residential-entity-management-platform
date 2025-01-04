package com.example.OurHome.init;

import com.example.OurHome.model.Entity.PropertyClass;
import com.example.OurHome.model.enums.PropertyClassName;
import com.example.OurHome.repo.PropertyClassRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PropertyClassInit implements CommandLineRunner {

    private final PropertyClassRepository propertyClassRepository;

    public PropertyClassInit(PropertyClassRepository propertyClassRepository) {
        this.propertyClassRepository = propertyClassRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        PropertyClassName[] propertyClassNames = PropertyClassName.values();

        for (PropertyClassName currentPropertyClassName : propertyClassNames) {
            if (propertyClassRepository.findByName(currentPropertyClassName) == null) {
                propertyClassRepository.save(new PropertyClass(currentPropertyClassName));
            }
        }
    }
}
