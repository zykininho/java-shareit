package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Test
    void saveItem() {
        User user = makeUser("User", "user@ya.ru");
        UserDto userDto = userMapper.toUserDto(user);
        Item item = makeItem("Item 1", "Description of item 1", true, user);
        ItemDto itemDto = itemMapper.toItemDto(item);

        userDto = userService.create(userDto);
        itemService.addNewItem(userDto.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.owner.id = :ownerId", Item.class);
        Item savedItem = query.setParameter("ownerId", userDto.getId()).getSingleResult();

        assertThat(savedItem.getId(), notNullValue());
        assertThat(savedItem.getName(), equalTo(item.getName()));
        assertThat(savedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(savedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(savedItem.getOwner().getId(), equalTo(userDto.getId()));
    }

    private Item makeItem(String name, String description, boolean available, User owner) {
        return Item.builder()
                .name(name)
                .description(description)
                .available(available)
                .build();
    }

    private User makeUser(String name, String email) {
        return User.builder()
                .name(name)
                .email(email)
                .build();
    }

}