package bang_anas.restful.service;

import bang_anas.restful.entity.Address;
import bang_anas.restful.entity.Contact;
import bang_anas.restful.entity.User;
import bang_anas.restful.model.AddressResponse;
import bang_anas.restful.model.CreateAddressRequest;
import bang_anas.restful.model.UpdateAddressRequest;
import bang_anas.restful.repository.AddressRepository;
import bang_anas.restful.repository.ContactRepository;
import bang_anas.restful.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
    public AddressResponse create(User user, CreateAddressRequest request) {

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

    @Transactional(readOnly = true)
    public AddressResponse get(User user, String contactId, String addressID) {

        Contact contact = contactRepository.findFirstByUserAndId(
                user,
                contactId
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "contact is not found"
        ));

        Address address =
                addressRepository.findFirstByContactAndId(
                        contact,
                        addressID
                ).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "address is not found"
                ));

        return toAddressResponse(address);
    }

    @Transactional
    public AddressResponse update(User user, UpdateAddressRequest request) {

        validationService.validate(request);

        Contact contact =
                contactRepository.findFirstByUserAndId(
                        user,
                        request.getContactId()
                ).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "contact is not found"
                ));

        Address address =
                addressRepository.findFirstByContactAndId(
                        contact,
                        request.getAddressId()
                ).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "address is not found"
                ));

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        addressRepository.save(address);

        return toAddressResponse(address);
    }

    @Transactional
    public void remove(User user, String contactId, String addressId) {
        Contact contact = contactRepository.findFirstByUserAndId(
                user,
                contactId
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "contact is not found"
        ));

        Address address =
                addressRepository.findFirstByContactAndId(
                        contact,
                        addressId
                ).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "address is not found"
                ));

        addressRepository.delete(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> list(User user, String contactId) {
        Contact contact = contactRepository.findFirstByUserAndId(
                        user,
                        contactId
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Contact is not found"
                ));

        List<Address> addresses = addressRepository.findAllByContact(contact);

        return addresses.stream().map(this::toAddressResponse).toList();
    }
}
