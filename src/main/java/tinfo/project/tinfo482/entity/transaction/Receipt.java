package tinfo.project.tinfo482.entity.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tinfo.project.tinfo482.dto.transaction.ReceiptDto;
import tinfo.project.tinfo482.entity.inventory.Item;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name= "cart_bundle_id", referencedColumnName = "id")
    private CartBundle cartBundle;

    private float subTotal;
    private float tax;

    @ManyToOne
    @JoinColumn(name="member_id", referencedColumnName = "id")
    private Member member;

    private LocalDate date;



    public ReceiptDto toReceiptDto(){
        return ReceiptDto.builder()
                .cartBundleDto(this.getCartBundle().toCartBundleDto())
                .addressDto(this.getMember().getAddress().toAddressDto())
                .id(this.getId())
                .tax(this.getTax())
                .subTotal(this.getSubTotal())
                .user_id(this.getMember().getId())
                .username(this.getMember().getUsername())
                .date(this.getDate())
                .build();
    }



}
