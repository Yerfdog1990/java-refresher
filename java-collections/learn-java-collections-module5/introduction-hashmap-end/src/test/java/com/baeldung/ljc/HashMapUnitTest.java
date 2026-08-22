package com.baeldung.ljc;

import com.baeldung.ljc.domain.model.Task;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class HashMapUnitTest {

    @Test
    void givenDefaultHashMapConstructor_whenInitializing_thenMapCreated() {
        
        HashMap<String, Integer> map = new HashMap<>();
      
        assertNotNull(map);
        assertEquals(0, map.size());
    }

    @Test
    void givenHashMapConstructorForInitialCapacity_whenInitializing_thenMapCreated() {
        
        HashMap<String, Integer> map = new HashMap<>(32);
      
        assertNotNull(map);
        assertEquals(0, map.size());
    }

    @Test
    void givenHashMapConstructorForInitialCapacityAndLoadFactor_whenInitializing_thenMapCreated() {
    
        HashMap<String, Integer> map = new HashMap<>(64, 0.8f);
        
        assertNotNull(map);
        assertEquals(0, map.size());
    }

    @Test
    void givenHashMapConstructorForMapInstance_whenInitializing_thenMapCreated() {
        
        Map<String, String> originalMap = new HashMap<>();
        HashMap<String, String> copiedMap = new HashMap<>(originalMap);
        
        assertEquals(originalMap.size(), copiedMap.size());
    }

    @Test
    void givenACollectionOfKeyValuePairs_whenUsingMapOf_thenMapCreated() {
        Map<String, Integer> fruitMap = Map.of(
            "apple", 1,
            "banana", 2,
            "orange", 3
        );

        assertEquals(3, fruitMap.size());
    }

    @Test
    void givenACollectionOfKeyValuePairs_whenUsingMapOfEntries_thenMapCreated() {
        Map<String, String> nameMap = Map.ofEntries(
          Map.entry("firstName", "John"),
          Map.entry("lastName", "Doe")
        );

        assertEquals(2, nameMap.size());
    }

    @Test
    void givenTaskEntity_whenUsingTaskAsValue_thenMapCreated() {

        Task task1 = new Task("T001", "Complete Report", "Finish quarterly sales report", LocalDate.of(2025, 7, 15));
        Task task2 = new Task("T002", "Schedule Meeting", "Arrange team sync-up", LocalDate.of(2025, 7, 10));
        Task task3 = new Task("T003", "Review Code", "Review pull requests for Project X", LocalDate.of(2025, 7, 12));

        Map<String, Task> taskMap = Map.ofEntries(
          Map.entry(task1.getCode(), task1),
          Map.entry(task2.getCode(), task2),
          Map.entry(task3.getCode(), task3)  
        );
        
        assertEquals(3, taskMap.size());
    }
}
