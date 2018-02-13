package service;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.ibatis.session.SqlSession;

import annotation.Server;
import base.DataBase;
import inter.UserDAO;
import user.User;


public class UserService {

	public User register(String username,String password) {
		SqlSession session=DataBase.open(true);
		UserDAO dao=session.getMapper(UserDAO.class);
		User user=new User();
		user.setUsername(username);
		user.setPassword(DigestUtils.md5Hex(password));//DigestUtils.md5Hex(password) MD5¼ÓÃÜ
		if(dao.getUser(username)!=null) {
			return null;
		}
		int i=dao.addUser(user);
		if(i!=1) {
			System.out.println("×¢²áÊ§°Ü");
			return null;
		}
		return user;
	}
	
	public User login(String username,String password) {
		SqlSession session=DataBase.open(true);
		UserDAO dao=session.getMapper(UserDAO.class);
		User user=dao.getUser(username);
		if(null==user) {
			System.out.println("ÓÃ»§µÇÂ¼Ê§°Ü");
			return null;
		}
		if(!DigestUtils.md5Hex(password).equals(user.getPassword()))
		{
			System.out.println("ÓÃ»§µÇÂ¼Ê§°Ü");
			return null;
		}
		return user;
	}

}
