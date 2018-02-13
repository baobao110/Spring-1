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
 * ע������ע������ú���ζ���ע�⣬ע��Ķ��巽�����Բ鿴ע����ĵ�
 * �����ע�������controlʹ�õģ���������ע������ȡ��,���ע���value()
 * ����ȡ������һ�㣬������������ʹ��,�����ע��ֵ��Ϊ����ļ�ֵ�Եļ�ʹ��,
 * ��control���н���Ҫ��control������ע�⣬�����ڻ�ȡ������.class�ļ��ͷ���
 * ��ȡ��Ͷ���
 */