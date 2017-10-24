package com.standartproect.isodsecurity.controller;



import com.standartproect.isodsecurity.entities.Request;
import com.standartproect.isodsecurity.entities.Response;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;

/**
 * REST-клиент. Класс отвечает за отправку фотографий для распознавания и обработку ответа.
 * Для работы с БД используется класс {@link com}
 */

@Component
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final RestTemplate restTemplate;


    public Client() {
        ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory();
        this.restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    }

    public Response sendRequest(Request request, String requestUrl){
//        String requestUrl = "http://localhost:8180/classify";
        HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Request> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Response> restResponse =
                restTemplate.exchange(requestUrl, HttpMethod.POST, entity, Response.class);

        HttpStatus statusCode = restResponse.getStatusCode();

        if(statusCode.is2xxSuccessful()) {
                 String body = restResponse.toString();
                 logger.debug("Содержание HTTP ответа: " + body);
            Response response = restResponse.getBody();
                String model = response.getModel();
                if(model.contains("trash")){
                 logger.info("ТС не распознано. Ответ от сервиса Luna Cars получен. ");
                }else
                 logger.info("ТС распознано. Ответ от сервиса Luna Cars получен. ");
                 logger.info("Модель - " + model);
            return response;
                }else
                 logger.error(dateFormat + "Статус ответа: " + String.valueOf(statusCode.value()));
                 logger.error("Ответ сервиса Luna Cars: " + String.valueOf(statusCode.getReasonPhrase()));
            return null;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 20000;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(config)
                .build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }
}
