<%@page import="main.MainFile"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@page import="main.Tree"%>  
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>TestWebProject</title>
<script >

/**
 * Showing an input form to get value for updating content of node
 */
function ShowForm (id,action) {
	var frm = document.getElementById("FrmUpdate");
	frm.style.display = 'block';
	frm.id.value = id;
	frm.action.value = action;
}

/** 
 * Show confirmation window before deleting a node
 */
function GetConfirmation(id){
	 var txt;
	 var r = confirm("Are you sure that you want to delete this node?");
	 if (r == true) {
	    document.location = 'TreeControler?action=delete&id='+id;
	 } else {
	    return false;
	 }
 }
 
/** 
 * Send information of cliked node to TreeContorller servelt for adding a child for it
 */
 function RedirectForm(id){
	 document.location =  'TreeControler?action=add&id='+id;
 }

/**
 * Show the Set button on form and send the id of source node to the FrmFetValue form
 */
 function StartMove(id){
	 var frm = document.getElementById("FrmGetValue"); 
	 frm.id.value = id;
	var x = document.getElementsByClassName("SetElement");
	var i;
	for (i = 0; i < x.length; i++) {
	    x[i].style.display = 'inline';
	}
 }

/**
 * Send information of id of source node and id of destination node to the TreeControler servlet by submiting FrmGetValue form
 */
 function FinishMove(idParent){
	 var frm = document.getElementById("FrmGetValue");
	 frm.idParent.value = idParent;
	 frm.action.value = 'move';
	 frm.submit();
 }
  
</script>
</head>

<body>
<jsp:useBean id="obj" class="main.Tree">  
</jsp:useBean>  
<%  
out.print(Tree.callingJSPFile());
%> 
<form id = "FrmGetValue" style="display: none" name="FrmGetValue" action='TreeControler' method="get">
   <input type="hidden"  name="id" id= "id" /> <br /> <br /> 
   <input type="hidden"  name="idParent" id= "idParent" /> <br /> <br />
    <input type="hidden"  name="action" id= "action" /> <br /> <br />
   <input  type = "submit" value ="Submit"  />
</form>

<form id = "FrmUpdate" style="display: none" name="FrmUpdate" action='TreeControler' method="post">
   <input type="hidden"  name="id" id= "id" /> <br /> <br /> 
   <input type="hidden"  name="action" id= "action" /> <br /> <br /> 
   Content : <input type="text"  name="content"/> <br /> <br /> 
   <input  type = "submit" value ="Submit"  />
</form>
</body>
</html>