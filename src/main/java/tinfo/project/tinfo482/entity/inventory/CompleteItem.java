package tinfo.project.tinfo482.entity.inventory;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tinfo.project.tinfo482.dto.inventory.CompleteItemDto;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="flower_id", referencedColumnName = "id")
    private Flower flower;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="acc_id", referencedColumnName = "id")
    private Acc acc;


    public CompleteItemDto toCompleteItemDto(){
        return CompleteItemDto.builder()
                .id(this.id)
                .flowerDto(this.flower.toFlowerDto())
                .accDto(this.acc.toAccDto())
                .build();
    }
}
