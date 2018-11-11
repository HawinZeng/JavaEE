# 第十二节 线程安全、线程池、Lambda表达式

##一、线程安全

当多线程操作共享数据时，会产生安全问题！如下例子：

```java
public class Demo01Ticket {
    public static void main(String[] args) {
        //创建Runnable接口的实现类对象
        RunnableImpl run = new RunnableImpl();
        //创建Thread类对象,构造方法中传递Runnable接口的实现类对象
        Thread t0 = new Thread(run);
        Thread t1 = new Thread(run);
        Thread t2 = new Thread(run);
        //调用start方法开启多线程
        t0.start();
        t1.start();
        t2.start();
    }
}

public class RunnableImpl implements Runnable{
    //定义一个多个线程共享的票源
    private  int ticket = 100;
    //设置线程任务:卖票
    @Override
    public void run() {
        //使用死循环,让卖票操作重复执行
        while(true){
            //先判断票是否存在
            if(ticket>0){
                //提高安全问题出现的概率,让程序睡眠
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //票存在,卖票 ticket--
                System.out.println(Thread.currentThread().getName()+"-->正在卖第"+ticket+"张票");
                ticket--;
            }
        }
    }
}
--------------
Thread-0-->正在卖第1张票
Thread-2-->正在卖第1张票
Thread-1-->正在卖第0张票
Thread-0-->正在卖第-1张票 // 出现了重复票或不存在的票
```

> 线程安全问题：都是由全局变量、静态变量引起的，因为共享。若这两种数据，一般只有读操作，线程安全；若一定要写操作，则要考虑线程同步，否则会引发线程安全问题；

上面讲了线程安全问题，该如何解决呢？那就得使用java的同步机制 －－ 关键字**`synchronized`**;

### 1.1、线程同步

三种方式完成同步操作：

1. 同步代码t块；
2. 同步方法；
3. 锁机制；

###1.2、同步代码块

```java
synchronized (obj){ // obj 为同步锁对象，run()方法外任意对象
	...
}

public class RunnableImpl implements Runnable{
    private  int ticket = 100;
    //创建一个锁对象
    Object obj = new Object();
    @Override
    public void run() {
        //使用死循环,让卖票操作重复执行
        while(true){
           //同步代码块
            synchronized (obj){
                //先判断票是否存在
                if(ticket>0){
                   ...
                }
            }
        }
    }
}
```

**【同步原理：】**

> **当多个线程来操作同一共享资源时，发现有synchronized同步锁，先抢到的会劫持同步锁对象obj。而另外线程执行过来，发现同步锁不见了，就会线程阻塞，直到拿到同步锁为止才会往下执行。**
>
> **所以，这个又是一个抢同步锁的过程，虽然使用同步锁，存在劫持/释放同步锁过程，效率变低了。但是却保证了操作共享资源安全；**



###1.3、同步方法：使用synchronized修饰的方法

```java
public synchronized void method(){
    ....
}
//等价下面
public /*synchronized*/ void method(){
    synchronized(this){
         ....
    }
}

------------------------------
public static synchronized void method1(){
    ...
}

//等价于
public static /*synchronized*/ void method(){
    synchronized(类名.class){
         ....
    }
}

```

> 同步锁是谁呢？
>
> 对于非static方法，同步锁就是this；
>
> 对于static方法，我们使用当前方法所在类的字节码对象（类名.class）为锁对象；

###1.3、Lock锁

java.util.concurrent.locks.Lock接口
Lock 实现提供了比使用 synchronized 方法和语句可获得的更广泛的锁定操作。
Lock接口中的方法:
​    void lock()获取锁。
​    void unlock()  释放锁。
java.util.concurrent.locks.ReentrantLock implements Lock接口

使用步骤:

1. 在成员位置创建一个ReentrantLock对象;
2. 在可能会出现安全问题的代码前调用Lock接口中的方法lock获取锁
3. 在可能会出现安全问题的代码后调用Lock接口中的方法unlock释放锁

```java
public class RunnableImpl implements Runnable{
    //定义一个多个线程共享的票源
    private  int ticket = 100;

    //1.在成员位置创建一个ReentrantLock对象
    Lock l = new ReentrantLock();

    //设置线程任务:卖票
    @Override
    public void run() {
        //使用死循环,让卖票操作重复执行
        while(true){
            //2.在可能会出现安全问题的代码前调用Lock接口中的方法lock获取锁
            l.lock();

            //先判断票是否存在
            if(ticket>0){
                //提高安全问题出现的概率,让程序睡眠
                try {
                    Thread.sleep(10);
                    //票存在,卖票 ticket--
                    System.out.println(Thread.currentThread().getName()+"-->正在卖第"+ticket+"张票");
                    ticket--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    //3.在可能会出现安全问题的代码后调用Lock接口中的方法unlock释放锁
                    l.unlock();//无论程序是否异常,都会把锁释放
                }
            }
        }
    }
}   
```



