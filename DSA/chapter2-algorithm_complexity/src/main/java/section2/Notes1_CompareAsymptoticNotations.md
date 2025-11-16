
---

# **Section 2.2: Comparison of Asymptotic Notations**

Asymptotic notations in algorithms are **mathematical tools** used to describe how the performance of an algorithm changes as the input size ( n ) becomes very large.
They don’t give exact runtime in milliseconds; instead, they focus on **growth rate**, which helps us compare algorithms in a way that ignores machine differences.

---

# **1. Why We Use Asymptotic Notations**

They help programmers:

### **1. Analyze algorithm efficiency**

Understand whether an algorithm is fast or slow as input increases.

### **2. Compare different algorithms**

For example, if Algorithm A is ( O(n) ) and Algorithm B is ( O(n^2) ), A will always outperform B on large inputs.

### **3. Predict performance for large datasets**

Very important in data structures, algorithms, system design.

### **4. Ignore machine-specific details**

CPU speed, compiler optimization, RAM size—none of these matter with asymptotic analysis.

---

# **2. The Three Primary Asymptotic Notations**

The three most important notations are **Big-O**, **Big-Omega**, and **Big-Theta**.
These notations compare two functions: the algorithm’s runtime ( f(n) ) and a reference growth rate ( g(n) ).

---

## **2.1 Big-O (Upper Bound)**

```
f(n) = O(g(n))
```

Meaning:

> “f(n) does not grow faster than g(n) (up to constant factors).”

This gives the worst-case growth rate.

Example:

```
7n + 10 = O(n²)
```

because ( n^2 ) eventually grows faster than a linear function.

### **Formal definition**

```
f(n) = O(g(n))
if ∃ c > 0 and ∃ n0 > 0 such that for all n ≥ n0:
0 ≤ f(n) ≤ c * g(n)
```

---

## **2.2 Big-Ω (Lower Bound)**

```
f(n) = Ω(g(n))
```

Meaning:

> “f(n) grows at least as fast as g(n).”

This is the best-case growth rate (or minimum work needed).

Example:

```
n³ - 34 = Ω(10n² - 7n + 1)
```

### **Formal definition**

```
f(n) = Ω(g(n))
if ∃ c > 0 and ∃ n0 > 0 such that for all n ≥ n0:
0 ≤ c * g(n) ≤ f(n)
```

---

## **2.3 Big-Θ (Tight Bound)**

```
f(n) = Θ(g(n))
```

Meaning:

> “f(n) grows at the same rate as g(n).
> Both upper and lower bounds match (up to constants).”

Example:

```
(1/2)n² - 7n = Θ(n²)
```

### **Formal definition**

```
f(n) = Θ(g(n))
if ∃ c1, c2 > 0 and ∃ n0 > 0 such that for all n ≥ n0:
0 ≤ c1 * g(n) ≤ f(n) ≤ c2 * g(n)
```

---

# **3. The Two Secondary Notations: Little-o and Little-ω**

These are stricter versions of Big-O and Big-Ω.

---

## **3.1 Little-o (Strictly Slower)**

```
f(n) = o(g(n))
```

Meaning:

> “f(n) grows strictly slower than g(n).”

Example:

```
5n² = o(n³)
```

### **Formal definition**

```
f(n) = o(g(n))
if for every c > 0, ∃ n0 > 0 such that for all n ≥ n0:
0 ≤ f(n) < c * g(n)
```

---

## **3.2 Little-ω (Strictly Faster)**

```
f(n) = ω(g(n))
```

Meaning:

> “f(n) grows strictly faster than g(n).”

Example:

```
7n² = ω(n)
```

### **Formal definition**

```
f(n) = ω(g(n))
if for every c > 0, ∃ n0 > 0 such that for all n ≥ n0:
f(n) > c * g(n)
```

---

# **4. Easy Analogy Using Numbers**

Think of it like comparing two numbers ( a ) and ( b ):

| Notation         | Analogy | Meaning                       |
| ---------------- | ------- | ----------------------------- |
| `f(n) = O(g(n))` | `a ≤ b` | f grows no faster than g      |
| `f(n) = Ω(g(n))` | `a ≥ b` | f grows at least as fast as g |
| `f(n) = Θ(g(n))` | `a = b` | f and g grow equally fast     |
| `f(n) = o(g(n))` | `a < b` | f grows strictly slower       |
| `f(n) = ω(g(n))` | `a > b` | f grows strictly faster       |

