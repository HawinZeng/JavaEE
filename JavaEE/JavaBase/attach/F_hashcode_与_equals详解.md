#  equals 与 hashCode 详解
内容资源：http://blog.csdn.net/javazejian/article/details/51348320

## 一、equals

### 1.equals()的所属以及内部原理（即Object中equals方法的实现原理）

​	说起equals方法，我们都知道是超类Object中的一个基本方法，用于检测一个对象是否与另外一个对象相等。而在Object类中这个方法实际上是判断两个对象是否具有相同的引用，如果有，它们就一定相等。其源码如下：


```java
public boolean equals(Object obj) {   return (this == obj);     }
```
​	实际上我们知道所有的对象都拥有标识(内存地址)和状态(数据)，同时“==”比较两个对象的的内存地址，所以说 Object 的 equals() 方法是比较两个对象的内存地址是否相等，即若 object1.equals(object2) 为 true，则表示 equals1 和 equals2 实际上是引用同一个对象。

### 2.equals()与‘==’的区别

​	或许这是我们面试时更容易碰到的问题”equals方法与‘==’运算符有什么区别？“，并且常常我们都会胸有成竹地回答：“equals比较的是对象的内容，而‘==’比较的是对象的地址。”。但是从前面我们可以知道equals方法在Object中的实现也是间接使用了‘==’运算符进行比较的，所以从严格意义上来说，我们前面的回答并不完全正确。我们先来看一段代码并运行再来讨论这个问题。

```java
public class Car {
	private int batch;
	public Car(int batch) {
		this.batch = batch;
	}
	public static void main(String[] args) {
		Car c1 = new Car(1);
		Car c2 = new Car(1);
		System.out.println(c1.equals(c2));
		System.out.println(c1 == c2);
	}
}
 ======
 结果：
 false
 false
```
​	分析：对于‘==’运算符比较两个Car对象，返回了false，这点我们很容易明白，毕竟它们比较的是内存地址，而c1与c2是两个不同的对象，所以c1与c2的内存地址自然也不一样。现在的问题是，我们希望生产的两辆的批次（batch）相同的情况下就认为这两辆车相等，但是运行的结果是尽管c1与c2的批次相同，但equals的结果却反回了false。当然对于equals返回了false，我们也是心知肚明的，因为equal来自Object超类，访问修饰符为public，而我们并没有重写equal方法，故调用的必然是Object超类的原始方equals方法，根据前面分析我们也知道该原始equal方法内部实现使用的是'=='运算符，所以返回了false。因此为了达到我们的期望值，我们必须重写Car的equal方法，让其比较的是对象的批次（即对象的内容），而不是比较内存地址，于是修改如下：

```java
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Car) {
			Car c = (Car) obj;
			return batch == c.batch;
		}
		return false;
	}
	====
	true
	false
```
​	嗯，达到我们预期的结果了。因为前面的面试题我们应该这样回答更佳
总结：默认情况下也就是从超类Object继承而来的equals方法与‘==’是完全等价的，比较的都是对象的内存地址，但我们可以重写equals方法，使其按照我们的需求的方式进行比较，如String类重写了equals方法，使其比较的是字符的序列，而不再是内存地址。

### 3.equals()的重写规则

​	前面我们已经知道如何去重写equals方法来实现我们自己的需求了，但是我们在重写equals方法时，还是需要注意如下几点规则的。

1. 自反性。对于任何非null的引用值x，x.equals(x)应返回true。

2. 对称性。对于任何非null的引用值x与y，当且仅当：y.equals(x)返回true时，x.equals(y)才返回true。

3. 传递性。对于任何非null的引用值x、y与z，如果y.equals(x)返回true，y.equals(z)返回true，那么x.equals(z)也应返回true。

4. 一致性。对于任何非null的引用值x与y，假设对象上equals比较中的信息没有被修改，则多次调用x.equals(y)始终返回true或者始终返回false。

5. 对于任何非空引用值x，x.equal(null)应返回false。

当然在通常情况下，如果只是进行同一个类两个对象的相等比较，一般都可以满足以上5点要求，下面我们来看前面写的一个例子。

