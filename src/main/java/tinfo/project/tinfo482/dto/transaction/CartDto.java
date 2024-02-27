package tinfo.project.tinfo482.dto.transaction;


import lombok.Builder;
import lombok.Data;
import tinfo.project.tinfo482.dto.AddressDto;
import tinfo.project.tinfo482.dto.inventory.FlowerDto;
import tinfo.project.tinfo482.dto.inventory.ItemDto;
import tinfo.project.tinfo482.entity.inventory.Item;

@Data
@Builder
public class CartDto {

    private Long id;

    private ItemDto itemDto;

    private Long userId;

    private AddressDto addressDto;

    private int quantity;

    private Long bundleId;



}
