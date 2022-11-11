<%@page import="utils.RandomID"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%
    List js = request.getAttribute("js") == null ? new ArrayList<String>() : (List) request.getAttribute("js");
    String v = "?_=" + RandomID.rand();
%>
<!-- jQuery 3 -->
<script type="text/javascript" src="${sublink}public/plugins/jquery/js/jquery.min.js"></script>
<!-- Bootstrap 3.3.7 -->
<script type="text/javascript" src="${sublink}public/assets/bootstrap/js/bootstrap.min.js"></script>
<!-- fastClick -->
<script type="text/javascript" src="${sublink}public/plugins/fastclick/js/fastclick.js"></script>
<!-- Pace -->
<script type="text/javascript" src="${sublink}public/plugins/pace/js/pace.min.js"></script>
<!-- BackStretch -->
<script type="text/javascript" src="${sublink}public/plugins/backstretch/js/jquery.backstretch.min.js"></script>
<!-- formValidation -->
<script type="text/javascript" src="${sublink}public/plugins/formValidation/js/formValidation.min.js"></script>
<script type="text/javascript" src="${sublink}public/plugins/formValidation/js/framework/bootstrap.min.js"></script>
<script type="text/javascript" src="${sublink}public/plugins/formValidation/js/language/th_TH.js"></script>
<!-- DatePicker -->
<script type="text/javascript" src="${sublink}public/plugins/datepicker/js/bootstrap-datepicker.min.js"></script>
<script type="text/javascript" src="${sublink}public/plugins/datepicker/locales/bootstrap-datepicker.th.min.js"></script>
<!-- Select2 -->
<script type="text/javascript" src="${sublink}public/plugins/select2/js/select2.full.min.js"></script>
<script type="text/javascript" src="${sublink}public/plugins/select2/js/locales/th.js"></script>
<!-- SweetAlert -->
<script type="text/javascript" src="${sublink}public/plugins/sweetalert/js/sweetalert2.min.js"></script>
<!-- Toastr -->
<script type="text/javascript" src="${sublink}public/plugins/toastr/js/toastr.min.js"></script>
<!-- customScrollbar -->
<script type="text/javascript" src="${sublink}public/plugins/customScrollbar/js/jquery.mCustomScrollbar.js"></script>
<!-- iCheck -->
<script type="text/javascript" src="${sublink}public/plugins/iCheck/icheck.js"></script>
<!-- input mask -->
<script type="text/javascript" src="${sublink}public/plugins/inputMask/js/jquery.inputmask.js"></script>
<script type="text/javascript" src="${sublink}public/plugins/inputMask/js/jquery.inputmask.date.extensions.js"></script>
<script type="text/javascript" src="${sublink}public/plugins/inputMask/js/jquery.inputmask.extensions.js"></script>
<!-- Theme style -->
<!--<script type="text/javascript" src="${sublink}public/assets/adminLTE/js/adminlte.min.js"></script>-->
<script type="text/javascript" src="${sublink}public/assets/custom/js/custom.min.js"></script>

<%
    for (int i = 0; i < js.size(); i++) {
        out.print("<script src='" + js.get(i).toString() + v + "'></script>");
    }
%>