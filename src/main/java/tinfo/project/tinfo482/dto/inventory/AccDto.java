package tinfo.project.tinfo482.dto.inventory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccDto {

    private Long id;

    private Long price;

    private String name;

    private int stock;

    private String content;

    private String img_url;

}
