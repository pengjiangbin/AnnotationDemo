# AnnotationDemo
运行时注解与编译时注解Demo

#### @interface
> 声名注解类的关键字，使用该注解表示自动继承java.lang.annotation.Annotation类，继承过程由编译器完成

#### @Retention
> 该注解用于定义注解保留策略，即注解类在什么阶段存在（源码、编译后、运行期）

@Retention有如下几个参数：
- @Retention(RetentionPolicy.SOURCE)

注解在源码中保留，class文件中不存在 

- @Retention(RetentionPolicy.CLASS)

注解在源码和class文件中都存在，但是在运行时不存在，即运行时无法获得

- @Retention(RetentionPolicy.RUNTIME)

注解在源码、class文件中存在并且在运行时可以通过反射机制获取到


#### @Target
> 用于定义注解的作用目标

参数名称 | 含义
---|---
@Target(ElementType.TYPE) | 用于接口、类、枚举
@Target(ElementType.FIELD) | 用于字段、枚举常量
@Target(ElementType.METHOD) | 用于方法
@Target(ElementType.PARAMETER) | 用于方法参数
@Target(ElementType.CONSTRUCTOR) | 用于构造函数
@Target(ElementType.LOCAL_VARIABLE) | 用于局部变量
@Target(ElementType.ANNOTATION_TYPE) | 用于注解
@Target(ElementType.PACKAGE) | 用于包


#### @Inherited
默认情况下，自定义注解用在父类上不会被子类所继承，如果想让子类继承父类的注解，即注解在子类上也生效

#### @Documented
用于描述其他类型的annotation应该被javadoc文档化，出现在api doc中

### 自定义注解

##### 注解格式
```
public @interface 注解名 {定义体}
```
定义体为方法的集合，每个方法是声名了一个配置参数，方法的名称作为配置参数的名称，方法的返回值类型是配置参数的类型，和普通方法不一样，可以通过default关键字来声名配置参数的默认值。

需要注意的是：
> 1. 此处只能使用public或者默认default权限修饰符
> 2. 配置参数的类型只能使用基本类型和String、Enum、C lass、annotation
> 3. 对于只含有一个配置参数的注解，参数名建议设置为v> alue，即方法名为value
> 4. 配置参数一旦设置，其参数值必须有确定的值,要不在使用注解时指定，或者在定义注解时使用default为其设置默认值，对于非基本类型值不能为null

自定义注解示例：
```
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserMate{
    public int id() default 0;
    public String name() default "";
    public int age() default 0;
}
```

### 注解处理器

#### 运行时注解处理器
在Java中，我们可以通过反射获取某个类中的AnnotatedElement对象，然后通过该对象提供的方法访问Annotation信息，以下为常用的方法：

方法 | 含义
--- |---
<T extends Annotation> T getAnnotation(Class<T> annotationClass) | 返回该元素上指定类型的注解
Annotation[] getAnnotations() | 返回存在该元素上的所有注解
default <T extends Annotaion> T[] getAnnotationsByType(Class<T> annotationClass) | 返回该元素指定类型的注解
default <T extends Annotaion> T getDeclaredAnnotation(Class<T> annotationClass) | 返回直接存在该元素上的所有注解
default <T extends Annotaion> T getDeclaredAnnotationsByType(Class<T> annotationClass) | 返回直接存在该元素上的某类型注解
Annotation[] getDeclaredAnnotations() | 返回存在该元素上的所有注解


#### 运行时注解示例：

用户类：
```
public class User {
    private int id;
    private int age;
    private String name;

    @UserMate(id = 1, name = "kobe", age = 20)
    public User() {
    }

    public User(int id, int age, String name) {
        this.id = id;
        this.age = age;
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
```

自定义注解：
```
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserMate {
    int id() default 0;

    String name() default "";

    int age() default 0;
}
```

注解处理器：
```
public class AnnotationProcessor {

    public static void init(Object obj) {
        if (!(obj instanceof User)) {
            throw new IllegalArgumentException("[" + obj.getClass().getSimpleName() + "] is not type of User");
        }
        Constructor<?>[] constructors = obj.getClass().getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (!constructor.isAnnotationPresent(UserMate.class)) {
                continue;
            }
            UserMate annotation = constructor.getAnnotation(UserMate.class);
            int id = annotation.id();
            int age = annotation.age();
            String name = annotation.name();
            User user = (User) obj;
            user.setId(id);
            user.setAge(age);
            user.setName(name);
        }
    }
}
```

测试代码：
```
 User user = new User();
 AnnotationProcessor.init(user);
 Log.d(TAG, user.toString());
```

输出结果：
```
User{id=1, age=20, name='kobe'}
```

#### 编译时注解处理器
APT（Annotation Processor Tool）用于编译时期扫描和处理注解信息。一个特定的注解处理器可以以java源码文件或编译后的class文件作为输入，然后输出另一些文件，可以是java文件，也可以是class文件，通常输出的是java文件。如果输出的是java文件，这些java文件会和其他源码文件一起被javac编译

注解处理是的介入阶段：在javac开始编译之前

#### 自定义注解处理器
```
public class MyPorcessor extends AbstractProcessor {

        @Override
        public Set<String> getSupportedAnnotationTypes() {
            return super.getSupportedAnnotationTypes();
        }

        @Override
        public SourceVersion getSupportedSourceVersion() {
            return super.getSupportedSourceVersion();
        }

        @Override
        public synchronized void init(ProcessingEnvironment processingEnv) {
            super.init(processingEnv);
        }

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            return false;
        }
    }
```


方法 | 作用
---|---
init(ProcessingEnvironment processingEnv) | 由注解处理器自动调用，启动ProcessingEnvironment提供了许多工具类：Filter、Type、Elements、Messager等
getSupportedAnnotationTypes() | 返回的字符串集合表示处理器用于处理哪些注解
getSupportedSourceVersion()| 用来指定支持的Java版本
process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) | 用于扫描和处理注解，以及最终生成java文件，需要深入的是RoundEnvironment类，用于查找出程序元素上使用的注解

编写注解处理器需要对ProcessingEnvironment和RoundEnvironment非常熟悉。

- ProcessingEnvironment
```
public interface ProcessingEnvironment {
    Map<String, String> getOptions();

    Messager getMessager();

    Filer getFiler();

    Elements getElementUtils();

    Types getTypeUtils();

    SourceVersion getSourceVersion();

    Locale getLocale();
}
```

- RoundEnvironment

```
public interface RoundEnvironment {
    boolean processingOver();

    boolean errorRaised();

    Set<? extends Element> getRootElements();

    Set<? extends Element> getElementsAnnotatedWith(TypeElement var1);

    Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> var1);
}
```

