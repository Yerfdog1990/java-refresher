
---

### **Section 3.2: A Simple Loop**

The following function finds the maximal element in an array:

```java
public static int findMax(int[] array) {
    int max = Integer.MIN_VALUE;
    for (int i = 0; i < array.length; i++) {
        if (max < array[i]) {
            max = array[i];
        }
    }
    return max;
}
```

The input size is the size of the array, which I called `array.length` in the code.

Let's count the operations.

```
int max = Integer.MIN_VALUE;
int i = 0;
```

These two assignments are done only once, so that's 2 operations. The operations that are looped are:

```
if (max < array[i])
i++;
max = array[i]
```

Since there are 3 operations in the loop, and the loop is done n times, we add 3n to our already existing 2 operations to get **3n + 2**. So our function takes **3n + 2 operations** to find the max (its complexity is 3n + 2), a polynomial where the fastest growing term is a factor of n, so it is **O(n)**.

You probably have noticed that "operation" is not very well defined. For instance I said that `if (max < array[i])` was one operation, but depending on the architecture this statement can compile to for instance three instructions: one memory read, one comparison and one branch. I have also considered all operations as the same, even though for instance the memory operations will be slower than the others, and their performance will vary wildly due for instance to cache effects. I also have completely ignored the return statement, the fact that a frame will be created for the function, etc. In the end it doesn't matter to complexity analysis, because whatever way I choose to count operations, it will only change the coefficient of the n factor and the constant, so the result will still be O(n).

Complexity shows how the algorithm scales with the size of the input, but it isn't the only aspect of performance!
