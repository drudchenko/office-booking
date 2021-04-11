package org.denysr.learning.office_booking.infrastructure.jpa.user;

import lombok.RequiredArgsConstructor;
import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.user.UserRepository;
import org.denysr.learning.office_booking.domain.validation.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class UserRepositoryJpaImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final ModelMapper modelMapper;

    @Override
    public User findUserById(UserId userId) throws EntityNotFoundException {
        return modelMapper.map(jpaUserRepository.findByUserId(userId.getUserId()), User.class);
    }

    @Override
    public List<User> getAllUsers() {
        return modelMapper.map(
                jpaUserRepository.findAll(),
                new TypeToken<List<User>>() {}.getType()
        );
    }

    @Override
    public UserId saveUser(User user) throws EntityNotFoundException {
        final int userId = jpaUserRepository
                .save(modelMapper.map(user, UserJpaDto.class))
                .getUserId();
        return new UserId(userId);
    }

    @Override
    public void deleteUser(UserId userId) throws EntityNotFoundException {
        try {
            jpaUserRepository.deleteById(userId.getUserId());
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Entity with the mentioned id not found", e);
        }
    }
}
