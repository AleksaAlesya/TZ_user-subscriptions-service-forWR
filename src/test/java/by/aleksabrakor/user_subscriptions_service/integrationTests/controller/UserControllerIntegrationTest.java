package by.aleksabrakor.user_subscriptions_service.integrationTests.controller;

import by.aleksabrakor.user_subscriptions_service.dto.UserDto;
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
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        System.out.println("Очистка БД - настройка перед тестом");
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Сохранение юзера")
    void createUser_ShouldReturnCreatedUser() throws Exception {
        // Подготовка тестовых данных
        String userJson = """
                {
                    "name": "Test User",
                    "email": "test@example.com"
                }
                """;
        // Выполнение запроса и проверка результата
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Вернет BadRequest 400 при попытке сохранения юзера, с невалидными полями")
    void createUser_ShouldReturnBadRequest400_WhenInvalidInput() throws Exception {
        // Подготовка тестовых данных
        String userJson = """
                {
                    "name": ""
                }
                """;

        // Выполнение запроса и проверка результата
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Поиск списка всех юзеров")
    void findAllUsers_ShouldReturnAllUsers() throws Exception {
        //  создаем 2 пользователя
        String userJson1 = """
                {
                    "name": "Test User1",
                    "email": "test1@example.com"
                }
                """;
        String userJson2 = """
                {
                    "name": "Test User2",
                    "email": "test2@example.com"
                }
                """;
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson1));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson2));


        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Test User1"))
                .andExpect(jsonPath("$[1].name").value("Test User2"));
    }

    @Test
    @DisplayName("Поиск списка всех юзеров, если список пуст")
    void findAllUsers_ShouldReturnEmptyList() throws Exception {

        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Поиск юзера по существующему id")
    void getUser_ShouldReturnUser_WhenUserExists() throws Exception {
        // Подготовка тестовых данных
        // 1. создаем пользователя
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

        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Вернет NotFound 404 при поиске юзера, если id не существует")
    void getUser_ShouldReturnNotFound_WhenUserNotExists() throws Exception {
        // Подготовка тестовых данных
        Long userId = 999L;

        // Выполнение запроса и проверка результата
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Object was not found: Юзер с id = 999 не найден"));
    }

    @Test
    @DisplayName("Обновление юзера, по существующему id")
    void updateUser_ShouldReturnUser_WhenUserExists() throws Exception {
        // Подготовка тестовых данных
        // 1. создаем пользователя
        String userJson = """
                {
                    "name": "Test User",
                    "email": "test1@example.com"
                }
                """;
        String createUserResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andReturn().getResponse().getContentAsString();

        //2. Извлекаем ID созданного пользователя
        Long userId = objectMapper.readValue(createUserResponse, UserDto.class).getId();

        String updatedUserJson = """
                {
                    "name": "Updated User"
                }
                """;

        // Выполнение запроса и проверка результата
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("test1@example.com"))
        ;
    }

    @Test
    @DisplayName("Вернет NotFound 404 при попытке обновления юзера, если id не существует")
    void updateUser_ShouldReturns404_WhenUserNotExists() throws Exception {
        // Подготовка тестовых данных

        Long userId = 999L;

        String userJson = """
                {
                    "name": "Updated User",
                    "email": "test@example.com"
                }
                """;

        // Выполнение запроса и проверка результата
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Object was not found: Юзер с id = 999 не найден"))
        ;
    }

    @Test
    @DisplayName("Удаление юзера, по существующему id")
    void deleteUser_ShouldDeleteUser_WhenUserExist() throws Exception {
        // Подготовка тестовых данных
        // 1. создаем пользователя
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

        // Выполнение запроса и проверка результата
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        // Проверяем, что пользователь удален
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Вернет NotFound 404 при попытке удаление юзера, по  не существующему id")
    void deleteUser_ShouldReturns404_WhenUserNotExists() throws Exception {
        // Подготовка тестовых данных
        Long userId = 999L;

        // Выполнение запроса и проверка результата
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Object was not found: Юзер с id = 999 не найден"));
    }
}