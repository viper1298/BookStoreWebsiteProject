package com.bookstore.service;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bookstore.dao.UserDAO;
import com.bookstore.entity.Users;

public class UserServices {

	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	private UserDAO userDAO;
	private HttpServletRequest request;
	private HttpServletResponse response;

	public UserServices(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		entityManagerFactory = Persistence.createEntityManagerFactory("BookStoreWebsite");
		entityManager = entityManagerFactory.createEntityManager();
		userDAO = new UserDAO(entityManager);
	}

	public void listUser() throws ServletException, IOException {
		listUser(null);
	}

	public void listUser(String message) throws ServletException, IOException {
		List<Users> listUsers = userDAO.listAll();

		request.setAttribute("listUsers", listUsers);
		if (message != null) {
			request.setAttribute("message", message);
		}
		String listPage = "user_list.jsp";
		RequestDispatcher requestDispatcher = request.getRequestDispatcher(listPage);
		requestDispatcher.forward(request, response);

	}

	public void createUser() throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String fullName = request.getParameter("fullname");

		Users existsUser = userDAO.findByEmail(email);

		if (existsUser != null) {
			String message = " Could not create user. A user with email  " + email + " already exists";
			request.setAttribute("message", message);
			RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
			dispatcher.forward(request, response);
		} else {
			Users newUser = new Users(email, fullName, password);
			userDAO.create(newUser);
			listUser("New User Created Successfully");
		}
	}

	public void editUser() throws ServletException, IOException {
		int userId = Integer.parseInt(request.getParameter("id"));
		Users user = userDAO.get(userId);
		String editPage = "user_form.jsp";
		request.setAttribute("user", user);
		RequestDispatcher requestDispatcher = request.getRequestDispatcher(editPage);
		requestDispatcher.forward(request, response);
	}

	public void updateUser() throws ServletException, IOException {
		int userId = Integer.parseInt(request.getParameter("userId"));
		String email = request.getParameter("email");
		String fullName = request.getParameter("fullname");
		String password = request.getParameter("password");

		Users userById = userDAO.get(userId);
		Users userByEmail = userDAO.findByEmail(email);

		if (userByEmail != null && userByEmail.getUserid() != userById.getUserid()) {
			String message = "Could not update user. User with email " + email + " already exists.";
			request.setAttribute("message", message);

			RequestDispatcher requestDispatcher = request.getRequestDispatcher("message.jsp");
			requestDispatcher.forward(request, response);
		} else {

			// System.out.println(userId + ":" + email + " , " + fullName + " , " +
			// password);
			Users user = new Users(userId, email, fullName, password);

			userDAO.update(user);

			String message = "User has been updated successfully";
			listUser(message);
		}
	}
}
