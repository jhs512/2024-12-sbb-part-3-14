package com.kkd.sbb.user;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String username;

    private String password;

//    @Column(unique = true)
    private String email;

    private String registerId;

    @Builder
    public SiteUser(String username, String email){
        this.username = username;
        this.email = email;
    }

}
