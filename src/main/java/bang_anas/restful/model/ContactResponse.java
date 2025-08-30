package bang_anas.restful.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ContactResponse {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;
}
