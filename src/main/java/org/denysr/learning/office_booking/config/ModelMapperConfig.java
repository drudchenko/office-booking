package org.denysr.learning.office_booking.config;

import org.denysr.learning.office_booking.domain.booking.Booking;
import org.denysr.learning.office_booking.domain.booking.BookingDateRange;
import org.denysr.learning.office_booking.domain.booking.BookingId;
import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.User.UserBuilder;
import org.denysr.learning.office_booking.domain.user.UserEmail;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.user.UserName;
import org.denysr.learning.office_booking.infrastructure.jpa.booking.BookingJpaDto;
import org.denysr.learning.office_booking.infrastructure.jpa.user.UserJpaDto;
import org.denysr.learning.office_booking.infrastructure.rest.BookingResponseEntity;
import org.denysr.learning.office_booking.infrastructure.rest.UserResponseEntity;
import org.denysr.learning.office_booking.infrastructure.rest.UserRestDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        addUserMappings(modelMapper);
        addBookingDomainToRestMapping(modelMapper);
        return modelMapper;
    }

    private void addUserMappings(ModelMapper modelMapper) {
        TypeMap<UserRestDto, UserBuilder> userRestMap = modelMapper.createTypeMap(UserRestDto.class, UserBuilder.class);
        userRestMap.setConverter(context -> {
            UserRestDto source = context.getSource();
            return User.builder()
                    .withUserEmail(new UserEmail(source.email()))
                    .withUserName(new UserName(source.firstName(), source.secondName()));
        });

        TypeMap<User, UserResponseEntity> userResponseMap = modelMapper.createTypeMap(User.class, UserResponseEntity.class);
        userResponseMap.setConverter(context -> {
            User user = context.getSource();
            return new UserResponseEntity(
                    user.userId().userId(),
                    user.userEmail().email(),
                    user.userName().firstName(),
                    user.userName().secondName()
            );
        });

        TypeMap<UserJpaDto, User> userJpaMap = modelMapper.createTypeMap(UserJpaDto.class, User.class);
        userJpaMap.setConverter(context -> {
            UserJpaDto source = context.getSource();
            return User.builder()
                    .withUserId(new UserId(source.getUserId()))
                    .withUserEmail(new UserEmail(source.getEmail()))
                    .withUserName(new UserName(source.getFirstName(), source.getSecondName()))
                    .build();
        });

        TypeMap<User, UserJpaDto> userToJpaMap = modelMapper.createTypeMap(User.class, UserJpaDto.class);
        userToJpaMap.setProvider(request -> {
            User source = (User) request.getSource();
            UserJpaDto userJpaDto = new UserJpaDto();
            userJpaDto.setUserId(source.userId().userId());
            userJpaDto.setEmail(source.userEmail().email());
            userJpaDto.setFirstName(source.userName().firstName());
            userJpaDto.setSecondName(source.userName().secondName());
            return userJpaDto;
        });
    }

    private void addBookingDomainToRestMapping(ModelMapper modelMapper) {
        TypeMap<Booking, BookingResponseEntity> bookingResponseMap = modelMapper
                .createTypeMap(Booking.class, BookingResponseEntity.class);
        bookingResponseMap.setConverter(context -> {
            Booking booking = context.getSource();
            return new BookingResponseEntity(
                    booking.bookingId().bookingId(),
                    booking.user().userName().getFullName(),
                    booking.bookingDateRange().getStartDate(),
                    booking.bookingDateRange().getEndDate()
            );
        });

        TypeMap<BookingJpaDto, Booking> bookingJpaMap = modelMapper.createTypeMap(BookingJpaDto.class, Booking.class);
        bookingJpaMap.setConverter(context -> {
            BookingJpaDto source = context.getSource();
            User user = modelMapper.map(source.getUserDto(), User.class);
            return Booking.builder()
                    .withBookingId(new BookingId(source.getBookingId()))
                    .withUser(user)
                    .withBookingDateRange(new BookingDateRange(source.getStartDate(), source.getEndDate()))
                    .build();
        });

        TypeMap<Booking, BookingJpaDto> bookingtoJpaMap = modelMapper.createTypeMap(Booking.class, BookingJpaDto.class);
        bookingtoJpaMap.setProvider(request -> {
            Booking source = (Booking) request.getSource();
            BookingJpaDto bookingJpaDto = new BookingJpaDto();
            bookingJpaDto.setBookingId(source.bookingId().bookingId());
            bookingJpaDto.setStartDate(source.bookingDateRange().getStartDate());
            bookingJpaDto.setEndDate(source.bookingDateRange().getEndDate());
            bookingJpaDto.setUserDto(modelMapper.map(source.user(), UserJpaDto.class));
            return bookingJpaDto;
        });
    }
}
