# 第九节 集合框架(二)  List 、Set

##一、List集合

### 1.1、List接口

java.util.List接口继承自Collection接口，是单列集合的一个重要分支，习惯性地会将实现了`List`接口的对象称为List集合。

List接口特点：

1. 它是一个元素存取有序的集合。例如，存元素的顺序是11、22、33。那么集合中，元素的存储就是按照11、22、33的顺序完成的）。
2. 它是一个带有索引的集合，通过索引就可以精确的操作集合中的元素（与数组的索引是一个道理）。
3. 集合中可以有重复的元素，通过元素的equals方法，来比较是否为重复的元素。（与Set集合不同，不允许重复元素）
4. **List 接口提供了特殊的迭代器，称为 ListIterator，除了允许 Iterator 接口提供的正常操作外，该迭代器还允许元素插入和替换，以及双向访问。还提供了一个方法来获取从列表中指定位置开始的列表迭代器。**
5. 

> tips:前面java.util.ArrayList类，该类中的方法都是来自List中定义，即是List一个实现类。

### 1.2、 List接口中常用方法

List作为Collection集合的子接口，不但继承了Collection接口中的全部方法，而且还增加了一些根据元素索引来操作集合的特有方法，如下：

- `public void add(int index, E element)`: 将指定的元素，添加到该集合中的指定位置上。
- `public E get(int index)`:返回集合中指定位置的元素。
- `public E remove(int index)`: 移除列表中指定位置的元素, 返回的是被移除的元素。
- `public E set(int index, E element)`:用指定元素替换集合中指定位置的元素,返回值的更新前的元素。



## 二、List接口的实现类

### 2.1、ArrayList集合

`java.util.ArrayList`集合数据存储的结构是数组结构。元素增删慢，查找快，由于日常开发中使用最多的功能为查询数据、遍历数据，所以`ArrayList`是最常用的集合。

许多程序员开发时非常随意地使用ArrayList完成任何需求，并不严谨，这种用法是不提倡的。

### 2.2、LinkedList集合

`java.util.LinkedList`集合数据存储的结构是链表结构(双链表)。方便元素添加、删除的集合。

实际开发中对一个集合元素的添加与删除经常涉及到首尾操作，而LinkedList提供了大量首尾操作的方法。这些方法我们作为了解即可：

- `public void addFirst(E e)`:将指定元素插入此列表的开头。
- `public void addLast(E e)`:将指定元素添加到此列表的结尾。
- `public E getFirst()`:返回此列表的第一个元素。
- `public E getLast()`:返回此列表的最后一个元素。
- `public E removeFirst()`:移除并返回此列表的第一个元素。
- `public E removeLast()`:移除并返回此列表的最后一个元素。
- `public E pop()`:从此列表所表示的堆栈处弹出一个元素。// 等效removeFirst
- `public void push(E e)`:将元素推入此列表所表示的堆栈。// 等效addFirst
- `public boolean isEmpty()`：如果列表不包含元素，则返回true。

LinkedList是List的子类，List中的方法LinkedList都是可以使用，这里就不做详细介绍，我们只需要了解LinkedList的特有方法即可。在开发时，LinkedList集合也可以作为堆栈，队列的结构使用。（了解即可）

### 2.3、List集合实现类的区别

- **ArrayList 、 LinkedList **

  1. 数据结构：ArrayList内部存储是数组存储，访问快，增删慢；LinkedList内部存储是双链表，访问慢，增删快；

  2. ArrayList、LinkedList都是非线程安全的。所以，若要同步的时候需要自己手动同步，比较费事；当然也可以使用集合工具类实例化时进行同步，具体参照如下：

     ```java
     List<String> springokList=Collections.synchronizedCollection(new 需要同步的类)。
     List<String> springOkList = Collections.synchronizedList(new ArrayList<>());
     ```

  3. ArrayList具有初始化容量，当元素超出其容量，会出现扩容，消耗资源；而LinkedList是链表形式，不存在扩容的说法，在数据添加过程中，容量即扩大；

  4. 

- **ArrayList、Vector**

  1. 数据结构：都是数组存储；

  2. 元素存取有序，并都允许为null；

  3. 都支持fail-fast机制；

  4. Vector单线程，线程安全；但是，ArrayList更通用，实在要同步线程安全，可以使用集合工具类初始化实例，或者直接用`java.util.concurrent`包下对应的类；

  5. 扩容方面，Vector是2倍扩容，ArrayList是1.5倍扩容。扩容效率高于ArrayList；（可以参考源码）

     ```java
     // 1. ArrayList中增长定义：50%
     private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
                 newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
                 newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
             elementData = Arrays.copyOf(elementData, newCapacity);
         }
         
     //2.  Vector中增长定义：100%
     int oldCapacity = elementData.length;
     int newCapacity = oldCapacity + ((capacityIncrement > 0) ?capacityIncrement : 		oldCapacity);
     if (newCapacity - minCapacity < 0) 
     	newCapacity = minCapacity;
     if (newCapacity - MAX_ARRAY_SIZE > 0)
         newCapacity = hugeCapacity(minCapacity);
     elementData = Arrays.copyOf(elementData, newCapacity);
     ```

  6. 在集合中使用数据量比较大的数据，用Vector有一定的优势。