```java
public class Car {
	private int batch;
	public Car(int batch) {
		this.batch = batch;
	}
	public static void main(String[] args) {
		Car c1 = new Car(1);
		Car c2 = new Car(1);
		Car c3 = new Car(1);
		System.out.println("自反性->c1.equals(c1)：" + c1.equals(c1));
		System.out.println("对称性：");
		System.out.println(c1.equals(c2));
		System.out.println(c2.equals(c1));
		System.out.println("传递性：");
		System.out.println(c1.equals(c2));
		System.out.println(c2.equals(c3));
		System.out.println(c1.equals(c3));
		System.out.println("一致性：");
		for (int i = 0; i < 50; i++) {
			if (c1.equals(c2) != c1.equals(c2)) {
				System.out.println("equals方法没有遵守一致性！");
				break;
			}
		}
		System.out.println("equals方法遵守一致性！");
		System.out.println("与null比较：");
		System.out.println(c1.equals(null));
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Car) {
			Car c = (Car) obj;
			return batch == c.batch;
		}
		return false;
	}
}
```
​	由运行结果我们可以看出equals方法在同一个类的两个对象间的比较还是相当容易理解的。但是如果是子类与父类混合比较，那么情况就不太简单了。下面我们来看看另一个例子，首先，我们先创建一个新类BigCar，继承于Car,然后进行子类与父类间的比较。

```java
public class BigCar extends Car {
	int count;
	public BigCar(int batch, int count) {
		super(batch);
		this.count = count;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BigCar) {
			BigCar bc = (BigCar) obj;
			return super.equals(bc) && count == bc.count;
		}
		return false;
	}
	public static void main(String[] args) {
		Car c = new Car(1);
		BigCar bc = new BigCar(1, 20);
		System.out.println(c.equals(bc));
		System.out.println(bc.equals(c));
	}
}
 ======
 true
 false
```
 	对于这样的结果，自然是我们意料之中的啦。因为BigCar类型肯定是属于Car类型，所以c.equals(bc)肯定为true，对于bc.equals(c)返回false，是因为Car类型并不一定是BigCar类型（Car类还可以有其他子类）。嗯，确实是这样。但如果有这样一个需求，只要BigCar和Car的生产批次一样，我们就认为它们两个是相当的，在这样一种需求的情况下，父类（Car）与子类（BigCar）的混合比较就不符合equals方法对称性特性了。很明显一个返回true，一个返回了false，根据对称性的特性，此时两次比较都应该返回true才对。

​	那么该如何修改才能符合对称性呢？其实造成不符合对称性特性的原因很明显，那就是因为Car类型并不一定是BigCar类型（Car类还可以有其他子类），在这样的情况下(Car instanceof BigCar)永远返回false，因此，我们不应该直接返回false，而应该继续使用父类的equals方法进行比较才行（因为我们的需求是批次相同，两个对象就相等，父类equals方法比较的就是batch是否相同）。因此BigCar的equals方法应该做如下修改：

```java
@Override
	public boolean equals(Object obj) {
		if (obj instanceof BigCar) {
			BigCar bc = (BigCar) obj;
			return super.equals(bc) && count == bc.count;
		}
		return super.equals(obj);
	}
```
这样运行的结果就都为true了。但是到这里问题并没有结束，虽然符合了对称性，却还没符合传递性，实例如下：

```java
public class BigCar extends Car {
	int count;
	public BigCar(int batch, int count) {
		super(batch);
		this.count = count;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BigCar) {
			BigCar bc = (BigCar) obj;
			return super.equals(bc) && count == bc.count;
		}
		return super.equals(obj);
	}
	public static void main(String[] args) {
		Car c = new Car(1);
		BigCar bc = new BigCar(1, 20);
		BigCar bc2 = new BigCar(1, 22);
		System.out.println(bc.equals(c));
		System.out.println(c.equals(bc2));
		System.out.println(bc.equals(bc2));
	}
}
 =====
 true
 true
 false
```
bc，bc2，c的批次都是相同的，按我们之前的需求应该是相等，而且也应该符合equals的传递性才对。但是事实上运行结果却不是这样，违背了传递性。出现这种情况根本原因在于：

1. 父类与子类进行混合比较。

2. 子类中声明了新变量，并且在子类equals方法使用了新增的成员变量作为判断对象是否相等的条件。

只要满足上面两个条件，equals方法的传递性便失效了。而且目前并没有直接的方法可以解决这个问题。因此我们在重写equals方法时这一点需要特别注意。虽然没有直接的解决方法，但是间接的解决方案还说有滴，那就是通过组合的方式来代替继承,还有一点要注意的是组合的方式并非真正意义上的解决问题（只是让它们间的比较都返回了false，从而不违背传递性，然而并没有实现我们上面batch相同对象就相等的需求），而是让equals方法满足各种特性的前提下，让代码看起来更加合情合理，代码如下：

```java
public class Combination4BigCar {
	private Car c;
	private int count;
	public Combination4BigCar(int batch, int count) {
		c = new Car(batch);
		this.count = count;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Combination4BigCar) {
			Combination4BigCar bc = (Combination4BigCar) obj;
			return c.equals(bc.c) && count == bc.count;
		}
		return false;
	}
}
```
​	从代码来看即使batch相同，Combination4BigCar类的对象与Car类的对象间的比较也永远都是false，但是这样看起来也就合情合理了，毕竟Combination4BigCar也不是Car的子类，因此equals方法也就没必要提供任何对Car的比较支持，同时也不会违背了equals方法的传递性。

