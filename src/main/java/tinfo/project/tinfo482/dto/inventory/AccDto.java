package tinfo.project.tinfo482.dto.inventory;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AccDto {

    private Long id;

    private Long price;

    private String name;

    private int stock;

    private String content;

    private String img_url;

}
