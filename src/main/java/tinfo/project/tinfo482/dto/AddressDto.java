package tinfo.project.tinfo482.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDto {
    private String address1;
    private String city;
    private String state;
    private String zipCode;
}
