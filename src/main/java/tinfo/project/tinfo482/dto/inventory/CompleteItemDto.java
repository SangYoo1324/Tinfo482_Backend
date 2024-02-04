package tinfo.project.tinfo482.dto.inventory;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class CompleteItemDto {

    // completeItem ID
    private Long id;

    private AccDto accDto;

    private FlowerDto flowerDto;


}


