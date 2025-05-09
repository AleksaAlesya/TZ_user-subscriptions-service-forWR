package by.aleksabrakor.user_subscriptions_service.mapper;


import by.aleksabrakor.user_subscriptions_service.dto.UserDto;
import by.aleksabrakor.user_subscriptions_service.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = SubscriptionMapper.class)
public interface UserMapper {

    User userDtoToEntity(UserDto userDto);

    UserDto entityToUserDto(User user);

    List<UserDto> toDtoList(List<User> tasks);

    List<User> toEntityList(List<UserDto> taskDtos);
}
