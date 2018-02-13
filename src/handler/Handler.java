package handler;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.lang.*;

import annotation.Annotation;
import annotation.Control;
import annotation.Method;
import annotation.Server;
import control.cardControl;
import control.control;
import control.userControl;

public class Handler {
	
	private static final Map<String,Object> map=new HashMap<String,Object>();//这里用键值对的形式保存不同的control对象
	private static final List<File>fileList=new ArrayList<File>();
	private static final List<File>pick=new ArrayList<File>();
	private static final List<String>str=new ArrayList<String>();

	public static void init(String root) {
		loadfiles(root);
	}
	
	private static void find(File file) {
		if(null==file) {
			return;
		}
		if(file.isFile()) {
			fileList.add(file);
			return;
		}
		File[] list=file.listFiles();
		for(File i:list) {
			find(i);
		}
	}
	/*
	 * 这里注意find()方法,这里先放入classes文件目录,find方法的作用是遍历classes里面的所有方法,
	 * 这里利用了递归的操作,用递归时要特别注意什么时候递归结束,比如这里是文件为null或者已经找到文件时结束
	 * 这里用一个集合保存所有的文件
	 */
	
	private static void pick() {
		for(File i:fileList) {
			if(i.getName().endsWith("class")) {
				pick.add(i);
			}
		}
	}
	/*
	 * 这里的pick()方法的作用是在find()方法已经找到的所有文件中再次进行筛选,保存以.class为结尾的
	 * 文件,将它们保存,其实就是找源java文件编译后的字节码文件
	 */
	
	private static void list() {
		for(String i:str) {
			System.out.println(i);
		}
	}
	
	private static void str() {
		for(File i:pick) {
			String x=i.getPath();
			str.add(x.substring(x.indexOf("classes")+8, x.length()));
		}
	}
	//这里就是获取源文件的路径名，但是这里需要z注意getPath()方法将路径名以字符串的形式保存
	private static void loadfiles(String root) {
		File file=new File(root+"/WEB-INF/classes");//这里先找到classes即字节码文件的父目录
		find(file);
		pick();
		str();
		List<String> xx=new ArrayList<String>();//获取字节码文件全名
		for(String i:str) {
			xx.add(i.substring(0,i.length()-6));//获取源文件的绝对路径
		}
		List<String> xxx=new ArrayList<String>();
		for(String i:xx) {
			String m=i.replace("\\", ".");//将路径的"\\"转化为"." 其实也是获取路径名，这里参考导包操作
			xxx.add(m);
		}
		for(String i:xxx) {
			try {
				Class cls=Class.forName(i);//根据文件路径获取类
				if(Modifier.isAbstract(cls.getModifiers())) {
					continue;
				}//抽象类的判断
			java.lang.annotation.Annotation[] a=cls.getAnnotations();//获取该类的所以注解,这里用数组的方法是不确定注解的类型,需要后面判断,其实这里可以省略
			if(null==a) {
				continue;
			}
			else {
				for(java.lang.annotation.Annotation i1:a) {
					String msg;
					Object object;
					System.out.println(i1);
					if(i1.annotationType()==Control.class) {
						System.out.println(i1);
						Control s=(Control)i1;
						msg=s.value();
						System.out.println(msg);
						object=cls.newInstance();
						map.put(msg, object);
					}
					if(i1.annotationType()==Server.class) {
						System.out.println(i1);
						Server s=(Server)i1;
						msg=s.value();
						object=cls.newInstance();
						map.put(msg, object);
						}
					}
				}
			/*
			 * 遍历所有的注解判断注解的类型用annotationType()方法进行判断
			 * 获取类的注解值,根据注解名保存不同的对象(Control 或者 Service)
			 */
			 Set<Map.Entry<String,Object>> List=map.entrySet();
			 for( Entry<String, Object> x:List) {
				Object object= x.getValue();
				if(object instanceof control) {
					java.lang.reflect.Method[] method=object.getClass().getMethods();//获取类中的所有的方法
					for(java.lang.reflect.Method k:method) {
						Method zz=k.getAnnotation(Method.class);//方法获取注解
						if(null==zz) {
							continue;
							}
						else {
							System.out.println(object);
							System.out.println(zz.value());
							System.out.println(map.get(zz.value()));
								k.invoke(object,map.get(zz.value()));
								/*
								 * 通过invoke方法进行Control层对于Service的对象创建,
								 * 这里Control层的方法注解和Service层的类注解名称相同在这里就格外的重要
								 */
						}
					}
				}
				
			 } 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public static Object getControl(String parent) {
		// TODO Auto-generated method stub
		return  map.get(parent);
	}
}
