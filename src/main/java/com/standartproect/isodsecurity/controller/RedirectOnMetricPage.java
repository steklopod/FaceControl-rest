package com.standartproect.isodsecurity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для переадресации с корневого адреса на страницу метрики приложения.
 * Страница ".../metrics" отображает актуальную информацию о состоянии приложения.
 */

@Controller
public class RedirectOnMetricPage {
    @RequestMapping(value = "/")
    public String metrics() {
        return "metrics";
    }
}
