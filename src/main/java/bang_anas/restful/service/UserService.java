package bang_anas.restful.service;

import bang_anas.restful.entity.User;
import bang_anas.restful.model.RegisterUserRequest;
import bang_anas.restful.model.UserResponse;
import bang_anas.restful.repository.UserRepository;
import bang_anas.restful.security.BCrypt;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void register(RegisterUserRequest request){

        // validate the data reuqst
        validationService.validate(request);

        if (userRepository.existsById(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"username already registered");
        }

        // modularity for save data
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());
        userRepository.save(user);

    }

    public UserResponse get(User user){
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

}