### 4.equals()的重写规则之必要性深入解读

​	前面我们一再强调了equals方法重写必须遵守的规则，接下来我们就是分析一个反面的例子，看看不遵守这些规则到底会造成什么样的后果。

```java
/** * 反面例子 **/
public class AbnormalResult {
	public static void main(String[] args) {
		List<A> list = new ArrayList<A>();
		A a = new A();
		B b = new B();
		list.add(a);
19		System.out.println("list.contains(a)->" + list.contains(a));
20		System.out.println("list.contains(b)->" + list.contains(b));
21		list.clear();
22		list.add(b);
23		System.out.println("list.contains(a)->" + list.contains(a));
24		System.out.println("list.contains(b)->" + list.contains(b));
	}
	static class A {
		@Override
		public boolean equals(Object obj) {
			return obj instanceof A;
		}
	}
	static class B extends A {
		@Override
		public boolean equals(Object obj) {
			return obj instanceof B;
		}
	}
}
 ==========
 list.contains(a)->true
 list.contains(b)->false
 list.contains(a)->true
 list.contains(b)->true
```
​	19行和24行的输出没什么好说的，将a，b分别加入list中，list中自然会含有a，b。但是为什么20行和23行结果会不一样呢？我们先来看看contains方法内部实现:

```java
@Override       
public boolean contains(Object o) { 
     return indexOf(o) != -1; 
 }
进入indexof方法
        @Override
	public int indexOf(Object o) {
		E[] a = this.a;
		if (o == null) {
			for (int i = 0; i < a.length; i++)
				if (a[i] == null)
					return i;
		} else {
			for (int i = 0; i < a.length; i++)
				if (o.equals(a[i]))
					return i;
		}
		return -1;
	}
```
​	可以看出最终调用的是对象的equals方法，所以当调用20行代码list.contains(b)时，实际上调用了b.equals(a[i]),a[i]是集合中的元素集合中的类型而且为A类型(只添加了a对象)，虽然B继承了A,但此时a[i] instanceof B结果为false，equals方法也就会返回false；而当调用23行代码list.contains(a)时，实际上调用了a.equal(a[i]),其中a[i]是集合中的元素而且为B类型(只添加了b对象)，由于B类型肯定是A类型（B继承了A），所以a[i] instanceof A结果为true，equals方法也就会返回true，这就是整个过程。

​	但很明显结果是有问题的，因为我们的 list的泛型是A,而B又继承了A，此时无论加入了a还是b，都属于同种类型，所以无论是contains(a),还是contains(b)都应该返回true才算正常。而最终却出现上面的结果，这就是因为重写equals方法时没遵守对称性原则导致的结果，如果没遵守传递性也同样会造成上述的结果。当然这里的解决方法也比较简单，我们只要将B类的equals方法修改一下就可以了。

```java
static class B extends A{
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof B){
				return true;
			}
			return super.equals(obj);
		}
	}
```
​	到此，我们也应该明白了重写equals必须遵守几点原则的重要性了。当然这里不止是list，只要是java集合类或者java类库中的其他方法，重写equals不遵守5点原则的话，都可能出现意想不到的结果。

### 5.为什么重写equals()的同时还得重写hashCode()

​	这个问题之前我也很好奇，不过最后还是在书上得到了比较明朗的解释，当然这个问题主要是针对映射相关的操作（Map接口）。学过数据结构的同学都知道Map接口的类会使用到键对象的哈希码，当我们调用put方法或者get方法对Map容器进行操作时，都是根据键对象的哈希码来计算存储位置的，因此如果我们对哈希码的获取没有相关保证，就可能会得不到预期的结果。在java中，我们可以使用hashCode()来获取对象的哈希码，其值就是对象的存储地址，这个方法在Object类中声明，因此所有的子类都含有该方法。那我们先来认识一下hashCode()这个方法吧。hashCode的意思就是散列码，也就是哈希码，是由对象导出的一个整型值，散列码是没有规律的，如果x与y是两个不同的对象，那么x.hashCode()与y.hashCode()基本是不会相同的，下面通过String类的hashCode()计算一组散列码：

