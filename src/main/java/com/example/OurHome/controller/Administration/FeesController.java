package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.*;
import com.example.OurHome.model.dto.BindingModels.Fee.FeeEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeAddBindingModel;
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
     * @param id Residential entity ID
     */
    @GetMapping("/administration/fees/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityFees(@PathVariable("id") Long id) {

        return new ModelAndView("administration-fees")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Administration -> Monthly Fees -> Edit
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

        return new ModelAndView("administration-fees-edit")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("feeEditBindingModel", feeEditBindingModel);
    }

    /**
     * Administration -> Monthly Fees -> Edit
     * @param id Residential entity ID
     * POST
     */
    @PostMapping("/administration/fees/edit/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView editResidentialEntityFees(@PathVariable("id") Long id,
                                                  @Valid FeeEditBindingModel feeEditBindingModel,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration-fees-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("residentialEntity", getResidentialEntity(id))
                    .addObject("feeEditBindingModel", feeEditBindingModel);
        }

        ResidentialEntity residentialEntity = getResidentialEntity(id);
        if (residentialEntity == null) {
            return new ModelAndView("redirect:/administration/fees/" + id);
        }
        feeService.changeFee(residentialEntity, feeEditBindingModel);

        return new ModelAndView("redirect:/administration/fees/" + id);
    }

    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID
     * @param id property ID
     */
    @GetMapping("/administration/fees/details/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView propertyFees(@PathVariable("id") Long id) {

        return new ModelAndView("administration-property-fees")
                .addObject("userViewModel", getUserViewModel())
                .addObject("property", getProperty(id));
    }

    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID -> Edit fee
     * @param id PropertyFee ID
     */
    @GetMapping("/administration/fees/details/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyFeeModeratorAccess(#id, authentication)")
    public ModelAndView editPropertyFee(@PathVariable("id") Long id) {

        PropertyFeeEditBindingModel propertyFeeEditBindingModel = propertyFeeService.mapPropertyFeeToBindingModel(id);
        Long propertyId = propertyFeeEditBindingModel.getPropertyId();
        propertyFeeEditBindingModel.setOverPayment(getProperty(propertyId).getOverpayment());

        return new ModelAndView("administration-property-fees-edit")
                .addObject("userViewModel", getUserViewModel())
                .addObject("propertyFeeEditBindingModel", propertyFeeEditBindingModel);
    }


    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID -> Edit fee
     * @param id PropertyFee ID
     * POST
     */
    @PostMapping("/administration/fees/details/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyFeeModeratorAccess(#id, authentication)")
    public ModelAndView editPropertyFee(@PathVariable("id") Long id,
                                        @Valid PropertyFeeEditBindingModel propertyFeeEditBindingModel,
                                        BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration-property-fees-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("propertyFeeEditBindingModel", propertyFeeEditBindingModel);
        }

        propertyFeeService.modifyFee(id, propertyFeeEditBindingModel);
        propertyService.setOverpayment(propertyFeeEditBindingModel);

        return new ModelAndView("redirect:/administration/fees/details/" + propertyFeeEditBindingModel.getPropertyId());
    }

    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID -> Manually add fee
     * @param id PropertyFee ID
     */
    @GetMapping("/administration/fees/details/add/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView addPropertyFee(@PathVariable("id") Long id) {

        return new ModelAndView("administration-property-fees-add")
                .addObject("userViewModel", getUserViewModel())
                .addObject("propertyFeeAddBindingModel", new PropertyFeeAddBindingModel());
    }

    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID -> Manually add fee
     * @param id PropertyFee ID
     * POST
     */
    @PostMapping("/administration/fees/details/add/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView addPropertyFee(@PathVariable("id") Long id,
                                       @Valid PropertyFeeAddBindingModel propertyFeeAddBindingModel,
                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration-property-fees-add")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("propertyFeeAddBindingModel", propertyFeeAddBindingModel);
        }

        Property property = propertyService.findPropertyById(id);
        propertyFeeService.addFee(property, propertyFeeAddBindingModel);

        return new ModelAndView("redirect:/administration/fees/details/" + id);
    }


    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID -> Delete fee
     * @param id PropertyFee ID
     * POST
     */
    @PostMapping("/administration/fees/details/delete/{id}")
    @PreAuthorize("@securityService.checkPropertyFeeModeratorAccess(#id, authentication)")
    public ModelAndView deletePropertyFee(@PathVariable("id") Long id) {

        PropertyFee propertyFee = propertyFeeService.findPropertyFeeById(id);
        Long propertyId = propertyFee.getProperty().getId();

        propertyFeeService.deleteFee(propertyFee);

        return new ModelAndView("redirect:/administration/fees/details/" + propertyId);
    }

    /**
     * Administration -> Monthly Fees -> PropertyFees by property ID -> Change payment status
     * @param id PropertyFee ID
     * POST
     */
    @PostMapping("/administration/fees/changePaymentStatus/{id}")
    @PreAuthorize("@securityService.checkPropertyFeeModeratorAccess(#id, authentication)")
    public ModelAndView editPropertyFeesPaymentStatus(@PathVariable("id") Long id) {

        PropertyFee propertyFee = propertyFeeService.findPropertyFeeById(id);
        propertyFeeService.changePaymentStatus(propertyFee);
        return new ModelAndView("redirect:/administration/fees/details/" + propertyFee.getProperty().getId());
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