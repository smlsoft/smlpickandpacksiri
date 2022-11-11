<%
    if (session.getAttribute("user_code") != null) {
        
    } else {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
%>