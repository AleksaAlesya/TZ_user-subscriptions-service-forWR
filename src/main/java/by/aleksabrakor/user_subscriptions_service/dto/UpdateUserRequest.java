package by.aleksabrakor.user_subscriptions_service.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "User DTO")
public class UpdateUserRequest {

    @Schema(description = "user id", example = "1")
    private Long id;

    @Size(min = 2)
    @Schema(description = "name", example = "Наталья Петровна")
    private String name;

    @Email
    @Schema(description = "email", example = "nataly@mail.com")
    private String email;

    @Schema(description = "list subscriptions")
    private List<SubscriptionDto> subscriptions;
}
