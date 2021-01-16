package org.denysr.learning.office_booking.infrastructure.jpa.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

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
