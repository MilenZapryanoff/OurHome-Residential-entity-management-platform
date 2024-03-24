package com.example.OurHome.config;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.User.UserRegisterBindingModel;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(UserRegisterBindingModel.class, UserEntity.class).addMappings(mapper ->
                mapper.skip(UserEntity::setPassword));
        modelMapper.typeMap(Property.class, PropertyEditBindingModel.class).addMappings(mapper ->
                mapper.skip(PropertyEditBindingModel::setPropertyType));
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper;
    }
}
