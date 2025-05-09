package by.aleksabrakor.user_subscriptions_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Subscription DTO")
public class SubscriptionDto {

    @Schema(description = "Subscription id", example = "1")
    private Long id;

    @NotBlank
    @Size(min = 2)
    @Schema(description = "service title", example = "YouTube")
    private String serviceTitle;

    @Schema(description = "plan", example = "Premium")
    private String plan;

    @Schema(description = "description")
    private String description;

    @Schema(description = "user id", example = "1")
    private Long userId;
}
