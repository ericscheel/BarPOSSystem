package com.barpos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    @Test
    void testProductNameAndPrice() {
        Product p = new Product("Cola", 2.5);
        assertEquals("Cola", p.getName());
        assertEquals(2.5, p.getPrice());
    }
}
