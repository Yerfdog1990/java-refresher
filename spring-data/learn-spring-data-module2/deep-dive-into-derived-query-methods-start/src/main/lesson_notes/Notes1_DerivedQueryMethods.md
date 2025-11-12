
---

# **Derived Query Methods in Spring Data JPA**

---

## **1. Introduction**

In the previous lesson, we explored **Spring Data JPA repositories** and their built-in CRUD methods. While those methods make basic data operations effortless, they are limited when you need to query by non-ID fields.

This is where **Derived Query Methods** come in. They let you perform complex queries without manually writing JPQL or SQL — Spring derives them directly from the **method name**.

---

## **2. What is a Derived Query Method?**

Derived query methods are custom method signatures defined in repository interfaces such as `JpaRepository` or `CrudRepository`.

Spring Data JPA parses the **method name** and automatically builds the **JPQL query** behind the scenes. This means you can write:

```java
List<Plant> findByName(String name);
```

…instead of manually defining:

```java
@Query("SELECT p FROM Plant p WHERE p.name = :name")
```

Spring’s ability to **“derive”** the query from the method name eliminates the need for repetitive query definitions.

---

## **3. Example Entities**

```java
@Entity
@Table(name = "plants")
public class Plant {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private SoilType favoriteSoilType;
    private String sunType;
    @Column(nullable = false)
    private boolean fruitBearing;
    private Integer numDaysTillRipeFruit;
}
```

```java
@Entity
@Table(name = "soil_types")
public class SoilType {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private long ph;
    @Column(nullable = false)
    private boolean dry;
}
```

---

## **4. Repository Setup**

```java
@Repository
public interface PlantRepo extends JpaRepository<Plant, Long> {
    // Derived query methods go here
}
```

---

## **5. Structure of a Derived Query Method**

Each derived query method has two parts:

* **Introducer:** `find`, `get`, `read`, `query`, `count`
* **Criteria:** conditions after the `By` keyword

Example:

```java
List<Plant> findByName(String name);
```

**Equivalent JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.name = :name
```

Introducers like `find`, `get`, and `read` are functionally identical.

Counting variant:

```java
int countByName(String name);
```

**JPQL:**

```jpql
SELECT COUNT(p) FROM Plant p WHERE p.name = :name
```

---

## **6. Derived Query Criteria**

### **A. Equality Keywords**

Equality keywords define whether a property matches or doesn’t match a value.

```java
List<Plant> findByNameIs(String name);
List<Plant> findByName(String name);
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.name = :name
```

```java
List<Plant> findByNameIsNot(String name);
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.name <> :name
```

**Null Checks**

```java
List<Plant> findBySunTypeIsNull();
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.sunType IS NULL
```

```java
List<Plant> findBySunTypeIsNotNull();
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.sunType IS NOT NULL
```

**Boolean Checks**

```java
List<Plant> findByFruitBearingIsTrue();
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.fruitBearing = TRUE
```

```java
List<Plant> findByFruitBearingIsFalse();
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.fruitBearing = FALSE
```

---

### **B. Similarity Keywords**

Similarity keywords perform **pattern matching** using `LIKE`.

```java
List<Plant> findByNameStartingWith(String namePrefix);
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.name LIKE CONCAT(:namePrefix, '%')
```

```java
List<Plant> findByNameEndingWith(String nameSuffix);
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.name LIKE CONCAT('%', :nameSuffix)
```

```java
List<Plant> findByNameContaining(String pattern);
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.name LIKE CONCAT('%', :pattern, '%')
```

---

### **C. Comparison Keywords**

Used to compare numeric or comparable values.

```java
List<Plant> findByNumDaysTillRipeFruitLessThan(int numDays);
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.numDaysTillRipeFruit < :numDays
```

```java
List<Plant> findByNumDaysTillRipeFruitGreaterThanEqual(int numDays);
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.numDaysTillRipeFruit >= :numDays
```

---

### **D. Nested Property Character**

Used to query related entities using an underscore (`_`).

```java
List<Plant> findByFavoriteSoilType_Name(String soilTypeName);
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.favoriteSoilType.name = :soilTypeName
```

```java
List<Plant> findByFavoriteSoilType_PhLessThan(long ph);
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.favoriteSoilType.ph < :ph
```

```java
List<Plant> findByFavoriteSoilType_DryIsTrue();
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.favoriteSoilType.dry = TRUE
```

---

### **E. Limiting Keywords**

Used to limit results with `Top` or `First`.

```java
Plant findFirstByFavoriteSoilType_DryIsTrue();
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.favoriteSoilType.dry = TRUE
```

*(Spring adds `LIMIT 1` when translating to SQL)*

```java
List<Plant> findFirst5ByFavoriteSoilType_PhLessThan(long ph);
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.favoriteSoilType.ph < :ph
```

*(Spring adds `LIMIT 5` in SQL translation)*

---

### **F. Distinct Keyword**

Ensures unique results.

```java
List<Plant> findDistinctByFavoriteSoilType_PhLessThan(long ph);
```

**JPQL:**

```jpql
SELECT DISTINCT p FROM Plant p WHERE p.favoriteSoilType.ph < :ph
```

---

### **G. Sorting and Pagination Support**

You can add sorting and paging directly to derived methods.

```java
List<Plant> findByNameContaining(String pattern, Pageable pageable);
```

**JPQL:**

```jpql
SELECT p FROM Plant p WHERE p.name LIKE CONCAT('%', :pattern, '%')
```

*(Pagination applied automatically via `Pageable`)*

```java
List<Plant> findTop5DistinctByFruitBearingIsTrue(Sort sort);
```

**JPQL:**

```jpql
SELECT DISTINCT p FROM Plant p WHERE p.fruitBearing = TRUE
```

*(Sorting applied automatically)*

---

### **H. Multiple Condition Keywords**

Use `And` and `Or` to chain multiple conditions.

```java
List<Plant> findByNameContainingAndFavoriteSoilType_Name(
        String partialPlantName, String soilTypeName);
