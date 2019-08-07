

# Springboot / Spring Cloud 依赖说明

### 一、Springboot 工程

### 1.1、spring-boot-starter-parent

一个springboot项目工程，在对应工程的pom.xml , 都会有一个parent spring-boot 指定了springboot的vesrion。

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.1.RELEASE</version>
</parent>
```

然后，alt 点击`spring-boot-starter-parent`进入 spring-boot 原始工程的pom.xml

```xml
<?xml version="1.0" encoding="utf-8"?><project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>2.0.1.RELEASE</version>
        <relativePath>../../spring-boot-dependencies</relativePath>
    </parent>
    <artifactId>spring-boot-starter-parent</artifactId>
    <packaging>pom</packaging>
    <name>Spring Boot Starter Parent</name>
    <description>Parent pom providing dependency and plugin management for applications
		built with Maven</description>
    <url>https://projects.spring.io/spring-boot/#/spring-boot-starter-parent</url>
    <properties>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <resource.delimiter>@</resource.delimiter>
        <maven.compiler.source>${java.version}</maven.compiler.source>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.target>${java.version}</maven.compiler.target>
    </properties>
    <build>
        <resources>
            <resource>
                <filtering>true</filtering>
                <directory>${basedir}/src/main/resources</directory>
                <includes>
                    <include>**/application*.yml</include>
                    <include>**/application*.yaml</include>
                    <include>**/application*.properties</include>
                </includes>
            </resource>
            <resource>
                <directory>${basedir}/src/main/resources</directory>
                <excludes>
                    <exclude>**/application*.yml</exclude>
                    <exclude>**/application*.yaml</exclude>
                    <exclude>**/application*.properties</exclude>
                </excludes>
            </resource>
        </resources>
        <pluginManagement>
            <plugins>
              ....
            </plugins>
        </pluginManagement>
    </build>
</project>
```

这个文件主要有3部分信息：

##### 1、 spring-boot-dependencies 这个工程就指定了spring-boot所有依赖的版本，alt进入即可查看；

```XML
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-dependencies</artifactId>
    <version>2.0.1.RELEASE</version>
    <relativePath>../../spring-boot-dependencies</relativePath>
</parent>
```

2、build的resource资源读取

```xml
<resources>
    <resource>
        <filtering>true</filtering>
        <directory>${basedir}/src/main/resources</directory>
        <includes>
            <include>**/application*.yml</include>
            <include>**/application*.yaml</include>
            <include>**/application*.properties</include>
        </includes>
    </resource>
    <resource>
        <directory>${basedir}/src/main/resources</directory>
        <excludes>
            <exclude>**/application*.yml</exclude>
            <exclude>**/application*.yaml</exclude>
            <exclude>**/application*.properties</exclude>
        </excludes>
    </resource>
