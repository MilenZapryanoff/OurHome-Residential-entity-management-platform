package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.Report;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Reports.ReportAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.Reports.ReportEditBindingModel;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.repo.ReportRepository;
import com.example.OurHome.service.ReportService;
import com.example.OurHome.service.ResidentialEntityService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ReportServiceImpl implements ReportService {

    private final ModelMapper modelMapper;
    private final ResidentialEntityService residentialEntityService;
    private final ReportRepository reportRepository;
    private static final String UPLOAD_DIR = "src/main/resources/static/reports-images/"; // Директория за качените файлове
    private static final String PICTURE_PATH_DIR = "/reports-images/"; // Директория за качените файлове
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB в байтове
    private final PropertyRepository propertyRepository;

    public ReportServiceImpl(ModelMapper modelMapper, ResidentialEntityService residentialEntityService, ReportRepository reportRepository, PropertyRepository propertyRepository) {
        this.modelMapper = modelMapper;
        this.residentialEntityService = residentialEntityService;
        this.reportRepository = reportRepository;
        this.propertyRepository = propertyRepository;
    }

    /**
     * Processing reports images
     * Administration -> Reports -> Add
     *
     * @param reportAddBindingModel - input data
     * @param id                    - condominium id
     * @param loggedUser            - currently logged user
     */
    @Override
    public void processReport(ReportAddBindingModel reportAddBindingModel, Long id, UserEntity loggedUser) {
        List<MultipartFile> images = Arrays.asList(
                reportAddBindingModel.getImage1(),
                reportAddBindingModel.getImage2(),
                reportAddBindingModel.getImage3()
        );

        // Проверка на всички файлове за валидност
        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                validateFileSize(image);
                validateFileType(image);
            }
        }

        // Запис на файловете, след като всички са проверени
        String imagePath1 = images.get(0) != null && !images.get(0).isEmpty() ? saveImage(images.get(0)) : null;
        String imagePath2 = images.get(1) != null && !images.get(1).isEmpty() ? saveImage(images.get(1)) : null;
        String imagePath3 = images.get(2) != null && !images.get(2).isEmpty() ? saveImage(images.get(2)) : null;

        // Създаване на сигнал с валидираните снимки
        createReport(reportAddBindingModel, id, imagePath1, imagePath2, imagePath3, loggedUser);
    }

    /**
     * Saving reports images
     * Administration -> Reports -> Add
     *
     * @param file
     */
    @Override
    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null; // Ако няма файл, връщаме null
        }

        // Проверка за типа на файла (само изображения)
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new IllegalArgumentException("Неподдържан тип на файла: " + file.getContentType());
        }

        try {
            // Генериране на уникално име за файла
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);

            // Създаване на директория, ако тя не съществува
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Пълен път на файла
            Path filePath = uploadPath.resolve(fileName);

            // Запис на файла в директорията
            Files.copy(file.getInputStream(), filePath);

            // Връщане на пътя към запазеното изображение
            return PICTURE_PATH_DIR + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Неуспешно качване на файла: " + file.getOriginalFilename(), e);
        }
    }


    /**
     * Processing (creating) report after images are checked and saved
     * Administration -> Reports -> Add
     *
     * @param reportAddBindingModel - input data
     * @param id                    - condominium id
     * @param loggedUser            - currently logged user
     */
    @Override
    public void createReport(ReportAddBindingModel reportAddBindingModel, Long id, String imagePath1, String imagePath2, String imagePath3, UserEntity loggedUser) {

        // Създаване на нов обект Report
        Report report = new Report();

        report.setStatus("Входиран");
        report.setCategory(reportAddBindingModel.getCategory());
        report.setSubCategory(reportAddBindingModel.getSubCategory());
        report.setPriority(reportAddBindingModel.getPriority());
        report.setSource(reportAddBindingModel.getSource());
        report.setCreator(loggedUser);
        report.setDescription(reportAddBindingModel.getDescription());
        report.setContactInfo(reportAddBindingModel.getContactInfo());
        report.setPublicReport(reportAddBindingModel.isPublicReport());
        report.setCreationDateTime(LocalDateTime.now());

        // Задаване на пътищата към снимките
        report.setImage1(imagePath1);
        report.setImage2(imagePath2);
        report.setImage3(imagePath3);

        // Задаване на свързаните обекти (ако е необходимо)
        ResidentialEntity residentialEntity = residentialEntityService.findResidentialEntityById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid residential entity ID"));
        report.setResidentialEntity(residentialEntity);

        // Записване на обекта в базата
        reportRepository.save(report);
    }

    /**
     * Resolution of a report
     * Administration -> Reports -> View
     *
     * @param reportEditBindingModel - input data
     * @param id                     - condominium id
     * @param loggedUser             - currently logged user
     */
    @Override
    public void resolveReport(ReportEditBindingModel reportEditBindingModel, Long id, UserEntity loggedUser) {

        Report report = reportRepository.findById(id).orElse(null);

        if (report != null) {
            report.setStatus(reportEditBindingModel.getStatus());
            report.setCategory(reportEditBindingModel.getCategory());
            report.setSubCategory(reportEditBindingModel.getSubCategory());
            report.setPriority(reportEditBindingModel.getPriority());
            report.setSource(reportEditBindingModel.getSource());
            report.setPublicReport(reportEditBindingModel.isPublicReport());
            report.setResolution(reportEditBindingModel.getResolution());

            report.setResolveDateTime(LocalDateTime.now());
            report.setProcessedBy(loggedUser);

            // Записване на обекта в базата
            reportRepository.save(report);
        }
    }



    /**
     * Resolution of a report
     * Administration -> Reports -> View
     *
     * @param reportEditBindingModel - input data
     * @param id                     - condominium id
     * @param loggedUser             - currently logged user
     */
    @Override
    public void editReport(ReportEditBindingModel reportEditBindingModel, Long id, UserEntity loggedUser) {

        Report report = reportRepository.findById(id).orElse(null);

        if (report != null) {
            report.setCategory(reportEditBindingModel.getCategory());
            report.setSubCategory(reportEditBindingModel.getSubCategory());
            report.setPriority(reportEditBindingModel.getPriority());
            report.setSource(reportEditBindingModel.getSource());
            report.setPublicReport(reportEditBindingModel.isPublicReport());
            report.setDescription(reportEditBindingModel.getDescription());

            reportRepository.save(report);
        }
    }


    /**
     * Report image size validation method
     *
     * @param file report ID
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Размерът на файл " + file.getOriginalFilename() + " надвишава максимално допустимия размер от 10MB.");
        }
    }

    /**
     * Проверка за типа на файла (само изображения)
     *
     * @param file MultipartFile
     */
    private void validateFileType(MultipartFile file) {
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new IllegalArgumentException("Неподдържан тип на файла: " + file.getContentType());
        }
    }

    /**
     * Report deletion method
     * Administration -> Reports -> Delete
     *
     * @param reportId report ID
     */
    @Override
    @Transactional
    public void deleteReport(Long reportId) {
        // Намиране на сигнала по идентификатор
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report with ID " + reportId + " not found"));

        // Изтриване на свързаните изображения
        deleteImageIfExists(report.getImage1());
        deleteImageIfExists(report.getImage2());
        deleteImageIfExists(report.getImage3());

        // Изтриване на самия сигнал от базата
        reportRepository.delete(report);
    }

    @Override
    public ReportEditBindingModel mapReportToBindingModel(Long id) {

        Report report = reportRepository.findById(id).orElse(null);
        if (report == null) {
            throw new IllegalArgumentException("Report with ID " + id + " not found");
        }

        ReportEditBindingModel reportEditBindingModel = modelMapper.map(report, ReportEditBindingModel.class);

        if (report.getImage1() != null) {
            reportEditBindingModel.setImage1Path(report.getImage1());
        }
        if (report.getImage2() != null) {
            reportEditBindingModel.setImage2Path(report.getImage2());
        }
        if (report.getImage3() != null) {
            reportEditBindingModel.setImage3Path(report.getImage3());
        }

        //Extracting creator name + property data
        if (report.getCreator() != null) {
            //Setting base result string
            String creatorName = getCreatorName(report);
            reportEditBindingModel.setCreatorName(creatorName);
        } else {
            reportEditBindingModel.setCreatorName("Неизвестен");
        }

        //Extracting processor name
        if (report.getProcessedBy() != null) {
            reportEditBindingModel.setProcessedByName(report.getProcessedBy().getFirstName() + " " + report.getProcessedBy().getLastName());
        } else {
            reportEditBindingModel.setProcessedByName("");
        }
        return reportEditBindingModel;
    }


    /**
     * Private method for creating creator name string.
     *
     * @param report current report
     */
    private String getCreatorName(Report report) {

        Long ownerId = report.getCreator().getId();
        Long residentialEntityId = report.getResidentialEntity().getId();

        String creatorName = report.getCreator().getFirstName() + " " + report.getCreator().getLastName();

        List<Property> allProperties = propertyRepository.findAllUserProperties(ownerId, residentialEntityId);

        if (!allProperties.isEmpty()) {
            creatorName += ", ап. ";

            StringBuilder result = new StringBuilder();
            for (Property property : allProperties) {
                if (!result.isEmpty()) {
                    result.append(", "); // Добавя запетая и интервал между номерата
                }
                result.append(property.getNumber());
            }
            String stringResult = result.toString();
            creatorName += stringResult;
        }
        return creatorName;
    }


    /**
     * Unlink as creator of all reports in RE. All owned properties remain active but with no owner.
     * Performed only by MANAGER
     *
     * @param userId            is user id
     * @param residentialEntity is the current residentialEntity
     */
    @Override
    public void unlinkAllReportsFromOwner(Long userId, ResidentialEntity residentialEntity) {
        List<Report> allUserReports = reportRepository.findReportsByREAndCreatorId(userId, residentialEntity.getId());

        if (!allUserReports.isEmpty()) {
            // Reset creator for all reports
            allUserReports.forEach(report -> report.setCreator(null));

            reportRepository.saveAll(allUserReports);
        }
    }

    /**
     * Report image deletion method
     * Administration -> Reports -> Delete
     *
     * @param imagePath - image path
     */
    private void deleteImageIfExists(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                // Преобразуване на относителния път към абсолютен път
                Path filePath = Paths.get(UPLOAD_DIR, imagePath.replace(PICTURE_PATH_DIR, ""));

                // Изтриване на файла, ако съществува
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Неуспешно изтриване на файла: " + imagePath, e);
            }
        }
    }



}
