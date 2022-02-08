//package com.datn.doffice.utils;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.i18n.LocaleContextHolder;
//
//import java.text.MessageFormat;
//import java.util.Locale;
//
//@Slf4j
//public abstract class MessageUtils {
//    private static final String BASE_NAME = "i18n/messages";
//
//    private MessageUtils(){}
//    public static String getMessage(String code, Locale locale) {
//        return getMessage(code, locale, null);
//    }
//
//    public static String getMessage(String code, Locale locale, Object... args) {
//        UTF8ResourceBundle resourceBundle = new UTF8ResourceBundle(BASE_NAME, locale);
//        String message;
//        try {
//            message = resourceBundle.getString(code);
//            message = MessageFormat.format(message, args);
//        } catch (Exception ex) {
//            log.info(">>> Can not get message with code {}", code);
//            log.info(ex.getMessage(), ex);
//            message = code;
//        }
//
//        return message;
//    }
//
//    public static String getMessage(String code) {
//        LocaleContextHolder.setLocale(new Locale("vi", "VN"));
//        return getMessage(code, LocaleContextHolder.getLocale(), null);
//    }
//
//    public static String getMessage(String code, Object... args) {
//        LocaleContextHolder.setLocale(new Locale("vi", "VN"));
//        return getMessage(code, LocaleContextHolder.getLocale(), args);
//    }
//}
