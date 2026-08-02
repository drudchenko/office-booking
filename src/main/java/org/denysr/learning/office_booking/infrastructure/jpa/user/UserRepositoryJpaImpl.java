package org.denysr.learning.office_booking.infrastructure.jpa.user;

import lombok.RequiredArgsConstructor;
import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.user.UserRepository;
import org.denysr.learning.office_booking.domain.user.exceptions.EmailAlreadyTakenException;
import org.denysr.learning.office_booking.domain.validation.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class UserRepositoryJpaImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final ModelMapper modelMapper;

    @Override
    public User findUserById(UserId userId) throws EntityNotFoundException {
        final UserJpaDto userDto = jpaUserRepository.findByUserId(userId.userId());
        if (userDto == null) {
            throw new EntityNotFoundException(notFoundMessage(userId));
        }
        return modelMapper.map(userDto, User.class);
    }

    @Override
    public List<User> getAllUsers() {
        return modelMapper.map(
                jpaUserRepository.findAll(),
                new TypeToken<List<User>>() {}.getType()
        );
    }

    /**
     * Saving a user with an id that no longer exists would silently insert a new row under a fresh
     * id, so an update of an unknown user is reported as such instead.
     */
    @Override
    public UserId saveUser(User user) throws EntityNotFoundException {
        if (user.userId() != null) {
            requireExisting(user.userId());
        }
        final int userId;
        try {
            userId = jpaUserRepository
                    .save(modelMapper.map(user, UserJpaDto.class))
                    .getUserId();
        } catch (DataIntegrityViolationException e) {
            // The only constraint on the table is the unique email index.
            throw new EmailAlreadyTakenException(
                    "Email " + user.userEmail().email() + " is already taken", e);
        }
        return new UserId(userId);
    }

    /** {@code deleteById} is a no-op for unknown ids, so the row has to be looked up first. */
    @Override
    public void deleteUser(UserId userId) throws EntityNotFoundException {
        requireExisting(userId);
        jpaUserRepository.deleteById(userId.userId());
    }

    private void requireExisting(UserId userId) throws EntityNotFoundException {
        if (!jpaUserRepository.existsById(userId.userId())) {
            throw new EntityNotFoundException(notFoundMessage(userId));
        }
    }

    private static String notFoundMessage(UserId userId) {
        return "User with id " + userId.userId() + " not found";
    }
}