##二、线程状态

### 2.1、概述( 6种状态 )

| 线程状态               | 导致状态发生条件                                             |
| ---------------------- | ------------------------------------------------------------ |
| NEW(新建)              | 线程刚被创建，但是并未启动。还没调用start方法。              |
| Runnable(可运行)       | 线程可以在java虚拟机中运行的状态，可能正在运行自己代码，也可能没有，这取决于操作系统处理。 |
| Blocked(锁阻塞)        | 当一个线程试图获取一个对象锁，而该对象锁被其他的线程持有，则该线程进入Blocked状态；当该线程持有锁时，该线程将变成Runnable状态。 |
| Waiting(无限等待)      | 一个线程在等待另一个线程执行一个（唤醒）动作时，该线程进入Waiting状态。进入这个状态后是不能自动唤醒的，必须等待另一个线程调用notify或者notifyAll方法才能够唤醒。 |
| TimedWaiting(计时等待) | 同waiting状态，有几个方法有超时参数，调用他们将进入Timed Waiting状态。这一状态将一直保持到超时期满或者接收到唤醒通知。带有超时参数的常用方法有Thread.sleep 、Object.wait。 |
| Teminated(被终止)      | 因为run方法正常退出而死亡，或者因为没有捕获的异常终止了run方法而死亡。 |

### 2.2、Timed Waiting （计时等待）

sleep状态，只对自身相关

### 2.3、BLOCKED （锁阻塞）

同步锁，由其它线程影响而来；或者CPU资源不够

### 2.4、Waiting（无限等待）

```java
public class DemoWaitNotifyTest {
    public static void main(String[] args) {
        //  创建同步锁，使用多线程只有一个在执行
        Object obj = new Object();
        // 创建等待线程
        new Thread(){
            @Override
            public void run() {
                synchronized (obj){
                    try {
                        System.out.println("准备进入线程等待。。。。。。");
                        obj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("等待线程已被唤醒！！！！！");
                }
                System.out.println("等待线程over!!!!");
            }
        }.start();

        //创建唤醒线程
        new Thread(){
            @Override
            public void run() {
                synchronized (obj){
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("唤醒等待线程!!!!");
                    obj.notify();
                }
                System.out.println("唤醒线程over!!!!");
            }
        }.start();
    }
}
```

> 1. wait(5000) : 这个在没有特定唤醒时，过5s后自动苏醒，相当于sleep；
> 2. **notify():// 若有多个等待线程，就随机唤醒一个单线程；若都唤醒则使用notifyAll()方法；**



##三、等待唤醒机制

### 3.1、线程间通信

**概念：**多个线程在处理同一个资源，但是处理的动作（线程的任务）却不相同。

**为什么要处理线程间通信：**

多个线程并发执行时, 在默认情况下CPU是随机切换线程的，当我们需要多个线程来共同完成一件任务，并且我们希望他们有规律的执行, 那么多线程之间需要一些协调通信，以此来帮我们达到多线程共同操作一份数据。

**如何保证线程间通信有效利用资源：**

多个线程在处理同一个资源，并且任务不同时，需要线程通信来帮助解决线程之间对同一个变量的使用或操作。 就是多个线程在操作同一份数据时， 避免对同一共享变量的争夺。也就是我们需要通过一定的手段使各个线程能有效的利用资源。而这种手段即—— **等待唤醒机制。**

###3.2、等待唤醒机制

**什么是等待唤醒机制**？

这是多个线程间的一种**协作**机制。谈到线程我们经常想到的是线程间的**竞争（race）**，比如去争夺锁，但这并不是故事的全部，线程间也会有协作机制。就好比在公司里你和你的同事们，你们可能存在在晋升时的竞争，但更多时候你们更多是一起合作以完成某些任务。

就是在一个线程进行了规定操作后，就进入等待状态（**wait()**）， 等待其他线程执行完他们的指定代码过后 再将其唤醒（**notify()**）;在有多个线程进行等待时， 如果需要，可以使用 notifyAll()来唤醒所有的等待线程。

wait/notify 就是线程间的一种协作机制。

**等待唤醒中的方法**

等待唤醒机制就是用于解决线程间通信的问题的，使用到的3个方法的含义如下：

1. wait：线程不再活动，不再参与调度，进入 wait set 中，因此不会浪费 CPU 资源，也不会去竞争锁了，这时的线程状态即是 WAITING。它还要等着别的线程执行一个**特别的动作**，也即是“**通知（notify）**”在这个对象上等待的线程从wait set 中释放出来，重新进入到调度队列（ready queue）中
2. notify：则选取所通知对象的 wait set 中的一个线程释放；例如，餐馆有空位置后，等候就餐最久的顾客最先入座。
3. notifyAll：则释放所通知对象的 wait set 上的全部线程。

