package tinfo.project.tinfo482.entity.transaction;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tinfo.project.tinfo482.dto.transaction.CartBundleDto;
import tinfo.project.tinfo482.dto.transaction.CartDto;
import tinfo.project.tinfo482.entity.inventory.CompleteItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartBundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cartBundle", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Cart> cartList = new ArrayList<Cart>();

    private String request;

    public CartBundleDto toCartBundleDto(){



        return CartBundleDto.builder()
                .cartDtos(
                        this.getCartList().stream().map(
                                cart->
                                        cart.toCartDto(cart.getQuantity(),cart.getCartBundle().getId())
                        ).collect(Collectors.toList())
                )
                .request(getRequest())
                .build();
    }
}
