
---

### **Section 3.3: A Nested Loop**

The following function checks if an array has any duplicates by taking each element, then iterating over the whole array to see if the element is there:

```java
public static boolean containsDuplicates(int[] array) {
    for (int i = 0; i < array.length - 1; i++) {
        for (int j = 0; j < array.length; j++) {
            if (i != j && array[i] == array[j]) {
                return true;
            }
        }
    }
    return false;
}
```

The inner loop performs at each iteration a number of operations that is constant with n. The outer loop also does a few constant operations, and runs the inner loop n times. The outer loop itself is run n times. So the operations inside the inner loop are run n² times, the operations in the outer loop are run n times, and the assignment to i is done one time. Thus, the complexity will be something like **a·n² + b·n + c**, and since the highest term is n², the O notation is **O(n²)**.

As you may have noticed, we can improve the algorithm by avoiding doing the same comparisons multiple times. We can start from **i + 1**. This allows us to drop the `i == j` check.

```java
public static boolean fasterContainsDuplicates(int[] array) {
    for (int i = 0; i < array.length - 1; i++) {
        for (int j = i + 1; j < array.length; j++) {
            if (array[i] == array[j]) {
                return true;
            }
        }
    }
    return false;
}
```

Obviously, this second version does fewer operations and so is more efficient. How does that translate to Big-O notation? Well, now the inner loop body is run:

[
1 + 2 + ... + (n - 1) = (n × (n - 1)) / 2
]

times. This is still a polynomial of the second degree, and so is still only **O(n²)**. We have clearly lowered the complexity since we roughly divided by 2 the number of operations that we are doing, but we are still in the same complexity class as defined by Big-O. In order to lower the complexity to a lower class we would need to divide the number of operations by something that tends to infinity with n.

---

