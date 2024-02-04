package tinfo.project.tinfo482.entity.inventory;

import jakarta.persistence.*;
import lombok.*;
import tinfo.project.tinfo482.dto.inventory.AccDto;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("acc_indicator")

public class Acc extends Item{

    @Lob
    private String content;


    @Builder
    public Acc(Long id, Long price, String name, int stock, String content, String img_url) {
        super(id, price, name, stock, img_url);
        this.content = content;
    }


    public AccDto toAccDto(){
        return AccDto.builder()
                .id(this.id)
                .content(this.content)
                .name(this.name)
                .price(this.price)
                .stock(this.stock)
                .img_url(this.img_url)
                .build();
    }
}
