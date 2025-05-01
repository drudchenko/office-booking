package org.denysr.learning.office_booking.controllers.mapping;

import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.User.UserBuilder;
import org.denysr.learning.office_booking.domain.user.UserEmail;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.user.UserName;
import org.denysr.learning.office_booking.infrastructure.rest.UserResponseEntity;
import org.denysr.learning.office_booking.infrastructure.rest.UserRestDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserRestMapperTest {
    private final static int CORRECT_ID = 3;
    private final static String CORRECT_EMAIL = "pupkin@mail.com";
    private final static String CORRECT_FIRST_NAME = "Vasya";
    private final static String CORRECT_SECOND_NAME = "Pupkin";

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void shouldTransformProperRestDtoToBuilder() {
        UserRestDto userRestDto = new UserRestDto(CORRECT_EMAIL, CORRECT_FIRST_NAME, CORRECT_SECOND_NAME);

        User user = modelMapper.map(userRestDto, UserBuilder.class).build();

        assertAll(
                () -> assertEquals(CORRECT_EMAIL, user.userEmail().email()),
                () -> assertEquals(CORRECT_FIRST_NAME, user.userName().firstName()),
                () -> assertEquals(CORRECT_SECOND_NAME, user.userName().secondName())
        );
    }

    @Test
    void shouldTransformCorrectDomainUserToResponse() {
        User user = new User(
                new UserId(CORRECT_ID),
                new UserEmail(CORRECT_EMAIL),
                new UserName(CORRECT_FIRST_NAME, CORRECT_SECOND_NAME)
        );

        UserResponseEntity userResponseEntity = modelMapper.map(user, UserResponseEntity.class);

        assertAll(
                () -> assertEquals(CORRECT_ID, userResponseEntity.userId()),
                () -> assertEquals(CORRECT_EMAIL, userResponseEntity.email()),
                () -> assertEquals(CORRECT_FIRST_NAME, userResponseEntity.firstName()),
                () -> assertEquals(CORRECT_SECOND_NAME, userResponseEntity.secondName())
        );
    }
}