```

**JPQL:**

```jpql
SELECT p FROM Plant p
WHERE p.name LIKE CONCAT('%', :partialPlantName, '%')
AND p.favoriteSoilType.name = :soilTypeName
```

```java
List<Plant> findByNameContainingOrFruitBearingIsTrue(String pattern);
```

**JPQL:**

```jpql
SELECT p FROM Plant p
WHERE p.name LIKE CONCAT('%', :pattern, '%')
OR p.fruitBearing = TRUE
```

---

## **7. Summary**

| Keyword Type        | Example Method                                      | Equivalent JPQL                                                              |
| ------------------- | --------------------------------------------------- | ---------------------------------------------------------------------------- |
| Equality            | `findByName("Rose")`                                | `SELECT p FROM Plant p WHERE p.name = :name`                                 |
| Similarity          | `findByNameContaining("Rose")`                      | `SELECT p FROM Plant p WHERE p.name LIKE '%Rose%'`                           |
| Comparison          | `findByNumDaysTillRipeFruitLessThan(10)`            | `SELECT p FROM Plant p WHERE p.numDaysTillRipeFruit < 10`                    |
| Nested              | `findByFavoriteSoilType_Name("Loam")`               | `SELECT p FROM Plant p WHERE p.favoriteSoilType.name = 'Loam'`               |
| Limiting            | `findFirst5ByFavoriteSoilType_PhLessThan(7)`        | `SELECT p FROM Plant p WHERE p.favoriteSoilType.ph < 7`                      |
| Distinct            | `findDistinctByFavoriteSoilType_DryIsTrue()`        | `SELECT DISTINCT p FROM Plant p WHERE p.favoriteSoilType.dry = TRUE`         |
| Multiple Conditions | `findByNameContainingAndFruitBearingIsTrue("Rose")` | `SELECT p FROM Plant p WHERE p.name LIKE '%Rose%' AND p.fruitBearing = TRUE` |

---

## **8. Key Takeaways**

- ✅ **Spring Data JPA derives JPQL automatically** from method names.
- ✅ You can express equality, similarity, comparison, nested properties, limits, and distinctness effortlessly.
- ✅ **Sorting** and **pagination** integrate seamlessly.
- ✅ Complex queries with **AND/OR** are simple to express without writing JPQL yourself.

---

