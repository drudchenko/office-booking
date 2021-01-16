package org.denysr.learning.office_booking.infrastructure.jpa.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaUserRepository extends JpaRepository<UserJpaDto, Integer> {
    UserJpaDto findByUserId(int id);
    List<UserJpaDto> findAllBy();
    UserJpaDto save(UserJpaDto entity);
    void deleteById(Integer id);
}
