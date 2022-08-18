package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemDao extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE (LOWER(i.name) LIKE CONCAT('%', LOWER(:text), '%') OR LOWER(i.description) LIKE CONCAT('%', LOWER(:text), '%')) " +
            "AND i.available=TRUE")
    List<Item> findByNameOrDescriptionLikeAndIsAvailableTrue(@Param("text") String text);
}
