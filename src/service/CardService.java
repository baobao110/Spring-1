package service;

import java.util.ArrayList;

import org.apache.ibatis.session.SqlSession;

import AccountFlow.Account;
import BankCard.Card;
import annotation.Server;
import base.DataBase;
import fenye.Fenye;
import inter.AccountDAO;
import inter.CardDAO;

public class CardService {
	
	public Fenye List(int number,int currentPage) {
		SqlSession session = DataBase.open(true);
		try {
		CardDAO card = session.getMapper(CardDAO.class);
		int totalNumber=card.totalNumber(number);
		Fenye fenye=new Fenye(totalNumber, currentPage);
		 ArrayList<Card>list=card.List(number, fenye.getcurrentNumber(), fenye.move);
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
	
	public Fenye list(String username,int currentPage) {
		SqlSession session = DataBase.open(true);
		try {
		CardDAO card = session.getMapper(CardDAO.class);
		int totalNumber=card.total(username);
		System.out.println(totalNumber);
		Fenye fenye=new Fenye(totalNumber, currentPage);
		 ArrayList<Card>list=card.list(username, fenye.getcurrentNumber(), fenye.move);
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

}
