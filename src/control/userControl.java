package control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import AccountFlow.Account;
import BankCard.Card;
import annotation.Annotation;
import annotation.Control;
import annotation.Method;
import annotation.Server;
import fenye.Fenye;
import service.CardService;
import service.Service;
import service.UserService;
import user.User;

public class userControl extends control {

	private Service service;
	
	private UserService userservice;
	
	private CardService cardservice;

	public void setCardservice(CardService cardservice) {
		this.cardservice = cardservice;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public void setUserservice(UserService userservice) {
		this.userservice = userservice;
	}
	
	/*
	 * ������Ҫ�ر�ע�ⷽ����ע�ⲻ�ܺ����ע����ͬ,������Ϊ�ں�����Զ�����Server����ʱ,����ķ���
	 * ע���Service����ע��Ҫ��ͬ���������ܷ����Զ���������,���﷽����ע��Ͷ�Ӧ��Serviceע����ͬ
	 * �������㹹�췽��������Ӧ�Ķ���,���������Լ���д����ʱ����һ��������ǽ�Control�����ע���Service��
	 * ����ע�������������ǲ���ȡ����Ϊ���������һ�����⣬�ں����Զ���������ʱ������֪�������ĸ�����,��������������Ĺ���ǳ���Ҫ
	 */

	public String load(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// Set factory constraints
		factory.setSizeThreshold(10240);
		
		String src = req.getServletContext().getRealPath("/");
		
		factory.setRepository(new File(src + "WEB-INF/load-tmp"));

		// Create a new file upload handler
		ServletFileUpload load = new ServletFileUpload(factory);

		// Set overall request size constraint
		load.setSizeMax(1024 * 500);
		// Parse the request
		try {
			List<FileItem> items = load.parseRequest(req);
			Iterator<FileItem> iter = items.iterator();
			while (iter.hasNext()) {
			    FileItem item = iter.next();

			    if (item.isFormField()) {
			    	 String name = item.getFieldName();
			    	 String value = item.getString();
			    } else {
			    	 HttpSession session = req.getSession();
			    	 User user = (User)session.getAttribute("user");
			    	 String fileName = "" + user.getUsername();
			    	 File loadedFile = new File(src + "WEB-INF/load/" +  fileName);
			    	 item.write(loadedFile);
			    }
			}
			return toUsercenter(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	/*
	 * �ļ����ϴ�����ģ��Apache��fileupload
	 */
	
	public void showPicture(HttpServletRequest req, HttpServletResponse resp) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		String src=req.getServletContext().getRealPath("/");
		HttpSession session=req.getSession();//�����һ��֪ʶ�����session��request������һ����ȫ�ֱ���һ���Ǿֲ�����
		User user=(User)session.getAttribute("user");
		try (FileInputStream in = new FileInputStream(src+ "WEB-INF/load/" + user.getUsername());
				OutputStream out = resp.getOutputStream()) {
			byte[] data = new byte[1024];
			int length = -1;
			while((length=in.read(data))!=-1) {
				out.write(data, 0, length);
				out.flush();
			}
		}
	}
	
	public String  toRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		javax.servlet.http.HttpSession session = req.getSession();
		return "register";
	
	}
	
	public String Register(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		javax.servlet.http.HttpSession session = req.getSession();//����н��и�ֵ����ԭ����Session�����û�д����µ�Session
		String username= req.getParameter("username");
		String psssword= req.getParameter("password");
		User user= userservice.register(username,psssword);
		if(user==null) {
			return "register";
		}
		else {
			session.setAttribute("user",user);
			return "login";
		}
	}
	
	public String toLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		javax.servlet.http.HttpSession session = req.getSession();
		return "login";																																								
	}

	public String  login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		javax.servlet.http.HttpSession session = req.getSession();
		String username= req.getParameter("username");
		String psssword= req.getParameter("password");
		User user=userservice.login(username, psssword);
		if(user==null) {
			return "login";	
		}
		else {
			session.setAttribute("user", user);
			session.setAttribute("username",username);
			return toUsercenter(req, resp);
		}
	}
	
	public String toUsercenter(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("--------");
		javax.servlet.http.HttpSession session = req.getSession();
		String username= (String) session.getAttribute("username");
		System.out.println(username);
		String currentPage = req.getParameter("currentPage");
		currentPage=currentPage ==null ? "1" : currentPage;//ע������ķ�ҳ��̨ʵ��
		Fenye list=cardservice.list(username, Integer.parseInt(currentPage));
		System.out.println("\\\\\\"+list);
		req.setAttribute("fenye", list);
		req.setAttribute("currentPage", Integer.parseInt(currentPage));
		req.setAttribute("username", username);
		return "usercenter";
	}
	/*
	 * �������һ����������� toUsercenter()�������ڳ������г��ֱ�������������ԭ��̫��ȷ,������Է���һ��
	 */

}
