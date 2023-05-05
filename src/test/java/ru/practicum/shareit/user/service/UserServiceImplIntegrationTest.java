package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserService userService;

    @Test
    void saveUser() {
        UserDto userDto = makeUser("User", "user@ya.ru");
        userDto = userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User savedUser = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getName(), equalTo(userDto.getName()));
        assertThat(savedUser.getEmail(), equalTo(userDto.getEmail()));
    }

    private UserDto makeUser(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }

}