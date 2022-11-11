<%
    HttpSession __session = request.getSession();
    __session.removeAttribute("");
    __session.removeAttribute("provider_code");
    __session.removeAttribute("database_name");
    __session.removeAttribute("branch_code");
    __session.removeAttribute("branch_name");
    __session.removeAttribute("user_code");
    __session.removeAttribute("user_name");
    __session.removeAttribute("system_id");
    __session.removeAttribute("session_id");

    response.sendRedirect(request.getContextPath() + "/index.jsp");
%>