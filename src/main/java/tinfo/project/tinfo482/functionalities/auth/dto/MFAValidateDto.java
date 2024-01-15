package tinfo.project.tinfo482.functionalities.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MFAValidateDto {
    private String email;
    private String verificationCode;
}