```java
public class HashCodeTest {
	public static void main(String[] args) {
		int hash=0;
		String s="ok";
		StringBuilder sb =new StringBuilder(s);
		System.out.println(s.hashCode()+"  "+sb.hashCode());
		String t = new String("ok");
		StringBuilder tb =new StringBuilder(t);
		System.out.println(t.hashCode()+"  "+tb.hashCode());
	}
}
 ====================
 3548  1829164700
 3548  2018699554
```
​	我们可以看出，字符串s与t拥有相同的散列码，这是因为字符串的散列码是由内容导出的。而字符串缓冲sb与tb却有着不同的散列码，这是因为StringBuilder没有重写hashCode方法，它的散列码是由Object类默认的hashCode方法计算出来的对象存储地址，所以散列码自然也就不同了。那么我们该如何重写出一个较好的hashCode方法呢，其实并不难，我们只要合理地组织对象的散列码，就能够让不同的对象产生比较均匀的散列码。例如下面的例子：

```java
public class Model {
	private String name;
	private double salary;
	private int sex;
	
	@Override
	public int hashCode() {
		return name.hashCode()+new Double(salary).hashCode() 
				+ new Integer(sex).hashCode();
	}
}
```
​	上面的代码我们通过合理的利用各个属性对象的散列码进行组合，最终便能产生一个相对比较好的或者说更加均匀的散列码，当然上面仅仅是个参考例子而已，我们也可以通过其他方式去实现，只要能使散列码更加均匀（所谓的均匀就是每个对象产生的散列码最好都不冲突）就行了。

​	不过这里有点要注意的就是java 7中对hashCode方法做了两个改进，首先java发布者希望我们使用更加安全的调用方式来返回散列码，也就是使用null安全的方法Objects.hashCode（**注意不是Object而是java.util.Objects**）方法，这个方法的优点是如果参数为null，就只返回0，否则返回对象参数调用的hashCode的结果。**Objects.hashCode 源码如下：**

```
	public static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }
```
java 7还提供了另外一个方法java.util.Objects.hash(Object... objects),当我们需要组合多个散列值时可以调用该方法。

```java
public  class Model {
	private   String name;
	private double salary;
	private int sex;
//	@Override
//	public int hashCode() {
//		return Objects.hashCode(name)+new Double(salary).hashCode() 
//				+ new Integer(sex).hashCode();
//	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name,salary,sex);
	}
}
```
好了，到此hashCode()该介绍的我们都说了，还有一点要说的如果我们提供的是一个数值类型的变量的话，那么我们可以调用Arrays.hashCode()来计算它的散列码，这个散列码是由数组元素的散列码组成的。接下来我们回归到我们之前的问题，重写equals方法时也必须重写hashCode方法。在Java API文档中关于hashCode方法有以下几点规定（原文来自java深入解析一书）。

1. 在java应用程序执行期间，如果在equals方法比较中所用的信息没有被修改，那么在同一个对象上多次调用hashCode方法时必须一致地返回相同的整数。如果多次执行同一个应用时，不要求该整数必须相同。

2. 如果两个对象通过调用equals方法是相等的，那么这两个对象调用hashCode方法必须返回相同的整数。

3. 如果两个对象通过调用equals方法是不相等的，不要求这两个对象调用hashCode方法必须返回不同的整数。但是程序员应该意识到对不同的对象产生不同的hash值可以提供哈希表的性能。

通过前面的分析，我们知道在Object类中，hashCode方法是通过Object对象的地址计算出来的，因为Object对象只与自身相等，所以同一个对象的地址总是相等的，计算取得的哈希码也必然相等，对于不同的对象，由于地址不同，所获取的哈希码自然也不会相等。因此到这里我们就明白了，如果一个类重写了equals方法，但没有重写hashCode方法，将会直接违法了第2条规定，这样的话，如果我们通过映射表(Map接口)操作相关对象时，就无法达到我们预期想要的效果。如果大家不相信, 可以看看下面的例子（来自java深入解析一书）:

```java
public class MapTest {
	public static void main(String[] args) {
		Map<String,Value> map1 = new HashMap<String,Value>();
		String s1 = new String("key");
		String s2 = new String("key");	
		Value value = new Value(2);
		map1.put(s1, value);
		System.out.println("s1.equals(s2):"+s1.equals(s2));
		System.out.println("map1.get(s1):"+map1.get(s1));
		System.out.println("map1.get(s2):"+map1.get(s2));
		
		Map<Key,Value> map2 = new HashMap<Key,Value>();
		Key k1 = new Key("A");
		Key k2 = new Key("A");
		map2.put(k1, value);
		System.out.println("k1.equals(k2):"+k1.equals(k2));
		System.out.println("map2.get(k1):"+map2.get(k1));
		System.out.println("map2.get(k2):"+map2.get(k2));
	}
	
	static class Key{
		private String k;
		public Key(String key){
			this.k=key;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Key){
				Key key=(Key)obj;
				return k.equals(key.k);
			}
			return false;
		}
	}
	
	static class Value{
		private int v;
		
		public Value(int v){
			this.v=v;
		}
		
		@Override
		public String toString() {
			return "类Value的值－－>"+v;
		}
	}
}
 ===========================================
 s1.equals(s2):true
 map1.get(s1):类Value的值－－>2
 map1.get(s2):类Value的值－－>2
 
 k1.equals(k2):true
 map2.get(k1):类Value的值－－>2
 map2.get(k2):null
```
​	对于s1和s2的结果，我们并不惊讶，因为相同的内容的s1和s2获取相同内的value这个很正常，因为String类重写了equals方法和hashCode方法，使其比较的是内容和获取的是内容的哈希码。但是对于k1和k2的结果就不太尽人意了，k1获取到的值是2，k2获取到的是null，这是为什么呢？想必大家已经发现了，Key只重写了equals方法并没有重写hashCode方法，这样的话，equals比较的确实是内容，而hashCode方法呢？没重写，那就肯定调用超类Object的hashCode方法，这样返回的不就是地址了吗？k1与k2属于两个不同的对象，返回的地址肯定不一样，所以现在我们知道调用map2.get(k2)为什么返回null了吧？那么该如何修改呢？很简单，我们要做也重写一下hashCode方法即可（如果参与equals方法比较的成员变量是引用类型的，则可以递归调用hashCode方法来实现）：

