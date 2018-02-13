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
	
	private static final Map<String,Object> map=new HashMap<String,Object>();//�����ü�ֵ�Ե���ʽ���治ͬ��control����
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
	 * ����ע��find()����,�����ȷ���classes�ļ�Ŀ¼,find�����������Ǳ���classes��������з���,
	 * ���������˵ݹ�Ĳ���,�õݹ�ʱҪ�ر�ע��ʲôʱ��ݹ����,�����������ļ�Ϊnull�����Ѿ��ҵ��ļ�ʱ����
	 * ������һ�����ϱ������е��ļ�
	 */
	
	private static void pick() {
		for(File i:fileList) {
			if(i.getName().endsWith("class")) {
				pick.add(i);
			}
		}
	}
	/*
	 * �����pick()��������������find()�����Ѿ��ҵ��������ļ����ٴν���ɸѡ,������.classΪ��β��
	 * �ļ�,�����Ǳ���,��ʵ������Դjava�ļ��������ֽ����ļ�
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
	//������ǻ�ȡԴ�ļ���·����������������Ҫzע��getPath()������·�������ַ�������ʽ����
	private static void loadfiles(String root) {
		File file=new File(root+"/WEB-INF/classes");//�������ҵ�classes���ֽ����ļ��ĸ�Ŀ¼
		find(file);
		pick();
		str();
		List<String> xx=new ArrayList<String>();//��ȡ�ֽ����ļ�ȫ��
		for(String i:str) {
			xx.add(i.substring(0,i.length()-6));//��ȡԴ�ļ��ľ���·��
		}
		List<String> xxx=new ArrayList<String>();
		for(String i:xx) {
			String m=i.replace("\\", ".");//��·����"\\"ת��Ϊ"." ��ʵҲ�ǻ�ȡ·����������ο���������
			xxx.add(m);
		}
		for(String i:xxx) {
			try {
				Class cls=Class.forName(i);//�����ļ�·����ȡ��
				if(Modifier.isAbstract(cls.getModifiers())) {
					continue;
				}//��������ж�
			java.lang.annotation.Annotation[] a=cls.getAnnotations();//��ȡ���������ע��,����������ķ����ǲ�ȷ��ע�������,��Ҫ�����ж�,��ʵ�������ʡ��
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
			 * �������е�ע���ж�ע���������annotationType()���������ж�
			 * ��ȡ���ע��ֵ,����ע�������治ͬ�Ķ���(Control ���� Service)
			 */
			 Set<Map.Entry<String,Object>> List=map.entrySet();
			 for( Entry<String, Object> x:List) {
				Object object= x.getValue();
				if(object instanceof control) {
					java.lang.reflect.Method[] method=object.getClass().getMethods();//��ȡ���е����еķ���
					for(java.lang.reflect.Method k:method) {
						Method zz=k.getAnnotation(Method.class);//������ȡע��
						if(null==zz) {
							continue;
							}
						else {
							System.out.println(object);
							System.out.println(zz.value());
							System.out.println(map.get(zz.value()));
								k.invoke(object,map.get(zz.value()));
								/*
								 * ͨ��invoke��������Control�����Service�Ķ��󴴽�,
								 * ����Control��ķ���ע���Service�����ע��������ͬ������͸������Ҫ
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
