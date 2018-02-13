package service;

import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import AccountFlow.Account;
import BankCard.Card;
import annotation.Server;
import base.DataBase;
import fenye.Fenye;
import inter.AccountDAO;
import inter.CardDAO;

public class Service {
	
	public Card open(String username,String password) {
		SqlSession session=DataBase.open(true);//���￪������,�����JDBC����һ����Ҫ�ֶ����ύ������������Ϊfalse,trueΪ�Զ��ύ��falseΪ�ֶ��ύ��Ĭ��Ϊfalse�ֶ��ύ
	try {
		CardDAO dao=session.getMapper(CardDAO.class);//��ȡCard.xml�е���
		Card card=new Card();
		card.setNumber(DataBase.CreateNumber());
		card.setPassword(DigestUtils.md5Hex(password));
		card.setMoney(0);
		card.setUsername(username);
		System.out.println(card);
		if(dao.open(card)==1) {//�������Card.xml���е�SQL���
			return card;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			session.rollback();//�����������ִ�г������Ҫ�ع����������session��rollback()����
		}
		session.close();//���ر�session�Ự
		return null;
	}
	public void save(int number,String password,double money)  {
		SqlSession session=DataBase.open(false);
		try {
			CardDAO dao1=session.getMapper(CardDAO.class);
			Card card=dao1.GetCad(number);
			if(null==card) {
				System.out.println("�˺Ż������벻����");
				return;
			}
			if(!DigestUtils.md5Hex(password).equals(card.getPassword())) {
				System.out.println("�˺Ż������벻����");
				return;
			}
			if(money<0) {
				System.out.println("���С����");
				return;
			}
			double x=card.getMoney();
			x+=money;
			if(dao1.modifyMoney(number, x)!=0) {
				System.out.println("��Ǯ�ɹ�");	
			}
			Account account=new Account();
			account.setNumber(number);
			account.setMoney(money);
			account.setType(1);
			account.setDescription("��Ǯ");
			AccountDAO dao2=session.getMapper(AccountDAO.class);
			dao2.add(account);
			System.out.println("��ˮ�˲���");
			session.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			session.rollback();
			}
		session.close();
		}
		
	
	public void draw(int number,String password,double money) {
		SqlSession session=DataBase.open(false);
		try {
			CardDAO dao1=session.getMapper(CardDAO.class);
			Card card=dao1.GetCad(number);
			if(null==card) {
				System.out.println("�˺Ż������벻����");
				return;
			}
			if(!DigestUtils.md5Hex(password).equals(card.getPassword())) {
				System.out.println("�˺Ż������벻����");
				return;
			}
			if(money<0) {
				System.out.println("���С����");
				return;
			}
			double x=card.getMoney();
			if(money>x) {
				System.out.println("����");
				return;
			}
			x-=money;
			if(dao1.modifyMoney(number, x)!=0) {
				System.out.println("ȡǮ�ɹ�");	
			}
			Account account=new Account();
			account.setNumber(number);
			account.setMoney(money);
			account.setType(1);
			account.setDescription("ȡǮ");
			AccountDAO dao2=session.getMapper(AccountDAO.class);
			dao2.add(account);
			System.out.println("��ˮ�˲���");
			session.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			session.rollback();
			}
		session.close();
	}
	
	public void transfer(int OutNumber,String password,double money,int InNumber) {
		SqlSession session=DataBase.open(false);
		try {
			CardDAO dao1=session.getMapper(CardDAO.class);
			Card card1=dao1.GetCad(OutNumber);
			if(null==card1) {
				System.out.println("�˺Ż������벻����");
				return;
			}
			if(!DigestUtils.md5Hex(password).equals(card1.getPassword())) {
				System.out.println("�˺Ż������벻����");
				return;
			}
			if(money<0) {
				System.out.println("���С����");
				return;
			}
			double x=card1.getMoney();
			if(money>x) {
				System.out.println("����");
				return;
			}
			x-=money;
			if(dao1.modifyMoney(OutNumber, x)!=0) {
				System.out.println("ȡǮ�ɹ�");	
			}
			Account account1=new Account();
			account1.setNumber(OutNumber);
			account1.setMoney(money);
			account1.setType(1);
			account1.setDescription("ȡǮ");
			AccountDAO dao2=session.getMapper(AccountDAO.class);
			dao2.add(account1);
			System.out.println("��ˮ�˲���");
			CardDAO dao3=session.getMapper(CardDAO.class);
			Card card2=dao3.GetCad(InNumber);
			if(null==card2) {
				System.out.println("�˺Ż������벻����");
				return;
			}
			double y=card2.getMoney();
			money+=y;
			if(dao3.modifyMoney(InNumber, money)!=0) {
				System.out.println("ת��ɹ�");	
			}
			Account account2=new Account();
			account2.setNumber(InNumber);
			account2.setMoney(money);
			account2.setType(1);
			account2.setDescription("��Ǯ");
			AccountDAO dao4=session.getMapper(AccountDAO.class);
			dao4.add(account2);
			System.out.println("��ˮ�˲���");
			session.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			session.rollback();
			}
		session.close();
		}
	
	public Card ChangePassword(int number,String oldPassword,String newPassword) {
		SqlSession session=DataBase.open(true);
		try {
			CardDAO dao=session.getMapper(CardDAO.class);
			Card a=dao.GetCad(number);
			if(null==a) {
				System.out.println("�˺Ż������벻����");
				return null;
			}
			if(!DigestUtils.md5Hex(oldPassword).equals(a.getPassword())) {
				System.out.println("�˺Ż������벻����");
				return null;
			}
			if(dao.modifyPassword(number, DigestUtils.md5Hex(newPassword))!=0) {
				a=dao.GetCad(number);
				return a;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			session.rollback();
		}
		session.close();
		return null;
	}
	
	public Fenye List(int number, String password, int currentPage) {
		SqlSession session = DataBase.open(true);
		try {
		CardDAO car = session.getMapper(CardDAO.class);
		AccountDAO account= session.getMapper(AccountDAO.class);
		Card card=car.GetCad(number);
		if(null==card) {
			System.out.println("�˺Ż������벻����");
			return null;
		}
		if(!DigestUtils.md5Hex(password).equals(card.getPassword())) {
			System.out.println("�˺Ż������벻����");
			return null;
		}
		int totalNumber=account.totalNumber(number);//����totalNumber������ȡ�ܼ�¼��Ŀ
		Fenye fenye=new Fenye(totalNumber, currentPage);//��ʼ��������Ϊ�ܼ�¼�͵�ǰҳ��
		 ArrayList<Account>list=account.List(number, fenye.getcurrentNumber(), fenye.move);//����List()������ȡ��number�ĸ�ҳ����
		 fenye.setObject(list);//����ȡ�ļ�¼����
		 return fenye;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		session.rollback();
	}
	session.close();
	return null;
	}
	
	public int total(int number) {
		SqlSession session = DataBase.open(true);
		AccountDAO account= session.getMapper(AccountDAO.class);
		return account.totalNumber(number);
	}
	
	public Card GetCard(int number) {
		SqlSession session=DataBase.open(false);
		try {
			CardDAO dao=session.getMapper(CardDAO.class);
			Card a=dao.GetCad(number);
			if(null==a) {
				System.out.println("�˺Ż������벻����");
				return null;
			}
			else {
				return a;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			session.rollback();
			}
		session.close();
		return null;
		}
	
	public Card Get(int number,String password) {
		SqlSession session=DataBase.open(false);
		try {
			CardDAO dao=session.getMapper(CardDAO.class);
			Card a=dao.Get(number,DigestUtils.md5Hex(password));
			if(null==a) {
				System.out.println("�˺Ż������벻����");
				return null;
			}
			else {
				return a;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			session.rollback();
			}
		session.close();
		return null;
		}
	
	public void delete(int number) {
		SqlSession session=DataBase.open(true);
		try {
			CardDAO dao=session.getMapper(CardDAO.class);
			int i=dao.delete(number);
			System.out.println(i);
			if(i==0) {
				System.out.println("�˺Ż������벻����");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			session.rollback();
			}
		session.close();
	}
}
