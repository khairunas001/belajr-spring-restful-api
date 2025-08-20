package bang_anas.restful.controller;

import bang_anas.restful.entity.User;
import bang_anas.restful.model.LoginUserRequest;
import bang_anas.restful.model.TokenResponse;
import bang_anas.restful.model.WebResponse;
import bang_anas.restful.repository.UserRepository;
import bang_anas.restful.security.BCrypt;
import bang_anas.restful.service.AuthService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void loginFailedUserNotFound() throws Exception {
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test_anas");
        request.setPassword("tes_password");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))

        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {

                    }
            );

            assertNotNull(response.getErrors());
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getErrors()));
        });

    }


    @Test
    void loginFailedWrongPassword() throws Exception {
        User user = new User();
        user.setName("Test_anas");
        user.setUsername("test_anas");
        user.setPassword(BCrypt.hashpw("test_password",BCrypt.gensalt()));

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test_anas");
        request.setPassword("tes_password_salah");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))

        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {

                    }
            );

            assertNotNull(response.getErrors());
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getErrors()));
        });

    }


    @Test
    void loginSuccess() throws Exception {
        User user = new User();
        user.setName("Test_anas");
        user.setUsername("test_anas");
        user.setPassword(BCrypt.hashpw("test_password_benar",BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test_anas");
        request.setPassword("test_password_benar");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))

        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {

                    }
            );

            assertNull(response.getErrors());
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());

            User userDb = userRepository.findById("test_anas").orElse(null);
            assertNotNull(userDb);
            assertEquals(response.getData().getToken(), userDb.getToken());
            assertEquals(response.getData().getExpiredAt(), userDb.getTokenExpiredAt());

            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getData()));
        });

    }
}