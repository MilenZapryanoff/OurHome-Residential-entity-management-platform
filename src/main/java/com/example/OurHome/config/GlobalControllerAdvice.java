package com.example.OurHome.config;

import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;

    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    /**
     * Globally exposing currentURI to all resultViews. Used for active menu red color.
     *
     * @return current URI
     */

    @ModelAttribute("currentURI")
    public String currentURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * Globally exposing logged user
     *
     * @return loggedUser
     */
    @ModelAttribute("loggedUser")
    public UserViewModel loggedUser() {
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
            return userService.getUserViewData(loggedUser);
        }
        return null;
    }
}