```java
@Override
public int hashCode() {
     return k.hashCode();
}
 ===============
 s1.equals(s2):true
 map1.get(s1):类Value的值－－>2
 map1.get(s2):类Value的值－－>2
 
 k1.equals(k2):true
 map2.get(k1):类Value的值－－>2
 map2.get(k2):类Value的值－－>2
```

### 6.重写equals()中getClass与instanceof的区别

​	虽然前面我们都在使用instanceof（当然前面我们是根据需求（批次相同即相等）而使用instanceof的），但是在重写equals() 方法时，**一般都是推荐使用 getClass 来进行类型判断（除非所有的子类有统一的语义才使用instanceof），不是使用 instanceof**。我们都知道 instanceof 的作用是判断其左边对象是否为其右边类的实例，返回 boolean 类型的数据。可以用来判断继承中的子类的实例是否为父类的实现。下来我们来看一个例子：

```java
父类Person:
public class Person {
        protected String name;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Person(String name){
            this.name = name;
        }
        public boolean equals(Object object){
            if(object instanceof Person){
                Person p = (Person) object;
                if(p.getName() == null || name == null){
                    return false;
                }
                else{
                    return name.equalsIgnoreCase(p.getName ());
                }
            }
            return false;
       }
    }
```
```java
子类 Employee:
public class Employee extends Person{
        private int id;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public Employee(String name,int id){
            super(name);
            this.id = id;
        }
        /**
         * 重写equals()方法
         */
        public boolean equals(Object object){
            if(object instanceof Employee){
                Employee e = (Employee) object;
                return super.equals(object) && e.getId() == id;
            }
            return false;
        }
    }
```
上面父类 Person 和子类 Employee 都重写了 equals(),不过 Employee 比父类多了一个id属性,而且这里我们并没有统一语义。测试代码如下：

```java
public class Test {
        public static void main(String[] args) {
            Employee e1 = new Employee("chenssy", 23);
            Employee e2 = new Employee("chenssy", 24);
            Person p1 = new Person("chenssy");
            System.out.println(p1.equals(e1));
            System.out.println(p1.equals(e2));
            System.out.println(e1.equals(e2));
        }
    }
    ======
    true
    true
    false
```
上面代码我们定义了两个员工和一个普通人，虽然他们同名，但是他们肯定不是同一人，所以按理来说结果应该全部是 false，但是事与愿违，结果是：true、true、false。对于那 e1!=e2 我们非常容易理解，因为他们不仅需要比较 name,还需要比较 ID。但是 p1 即等于 e1 也等于 e2，这是非常奇怪的，因为 e1、e2 明明是两个不同的类，但为什么会出现这个情况？首先 p1.equals(e1)，是调用 p1 的 equals 方法，该方法使用 instanceof 关键字来检查 e1 是否为 Person 类，这里我们再看看 instanceof：判断其左边对象是否为其右边类的实例，也可以用来判断继承中的子类的实例是否为父类的实现。他们两者存在继承关系，肯定会返回 true 了，而两者 name 又相同，所以结果肯定是 true。所以出现上面的情况就是使用了关键字 instanceof，这是非常容易导致我们“钻牛角尖”。故在覆写 equals 时推荐使用 getClass 进行类型判断。而不是使用 instanceof（除非子类拥有统一的语义）。
重写如下，得到理想结果：

