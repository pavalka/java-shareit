package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.PageableByOffsetAndSize;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDao;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemDaoTest {
    private long nextUserId = 1;
    private final ItemDao itemRepository;
    private final UserDao userRepository;

    @Test
    void findByNameOrDescriptionLikeAndIsAvailableTrueReturnFirstItem() {
        var userOne = userRepository.save(createUser());
        var userTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(userOne, "item of userOne", "description One"));
        var pageable = new PageableByOffsetAndSize(0, 1, Sort.unsorted());

        itemRepository.save(createItem(userTwo, "name", "description of item two"));
        itemRepository.save(createItem(userOne, "name", "description"));

        var items = itemRepository.findByNameOrDescriptionLikeAndIsAvailableTrue("Tem", pageable);

        assertEquals(1, items.size());
        assertEquals(itemOne.getId(), items.get(0).getId());
        assertEquals(itemOne.getName(), items.get(0).getName());
        assertEquals(itemOne.getDescription(), items.get(0).getDescription());
        assertEquals(itemOne.getAvailable(), items.get(0).getAvailable());
        assertTrue(items.get(0).getAvailable());
        assertEquals(itemOne.getOwner(), items.get(0).getOwner());
    }

    @Test
    void findByNameOrDescriptionLikeAndIsAvailableTrueReturnItemTwo() {
        var userOne = userRepository.save(createUser());
        var userTwo = userRepository.save(createUser());
        var pageable = new PageableByOffsetAndSize(1, 1, Sort.unsorted());

        itemRepository.save(createItem(userOne, "item of userOne", "description One"));

        var itemTwo = itemRepository.save(createItem(userTwo, "name", "description of item two"));

        itemRepository.save(createItem(userOne, "name", "description"));

        var items = itemRepository.findByNameOrDescriptionLikeAndIsAvailableTrue("Tem", pageable);

        assertEquals(1, items.size());
        assertEquals(itemTwo.getId(), items.get(0).getId());
        assertEquals(itemTwo.getName(), items.get(0).getName());
        assertEquals(itemTwo.getDescription(), items.get(0).getDescription());
        assertEquals(itemTwo.getAvailable(), items.get(0).getAvailable());
        assertTrue(items.get(0).getAvailable());
        assertEquals(itemTwo.getOwner(), items.get(0).getOwner());
    }

    @Test
    void findByNameOrDescriptionLikeAndIsAvailableTrueReturnItemOneAndItemTwo() {
        var userOne = userRepository.save(createUser());
        var userTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(userOne, "item of userOne", "description One"));
        var itemTwo = itemRepository.save(createItem(userTwo, "name", "description of item two"));
        var pageable = new PageableByOffsetAndSize(0, 4, Sort.unsorted());

        itemRepository.save(createItem(userOne, "name", "description"));

        var items = itemRepository.findByNameOrDescriptionLikeAndIsAvailableTrue("Tem", pageable);

        assertEquals(2, items.size());
        assertEquals(itemOne.getId(), items.get(0).getId());
        assertEquals(itemOne.getName(), items.get(0).getName());
        assertEquals(itemOne.getDescription(), items.get(0).getDescription());
        assertEquals(itemOne.getAvailable(), items.get(0).getAvailable());
        assertTrue(items.get(0).getAvailable());
        assertEquals(itemOne.getOwner(), items.get(0).getOwner());
        assertEquals(itemTwo.getId(), items.get(1).getId());
        assertEquals(itemTwo.getName(), items.get(1).getName());
        assertEquals(itemTwo.getDescription(), items.get(1).getDescription());
        assertEquals(itemTwo.getAvailable(), items.get(1).getAvailable());
        assertTrue(items.get(1).getAvailable());
        assertEquals(itemTwo.getOwner(), items.get(1).getOwner());
    }

    @Test
    void findByNameOrDescriptionLikeAndIsAvailableTrueReturnItemWithAvailableEqualsTrue() {
        var userOne = userRepository.save(createUser());
        var userTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(userOne, "item of userOne", "description One"));
        var itemTwo = createItem(userTwo, "name", "description of item two");
        var pageable = new PageableByOffsetAndSize(0, 4, Sort.unsorted());

        itemTwo.setAvailable(false);
        itemRepository.save(itemTwo);

        itemRepository.save(createItem(userOne, "name", "description"));

        var items = itemRepository.findByNameOrDescriptionLikeAndIsAvailableTrue("Tem", pageable);

        assertEquals(1, items.size());
        assertEquals(itemOne.getId(), items.get(0).getId());
        assertEquals(itemOne.getName(), items.get(0).getName());
        assertEquals(itemOne.getDescription(), items.get(0).getDescription());
        assertEquals(itemOne.getAvailable(), items.get(0).getAvailable());
        assertTrue(items.get(0).getAvailable());
        assertEquals(itemOne.getOwner(), items.get(0).getOwner());
    }

    @Test
    void findAllByOwnerReturnFirstItemOfUserOne() {
        var userOne = userRepository.save(createUser());
        var userTwo = userRepository.save(createUser());
        var pageable = new PageableByOffsetAndSize(0, 1, Sort.unsorted());
        var itemOne = itemRepository.save(createItem(userOne, "item of userOne", "description One"));

        itemRepository.save(createItem(userTwo, "item 2", "description of item 2"));
        itemRepository.save(createItem(userOne, "item 3", "description of item 3"));

        var items = itemRepository.findAllByOwner(userOne, pageable);

        assertEquals(1, items.size());
        assertEquals(itemOne.getId(), items.get(0).getId());
        assertEquals(itemOne.getName(), items.get(0).getName());
        assertEquals(itemOne.getDescription(), items.get(0).getDescription());
        assertEquals(itemOne.getAvailable(), items.get(0).getAvailable());
        assertTrue(items.get(0).getAvailable());
        assertEquals(itemOne.getOwner(), items.get(0).getOwner());
    }

    @Test
    void findAllByOwnerReturnSecondItemOfUserOne() {
        var userOne = userRepository.save(createUser());
        var userTwo = userRepository.save(createUser());
        var pageable = new PageableByOffsetAndSize(1, 1, Sort.unsorted());

        itemRepository.save(createItem(userOne, "item of userOne", "description One"));
        itemRepository.save(createItem(userTwo, "item 2", "description of item 2"));

        var itemTwo = itemRepository.save(createItem(userOne, "item 3", "description of item 3"));

        var items = itemRepository.findAllByOwner(userOne, pageable);

        assertEquals(1, items.size());
        assertEquals(itemTwo.getId(), items.get(0).getId());
        assertEquals(itemTwo.getName(), items.get(0).getName());
        assertEquals(itemTwo.getDescription(), items.get(0).getDescription());
        assertEquals(itemTwo.getAvailable(), items.get(0).getAvailable());
        assertTrue(items.get(0).getAvailable());
        assertEquals(itemTwo.getOwner(), items.get(0).getOwner());
    }

    @Test
    void findAllByOwnerReturnAllItemsOfUserOne() {
        var userOne = userRepository.save(createUser());
        var userTwo = userRepository.save(createUser());
        var pageable = new PageableByOffsetAndSize(0, 4, Sort.unsorted());
        var itemOne = itemRepository.save(createItem(userOne, "item of userOne", "description One"));

        itemRepository.save(createItem(userTwo, "item 2", "description of item 2"));

        var itemTwo = itemRepository.save(createItem(userOne, "item 3", "description of item 3"));
        var items = itemRepository.findAllByOwner(userOne, pageable);

        assertEquals(2, items.size());
        assertEquals(itemOne.getId(), items.get(0).getId());
        assertEquals(itemOne.getName(), items.get(0).getName());
        assertEquals(itemOne.getDescription(), items.get(0).getDescription());
        assertEquals(itemOne.getAvailable(), items.get(0).getAvailable());
        assertTrue(items.get(0).getAvailable());
        assertEquals(itemOne.getOwner(), items.get(0).getOwner());
        assertEquals(itemTwo.getId(), items.get(1).getId());
        assertEquals(itemTwo.getName(), items.get(1).getName());
        assertEquals(itemTwo.getDescription(), items.get(1).getDescription());
        assertEquals(itemTwo.getAvailable(), items.get(1).getAvailable());
        assertTrue(items.get(1).getAvailable());
        assertEquals(itemTwo.getOwner(), items.get(1).getOwner());
    }

    @Test
    void findAllByOwnerReturnAllItemsOfUserOneWhenAvailableIsFalse() {
        var userOne = userRepository.save(createUser());
        var userTwo = userRepository.save(createUser());
        var pageable = new PageableByOffsetAndSize(0, 4, Sort.unsorted());
        var itemOne = itemRepository.save(createItem(userOne, "item of userOne", "description One"));
        var itemTwo = createItem(userOne, "item 3", "description of item 3");

        itemRepository.save(createItem(userTwo, "item 2", "description of item 2"));
        itemTwo.setAvailable(false);
        itemTwo = itemRepository.save(itemTwo);

        var items = itemRepository.findAllByOwner(userOne, pageable);

        assertEquals(2, items.size());
        assertEquals(itemOne.getId(), items.get(0).getId());
        assertEquals(itemOne.getName(), items.get(0).getName());
        assertEquals(itemOne.getDescription(), items.get(0).getDescription());
        assertEquals(itemOne.getAvailable(), items.get(0).getAvailable());
        assertTrue(items.get(0).getAvailable());
        assertEquals(itemOne.getOwner(), items.get(0).getOwner());
        assertEquals(itemTwo.getId(), items.get(1).getId());
        assertEquals(itemTwo.getName(), items.get(1).getName());
        assertEquals(itemTwo.getDescription(), items.get(1).getDescription());
        assertEquals(itemTwo.getAvailable(), items.get(1).getAvailable());
        assertFalse(items.get(1).getAvailable());
        assertEquals(itemTwo.getOwner(), items.get(1).getOwner());
    }

    private long getNextUserId() {
        return nextUserId++;
    }

    private Item createItem(User owner, String name, String description) {
        var item = new Item();

        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private User createUser() {
        var user = new User();
        var userId = getNextUserId();

        user.setName("user " + userId);
        user.setEmail(String.format("user%d@email.ru", userId));
        return user;
    }
}