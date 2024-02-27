package tinfo.project.tinfo482.dto.transaction;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CartBundleDto {

    private String request;
    private List<CartDto> cartDtos;

}
