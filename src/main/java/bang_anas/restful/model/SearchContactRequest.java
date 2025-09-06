package bang_anas.restful.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SearchContactRequest {

    private String email;

    private String name;

    private String phone;

    @NotNull
    private Integer page;

    @NotNull
    private Integer size;

}
