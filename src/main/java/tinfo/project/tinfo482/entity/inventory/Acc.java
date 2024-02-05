package tinfo.project.tinfo482.entity.inventory;

import jakarta.persistence.*;
import lombok.*;
import tinfo.project.tinfo482.dto.inventory.AccDto;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor

@DiscriminatorValue("acc_indicator")

public class Acc extends Item{

    @Lob
    private String content;

    @OneToMany(mappedBy = "acc", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<CompleteItem> completeItemList = new ArrayList<CompleteItem>();


    @Builder
    public Acc(Long id, Long price, String name, int stock, String content, String img_url, List<CompleteItem> completeItemList) {
        super(id, price, name, stock, img_url);
        this.content = content;
        this.completeItemList = completeItemList;
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
