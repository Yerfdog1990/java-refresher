
# **Swapping Variable Values**

## **1. What Does Swapping Mean?**

Swapping is the process of **exchanging the values** of two variables.
For example, if
`a = 10` and `b = 20`,
then after swapping:
`a = 20` and `b = 10`.

This operation is very common in programming — especially in sorting algorithms and data manipulation tasks.

---

## **2. Method 1: Using a Third (Temporary) Variable — *The Standard Method***

This is the **safest** and **most common** way to swap variable values.
It works for **all data types** — integers, floats, strings, or even objects.

### **Logic**

1. Store the value of the first variable (`a`) in a temporary variable (`temp`).
2. Copy the value of the second variable (`b`) into the first variable (`a`).
3. Copy the original value of the first variable (from `temp`) into the second variable (`b`).

### **Example Code**

```java
int a = 10;
int b = 20;

System.out.println("Before Swap: a = " + a + ", b = " + b);

// Step 1: Store value of 'a' in 'temp'
int temp = a;   // temp = 10

// Step 2: Copy 'b' into 'a'
a = b;          // a = 20

// Step 3: Copy original 'a' (from temp) into 'b'
b = temp;       // b = 10

System.out.println("After Swap: a = " + a + ", b = " + b);
```

### **Output**

```
Before Swap: a = 10, b = 20
After Swap: a = 20, b = 10
```

### ✅ **Advantages**

* Very clear and easy to understand.
* Works for any variable type.
* No risk of arithmetic overflow.

---

## **3. Method 2: Swapping Without a Third Variable — *Arithmetic Swap***

This approach swaps two integer values using **arithmetic operations** (`+` and `-`).
It eliminates the need for an extra variable but is **less safe** and **less readable**.

> ⚠️ Use this method only for **numeric (int, float)** types, and avoid when dealing with large numbers (it may cause **overflow**).

### **Logic**

1. Add both numbers and store the result in the first variable (`a = a + b`).
2. Subtract the new `b` value from the sum to get the original `a` (`b = a - b`).
3. Subtract the new `b` value from the sum to get the original `b` (`a = a - b`).

### **Example Code**

```java
int x = 5;
int y = 8;

System.out.println("Before Swap: x = " + x + ", y = " + y);

// Step 1: Add x and y
x = x + y;  // x = 13

// Step 2: Subtract new y from x
y = x - y;  // y = 5 (original x)

// Step 3: Subtract new y from x
x = x - y;  // x = 8 (original y)

System.out.println("After Swap: x = " + x + ", y = " + y);
```

### **Output**

```
Before Swap: x = 5, y = 8
After Swap: x = 8, y = 5
```

---

## **4. Comparison of Methods**

| **Aspect**        | **Using Temporary Variable** | **Without Temporary Variable**                  |
| ----------------- | ---------------------------- | ----------------------------------------------- |
| **Readability**   | Easy to understand           | Less clear                                      |
| **Safety**        | Works for all data types     | Risk of overflow (only for numbers)             |
| **Memory**        | Uses one extra variable      | No extra variable                               |
| **Best Use Case** | General-purpose swapping     | When optimizing memory and using small integers |

---