```

> ##### 注意： 配置文件读取顺序，最后是读取properties， 所有properties的信息会覆盖yml的同名信息；
>
> ##### 问题：为啥要写2次？ 上面还加了一个filtering ? 待深入探讨！！！

3、build的plugins管理；



### 1.2、spring-boot 依赖添加说明

上面的spring-boot-parent 只是一个空壳，只是定义一系列的规范：**指定其他jar依赖的版本信息；**

- #### 如我们的工程是一个web项目，就必须引入：

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  ```

  > #### 说明：
  >
  > 1、spring-boot-starter-web： 他其实也是集合jar, 里面主要包含了 tomcat, spring-webmvc。 这个在spring-boot-parent的spring-boot-dependencies 工程中，已通过dependencies-mangent 管理起来；所以直接拿来用，无需指定版本信息；
  >
  > ```xml
  > <?xml version="1.0" encoding="utf-8"?><project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  >     <modelVersion>4.0.0</modelVersion>
  >     <groupId>org.springframework.boot</groupId>
  >     <artifactId>spring-boot-dependencies</artifactId>
  >     <version>2.0.1.RELEASE</version>
  >    ...
  >     <properties>
  >         <activemq.version>5.15.3</activemq.version>
  >         <antlr2.version>2.7.7</antlr2.version>
  >         <appengine-sdk.version>1.9.63</appengine-sdk.version>
  >        ...
  >     </properties>
  >     <dependencyManagement>
  >         <dependencies>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-test</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-configuration-processor</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-devtools</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-loader</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-loader-tools</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-activemq</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-actuator</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-amqp</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-aop</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-data-jpa</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-data-mongodb</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-data-redis</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-data-rest</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-data-solr</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-freemarker</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >            
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-jdbc</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-json</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >            
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-logging</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-security</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-test</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-tomcat</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.springframework.boot</groupId>
  >                 <artifactId>spring-boot-starter-web</artifactId>
  >                 <version>2.0.1.RELEASE</version>
  >             </dependency>
  >           ...
  >             <dependency>
  >                 <groupId>org.thymeleaf</groupId>
  >                 <artifactId>thymeleaf</artifactId>
  >                 <version>${thymeleaf.version}</version>
  >             </dependency>
  >             <dependency>
  >                 <groupId>org.thymeleaf</groupId>
  >                 <artifactId>thymeleaf-spring5</artifactId>
  >                 <version>${thymeleaf.version}</version>
  >             </dependency>
  >         </dependencies>
  >     </dependencyManagement>
  >     <build>
  >         <pluginManagement>
  >             <plugins>
  >                 <plugin>
  >                     <groupId>org.jetbrains.kotlin</groupId>
  >                     <artifactId>kotlin-maven-plugin</artifactId>
  >                     <version>${kotlin.version}</version>
  >                 </plugin>
  >                 <plugin>
  >                     <groupId>org.jooq</groupId>
  >                     <artifactId>jooq-codegen-maven</artifactId>
  >                     <version>${jooq.version}</version>
  >                 </plugin>
  >                 <plugin>
  >                     <groupId>org.springframework.boot</groupId>
  >                     <artifactId>spring-boot-maven-plugin</artifactId>
  >                     <version>2.0.1.RELEASE</version>
  >                 </plugin>
  >                 ...
  >             </plugins>
  >         </pluginManagement>
  >     </build>
  > </project>
  > ```
  >
  > 2、名称说明：spring-boot-starter 是所有jar的一个起点，后面的web代表项目 (jar) 具体名称；
  >
  > 从spring-boot-starter-web.jar又具体依赖下列 就很容易看来： 
  >
  > **（即后面所有对应依赖大多都需要spring-boot-starter名开头 ，当然不是spring-boot指定的jar，而是外界直接引用则需要指定version）**
  >
  > ##### 3、总之、引入的jar无需指定version的是在spring-boot-dependencies指定了。同时，一般命名都一spring-boot-starter开头，需要这个spring-boot-starter.jar做一个引擎一样。反之，则不然；
  >
  > ```xml
  > <dependencies>
  >     <!--热部署配置-->
  >     <dependency>
  >         <groupId>org.springframework.boot</groupId>
  >         <artifactId>spring-boot-devtools</artifactId>
  >     </dependency>
  >     <dependency>
  >         <groupId>org.springframework.boot</groupId>
  >         <artifactId>spring-boot-starter-web</artifactId>
  >     </dependency>
  >     <!--配置执行器-->
  >     <dependency>
  >         <groupId>org.springframework.boot</groupId>
  >         <artifactId>spring-boot-configuration-processor</artifactId>
  >         <optional>true</optional>
  >     </dependency>
  >      <!--mybatis没有指定，需要指定version，但是需要spring-boot-starter引擎，故命名在后-->
  >     <dependency>
  >         <groupId>org.mybatis.spring.boot</groupId>
  >         <artifactId>mybatis-spring-boot-starter</artifactId>
  >         <version>1.1.1</version>
  >     </dependency>
  > </dependencies>
  > ```
  >
  > ```xml
  > <dependencies>
  >     <dependency>
  >       <groupId>org.springframework.boot</groupId>
  >       <artifactId>spring-boot-starter</artifactId>
  >       <version>2.0.1.RELEASE</version>
  >       <scope>compile</scope>
  >     </dependency>
  >     <dependency>
  >       <groupId>org.springframework.boot</groupId>
  >       <artifactId>spring-boot-starter-json</artifactId>
  >       <version>2.0.1.RELEASE</version>
  >       <scope>compile</scope>
  >     </dependency>
  >     <dependency>
  >       <groupId>org.springframework.boot</groupId>
  >       <artifactId>spring-boot-starter-tomcat</artifactId>
  >       <version>2.0.1.RELEASE</version>
  >       <scope>compile</scope>
  >     </dependency>
  >     <dependency>
  >       <groupId>org.hibernate.validator</groupId>
  >       <artifactId>hibernate-validator</artifactId>
  >       <version>6.0.9.Final</version>
  >       <scope>compile</scope>
  >     </dependency>
  >     <dependency>
  >       <groupId>org.springframework</groupId>
  >       <artifactId>spring-web</artifactId>
  >       <version>5.0.5.RELEASE</version>
  >       <scope>compile</scope>
  >     </dependency>
  >     <dependency>
  >       <groupId>org.springframework</groupId>
  >       <artifactId>spring-webmvc</artifactId>
  >       <version>5.0.5.RELEASE</version>
  >       <scope>compile</scope>
  >     </dependency>
  >   </dependencies>
  > ```



### 二、Spring-Cloud

通过上面spring-boot认知，Spring-Cloud依赖也类似。

我们工程若要用到spring-cloud框架，可以先在项目的parent pom.xml 依赖管理定义好spring-cloud。

#### xc-framework-parent pom.xml

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Finchley.SR1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

上面我们就指定了spring-cloud版本为 Finchley.SR1。 

至于Finchley.SR1 是哪个版本，以及配合的是哪个spring-boot版本都有说明，可以在官网查看。 



#### 2.1、 具体的spring-cloud-xx.jar依赖添加说明：

