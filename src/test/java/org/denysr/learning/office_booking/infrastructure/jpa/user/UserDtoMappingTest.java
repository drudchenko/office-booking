package org.denysr.learning.office_booking.infrastructure.jpa.user;

import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.UserEmail;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.user.UserName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserDtoMappingTest {
    private final static int USER_ID = 4;
    private final static String USER_EMAIL = "Smith@gmail.com";
    private final static String USER_FIRST_NAME = "Stephen";
    private final static String USER_SECOND_NAME = "Smith";

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void shouldConvertDomainUserToJpaDto() {
        User user = User.builder()
                .withUserId(new UserId(USER_ID))
                .withUserEmail(new UserEmail(USER_EMAIL))
                .withUserName(new UserName(USER_FIRST_NAME, USER_SECOND_NAME))
                .build();

        UserJpaDto userJpaDto = modelMapper.map(user, UserJpaDto.class);

        assertAll(
                () -> assertEquals(USER_ID, userJpaDto.getUserId()),
                () -> assertEquals(USER_EMAIL, userJpaDto.getEmail()),
                () -> assertEquals(USER_FIRST_NAME, userJpaDto.getFirstName()),
                () -> assertEquals(USER_SECOND_NAME, userJpaDto.getSecondName())
        );
    }

    @Test
    void shouldConvertJpaDtoUserToDomainModel() {
        UserJpaDto userJpaDto = new UserJpaDto(USER_ID, USER_EMAIL, USER_FIRST_NAME, USER_SECOND_NAME);

        User user = modelMapper.map(userJpaDto, User.class);

        assertAll(
                () -> assertEquals(USER_ID, user.userId().userId()),
                () -> assertEquals(USER_EMAIL, user.userEmail().email()),
                () -> assertEquals(USER_FIRST_NAME, user.userName().firstName()),
                () -> assertEquals(USER_SECOND_NAME, user.userName().secondName())
        );
    }

    @Test
    void shouldConvertJpaUserListToDomainModelList() {
        UserJpaDto userJpaDto1 = new UserJpaDto(USER_ID, USER_EMAIL, USER_FIRST_NAME, USER_SECOND_NAME);
        int userId2 = 5;
        String userEmail2 = "John.Doe@example.com";
        String userFirstName2 = "John";
        String userSecondName2 = "Doe";
        UserJpaDto userJpaDto2 = new UserJpaDto(userId2, userEmail2, userFirstName2, userSecondName2);
        List<UserJpaDto> jpaDtoUsers = Arrays.asList(userJpaDto1, userJpaDto2);

        Type userListType = new TypeToken<List<User>>() {}.getType();
        List<User> users = modelMapper.map(jpaDtoUsers, userListType);

        assertAll(
                () -> assertEquals(USER_ID, users.get(0).userId().userId()),
                () -> assertEquals(USER_EMAIL, users.get(0).userEmail().email()),
                () -> assertEquals(USER_FIRST_NAME, users.get(0).userName().firstName()),
                () -> assertEquals(USER_SECOND_NAME, users.get(0).userName().secondName()),
                () -> assertEquals(userId2, users.get(1).userId().userId()),
                () -> assertEquals(userEmail2, users.get(1).userEmail().email()),
                () -> assertEquals(userFirstName2, users.get(1).userName().firstName()),
                () -> assertEquals(userSecondName2, users.get(1).userName().secondName())
        );
    }
}
