package uz.pdp.appjwtrealemailauditing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private UUID id;

   // @Size(min = 3, max = 50) spring-boot-starter-validation kutubxonasini yuklash orqali ishlaydi(Malumotlar omboriga tasir qilmaydi faqat javani uzi tekshiradi)
//    @Column(nullable = false, length = 50)
    private String firstName;

    //@Length(min = 3, max = 50) @Size bilan bir xil
//    @Column(nullable = false)
    private String lastName;

    //@Email example@mail.ru shaklidagi ko'rinishda bo'lishini taminlab beradi.
    @Column(unique = true)
    private String email; //userning emaili

//    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt; // ushbu USER qachon sistemaga kirgan

    @UpdateTimestamp
    private Timestamp updatedAt;


    @ManyToMany
    private Set<Role> roles;

    private boolean accountNonExpired = true; //bu userning amal qilish muddati o'tmaganligi
    private boolean accountNonLocked = true; //bu user bloklanmaganligi
    private boolean credentialsNonExpired = true; //
    private boolean enabled;
    private String emailCode;

// Bu UserDetailsning majburiy metodlari

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    // Akkauntning ishonchlilik muddati tugagan yoki tugamaganligini qaytaradi
    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    // Akkauntning yoniq yoki o'chiqligini ifodalaydi
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
