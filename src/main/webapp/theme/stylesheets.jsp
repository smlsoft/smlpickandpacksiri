<%@page import="utils.RandomID"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%
    List css = request.getAttribute("css") == null ? new ArrayList<String>() : (List) request.getAttribute("css");
    String v = "?_=" + RandomID.rand();
%>
<!-- Bootstrap -->
<link rel="stylesheet" href="${sublink}public/assets/bootstrap/css/bootstrap.min.css">
<!-- Font Awesome -->
<link rel="stylesheet" href="${sublink}public/assets/font-awesome/css/font-awesome.min.css">
<!-- Ionicons -->
<link rel="stylesheet" href="${sublink}public/assets/Ionicons/css/ionicons.min.css">
<!-- Pace -->
<link rel="stylesheet" href="${sublink}public/plugins/pace/themes/red/pace-theme-minimal.css">
<!-- Animate -->
<link rel="stylesheet" href="${sublink}public/plugins/animate/css/animate.css">
<!-- DatePicker -->
<link rel="stylesheet" href="${sublink}public/plugins/datepicker/css/bootstrap-datepicker3.min.css">
<!-- Select2 -->
<link rel="stylesheet" href="${sublink}public/plugins/select2/css/select2.min.css">
<!-- SweetAlert -->
<link rel="stylesheet" href="${sublink}public/plugins/sweetalert/css/sweetalert2.min.css">
<link rel="stylesheet" href="${sublink}public/plugins/sweetalert/themes/twitter/twitter.css">
<!-- Toastr -->
<link rel="stylesheet" href="${sublink}public/plugins/toastr/css/toastr.min.css">
<!-- formValidation -->
<link rel="stylesheet" href="${sublink}public/plugins/formValidation/css/formValidation.min.css">
<!-- customScrollbar -->
<link rel="stylesheet" href="${sublink}public/plugins/customScrollbar/css/jquery.mCustomScrollbar.css">
<!-- iCheck -->
<link rel="stylesheet" href="${sublink}public/plugins/iCheck/green.css">
<!-- Theme style -->
<!--<link rel="stylesheet" href="${sublink}public/assets/adminLTE/css/AdminLTE.min.css">
<link rel="stylesheet" href="${sublink}public/assets/adminLTE/css/skins/skin-blue.min.css">-->
<link rel="stylesheet" href="${sublink}public/assets/custom/css/custom.min.css">
<!-- Google Font -->
<!--<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600,700,300italic,400italic,600italic">-->
<!--<link href="${sublink}assets/css/custom/css/css.css" rel="stylesheet">-->

<style type="text/css">
/*    body {
        font-family: 'Kanit', sans-serif;
    }*/
    .vertical-center {
        vertical-align: middle !important;
    }
</style>
<%
    for (int i = 0; i < css.size(); i++) {
        out.print("<link rel='stylesheet' href='" + css.get(i).toString() + v + "'>");
    }
%>