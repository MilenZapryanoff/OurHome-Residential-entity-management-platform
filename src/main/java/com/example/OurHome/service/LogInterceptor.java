package com.example.OurHome.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final Logger logger = Logger.getLogger(LogInterceptor.class.getName());

    public LogInterceptor() {
        initializeFileHandler();
    }

    private void initializeFileHandler() {
        String logFilePath = "src/main/resources/OurHomeApplication.log"; // Relative path within the project

        try {
            FileHandler fileHandler = new FileHandler(logFilePath, true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logRequestDetails(request);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private void logRequestDetails(HttpServletRequest request) {

        String requestURI = request.getRequestURI();

        if (!requestURI.endsWith(".css")
                && !requestURI.endsWith(".js")
                && !requestURI.endsWith(".png")
                && !requestURI.endsWith(".jpg")
                && !requestURI.contains("/webfonts/")) {
            String requestInfo = String.format("Requested URL:%s, Method:%s, Remote Address:%s, Remote host:%s, Requested Session id:%s",
                    request.getRequestURL(),
                    request.getMethod(),
                    request.getRemoteAddr(),
                    request.getRemoteHost(),
                    request.getRequestedSessionId());
            logger.info(requestInfo);
        }
    }
}