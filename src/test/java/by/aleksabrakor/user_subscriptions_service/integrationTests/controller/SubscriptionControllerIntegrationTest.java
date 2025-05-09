package by.aleksabrakor.user_subscriptions_service.integrationTests.controller;

import by.aleksabrakor.user_subscriptions_service.dto.UserDto;
import by.aleksabrakor.user_subscriptions_service.repository.SubscriptionRepository;
import by.aleksabrakor.user_subscriptions_service.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class SubscriptionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        System.out.println("Очистка БД - настройка перед тестом");
        subscriptionRepository.deleteAll();
        userRepository.deleteAll();
    }

    private Long createTestUserReturnId() throws Exception {
        String userJson = """
                {
                    "name": "Test User",
                    "email": "test@example.com"
                }
                """;
        String createUserResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andReturn().getResponse().getContentAsString();

        //2. Извлекаем ID созданного пользователя
        Long userId = objectMapper.readValue(createUserResponse, UserDto.class).getId();
        return userId;
    }

    @Test
    @DisplayName("Сохранение подписки для юзера, если юзер существует")
    void addSubscriptionToUser_ShouldReturnSubscription_WhenUserIdExists() throws Exception {
        // Подготовка тестовых данных
        //  создаем пользователя
        Long userId = createTestUserReturnId();

        //Входящие данные
        String subscriptionJson = """
                {
                     "serviceTitle": "Яндекс.Плюс"
                }
                """;
        // Выполнение запроса и проверка результата
        mockMvc.perform(post("/subscriptions/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscriptionJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceTitle").value("Яндекс.Плюс"))
                .andExpect(jsonPath("$.userId").value(userId))
        ;
    }

    @Test
    @DisplayName("Вернет BadRequest 400 при попытке сохранения подписки, с невалидными полями")
    void addSubscriptionToUser_ShouldReturnBadRequest400_WhenInvalidInput() throws Exception {
        // Подготовка тестовых данных
        //Создаем пользователя
        Long userId = createTestUserReturnId();

        //входящие данные
        String subscriptionJson = """
                {
                     "serviceTitle": null
                }
                """;
        // Выполнение запроса и проверка результата
        mockMvc.perform(post("/subscriptions/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscriptionJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Вернет NotFound 404 при попытке сохранения подписки для юзера, если юзер не существует")
    void addSubscriptionToUser_ShouldReturnSubscription_WhenUserIdNotExists() throws Exception {
        // Подготовка тестовых данных
        Long userId = 1L;

        String subscriptionJson = """
                {
                     "serviceTitle": "Яндекс.Плюс"
                }
                """;
        // Выполнение запроса и проверка результата
        mockMvc.perform(post("/subscriptions/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscriptionJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Поиск списка всех подписок из БД")
    void findAllSubscriptions_ShouldReturnAllSubscriptions() throws Exception {
        // Подготовка тестовых данных
        // создаем пользователя
        Long userId = createTestUserReturnId();

        //Создаем подписки для этого пользователя
        String subscriptionJson1 = """
                {
                     "serviceTitle": "Яндекс.Плюс"
                }
                """;
        String subscriptionJson2 = """
                {
                     "serviceTitle": "Яндекс"
                }
                """;
        mockMvc.perform(post("/subscriptions/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(subscriptionJson1));
        mockMvc.perform(post("/subscriptions/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(subscriptionJson2));

        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].serviceTitle").value("Яндекс.Плюс"))
                .andExpect(jsonPath("$[1].serviceTitle").value("Яндекс"));
    }

    @Test
    @DisplayName("Вернет пустой список подписок, если в БД их нет")
    void findAllSubscriptions_ShouldReturnEmptyList() throws Exception {
        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Поиск подписки по ee id, если существует")
    void getSubscriptionById_shouldReturnSubscription_WhenIdExists() throws Exception {
        // Подготовка тестовых данных
        //создаем пользователя
        Long userId = createTestUserReturnId();

        //Создаем подписку для этого пользователя
        String subscriptionJson1 = """
                {
                     "serviceTitle": "Яндекс.Плюс"
                }
                """;
        String createSubResponse = mockMvc.perform(post("/subscriptions/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscriptionJson1))
                .andReturn().getResponse().getContentAsString();

        //Извлекаем ID подписи
        Long subId = objectMapper.readValue(createSubResponse, UserDto.class).getId();

        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/subscriptions/{id}", subId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subId))
                .andExpect(jsonPath("$.serviceTitle").value("Яндекс.Плюс"))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    @DisplayName("Вернет NotFound 404 при поиске подписки по id, если id не существует")
    void getSubscriptionById_ShouldReturnNotFound_WhenSubIdNotExists() throws Exception {
        // Входящие параметры
        Long subId = 999L;

        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/subscriptions/{id}", subId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Object was not found: Подписка на сервис с id = 999 не найдена"));
    }


    @Test
    @DisplayName("Поиск списка  подписок для пользователя, если пользователь существует")
    void getUserSubscriptions_ShouldReturnAllSubscriptions() throws Exception {
        // Подготовка тестовых данных
        // создаем пользователя
        Long userId = createTestUserReturnId();

        // Создаем подписки для этого пользователя
        String subscriptionJson1 = """
                {
                     "serviceTitle": "Яндекс.Плюс"
                }
                """;
        String subscriptionJson2 = """
                {
                     "serviceTitle": "Яндекс"
                }
                """;
        mockMvc.perform(post("/subscriptions/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(subscriptionJson1));
        mockMvc.perform(post("/subscriptions/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(subscriptionJson2));

        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/subscriptions/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].serviceTitle").value("Яндекс.Плюс"))
                .andExpect(jsonPath("$[1].serviceTitle").value("Яндекс"));
    }

    @Test
    @DisplayName("Вернет пустой список, если подписок нет, если пользователь существует")
    void getUserSubscriptions_ShouldReturnEmptyList() throws Exception {
        // Подготовка тестовых данных
        //  создаем пользователя
        Long userId = createTestUserReturnId();

        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/subscriptions/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    @DisplayName("Обновление полей подписки, если sub_id существует ")
    void updateSubscription_ShouldUpdateFields_WhenSubIdExists() throws Exception {
        // Подготовка тестовых данных
        // создаем пользователя
        Long userId = createTestUserReturnId();

        //Создаем подписку для этого пользователя
        String subscriptionJson = """
                {
                     "serviceTitle": "Яндекс.Плюс"
                }
                """;
        String createSubResponse = mockMvc.perform(post("/subscriptions/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscriptionJson))
                .andReturn().getResponse().getContentAsString();

        //Извлекаем ID подписи
        Long subId = objectMapper.readValue(createSubResponse, UserDto.class).getId();

        String updateSubJson = """
                {
                    "serviceTitle": "UpdateЯндекс.Плюс",
                     "plan": "update"
                }
                """;

        // Выполнение запроса и проверка результата
        mockMvc.perform(put("/subscriptions/{subscription_id}", subId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateSubJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subId))
                .andExpect(jsonPath("$.serviceTitle").value("UpdateЯндекс.Плюс"))
                .andExpect(jsonPath("$.plan").value("update"))
        ;
    }

    @Test
    @DisplayName("Обновление полей подписки, если sub_id  не существует ")
    void updateSubscription_ShouldReturns404_WhenSubNotExists() throws Exception {
        Long subId = 999L;
        String updateSubJson = """
                {
                    "serviceTitle": "UpdateЯндекс.Плюс",
                     "plan": "update"
                }
                """;

        // Выполнение запроса и проверка результата
        mockMvc.perform(put("/subscriptions/{subscription_id}", subId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateSubJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Object was not found: Подписка на сервис с id = 999 не найдена"));
    }


    @Test
    @DisplayName("Удаление подписки по существующему sub_id и user_id")
    void deleteSubscriptionFromUser_WhenSubIdAndUserIdExists() throws Exception {
        // Подготовка тестовых данных
        // создаем пользователя
        Long userId = createTestUserReturnId();

        //Создаем подписку для этого пользователя
        String subscriptionJson = """
                {
                     "serviceTitle": "Яндекс.Плюс"
                }
                """;
        String createSubResponse = mockMvc.perform(post("/subscriptions/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscriptionJson))
                .andReturn().getResponse().getContentAsString();

        //Извлекаем ID подписи
        Long subId = objectMapper.readValue(createSubResponse, UserDto.class).getId();

        // Выполнение запроса и проверка результата
        mockMvc.perform(delete("/subscriptions/{subscriptionId}/users/{userId}", subId, userId))
                .andExpect(status().isOk());

        // Проверяем, что подписка удалена
        mockMvc.perform(get("/subscriptions/{id}", subId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Вернет NotFound 404 при попытке удаление подписки по не существующему sub_id")
    void deleteSubscriptionFromUser_ShouldReturns404_WhenUserNotExists() throws Exception {

        Long subId = 999L;
        Long userId = 999L;

        // Выполнение запроса и проверка результата
        mockMvc.perform(delete("/subscriptions/{subscriptionId}/users/{userId}", subId, userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Object was not found: Подписка на сервис с id = 999 не найдена"));
    }

    @Test
    @DisplayName("Вернет NotFound 404 при попытке удаление подписки по существующему sub_id, но если user_id не существует")
    void deleteSubscriptionFromUser_ShouldThrowNotFoundException_WhenUserIdNotExists() throws Exception {
        // Подготовка тестовых данных
        Long userId = createTestUserReturnId();

        //Создаем подписку для этого пользователя
        String subscriptionJson = """
                {
                     "serviceTitle": "Яндекс.Плюс"
                }
                """;
        String createSubResponse = mockMvc.perform(post("/subscriptions/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscriptionJson))
                .andReturn().getResponse().getContentAsString();

        // Извлекаем ID подписи
        Long subId = objectMapper.readValue(createSubResponse, UserDto.class).getId();

        Long invalidUserId = 999L;

        // Выполнение запроса и проверка результата
        mockMvc.perform(delete("/subscriptions/{subscriptionId}/users/{userId}", subId, invalidUserId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Object was not found: Юзер с id = 999 не найден"));
    }

    @Test
    @DisplayName("ТОП-3 популярных подписок")
    void getTop3PopularSubscriptions_ShouldReturnTop3Subscriptions() throws Exception {
        // Подготовка тестовых данных
        Long userId1 = createTestUserReturnId();

        //Создаем подписку для этого пользователя
        String subscriptionJson = """
                {
                     "serviceTitle": "Яндекс.Плюс"
                }
                """;
        String subscriptionJson1 = """
                {
                     "serviceTitle": "Яндекс"
                }
                """;
        String subscriptionJson2 = """
                {
                     "serviceTitle": "Яндекс.Плюс+"
                }
                """;
        String subscriptionJson3 = """
                {
                     "serviceTitle": "Яндекс"
                }
                """;

        mockMvc.perform(post("/subscriptions/users/{userId}", userId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscriptionJson))
                .andReturn().getResponse().getContentAsString();
        mockMvc.perform(post("/subscriptions/users/{userId}", userId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscriptionJson1))
                .andReturn().getResponse().getContentAsString();
        mockMvc.perform(post("/subscriptions/users/{userId}", userId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscriptionJson2))
                .andReturn().getResponse().getContentAsString();
        mockMvc.perform(post("/subscriptions/users/{userId}", userId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscriptionJson3))
                .andReturn().getResponse().getContentAsString();


        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/subscriptions/top")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
        ;
    }

    @Test
    @DisplayName("ТОП-3 популярных подписок")
    void getTop3PopularSubscriptions_ShouldReturnEmptySubscriptions() throws Exception {

        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/subscriptions/top")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
        ;
    }
}