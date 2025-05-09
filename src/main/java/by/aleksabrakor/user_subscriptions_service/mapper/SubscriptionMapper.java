package by.aleksabrakor.user_subscriptions_service.mapper;

import by.aleksabrakor.user_subscriptions_service.dto.SubscriptionDto;
import by.aleksabrakor.user_subscriptions_service.model.Subscription;
import by.aleksabrakor.user_subscriptions_service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "user", source = "userId")
    Subscription subscriptionDtoToEntity(SubscriptionDto subscriptionDto);

    @Mapping(target = "userId", source = "user.id")
    SubscriptionDto entityToSubscriptionDto(Subscription subscription);

    List<SubscriptionDto> toDtoList(List<Subscription> subscriptions);

    List<Subscription> toEntityList(List<SubscriptionDto> taskDtos);

    // Метод для преобразования ID в User
    default User mapIdToUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}
