package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    void saveNewRequest() {
        UserDto userDto = makeUser("User", "user@ya.ru");
        userDto = userService.create(userDto);

        ItemRequestShortDto itemRequestShortDto = makeRequestFromUser("Description 1");
        ItemRequestDto itemRequestDto = itemRequestService.create(userDto.getId(), itemRequestShortDto);

        assertThat(itemRequestDto.getId(), notNullValue());
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequestShortDto.getDescription()));
    }

    private UserDto makeUser(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }

    private ItemRequestShortDto makeRequestFromUser(String description) {
        return ItemRequestShortDto.builder()
                .description(description)
                .build();
    }

}