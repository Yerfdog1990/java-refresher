package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import java.util.LinkedList;
import java.util.Deque;
import org.junit.jupiter.api.Test;


class JavaCollectionsTests {

    @Test
    void givenLinkedList_whenUsingBasicListOperations_thenBehavesLikeArrayList() {
        List<String> fruitBasket = new LinkedList<>();
        
        fruitBasket.add("Apple");
        fruitBasket.add(0, "Banana");

        assertEquals("Banana", fruitBasket.get(0));
        assertEquals(2, fruitBasket.size());
    }

    @Test
    void givenLinkedList_whenUsingAsDeque_thenSupportsDoubleEndedOperations() {
        Deque<String> tasks = new LinkedList<>();
    
        // Deque operations - not available in ArrayList
        tasks.offer("High Priority Task");
        tasks.offerFirst("Urgent Task");
        tasks.offerLast("Low Priority Task");
    
        // Retrieves, but does not remove
        assertEquals("Urgent Task", tasks.peekFirst());
        assertEquals("Low Priority Task", tasks.peekLast());
    
        // Retrieves and removes from both ends
        assertEquals("Urgent Task", tasks.pollFirst());
        assertEquals("Low Priority Task", tasks.pollLast());
    }

    @Test
    void givenLinkedList_whenUsingAsStack_thenSupportsLIFOOperations() {
        Deque<String> browserHistory = new LinkedList<>();
    
        // Stack operations (LIFO)
        browserHistory.push("google.com");
        browserHistory.push("stackoverflow.com");
        browserHistory.push("baeldung.com");
    
        assertEquals("baeldung.com", browserHistory.peek());
        assertEquals("baeldung.com", browserHistory.pop());
        assertEquals("stackoverflow.com", browserHistory.pop());
    }

    @Test
    void givenLinkedList_whenUsingAsQueue_thenSupportsFIFOOperations() {
        Deque<String> printQueue = new LinkedList<>();
    
        // Queue operations (FIFO)
        printQueue.offer("Document1.pdf");
        printQueue.offer("Document2.pdf");
        printQueue.offer("Document3.pdf");
    
        assertEquals("Document1.pdf", printQueue.poll());
        assertEquals("Document2.pdf", printQueue.peek());
    }
}
