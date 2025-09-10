package bang_anas.restful.controller;

import bang_anas.restful.entity.Address;
import bang_anas.restful.entity.Contact;
import bang_anas.restful.entity.User;
import bang_anas.restful.model.AddressResponse;
import bang_anas.restful.model.CreateAddressRequest;
import bang_anas.restful.model.UpdateAddressRequest;
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
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test_anas_user_test");
        user.setName("test_anas");
        user.setToken("test_token");
        user.setPassword(BCrypt.hashpw("test_password", BCrypt.gensalt()));
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
        userRepository.save(user);

        Contact contact = new Contact();
        contact.setId("anas-contactId");
        contact.setUser(user);
        contact.setFirstName("anas");
        contact.setLastName("Guege");
        contact.setEmail("Nyueno@example.com");
        contact.setPhone("9238423432");
        contactRepository.save(contact);
    }


    @Test
    void createAddressBadRequest() throws  Exception{
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCountry("");

        mockMvc.perform(
                post("/api/contacts/anas-contactId/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(
                                "X-API-TOKEN",
                                "test_token"
                        )
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNotNull(response.getErrors());
        });
    }


    @Test
    void createAddressSuccess() throws  Exception{
        CreateAddressRequest request = new CreateAddressRequest();
        request.setStreet("Jakal");
        request.setCity("YKC");
        request.setProvince("DIY");
        request.setCountry("Indonesia");
        request.setPostalCode("69696");

        mockMvc.perform(
                post("/api/contacts/anas-contactId/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(
                                "X-API-TOKEN",
                                "test_token"
                        )
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNull(response.getErrors());
            assertEquals(request.getStreet(), response.getData().getStreet());
            assertEquals(request.getCity(), response.getData().getCity());
            assertEquals(request.getProvince(), response.getData().getProvince());
            assertEquals(request.getCountry(), response.getData().getCountry());
            assertEquals(request.getPostalCode(), response.getData().getPostalCode());

            assertTrue(addressRepository.existsById(response.getData().getId()));
        });

    }

    @Test
    void getAddressNotFound() throws  Exception{
        mockMvc.perform(
                get("/api/contacts/anas-contactId/addresses/not-addressId")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                "X-API-TOKEN",
                                "test_token"
                        )
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNotNull(response.getErrors());
        });
    }


    @Test
    void getAddressSuccess() throws  Exception{
        Contact contact = contactRepository.findById("anas-contactId").orElseThrow();

        Address address = new Address();
        address.setId("test_jakal_jogja");
        address.setStreet("Jakal");
        address.setCity("YKC");
        address.setProvince("DIY");
        address.setCountry("Indonesia");
        address.setPostalCode("69696");
        address.setContact(contact);
        addressRepository.save(address);

        mockMvc.perform(
                get("/api/contacts/anas-contactId/addresses/test_jakal_jogja")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                "X-API-TOKEN",
                                "test_token"
                        )
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNull(response.getErrors());

            assertEquals(address.getId(), response.getData().getId());
            assertEquals(address.getStreet(), response.getData().getStreet());
            assertEquals(address.getCity(), response.getData().getCity());
            assertEquals(address.getProvince(), response.getData().getProvince());
            assertEquals(address.getCountry(), response.getData().getCountry());
            assertEquals(address.getPostalCode(), response.getData().getPostalCode());
        });
    }


    @Test
    void updateAddressBadRequest() throws  Exception{
        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCountry("");

        mockMvc.perform(
                put("/api/contacts/anas-contactId/addresses/test_jakal_jogja")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(
                                "X-API-TOKEN",
                                "test_token"
                        )
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateAddressSuccess() throws  Exception{
        Contact contact = contactRepository.findById("anas-contactId").orElseThrow();

        Address address = new Address();
        address.setId("test_jakal_jogja");
        address.setContact(contact);
        address.setStreet("Jakal_Lama");
        address.setCity("YKC_Lama");
        address.setProvince("DIY_Lama");
        address.setCountry("Indonesia_Lama");
        address.setPostalCode("69696");
        address.setContact(contact);
        addressRepository.save(address);


        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setStreet("Jakal");
        request.setCity("YKC");
        request.setProvince("DIY");
        request.setCountry("Indonesia");
        request.setPostalCode("1234");

        mockMvc.perform(
                put("/api/contacts/anas-contactId/addresses/test_jakal_jogja")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(
                                "X-API-TOKEN",
                                "test_token"
                        )
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNull(response.getErrors());
            assertEquals(request.getStreet(), response.getData().getStreet());
            assertEquals(request.getCity(), response.getData().getCity());
            assertEquals(request.getProvince(), response.getData().getProvince());
            assertEquals(request.getCountry(), response.getData().getCountry());
            assertEquals(request.getPostalCode(), response.getData().getPostalCode());

            assertTrue(addressRepository.existsById(response.getData().getId()));
        });

    }

    @Test
    void deleteAddressNotFound() throws  Exception{
        mockMvc.perform(
                delete("/api/contacts/anas-contactId/addresses/not-addressId")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                "X-API-TOKEN",
                                "test_token"
                        )
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNotNull(response.getErrors());
        });
    }


    @Test
    void deleteAddressSuccess() throws  Exception{
        Contact contact = contactRepository.findById("anas-contactId").orElseThrow();

        Address address = new Address();
        address.setId("test_jakal_jogja");
        address.setStreet("Jakal");
        address.setCity("YKC");
        address.setProvince("DIY");
        address.setCountry("Indonesia");
        address.setPostalCode("69696");
        address.setContact(contact);
        addressRepository.save(address);

        mockMvc.perform(
                delete("/api/contacts/anas-contactId/addresses/test_jakal_jogja")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                "X-API-TOKEN",
                                "test_token"
                        )
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNull(response.getErrors());

            assertEquals("oke", response.getData());

            assertFalse(addressRepository.existsById("test_jakal_jogja"));
        });
    }

    @Test
    void listAddressNotFound() throws  Exception{
        mockMvc.perform(
                get("/api/contacts/anas-contactId-wrong/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                "X-API-TOKEN",
                                "test_token"
                        )
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void listAddressSuccess() throws  Exception{
        Contact contact = contactRepository.findById("anas-contactId").orElseThrow();

        for (int i = 0; i < 10; i++) {
            Address address = new Address();
            address.setId("test_jakal_jogja" + i);
            address.setStreet("Jakal"+i);
            address.setCity("YKC" + i);
            address.setProvince("DIY" + i);
            address.setCountry("Indonesia" + i);
            address.setPostalCode("69696");
            address.setContact(contact);
            addressRepository.save(address);
        }

        mockMvc.perform(
                get("/api/contacts/anas-contactId/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                "X-API-TOKEN",
                                "test_token"
                        )
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<AddressResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNull(response.getErrors());

            assertEquals(10, response.getData().size());


        });
    }


}