package org.denysr.learning.office_booking.infrastructure.jpa.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity(name = "Users")
@Table(indexes = @Index(columnList = "email", unique = true, name = "email"))
public class UserJpaDto {
    @Id
    @GeneratedValue
    private Integer userId;
    private String email;
    private String firstName;
    private String secondName;
}
