package bang_anas.restful.repository;

import bang_anas.restful.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
