# java类 - ArrayList解析(基于jdk1.8)

`ArrayList` 实现于 `List`、`RandomAccess` 接口。 可以插入空数据，也支持随机访问。

`ArrayList` 相当于动态数据，其中最重要的两个属性分别是： `elementData` 数组，以及 `size` 大小。

- 添加元素
 - `add()`
    ```java
        /**
         * Appends the specified element to the end of this list.
         *
         * @param e element to be appended to this list
         * @return <tt>true</tt> (as specified by {@link Collection#add})
         */
        public boolean add(E e) {
            //验证数组容量，如果容量不足时，按 capacity + (capacity >> 1) 进行容量扩展
            ensureCapacityInternal(size + 1);  // Increments modCount!!
            //将要添加的元素放入到尾部，并将size+1.
            //因为插入元素其实是引用关系，所以，可以允许null的值被添加
            elementData[size++] = e;
            return true;
        }
    ```

 - `add(index, e)`
     ```java
        /**
         * Inserts the specified element at the specified position in this
         * list. Shifts the element currently at that position (if any) and
         * any subsequent elements to the right (adds one to their indices).
         *
         * @param index index at which the specified element is to be inserted
         * @param element element to be inserted
         * @throws IndexOutOfBoundsException {@inheritDoc}
         */
        public void add(int index, E element) {
            //检查要放入的位置是否有问题
            rangeCheckForAdd(index);
            //验证数组容量，如果容量不足时，按 capacity + (capacity >> 1) 进行容量扩展
            ensureCapacityInternal(size + 1);  // Increments modCount!!
            //进行数组的拷贝，向后移动，将index位置空出，用来存放本次要添加的数据
            System.arraycopy(elementData, index, elementData, index + 1,
                             size - index);
            //在指定的位置插入元素
            elementData[index] = element;
            //size + 1
            size++;
        }
     ```

 - `group(minCapacity)`
     ```java
         /**
          * Increases the capacity to ensure that it can hold at least the
          * number of elements specified by the minimum capacity argument.
          *
          * @param minCapacity the desired minimum capacity
          */
         private void grow(int minCapacity) {
             // overflow-conscious code
             int oldCapacity = elementData.length;
             //对数组进行容量计算，每次扩容都是当前容量的一半
             int newCapacity = oldCapacity + (oldCapacity >> 1);
             if (newCapacity - minCapacity < 0)
                 newCapacity = minCapacity;
             if (newCapacity - MAX_ARRAY_SIZE > 0)
                 newCapacity = hugeCapacity(minCapacity);
             // minCapacity is usually close to size, so this is a win:
             elementData = Arrays.copyOf(elementData, newCapacity);
         }
     ```

 > `ArrayList` 读取数据快，但是在添加数据时，主要消耗都是在数组的扩容。
 >
 > 所以，在日常的使用中，如果能预知数组的长度，最好指定大小，尽量减少扩容的次数。
 >
 > 并且，要减少在指定位置播入数据的操作


- 序列化

由于`ArrayList`是基于数组实现的， 所以，并不是所有的空间都会被使用。因此，`elementData` 是使用 `transient` 修饰的， 防止被自动序列化. 所以, `ArrayList` 重写了 `writeObject` 和  `readObject` 来自己实现序列化功能。

```java
    /**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer. Any
     * empty ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * will be expanded to DEFAULT_CAPACITY when the first element is added.
     */
    transient Object[] elementData; // non-private to simplify nested class access
```

```java
   /**
   * Save the state of the <tt>ArrayList</tt> instance to a stream (that
   * is, serialize it).
   *
   * @serialData The length of the array backing the <tt>ArrayList</tt>
   *             instance is emitted (int), followed by all of its elements
   *             (each an <tt>Object</tt>) in the proper order.
   */
  private void writeObject(java.io.ObjectOutputStream s)
      throws java.io.IOException{
      // Write out element count, and any hidden stuff
      int expectedModCount = modCount;
      s.defaultWriteObject();

      // Write out size as capacity for behavioural compatibility with clone()
      s.writeInt(size);

      // Write out all elements in the proper order.
      // 读取数组中存在元素的数据
      for (int i=0; i<size; i++) {
          s.writeObject(elementData[i]);
      }

      if (modCount != expectedModCount) {
          throw new ConcurrentModificationException();
      }
  }
```

```java
    /**
     * Reconstitute the <tt>ArrayList</tt> instance from a stream (that is,
     * deserialize it).
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        elementData = EMPTY_ELEMENTDATA;

        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // Read in capacity
        s.readInt(); // ignored

        if (size > 0) {
            // be like clone(), allocate array based upon size not capacity
            ensureCapacityInternal(size);

            Object[] a = elementData;
            // Read in all elements in the proper order.
            for (int i=0; i<size; i++) {
                a[i] = s.readObject();
            }
        }
    }
```
