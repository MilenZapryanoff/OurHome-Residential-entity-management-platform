package com.example.OurHome.service;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Reports.ReportAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.Reports.ReportEditBindingModel;
import org.springframework.web.multipart.MultipartFile;

public interface ReportService {

    public String saveImage(MultipartFile file);

    void createReport(ReportAddBindingModel reportAddBindingModel, Long id, String imagePath1, String imagePath2, String imagePath3, UserEntity loggedUser);

    void resolveReport(ReportEditBindingModel reportEditBindingModel, Long id, UserEntity loggedUser);

    void editReport(ReportEditBindingModel reportEditBindingModel, Long id, UserEntity loggedUser);

    void processReport(ReportAddBindingModel reportAddBindingModel, Long id, UserEntity loggedUser);

    void deleteReport(Long reportId);

    ReportEditBindingModel mapReportToBindingModel(Long id);

    void unlinkAllReportsFromOwner(Long userId, ResidentialEntity residentialEntity);
}
