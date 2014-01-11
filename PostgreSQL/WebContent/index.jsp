<%
/****************************************************************************
							CSE532 -- Project 2
File name: index.jsp
Author(s): 	Gaurav Piyush (108996990 )
			Sudheer Kumar Vavilapalli (109203795 )
Brief description: 
	The file is the front end for the project.
	The user needs to click on "Get Query Results" button and all the 7 
	query results will be displayed in order.
	
	The query results are passed as a request attribute from server side.
	The attribute is caught on front end by accessing the attribute's 
	name (eg. ${query1}). We then iterate over the result set to display
	each item.
****************************************************************************/

/***************************************************************************
 *We pledge our honor that all parts of this project were done by us 
 *alone and without collaboration with anybody else.
 ***************************************************************************/
%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>LinkedOut</title>
</head>
<body>
<!--<p>Hello World</p>-->
<form action="ApiManager" method="get">
	<input type="submit" name="submit" value="Get Query Results">
</form>
<p>Query 1: Endorsement pairs sharing a common organization on 09/18/2013</p>
<c:forEach items = "${query1}" var = "item1">
	<c:out value="${item1}"></c:out><br/>
</c:forEach>
<p>Query 2: Highly qualified endorsements</p>
<c:forEach items = "${query2}" var = "item2">
	<c:out value="${item2}"></c:out><br/>
</c:forEach>
<p>Query 3: Users endorsed for unclaimed skills</p>
<c:forEach items = "${query3}" var = "item3">
	<c:out value="${item3}"></c:out><br/>
</c:forEach>
<p>Query 4: Strictly more skilled users</p>
<c:forEach items = "${query4}" var = "item4">
	<c:out value="${item4}"></c:out><br/>
</c:forEach>
<p>Query 5: Strictly more certified users</p>
<c:forEach items = "${query5}" var = "item5">
	<c:out value="${item5}"></c:out><br/>
</c:forEach>
<p>Query 6: Indirect endorsements</p>
<c:forEach items = "${query6}" var = "item6">
	<c:out value="${item6}"></c:out><br/>
</c:forEach>
<p>Query 7: Skill-descending indirect endorsements</p>
<c:forEach items = "${query7}" var = "item7">
	<c:out value="${item7}"></c:out><br/>
</c:forEach>

</body>
</html>