
---

# **Section 2.1: Big-Theta (Θ) Notation**

## **1. Introduction**

When analyzing algorithms, we often want to describe how their running time grows as the input size increases.
There are several asymptotic notations for this purpose, such as **Big-O**, **Big-Omega**, and **Big-Theta**.

* **Big-O notation** gives an **upper bound**.
* **Big-Omega notation** gives a **lower bound**.
* **Big-Theta notation** gives a **tight bound**, meaning both an upper and a lower bound at the same time.

Big-Theta is therefore the most precise of the three, and it gives us the true growth rate of a function.

---

## **2. Intuition Behind Big-Theta**

Two functions ( f(x) ) and ( g(x) ) satisfy:

```
f(x) = Θ(g(x))
```

if they grow **at the same rate** as ( x ) becomes large.

Another way to say this:

> When graphed, ( f(x) ) and ( g(x) ) eventually behave similarly, staying within constant multiples of one another.

Big-Theta is **symmetric**, meaning:

```
f(x) = Θ(g(x))  <=>  g(x) = Θ(f(x))
```

---

## **3. Formal Mathematical Definition**

Big-Theta represents a **set of functions**.
The formal definition is:

```
Θ(g(x)) = { f(x) : there exist positive constants c1, c2, and N
            such that 0 ≤ c1*g(x) ≤ f(x) ≤ c2*g(x) for all x ≥ N }
```

In words:

* After some value ( N ),
* ( f(x) ) is **always** at least a constant multiple of ( g(x) ),
* and **never more** than another constant multiple of ( g(x) ).

Even though Θ(g(x)) is formally a set, we commonly write:

```
f(x) = Θ(g(x))
```

to mean “f(x) belongs to the set Θ(g(x)).”

---

## **4. Equivalent Definition Using Limits**

When limits exist, Big-Theta can be determined by evaluating:

```
lim (x → ∞) f(x) / g(x) = c
```

If:

* the limit exists, and
* ( c ) is a positive finite constant (i.e., ( 0 < c < ∞ ) ),

then:

```
f(x) = Θ(g(x))
```

This method is often the quickest way to compare two functions.

---

## **5. Relationship to Big-O and Big-Ω**

Because Θ bounds a function from **both** sides, we can express it using O and Ω:

```
f(x) = Θ(g(x))  <=>  f(x) = O(g(x)) and f(x) = Ω(g(x))
```

Thus Big-Theta is the intersection of upper and lower bound constraints.

---

## **6. Worked Example**

Suppose an algorithm takes the following number of operations for input size ( n ):

```
T(n) = 42n^2 + 25n + 4
```

### **Big-O Observation**

We can say:

```
T(n) = O(n^2)
```

but also:

```
T(n) = O(n^3)
T(n) = O(n^100)
```

because Big-O only provides an **upper bound**, even if that bound is loose.

### **Big-Theta Observation**

However:

```
T(n) = Θ(n^2)
```

and the function is **not** in:

```
Θ(n^3)
Θ(n^4)
Θ(n^10)
```

These would grow strictly faster than the given function.

Thus Big-Theta describes the **true** asymptotic growth behaviour.

---

## **7. Using Big-Theta in Recurrence Relations**

Big-Theta clauses often appear inside recurrences to hide irrelevant details.

Example:

```
T(n) = T(n/2) + Θ(n)
```

This means that inside the recurrence, the additive term is some function:

```
f(n) ∈ Θ(n)
```

We do not care about its exact formula—only that it grows linearly.

---

## **8. Common Complexity Classes**

Here is a table showing several popular Θ-classes and how fast they grow.

| Complexity Class | Notation   | Example for n = 30 | Example for n = 100 |
| ---------------- | ---------- | ------------------ | ------------------- |
| Linearithmic     | Θ(n log n) | 30 log(30) ≈ 147   | 100 log(100) ≈ 664  |
| Quadratic        | Θ(n²)      | 30² = 900          | 100² = 10,000       |
| Exponential      | Θ(2ⁿ)      | 2³⁰ ≈ 1.07×10⁹     | 2¹⁰⁰ ≈ 1.27×10³⁰    |
| Factorial        | Θ(n!)      | 30! ≈ 2.65×10³²    | 100! ≈ 9.33×10¹⁵⁷   |

These illustrate how dramatically runtime increases when moving up the asymptotic hierarchy.

---

## **9. Summary**

* **Big-Theta (Θ)** gives the **tight bound** on algorithm complexity.
* It describes functions that grow at **asymptotically the same rate**.
* It is symmetric and more precise than Big-O alone.
* A function belongs to Θ(g(x)) if it is bounded above and below by constant multiples of g(x) for sufficiently large x.
* Limits provide a convenient method for verifying Θ relationships.
* Θ(g(x)) = O(g(x)) ∩ Ω(g(x))

Big-Theta is essential for accurately characterizing the true performance of an algorithm as inputs grow large.

---
