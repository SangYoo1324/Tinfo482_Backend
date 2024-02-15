package tinfo.project.tinfo482.dto.inventory;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompleteItemDto {

    private List<AccDto> accDto;

    private FlowerDto flowerDto;


}


