package ru.practicum.shareit.item.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryDataJpaTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private static User user;
    private static Item item;
    private static Item anotherItem;
    private static Item unavailableItem;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("User")
                .email("user@ya.ru")
                .build();

        item = Item.builder()
                .name("Name for search")
                .description("Description of item 1")
                .available(true)
                .owner(user)
                .build();

        anotherItem = Item.builder()
                .name("Item 2")
                .description("Description for search")
                .available(true)
                .owner(user)
                .build();

        unavailableItem = Item.builder()
                .name("Name for search")
                .description("Description for search")
                .available(false)
                .owner(user)
                .build();
    }

    @Test
    void saveItemsAndSearchByName() {

        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(anotherItem);
        itemRepository.save(unavailableItem);

        String text = "Name for search";

        TypedQuery<Item> query = em.getEntityManager()
                .createQuery("Select i from Item i " +
                        "where i.available = true and (upper(i.name) like upper(concat('%', :text, '%')) " +
                        " or upper(i.description) like upper(concat('%', :text, '%')))", Item.class);
        Item foundItem = query.setParameter("text", text).getSingleResult();

        assertEquals(item, foundItem);

    }

    @Test
    void saveItemsAndSearchByDescription() {

        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(anotherItem);
        itemRepository.save(unavailableItem);

        String text = "Description for search";

        TypedQuery<Item> query = em.getEntityManager()
                .createQuery("Select i from Item i " +
                        "where i.available = true and (upper(i.name) like upper(concat('%', :text, '%')) " +
                        " or upper(i.description) like upper(concat('%', :text, '%')))", Item.class);
        Item foundItem = query.setParameter("text", text).getSingleResult();

        assertEquals(anotherItem, foundItem);

    }

}