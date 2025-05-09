package by.aleksabrakor.user_subscriptions_service.service;

import by.aleksabrakor.user_subscriptions_service.dto.SubscriptionDto;
import by.aleksabrakor.user_subscriptions_service.dto.UpdateSubscriptionRequest;
import by.aleksabrakor.user_subscriptions_service.dto.UserDto;
import by.aleksabrakor.user_subscriptions_service.exception.NotFoundException;
import by.aleksabrakor.user_subscriptions_service.mapper.SubscriptionMapper;
import by.aleksabrakor.user_subscriptions_service.model.Subscription;
import by.aleksabrakor.user_subscriptions_service.model.User;
import by.aleksabrakor.user_subscriptions_service.repository.SubscriptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private SubscriptionMapper subscriptionMapper;
    @Mock
    private UserService userService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    @DisplayName("Добавление подписки, если  если UserId существует")
    void addSubscriptionToUser_ShouldSetSubscription_WhenUserIdExists() {
        //Arrange
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);

        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setServiceTitle("Test subscription");

        Subscription subscription = new Subscription();
        subscription.setUser(existingUser);
        subscription.setServiceTitle("Test subscription");

        Subscription savedSubscription = new Subscription();
        savedSubscription.setId(2L);
        savedSubscription.setUser(existingUser);
        savedSubscription.setServiceTitle("Test subscription");

        SubscriptionDto savedSubscriptionDto = new SubscriptionDto();
        savedSubscriptionDto.setId(2L);
        savedSubscriptionDto.setUserId(userId);
        savedSubscriptionDto.setServiceTitle("Test subscription");

        when(subscriptionMapper.subscriptionDtoToEntity(subscriptionDto)).thenReturn(subscription);
        when(userService.findUserOrThrow(userId)).thenReturn(existingUser);
        when(subscriptionRepository.save(subscription)).thenReturn(savedSubscription);
        when(subscriptionMapper.entityToSubscriptionDto(savedSubscription)).thenReturn(savedSubscriptionDto);

        //Act
        SubscriptionDto resultDto = subscriptionService.addSubscriptionToUser(subscriptionDto, userId);

        //Assert
        assertNotNull(resultDto);
        assertEquals(userId, resultDto.getUserId());
        assertEquals("Test subscription", resultDto.getServiceTitle());
        verify(subscriptionMapper, times(1)).subscriptionDtoToEntity(subscriptionDto);
        verify(userService, times(1)).findUserOrThrow(userId);
        verify(subscriptionRepository, times(1)).save(subscription);
        verify(subscriptionMapper, times(1)).entityToSubscriptionDto(savedSubscription);
    }

    @Test
    @DisplayName("Выброс NotFoundException при добавлении подписки, если UserId не существует ")
    void addSubscriptionToUser_ShouldThrowNotFoundException_WhenUserIdNotExists() {
        //Arrange
        Long userId = 999L;

        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setServiceTitle("Test subscription");

        doThrow(new NotFoundException("Юзер с id = " + userId + " не найден"))
                .when(userService).findUserOrThrow(userId);

        //Act & Assert
        assertThrows(NotFoundException.class, () -> subscriptionService.addSubscriptionToUser(subscriptionDto, userId));
        verify(userService, times(1)).findUserOrThrow(userId);
    }

    @Test
    @DisplayName("Получение списка всех подписок из БД")
    void findAllSubscriptions_shouldReturnAllSubscriptions() {
        // Arrange
        List<Subscription> subscriptions = List.of(new Subscription(), new Subscription());
        List<SubscriptionDto> subscriptionDtos = List.of(new SubscriptionDto(), new SubscriptionDto());

        when(subscriptionRepository.findAll()).thenReturn(subscriptions);
        when(subscriptionMapper.toDtoList(subscriptions)).thenReturn(subscriptionDtos);

        //Act
        List<SubscriptionDto> resultDtos = subscriptionService.findAllSubscriptions();

        //Assert
        assertNotNull(resultDtos);
        assertEquals(subscriptions.size(), resultDtos.size());
        verify(subscriptionRepository, times(1)).findAll();
        verify(subscriptionMapper, times(1)).toDtoList(subscriptions);
    }

    @Test
    @DisplayName("Получение списка всех подписок для пользователя, если id существует")
    void getUserSubscriptions_shouldReturnAllSubscriptions_WhenUserIdExists() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Updated User");

        Subscription subscription1 = new Subscription();
        subscription1.setServiceTitle("Test subscription1");
        subscription1.setUser(existingUser);

        Subscription subscription2 = new Subscription();
        subscription2.setServiceTitle("Test subscription2");
        subscription2.setUser(existingUser);

        List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);

        SubscriptionDto subscriptionDto1 = new SubscriptionDto();
        subscriptionDto1.setServiceTitle("Test subscription1");
        subscriptionDto1.setUserId(userId);

        SubscriptionDto subscriptionDto2 = new SubscriptionDto();
        subscriptionDto2.setServiceTitle("Test subscription2");
        subscriptionDto2.setUserId(userId);

        List<SubscriptionDto> subscriptionDtos = Arrays.asList(subscriptionDto1, subscriptionDto2);

        when(userService.findUserOrThrow(userId)).thenReturn(existingUser);
        when(subscriptionRepository.findByUserId(userId)).thenReturn(subscriptions);
        when(subscriptionMapper.toDtoList(subscriptions)).thenReturn(subscriptionDtos);

        //Act
        List<SubscriptionDto> resultDtos = subscriptionService.getUserSubscriptions(userId);

        //Assert
        assertNotNull(resultDtos);
        assertEquals(subscriptions.size(), resultDtos.size());
        verify(userService, times(1)).findUserOrThrow(userId);
        verify(subscriptionRepository, times(1)).findByUserId(userId);
        verify(subscriptionMapper, times(1)).toDtoList(subscriptions);
    }

    @Test
    @DisplayName("Получение списка всех подписок для пользователя, если id существует")
    void getUserSubscriptions_ShouldThrowNotFoundException_WhenUserIdNotExists() {
        //Arrange
        Long userId = 999L;

        doThrow(new NotFoundException("Юзер с id = " + userId + " не найден"))
                .when(userService).findUserOrThrow(userId);

        //Act & Assert
        assertThrows(NotFoundException.class, () -> subscriptionService.getUserSubscriptions(userId));
        verify(userService, times(1)).findUserOrThrow(userId);
    }

    @Test
    @DisplayName("Получение подписки по ее id, если id существует")
    void findSubscriptionById_shouldReturnSubscription_WhenIdExists() {
        //Arrange
        Long subscriptionId = 1L;
        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);
        subscription.setServiceTitle("Test subscription");

        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setServiceTitle("Test subscription1");
        subscriptionDto.setId(subscriptionId);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(subscriptionMapper.entityToSubscriptionDto(subscription)).thenReturn(subscriptionDto);

        //Act
        SubscriptionDto result = subscriptionService.findSubscriptionById(subscriptionId);
        //Assert
        assertNotNull(result);
        assertEquals(subscription.getId(), result.getId());
        verify(subscriptionRepository, times(1)).findById(subscriptionId);
        verify(subscriptionMapper, times(1)).entityToSubscriptionDto(subscription);
    }

    @Test
    @DisplayName("Выброс NotFoundException при попытке пучение подписки по ее id, если id не существует")
    void findSubscriptionById_ShouldThrowNotFoundException_WhenSubIdNotExists() {
        //Arrange
        Long subscriptionId = 999L;

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> subscriptionService.findSubscriptionById(subscriptionId));
        verify(subscriptionRepository, times(1)).findById(subscriptionId);
    }


    @Test
    @DisplayName("Обновление полей подписки, если id существует и изменяется пользователь и он существует")
    void updateSubscription_ShouldUpdateFields_WhenSubIdExists() {
        //Arrange
        Long subscriptionId = 1L;

        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);

        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);
        subscription.setServiceTitle("Test subscription");
        subscription.setPlan(null);


        UpdateSubscriptionRequest updateDto = new UpdateSubscriptionRequest();
        updateDto.setId(subscriptionId);
        updateDto.setUserId(userId);
        updateDto.setServiceTitle("Update subscription");
        updateDto.setPlan("Update plan");


        Subscription updatedSubscription = new Subscription();
        updatedSubscription.setId(subscriptionId);
        updatedSubscription.setUser(existingUser);
        updatedSubscription.setServiceTitle("Update subscription");
        updatedSubscription.setPlan("Update plan");

        SubscriptionDto updatedSubscriptionDto = new SubscriptionDto();
        updatedSubscriptionDto.setId(subscriptionId);
        updatedSubscriptionDto.setUserId(userId);
        updatedSubscriptionDto.setServiceTitle("Update subscription");
        updatedSubscriptionDto.setPlan("Update plan");

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(userService.findUserOrThrow(userId)).thenReturn(existingUser);
        when(subscriptionRepository.save(subscription)).thenReturn(updatedSubscription);
        when(subscriptionMapper.entityToSubscriptionDto(updatedSubscription)).thenReturn(updatedSubscriptionDto);

        //Act
        SubscriptionDto resultDto = subscriptionService.updateSubscription(updateDto, subscriptionId);

        //Assert
        assertNotNull(resultDto);
        assertEquals(userId, resultDto.getUserId());
        assertEquals(subscriptionId, resultDto.getId());
        assertEquals("Update subscription", resultDto.getServiceTitle());
        verify(subscriptionRepository, times(1)).findById(subscriptionId);
        verify(userService, times(1)).findUserOrThrow(userId);
        verify(subscriptionRepository, times(1)).save(updatedSubscription);
        verify(subscriptionMapper, times(1)).entityToSubscriptionDto(updatedSubscription);
    }

    @Test
    @DisplayName("Выброс NotFoundException при обновлении полей подписки, если id не существует ")
    void updateSubscription_ShouldThrowNotFoundException_WhenSubIdNotExists() {
        //Arrange
        Long subscriptionId = 999L;
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> subscriptionService.updateSubscription(any(), subscriptionId));
        verify(subscriptionRepository).findById(subscriptionId);
    }

    @Test
    @DisplayName("Выброс NotFoundException при обновлении подписки, если user по id не найден")
    void updateTask_ShouldThrowNotFoundException_WhenUserIdNotExists() {
        // Arrange
        Long subscriptionId = 1L;
        Long userId = 999L;

        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);

        UpdateSubscriptionRequest updateDto = new UpdateSubscriptionRequest();
        updateDto.setId(subscriptionId);
        updateDto.setUserId(userId);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        doThrow(new NotFoundException("Юзер с id = " + userId + " не найден"))
                .when(userService).findUserOrThrow(userId);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> subscriptionService.updateSubscription(updateDto, subscriptionId));
        verify(subscriptionRepository, times(1)).findById(subscriptionId);
        verify(userService, times(1)).findUserOrThrow(userId);
    }

    @Test
    @DisplayName("Удаление задачи по существующему sub_id и user_id")
    void deleteSubscriptionFromUser_WhenSubIdAndUserIdExists() {
        //Arrange
        Long subscriptionId = 1L;
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);

        Subscription existingSubscription = new Subscription();
        existingSubscription.setId(subscriptionId);
        existingSubscription.setServiceTitle("Test subscription");
        existingSubscription.setUser(existingUser);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(existingSubscription));
        when(userService.findUserOrThrow(userId)).thenReturn(existingUser);
        doNothing().when(subscriptionRepository).delete(existingSubscription);

        //Act
        subscriptionService.deleteSubscriptionFromUser(subscriptionId, userId);

        //Assert
        verify(subscriptionRepository, times(1)).findById(subscriptionId);
        verify(userService, times(1)).findUserOrThrow(userId);
        verify(subscriptionRepository, times(1)).delete(existingSubscription);
    }

    @Test
    @DisplayName("Выброс NotFoundException при попытке удаления подписки, если  sub_id не существует")
    void deleteSubscriptionFromUser_ShouldThrowNotFoundException_WhenSubIdNotExists() {
        //Arrange
        Long subscriptionId = 999L;

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> subscriptionService.deleteSubscriptionFromUser(subscriptionId, any()));
        verify(subscriptionRepository).findById(subscriptionId);
    }

    @Test
    @DisplayName("Выброс IllegalArgumentException при попытке удаления подписки, если  sub_id  существует, а  юзер указан неверный")
    void deleteSubscriptionFromUser_ShouldThrowIllegalArgumentException_WhenUserIdNotValid() {
        //Arrange
        Long subscriptionId = 1L;
        Long invalidUserId = 1L;

        User existingUser = new User();
        existingUser.setId(invalidUserId);

        User user = new User();
        user.setId(5L);

        Subscription existingSubscription = new Subscription();
        existingSubscription.setId(subscriptionId);
        existingSubscription.setServiceTitle("Test subscription");
        existingSubscription.setUser(user);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(existingSubscription));
        when(userService.findUserOrThrow(invalidUserId)).thenReturn(existingUser);

        //Act//Assert
        assertThrows(IllegalArgumentException.class, () -> subscriptionService.deleteSubscriptionFromUser(subscriptionId, invalidUserId));
        verify(subscriptionRepository, times(1)).findById(subscriptionId);
        verify(userService, times(1)).findUserOrThrow(invalidUserId);
        verify(subscriptionRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Выброс NotFoundException при попытке удаления подписки, если user_id не существует")
    void deleteSubscriptionFromUser_ShouldThrowNotFoundException_WhenUserIdNotExists() {
        //Arrange
        Long subscriptionId = 1L;
        Long userId = 999L;

        Subscription existingSubscription = new Subscription();
        existingSubscription.setId(subscriptionId);
        existingSubscription.setServiceTitle("Test subscription");

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(existingSubscription));

        doThrow(new NotFoundException("Юзер с id = " + userId + " не найден"))
                .when(userService).findUserOrThrow(userId);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> subscriptionService.deleteSubscriptionFromUser(subscriptionId, userId));
        verify(subscriptionRepository, times(1)).findById(subscriptionId);
        verify(subscriptionRepository, never()).delete(any());
    }

    @Test
    @DisplayName("ТОП-3 популярных подписок")
    void getTop3PopularSubscriptions_ShouldReturnTop3Subscriptions() {
        // Arrange
        List<Object[]> popularSubscriptions = Arrays.asList(
                new Object[]{"Netflix", 1L},
                new Object[]{"Spotify", 2L},
                new Object[]{"YouTube Premium", 3L}
        );

        when(subscriptionRepository.findTop3PopularSubscriptions()).thenReturn(popularSubscriptions);

        // Act
        List<Object[]> result = subscriptionService.getTop3PopularSubscriptions();

        // Assert
        assertEquals(3, result.size());
        assertEquals("Netflix", result.get(0)[0]);
        assertEquals("Spotify", result.get(1)[0]);
        assertEquals("YouTube Premium", result.get(2)[0]);
        verify(subscriptionRepository, times(1)).findTop3PopularSubscriptions();
    }

    @Test
    @DisplayName("ТОП-3 популярных подписок, если лист пустой")
    void getTop3PopularSubscriptions_ShouldHandleEmptyResult() {
        // Arrange
        when(subscriptionRepository.findTop3PopularSubscriptions()).thenReturn(List.of());

        // Act
        List<Object[]> result = subscriptionService.getTop3PopularSubscriptions();

        // Assert
        assertEquals(0, result.size());
        verify(subscriptionRepository, times(1)).findTop3PopularSubscriptions();
    }
}