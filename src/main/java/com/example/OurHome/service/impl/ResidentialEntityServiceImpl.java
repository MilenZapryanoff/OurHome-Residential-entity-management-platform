package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.ResidentialEntityEditBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.ResidentialEntityRegisterBindingModel;
import com.example.OurHome.repo.CityRepository;
import com.example.OurHome.repo.ResidentialEntityRepository;
import com.example.OurHome.service.FeeService;
import com.example.OurHome.service.ResidentialEntityService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ResidentialEntityServiceImpl implements ResidentialEntityService {

    private final ModelMapper modelMapper;
    private final CityRepository cityRepository;
    private final FeeService feeService;
    private final ResidentialEntityRepository residentialEntityRepository;
    private final PasswordEncoder passwordEncoder;

    public ResidentialEntityServiceImpl(ModelMapper modelMapper, CityRepository cityRepository, FeeService feeService, ResidentialEntityRepository residentialEntityRepository, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.cityRepository = cityRepository;
        this.feeService = feeService;
        this.residentialEntityRepository = residentialEntityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * New Residential entity creation method.
     *
     * @return boolean
     */
    @Override
    public boolean newResidentialEntity(ResidentialEntityRegisterBindingModel residentialEntityRegisterBindingModel, UserEntity loggedUser) {

        ResidentialEntity newResidentialEntity = modelMapper.map(residentialEntityRegisterBindingModel, ResidentialEntity.class);

        newResidentialEntity.setFee(feeService.createFee(newResidentialEntity));
        newResidentialEntity.setManager(loggedUser);
        newResidentialEntity.setCity(cityRepository.findByName(residentialEntityRegisterBindingModel.getCity()));

        //generating a random 8-digit code user as residentialEntity ID
        Long generatedRandomId = new Random().nextLong(9999999, 100000000);
        newResidentialEntity.setId(generatedRandomId);

        //creation of access code hint
        String accessCode = residentialEntityRegisterBindingModel.getAccessCode();
        newResidentialEntity.setAccessCode(passwordEncoder.encode(accessCode));
        newResidentialEntity.setAccessCodeHint(createAccessCodeHint(accessCode));

        residentialEntityRepository.save(newResidentialEntity);

        return residentialEntityRepository.countById(generatedRandomId) != 0;
    }

    @Override
    public void removeResidentialEntity(Long id) {
        residentialEntityRepository.deleteById(id);
    }

    @Override
    public boolean checkIfResidentialEntityDeletable(Long id) {
        ResidentialEntity residentialEntity = residentialEntityRepository.findResidentialEntityById(id);
        return residentialEntity.getResidents().isEmpty();
    }

    @Override
    public boolean accessCodesMatchCheck(String accessCode, String confirmAccessCode) {
        return accessCode.equals(confirmAccessCode);
    }

    @Override
    public Optional<ResidentialEntity> findResidentialEntityById(Long id) {
        return residentialEntityRepository.findById(id);
    }

    @Override
    public void editResidentialEntity(Long entityId, ResidentialEntityEditBindingModel bindingModel) {

        ResidentialEntity residentialEntity = residentialEntityRepository.findById(entityId).orElse(null);
        if (residentialEntity != null) {
            String accessCode = bindingModel.getAccessCode();

            residentialEntity.setCity(cityRepository.findByName(bindingModel.getCity()));
            residentialEntity.setStreetName(bindingModel.getStreetName());
            residentialEntity.setStreetNumber(bindingModel.getStreetNumber());
            residentialEntity.setEntrance(bindingModel.getEntrance());
            residentialEntity.setNumberOfApartments(bindingModel.getNumberOfApartments());

            if (accessCode.length() >= 3) {
                residentialEntity.setAccessCode(passwordEncoder.encode(accessCode));
                residentialEntity.setAccessCodeHint(createAccessCodeHint(accessCode));
            }
            residentialEntityRepository.save(residentialEntity);
        }


    }

    @Override
    public List<ResidentialEntity> findResidentialEntitiesByManagerId(Long id) {
        return residentialEntityRepository.findAllByManager_Id(id);
    }

    /**
     * Method maps ResidentialEntity to ResidentialEntityEditBindingModel used for edit of residential
     * entity data.
     *
     * @param residentialEntity
     * @return ResidentialEntityEditBindingModel
     */
    @Override
    public ResidentialEntityEditBindingModel mapEntityToEditBindingModel(ResidentialEntity residentialEntity) {
        ResidentialEntityEditBindingModel residentialEntityEditBindingModel = new ResidentialEntityEditBindingModel();

        if (residentialEntity != null) {
            residentialEntityEditBindingModel = modelMapper.map(residentialEntity, ResidentialEntityEditBindingModel.class);
            residentialEntityEditBindingModel.setCity(residentialEntity.getCity().getName());
        }
        return residentialEntityEditBindingModel;
    }

    @Override
    public ResidentialEntity findResidentialEntityByPropertyId(Long id) {
       return residentialEntityRepository.findResidentialEntityByPropertyId(id);
    }

    /**
     * Access code hint creation method
     *
     * @return String
     */
    private String createAccessCodeHint(String accessCode) {
        int passwordHintLength = accessCode.length();
        return accessCode.substring(0, 2) +
                "*".repeat(passwordHintLength - 3) +
                accessCode.substring(passwordHintLength - 1);
    }
}
