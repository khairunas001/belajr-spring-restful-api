package bang_anas.restful.service;

import bang_anas.restful.entity.User;
import bang_anas.restful.model.LoginUserRequest;
import bang_anas.restful.model.TokenResponse;
import bang_anas.restful.repository.UserRepository;
import bang_anas.restful.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public TokenResponse login(LoginUserRequest request) {

        // validate the data request
        validationService.validate(request);
        User user =
                userRepository.findById(request.getUsername()).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "username or password is wrong"
                ));

        // check password
        if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);

            // return the response
            return TokenResponse.builder()
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            throw  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password is wrong");
        }
    }

    // make expired date for 30 days
    public Long next30Days() {
        return System.currentTimeMillis() + (1000L * 60 * 12 * 30);
    }

}
