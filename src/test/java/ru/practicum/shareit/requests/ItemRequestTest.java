package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    @Test
    void testHashCode() {
        var requestOne = createRequest(1);
        var requestTwo = createRequest(1);

        assertEquals(requestOne.hashCode(), requestTwo.hashCode());
    }

    @Test
    void testEqualsReturnTrueWhenObjIsThis() {
        var request = createRequest(1);

        assertEquals(request, request);
    }

    @Test
    void testEqualsReturnFalseWhenObjIsNull() {
        var request = createRequest(1);

        assertFalse(request.equals(null));
    }

    @Test
    void testEqualsReturnFalseWhenRequestIdIsNull() {
        var requestOne = createRequest(1);
        var requestTwo = createRequest(1);

        requestOne.setId(null);
        assertNotEquals(requestTwo, requestOne);
    }

    @Test
    void testEqualsReturnFalseWhenRequestIdsIsDifferent() {
        var requestOne = createRequest(1);
        var requestTwo = createRequest(2);

        assertNotEquals(requestTwo, requestOne);
    }

    @Test
    void testEqualsReturnFalseWhenObjIsNotRequest() {
        var requestOne = createRequest(1);
        var other = new ArrayList<Long>();

        assertNotEquals(requestOne, other);
    }

    private ItemRequest createRequest(long requestId) {
        var request = new ItemRequest();

        request.setId(requestId);
        request.setDescription("description " + requestId);
        return request;
    }
}