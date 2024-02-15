package tinfo.project.tinfo482.functionalities.redis.redisTest;



import jakarta.persistence.*;
import lombok.*;
import tinfo.project.tinfo482.dto.inventory.AccDto;
import tinfo.project.tinfo482.entity.inventory.CompleteItem;
import tinfo.project.tinfo482.entity.inventory.Item;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor

@DiscriminatorValue("testObj_indicator")
public class RedisTestObj extends Item {

    @Lob
    private String content;


    @Builder
    public RedisTestObj(Long id, Long price, String name, int stock, String content, String img_url, List<CompleteItem> completeItemList, boolean delivery) {
        super(id, price, name, stock, img_url, delivery);
        this.content = content;
    }

}
