package tinfo.project.tinfo482.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDto {
    private int errorCode;
    private String description;
}