```java
public class EqualsTest {

	public static void main(String[] args) {
		Employee e1 = new Employee("cherry", 21);
		Employee e2 = new Employee("cherry", 23);
		
		Person p1 = new Person("cherry");
		System.out.println(p1.equals(e1));
		System.out.println(e1.equals(p1));
		System.out.println(p1.equals(e2));
		System.out.println(e1.equals(e2));
		
		Employee e3 = new Employee("cherry", 21);
		System.out.println(e1.equals(e3));
	}
}

class Person {
	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Person(String name) {
		this.name = name;
	}

	public boolean equals(Object object) {
		if (this.getClass() != object.getClass()) {
			System.out.println("=======Person===========");
			return false;
		}
		Person p = (Person) object;
		if (p.getName() == null || name == null) {
			return false;
		} else {
			return name.equalsIgnoreCase(p.getName());
		}
	}
}

class Employee extends Person {
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Employee(String name, int id) {
		super(name);
		this.id = id;
	}

	public boolean equals(Object object) {
		if(this.getClass() != object.getClass()) {
			System.out.println("=======Employee===========");
			return false;
		}
		Employee e = (Employee) object;
		return super.equals(object) && e.getId() == id;
	}
}
 =====================================================================
 =======Person===========
 false
 =======Employee===========
 false
 =======Person===========
 false
 false
 true
```

### 7.编写一个完美equals()的几点建议

下面给出编写一个完美的equals方法的建议（出自Java核心技术 第一卷：基础知识）：

1. 显式参数命名为otherObject,稍后需要将它转换成另一个叫做other的变量（参数名命名，强制转换请参考建议5）

2. 检测this与otherObject是否引用同一个对象 ：if(this == otherObject) return true;（存储地址相同，肯定是同个对象，直接返回true）

3. 检测otherObject是否为null ，如果为null,返回false.if(otherObject == null) return false;

4. 比较this与otherObject是否属于同一个类 （视需求而选择）

   4.1 如果equals的语义在每个子类中有所改变，就使用getClass检测 ：if(getClass()!=otherObject.getClass()) return false; (参考前面分析的第6点)

   4.2 如果所有的子类都拥有统一的语义，就使用instanceof检测 ：if(!(otherObject instanceof ClassName)) return false;（即前面我们所分析的父类car与子类bigCar混合比，我们统一了批次相同即相等）

5. 将otherObject转换为相应的类类型变量：ClassName other = (ClassName) otherObject;

6. 现在开始对所有需要比较的域进行比较 。使用==比较基本类型域，使用equals比较对象域。如果所有的域都匹配，就返回true，否则就返回flase。如果在子类中重新定义equals，就要在其中包含调用super.equals(other)；

当此方法被重写时，通常有必要重写 hashCode 方法，以维护 hashCode 方法的常规协定，该协定声明 相等对象必须具有相等的哈希码 。




## 二、HashCode深入
内容资源来源：https://blog.csdn.net/qq_38182963/article/details/78940047

我们知道 HashMap 依赖的 hashcode 和 hash 算法到底是怎么实现的嘛？

HashMap 高度依赖的 hashcode 和 hash 算法，虽然在很多书里面，都说这是数学家应该去研究的事情，但我想，程序员也应该了解他是怎么实现的。为什么这么做？就像娶老婆，你可能做不到创造老婆，但是你得知道你老婆是怎么来的？家是哪的？为什么喜欢你？

### 1. 二进制计算的一些基础知识
1. << : 左移运算符，num << 1,相当于num乘以2  低位补0
2. \>> : 右移运算符，num >> 1,相当于num除以2  高位补0
3. \>>> : 无符号右移，忽略符号位，空位都以0补齐
4. % : 模运算 取余
5. ^ :   位异或 第一个操作数的的第n位于第二个操作数的第n位相反，那么结果的第n为也为1，否则为0
6. & : 与运算 第一个操作数的的第n位于第二个操作数的第n位如果都是1，那么结果的第n为也为1，否则为0
7. | :  或运算 第一个操作数的的第n位于第二个操作数的第n位 只要有一个是1，那么结果的第n为也为1，否则为0
8. ~ : 非运算 操作数的第n位为1，那么结果的第n位为0，反之，也就是取反运算（一元操作符：只操作一个数）

### 2. 为什么使用 hashcode

那么我们就说说为什么使用 hashcode ，hashCode 存在的第一重要的原因就是在 HashMap(HashSet 其实就是HashMap) 中使用（其实Object 类的 hashCode 方法注释已经说明了 ），我知道，HashMap 之所以速度快，因为他使用的是散列表，根据 key 的 hashcode 值生成数组下标（通过内存地址直接查找，没有任何判断），时间复杂度完美情况下可以达到 n1（和数组相同，但是比数组用着爽多了，但是需要多出很多内存，相当于以空间换时间）。

扩展内容如下：[hash的详解](attach/F_hash的基本原理与实现.md)


