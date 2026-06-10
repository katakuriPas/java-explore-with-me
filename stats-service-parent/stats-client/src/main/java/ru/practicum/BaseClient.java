package ru.practicum;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    protected <T> ResponseEntity<Object> get(String path, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    /*
    makeAndSendRequest принимает:
    1. method - тип запроса (GET, POST и другие)
    2. path - эндпоинт (/hit, /stats)
    3. parameters - карта параметров для URL (даты и тд и тп)
    4. body - Java-объекты (DTO), которые нужно отправить
     */
    private <T> ResponseEntity<Object> makeAndSendRequest(
            HttpMethod method,
            String path,
            Map<String, Object> parameters,
            T body
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Тут отправляем JSON
        headers.setAccept(List.of(MediaType.APPLICATION_JSON)); // а тут получаем JSON

        HttpEntity<T> requestEntity = new HttpEntity<>(body, headers); // Упаковываем всё в "конверт" HttpEntity (тело + заголовок)

        ResponseEntity<Object> response;
        try {
            if (parameters != null) {
                // Если есть параметры (как в методе /stats?start=...), RestTemplate сам подставит их в URL
                response = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                response = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            // ЕСЛИ ЧТО-ТО ПОШЛО НЕ ТАК (сервер упал или прислал ошибку):
            // Чтобы наше основное приложение не вылетело по Exception, мы берем ошибку за яйца
            // и вежливо ^^ возвращаем её в виде ResponseEntity со старым статус-кодом
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return response;
    }
}