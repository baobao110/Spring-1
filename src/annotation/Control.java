package annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)

@Retention(RetentionPolicy.RUNTIME)

public @interface Control {
	
	public String value();
	
}

/*
 * 注意这里注解的作用和如何定义注解，注解的定义方法可以查看注解的文档
 * 这里的注解是配合control使用的，这样根据注解名获取类,因此注解的value()
 * 尽量取得特殊一点，这样方便后面的使用,这里的注解值作为后面的键值对的键使用,
 * 在control包中将必要的control类加类的注解，这样在获取编译后的.class文件就方便
 * 获取类和对象
 */