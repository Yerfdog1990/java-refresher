
---

# **Section 3.4: An O(log n) Example**

## **Introduction**

Consider the following problem:

L is a **sorted list** containing `n` signed integers (with `n` large), for example:

```
[-5, -2, -1, 0, 1, 2, 4]
```

If L is known to contain the integer `0`, how can you find the **index** of `0`?

---

## **Naïve Approach**

A simple approach is to read every element one by one until `0` is found.
In the worst case, this takes `n` operations, giving the complexity:

```
O(n)
```

This is fine for small `n`, but is there a more efficient way?

---

## **Dichotomy (Binary Search Idea)**

Consider the following algorithm (in Java):

```java
public static int findZero(int[] L, int n) {
    int a = 0;
    int b = n - 1;

    while (true) {
        int h = (a + b) / 2;

        if (L[h] == 0) {
            return h;
        } else if (L[h] > 0) {
            b = h;
        } else if (L[h] < 0) {
            a = h;
        }
    }
}
```

Here:

* `a` and `b` represent the range in which `0` must be located.
* Each iteration selects the midpoint `h`.
* The search interval is reduced to **half** of its previous size.

### **Worst-case complexity**

We continue until `a` and `b` meet.
This does **not** take `n` steps because the interval is divided by 2 each iteration.

Thus the complexity is:

```
O(log n)
```

---

## **Explanation**

In this section, `log` means the **binary logarithm** (`log base 2`, written `log_2`).
Since:

```
O(log_2 n) = O(log n)
```

we simply write `log`.

Let `x` be the number of operations.
Each step divides the problem size by 2.

We know:

```
1 = n / (2^x)
```

Therefore:

```
2^x = n
```

Taking logarithms:

```
x = log n
```

So the number of operations grows logarithmically.

---

## **Conclusion**

Whenever a problem is **repeatedly divided** by a constant factor (such as dividing by 2 in binary search), its complexity is:

```
O(log n)
```

---