---

# **5. Why These Notations Matter**

### ✔ They help us focus on growth rate

We ignore tiny differences like +1 or ×2 because they don’t matter at scale.

### ✔ They let us compare algorithms fairly

An algorithm with:

* ( O(n) ) will always beat ( O(n^2) ) on large inputs.

### ✔ They are machine-independent

A slow laptop and a fast server both agree that:

* ( O(n \log n) ) grows slower than ( O(n^2) ).

### ✔ They help identify performance bottlenecks

Especially important when datasets grow into the millions.

### ✔ They are essential for writing scalable code

High-performance systems depend on good asymptotic behavior.

---

# **6. Common Complexity Classes (Fast → Slow)**

Below are the most common complexity classes, from best to worst:

```
O(1)        Constant time
O(log n)    Logarithmic
O(n)        Linear
O(n log n)  Linearithmic
O(n²)       Quadratic
O(n³)       Cubic
O(2ⁿ)       Exponential
O(n!)       Factorial
```

As input size grows:

* ( O(1) ) barely changes
* ( O(n) ) grows steadily
* ( O(n^2) ) becomes slow fast
* ( O(2^n) ) and ( O(n!) ) become impossible to run for large n

---

# **7. Key Properties to Remember**

### ✔ **Drop constants**

```
O(2n) → O(n)
```

### ✔ **Keep only the dominant term**

```
O(n² + n) → O(n²)
```

### ✔ **Asymptotics care about large n**

Small inputs don’t matter to the notation.

### ✔ **Θ gives the most accurate description**

Because it includes both upper and lower bounds.

---

# **8. Summary for Absolute Beginners**

* **Big-O** = How fast the algorithm *can* grow (upper bound).
* **Big-Ω** = How fast it *must* grow (lower bound).
* **Big-Θ** = How fast it *actually* grows (tight bound).
* **Little-o / ω** = Strict versions of Big-O / Big-Ω.
* These notations help compare algorithms without running them.

---

# 📘 **Asymptotic Notations – Ultimate Cheat Sheet**

## 🔹 **What Are Asymptotic Notations?**

Asymptotic notations describe **how the performance of an algorithm grows** with input size **n**.
They ignore hardware differences and focus only on growth rate.

---

# 📌 **1. CHEAT SHEET**

![Screenshot 2568-11-16 at 11.47.58.png](Screenshot%202568-11-16%20at%2011.47.58.png)

## **Big-O Notation — O(f(n))**

* Describes **upper bound** (worst-case)
* The algorithm will **not grow faster** than this.
* Example:
  `O(n^2)` → runtime grows at most quadratically.

---

## **Big-Omega — Ω(f(n))**

* Describes **lower bound** (best-case)
* The algorithm takes **at least** this much time.
* Example:
  `Ω(n log n)` → minimum time is proportional to `n log n`.

---

## **Big-Theta — Θ(f(n))**

* **Tight bound** (both upper & lower)
* Exact growth rate.
* Example:
  `Θ(n)` → runtime is linear in all cases.

---

## **Little-o — o(f(n))**

* **Strictly smaller order** than f(n)
* Algorithm grows *slower* than f(n)
* Example:
  `o(n^2)` → grows slower than quadratic, not equal.

---

## **Little-omega — ω(f(n))**

* **Strictly greater order** than f(n)
* Algorithm grows *faster* than f(n)
* Example:
  `ω(n)` → grows faster than linear.

---

# 📌 **2. DIAGRAM OF NOTATIONS (ASCII)**

### 👉 Relationship Between Notations

```
           STRICTLY SMALLER            EXACT MATCH             STRICTLY GREATER
    --------------------------------------------------------------------------------
    o(f(n))        O(f(n))       Θ(f(n))        Ω(f(n))        ω(f(n))    
```

### 👉 Hierarchy of Common Growth Rates

```
O(1)  →  O(log n)  →  O(n)  →  O(n log n)  →  O(n^2)  →  O(2^n)  →  O(n!)
```

### 👉 Visual Growth Curve (conceptual)

