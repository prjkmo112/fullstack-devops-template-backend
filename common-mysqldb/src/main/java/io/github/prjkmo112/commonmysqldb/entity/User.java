package io.github.prjkmo112.commonmysqldb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users", schema = "test")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 128)
    @NotNull
    @Column(name = "email", nullable = false, length = 128)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "passwd", nullable = false)
    private String passwd;

    @NotNull
    @Column(name = "role", nullable = false, length = 64)
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    @Column(name = "created_at")
    private Date createdAt;

}