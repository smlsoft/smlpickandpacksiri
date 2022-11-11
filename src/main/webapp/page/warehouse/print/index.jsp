<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
    <%
        String __strActionName = request.getParameter("action_name") != null ? request.getParameter("action_name") : "";
        String __strRefCode = request.getParameter("ref_code") != null ? request.getParameter("ref_code") : "";
        String __strDocNo = request.getParameter("doc_no") != null ? request.getParameter("doc_no") : "";
        String __strWhCode = request.getParameter("wh_code") != null ? request.getParameter("wh_code") : "";
        String __strShelfCode = request.getParameter("shelf_code") != null ? request.getParameter("shelf_code") : "";
        String __strTransFlag = request.getParameter("trans_flag") != null ? request.getParameter("trans_flag") : "";
        String __strIsPrint = request.getParameter("is_print") != null ? request.getParameter("is_print") : "";
        String __strCarCode = request.getParameter("car_code") != null ? request.getParameter("car_code") : "";
        String __strShipmentCode = request.getParameter("shipment_code") != null ? request.getParameter("shipment_code") : "";
    %>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
        <title>SML Pick And Pack | พิมพ์ใบจัด</title>

        <!-- Bootstrap -->
        <link href="../../../public/assets/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <link href="../../../public/plugins/sweetalert/css/sweetalert2.min.css" rel="stylesheet">
        <link href="../../../public/plugins/sweetalert/themes/twitter/twitter.css" rel="stylesheet">
        <style type="text/css" media="print">
            @page {
                size: A5;
                margin-left: 5px;
                margin-top: 5px;
                padding-right: 10px;
                margin-bottom: 5px;
                size: landscape;
            }

            div.content-print-layout
            {
                page-break-after: always;
                page-break-inside: avoid;
            }

        </style>
    </head>

    <body>
        <!-- HIDDEN CONTENT -->
        <input type="hidden" id="h_action_name" value="<%=__strActionName%>">
        <input type="hidden" id="h_ref_code" value="<%=__strRefCode%>">
        <input type="hidden" id="h_doc_no" value="<%=__strDocNo%>">
        <input type="hidden" id="h_wh_code" value="<%=__strWhCode%>">
        <input type="hidden" id="h_shelf_code" value="<%=__strShelfCode%>">
        <input type="hidden" id="h_trans_flag" value="<%=__strTransFlag%>">
        <input type="hidden" id="h_is_print" value="<%=__strIsPrint%>">
        <input type="hidden" id="h_car_code" value="<%=__strCarCode%>">
        <input type="hidden" id="h_shipment_code" value="<%=__strShipmentCode%>">

        <div class="container-fluid" id="content-list" style="margin: 0"></div>

        <script src="../../../public/plugins/jquery/js/jquery.min.js"></script>
        <script src="../../../public/assets/bootstrap/js/bootstrap.min.js"></script>
        <script src="../../../public/plugins/sweetalert/js/sweetalert2.min.js"></script>
        <script src="../../../public/pickandpack/js/warehouse/print/script.js"></script>
    </body>
</html>