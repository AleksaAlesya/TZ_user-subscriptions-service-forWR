package by.aleksabrakor.user_subscriptions_service.service;


import by.aleksabrakor.user_subscriptions_service.dto.UpdateUserRequest;
import by.aleksabrakor.user_subscriptions_service.dto.UserDto;
import by.aleksabrakor.user_subscriptions_service.exception.NotCreatedException;
import by.aleksabrakor.user_subscriptions_service.exception.NotFoundException;
import by.aleksabrakor.user_subscriptions_service.mapper.UserMapper;
import by.aleksabrakor.user_subscriptions_service.model.User;
import by.aleksabrakor.user_subscriptions_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Transactional
    public UserDto saveUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new NotCreatedException("Этот email уже существует");
        }
        User user = userMapper.userDtoToEntity(userDto);
        return userMapper.entityToUserDto(userRepository.save(user));
    }


    public List<UserDto> findAllUsers() {

        return userMapper.toDtoList(userRepository.findAll());
    }

    public UserDto findUserById(Long id) {

        return userMapper.entityToUserDto(findUserOrThrow(id));
    }

    @Transactional
    public UserDto updateUser(UpdateUserRequest userDto, Long id) {
        User user = findUserOrThrow(id);

        if (userRepository.existsByEmailAndIdNot(userDto.getEmail(), id)) {
            throw new NotCreatedException("Этот email уже существует");
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return userMapper.entityToUserDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        findUserOrThrow(id);
        userRepository.deleteById(id);
    }

    User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер с id = " + userId + " не найден"));
    }
}
