package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.Fee.BankDetailsBindingModel;
import com.example.OurHome.model.dto.BindingModels.Fee.FeeEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.OverpaymentBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeAddGlobalFeeBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;
import com.example.OurHome.service.FeeService;
import com.example.OurHome.service.PropertyFeeService;
import com.example.OurHome.service.PropertyService;
import com.example.OurHome.service.ResidentialEntityService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;

@Controller
public class FeesController {


    private final ResidentialEntityService residentialEntityService;
    private final PropertyService propertyService;
    private final FeeService feeService;
    private final PropertyFeeService propertyFeeService;

    public FeesController(ResidentialEntityService residentialEntityService, PropertyService propertyService, FeeService feeService, PropertyFeeService propertyFeeService) {
        this.residentialEntityService = residentialEntityService;
        this.propertyService = propertyService;
        this.feeService = feeService;
        this.propertyFeeService = propertyFeeService;
    }

    /**
     * Administration -> Monthly Fees
     *
     * @param id Condominium ID
     */
    @GetMapping("/administration/fees/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityFees(@PathVariable("id") Long id,
                                              @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-fees") : new ModelAndView("en/administration/administration-fees");

        return view.addObject("residentialEntity", getResidentialEntity(id));
    }

    @GetMapping("/administration/fees/settings/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityFeeSettings(@PathVariable("id") Long id,
                                                     @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-fees-settings") : new ModelAndView("en/administration/administration-fees-settings");

        return view.addObject("residentialEntity", getResidentialEntity(id));
    }

    @GetMapping("/administration/fees/bank-details/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityBankDetails(@PathVariable("id") Long id,
                                                     @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-fees-bank-details") : new ModelAndView("en/administration/administration-fees-bank-details");

        return view.addObject("residentialEntity", getResidentialEntity(id));
    }


    /**
     * Administration -> Monthly Fees -> Bank Details Edit page get mapping
     *
     * @param id Condominium ID
     */
    @GetMapping("/administration/fees/bank-details/edit/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityBankDetailsEdit(@PathVariable("id") Long id,
                                                         @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        BankDetailsBindingModel bankDetailsBindingModel = residentialEntityService.mapEntityToBankDetailsBindingModel(getResidentialEntity(id));

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-fees-bank-details-edit") : new ModelAndView("en/administration/administration-fees-bank-details-edit");

        return view.addObject("residentialEntity", getResidentialEntity(id))
                .addObject("bankDetailsBindingModel", bankDetailsBindingModel);
    }

    /**
     * Administration -> Monthly Fees -> Bank Details Edit page POST mapping
     *
     * @param id Condominium ID
     */
    @PostMapping("/administration/fees/bank-details/edit/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityBankDetailsEdit(@PathVariable("id") Long id,
                                                         @Valid BankDetailsBindingModel bankDetailsBindingModel,
                                                         BindingResult bindingResult,
                                                         @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-fees-bank-details-edit") : new ModelAndView("en/administration/administration-fees-bank-details-edit");

        if (bindingResult.hasErrors()) {
            return view.addObject("residentialEntity", getResidentialEntity(id));
        }

        residentialEntityService.updateResidentialEntityBankDetails(getResidentialEntity(id), bankDetailsBindingModel);

        return new ModelAndView("redirect:/administration/fees/bank-details/" + id);
    }

    /**
     * Administration -> Monthly Fees -> Bank Details Delete POST mapping
     *
     * @param id Condominium ID
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
     * @param id Condominium ID
     */
    @GetMapping("/administration/fees/edit/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView editResidentialEntityFees(@PathVariable("id") Long id,
                                                  @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ResidentialEntity residentialEntity = getResidentialEntity(id);
        if (residentialEntity == null) {
            return new ModelAndView("redirect:/administration/fees/" + id);
        }

        FeeEditBindingModel feeEditBindingModel = feeService.mapFeeToBindingModel(residentialEntity.getFee());

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-fees-edit") : new ModelAndView("en/administration/administration-fees-edit");

        return view
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("feeEditBindingModel", feeEditBindingModel);
    }

