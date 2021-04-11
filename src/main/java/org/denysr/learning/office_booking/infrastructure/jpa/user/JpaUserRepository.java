package org.denysr.learning.office_booking.infrastructure.jpa.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserJpaDto, Integer> {
    UserJpaDto findByUserId(int id);
}
