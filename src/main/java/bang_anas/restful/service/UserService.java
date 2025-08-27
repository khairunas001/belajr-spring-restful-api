package bang_anas.restful.service;

import bang_anas.restful.entity.User;
import bang_anas.restful.model.RegisterUserRequest;
import bang_anas.restful.model.UpdateUserRequest;
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

import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void register(RegisterUserRequest request) {

        // validate the data reuqst
        validationService.validate(request);

        if (userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "username already registered"
            );
        }

        // modularity for save data
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(
                request.getPassword(),
                BCrypt.gensalt()
        ));
        user.setName(request.getName());
        userRepository.save(user);

    }

    //    @Transactional
    //    public UserResponse register(RegisterUserRequest request){
    //
    //        // validate the data reuqst
    //        validationService.validate(request);
    //
    //        if (userRepository.existsById(request.getUsername())){
    //            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"username already registered");
    //        }
    //
    //        // modularity for save data
    //        User user = new User();
    //        user.setUsername(request.getUsername());
    //        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
    //        user.setName(request.getName());
    //        userRepository.save(user);
    //
    //        return UserResponse.builder()
    //                .username(user.getUsername())
    //                .name(user.getName())
    //                .build();
    //    }

    public UserResponse get(User user) {
        // return as string use builder, and push it to controller
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }


    public UserResponse update(User user, UpdateUserRequest request) {
        validationService.validate(request);

        log.info("REQUEST : {}", request);
        if (Objects.nonNull(request.getName())) {
            user.setName(request.getName());
        }

        if (Objects.nonNull(request.getPassword())) {
            user.setPassword(BCrypt.hashpw(
                    request.getPassword(),
                    BCrypt.gensalt()
            ));
        }

        userRepository.save(user);

        log.info("USER : {}", user.getName());

        return UserResponse.builder()
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }

}