> 注意：
>
> 哪怕只通知了一个等待的线程，被通知线程也不能立即恢复执行，因为它当初中断的地方是在同步块内，而此刻它已经不持有锁，所以她需要再次尝试去获取锁（很可能面临其它线程的竞争），成功后才能在当初调用 wait 方法之后的地方恢复执行。
>
> 总结如下：
>
> - 如果能获取锁，线程就从 WAITING 状态变成 RUNNABLE 状态；
> - 否则，从 wait set 出来，又进入 entry set，线程就从 WAITING 状态又变成 BLOCKED 状态

**调用wait和notify方法需要注意的细节**

1. wait方法与notify方法必须要由同一个锁对象调用。因为：对应的锁对象可以通过notify唤醒使用同一个锁对象调用的wait方法后的线程。
2. wait方法与notify方法是属于Object类的方法的。因为：锁对象可以是任意对象，而任意对象的所属类都是继承了Object类的。
3. wait方法与notify方法必须要在同步代码块或者是同步函数中使用。因为：必须要通过锁对象调用这2个方法。



##四、线程池

如果并发的线程数量很多，并且每个线程都是执行一个时间很短的任务就结束了，这样频繁创建线程就会大大降低系统的效率，因为频繁创建线程和销毁线程需要时间。

那么有没有一种办法使得线程可以复用，就是执行完一个任务，并不被销毁，而是可以继续执行其他的任务？

在Java中可以通过线程池来达到这样的效果。今天我们就来详细讲解一下Java的线程池。

###4.1、概念

- **线程池：**其实就是一个容纳多个线程的容器，其中的线程可以反复使用，省去了频繁创建线程对象的操作，无需反复创建线程而消耗过多资源。

合理利用线程池能够带来三个好处：

1. 降低资源消耗。减少了创建和销毁线程的次数，每个工作线程都可以被重复利用，可执行多个任务。
2. 提高响应速度。当任务到达时，任务可以不需要的等到线程创建就能立即执行。
3. 提高线程的可管理性。可以根据系统的承受能力，调整线程池中工作线线程的数目，防止因为消耗过多的内存，而把服务器累趴下(每个线程需要大约1MB内存，线程开的越多，消耗的内存也就越大，最后死机)。

###4.2、线程池的使用

线程池:JDK1.5之后提供了`java.util.concurrent.Executors`:线程池的工厂类,用来生成线程池;

- Executors类中的静态方法:

  static ExecutorService newFixedThreadPool(int nThreads) 创建一个可重用固定线程数的线程池
  ​    参数:int nThreads:创建线程池中包含的线程数量
  ​    返回值:ExecutorService接口,返回的是ExecutorService接口的实现类对象,我们可以使用ExecutorService接口接收(面向接口编程)

- java.util.concurrent.ExecutorService:线程池接口

  用来从线程池中获取线程,调用start方法,执行线程任务
  ​        submit(Runnable task) 提交一个 Runnable 任务用于执行

  关闭/销毁线程池的方法
  ​        void shutdown()

- 线程池的使用步骤:

1. 使用线程池的工厂类Executors里边提供的静态方法newFixedThreadPool生产一个指定线程数量的线程池;
2. 创建一个类,实现Runnable接口,重写run方法,设置线程任务;
3. 调用ExecutorService中的方法submit,传递线程任务(实现类),开启线程,执行run方法
4. 调用ExecutorService中的方法shutdown销毁线程池(不建议执行)

```java
public class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("我要一个教练");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("教练来了： " + Thread.currentThread().getName());
        System.out.println("教我游泳,交完后，教练回到了游泳池");
    }
}

public class ThreadPoolDemo {
    public static void main(String[] args) {
        // 创建线程池对象
        ExecutorService service = Executors.newFixedThreadPool(2);//包含2个线程对象
        // 创建Runnable实例对象
        MyRunnable r = new MyRunnable();

        //自己创建线程对象的方式
        // Thread t = new Thread(r);
        // t.start(); ---> 调用MyRunnable中的run()

        // 从线程池中获取线程对象,然后调用MyRunnable中的run()
        service.submit(r);
        // 再获取个线程对象，调用MyRunnable中的run()
        service.submit(r);
        service.submit(r);
        // 注意：submit方法调用结束后，程序并不终止，是因为线程池控制了线程的关闭。
        // 将使用完的线程又归还到了线程池中
        // 关闭线程池
        //service.shutdown();
    }
}
```

##五、Lambda表达式

### 5.1 函数式编程思想概述

