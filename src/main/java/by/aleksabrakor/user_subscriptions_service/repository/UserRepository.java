package by.aleksabrakor.user_subscriptions_service.repository;

import by.aleksabrakor.user_subscriptions_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail(String email);

    Boolean existsByEmailAndIdNot(String email, Long id);

//    Optional<User> findByEmailAndIdNot(String email, Long id);
//
//    Optional<User> findByEmail(String email);
}
