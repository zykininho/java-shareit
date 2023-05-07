package ru.practicum.shareit.request.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRequestRepositoryDataJpaTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository repository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User anotherUser;
    private ItemRequest request;
    private ItemRequest anotherRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("User")
                .email("user@ya.ru")
                .build();

        anotherUser = User.builder()
                .name("Another user")
                .email("another_user@ya.ru")
                .build();

        request = ItemRequest.builder()
                .description("Request 1")
                .created(LocalDateTime.of(2023, 6, 5, 12, 0, 0))
                .requestor(anotherUser)
                .build();

        anotherRequest = ItemRequest.builder()
                .description("Request 2")
                .created(LocalDateTime.of(2023, 6, 5, 15, 0, 0))
                .requestor(anotherUser)
                .build();
    }

    @Test
    void findRequestsFromOthers() {
        userRepository.save(user);
        userRepository.save(anotherUser);

        repository.save(request);
        repository.save(anotherRequest);

        TypedQuery<ItemRequest> query = em.getEntityManager()
                .createQuery("Select r from ItemRequest r where r.requestor.id <> :id", ItemRequest.class);
        List<ItemRequest> requests = query.setParameter("id", user.getId()).getResultList();

        assertEquals(List.of(request, anotherRequest), requests);
    }

}