### 3. String 类型的 hashcode 方法
在 JDK 中，Object 的 hashcode 方法是本地方法，也就是用 c 语言或 c++ 实现的，该方法直接返回对象的 内存地址。这么做会有说明问题呢？我们用代码看看：

```
class Test1{
  String name;
  public Test1(String name) {
    this.name = name;
  }
  public static void main(String[] args) {
    Map<Test1, String> map = new HashMap<>(4);
    map.put(new Test1("hello"), "hello");
    String hello = map.get(new Test1("hello"));
    System.out.println(hello);
  }
}
 =============
 null
```
从某个角度说，这两个对象是一样的，因为名称一样，name 属性都是 hello，当我们使用这个 key 时，按照逻辑，应该返回 hello 给我们。但是，由于没有重写 hashcode 方法，JDK 默认使用 Objective 类的 hashcode 方法，返回的是一个虚拟内存地址，而每个对象的虚拟地址都是不同的，所以，这个肯定不会返回 hello 。

如果我们重写 hashcode 和 equals 方法：

```
 @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Test1 test1 = (Test1) o;
        return Objects.equals(name, test1.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    ========
    hello
```
JDK 中，我们经常把 String 类型作为 key，那么 String 类型是如何重写 hashCode 方法的呢？

我们看看代码：

    public int hashCode() {
        int h = hash;
        if (h == 0 && value.length > 0) {
            char val[] = value;
    
            for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
            }
            hash = h;
        }
        return h;
    }

代码非常简单，就是使用 String 的 char 数组的数字每次乘以 31 再叠加最后返回，因此，每个不同的字符串，返回的 hashCode 肯定不一样。那么为什么使用 31 呢？

### 4. 为什么大部分 hashcode 方法使用 31

如果有使用 eclipse 的同学肯定知道，该工具默认生成的 hashCode 方法实现也和 String 类型差不多。都是使用的 31 ，那么有没有想过：为什么要使用 31 呢？

在名著 《Effective Java》第 42 页就有对 hashCode 为什么采用 31 做了说明：

之所以使用 31， 是因为他是一个奇素数。如果乘数是偶数，并且乘法溢出的话，信息就会丢失，因为与2相乘等价于移位运算（低位补0）。使用素数的好处并不很明显，但是习惯上使用素数来计算散列结果。 31 有个很好的性能，即用移位和减法来代替乘法，可以得到更好的性能： 31 * i == (i << 5） - i， 现代的 VM 可以自动完成这种优化。这个公式可以很简单的推导出来。

这个问题在 SO 上也有讨论： https://stackoverflow.com/questions/299304/why-does-javas-hashcode-in-string-use-31-as-a-multiplier）

可以看到，使用 31 最主要的还是为了性能。当然用 63 也可以。但是 63 的溢出风险就更大了。那么15 呢？仔细想想也可以。

在《Effective Java》也说道：编写这种散列函数是个研究课题，最好留给数学家和理论方面的计算机科学家来完成。我们此次最重要的是知道了为什么使用31。


### 5. HashMap 的 hash 算法的实现原理（为什么右移 16 位，为什么要使用 ^ 位异或）

好了，知道了 hashCode 的生成原理了，我们要看看今天的主角，hash 算法。

其实，这个也是数学的范畴，从我们的角度来讲，只要知道这是为了更好的均匀散列表的下标就好了，但是，就是耐不住好奇心啊！ 能多知道一点就是一点，我们来看看 HashMap 的 hash 算法（JDK 8）.

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16); // 可以核实下如何算
    }

乍看一下就是简单的异或运算和右移运算，但是为什么要异或呢？为什么要移位呢？而且移位16？

