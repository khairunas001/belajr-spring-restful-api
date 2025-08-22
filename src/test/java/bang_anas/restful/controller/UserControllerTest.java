package bang_anas.restful.controller;

import bang_anas.restful.entity.User;
import bang_anas.restful.model.RegisterUserRequest;
import bang_anas.restful.model.UserResponse;
import bang_anas.restful.model.WebResponse;
import bang_anas.restful.repository.UserRepository;
import bang_anas.restful.security.BCrypt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("test_anas");
        request.setPassword("test_password");
        request.setName("test_name");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                                                                        new TypeReference<>() {});
            assertEquals("ok",response.getData());
        });
    }


//    @Test
//    void testRegisterSuccess() throws Exception {
//        RegisterUserRequest request = new RegisterUserRequest();
//        request.setUsername("test_anas");
//        request.setPassword("test_password");
//        request.setName("test_name");
//
//        mockMvc.perform(
//                post("/api/users")
//                        .accept(MediaType.APPLICATION_JSON)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))
//        ).andExpectAll(
//                status().isOk()
//        ).andDo(result -> {
//            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
//                                                             new TypeReference<>() {});
//            // assertEquals("ok",response.getData());
//
//            assertEquals(response.getData().getUsername(),request.getUsername());
//            assertEquals(response.getData().getName(),request.getName());
//        });
//    }


    @Test
    void testRegisterBadRequest() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("");
        request.setPassword("");
        request.setName("");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

            assertNotNull(response.getErrors());
            System.out.println(response.getErrors());
        });

    }

    @Test
    void testRegisterDuplicate() throws Exception {
        User user = new User();
        user.setUsername("test_anas");
        user.setPassword("test_password");
        user.setName("test_name");
        userRepository.save(user);

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("test_anas");
        request.setPassword("test_password");
        request.setName("test_name");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNotNull(response.getErrors());
            System.out.println(response.getErrors());
        });


    }

    @Test
    void getUserUnauthorized() throws Exception{
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "notfound")
        ).andExpectAll(
           status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNotNull(response.getErrors());
            System.out.println(response.getErrors());
        });
    }

    @Test
    void getUserUnauthorizedTokenNotSend() throws Exception{
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNotNull(response.getErrors());
            System.out.println(response.getErrors());
        });
    }

    @Test
    void getUserTokenExpired() throws Exception{
        User user = new User();
        user.setUsername("test_anas");
        user.setName("test_anas");
        user.setPassword(BCrypt.hashpw("test_password", BCrypt.gensalt()));
        user.setTokenExpiredAt(System.currentTimeMillis() - 10000000000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "notfound")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNotNull(response.getErrors());
            System.out.println(response.getErrors());
        });
    }

    @Test
    void getUserSuccess() throws Exception{
        User user = new User();
        user.setUsername("test_anas");
        user.setName("test_anas");
        user.setToken("test_token");
        user.setPassword(BCrypt.hashpw("test_password", BCrypt.gensalt()));
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", user.getToken())
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNull(response.getErrors());
            assertEquals("test_anas",response.getData().getUsername());
            assertEquals("test_anas",response.getData().getName());
        });
    }

}
