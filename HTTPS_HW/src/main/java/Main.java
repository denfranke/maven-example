import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // Создаем и настраиваем HTTP клиент
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // таймаут подключения
                        .setSocketTimeout(30000)    // таймаут получения данных
                        .setRedirectsEnabled(false) // не следовать редиректам
                        .build())
                .build()) {

            // Создаем GET запрос
            HttpGet request = new HttpGet("https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats");

            // Выполняем запрос
            try (CloseableHttpResponse response = httpClient.execute(request)) {

                // Проверяем статус ответа
                if (response.getStatusLine().getStatusCode() != 200) {
                    System.err.println("Ошибка при запросе: " + response.getStatusLine().getStatusCode());
                    return;
                }

                // Создаем ObjectMapper для работы с JSON
                ObjectMapper mapper = new ObjectMapper();

                // Преобразуем JSON в список объектов CatFact
                List<CatFact> facts = mapper.readValue(
                        response.getEntity().getContent(),
                        new TypeReference<List<CatFact>>() {}
                );

                System.out.println("Всего фактов получено: " + facts.size());

                // Фильтруем: оставляем только те, у которых upvotes не null и больше 0
                List<CatFact> filteredFacts = facts.stream()
                        .filter(fact -> fact.getUpvotes() != null && fact.getUpvotes() > 0)
                        .collect(Collectors.toList());

                System.out.println("\n=== Факты, за которые голосовали (upvotes > 0) ===");
                filteredFacts.forEach(fact -> {
                    System.out.println("ID: " + fact.getId());
                    System.out.println("Голосов: " + fact.getUpvotes());
                    System.out.println("Текст: " + fact.getText());
                    System.out.println("---");
                });

                System.out.println("Найдено фактов с голосами: " + filteredFacts.size());

            } catch (IOException e) {
                System.err.println("Ошибка при обработке ответа: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.err.println("Ошибка при создании HTTP клиента: " + e.getMessage());
            e.printStackTrace();
        }
    }
}