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
        addUserDtoToBuilderMapping(modelMapper);
        addUserJpaDtoToDomainMapping(modelMapper);
        addBookingJpaDtoToDomainMapping(modelMapper);
        return modelMapper;
    }

    private void addUserDtoToBuilderMapping(ModelMapper modelMapper) {
        TypeMap<UserRestDto, UserBuilder> userTypeMap = modelMapper.createTypeMap(UserRestDto.class, UserBuilder.class);
        userTypeMap.setProvider(request -> {
            UserRestDto source = (UserRestDto) request.getSource();
            return User.builder()
                    .withUserEmail(new UserEmail(source.getEmail()))
                    .withUserName(new UserName(source.getFirstName(), source.getSecondName()));
        });
    }

    private void addUserJpaDtoToDomainMapping(ModelMapper modelMapper) {
        TypeMap<UserJpaDto, User> userTypeMap = modelMapper.createTypeMap(UserJpaDto.class, User.class);
        userTypeMap.setProvider(request -> {
            UserJpaDto source = (UserJpaDto) request.getSource();
            return User.builder()
                    .withUserId(new UserId(source.getUserId()))
                    .withUserEmail(new UserEmail(source.getEmail()))
                    .withUserName(new UserName(source.getFirstName(), source.getSecondName()))
                    .build();
        });
    }

    private void addBookingJpaDtoToDomainMapping(ModelMapper modelMapper) {
        TypeMap<BookingJpaDto, Booking> bookingTypeMap = modelMapper.createTypeMap(BookingJpaDto.class, Booking.class);
        bookingTypeMap.setProvider(request -> {
            BookingJpaDto source = (BookingJpaDto) request.getSource();
            return Booking.builder()
                    .withBookingId(new BookingId(source.getBookingId()))
                    .withUserId(new UserId(source.getUserId()))
                    .withBookingDateRange(new BookingDateRange(source.getStartDate(), source.getEndDate()))
                    .build();
        });
    }
}
