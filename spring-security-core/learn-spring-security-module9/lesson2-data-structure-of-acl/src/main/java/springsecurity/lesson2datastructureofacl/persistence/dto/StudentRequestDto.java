package springsecurity.lesson2datastructureofacl.persistence.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import springsecurity.lesson2datastructureofacl.persistence.validation.OnCreate;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"username", "email"})
public class StudentRequestDto {
    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required", groups = OnCreate.class)
    private String password;

    private LocalDateTime dateCreated;
}
