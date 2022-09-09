package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

class PageableByOffsetAndSizeTest {

    @Test
    void createPageableByOffsetAndSizeThrowsExceptionWhenOffsetIsNegative() {
        var errMsg = "Параметр offset < 0; offset: -1";

        var ex = assertThrows(IllegalArgumentException.class,
                () -> new PageableByOffsetAndSize(-1, 1, Sort.unsorted()));

        assertEquals(errMsg, ex.getMessage());
    }

    @Test
    void createPageableByOffsetAndSizeThrowsExceptionWhenSizeIsZero() {
        var errMsg = "Параметр size <= 0; size: 0";

        var ex = assertThrows(IllegalArgumentException.class,
                () -> new PageableByOffsetAndSize(1, 0, Sort.unsorted()));

        assertEquals(errMsg, ex.getMessage());
    }

    @Test
    void getPageNumber() {
        var pageable = new PageableByOffsetAndSize(1, 2, Sort.unsorted());

        assertEquals(0, pageable.getPageNumber());
    }

    @Test
    void getPageSize() {
        var pageable = new PageableByOffsetAndSize(1, 2, Sort.unsorted());

        assertEquals(2, pageable.getPageSize());
    }

    @Test
    void getOffset() {
        var pageable = new PageableByOffsetAndSize(1, 2, Sort.unsorted());

        assertEquals(1, pageable.getOffset());
    }

    @Test
    void getSort() {
        var sort = Sort.unsorted();
        var pageable = new PageableByOffsetAndSize(1, 2, sort);

        assertEquals(sort, pageable.getSort());
    }

    @Test
    void next() {
        var pageable = new PageableByOffsetAndSize(1, 2, Sort.unsorted());
        var nextPageable = pageable.next();

        assertEquals(pageable.getOffset() + 2, nextPageable.getOffset());
        assertEquals(pageable.getPageSize(), nextPageable.getPageSize());
        assertEquals(pageable.getSort(), nextPageable.getSort());
    }

    @Test
    void previousOrFirstReturnFirstPage() {
        var pageable = new PageableByOffsetAndSize(1, 2, Sort.unsorted());
        var previousPageable = pageable.previousOrFirst();

        assertEquals(0, previousPageable.getOffset());
        assertEquals(pageable.getPageSize(), previousPageable.getPageSize());
        assertEquals(pageable.getSort(), previousPageable.getSort());
    }

    @Test
    void first() {
        var pageable = new PageableByOffsetAndSize(1, 2, Sort.unsorted());
        var firstPage = pageable.first();

        assertEquals(0, firstPage.getOffset());
        assertEquals(pageable.getPageSize(), firstPage.getPageSize());
        assertEquals(pageable.getSort(), firstPage.getSort());
    }

    @Test
    void withPage() {
        var pageable = new PageableByOffsetAndSize(1, 2, Sort.unsorted());
        var newPageable = pageable.withPage(2);

        assertEquals(4, newPageable.getOffset());
        assertEquals(pageable.getPageSize(), newPageable.getPageSize());
        assertEquals(pageable.getSort(), newPageable.getSort());
    }

    @Test
    void hasPrevious() {
        var pageable = new PageableByOffsetAndSize(1, 2, Sort.unsorted());

        assertTrue(pageable.hasPrevious());
    }
}