- **CopyOnWriteArrayList**

  1. CopyOnWriteArrayList是ArrayList的线程安全的变体，其中的所有可变操作（add， set等）都是对底层数组进行一次新的复制来实现的，相比ArrayList的要慢一些，适用于读多写少的场景 ;
  2. 在并发操作容器对象时不会抛出ConcurrentModificationException，并且返回的元素与迭代器创建时的元素是一致的，也就是说在迭代中对集合做修改可以使用CopyOnWriteArrayList; **（一般建议不要再迭代时，对集合进行修改！！）**
  3. 容器对象的复制需要一定的开销，如果对象占用内存过大，可能造成频繁的YoungGC和Full GC ;
  4. CopyOnWriteArrayList不能保证数据实时一致性，只能保证最终一致性;

  CopyOnWriteArrayList添加元素源码如下：

  ```java
  public boolean add(E e) {
          final ReentrantLock lock = this.lock;
          lock.lock();
          try {
              Object[] elements = getArray();
              int len = elements.length;
              Object[] newElements = Arrays.copyOf(elements, len + 1);
              newElements[len] = e;
              setArray(newElements);
              return true;
          } finally {
              lock.unlock();
          }
      }
  ```

- **Stack**

  Stack呢，是继承自Vector的，所以用法啊，线程安全什么的跟Vector都差不多。其存储数据结构实现了：堆栈结构，先进后出；



## 三、Set集合

### 3.1、Set接口

1. `java.util.Set`接口：也是Collection接口的一个子接口，它表示数学意义上的集合概念。**Set中不包含重复的元素，即Set中不存两个这样的元素e1和e2，使得e1.equals(e2)为true。**

2. 由于Set接口提供的数据结构是数学意义上集合概念的抽象，因此它需要支持对象的添加、删除，而不需提供随机访问。故Set接口与Collection的接口相同；

3. 具体的Set 实现类依赖添加的对象的 equals()，hashCode()方法来检查等同性。

###3.2、Set接口的实现类

####1、 HashSet集合介绍

`java.util.HashSet`是`Set`接口的一个实现类，它所存储的元素是不可重复的，并且元素都是无序的(即存取顺序不一致)。`java.util.HashSet`底层的实现其实是一个`java.util.HashMap`支持;

`HashSet`是根据对象的哈希值来确定元素在集合中的存储位置，因此具有良好的存取和查找性能。保证元素唯一性的方式依赖于：`hashCode`与`equals`方法。

```java
public class HashSetDemo {
    public static void main(String[] args) {
        //创建 Set集合
        HashSet<String>  set = new HashSet<String>();
        //添加元素
        set.add(new String("cba"));
        set.add("abc");
        set.add("bac"); 
        set.add("cba");  
        //遍历
        for (String name : set) {
            System.out.println(name);
        }
    }
}
--------
cba
abc
bac
```

> tips:根据结果我们发现字符串"cba"只存储了一个，也就是说重复的元素set集合不存储。

####2、HashSet集合存储数据的结构（哈希表）

1.什么是哈希表呢？ 详解参考：**[hash的基本原理与实现](F_hash的基本原理与实现.md)**

在**JDK1.8**之前，哈希表底层采用数组+链表实现，即使用链表处理冲突，同一hash值的链表都存储在一个链表里。但是当位于一个桶中的元素较多，即hash值相等的元素较多时，通过key值依次查找的效率较低。而JDK1.8中，哈希表存储采用数组+链表+红黑树实现，当链表长度超过阈值（8）时，将链表转换为红黑树，这样大大减少了查找时间。

![](attach/img/F0_哈希表.png)

**2.Hashset存储流程原理图 ：**

![](attach/img/F0_哈希流程图.png)

总而言之，**JDK1.8**引入红黑树大程度优化了HashMap的性能，那么对于我们来讲保证HashSet集合元素的唯一，其实就是根据对象的hashCode和equals方法来决定的。如果我们往集合中存放自定义的对象，那么保证其唯一，就必须复写hashCode和equals方法建立属于当前对象的比较方式。

####3、HashSet，TreeSet、LinkedHashSet区别