```
|                     O(n!)
|                  .
|                .
|            .  
|         .    
|      .
|   .           
| .     O(2^n)
|.  
|      . O(n^2)
|      . 
|      .       O(n log n)
|      .    .
|      .  .
|      ..        O(n)
|      .     O(log n)
|_______._______________________________________ n
        O(1)
```
Here is the scanned table recreated **cleanly in Markdown**, ready to copy and paste:

---

# **Common Complexity Classes**
```markdown
| Name         | Notation      | n = 10        | n = 100               |
|--------------|---------------|---------------|-----------------------|
| Constant     | Θ(1)          | 1             | 1                     |
| Logarithmic  | Θ(log(n))     | 3             | 7                     |
| Linear       | Θ(n)          | 10            | 100                   |
| Linearithmic | Θ(n * log(n)) | 30            | 700                   |
| Quadratic    | Θ(n^2)        | 100           | 10 000                |
| Exponential  | Θ(2^n)        | 1 024         | 1.267650e+30          |
| Factorial    | Θ(n!)         | 3 628 800     | 9.332622e+157         |
```
---
# 📌 **3. MEMORY TRICK (SUPER EASY)**

### 🧠 **"SLOTH" — Think of a slow animal climbing up the complexity ladder**

Each letter represents a notation and its strength (from weak → strong):

**S → o(f(n))** (S-maller than f(n))
**L → O(f(n))** (L-imit upper bound)
**O → Θ(f(n))** (O-exact match)
**T → Ω(f(n))** (T-ar minimum)
**H → ω(f(n))** (H-igher than f(n))

👉 *The SLOTH climbs from smallest to largest.*

```
o  <  O  =  Θ  <  Ω  <  ω
(S)   (L) (O)  (T)   (H)
```

---

# 📌 **4. PRACTICE QUESTIONS (WITH ANSWERS)**

---

### **Q1: What is the Big-O of this loop?**

```java
for (int i = 0; i < n; i++) {
    System.out.println(i);
}
```

**Answer:** `O(n)` — runs n times.

---

### **Q2: What is the complexity of nested loops?**

```java
for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
        System.out.println(i + j);
    }
}
```

**Answer:** `O(n^2)` — nested loops.

---

### **Q3: What is the best notation for exact performance?**

✔ **Θ(f(n))** — the tight bound.

---

### **Q4: True or False**

`o(n)` is the same as `O(n)`.

**Answer:** ❌ False
`o(n)` means *strictly smaller*, `O(n)` allows equality.

---

### **Q5: Which grows faster?**

`n log n` or `n^2`?

**Answer:** `n^2` grows faster.

---

# 📌 **5. JAVA EXAMPLES FOR EACH COMPLEXITY CLASS**

---

## **O(1) — Constant Time**

```java
int getFirst(int[] arr) {
    return arr[0]; // always constant time
}
```

---

## **O(log n) — Logarithmic (Binary Search)**

```java
int binarySearch(int[] arr, int target) {
    int left = 0, right = arr.length - 1;

    while (left <= right) {
        int mid = (left + right) / 2;

        if (arr[mid] == target) return mid;
        if (arr[mid] < target) left = mid + 1;
        else right = mid - 1;
    }
    return -1;
}
```

---

## **O(n) — Linear**

```java
int sum(int[] arr) {
    int total = 0;
    for (int n : arr) {
        total += n;
    }
    return total;
}
```

---

## **O(n log n) — Sorting**

```java
Arrays.sort(arr); // Built-in merge sort / quicksort hybrid
```

---

## **O(n^2) — Quadratic**

```java
for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
        System.out.println(i + j);
    }
}
```

---

## **O(2^n) — Exponential**

```java
int fibonacci(int n) {
    if (n <= 1) return n;
    return fibonacci(n - 1) + fibonacci(n - 2);
}
```

---

## **O(n!) — Factorial**

```java
void permutations(String s, String answer) {
    if (s.length() == 0) {
        System.out.println(answer);
        return;
    }
    for (int i = 0; i < s.length(); i++) {
        char ch = s.charAt(i);
        String rest = s.substring(0, i) + s.substring(i + 1);
        permutations(rest, answer + ch);
    }
}
```

---


