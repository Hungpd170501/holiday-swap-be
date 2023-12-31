package com.example.holidayswap.domain.entity.auth;

import com.example.holidayswap.domain.entity.booking.Booking;
import com.example.holidayswap.domain.entity.chat.ConversationParticipant;
import com.example.holidayswap.domain.entity.common.BaseEntityAudit;
import com.example.holidayswap.domain.entity.notification.Notification;
import com.example.holidayswap.domain.entity.payment.MoneyTranfer;
import com.example.holidayswap.domain.entity.payment.TransactLog;
import com.example.holidayswap.domain.entity.payment.Wallet;
import com.example.holidayswap.domain.entity.post.Post;
import com.example.holidayswap.domain.entity.post.UserReactionPost;
import com.example.holidayswap.domain.entity.subscription.Subscription;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "users")
public class User extends BaseEntityAudit implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "user_id"
    )
    private Long userId;

    @NotBlank(message = "Email must be specified.")
    @Email(message = "Email must be valid.")
    @Column(
            nullable = false,
            unique = true
    )
    private String email;

    @NotBlank(message = "Password must be specified.")
    @Column(nullable = false, name = "password_hash")
    private String passwordHash;

    @NotEmpty(message = "Username must be specified.")
    @Nationalized
    @Column(nullable = false,
            unique = true
    )
    private String username;

    @Nationalized
    @Column(name = "full_name")
    private String fullName;

    @Column
    private String avatar;

    @NotNull(message = "Gender must be specified.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @NotNull(message = "Date of birth must be specified.")
    @Column(name = "dob")
    private LocalDate dob;

    @Column
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phone;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "phone_verified")
    private boolean phoneVerified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<MoneyTranfer> moneyTranfers;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id", referencedColumnName = "wallet_id")
    private Wallet wallet;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "role_id")
    private Role role;
@JsonIgnore
    @OneToMany(mappedBy = "user",
            orphanRemoval = true
    )
    private List<Subscription> subscriptions;

    @ManyToMany
    @JoinTable(name = "user_blocked",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "blocked_user_id"))
    private List<User> userBlockedList;

    @JsonIgnore
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<UserReactionPost> userReactionPosts;

    @JsonIgnore
    @OneToMany(mappedBy = "user",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private List<Token> tokens;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<Post> posts;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<ConversationParticipant> conversationParticipants;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Booking> bookingList;
@JsonIgnore
    @OneToMany(mappedBy = "userOwner")
    private List<Booking> ownerBookingList;
@JsonIgnore
    @OneToMany(mappedBy = "user",
            orphanRemoval = true
    )
    private List<Notification> notifications;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.getName()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status.equals(UserStatus.ACTIVE) || status.equals(UserStatus.PENDING);
    }

    @Override
    public boolean isAccountNonLocked() {
        return status.equals(UserStatus.ACTIVE) || status.equals(UserStatus.PENDING);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
