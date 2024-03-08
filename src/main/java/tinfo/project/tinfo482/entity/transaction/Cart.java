package tinfo.project.tinfo482.entity.transaction;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import tinfo.project.tinfo482.dto.inventory.FlowerDto;
import tinfo.project.tinfo482.dto.inventory.ItemDto;
import tinfo.project.tinfo482.dto.transaction.CartDto;
import tinfo.project.tinfo482.entity.inventory.Acc;
import tinfo.project.tinfo482.entity.inventory.Flower;
import tinfo.project.tinfo482.entity.inventory.Item;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="member_id", referencedColumnName = "id", nullable = false)
    private Member member ;

    @OneToOne
    @JoinColumn(name="item_id", referencedColumnName = "id", nullable = false)
    private Item item;

    private int quantity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="bundle_id", referencedColumnName = "id", nullable = true)
    private CartBundle cartBundle;

    @ColumnDefault("false")
    private boolean transaction;

    public CartDto toCartDto(int quantity, Long bundleId){
        ItemDto itemDto = null;
        if(item instanceof Flower){
            itemDto =(ItemDto)((Flower) item).toFlowerDto();
        } else {
          itemDto = (ItemDto) ((Acc)item).toAccDto();
        }

        return CartDto.builder()
                .id(this.getId())
                .addressDto(this.member.getAddress().toAddressDto())
                .itemDto(itemDto)
                .userId(member.getId())
                .quantity(quantity)
                .bundleId(bundleId)
                .build();
    }
}
