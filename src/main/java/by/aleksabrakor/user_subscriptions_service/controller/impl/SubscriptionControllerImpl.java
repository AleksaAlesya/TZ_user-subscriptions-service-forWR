package by.aleksabrakor.user_subscriptions_service.controller.impl;

import by.aleksabrakor.user_subscriptions_service.controller.SubscriptionController;
import by.aleksabrakor.user_subscriptions_service.dto.SubscriptionDto;
import by.aleksabrakor.user_subscriptions_service.dto.UpdateSubscriptionRequest;
import by.aleksabrakor.user_subscriptions_service.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/subscriptions")
@Slf4j
@RequiredArgsConstructor
public class SubscriptionControllerImpl implements SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/users/{user_id}")
    public SubscriptionDto addSubscriptionToUser(@PathVariable ("user_id") Long userId,
                                                 @RequestBody @Valid SubscriptionDto subscriptionDto) {
        log.info("POST /subscriptions/users/{userId} — добавление новой подписки на сервис пользователю по id");

        return subscriptionService.addSubscriptionToUser(subscriptionDto, userId);
    }

    @GetMapping()
    public List<SubscriptionDto> findAllSubscriptions() {
        log.info("GET /subscriptions — получение списка всех существующих подписок на сервисы");

        return subscriptionService.findAllSubscriptions();
    }

    @GetMapping("/{subscription_id}")
    public SubscriptionDto getSubscriptionById(@PathVariable("subscription_id") Long subscriptionId) {
        log.info("GET /subscriptions/{id} — получение подписки на сервис по ID.");

        return subscriptionService.findSubscriptionById(subscriptionId);
    }


    @GetMapping("/users/{user_id}")
    public List<SubscriptionDto> getUserSubscriptions(@PathVariable ("user_id") Long userId) {
        log.info("GET /subscriptions/users/{id} — получение списка всех  подписок на сервисы для пользователя по его id");

        return subscriptionService.getUserSubscriptions(userId);
    }

    @PutMapping("/{subscription_id}")
    public SubscriptionDto updateSubscription(@RequestBody @Valid UpdateSubscriptionRequest subscriptionDto,
                                              @PathVariable("subscription_id") Long subscriptionId) {
        log.info("PUT /subscriptions/{subscription_id} — обновление подписки на сервис");

        return subscriptionService.updateSubscription(subscriptionDto, subscriptionId);
    }

    @DeleteMapping("/{subscription_id}/users/{user_id}")
    public void deleteSubscriptionFromUser(@PathVariable ("subscription_id") Long subscriptionId,
                                           @PathVariable ("user_id") Long userId) {
        log.info("DELETE /subscriptions/{subscriptionId}/users/{userId} — удаление подписки на сервис у пользователя");

        subscriptionService.deleteSubscriptionFromUser(subscriptionId, userId);
    }

    @GetMapping("/top")
    public List<Object[]> getTop3PopularSubscriptions() {
        log.info("GET /subscriptions/top — получение ТОП-3 популярных подписок");

        return subscriptionService.getTop3PopularSubscriptions();
    }
}
