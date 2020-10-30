package org.denysr.learning.office_booking.infrastructure.user;

import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.user.UserRepository;
import org.denysr.learning.office_booking.domain.validation.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
final public class InMemoryUserRepository implements UserRepository {
    private final ConcurrentMap<UserId, User> users = new ConcurrentHashMap<>();

    @Override
    public User findUserById(UserId userId) {
        Assert.notNull(userId, "User id is required param");
        if (!users.containsKey(userId)) {
            throw new EntityNotFoundException("User with mentioned id not found");
        }
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public synchronized UserId saveUser(User user) {
        if (user.hasId()) {
            return changeUser(user);
        } else {
            return createUser(user);
        }
    }

    @Override
    public synchronized void deleteUser(UserId userId) {
        if (!users.containsKey(userId)) {
            throw new EntityNotFoundException("Attempt to delete the nonexistent user.");
        }
        users.remove(userId);
    }

    private UserId changeUser(User user) {
        final UserId userId = user.getUserId();
        if (!users.containsKey(userId)) {
            throw new EntityNotFoundException("Attempt to change the nonexistent user.");
        }
        users.put(userId, user);
        return userId;
    }

    private UserId createUser(User user) {
        UserId newUserId;
        if (users.isEmpty()) {
            newUserId = new UserId(1);
        } else {
            UserId maxUserId = Collections.max(users.keySet());
            newUserId = new UserId(maxUserId.getUserId() + 1);
        }
        users.put(newUserId, user.withUserId(newUserId));
        return newUserId;
    }
}
