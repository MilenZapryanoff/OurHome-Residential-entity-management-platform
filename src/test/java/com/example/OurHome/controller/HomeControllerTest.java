package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.LanguageService;
import com.example.OurHome.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class HomeControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private LanguageService languageService;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(languageService.resolveView(anyString(), eq("index"))).thenReturn("index");
    }

    @Test
    public void testHomeAuthenticatedUser() {
        UserEntity mockUserEntity = new UserEntity();
        when(userService.findUserByEmail(anyString())).thenReturn(mockUserEntity);
        when(userService.getUserViewData(mockUserEntity)).thenReturn(new UserViewModel()); // Mock the UserViewModel

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("username");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String mockLang = "bg";

        ModelAndView modelAndView = homeController.home(mockLang);

        assertEquals("index", modelAndView.getViewName());
        assertEquals(UserViewModel.class, modelAndView.getModel().get("userViewModel").getClass());
    }

    @Test
    public void testHomeAnonymousUser() {
        Authentication authentication = mock(AnonymousAuthenticationToken.class);
        when(authentication.getName()).thenReturn("anonymousUser");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String mockLang = "bg";

        ModelAndView modelAndView = homeController.home(mockLang);

        assertEquals("index", modelAndView.getViewName());
        assertNull(modelAndView.getModel().get("userViewModel"));
    }


}