package by.aleksabrakor.user_subscriptions_service.controller;

import by.aleksabrakor.user_subscriptions_service.dto.UpdateUserRequest;
import by.aleksabrakor.user_subscriptions_service.dto.UserDto;
import by.aleksabrakor.user_subscriptions_service.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/users")
@Tag(name = "Users Controller", description = "API c CRUD операциями для users")
public interface UserController {

    @Operation(summary = "Создание нового пользователя")
    @ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = UserDto.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "404", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "500", content = @Content)
    @PostMapping
    UserDto createUser(@RequestBody UserDto userDto);


    @Operation(summary = "Получение списка всех пользователя")
    @ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = UserDto.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "400", content = @Content)
    @ApiResponse(responseCode = "404", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "500", content = @Content)
    @GetMapping()
    List<UserDto> findAllUsers();


    @Operation(summary = "Получение пользователя по ID")
    @ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = UserDto.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "400", content =
    @Content(schema = @Schema(implementation = UserDto.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "404", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "500", content = @Content)
    @GetMapping("/{id}")
    UserDto getUser(@PathVariable("id") Long id);


    @Operation(summary = "Обновление пользователя по ID")
    @ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = UserDto.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "400", content =
    @Content(schema = @Schema(implementation = UserDto.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "404", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "500", content = @Content)
    @PutMapping("/{id}")
    UserDto updateUser(@RequestBody UpdateUserRequest userDto,
                       @PathVariable("id") Long id);


    @Operation(summary = "Удаление пользователя по ID")
    @ApiResponse(responseCode = "200", content = @Content)
    @ApiResponse(responseCode = "400", content = @Content)
    @ApiResponse(responseCode = "404", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "500", content = @Content)
    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable("id") Long id);
}
