package tinfo.project.tinfo482.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tinfo.project.tinfo482.dto.AddressDto;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String address1;
    private String city;
    private String state;
    private String zipCode;


    public AddressDto toAddressDto(){
        return AddressDto.builder()
                .address1(this.address1)
                .zipCode(this.zipCode)
                .state(this.state)
                .city(this.city)
                .build();
    }


}
