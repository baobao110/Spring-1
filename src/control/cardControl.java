package control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
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
import fenye.Fenye;
import service.CardService;
import service.Service;
import user.User;


public class cardControl extends control{

	private Service service;
	
	private CardService cardservice;
	
	public void setService(Service service) {
		this.service = service;
	}

	public void setCardservice(CardService cardservice) {
		this.cardservice = cardservice;
	}



	public void down(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// TODO Auto-generated method stub
		javax.servlet.http.HttpSession session = req.getSession(); 
		User user=(User) session.getAttribute("user");
		String a=req.getParameter("number");
		int number=Integer.parseInt(a);
		String password=req.getParameter("password");
		Card cad=service.Get(number,password);
		String filename = number + ".csv";
		resp.setContentType("application/octet-stream");  
		resp.setHeader("Content-Disposition", "attachment;filename="+ filename);  
		
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()))) {
			int currentPage = 1;
			String header = "卡号,金额,备注,时间";
			bw.write(header);
			bw.newLine();
			bw.flush();
			while (true) {
				Fenye list = service.List(number, password, currentPage);
				System.out.println(currentPage);
				 ArrayList<Account>account=(ArrayList<Account>) list.getObject();
				if(null==account) {
					break;
				}
				if(account.isEmpty()) {//这里特别注意不然不能下载完成
					break;
				}
				for(Account i:account) {
					StringBuilder text = new StringBuilder();
					text.append(i.getNumber()).append(",")
					.append(i.getMoney()).append(",")
					.append(i.getDescription()).append(",")
					.append(i.getCreatetime()).append(",");
					bw.write(text.toString());
					bw.newLine();
					bw.flush();
					}
				System.out.println(111);
				currentPage++;
			}
		}
	}

	
	
	public String toDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String a=req.getParameter("number");
		int number=Integer.parseInt(a);
		req.setAttribute("number", number);
		return "delete";
	}
	
	public String delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		javax.servlet.http.HttpSession session = req.getSession();
		User user=(User)session.getAttribute("user");
		String a=req.getParameter("number");
		int number=Integer.parseInt(a);
		String password=req.getParameter("password");
		Card cad=service.Get(number,password);
		if(cad==null) {
			return null;
		}
		else {
			service.delete(number);
			session.setAttribute("user",user);
			return toUsercenter(req, resp);
		}
	}
	
	public String openAccount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session=req.getSession();
		User user=(User) session.getAttribute("user");
		String username=user.getUsername();
		String password=req.getParameter("password");
		Card card=service.open(username,password);
		req.setAttribute("card", card);//这里是通过键值对的形式存储相关的数值,对于Attribute属性有set方法也有get方法这里可以和Parameter参数区别
		return "success";
	}
	
	public String toOpenAccount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		javax.servlet.http.HttpSession session = req.getSession();
		return "OpenAccount";
	}
	
	public String save(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String a=req.getParameter("number");
		int number=Integer.parseInt(a);
		String password=req.getParameter("password");
		String b=req.getParameter("money");
		double money=Double.parseDouble(b);
		Card cad=service.Get(number,password);
		if(cad==null) {
			return null;
		}
		else {
			service.save(number, password, money);
			Card card=service.GetCard(number);
			req.setAttribute("card", card);
			return "success";
		}
	}
	
	public String  toSave(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String a=req.getParameter("number");
		int number=Integer.parseInt(a);
		req.setAttribute("number", number);
		return "save";
	}
	
	public String transfer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String a=req.getParameter("number");
		int number=Integer.parseInt(a);
		String password=req.getParameter("password");
		String b=req.getParameter("money");
		double money=Double.parseDouble(b);
		Card cad=service.Get(number,password);
		String c=req.getParameter("InNumber");
		int InNumber=Integer.parseInt(c);
		service.transfer(number, password, money, InNumber);
		Card card1=service.GetCard(number);
		Card card2=service.GetCard(InNumber);
		if((card1==null)||(card2==null)) {
			return null;
		}
		else {
			req.setAttribute("card1", card1);
			req.setAttribute("card2", card2);
			return "success2";
		}
	}
	
	public String toTransfer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String a=req.getParameter("number");
		int number=Integer.parseInt(a);
		req.setAttribute("number", number);
		return "transfer";
	}
	
	public String  check(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String a=req.getParameter("number");
		int number=Integer.parseInt(a);
		String password=req.getParameter("password");
		String b=req.getParameter("money");
		double money=Double.parseDouble(b);
		Card cad=service.Get(number,password);
		service.draw(number, password, money);
		Card card=service.GetCard(number);
		if(card==null) {
			return null;
		}
		else {
			req.setAttribute("card", card);
			return "success";
		}
	}
	
	public String toCheck(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String a=req.getParameter("number");
		int number=Integer.parseInt(a);
		req.setAttribute("number", number);
		return "check";
	}
	
	public String list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			String currentPage = req.getParameter("currentPage");
			String a=req.getParameter("number");
			int number=Integer.parseInt(a);
			String password=req.getParameter("password");
			Card cad=service.Get(number,password);
			if(null==cad) {
				return null;
			}
			else {
				Fenye list = service.List(number, password, Integer.parseInt(currentPage));//获取记录
				req.setAttribute("fenye", list);//保存记录
				req.setAttribute("number", number);
				req.setAttribute("password", password);//这里之所以保存number和password是为了前端页面显示的作用
				return "list";
			}
		}
	
	public String toList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String a=req.getParameter("number");
		int number=Integer.parseInt(a);
		req.setAttribute("number", number);
		return "list";
	
	}
	
	public String toChangePassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String a=req.getParameter("number");
		int number=Integer.parseInt(a);
		req.setAttribute("number", number);
		return "password";
	
	}
	
	public String  password(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String a=req.getParameter("number");
		int number=Integer.parseInt(a);
		String oldPassword= req.getParameter("oldPassword");
		Card cad=service.Get(number,oldPassword);
		if(cad==null) {
			return null;
		}
		else {
			String newPassword= req.getParameter("newPassword");
			Card card=service.ChangePassword(number, oldPassword, newPassword);
			req.setAttribute("card", card);
			return "success";
		}
	}
	
	public String toUsercenter(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("--------");
		javax.servlet.http.HttpSession session = req.getSession();
		String username= (String) session.getAttribute("username");
		System.out.println(username);
		String currentPage = req.getParameter("currentPage");
		currentPage=currentPage ==null ? "1" : currentPage;//注意这里的分页后台实现
		Fenye list=cardservice.list(username, Integer.parseInt(currentPage));
		System.out.println("\\\\\\"+list);
		req.setAttribute("fenye", list);
		req.setAttribute("currentPage", Integer.parseInt(currentPage));
		req.setAttribute("username", username);
		return "usercenter";
	}

}
