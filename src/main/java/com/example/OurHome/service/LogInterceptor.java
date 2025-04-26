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

    // Създава логър за класа
    private static final Logger logger = Logger.getLogger(LogInterceptor.class.getName());

    // Конструктор, който инициализира лог файла
    public LogInterceptor() {
        initializeFileHandler();
    }

    // Метод за настройка на лог файла
    private void initializeFileHandler() {
        String logFilePath = "src/main/resources/HttpRequests.log"; // Път до файла с логове

        try {
            FileHandler fileHandler = new FileHandler(logFilePath, true); // Добавя логовете във файла, без да го презаписва
            fileHandler.setFormatter(new SimpleFormatter() {
                private static final String format = "%1$tF %1$tT %2$s%n"; // Формат: YYYY-MM-DD HH:MM:SS Message

                @Override
                public synchronized String format(java.util.logging.LogRecord record) {
                    return String.format(format, record.getMillis(), record.getMessage());
                }
            });
            logger.addHandler(fileHandler); // Добавя FileHandler към логъра
        } catch (IOException e) {
            e.printStackTrace(); // Отпечатва грешка при проблем с лог файла
        }
    }

    // Метод, който се изпълнява преди обработката на всяка заявка
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logRequestDetails(request); // Логва информация за заявката
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    // Метод за логване на детайлите на заявката
    private void logRequestDetails(HttpServletRequest request) {
        String requestURI = request.getRequestURI(); // Взима URI на заявката

        // Изключване на статични ресурси от логването
        if (!requestURI.endsWith(".css")
                && !requestURI.endsWith(".js")
                && !requestURI.endsWith(".png")
                && !requestURI.endsWith(".jpg")
                && !requestURI.endsWith(".svg")
                && !requestURI.contains("/webfonts/")) {

            // Взима User-Agent (идентифицира браузъра/клиента)
            String userAgent = request.getHeader("User-Agent");
            // Взима референтния URL (ако заявката идва от друга страница)
            String referer = request.getHeader("Referer");
            // Взима времето на създаване на сесията (ако има активна сесия)
            String sessionCreationTime = request.getSession(false) != null
                    ? new java.util.Date(request.getSession().getCreationTime()).toString()
                    : "No session";
            // Взима GET параметрите (ако има такива)
            String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";

            // Съставяне на лог съобщението
            String requestInfo = String.format(
                    "Requested URL:%s%s, Method:%s, Remote Address:%s, Remote host:%s, Requested Session id:%s, User-Agent:%s, Referer:%s, Session Created:%s",
                    request.getRequestURL(),
                    queryString,
                    request.getMethod(),
                    request.getRemoteAddr(),
                    request.getRemoteHost(),
                    request.getRequestedSessionId(),
                    userAgent,
                    referer != null ? referer : "No referer",
                    sessionCreationTime
            );

            logger.info(requestInfo); // Записва лог съобщението
        }
    }
}