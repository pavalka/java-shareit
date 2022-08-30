package ru.practicum.shareit;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@EqualsAndHashCode
public class PageableByOffsetAndSize implements Pageable {
    private final long offset;
    private final int size;
    private final Sort sort;

    public PageableByOffsetAndSize(long offset, int size, @NonNull Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException(String.format("Параметр offset < 0; offset: %d", offset));
        }
        if (size <= 0) {
            throw new IllegalArgumentException(String.format("Параметр size <= 0; size: %d", size));
        }
        this.offset = offset;
        this.size = size;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return (int) offset / size;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new PageableByOffsetAndSize(offset + size, size, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        var previousOffset = offset - size;

        if (previousOffset < 0) {
            return first();
        }
        return new PageableByOffsetAndSize(previousOffset, size, sort);
    }

    @Override
    public Pageable first() {
        return new PageableByOffsetAndSize(0, size, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new PageableByOffsetAndSize(pageNumber * size, size, sort);
    }

    @Override
    public boolean hasPrevious() {
        return offset > 0;
    }
}
