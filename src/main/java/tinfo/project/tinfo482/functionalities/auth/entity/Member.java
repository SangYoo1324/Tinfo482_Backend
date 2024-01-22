package tinfo.project.tinfo482.functionalities.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tinfo.project.tinfo482.entity.Address;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "startingThousand")
    @SequenceGenerator(name="startingThousand", sequenceName = "sequenceName", initialValue = 1000, allocationSize = 1)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private Role authority;

    @Column
    private String refreshToken;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private Address address;

    private String provider;


}
