package by.aleksabrakor.user_subscriptions_service.controller.impl;

import by.aleksabrakor.user_subscriptions_service.controller.UserController;
import by.aleksabrakor.user_subscriptions_service.dto.UpdateUserRequest;
import by.aleksabrakor.user_subscriptions_service.dto.UserDto;
import by.aleksabrakor.user_subscriptions_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {
    private final UserService userService;


    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("POST /users — создание нового юзера");

        return userService.saveUser(userDto);
    }

    @GetMapping()
    public List<UserDto> findAllUsers() {
        log.info("GET /tasks — получение списка всех юзеров.");

        return userService.findAllUsers();
    }

    @GetMapping("/{user_id}")
    public UserDto getUser(@PathVariable("user_id") Long userId) {
        log.info("GET /users/{user_id} — получение юзера по ID.");

        return userService.findUserById(userId);
    }

    @PutMapping("/{user_id}")
    public UserDto updateUser(@RequestBody @Valid UpdateUserRequest userDto,
                              @PathVariable("user_id") Long userId) {
        log.info("PUT /Users/{user_id} — обновление юзера.");

        return userService.updateUser(userDto, userId);
    }


    @DeleteMapping("/{user_id}")
    public void deleteUser(@PathVariable("user_id") Long userId) {
        log.info("DELETE /users/{user_id} — удаление юзера.");

        userService.deleteUser(userId);
    }
}
