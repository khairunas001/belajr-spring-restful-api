package bang_anas.restful.controller;

import bang_anas.restful.entity.User;
import bang_anas.restful.model.ContactResponse;
import bang_anas.restful.model.CreateContactRequest;
import bang_anas.restful.model.WebResponse;
import bang_anas.restful.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;


    @PostMapping(
            path = "/api/contacts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request){
        ContactResponse contactResponse = contactService.create(user,request);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }

    @GetMapping(
            path = "/api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private WebResponse<ContactResponse> get(User user, @PathVariable("contactId") String contactId){
        ContactResponse contactResponse = contactService.get(user,contactId);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }
}
