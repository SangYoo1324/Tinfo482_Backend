package tinfo.project.tinfo482.dto.transaction;


import lombok.Builder;
import lombok.Data;
import tinfo.project.tinfo482.dto.AddressDto;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ReceiptDto {

    private Long id;
    private float subTotal;
    private float tax;
    private CartBundleDto cartBundleDto;
    private String username;
    private AddressDto addressDto;
    private Long user_id;
    private LocalDate date;
}
