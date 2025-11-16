
---

# **Section 3.4: O(log n) Types of Algorithms**

Let's say we have a problem of size `n`. For each step of our algorithm, the original problem becomes half of its previous size (`n/2`).

So at each step, our problem becomes half.

**Step | Problem Size**
1 → `n/2`
2 → `n/4`
3 → `n/8`
4 → `n/16`

When the problem space is reduced (i.e., solved completely), it cannot be reduced any further. The size becomes `1` at the final step.

---

## **Deriving the logarithmic complexity**

1. At the k-th step:
   `problem_size = 1`

2. But we know:
   `problem_size = n / 2^k`

3. Therefore:
   `n / 2^k = 1`
   which gives:
   `n = 2^k`

4. Take log on both sides:
   `log(n) = k * log(2)`
   so
   `k = log(n) / log(2)`

5. Using the identity `log_x(m) / log_x(n) = log_n(m)`
   we get:
   `k = log_2(n)`
   or simply
   `k = log n`

Thus the algorithm runs a maximum of `log n` steps, giving complexity:

```
O(log n)
```

---

## **Example loop that runs in O(log n)**

```java
for (int i = 1; i <= n; i = i * 2) {
    // perform some operation
}
```

If someone asks you: *If n = 256, how many steps does this loop run?*

```
k = log2(256)
k = log2(2^8)   (because log_a(a) = 1)
k = 8
```

---

## **Binary Search (another O(log n) example)**

```java
public static int bSearch(int[] arr, int size, int item) {
    int low = 0;
    int high = size - 1;

    while (low <= high) {
        int mid = low + (high - low) / 2;

        if (arr[mid] == item)
            return mid;
        else if (arr[mid] < item)
            low = mid + 1;
        else
            high = mid - 1;
    }
    return -1; // Unsuccessful result
}
```

---

