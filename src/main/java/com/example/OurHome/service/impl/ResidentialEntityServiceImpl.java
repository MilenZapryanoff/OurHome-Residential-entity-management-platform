package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Fee.BankDetailsBindingModel;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentialEntityEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentialEntityRegisterBindingModel;
import com.example.OurHome.repo.CityRepository;
import com.example.OurHome.repo.ResidentialEntityRepository;
import com.example.OurHome.service.FeeService;
import com.example.OurHome.service.PropertyChangeRequestService;
import com.example.OurHome.service.PropertyService;
import com.example.OurHome.service.ResidentialEntityService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    private final PropertyService propertyService;
    private final PropertyRegisterRequestServiceImpl propertyRegisterRequestService;
    private final PropertyChangeRequestService propertyChangeRequestService;
    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.ZERO;

    public ResidentialEntityServiceImpl(ModelMapper modelMapper, CityRepository cityRepository, FeeService feeService, ResidentialEntityRepository residentialEntityRepository, PasswordEncoder passwordEncoder, PropertyService propertyService, PropertyRegisterRequestServiceImpl propertyRegisterRequestService, PropertyChangeRequestService propertyChangeRequestService) {
        this.modelMapper = modelMapper;
        this.cityRepository = cityRepository;
        this.feeService = feeService;
        this.residentialEntityRepository = residentialEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.propertyService = propertyService;
        this.propertyRegisterRequestService = propertyRegisterRequestService;
        this.propertyChangeRequestService = propertyChangeRequestService;
    }

    /**
     * New Residential entity creation method.
     *
     * @return boolean
     */
    @Override
    public boolean newResidentialEntity(ResidentialEntityRegisterBindingModel residentialEntityRegisterBindingModel, UserEntity loggedUser) {

        Long numberOfApartments = residentialEntityRegisterBindingModel.getNumberOfApartments();

        ResidentialEntity newResidentialEntity = modelMapper.map(residentialEntityRegisterBindingModel, ResidentialEntity.class);

        newResidentialEntity.setFee(feeService.createFee(newResidentialEntity));
        newResidentialEntity.setIncomesFundMm(DEFAULT_AMOUNT);
        newResidentialEntity.setIncomesFundRepair(DEFAULT_AMOUNT);
        newResidentialEntity.setIncomesTotalAmount(DEFAULT_AMOUNT);
        newResidentialEntity.setManager(loggedUser);
        newResidentialEntity.setIncomesVisible(true);
        newResidentialEntity.setExpensesVisible(true);
        newResidentialEntity.setBankDetailsSet(false);
        newResidentialEntity.setCity(cityRepository.findByName(residentialEntityRegisterBindingModel.getCity()));

        //generating a random 8-digit code user as residentialEntity ID
        Long generatedRandomId = new Random().nextLong(9999999, 100000000);
        newResidentialEntity.setId(generatedRandomId);

        //creation of access code hint
        String accessCode = residentialEntityRegisterBindingModel.getAccessCode();
        newResidentialEntity.setAccessCode(passwordEncoder.encode(accessCode));
        newResidentialEntity.setAccessCodeHint(createAccessCodeHint(accessCode));

        residentialEntityRepository.save(newResidentialEntity);

        propertyService.createAllProperties(newResidentialEntity, numberOfApartments);

        return residentialEntityRepository.countById(generatedRandomId) != 0;
    }

    @Override
    public void deleteResidentialEntity(Long id) {
        ResidentialEntity residentialEntity = residentialEntityRepository.findResidentialEntityById(id);

        List<Property> residentialEntityProperties = residentialEntity.getProperties();
        propertyService.deleteAllProperties(residentialEntityProperties);

        if (!residentialEntity.getPropertyTypes().isEmpty()) {
            //detach register/change requests from propertyTypes.
            propertyRegisterRequestService.deleteAllRegistrationRequests(id);
            propertyChangeRequestService.deleteAllRegistrationRequests(id);
        }

        residentialEntityRepository.deleteById(id);
    }

    public boolean checkIfUserIsResidentialEntityModerator(Long residentialEntityId, Long residentId) {
        Long result = residentialEntityRepository.isUserModeratorOfResidentialEntity(residentialEntityId, residentId);
        return result >= 1;
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
     * @param residentialEntity actual Residential entity from DB
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

    @Override
    public void addPaymentAmountToIncomes(PropertyFee propertyFee, Property property) {
        ResidentialEntity residentialEntity = residentialEntityRepository.findResidentialEntityByPropertyId(property.getId());

        BigDecimal currentFundMm = residentialEntity.getIncomesFundMm();
        BigDecimal currentFundRepair = residentialEntity.getIncomesFundRepair();
        BigDecimal currentTotalAmount = residentialEntity.getIncomesTotalAmount();

        residentialEntity.setIncomesFundMm(currentFundMm.
                add(propertyFee.getFundMmAmount()));
        residentialEntity.setIncomesFundRepair(currentFundRepair
                .add(propertyFee.getFundRepairAmount()));
        residentialEntity.setIncomesTotalAmount(currentTotalAmount
                .add(propertyFee.getFundMmAmount())
                .add(propertyFee.getFundRepairAmount()));

        residentialEntityRepository.save(residentialEntity);
    }

    @Override
    public void reversePaymentAmountFromIncomes(PropertyFee propertyFee, Property property) {
        ResidentialEntity residentialEntity = residentialEntityRepository.findResidentialEntityByPropertyId(property.getId());

        BigDecimal currentFundMm = residentialEntity.getIncomesFundMm();
        BigDecimal currentFundRepair = residentialEntity.getIncomesFundRepair();
        BigDecimal currentTotalAmount = residentialEntity.getIncomesTotalAmount();

        residentialEntity.setIncomesFundMm(currentFundMm
                .subtract(propertyFee.getFundMmAmount()));
        residentialEntity.setIncomesFundRepair(currentFundRepair
                .subtract(propertyFee.getFundRepairAmount()));
        residentialEntity.setIncomesTotalAmount(currentTotalAmount
                .subtract(propertyFee.getFundMmAmount())
                .subtract(propertyFee.getFundRepairAmount()));

        residentialEntityRepository.save(residentialEntity);
    }


    /**
     * Method for setting expense visibility of Residential Entity. Turning ON or OFF
     *
     * @param id Residential Expense id
     */
    @Override
    public void changeExpensesVisibility(Long id) {
        ResidentialEntity residentialEntity = residentialEntityRepository.findResidentialEntityById(id);

        if (residentialEntity != null) {
            residentialEntity.setExpensesVisible(!residentialEntity.isExpensesVisible());
            residentialEntityRepository.save(residentialEntity);
        }
    }

    @Override
    public void changeIncomesVisibility(Long id) {
        ResidentialEntity residentialEntity = residentialEntityRepository.findResidentialEntityById(id);

        if (residentialEntity != null) {
            residentialEntity.setIncomesVisible(!residentialEntity.isIncomesVisible());
            residentialEntityRepository.save(residentialEntity);
        }
    }

    @Override
    public boolean checkIfResidentialEntityDeletable(Long residentialEntityId) {
        ResidentialEntity residentialEntity = residentialEntityRepository.findResidentialEntityById(residentialEntityId);
        return residentialEntity.getResidents().isEmpty();
    }

    @Override
    public BankDetailsBindingModel mapEntityToBankDetailsBindingModel(ResidentialEntity residentialEntity) {
        return modelMapper.map(residentialEntity, BankDetailsBindingModel.class);
    }

    /**
     * Method for update of Residential Entity Bank details
     *
     * @param residentialEntity       current Residential Entity
     * @param bankDetailsBindingModel input data
     */
    @Override
    public void updateResidentialEntityBankDetails(ResidentialEntity residentialEntity, BankDetailsBindingModel bankDetailsBindingModel) {
        residentialEntity.setBankAccountHolder(bankDetailsBindingModel.getBankAccountHolder());
        residentialEntity.setBankIban(bankDetailsBindingModel.getBankIban());
        residentialEntity.setBankName(bankDetailsBindingModel.getBankName());
        residentialEntity.setBankAdditionalData(bankDetailsBindingModel.getBankAdditionalData());
        residentialEntity.setBankDetailsSet(true);
        residentialEntityRepository.save(residentialEntity);
    }

    /**
     * Method for deletion of Residential Entity Bank details
     *
     * @param residentialEntity current Residential Entity
     */
    @Override
    public void deleteResidentialEntityBankDetails(ResidentialEntity residentialEntity) {
        residentialEntity.setBankAccountHolder(null);
        residentialEntity.setBankIban(null);
        residentialEntity.setBankName(null);
        residentialEntity.setBankAdditionalData(null);
        residentialEntity.setBankDetailsSet(false);
        residentialEntityRepository.save(residentialEntity);
    }

    /**
     * @param file              upload file
     * @param residentialEntity current residential entity
     * @return String picturePath
     * @throws IOException IOException or IllegalArgumentException
     */
    @Override
    public String savePicture(MultipartFile file, ResidentialEntity residentialEntity) throws IOException {

        if (file != null && !file.isEmpty()) {

            //size check
            if (file.getSize() > 3 * 1024 * 1024) {
                throw new IllegalArgumentException("File size exceeds the allowed limit (3MB)");
            }

            assert residentialEntity != null;
            String uploadDirectory = "src/main/resources/static/res-entity-pictures";
            File directory = new File(uploadDirectory);

            //directory existence check
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Failed to create directory!");
                }
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

                // Validate file type (allow only image files)
                if (!fileExtension.matches("\\.(jpg|jpeg|png|gif)$")) {
                    throw new IllegalArgumentException("Invalid file type!");
                }

                String fileName = "picture-" + residentialEntity.getId() + fileExtension;
                Path tempFilePath = Paths.get(uploadDirectory, "temp-" + fileName);
                Path finalFilePath = Paths.get(uploadDirectory, fileName);

                try {
                    // Save the file temporarily
                    Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);

                    // If a previous picture exists, delete it only after the new one is saved
                    if (residentialEntity.getPicturePath() != null) {
                        String existingPicturePath = residentialEntity.getPicturePath();
                        File existingFile = new File("src/main/resources/static" + existingPicturePath);
                        if (existingFile.exists()) {
                            if (!existingFile.delete()) {
                                throw new IOException("Failed to delete existing picture!");
                            }
                        }
                    }

                    // Rename the temp file to the final file name
                    Files.move(tempFilePath, finalFilePath, StandardCopyOption.REPLACE_EXISTING);

                    // Update the residentialEntity's picturePath in the database
                    String picturePath = "/res-entity-pictures/" + fileName;
                    updatePicturePath(residentialEntity, picturePath);
                    return picturePath;
                } catch (IOException e) {
                    // Clean up temp file if something goes wrong
                    Files.deleteIfExists(tempFilePath);
                    throw new IOException("Failed to save the file!", e);
                }
            } else {
                throw new IOException("Invalid file name!");
            }
        } else {
            throw new IllegalArgumentException("File is null or empty!");
        }
    }


    /**
     * Update of picturePath in the DB.
     *
     * @param residentialEntity current Residential entity
     */
    @Override
    public void removeResidentialEntityPicture(ResidentialEntity residentialEntity) throws IOException {

        // Get the current avatar path
        String picturePath = residentialEntity.getPicturePath();

        // Check if the user has a custom avatar
        if (picturePath != null && picturePath.matches("/res-entity-pictures/picture-" + residentialEntity.getId() + ".*\\.(jpg|jpeg|png|gif)$")) {
            String uploadDirectory = "src/main/resources/static";
            Path filePath = Paths.get(uploadDirectory, picturePath);

            // Delete the file from the file system
            try {
                Files.deleteIfExists(filePath);
                residentialEntity.setPicturePath(null);
                residentialEntityRepository.save(residentialEntity);
            } catch (IOException e) {
                throw new IOException("Failed to delete the file!", e);
            }
        } else {
            throw new IllegalArgumentException("Residential entity does not have a custom picture set!");
        }
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

    /**
     * Update of picturePath in the DB.
     *
     * @param residentialEntity current Residential entity
     * @param picturePath       the path of the user picture stored in DB
     */
    public void updatePicturePath(ResidentialEntity residentialEntity, String picturePath) {
        if (residentialEntity != null) {
            residentialEntity.setPicturePath(picturePath);
            residentialEntityRepository.save(residentialEntity);
        } else {
            throw new IllegalArgumentException("Residential Entity is null!");
        }
    }
}
