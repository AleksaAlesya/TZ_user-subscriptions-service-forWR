package by.aleksabrakor.user_subscriptions_service.service;

import by.aleksabrakor.user_subscriptions_service.dto.SubscriptionDto;
import by.aleksabrakor.user_subscriptions_service.dto.UpdateSubscriptionRequest;
import by.aleksabrakor.user_subscriptions_service.exception.NotCreatedException;
import by.aleksabrakor.user_subscriptions_service.exception.NotFoundException;
import by.aleksabrakor.user_subscriptions_service.mapper.SubscriptionMapper;
import by.aleksabrakor.user_subscriptions_service.model.Subscription;
import by.aleksabrakor.user_subscriptions_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserService userService;


    @Transactional
    public SubscriptionDto addSubscriptionToUser(SubscriptionDto subscriptionDto, Long userId) {
        Subscription newSubscription = subscriptionMapper.subscriptionDtoToEntity(subscriptionDto);

        newSubscription.setUser(userService.findUserOrThrow(userId));
        return subscriptionMapper.entityToSubscriptionDto(subscriptionRepository.save(newSubscription));
    }


    public List<SubscriptionDto> findAllSubscriptions() {

        return subscriptionMapper.toDtoList(subscriptionRepository.findAll());
    }

    public SubscriptionDto findSubscriptionById(Long id) {

        return subscriptionMapper.entityToSubscriptionDto(findSubscriptionOrThrow(id));
    }

    public List<SubscriptionDto> getUserSubscriptions(Long userId) {
        userService.findUserOrThrow(userId);
        return subscriptionMapper.toDtoList(subscriptionRepository.findByUserId(userId));
    }


    @Transactional
    public SubscriptionDto updateSubscription(UpdateSubscriptionRequest subscriptionDto, Long subscriptionId) {
        Subscription subscription = findSubscriptionOrThrow(subscriptionId);

        if (subscriptionDto.getUserId() != null) {
            subscription.setUser(userService.findUserOrThrow(subscriptionDto.getUserId()));
        }
        if (subscriptionDto.getServiceTitle() != null) {
            subscription.setServiceTitle(subscriptionDto.getServiceTitle());
        }
        if (subscriptionDto.getDescription() != null) {
            subscription.setDescription(subscriptionDto.getDescription());
        }
        if (subscriptionDto.getPlan() != null) {
            subscription.setPlan(subscriptionDto.getPlan());
        }

        return subscriptionMapper.entityToSubscriptionDto(subscriptionRepository.save(subscription));
    }

    @Transactional
    public void deleteSubscriptionFromUser(Long subscriptionId, Long userId) {
        Subscription subscription = findSubscriptionOrThrow(subscriptionId);
        userService.findUserOrThrow(userId);

        if (subscription.getUser().getId().equals(userId)) {
            subscriptionRepository.delete(subscription);
        }
        else {
            throw new IllegalArgumentException("У  подписки другой пользователь");
        }
    }


    public List<Object[]> getTop3PopularSubscriptions() {
        return subscriptionRepository.findTop3PopularSubscriptions();
    }


    private Subscription findSubscriptionOrThrow(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Подписка на сервис с id = " + id + " не найдена"));
    }
}
