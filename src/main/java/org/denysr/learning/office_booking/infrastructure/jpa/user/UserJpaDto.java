package org.denysr.learning.office_booking.infrastructure.jpa.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserJpaDto {
    private int userId;
    private String email;
    private String firstName;
    private String secondName;
}
