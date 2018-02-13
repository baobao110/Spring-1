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
		SqlSession session=DataBase.open(true);//这里开启事务,这里和JDBC那里一样需要手动的提交事务所以设置为false,true为自动提交，false为手动提交，默认为false手动提交
	try {
		CardDAO dao=session.getMapper(CardDAO.class);//获取Card.xml中的类
		Card card=new Card();
		card.setNumber(DataBase.CreateNumber());
		card.setPassword(DigestUtils.md5Hex(password));
		card.setMoney(0);
		card.setUsername(username);
		System.out.println(card);
		if(dao.open(card)==1) {//这里调用Card.xml类中的SQL语句
			return card;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			session.rollback();//这里如果程序执行出错就需要回滚，这里调用session的rollback()方法
		}
		session.close();//最后关闭session会话
		return null;
	}
	public void save(int number,String password,double money)  {
		SqlSession session=DataBase.open(false);
		try {
			CardDAO dao1=session.getMapper(CardDAO.class);
			Card card=dao1.GetCad(number);
			if(null==card) {
				System.out.println("账号或者密码不存在");
				return;
			}
			if(!DigestUtils.md5Hex(password).equals(card.getPassword())) {
				System.out.println("账号或者密码不存在");
				return;
			}
			if(money<0) {
				System.out.println("金额小于零");
				return;
			}
			double x=card.getMoney();
			x+=money;
			if(dao1.modifyMoney(number, x)!=0) {
				System.out.println("存钱成功");	
			}
			Account account=new Account();
			account.setNumber(number);
			account.setMoney(money);
			account.setType(1);
			account.setDescription("存钱");
			AccountDAO dao2=session.getMapper(AccountDAO.class);
			dao2.add(account);
			System.out.println("流水账产生");
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
				System.out.println("账号或者密码不存在");
				return;
			}
			if(!DigestUtils.md5Hex(password).equals(card.getPassword())) {
				System.out.println("账号或者密码不存在");
				return;
			}
			if(money<0) {
				System.out.println("金额小于零");
				return;
			}
			double x=card.getMoney();
			if(money>x) {
				System.out.println("金额不足");
				return;
			}
			x-=money;
			if(dao1.modifyMoney(number, x)!=0) {
				System.out.println("取钱成功");	
			}
			Account account=new Account();
			account.setNumber(number);
			account.setMoney(money);
			account.setType(1);
			account.setDescription("取钱");
			AccountDAO dao2=session.getMapper(AccountDAO.class);
			dao2.add(account);
			System.out.println("流水账产生");
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
				System.out.println("账号或者密码不存在");
				return;
			}
			if(!DigestUtils.md5Hex(password).equals(card1.getPassword())) {
				System.out.println("账号或者密码不存在");
				return;
			}
			if(money<0) {
				System.out.println("金额小于零");
				return;
			}
			double x=card1.getMoney();
			if(money>x) {
				System.out.println("金额不足");
				return;
			}
			x-=money;
			if(dao1.modifyMoney(OutNumber, x)!=0) {
				System.out.println("取钱成功");	
			}
			Account account1=new Account();
			account1.setNumber(OutNumber);
			account1.setMoney(money);
			account1.setType(1);
			account1.setDescription("取钱");
			AccountDAO dao2=session.getMapper(AccountDAO.class);
			dao2.add(account1);
			System.out.println("流水账产生");
			CardDAO dao3=session.getMapper(CardDAO.class);
			Card card2=dao3.GetCad(InNumber);
			if(null==card2) {
				System.out.println("账号或者密码不存在");
				return;
			}
			double y=card2.getMoney();
			money+=y;
			if(dao3.modifyMoney(InNumber, money)!=0) {
				System.out.println("转入成功");	
			}
			Account account2=new Account();
			account2.setNumber(InNumber);
			account2.setMoney(money);
			account2.setType(1);
			account2.setDescription("存钱");
			AccountDAO dao4=session.getMapper(AccountDAO.class);
			dao4.add(account2);
			System.out.println("流水账产生");
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
				System.out.println("账号或者密码不存在");
				return null;
			}
			if(!DigestUtils.md5Hex(oldPassword).equals(a.getPassword())) {
				System.out.println("账号或者密码不存在");
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
			System.out.println("账号或者密码不存在");
			return null;
		}
		if(!DigestUtils.md5Hex(password).equals(card.getPassword())) {
			System.out.println("账号或者密码不存在");
			return null;
		}
		int totalNumber=account.totalNumber(number);//调用totalNumber方法获取总纪录数目
		Fenye fenye=new Fenye(totalNumber, currentPage);//初始化，参数为总记录和当前页数
		 ArrayList<Account>list=account.List(number, fenye.getcurrentNumber(), fenye.move);//调用List()方法获取该number的该页内容
		 fenye.setObject(list);//将获取的记录保存
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
				System.out.println("账号或者密码不存在");
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
				System.out.println("账号或者密码不存在");
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
				System.out.println("账号或者密码不存在");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			session.rollback();
			}
		session.close();
	}
}
