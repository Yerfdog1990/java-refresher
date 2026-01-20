package springsecurity.lesson2datastructureofacl.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDto {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime dateCreated;
}