在数学中，**函数**就是有输入量、输出量的一套计算方案，也就是“拿什么东西做什么事情”。相对而言，面向对象过分强调“必须通过对象的形式来做事情”，而函数式思想则尽量忽略面向对象的复杂语法——**强调做什么，而不是以什么形式做**。

面向对象的思想:

​	做一件事情,找一个能解决这个事情的对象,调用对象的方法,完成事情；

函数式编程思想:

​	只要能获取到结果,谁去做的,怎么做的都不重要,重视的是结果,不重视过程；



### 5.2、冗余的Runnable代码

```java
public class Demo01Runnable {
	public static void main(String[] args) {
    	// 匿名内部类
		Runnable task = new Runnable() {
			@Override
			public void run() { // 覆盖重写抽象方法
				System.out.println("多线程任务执行！");
			}
		};
		new Thread(task).start(); // 启动线程
	}
}
```

####代码分析

对于`Runnable`的匿名内部类用法，可以分析出几点内容：

- `Thread`类需要`Runnable`接口作为参数，其中的抽象`run`方法是用来指定线程任务内容的核心；
- 为了指定`run`的方法体，**不得不**需要`Runnable`接口的实现类；
- 为了省去定义一个`RunnableImpl`实现类的麻烦，**不得不**使用匿名内部类；
- 必须覆盖重写抽象`run`方法，所以方法名称、方法参数、方法返回值**不得不**再写一遍，且不能写错；
- 而实际上，**似乎只有方法体才是关键所在**。

### 5.3、Lambda的更优化写法

借助Java 8的全新语法，上述`Runnable`接口的匿名内部类写法可以通过更简单的Lambda表达式达到等效：

```java
    public static void main(String[] args) {
        // 方案1 原始标准化
//        Runnable run = new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("DUO XIAN CHENG QIDONG LE !");
//            }
//        };
//
//        new Thread(run,"lolo").start();

        // 方案2 匿名内部类
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("DUO XIAN CHENG QIDONG LE !");
//            }
//        }).start();

        // 方案3 Lambda 表达式
        new Thread(()->{
            System.out.println("DUO XIAN CHENG QIDONG LE !");
        }).start();
        
    }
```

>**回顾匿名类：匿名内部类的好处与弊端**
>
>一方面，匿名内部类可以帮我们**省去实现类的定义**；
>
>另一方面，匿名内部类的语法——**确实太复杂了！**

###5.4 、Lambda标准格式

Lambda省去面向对象的条条框框，格式由**3个部分**组成：

- 一些参数
- 一个箭头
- 一段代码

Lambda表达式的**标准格式**为：

```
(参数类型 参数名称) -> { 代码语句 }
```

格式说明：

- 小括号内的语法与传统方法参数列表一致：无参数则留空；多个参数则用逗号分隔。
- `->`是新引入的语法格式，代表指向动作。
- 大括号内的语法与传统方法体要求基本一致。

```java
public class LambdaTest {

    public static void main(String[] args) {
        
        // 标准写法
        invokeCook(() -> {
            System.out.println("吃饭啦！");
        });
        // 简版写法
        invokeCook(() ->System.out.println(" 做饭了！！！！！"));
        
        
        // 标准写法
       invokeCalculate((int a,int b)->{
           return a+b
       },13,14);
        
       // 简版写法
//        System.out.println(invokeCalculate(12,13,(a,b)->a+b));
        invokeCalculate((a,b)->a+b,12,13);
        
    }

    public static void invokeCook(Cook cook){
        cook.makeFood();
    }

    static int invokeCalculate(Calculate calculate,int a,int b){
        return calculate.measure(a,b);
    }
}

```

####省略规则

在Lambda标准格式的基础上，使用省略写法的规则为：

1. 小括号内参数的类型可以省略；
2. 如果小括号内**有且仅有一个参**，则小括号可以省略；
3. 如果大括号内**有且仅有一个语句**，则无论是否有返回值，都可以省略大括号、return关键字及语句分号。

> 备注：掌握这些省略规则后，请对应地回顾本章开头的多线程案例。

###5.5、Lambda的使用前提

Lambda的语法非常简洁，完全没有面向对象复杂的束缚。但是使用时有几个问题需要特别注意：

1. 使用Lambda必须具有接口，且要求**接口中有且仅有一个抽象方法**。
   无论是JDK内置的`Runnable`、`Comparator`接口还是自定义的接口，只有当接口中的抽象方法存在且唯一时，才可以使用Lambda。
2. 使用Lambda必须具有**上下文推断**。
   也就是方法的参数或局部变量类型必须为Lambda对应的接口类型，才能使用Lambda作为该接口的实例。

> 备注：有且仅有一个抽象方法的接口，称为“**函数式接口**”。