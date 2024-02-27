package tinfo.project.tinfo482.dto.inventory;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowerDto extends ItemDto{

    private Long id;

    //Birthday, Wedding, Graduation etc...
    private String category;

    private String content;

    private Long price;

    private String name;

    private int stock;

    private String img_url;

    private boolean delivery;
}