比如：在工程引入 spring-cloud-starter-netflix-eureka-client 

```XML
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

我们查看 spring-cloud-dependencies pom.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-dependencies-parent</artifactId>
		<version>2.0.3.RELEASE</version>
		<relativePath/>
	</parent>
	<artifactId>spring-cloud-dependencies</artifactId>
	<version>Finchley.SR1</version>
	<name>spring-cloud-dependencies</name>
	<description>Spring Cloud Dependencies</description>
	<packaging>pom</packaging>
	<properties>
		<main.basedir>${basedir}/../..</main.basedir>
		<spring-cloud-aws.version>2.0.0.RELEASE</spring-cloud-aws.version>
		<spring-cloud-bus.version>2.0.0.RELEASE</spring-cloud-bus.version>
		<spring-cloud-cloudfoundry.version>2.0.0.RELEASE</spring-cloud-cloudfoundry.version>
		<spring-cloud-commons.version>2.0.1.RELEASE</spring-cloud-commons.version>
		<spring-cloud-config.version>2.0.1.RELEASE</spring-cloud-config.version>
		<spring-cloud-consul.version>2.0.1.RELEASE</spring-cloud-consul.version>
		<spring-cloud-contract.version>2.0.1.RELEASE</spring-cloud-contract.version>
		<spring-cloud-function.version>1.0.0.RELEASE</spring-cloud-function.version>
		<spring-cloud-gateway.version>2.0.1.RELEASE</spring-cloud-gateway.version>
		<spring-cloud-netflix.version>2.0.1.RELEASE</spring-cloud-netflix.version>
		<spring-cloud-openfeign.version>2.0.1.RELEASE</spring-cloud-openfeign.version>
		<spring-cloud-security.version>2.0.0.RELEASE</spring-cloud-security.version>
		<spring-cloud-sleuth.version>2.0.1.RELEASE</spring-cloud-sleuth.version>
		<spring-cloud-stream.version>Elmhurst.SR1</spring-cloud-stream.version>
		<spring-cloud-task.version>2.0.0.RELEASE</spring-cloud-task.version>
		<spring-cloud-vault.version>2.0.1.RELEASE</spring-cloud-vault.version>
		<spring-cloud-zookeeper.version>2.0.0.RELEASE</spring-cloud-zookeeper.version>
	</properties>
	<dependencyManagement>
		<dependencies>
	        <!-- bom dependencies at the bottom so they can be overridden above -->
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-commons-dependencies</artifactId>
				<version>${spring-cloud-commons.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-netflix-dependencies</artifactId>
				<version>${spring-cloud-netflix.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-stream-dependencies</artifactId>
				<version>${spring-cloud-stream.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-task-dependencies</artifactId>
				<version>${spring-cloud-task.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-config-dependencies</artifactId>
				<version>${spring-cloud-config.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-function-dependencies</artifactId>
				<version>${spring-cloud-function.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-gateway-dependencies</artifactId>
				<version>${spring-cloud-gateway.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-consul-dependencies</artifactId>
				<version>${spring-cloud-consul.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-sleuth-dependencies</artifactId>
				<version>${spring-cloud-sleuth.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-vault-dependencies</artifactId>
				<version>${spring-cloud-vault.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-zookeeper-dependencies</artifactId>
				<version>${spring-cloud-zookeeper.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-security-dependencies</artifactId>
				<version>${spring-cloud-security.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-cloudfoundry-dependencies</artifactId>
				<version>${spring-cloud-cloudfoundry.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-bus-dependencies</artifactId>
				<version>${spring-cloud-bus.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-contract-dependencies</artifactId>
				<version>${spring-cloud-contract.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-aws-dependencies</artifactId>
				<version>${spring-cloud-aws.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-openfeign-dependencies</artifactId>
				<version>${spring-cloud-openfeign.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
	<profiles>
		<profile>
			<id>spring</id>
			<repositories>
				<repository>
					<id>spring-snapshots</id>
					<name>Spring Snapshots</name>
					<url>https://repo.spring.io/libs-snapshot-local</url>
					<snapshots>
						<enabled>true</enabled>
					</snapshots>
				</repository>
				...
		</profile>
	</profiles>
</project>
```

#### 注意：依赖管理并没有具体指定 spring-cloud-starter-netflix-eureka-client 的版本！

而是泛指了spring-cloud框架 几个大范围集成的version! 这点与spring-boot是有些不一致的。  所以说spring-cloud 与 spring-boot 依赖管理类似，而非相同！

那么，我们项目工程引用的spring-cloud-starter-netflix-eureka-client 版本，其实就是对应下面：

```XML
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-netflix-dependencies</artifactId>
    <version>${spring-cloud-netflix.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

> - org.springframework.cloud:spring-cloud-netflix-eureka-client:2.0.1.RELEASE 
>
> 我们同样发现，spring-cloud-starter命名开头，也就是使用spring-cloud-netflix-eureka-client，也需spring-cloud-starter作为引擎一样；















