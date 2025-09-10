package bang_anas.restful.controller;

import bang_anas.restful.entity.Contact;
import bang_anas.restful.entity.User;
import bang_anas.restful.model.ContactResponse;
import bang_anas.restful.model.CreateContactRequest;
import bang_anas.restful.model.UpdateContactRequest;
import bang_anas.restful.model.WebResponse;
import bang_anas.restful.repository.AddressRepository;
import bang_anas.restful.repository.ContactRepository;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    void setUp(){

        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test-anas");
        user.setPassword(BCrypt.hashpw("test-password-anas", BCrypt.gensalt()));
        user.setName("Test-Name-Anas");
        user.setToken("test-token-anas");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000);
        userRepository.save(user);

    }

    @Test
    void createContactBadRequest() throws  Exception{
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("");
        request.setPhone("");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test-token-anas")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            }
                    );
                    assertNotNull(response.getErrors());
                    System.out.println(response.getErrors());
                }
        );
    }


    @Test
    void createContactSuccess() throws Exception {

        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("Anas");
        request.setLastName("Tsuyoi");
        request.setEmail("anas@example.com");
        request.setPhone("69696996969696969");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test-token-anas")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData().getId());
                    assertTrue(contactRepository.existsById(response.getData().getId()));
                    assertEquals("Anas", response.getData().getFirstName());
                    assertEquals("Tsuyoi", response.getData().getLastName());
                    assertEquals("anas@example.com", response.getData().getEmail());
                    assertEquals("69696996969696969", response.getData().getPhone());

                    System.out.println(response.getData());
                }
        );


    }


    @Test
    void getContactNotFound() throws  Exception{
        mockMvc.perform(
                get("/api/contacts/69696969")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test-token-anas")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            }
                    );
                    assertNotNull(response.getErrors());
                    System.out.println(response.getErrors());
                }
        );
    }

    @Test
    void getContactSuccess() throws Exception {
        User user = userRepository.findById("test-anas").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("anas");
        contact.setLastName("Guege");
        contact.setEmail("Nyueno@example.com");
        contact.setPhone("9238423432");
        contactRepository.save(contact);

        mockMvc.perform(
                get("/api/contacts/" + contact.getId() )
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                "X-API-TOKEN",
                                "test-token-anas"
                        )
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<ContactResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertNull(response.getErrors());
                    assertEquals(contact.getId(), response.getData().getId());
                    assertEquals(contact.getFirstName(), response.getData().getFirstName());
                    assertEquals(contact.getLastName(), response.getData().getLastName());
                    assertEquals(contact.getEmail(), response.getData().getEmail());
                    assertEquals(contact.getPhone(), response.getData().getPhone());

                    System.out.println(response.getData());
        });
    }

    @Test
    void updateContactBadRequest() throws  Exception{
        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("");
        request.setPhone("");

        mockMvc.perform(
                put("/api/contacts/69696969")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test-token-anas")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            }
                    );
                    assertNotNull(response.getErrors());
                    System.out.println(response.getErrors());
                }
        );
    }

    @Test
    void updateContactSuccess() throws Exception {
        User user = userRepository.findById("test-anas").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("Eko");
        contact.setLastName("Khanedy");
        contact.setEmail("eko@example.com");
        contact.setPhone("9238423432");
        contactRepository.save(contact);

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("Anas");
        request.setLastName("Tsuyoi");
        request.setEmail("anas@example.com");
        request.setPhone("69696996969696969");

        mockMvc.perform(
                put("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test-token-anas")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    });

                    assertNull(response.getErrors());
                    assertEquals(request.getFirstName(), response.getData().getFirstName());
                    assertEquals(request.getLastName(), response.getData().getLastName());
                    assertEquals(request.getEmail(), response.getData().getEmail());
                    assertEquals(request.getPhone(), response.getData().getPhone());

                    System.out.println(response.getData());
            }
        );


    }


    @Test
    void deleteContactNotFound() throws  Exception{
        mockMvc.perform(
                delete("/api/contacts/69696969")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test-token-anas")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            }
                    );
                    assertNotNull(response.getErrors());
                    System.out.println(response.getErrors());
                }
        );
    }

    @Test
    void deleteContactSuccess() throws Exception {
        User user = userRepository.findById("test-anas").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("anas");
        contact.setLastName("Guege");
        contact.setEmail("Nyueno@example.com");
        contact.setPhone("9238423432");
        contactRepository.save(contact);

        mockMvc.perform(
                delete("/api/contacts/" + contact.getId() )
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                "X-API-TOKEN",
                                "test-token-anas"
                        )
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            }
                    );
                    assertNull(response.getErrors());
                    assertEquals("oke", response.getData());

                    System.out.println(response.getData());
                }
        );
    }


    @Test
    void serachNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contacts" )
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                "X-API-TOKEN",
                                "test-token-anas"
                        )
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertNull(response.getErrors());
                    assertEquals(0,response.getData().size());
                    assertEquals(0,response.getPaging().getTotalPage());
                    assertEquals(0,response.getPaging().getCurrentPage());
                    assertEquals(10,response.getPaging().getSize());
                    System.out.println(response.getData());
                }
        );
    }


    @Test
    void serachNSuccess() throws Exception {

        User user = userRepository.findById("test-anas").orElseThrow();

        for (int i = 0; i < 100; i++) {
            Contact contact = new Contact();
            contact.setId(UUID.randomUUID().toString());
            contact.setUser(user);
            contact.setFirstName("anas " + i);
            contact.setLastName("Guege " + i);
            contact.setEmail("Nyueno" + i + "@example.com");
            contact.setPhone("9238423432");
            contactRepository.save(contact);
        }

        // using name parameters
        mockMvc.perform(
                get("/api/contacts" )
                        .queryParam("name", "anas")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                "X-API-TOKEN",
                                "test-token-anas"
                        )
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertNull(response.getErrors());
                    assertEquals(10,response.getData().size());
                    assertEquals(10,response.getPaging().getTotalPage());
                    assertEquals(0,response.getPaging().getCurrentPage());
                    assertEquals(10,response.getPaging().getSize());
                    System.out.println(response.getData());
                }
        );

        // using email ass query params
        mockMvc.perform(
                get("/api/contacts" )
                        .queryParam("email", "Nyueno")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                "X-API-TOKEN",
                                "test-token-anas"
                        )
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertNull(response.getErrors());
                    assertEquals(10,response.getData().size());
                    assertEquals(10,response.getPaging().getTotalPage());
                    assertEquals(0,response.getPaging().getCurrentPage());
                    assertEquals(10,response.getPaging().getSize());
                    System.out.println(response.getData());
                }
        );

        // using phone ass query params
        mockMvc.perform(
                get("/api/contacts" )
                        .queryParam("phone", "3842")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                "X-API-TOKEN",
                                "test-token-anas"
                        )
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertNull(response.getErrors());
                    assertEquals(10,response.getData().size());
                    assertEquals(10,response.getPaging().getTotalPage());
                    assertEquals(0,response.getPaging().getCurrentPage());
                    assertEquals(10,response.getPaging().getSize());
                    System.out.println(response.getData());
                }
        );


        // using page ass query params
        mockMvc.perform(
                get("/api/contacts" )
                        .queryParam("phone", "3842")
                        .queryParam("page", "1000")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                "X-API-TOKEN",
                                "test-token-anas"
                        )
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertNull(response.getErrors());
                    assertEquals(0,response.getData().size());
                    assertEquals(10,response.getPaging().getTotalPage());
                    assertEquals(1000,response.getPaging().getCurrentPage());
                    assertEquals(10,response.getPaging().getSize());
                    System.out.println(response.getData());
                }
        );
    }


}