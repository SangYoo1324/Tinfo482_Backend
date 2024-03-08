package tinfo.project.tinfo482.dto.transaction;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReceiptData {

    String name;
    Long price;

}