1. HashSet是采用hash表来实现的，底层调用就是HashMap。**其中的元素没有按顺序排列**，add()、remove()以及contains()等方法都是复杂度为O(1)的方法;
2. TreeSet是采用树结构实现(红黑树算法)。**元素是按顺序进行排列**，但是add()、remove()以及contains()等方法都是复杂度为O(log n)的方法。它还提供了一些方法来处理排序的set，如first(), last(), headSet(), tailSet()等等;
3. LinkedHashSet介于HashSet和TreeSet之间。它也是一个hash表，但是同时维护了一个双链表来记录插入的顺序。基本方法的复杂度为O(1);
4. HashSet中元素可以是null，但只能有一个，TreeSet不允许放入null;
5. 使用TreeSet的收集对象类必须实现Comparable接口，所以是一定指定规则排序存储。而LinkedHashSet按插入的顺序存储；
6. 性能：HashSet > LinkedHashSet > TreeSet;
7. 适应场景：仅去重，用HashSet；若还要有序，用TreeSet；若按插入的顺序纪录，用LinkedHashSet;



##四、List和Set的区别 

1. Set 接口实例存储的是无序的，不重复的数据,可以存储null，但只能一个null。List 接口实例存储的是有序的，可以重复的元素,可以多个null。 

2. Set检索效率低下，删除和插入效率高，插入和删除不会引起元素位置改变 ，实现类有HashSet，TreeSet。 

3. List和数组类似，可以动态增长，根据实际存储的数据的长度自动增长List的长度。查找元素效率高，插入删除效率低，因为会引起其他元素位置改变 ，实现类有ArrayList，LinkedList，Vector，CopyOnWriteArrayList。

4. List遍历：

   ```java
   public class ArrayListTest{
    public static void main(String[] args) {
        List<String> list=new ArrayList<String>();
        list.add("Hello");
        list.add("Java");
        list.add("ArrayList");
        //方法一：使用for或foreach遍历
        for (String str : list) {  
           System.out.println(str);
        }
   
        //方法二：将集合转化为数组，然后进行for或foreach遍历
        String[] strArray=new String[list.size()];
        list.toArray(strArray);
        for(int i=0;i<strArray.length;i++) 
        {
           System.out.println(strArray[i]);
        }
   
       //方法三：使用迭代器器
        Iterator<String> iterator=list.iterator();
        while(iterator.hasNext())//判断下一个元素之后有值
        {
            System.out.println(iterator.next());
        }
    }
   }
   ```

5. Set遍历：

   ```java
   public class SetTest {
       public static void main(String[] args) {
           HashSet<String> sets = new HashSet<>();
           sets.add("h");
           sets.add("e");
           sets.add("l");
           sets.add("l");//不可重复
           sets.add("0");
           //方法一：迭代遍历
           for (Iterator<String> iterator = sets.iterator(); iterator.hasNext();){
               System.out.println(iterator.next());
           }
           //输出结果：
           /*
           0
           e
           h
           l
            */
          //可以看出Set集合是不可重复（添加重复操作不会报错）且无序的
           //方法二：foreach循环（没有普通for循环方法,因为无序，没有索引）
           for (String value:sets) {
               System.out.println(value);
           }
       }
   }
   ```



##五、Collections类

`java.utils.Collections`是集合工具类，用来对集合进行操作。部分方法如下：

### 5.1、常用功能

- `public static <T> boolean addAll(Collection<T> c, T... elements)  `:往集合中添加一些元素。

- `public static void shuffle(List<?> list) 打乱顺序`:打乱集合顺序。

- `public static <T> void sort(List<T> list)`:将集合中元素按照默认规则排序。

  **使用这个方法时，一定要注意T类型一定要实现Comparable接口，并重写compareTo方法，告知外层如何排序，否则将无法编译通过！**

- `public static <T> void sort(List<T> list，Comparator<? super T> )`:将集合中元素按照指定规则排序。

###5.2、Comparable、Comparator区别

1.  都是用于比较排序，Comparable是在类内部实现，相当于是自身对比；而Comparator则是在类外实现，相当于算法与数据分离，相当于第三者参与作为裁判；
2. 一个接口方法int compareTo(Object antherObj)、Comparator.compare(Object o1,Object o2);

```java
public class Student implements Comparable<Student>{

    private String name;
    private int age;

    public static void main(String[] args){
         // 创建四个学生对象 存储到集合中
        List<Student> list = new ArrayList<>();

        list.add(new Student("rose",18));
        list.add(new Student("jack",21));
        list.add(new Student("abc",16));
        list.add(new Student("ace",17));
        list.add(new Student("mark",16));

        System.out.println(list);
        /*
          让学生 按照年龄排序 升序/降序
         */
        Collections.sort(list);//要求 该list中元素类型  必须实现比较器Comparable接口
        System.out.println(list);
    }

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }


    @Override
    public int compareTo(Student o) {
//        return this.age - o.age; //升序
        return o.age-this.age ; // 降序
    }
}
```

```java
Collections.sort(list, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                // 年龄降序
                int result = o2.getAge()-o1.getAge();//年龄降序

                if(result==0){//第一个规则判断完了 下一个规则 姓名的首字母 升序
                    result = o1.getName().charAt(0)-o2.getName().charAt(0);
                }

                return result;
            }
        });
```



