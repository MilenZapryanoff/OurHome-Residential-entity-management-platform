package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.*;
import com.example.OurHome.model.dto.BindingModels.Fee.BankDetailsBindingModel;
import com.example.OurHome.model.dto.BindingModels.Fee.FeeEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.OverpaymentBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeAddGlobalFeeBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;

@Controller
public class FeesController {

    private final UserService userService;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyService propertyService;
    private final FeeService feeService;
    private final PropertyFeeService propertyFeeService;

    public FeesController(UserService userService, ResidentialEntityService residentialEntityService, PropertyService propertyService, FeeService feeService, PropertyFeeService propertyFeeService) {
        this.userService = userService;
        this.residentialEntityService = residentialEntityService;
        this.propertyService = propertyService;
        this.feeService = feeService;
        this.propertyFeeService = propertyFeeService;
    }

    /**
     * Administration -> Monthly Fees
     *
     * @param id Residential entity ID
     */
    @GetMapping("/administration/fees/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityFees(@PathVariable("id") Long id) {

        return new ModelAndView("administration/administration-fees")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));
    }

    @GetMapping("/administration/fees/settings/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityFeeSettings(@PathVariable("id") Long id) {

        return new ModelAndView("administration/administration-fees-settings")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));
    }

    @GetMapping("/administration/fees/bank-details/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityBankDetails(@PathVariable("id") Long id) {

        return new ModelAndView("administration/administration-fees-bank-details")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));
    }


    /**
     * Administration -> Monthly Fees -> Bank Details Edit page get mapping
     *
     * @param id Residential entity ID
     */
    @GetMapping("/administration/fees/bank-details/edit/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityBankDetailsEdit(@PathVariable("id") Long id) {

        BankDetailsBindingModel bankDetailsBindingModel = residentialEntityService.mapEntityToBankDetailsBindingModel(getResidentialEntity(id));

        return new ModelAndView("administration/administration-fees-bank-details-edit")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("bankDetailsBindingModel", bankDetailsBindingModel);
    }

    /**
     * Administration -> Monthly Fees -> Bank Details Edit page POST mapping
     *
     * @param id Residential entity ID
     */
    @PostMapping("/administration/fees/bank-details/edit/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityBankDetailsEdit(@PathVariable("id") Long id,
                                                         @Valid BankDetailsBindingModel bankDetailsBindingModel,
                                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration/administration-fees-bank-details-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("residentialEntity", getResidentialEntity(id));
        }

        residentialEntityService.updateResidentialEntityBankDetails(getResidentialEntity(id), bankDetailsBindingModel);

        return new ModelAndView("redirect:/administration/fees/bank-details/" + id);
    }

    /**
     * Administration -> Monthly Fees -> Bank Details Delete POST mapping
     *
     * @param id Residential entity ID
     */
    @PostMapping("/administration/fees/bank-details/delete/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityBankDetailsDelete(@PathVariable("id") Long id) {

        residentialEntityService.deleteResidentialEntityBankDetails(getResidentialEntity(id));

        return new ModelAndView("redirect:/administration/fees/bank-details/" + id);
    }


    /**
     * Administration -> Monthly Fees -> Edit
     *
     * @param id Residential entity ID
     */
    @GetMapping("/administration/fees/edit/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView editResidentialEntityFees(@PathVariable("id") Long id) {

        ResidentialEntity residentialEntity = getResidentialEntity(id);
        if (residentialEntity == null) {
            return new ModelAndView("redirect:/administration/fees/" + id);
        }

        FeeEditBindingModel feeEditBindingModel = feeService.mapFeeToBindingModel(residentialEntity.getFee());

        return new ModelAndView("administration/administration-fees-edit")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("feeEditBindingModel", feeEditBindingModel);
    }

    /**
     * Administration -> Monthly Fees -> Edit
     *
     * @param id Residential entity ID
     *           POST
     */
    @PostMapping("/administration/fees/edit/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView editResidentialEntityFees(@PathVariable("id") Long id,
                                                  @Valid FeeEditBindingModel feeEditBindingModel,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration/administration-fees-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("residentialEntity", getResidentialEntity(id))
                    .addObject("feeEditBindingModel", feeEditBindingModel);
        }

        ResidentialEntity residentialEntity = getResidentialEntity(id);
        if (residentialEntity == null) {
            return new ModelAndView("redirect:/administration/fees/settings/" + id);
        }
        feeService.changeFee(residentialEntity, feeEditBindingModel);

        return new ModelAndView("redirect:/administration/fees/settings/" + id);
    }

    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID
     *
     * @param id property ID
     */
    @GetMapping("/administration/fees/details/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView propertyFees(@PathVariable("id") Long id) {

        Property property = propertyService.findPropertyById(id);
        OverpaymentBindingModel overpaymentBindingModel = propertyService.mapOverPaymentBindingModel(property);

        return new ModelAndView("administration/administration-property-fees")
                .addObject("userViewModel", getUserViewModel())
                .addObject("property", getProperty(id))
                .addObject("overpaymentBindingModel", overpaymentBindingModel);
    }


    /**
     * Switch monthly (auto) fee generation for a single property ON or OFF (Slide button in property fee details section)
     *
     * @param id property ID
     */
    @PostMapping("/administration/fees/changeAutoFeeSlider/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView changeAutoFeeSlider(@PathVariable("id") Long id) {

        propertyService.changeAutoFeeGeneration(propertyService.findPropertyById(id));

        return new ModelAndView("redirect:/administration/fees/details/" + id + "#autoFee-post-nav");
    }

    /**
     * Switch monthly (auto) fee generation for a single property ON or OFF (Table buttons in Monthly fees section table)
     *
     * @param id property ID
     */
    @PostMapping("/administration/fees/changeAutoFeeButton/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView changeAutoFeeButton(@PathVariable("id") Long id) {

        Property property = propertyService.findPropertyById(id);
        propertyService.changeAutoFeeGeneration(property);

        return new ModelAndView("redirect:/administration/fees/" + property.getResidentialEntity().getId() + "#autoFee-post-nav");
    }


    /**
     * Switch all properties monthly (auto) fee generation ON (Top Table buttons in Monthly fees section table)
     *
     * @param id residentialEntity ID
     */
    @PostMapping("/administration/fees/turnAllPropertiesFeesOn/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView turnAllPropertyFeesOn(@PathVariable("id") Long id) {

        ResidentialEntity residentialEntity = getResidentialEntity(id);
        propertyService.turnAllPropertiesFeesOn(residentialEntity);

        return new ModelAndView("redirect:/administration/fees/" + id + "#autoFee-post-nav");
    }

    /**
     * Switch all properties monthly (auto) fee generation OFF (Top Table buttons in Monthly fees section table)
     *
     * @param id residentialEntity ID
     */
    @PostMapping("/administration/fees/turnAllPropertiesFeesOff/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView turnAllPropertyFeesOff(@PathVariable("id") Long id) {

        ResidentialEntity residentialEntity = getResidentialEntity(id);
        propertyService.turnAllPropertiesFeesOff(residentialEntity);

        return new ModelAndView("redirect:/administration/fees/" + id + "#autoFee-post-nav");
    }

    /**
     * Updating overpayment amount
     *
     * @param id                      property ID
     * @param overpaymentBindingModel input data
     */

    @PostMapping("/administration/fees/setOverPayment/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView setOverPayment(@PathVariable("id") Long id,
                                       @Valid OverpaymentBindingModel overpaymentBindingModel,
                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration/administration-property-fees")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("property", getProperty(id))
                    .addObject("overpaymentBindingModel", overpaymentBindingModel);
        }

        Property property = propertyService.findPropertyById(id);
        propertyService.updateOverpayment(property, overpaymentBindingModel.getOverPayment());

        return new ModelAndView("redirect:/administration/fees/details/" + id + "#overpayment-post-nav");
    }

    @PostMapping("/administration/fees/setAdditionalPropertyFee/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView setAdditionalPropertyFee(@PathVariable("id") Long id,
                                                 @Valid OverpaymentBindingModel overpaymentBindingModel,
                                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration/administration-property-fees")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("property", getProperty(id))
                    .addObject("overpaymentBindingModel", overpaymentBindingModel);
        }

        propertyService.setAdditionalPropertyFee(propertyService.findPropertyById(id), overpaymentBindingModel.getAdditionalPropertyFee());

        return new ModelAndView("redirect:/administration/fees/details/" + id + "#overpayment-post-nav");
    }

    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID -> Edit fee
     *
     * @param id PropertyFee ID
     */
    @GetMapping("/administration/fees/details/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyFeeModeratorAccess(#id, authentication)")
    public ModelAndView editPropertyFee(@PathVariable("id") Long id) {

        PropertyFeeEditBindingModel propertyFeeEditBindingModel = propertyFeeService.mapPropertyFeeToBindingModel(id);
        Long propertyId = propertyFeeEditBindingModel.getPropertyId();
        propertyFeeEditBindingModel.setOverPayment(getProperty(propertyId).getOverpayment());


        return new ModelAndView("administration/administration-property-fees-edit")
                .addObject("userViewModel", getUserViewModel())
                .addObject("propertyFeeEditBindingModel", propertyFeeEditBindingModel);
    }


    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID -> Edit fee
     *
     * @param id PropertyFee ID
     *           POST
     */
    @PostMapping("/administration/fees/details/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyFeeModeratorAccess(#id, authentication)")
    public ModelAndView editPropertyFee(@PathVariable("id") Long id,
                                        @Valid PropertyFeeEditBindingModel propertyFeeEditBindingModel,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration/administration-property-fees-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("propertyFeeEditBindingModel", propertyFeeEditBindingModel);
        }

        if (propertyFeeEditBindingModel.getFundMmAmount() == null) {
            propertyFeeEditBindingModel.setFundMmAmount(BigDecimal.ZERO);
        }
        if (propertyFeeEditBindingModel.getFundRepairAmount() == null) {
            propertyFeeEditBindingModel.setFundRepairAmount(BigDecimal.ZERO);
        }

        if (propertyFeeEditBindingModel.getFundMmAmount().equals(BigDecimal.ZERO) && propertyFeeEditBindingModel.getFundRepairAmount().equals(BigDecimal.ZERO)) {
            return new ModelAndView("administration/administration-property-fees-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("propertyFeeEditBindingModel", propertyFeeEditBindingModel)
                    .addObject("feeChangeFailed", true);
        }

        propertyFeeService.editMonthlyFee(id, propertyFeeEditBindingModel);
        propertyService.setOverpayment(propertyFeeEditBindingModel);

        return new ModelAndView("redirect:/administration/fees/details/" + propertyFeeEditBindingModel.getPropertyId());
    }

    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID -> Manually add fee
     *
     * @param id PropertyFee ID
     */
    @GetMapping("/administration/fees/details/add/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView addPropertyFee(@PathVariable("id") Long id) {

        return new ModelAndView("administration/administration-property-fees-add")
                .addObject("userViewModel", getUserViewModel())
                .addObject("propertyFeeAddBindingModel", new PropertyFeeAddBindingModel());
    }

    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID -> Manually add fee
     *
     * @param id PropertyFee ID
     *           POST
     */
    @PostMapping("/administration/fees/details/add/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView addPropertyFee(@PathVariable("id") Long id,
                                       @Valid PropertyFeeAddBindingModel propertyFeeAddBindingModel,
                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration/administration-property-fees-add")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("propertyFeeAddBindingModel", propertyFeeAddBindingModel);
        }

        if (propertyFeeAddBindingModel.getFundMmAmount() == null) {
            propertyFeeAddBindingModel.setFundMmAmount(BigDecimal.ZERO);
        }
        if (propertyFeeAddBindingModel.getFundRepairAmount() == null) {
            propertyFeeAddBindingModel.setFundRepairAmount(BigDecimal.ZERO);
        }

        if (propertyFeeAddBindingModel.getFundMmAmount().equals(BigDecimal.ZERO) && propertyFeeAddBindingModel.getFundRepairAmount().equals(BigDecimal.ZERO)) {
            return new ModelAndView("administration/administration-property-fees-add")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("propertyFeeAddBindingModel", propertyFeeAddBindingModel)
                    .addObject("feeCreationFailed", true);
        }

        Property property = propertyService.findPropertyById(id);
        propertyFeeService.createSingleFee(property, propertyFeeAddBindingModel);

        return new ModelAndView("redirect:/administration/fees/details/" + id);
    }


    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID -> Delete fee
     *
     * @param id PropertyFee ID
     *           POST
     */
    @PostMapping("/administration/fees/details/delete/{id}")
    @PreAuthorize("@securityService.checkPropertyFeeModeratorAccess(#id, authentication)")
    public ModelAndView deletePropertyFee(@PathVariable("id") Long id) {

        PropertyFee propertyFee = propertyFeeService.findPropertyFeeById(id);
        Long propertyId = propertyFee.getProperty().getId();

        propertyFeeService.deleteMonthlyFee(propertyFee);

        return new ModelAndView("redirect:/administration/fees/details/" + propertyId + "#delete-fee-post-nav");
    }

    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID -> Change payment status
     *
     * @param id PropertyFee ID
     *           POST
     */
    @PostMapping("/administration/fees/changePaymentStatus/{id}")
    @PreAuthorize("@securityService.checkPropertyFeeModeratorAccess(#id, authentication)")
    public ModelAndView editPropertyFeesPaymentStatus(@PathVariable("id") Long id) {

        PropertyFee propertyFee = propertyFeeService.findPropertyFeeById(id);
        propertyFeeService.changePaymentStatus(propertyFee);
        return new ModelAndView("redirect:/administration/fees/details/" + propertyFee.getProperty().getId() + "#delete-fee-post-nav");
    }


    /**
     * Administration -> Monthly Fees -> Add fee for all properties
     *
     * @param id Residential entity ID
     */
    @GetMapping("/administration/fees/addglobalfee/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView addGlobalFee(@PathVariable("id") Long id) {

        return new ModelAndView("administration/administration-fees-addglobalfee")
                .addObject("userViewModel", getUserViewModel())
                .addObject("propertyFeeAddGlobalFeeBindingModel", new PropertyFeeAddGlobalFeeBindingModel());
    }

    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID -> Manually add fee
     *
     * @param id residentialEntity ID
     *           POST
     */
    @PostMapping("/administration/fees/addglobalfee/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView addGlobalFee(@PathVariable("id") Long id,
                                     @Valid PropertyFeeAddGlobalFeeBindingModel propertyFeeAddGlobalFeeBindingModel,
                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration/administration-fees-addglobalfee")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("propertyFeeAddGlobalFeeBindingModel", propertyFeeAddGlobalFeeBindingModel);
        }

        ResidentialEntity residentialEntity = residentialEntityService.findResidentialEntityById(id).orElse(null);

        if (propertyFeeAddGlobalFeeBindingModel.getFundMmAmount() == null) {
            propertyFeeAddGlobalFeeBindingModel.setFundMmAmount(BigDecimal.ZERO);
        }
        if (propertyFeeAddGlobalFeeBindingModel.getFundRepairAmount() == null) {
            propertyFeeAddGlobalFeeBindingModel.setFundRepairAmount(BigDecimal.ZERO);
        }

        if (propertyFeeAddGlobalFeeBindingModel.getFundMmAmount().equals(BigDecimal.ZERO) && propertyFeeAddGlobalFeeBindingModel.getFundRepairAmount().equals(BigDecimal.ZERO)) {
            return new ModelAndView("administration/administration-fees-addglobalfee")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("propertyFeeAddGlobalFeeBindingModel", propertyFeeAddGlobalFeeBindingModel)
                    .addObject("globalFeeFailed", true);
        }

        if (propertyFeeService.createMassFee(residentialEntity, propertyFeeAddGlobalFeeBindingModel)) {
            return new ModelAndView("administration/administration-fees")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("residentialEntity", getResidentialEntity(id))
                    .addObject("globalFeeAdded", true);
        }

        return new ModelAndView("redirect:/administration/fees/" + id);
    }


    /**
     * Method returns currently logged user
     *
     * @return UserEntity
     */
    private UserViewModel getUserViewModel() {
        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.getUserViewData(loggedUser);
    }

    /**
     * Method returns a ResidentialEntity
     *
     * @param id residential entity id
     * @return ResidentialEntity
     */
    private ResidentialEntity getResidentialEntity(Long id) {
        return residentialEntityService.findResidentialEntityById(id).orElse(null);
    }

    /**
     * Method returns a Property
     *
     * @param id property id
     * @return Property
     */
    private Property getProperty(Long id) {
        return propertyService.findPropertyById(id);
    }
}