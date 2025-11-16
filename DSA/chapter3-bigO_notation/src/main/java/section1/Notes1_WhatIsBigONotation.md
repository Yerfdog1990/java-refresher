
---

# **3.1: Big-O Notation**

Big-O notation is one of the most important tools in algorithm analysis. It helps us understand how fast a function grows, especially when the input size becomes very large. In algorithm analysis, Big-O describes **how the running time or memory usage of an algorithm increases as the input size (n) increases.**

---

## ✅ **1. What Is Big-O? (Simple Definition)**

Big-O notation gives an **upper bound** on the growth of a function.
It answers the question:

> “How fast does this algorithm grow in the worst case as n becomes very large?”

We compare two functions—say **f(n)** and **g(n)**—and say:

```
f(n) = O(g(n))
```

This means:

* **f(n) grows no faster than g(n)** (up to a constant factor)
* For sufficiently large n,
  `f(n) ≤ A * g(n)` for some constant `A > 0`

It is a way of saying:
**“f(n) is at most as big as g(n), ignoring constant factors.”**

---

## ✅ **2. Formal Definition (Beginner-Friendly)**

We say:

```
f(n) = O(g(n))
```

if:

* f(n) and g(n) are positive functions
* There exist constants **A > 0** and **n₀ > 0**
* Such that for all `n ≥ n₀`:

```
f(n) ≤ A * g(n)
```

This means **the ratio f(n)/g(n) is bounded**.

In plain words:

> If f(n) grows no faster than some constant times g(n), then f(n) is O(g(n)).

---

## ✅ **3. Example: Proving f(n) = O(n²)**

Let:

```
f(n) = 100n² + 10n + 1
g(n) = n²
```

Compute:

```
f(n)/g(n) = (100n² + 10n + 1) / n²
          = 100 + 10/n + 1/n²
```

Observations:

* `10/n` gets smaller as n increases
* `1/n²` also gets smaller

For all `n ≥ 1`, we have:

```
10/n ≤ 10
1/n² ≤ 1
```

So:

```
f(n)/g(n) ≤ 100 + 10 + 1 = 111
```

This satisfies the Big-O condition:

```
f(n) ≤ 111 * n²
```

Therefore:

```
f(n) = O(n²)
```

---

## ✅ **4. What Does This Mean?**

This does **NOT** mean the two functions are identical.

It means:

* f(n) **grows at most quadratically**
* As n becomes large, the dominating term is `100n²`
* Lower-order terms like `10n` and `1` become insignificant

Even though f(n) has extra terms, it still belongs to the same *growth class* as n².

---

## ❗ A common beginner confusion

You may think:
“Wait, f(n) grows **100 times faster**, how is that considered similar?”

Because in asymptotic notation:

> **Constant factors do not matter.**

We only care about the growth *trend*, not the exact values.

---

## 🚫 When Big-O Does NOT Hold

Let:

```
h(n) = n² log n
g(n) = n²
```

Compute:

```
h(n)/g(n) = log n
```

As n → ∞:

```
log n → ∞
```

This ratio is **not bounded**, therefore:

```
h(n) ≠ O(n²)
```

Because `n² log n` grows **faster** than `n²`.

---

## ✅ **5. Important Property**

If:

```
f(n) = O(g(n))
and
g(n) = O(h(n))
```

Then:

```
f(n) = O(h(n))
```

This is called **transitivity**.

Example:

```
n² = O(n³)
n³ = O(n⁴)
→ n² = O(n⁴)
```

---

## ⭐ Big-O vs Big-Theta (for clarity)

In algorithm discussions, people sometimes say:

> “The algorithm is O(n²)”

when they really mean:

> “The algorithm grows *like* n²,”
> which is actually **Θ(n²)** (tight bound)

But Big-O is often used informally.

---

## ✅ **6. How Big-O Is Used in Algorithm Analysis**

Big-O is used to classify:

### **Time Complexity**

How many operations an algorithm performs.

Example:

* A loop from 1 to n → O(n)
* A nested loop → O(n²)
* Binary search → O(log n)

### **Space Complexity**

How much memory an algorithm needs.

Example:

* Using an array of size n → O(n)
* Using only a few variables → O(1)

---

## ⭐ Typical Use in Practice

When we say “an O(n²) algorithm,” we mean:

* It performs at most a quadratic number of operations.
* Its growth is similar to n² (up to a constant factor).
* For large n, lower-order terms don’t matter.

---

## 🧠 Quick Notes for Beginners

* Faster growth → **slower algorithm**
* O(n) is **better** (faster) than O(n²)
* Constant factors (like 100n²) do not matter
* Always analyze the **dominant term**

---

## 📌 Summary

Big-O notation helps us:

* Understand algorithm efficiency
* Compare different algorithms
* Ignore constant factors and small details
* Focus on long-term performance behavior

**The goal is to classify algorithms into broad speed groups (constant, linear, quadratic, etc.).**

---