在分析这个问题之前，我们需要先看看另一个事情，什么呢？就是 HashMap 如何根据 hash 值找到数组种的对象，我们看看 get 方法的代码：

    final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        if ((tab = table) != null && (n = tab.length) > 0 &&
            // 我们需要关注下面这一行
            (first = tab[(n - 1) & hash]) != null) {
            if (first.hash == hash && // always check first node
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            if ((e = first.next) != null) {
                if (first instanceof TreeNode)
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
我们看看代码中注释下方的一行代码：first = tab[(n - 1) & hash])。

使用数组长度减一 与运算 hash 值。这行代码就是为什么要让前面的 hash 方法移位并异或。

我们分析一下：

首先，假设有一种情况，对象 A 的 hashCode 为 1 000 010 001 110 001 000 001 111 000 000，对象 B 的 hashCode 为 0111011100111000101000010100000。

如果数组长度是16，也就是 15 与运算这两个数， 你会发现结果都是0。**［具体可以核算下］**这样的散列结果太让人失望了。很明显不是一个好的散列算法。

**但是如果我们将 hashCode 值右移 16 位，也就是取 int 类型的一半，刚好将该二进制数对半切开。并且使用位异或运算（如果两个数对应的位置相反，则结果为1，反之为0），这样的话，就能避免我们上面的情况的发生。**

总的来说，使用位移 16 位和 异或 就是防止这种极端情况。但是，该方法在一些极端情况下还是有问题，比如：10000000000000000000000000 和 1000000000100000000000000 这两个数，如果数组长度是16，那么即使右移16位，在异或，hash 值还是会重复。但是为了性能，对这种极端情况，JDK 的作者选择了性能。毕竟这是少数情况，为了这种情况去增加 hash 时间，性价比不高。

### 6. HashMap 为什么使用 & 与运算代替模运算？

好了，知道了 hash 算法的实现原理还有他的一些取舍，我们再看看刚刚说的那个根据hash计算下标的方法：

tab[(n - 1) & hash]；

其中 n 是数组的长度。其实该算法的结果和模运算的结果是相同的。**但是，对于现代的处理器来说，除法和求余数（模运算）是最慢的动作。**

上面情况下和模运算相同呢？

** a % b == (b-1) & a ,当b是2的指数时，等式成立。 **

我们说 & 与运算的定义：与运算 第一个操作数的的第n位于第二个操作数的第n位如果都是1，那么结果的第n为也为1，否则为0；

当 n 为 16 时， 与运算 101010100101001001101 时，也就是
1111 & 101010100101001001000 结果：1000 = 8
1111 & 101000101101001001001 结果：1001 = 9
1111 & 101010101101101001010 结果： 1010 = 10
1111 & 101100100111001101100 结果： 1100 = 12

可以看到，当 n 为 2 的幂次方的时候，减一之后就会得到 1111* 的数字，这个数字正好可以掩码。并且得到的结果取决于 hash 值。因为 hash 值是1，那么最终的结果也是1 ，hash 值是0，最终的结果也是0。

### 7. HashMap 的容量为什么建议是 2的幂次方？

到这里，我们提了一个关键的问题： HashMap 的容量为什么建议是 2的幂次方？正好可以和上面的话题接上。楼主就是这么设计的。

为什么要 2 的幂次方呢？

我们说，hash 算法的目的是为了让hash值均匀的分布在桶中（数组），那么，如何做到呢？试想一下，如果不使用 2 的幂次方作为数组的长度会怎么样？

假设我们的数组长度是10，还是上面的公式：
1010 & 101010100101001001000 结果：1000 = 8
1010 & 101000101101001001001 结果：1000 = 8
1010 & 101010101101101001010 结果： 1010 = 10
1010 & 101100100111001101100 结果： 1000 = 8

看到结果我们惊呆了，这种散列结果，会导致这些不同的key值全部进入到相同的插槽中，形成链表，性能急剧下降。

所以说，我们一定要保证 & 中的二进制位全为 1，才能最大限度的利用 hash 值，并更好的散列，只有全是1 ，才能有更多的散列结果。如果是 1010，有的散列结果是永远都不会出现的，比如 0111，0101，1111，1110…，只要 & 之前的数有 0， 对应的 1 肯定就不会出现（因为只有都是1才会为1）。大大限制了散列的范围。

### 8. 我们自定义 HashMap 容量最好是多少？

那我们如何自定义呢？自从有了阿里的规约插件，每次楼主都要初始化容量，如果我们预计我们的散列表中有2个数据，那么我就初始化容量为2嘛？

绝对不行，如果大家看过源码就会发现，如果Map中已有数据的容量达到了初始容量的 75%，那么散列表就会扩容，而扩容将会重新将所有的数据重新散列，性能损失严重，所以，我们可以必须要大于我们预计数据量的 1.34 倍，如果是2个数据的话，就需要初始化 2.68 个容量。当然这是开玩笑的，2.68 不可以，3 可不可以呢？肯定也是不可以的，我前面说了，如果不是2的幂次方，散列结果将会大大下降。导致出现大量链表。那么我可以将初始化容量设置为4。 当然了，如果你预计大概会插入 12 条数据的话，那么初始容量为16简直是完美，一点不浪费，而且也不会扩容。

## 总结

好了，分析完了 hashCode 和 hash 算法，让我们对 HashMap 又有了全新的认识。当然，HashMap 中还有很多有趣的东西值得挖掘;

总的来说，通过今天的分析，对我们今后使用 HashMap 有了更多的把握，也能够排查一些问题，比如链表数很多，肯定是数组初始化长度不对，如果某个map很大，注意，肯定是事先没有定义好初始化长度，假设，某个Map存储了10000个数据，那么他会扩容到 20000，实际上，根本不用 20000，只需要 10000* 1.34= 13400 个，然后向上找到一个2 的幂次方，也就是 16384 初始容量足够。












