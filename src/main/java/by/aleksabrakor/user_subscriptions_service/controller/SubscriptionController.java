package by.aleksabrakor.user_subscriptions_service.controller;

import by.aleksabrakor.user_subscriptions_service.dto.SubscriptionDto;
import by.aleksabrakor.user_subscriptions_service.dto.UpdateSubscriptionRequest;
import by.aleksabrakor.user_subscriptions_service.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/subscriptions")
@Tag(name = "Subscription Controller", description = "API c CRUD операциями для subscriptions")
public interface SubscriptionController {


    @Operation(summary = "Добавление подписки на сервис пользователю по его id")
    @ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = SubscriptionDto.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "400", content = @Content)
    @ApiResponse(responseCode = "404", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "500", content = @Content)
    @PostMapping("/users/{user_id}")
    SubscriptionDto addSubscriptionToUser(@PathVariable ("user_id") Long userId,
                                          @RequestBody SubscriptionDto subscriptionDto);


    @Operation(summary = "Получение списка всех существующих подписок на сервисы")
    @ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = SubscriptionDto.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "400", content = @Content)
    @ApiResponse(responseCode = "404", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "500", content = @Content)
    @GetMapping()
    List<SubscriptionDto> findAllSubscriptions();

    @Operation(summary = "Получение подписки на сервис по subscription_id")
    @ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = SubscriptionDto.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "400", content = @Content)
    @ApiResponse(responseCode = "404", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "500", content = @Content)
    @GetMapping("/{subscription_id}")
    SubscriptionDto getSubscriptionById(@PathVariable("subscription_id") Long subscriptionId);

    @Operation(summary = "Получение списка подписок на сервисы у пользователя по его id")
    @ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = SubscriptionDto.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "400", content = @Content)
    @ApiResponse(responseCode = "404", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "500", content = @Content)
    @GetMapping("/users/{user_id}")
     List<SubscriptionDto> getUserSubscriptions(@PathVariable ("user_id") Long id) ;


    @Operation(summary = "Обновление подписки на сервис по subscription_Id")
    @ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = SubscriptionDto.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "400", content = @Content)
    @ApiResponse(responseCode = "404", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "500", content = @Content)
    @PutMapping("/{subscription_id}")
    SubscriptionDto updateSubscription(@RequestBody UpdateSubscriptionRequest subscriptionDto,
                                       @PathVariable("subscription_id") Long subscriptionId);


    @Operation(summary = "Удаление подписки на сервис по id у пользователя c ID")
    @ApiResponse(responseCode = "200", content = @Content)
    @ApiResponse(responseCode = "400", content = @Content)
    @ApiResponse(responseCode = "404", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "500", content = @Content)
    @DeleteMapping("/{subscription_id}/users/{user_id}")
    void deleteSubscriptionFromUser(@PathVariable ("subscription_id") Long subscriptionId, @PathVariable ("user_id") Long userId);


    @Operation(summary = "Получение ТОП-3 популярных подписок на сервисы")
    @ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = SubscriptionDto.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "400", content = @Content)
    @ApiResponse(responseCode = "404", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "500", content = @Content)
    @GetMapping("/top")
    List<Object[]> getTop3PopularSubscriptions();
}
