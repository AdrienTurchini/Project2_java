package com.adrienTurchini.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@ManagedBean(eager = true)
@ApplicationScoped
public class StudentDbUtil {

	static DataSource dataSource;

	public StudentDbUtil() throws NamingException {
		super();
		dataSource = getDataSource();
	}


	private DataSource getDataSource() throws NamingException{ 
		String jndi="java:comp/env/jdbc/studentdb";
		Context context = new InitialContext();
		DataSource dataSource = (DataSource) context.lookup(jndi); 
		return dataSource;
	}

	public static List<Student> getStudents() 
			throws SQLException{
			List<Student> students = new ArrayList<Student>();
			Connection myConn = null;
			Statement myStmt = null;
			ResultSet myRs = null;

			try {
				myConn = dataSource.getConnection();
				myStmt = myConn.createStatement();
				String sql = "select * from student order by last_name";
				myRs = myStmt.executeQuery(sql);

				while(myRs.next()) {
					int id = myRs.getInt("id");
					String firstName = myRs.getString("first_name");
					String lastName = myRs.getString("last_name"); 
					String email = myRs.getString("email");

					Student tempStudent = new Student(id, firstName, lastName, email);
					students.add(tempStudent);
				}

				return students;
			} finally {
				close(myConn, myStmt, myRs);
			}
	}
	
	private static void close(Connection myConn, Statement myStmt, ResultSet myRs) {

		try {
			if(myStmt != null)
				myStmt.close();
			if(myRs != null)
				myRs.close();
			if(myConn != null)
				myConn.close();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void addStudent(Student student) {
		Connection myConn=null;
		PreparedStatement myStmt = null;
		ResultSet myRs= null; try {
			myConn = dataSource.getConnection();
			String sql = "INSERT INTO Student (first_name, last_name, email) VALUES (?, ?, ?)";
			myStmt = myConn.prepareStatement(sql);
			String firstName = student.getFirstName(); 
			String lastName = student.getLastName(); 
			String email = student.getEmail();
			myStmt.setString(1, firstName); 
			myStmt.setString(2, lastName); 
			myStmt.setString(3, email); 
			myStmt.execute();
		}
		catch(Exception e){
			System.out.println(e.getMessage()); }

		finally{ close(myConn,myStmt,myRs);
		} 
	}
	
	public static Student fetchStudent(int id) {
		Connection myConn=null;
		Statement myStmt = null; 
		ResultSet myRs= null; 
		Student student=null;
		try {
			myConn = dataSource.getConnection();
			myStmt= myConn.createStatement();
			String sql= "select * from student where id="+id; 
			myRs = myStmt.executeQuery(sql);
			while(myRs.next()){
				String firstName=myRs.getString("first_name"); 
				String lastName=myRs.getString("last_name"); 
				String email = myRs.getString("email");
				student = new Student(id,firstName,lastName,email); 
				}
			return student;
		}catch(Exception e){
			System.out.println(e.getMessage()); 
			return null;
		} finally{ 
			close(myConn,myStmt,myRs);
		} 
	}
	
	public static void updateStudent(Student student) {
		Connection myConn=null;
		PreparedStatement myStmt = null; 
		try {
			myConn = dataSource.getConnection();
			String sql = "update student set first_name=?, last_name=?,email=? where id=?";
			myStmt = myConn.prepareStatement(sql);

			myStmt.setString(1, student.getFirstName()); 
			myStmt.setString(2, student.getLastName());
			myStmt.setString(3, student.getEmail()); 
			myStmt.setInt(4,student.getId()); 
			myStmt.execute();
		}
		catch(Exception e){
			System.out.println(e.getMessage()); 
		}
		finally{ close(myConn,myStmt,null);
		}
	}
	
	public static void deleteStudent(int id) {
		Connection myConn=null; Statement myStmt = null; 
		try {
			myConn = dataSource.getConnection();
			myStmt= myConn.createStatement();
			String sql= "delete from student where id="+id; 
			myStmt.execute(sql);
		}catch(Exception e){ 
			System.out.println(e.getMessage());
		} finally{ 
			close(myConn,myStmt,null); 
		}
	}

}
