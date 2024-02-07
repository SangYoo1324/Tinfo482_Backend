package tinfo.project.tinfo482.entity.inventory;


import jakarta.persistence.*;
import lombok.*;
import tinfo.project.tinfo482.dto.inventory.AccDto;
import tinfo.project.tinfo482.dto.inventory.FlowerDto;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor

@DiscriminatorValue("flower_indicator")
public class Flower extends Item{

    //Birthday, Wedding, Graduation etc...
    private String category;

    @Lob
    private String content;


    @OneToMany(mappedBy = "flower", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<CompleteItem> completeItemList = new ArrayList<CompleteItem>();

    @Builder
    public Flower(Long id, Long price, String name, int stock, String content, String category, String img_url, List<CompleteItem> completeItemList, boolean delivery) {
        super(id, price, name, stock, img_url, delivery);
        this.content = content;
        this.category = category;
        this.completeItemList = completeItemList;
    }

    public FlowerDto toFlowerDto(){
        return FlowerDto.builder()
                .id(this.id)
                .price(this.price)
                .name(this.name)
                .stock(this.stock)
                .price(this.price)
                .content(this.content)
                .category(this.category)
                .img_url(this.img_url)
                .delivery(this.delivery)
                .build();
    }

}
