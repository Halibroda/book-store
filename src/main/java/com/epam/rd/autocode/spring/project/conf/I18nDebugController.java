package com.epam.rd.autocode.spring.project.conf;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContextUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/__i18n")
@RequiredArgsConstructor
public class I18nDebugController {

    private final MessageSource messageSource;

    @GetMapping("/locale")
    public Map<String, Object> locale(HttpServletRequest req) {
        Locale effective = RequestContextUtils.getLocale(req);
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("effectiveLocale", effective.toLanguageTag());
        m.put("acceptLanguage", Optional.ofNullable(req.getHeader("Accept-Language")).orElse(""));
        return m;
    }

    @GetMapping("/messages")
    public Map<String, Object> messages(@RequestParam(defaultValue = "books.title") String key) {
        Map<String,Object> m = new LinkedHashMap<>();
        Locale en = Locale.ENGLISH;
        Locale uk = Locale.forLanguageTag("uk");

        String enVal = messageSource.getMessage(key, null, en);
        String ukVal = messageSource.getMessage(key, null, uk);

        m.put("key", key);
        m.put("EN", enVal);
        m.put("UK", ukVal);
        m.put("UK_codepoints", codepoints(ukVal));
        m.put("UK_bytes_UTF8", bytesHex(ukVal.getBytes(StandardCharsets.UTF_8)));
        return m;
    }

    private String codepoints(String s){
        StringBuilder sb = new StringBuilder();
        s.codePoints().forEach(cp -> sb.append(String.format("U+%04X ", cp)));
        return sb.toString().trim();
    }
    private String bytesHex(byte[] a){
        StringBuilder sb = new StringBuilder();
        for (byte b : a) sb.append(String.format("%02X ", b));
        return sb.toString().trim();
    }
}
