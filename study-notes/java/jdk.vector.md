# java类 - Vector分析

`Vector` 也是实现于 `List` 接口， 底层数据结构和 `ArrayList` 类似，也是一个动态数组存放数据。

 与 `ArrayList` 的差别在于， `add()` 方法的时候，使用了 `synchronized` 进行同步写数据。

 但是， `synchronized` 在并发时，琐竞争会非常的严重，所以 `Vector` 是一个同步容器并不是一个并发容器。

 - `add()`
 ```java
    /**
     * Appends the specified element to the end of this Vector.
     *
     * @param e element to be appended to this Vector
     * @return {@code true} (as specified by {@link Collection#add})
     * @since 1.2
     */
    public synchronized boolean add(E e) {
        modCount++;
        ensureCapacityHelper(elementCount + 1);
        elementData[elementCount++] = e;
        return true;
    }
 ```

- `add(index, e)`
 ```java

    /**
     * Inserts the specified element at the specified position in this Vector.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index > size()})
     * @since 1.2
     */
    public void add(int index, E element) {
        insertElementAt(element, index);
    }

    /**
    * Inserts the specified object as a component in this vector at the
    * specified {@code index}. Each component in this vector with
    * an index greater or equal to the specified {@code index} is
    * shifted upward to have an index one greater than the value it had
    * previously.
    *
    * <p>The index must be a value greater than or equal to {@code 0}
    * and less than or equal to the current size of the vector. (If the
    * index is equal to the current size of the vector, the new element
    * is appended to the Vector.)
    *
    * <p>This method is identical in functionality to the
    * {@link #add(int, Object) add(int, E)}
    * method (which is part of the {@link List} interface).  Note that the
    * {@code add} method reverses the order of the parameters, to more closely
    * match array usage.
    *
    * @param      obj     the component to insert
    * @param      index   where to insert the new component
    * @throws ArrayIndexOutOfBoundsException if the index is out of range
    *         ({@code index < 0 || index > size()})
    */
   public synchronized void insertElementAt(E obj, int index) {
       modCount++;
       if (index > elementCount) {
           throw new ArrayIndexOutOfBoundsException(index
                                                    + " > " + elementCount);
       }
       ensureCapacityHelper(elementCount + 1);
       System.arraycopy(elementData, index, elementData, index + 1, elementCount - index);
       elementData[index] = obj;
       elementCount++;
   }
 ```