    /**
     * Administration -> Monthly Fees -> Edit
     *
     * @param id Condominium ID
     *           POST
     */
    @PostMapping("/administration/fees/edit/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView editResidentialEntityFees(@PathVariable("id") Long id,
                                                  @Valid FeeEditBindingModel feeEditBindingModel,
                                                  BindingResult bindingResult,
                                                  @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-fees-edit") : new ModelAndView("en/administration/administration-fees-edit");

        if (bindingResult.hasErrors()) {
            return view
                    .addObject("residentialEntity", getResidentialEntity(id))
                    .addObject("feeEditBindingModel", feeEditBindingModel);
        }

        ResidentialEntity residentialEntity = getResidentialEntity(id);

        if (residentialEntity != null) {
            feeService.changeFee(residentialEntity, feeEditBindingModel);
        }
        return new ModelAndView("redirect:/administration/fees/edit/" + id);
    }

    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID
     *
     * @param id property ID
     */
    @GetMapping("/administration/fees/details/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView propertyFees(@PathVariable("id") Long id,
                                     @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Property property = propertyService.findPropertyById(id);
        OverpaymentBindingModel overpaymentBindingModel = propertyService.mapOverPaymentBindingModel(property);

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-fees") : new ModelAndView("en/administration/administration-property-fees");

        return view
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
                                       BindingResult bindingResult,
                                       @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-fees") : new ModelAndView("en/administration/administration-property-fees");

        if (bindingResult.hasErrors()) {
            return view
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
                                                 BindingResult bindingResult,
                                                 @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-fees") : new ModelAndView("en/administration/administration-property-fees");

        if (bindingResult.hasErrors()) {
            return view
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
    public ModelAndView editPropertyFee(@PathVariable("id") Long id,
                                        @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        PropertyFeeEditBindingModel propertyFeeEditBindingModel = propertyFeeService.mapPropertyFeeToBindingModel(id);
        Long propertyId = propertyFeeEditBindingModel.getPropertyId();
        propertyFeeEditBindingModel.setOverPayment(getProperty(propertyId).getOverpayment());

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-fees-edit") : new ModelAndView("en/administration/administration-property-fees-edit");

        return view.addObject("propertyFeeEditBindingModel", propertyFeeEditBindingModel);
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
                                        BindingResult bindingResult,
                                        @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-fees-edit") : new ModelAndView("en/administration/administration-property-fees-edit");

        view.addObject("propertyFeeEditBindingModel", propertyFeeEditBindingModel);

        if (bindingResult.hasErrors()) {
            return view;
        }

        if (propertyFeeEditBindingModel.getFundMmAmount() == null) {
            propertyFeeEditBindingModel.setFundMmAmount(BigDecimal.ZERO);
        }

        if (propertyFeeEditBindingModel.getFundRepairAmount() == null) {
            propertyFeeEditBindingModel.setFundRepairAmount(BigDecimal.ZERO);
        }

        if (propertyFeeEditBindingModel.getFundMmAmount().equals(BigDecimal.ZERO) && propertyFeeEditBindingModel.getFundRepairAmount().equals(BigDecimal.ZERO)) {
            return view.addObject("feeChangeFailed", true);
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
    public ModelAndView addPropertyFee(@PathVariable("id") Long id,
                                       @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-fees-add") : new ModelAndView("en/administration/administration-property-fees-add");

        return view
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
                                       BindingResult bindingResult,
                                       @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-fees-add") : new ModelAndView("en/administration/administration-property-fees-add");

        view.addObject("propertyFeeAddBindingModel", propertyFeeAddBindingModel);

        if (bindingResult.hasErrors()) {
            return view;
        }

        if (propertyFeeAddBindingModel.getFundMmAmount() == null) {
            propertyFeeAddBindingModel.setFundMmAmount(BigDecimal.ZERO);
        }

        if (propertyFeeAddBindingModel.getFundRepairAmount() == null) {
            propertyFeeAddBindingModel.setFundRepairAmount(BigDecimal.ZERO);
        }

        if (propertyFeeAddBindingModel.getFundMmAmount().equals(BigDecimal.ZERO) && propertyFeeAddBindingModel.getFundRepairAmount().equals(BigDecimal.ZERO)) {
            return view.addObject("feeCreationFailed", true);
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
     * @param id Condominium ID
     */
    @GetMapping("/administration/fees/addglobalfee/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView addGlobalFee(@PathVariable("id") Long id,
                                     @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-fees-addglobalfee") : new ModelAndView("en/administration/administration-fees-addglobalfee");

        return view.addObject("propertyFeeAddGlobalFeeBindingModel", new PropertyFeeAddGlobalFeeBindingModel());
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
                                     BindingResult bindingResult,
                                     @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-fees-addglobalfee") : new ModelAndView("en/administration/administration-fees-addglobalfee");

        view.addObject("propertyFeeAddGlobalFeeBindingModel", propertyFeeAddGlobalFeeBindingModel);

        if (bindingResult.hasErrors()) {
            return view;
        }

        ResidentialEntity residentialEntity = residentialEntityService.findResidentialEntityById(id).orElse(null);

        if (propertyFeeAddGlobalFeeBindingModel.getFundMmAmount() == null) {
            propertyFeeAddGlobalFeeBindingModel.setFundMmAmount(BigDecimal.ZERO);
        }
        if (propertyFeeAddGlobalFeeBindingModel.getFundRepairAmount() == null) {
            propertyFeeAddGlobalFeeBindingModel.setFundRepairAmount(BigDecimal.ZERO);
        }

        if (propertyFeeAddGlobalFeeBindingModel.getFundMmAmount().equals(BigDecimal.ZERO) && propertyFeeAddGlobalFeeBindingModel.getFundRepairAmount().equals(BigDecimal.ZERO)) {
            return view.addObject("globalFeeFailed", true);
        }

        if (propertyFeeService.createMassFee(residentialEntity, propertyFeeAddGlobalFeeBindingModel)) {
            view = resolveView(lang) ?
                    new ModelAndView("bg/administration/administration-fees") : new ModelAndView("en/administration/administration-fees");

            return view
                    .addObject("residentialEntity", getResidentialEntity(id))
                    .addObject("globalFeeAdded", true);
        }

        return new ModelAndView("redirect:/administration/fees/" + id);
    }

    /**
     * Method returns a ResidentialEntity
     *
     * @param id Condominium id
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

    /**
     * Language resolver
     * @param lang This value shows the language
     * @return boolean
     */
    private boolean resolveView(String lang) {
        return "bg".equals(lang);
    }
}