
---

# **Lesson Note — Introduction to Algorithms Using a Simple Example (FizzBuzz in Java)**

As you begin your journey into **Data Structures and Algorithms (DSA)**, it’s useful to start with simple algorithmic exercises that train your logical thinking. One of the most common beginner problems—also frequently asked in interviews—is the **FizzBuzz** problem.

This problem is not about memorizing code. It teaches you how to think algorithmically:

* break a problem down
* define input and output
* iterate through data
* apply conditions
* produce the correct result

---

# **1. Understanding the FizzBuzz Problem**

Imagine a list of numbers from 1 to 10:

```
1 2 3 4 5 6 7 8 9 10
```

The rules for FizzBuzz are simple:

* **If a number is divisible by 3 → print “Fizz”**
* **If a number is divisible by 5 → print “Buzz”**
* **If divisible by both 3 and 5 → print “FizzBuzz”**
* **Otherwise → print the number itself**

This is inspired by a children’s counting game and is widely used to check whether someone understands conditional logic and iteration.

---

# **2. Setting Up the Problem in Java**

Instead of using Swift playgrounds, you will write a simple Java program.

Start by creating an array of integers:

```java
int[] numbers = {1, 2, 3, 4, 5};
```

Here:

* `3` should become **Fizz**
* `5` should become **Buzz**

---

# **3. Step 1 — Detecting Fizz (multiples of 3)**

We iterate through the array using a `for` loop and check for divisibility using the modulus operator `%`.

```java
for (int num : numbers) {
    if (num % 3 == 0) {
        System.out.println(num + " fizz");
    } else {
        System.out.println(num);
    }
}
```

You should now see all the numbers *and* the ones divisible by 3 labeled as “fizz”.

---

# **4. Step 2 — Detecting Buzz (multiples of 5)**

Extend the condition to handle numbers divisible by 5:

```java
for (int num : numbers) {
    if (num % 3 == 0) {
        System.out.println(num + " fizz");
    } else if (num % 5 == 0) {
        System.out.println(num + " buzz");
    } else {
        System.out.println(num);
    }
}
```

Now both fizz and buzz appear correctly.

---

# **5. Step 3 — Expanding the Range to Include FizzBuzz**

Let’s increase the range from 1–10 to 1–15:

```java
int[] numbers = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
```

Because **15 is divisible by both 3 and 5**, it should become **FizzBuzz**.

We must check the combined case first, or the individual cases would match first.

```java
for (int num : numbers) {
    if (num % 3 == 0 && num % 5 == 0) {
        System.out.println(num + " fizz buzz");
    } else if (num % 3 == 0) {
        System.out.println(num + " fizz");
    } else if (num % 5 == 0) {
        System.out.println(num + " buzz");
    } else {
        System.out.println(num);
    }
}
```

This works correctly—but we can optimize further.

---

# **6. Step 4 — Improving the Runtime (Checking %15 Instead of %3 and %5 Separately)**

If the range grows from `1–15` to `1–100` or even `1–10,000`, checking two conditions (`num % 3` and `num % 5`) for every number becomes repetitive.

Since any number divisible by both 3 and 5 must be divisible by **15**, we can simplify the logic:

### ✔ Checking `%15` is faster than checking both `%3 && %5`.

Here is the optimized version:

```java
for (int num : numbers) {
    if (num % 15 == 0) {
        System.out.println(num + " fizz buzz");
    } else if (num % 3 == 0) {
        System.out.println(num + " fizz");
    } else if (num % 5 == 0) {
        System.out.println(num + " buzz");
    } else {
        System.out.println(num);
    }
}
```

This is the **final, efficient solution**.

---

# **7. Full Java Program (Copy & Run)**

```java
public class FizzBuzz {

    public static void main(String[] args) {

        int[] numbers = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};

        for (int num : numbers) {
            if (num % 15 == 0) {
                System.out.println(num + " fizz buzz");
            } else if (num % 3 == 0) {
                System.out.println(num + " fizz");
            } else if (num % 5 == 0) {
                System.out.println(num + " buzz");
            } else {
                System.out.println(num);
            }
        }
    }
}
```

Run this program, and you will see:

```
1
2
3 fizz
4
5 buzz
6 fizz
7
8
9 fizz
10 buzz
11
12 fizz
13
14
15 fizz buzz
```

---

# **8. Key Learning Points**

* You learned how to iterate through a collection of data.
* You applied conditional logic to transform values based on rules.
* You optimized the algorithm by reducing checks using `%15`.
* You practiced writing clean, readable Java code—important for DSA.
* This simple exercise builds the foundation for reasoning about algorithms.

---

