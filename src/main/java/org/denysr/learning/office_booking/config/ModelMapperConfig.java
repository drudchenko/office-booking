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
        addUserRestDtoToBuilderMapping(modelMapper);
        addBookingDomainToRestMapping(modelMapper);
        addUserJpaDtoToDomainMapping(modelMapper);
        addBookingJpaDtoToDomainMapping(modelMapper);
        addUserDomainToRestMapping(modelMapper);
        addBookingDomainModelToJpaMapping(modelMapper);
        return modelMapper;
    }

    private void addUserRestDtoToBuilderMapping(ModelMapper modelMapper) {
        TypeMap<UserRestDto, UserBuilder> userTypeMap = modelMapper.createTypeMap(UserRestDto.class, UserBuilder.class);
        userTypeMap.setConverter(context -> {
            UserRestDto source = context.getSource();
            return User.builder()
                    .withUserEmail(new UserEmail(source.email()))
                    .withUserName(new UserName(source.firstName(), source.secondName()));
        });
    }

    private void addUserDomainToRestMapping(ModelMapper modelMapper) {
        TypeMap<User, UserResponseEntity> userTypeMap = modelMapper.createTypeMap(User.class, UserResponseEntity.class);
        userTypeMap.setConverter(context -> {
            User user = context.getSource();
            return new UserResponseEntity(
                    user.getUserId().getUserId(),
                    user.getUserEmail().getEmail(),
                    user.getUserName().getFirstName(),
                    user.getUserName().getSecondName()
            );
        });
    }

    private void addBookingDomainToRestMapping(ModelMapper modelMapper) {
        TypeMap<Booking, BookingResponseEntity> bookingTypeMap = modelMapper
                .createTypeMap(Booking.class, BookingResponseEntity.class);
        bookingTypeMap.setConverter(context -> {
            Booking booking = context.getSource();
            return new BookingResponseEntity(
                    booking.getBookingId().getBookingId(),
                    booking.getUser().getUserName().getFullName(),
                    booking.getBookingDateRange().getStartDate(),
                    booking.getBookingDateRange().getEndDate()
            );
        });
    }

    private void addUserJpaDtoToDomainMapping(ModelMapper modelMapper) {
        TypeMap<UserJpaDto, User> userTypeMap = modelMapper.createTypeMap(UserJpaDto.class, User.class);
        userTypeMap.setConverter(context -> {
            UserJpaDto source = context.getSource();
            return User.builder()
                    .withUserId(new UserId(source.getUserId()))
                    .withUserEmail(new UserEmail(source.getEmail()))
                    .withUserName(new UserName(source.getFirstName(), source.getSecondName()))
                    .build();
        });
    }

    private void addBookingJpaDtoToDomainMapping(ModelMapper modelMapper) {
        TypeMap<BookingJpaDto, Booking> bookingTypeMap = modelMapper.createTypeMap(BookingJpaDto.class, Booking.class);
        bookingTypeMap.setConverter(context -> {
            BookingJpaDto source = context.getSource();
            User user = modelMapper.map(source.getUserDto(), User.class);
            return Booking.builder()
                    .withBookingId(new BookingId(source.getBookingId()))
                    .withUser(user)
                    .withBookingDateRange(new BookingDateRange(source.getStartDate(), source.getEndDate()))
                    .build();
        });
    }

    private void addBookingDomainModelToJpaMapping(ModelMapper modelMapper) {
        TypeMap<Booking, BookingJpaDto> bookingTypeMap = modelMapper.createTypeMap(Booking.class, BookingJpaDto.class);
        bookingTypeMap.setProvider(request -> {
            Booking source = (Booking) request.getSource();
            BookingJpaDto bookingJpaDto = new BookingJpaDto();
            bookingJpaDto.setUserDto(modelMapper.map(source.getUser(), UserJpaDto.class));
            return bookingJpaDto;
        });
    }
}
