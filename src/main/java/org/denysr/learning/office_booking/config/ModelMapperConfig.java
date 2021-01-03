package org.denysr.learning.office_booking.config;

import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.User.UserBuilder;
import org.denysr.learning.office_booking.domain.user.UserEmail;
import org.denysr.learning.office_booking.domain.user.UserName;
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
}
