/****************************************************************************
							CSE532 -- Project 2
File name: ApiManager.java
Author(s): 	Gaurav Piyush (108996990 )
			Sudheer Kumar Vavilapalli (109203795 )
Brief description: 
	This file runs on the server side. It connects to the database using the
	postgresql jdbc connector. Once the connection is established we query
	the database. The result set returned is then stored in an arrayList
	temporarily and then passed as an attribute to the front end.
****************************************************************************/
/***************************************************************************
 *We pledge our honor that all parts of this project were done by us 
 *alone and without collaboration with anybody else.
 ***************************************************************************/


package api;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
/**
 * Servlet implementation class ApiManager
 */
@WebServlet("/ApiManager")
public class ApiManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/*
	 * Postgresql access parameters
	 * Useranme: "postgres"
	 * Password: "sudheer"
	 * url: "jdbc:postgresql://localhost:5432/linkedout"
	 */
	private static String dbUser = "postgres";
    private static String dbPassword = "sudheer";
    private static String dbUrl = "jdbc:postgresql://localhost:5432/linkedout";
    
    
    /*
     * Allocate arrays to store results of each query 
     */
    private ArrayList<String> query1 = new ArrayList<String>();
    private ArrayList<String> query2 = new ArrayList<String>();
    private ArrayList<String> query3 = new ArrayList<String>();
    private ArrayList<String> query4 = new ArrayList<String>();
    private ArrayList<String> query5 = new ArrayList<String>();
    private ArrayList<String> query6 = new ArrayList<String>();
    private ArrayList<String> query7 = new ArrayList<String>();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ApiManager() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		
		
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * Handle incoming get requests from front end
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rs6 = null;
		ResultSet rs7 = null;
		
		/*
		 * Define properties user and password
		 * */
		Properties props = new Properties();
		props.setProperty("user", dbUser);
		props.setProperty("password", dbPassword);
		try {
			/*Open connection to postgresql through postgresql jdbc driver*/
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(dbUrl,props);
			stmt = conn.createStatement();
			
			//Query 1
			rs1 = stmt.executeQuery("SELECT DISTINCT U1.USERNAME, U2.USERNAME "
					+ "FROM USERS U1, USERS U2, USER_ENDORSE UE1, USER_ORG UO1, USER_ORG UO2 "
					+ "WHERE U1.URL = UE1.URL1 AND U2.URL = UE1.URL2 AND U1.URL <> U2.URL AND "
					+ "UO1.URL = UE1.URL1 AND UO1.ORGID = UO2.ORGID AND "
					+ "(UO1.DURATIONINCOMPANY)[1].JOINDATE < TO_DATE('2013-09-18','YYYY-MM-DD') AND "
					+ "(UO1.DURATIONINCOMPANY)[1].ENDDATE >  TO_DATE('2013-09-18','YYYY-MM-DD') AND "
					+ "UO2.URL = UE1.URL2 AND "
					+ "(UO2.DURATIONINCOMPANY)[1].JOINDATE < TO_DATE('2013-09-18','YYYY-MM-DD') AND "
					+ "(UO2.DURATIONINCOMPANY)[1].ENDDATE >  TO_DATE('2013-09-18','YYYY-MM-DD');");

			while(rs1.next()){
				query1.add(rs1.getString(1)+" endorses "+rs1.getString(2));
			}
			
			request.setAttribute("query1", query1);
			
			//Query 2
			stmt.executeUpdate("CREATE OR REPLACE VIEW SKILLENDORSEDTHATUSERHAS (URL1, URL2, SKILLID) AS "
					+ "SELECT U1.URL, U2.URL, US1.SKILLID FROM USERS U1, USERS U2, USER_ENDORSE UE1, "
					+ "USER_SKILL US1, SKILLS S WHERE U1.URL = UE1.URL1 AND U2.URL = UE1.URL2 AND "
					+ "U1.URL <> U2.URL AND US1.URL = U1.URL AND US1.SKILLID = UE1.SKILLID AND "
					+ "S.SKILLID=US1.SKILLID;");
			
			rs2 = stmt.executeQuery("SELECT DISTINCT U1.USERNAME, U2.USERNAME "
					+ "FROM SKILLENDORSEDTHATUSERHAS SE, USERS U1, USERS U2, USERS U3, USER_ENDORSE UE1, "
					+ "USER_ENDORSE UE2, SKILLS S "
					+ "WHERE U1.URL = SE.URL1 AND U2.URL = SE.URL2 AND UE1.URL1 = U3.URL AND "
					+ "UE1.URL1 <> UE1.URL2 AND UE1.URL2 = SE.URL1 AND UE2.URL1 = U3.URL AND "
					+ "UE2.URL2 = SE.URL2 AND UE2.URL1 <> UE2.URL2 AND UE1.SKILLID = SE.SKILLID AND "
					+ "UE2.SKILLID = SE.SKILLID AND S.SKILLID = UE2.SKILLID;");
			
			while(rs2.next()){
				query2.add(rs2.getString(1)+" endorses "+rs2.getString(2));
			}
			
			request.setAttribute("query2", query2);
			
			//Query 3
			stmt.executeUpdate("CREATE OR REPLACE VIEW USERSENDORSEUSER (URL,SKILLID) AS "
					+ "SELECT U1.URL,UE1.SKILLID FROM USERS U1, USERS U2, USERS U3, "
					+ "USER_ENDORSE UE1, USER_ENDORSE UE2, SKILLS S "
					+ "WHERE UE1.URL1 = U2.URL AND UE1.URL2 = U1.URL AND "
					+ "UE1.SKILLID = S.SKILLID AND UE2.URL1 = U3.URL AND "
					+ "UE2.URL2 = U1.URL AND "
					+ "UE2.SKILLID = S.SKILLID AND UE1.SKILLID = UE2.SKILLID;");
			
			rs3 = stmt.executeQuery("SELECT DISTINCT U1.USERNAME "
					+ "FROM USERSENDORSEUSER UEU, USERS U1, SKILLS S "
					+ "WHERE UEU.SKILLID = S.SKILLID AND U1.URL = UEU.URL AND "
					+ "UEU.SKILLID NOT IN (SELECT US.SKILLID "
					+ "FROM USER_SKILL US WHERE US.SKILLID = S.SKILLID AND "
					+ "U1.URL = US.URL);");
			
			while(rs3.next()){
				query3.add(rs3.getString(1));
			}
			
			request.setAttribute("query3", query3);
			
			//Query 4
			stmt.executeUpdate("CREATE OR REPLACE VIEW SKILLUSER1NOTINUSER2(URL1,URL2) AS "
					+ "SELECT US1.URL,US2.URL "
					+ "FROM USER_SKILL US1, USER_SKILL US2, SKILLS S "
					+ "WHERE US2.SKILLID = S.SKILLID AND US1.URL NOT IN "
					+ "(SELECT U.URL FROM USER_SKILL U WHERE U.SKILLID = S.SKILLID);");
			
			stmt.executeUpdate("CREATE OR REPLACE VIEW MORESKILLEDDEF(URL1,URL2) AS "
					+ "SELECT U1.URL,U2.URL FROM USERS U1,USERS U2 "
					+ "WHERE U2.URL NOT IN(SELECT URL FROM USER_SKILL) "
					+ "AND U1.URL IN(SELECT URL FROM USER_SKILL);");
			
			stmt.executeUpdate("CREATE OR REPLACE VIEW MORESKILLED12(URL1,URL2) AS "
					+ "SELECT US1.URL,US2.URL "
					+ "FROM USER_SKILL US1, USER_SKILL US2,SKILLS S,SKILLUSER1NOTINUSER2 SNU "
					+ "WHERE US1.URL = SNU.URL2 AND US2.URL = SNU.URL1 AND US1.URL NOT IN "
					+ "(SELECT SNU1.URL1 "
					+ "FROM SKILLUSER1NOTINUSER2 SNU1 "
					+ "WHERE SNU1.URL1 = US1.URL AND SNU1.URL2 = US2.URL);");
			
			stmt.executeUpdate("CREATE OR REPLACE VIEW MORESKILLED(URL1,URL2) AS "
					+ "SELECT U1.URL,U2.URL "
					+ "FROM USERS U1,USERS U2, MORESKILLED12 MS, MORESKILLEDDEF MSD "
					+ "WHERE (U1.URL = MS.URL1 AND U2.URL = MS.URL2) OR "
					+ "(U1.URL = MSD.URL1 AND U2.URL = MSD.URL2);");
			
			rs4 = stmt.executeQuery("SELECT DISTINCT U1.USERNAME, U2.USERNAME "
					+ "FROM USERS U1, USERS U2, MORESKILLED MS "
					+ "WHERE U1.URL = MS.URL1 AND U2.URL = MS.URL2;");
			
			while (rs4.next()){
				query4.add(rs4.getString(1)+" is more skilled than "+rs4.getString(2));
			}
			
			request.setAttribute("query4", query4);
			
			//Query 5
			stmt.executeUpdate("CREATE OR REPLACE VIEW SKILLNOTENDORSED(URL) AS "
					          +"SELECT DISTINCT U.URL "
					          +"FROM USERS U,USER_SKILL US "
					          +"WHERE US.URL = U.URL AND "
					          +"U.URL NOT IN(SELECT URL2 FROM USER_ENDORSE WHERE SKILLID = US.SKILLID) AND "
					          +"U.URL IN(SELECT URL FROM USER_SKILL WHERE SKILLID = US.SKILLID);");
			
			stmt.executeUpdate("CREATE OR REPLACE VIEW MORECERTIFIED(URL1, URL2) AS "
						  	  +"SELECT DISTINCT U1.URL,U2.URL " 
						  	  +"FROM USERS U1, USERS U2, MORESKILLED MS "
						  	  +"WHERE MS.URL1 = U1.URL AND MS.URL2 = U2.URL AND "
						  	  +"U1.URL NOT IN (SELECT URL FROM SKILLNOTENDORSED);");
			
			rs5 = stmt.executeQuery("SELECT DISTINCT U1.USERNAME, U2.USERNAME "
							  +"FROM USERS U1, USERS U2, MORECERTIFIED MC "
							  +"WHERE U1.URL = MC.URL1 AND U2.URL = MC.URL2;");
			
			while (rs5.next()){
				query5.add(rs5.getString(1)+" is more certified than "+rs5.getString(2));
			}
			
			request.setAttribute("query5", query5);
			
			//Query 6
			stmt.executeUpdate("CREATE OR REPLACE RECURSIVE VIEW INDIRECTENDORSEMENT(URL1,URL2) AS "
							  +"SELECT UE1.URL1,UE1.URL2 " 
							  +"FROM USER_ENDORSE UE1 "
							  +"WHERE UE1.URL1 NOT IN(SELECT UE2.URL2 FROM USER_ENDORSE UE2 WHERE UE2.URL1=UE1.URL2 AND UE2.URL2=UE1.URL1) "
							  +"UNION "
							  +"SELECT UE.URL1,IE.URL2 "
							  +"FROM USER_ENDORSE UE, INDIRECTENDORSEMENT IE "
							  +"WHERE UE.URL2 = IE.URL1;");
			
			rs6 = stmt.executeQuery("SELECT DISTINCT U1.USERNAME, U2.USERNAME "
							  +"FROM USERS U1, USERS U2, INDIRECTENDORSEMENT IE "
							  +"WHERE U1.URL = IE.URL1 AND U2.URL = IE.URL2;");
			
			while (rs6.next()){
				query6.add(rs6.getString(1)+" indirectly endorses "+rs6.getString(2));
			}
			
			request.setAttribute("query6", query6);
			
			//Query 7
			stmt.executeUpdate("CREATE OR REPLACE RECURSIVE VIEW INDIRECTENDORSEMENTSKILL(URL1,URL2) AS "
							  +"SELECT UE1.URL1,UE1.URL2 " 
							  +"FROM USER_ENDORSE UE1, MORESKILLED MS "
							  +"WHERE UE1.URL1 NOT IN(SELECT UE2.URL2 FROM USER_ENDORSE UE2 WHERE UE2.URL1=UE1.URL2 AND UE2.URL2=UE1.URL1) AND "
							  +"UE1.URL1 = MS.URL1 AND UE1.URL2=MS.URL2 "
							  +"UNION "
							  +"SELECT UE.URL1,IES.URL2 "
							  +"FROM USER_ENDORSE UE, INDIRECTENDORSEMENTSKILL IES "
							  +"WHERE UE.URL2 = IES.URL1;");
			
			rs7 = stmt.executeQuery("SELECT DISTINCT U1.USERNAME, U2.USERNAME "
							  +"FROM USERS U1, USERS U2, INDIRECTENDORSEMENTSKILL IES "
							  +"WHERE U1.URL = IES.URL1 AND U2.URL = IES.URL2;");
			
			while (rs7.next()){
				query7.add(rs7.getString(1)+" indirectly endorses "+rs7.getString(2));
			}
			
			request.setAttribute("query7", query7);
			
			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
	        rd.forward(request, response);
	        
	        /*
	         * Clear arrayLists so that if again a get request is made then the result of queries
	         * are not appended to the previous result.
	         */ 
	        query1.clear();
	        query2.clear();
	        query3.clear();
	        query4.clear();
	        query5.clear();
	        query6.clear();
	        query7.clear();
	        
		} catch (ClassNotFoundException | SQLException e) {
			/*
			 * Catch any errors while establishing connection to database through jdbc connector
			 * */
			System.out.println("Connection not established");
			e.printStackTrace();
		} finally {
			/*
			 * Once everything is done close the connection
			 * */
			try {
				if (rs1!=null) {		
					rs1.close();
				}
				if (rs2!=null) {		
					rs2.close();
				}
				if (rs3!=null) {		
					rs3.close();
				}
				if (rs4!=null) {		
					rs4.close();
				}
				if (rs5!=null) {		
					rs5.close();
				}
				if (rs6!=null) {		
					rs6.close();
				}
				if (rs7!=null) {		
					rs7.close();
				}
				if (stmt!=null) {
					stmt.close();
				}
				if (conn!=null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done without errors");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
