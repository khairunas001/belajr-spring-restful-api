package bang_anas.restful.service;

import bang_anas.restful.entity.Address;
import bang_anas.restful.entity.Contact;
import bang_anas.restful.entity.User;
import bang_anas.restful.model.AddressResponse;
import bang_anas.restful.model.CreateAddressRequest;
import bang_anas.restful.repository.AddressRepository;
import bang_anas.restful.repository.ContactRepository;
import bang_anas.restful.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ValidationService validationService;


    @Transactional
    public AddressResponse create(User user, CreateAddressRequest request){

        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(
                user,
                request.getContactId()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "contact is not found"
        ));

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());

        addressRepository.save(address);

        return toAddressResponse(address);
    }

    private AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }
}
