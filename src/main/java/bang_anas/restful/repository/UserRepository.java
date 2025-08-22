package bang_anas.restful.repository;

import bang_anas.restful.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    // query to get the token
    Optional<User> findFirstByToken (String token);
}
