<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../../../global.jsp"%>
<%    String __strPageCode = "P0000000013";
    request.setAttribute("sublink", "../../../");
    request.setAttribute("title", "รายงาน ระยะเวลาในการจัดสินค้าและส่งมอบสินค้า");
//    request.setAttribute("css", Arrays.asList("css/sweetalert.css", "css/bootstrap-datetimepicker.min.css"));
    request.setAttribute("js", Arrays.asList(
            "../../../public/pickandpack/js/global.js",
            "../../../public/pickandpack/js/reports/report2/script.js"));
    HttpSession __session = request.getSession();
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!-- Meta, title, CSS, favicons, etc. -->
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>SML Pick And Pack | ${title}</title>

        <!-- StyleSheets -->
        <jsp:include  page="${sublink}theme/stylesheets.jsp" flush="true" />
    </head>

    <body class="nav-md">
        <!-- HIDDEN -->
        <input type="hidden" id="h_page_code" value="<%=__strPageCode%>">
        <input type="hidden" id="h_user_code" value="${user_code}">
        <input type="hidden" id="h_sub_link" value="${sublink}">
        <!-- CONTENT -->
        <div class="container body">
            <div class="main_container">
                <!-- sidebar -->
                <jsp:include page="${sublink}theme/sidebar.jsp" flush="true" />
                <!-- top navigation -->
                <jsp:include page="${sublink}theme/header.jsp" flush="true" />

                <!-- page content -->
                <div class="right_col" role="main">
                    <div class="">
                        <div class="page-title">
                            <div class="title">
                                <div class="col-md-12 col-sm-12 col-xs-12">
                                    <h3>${title}</h3>
                                </div>
                            </div>
                        </div>

                        <div class="clearfix"></div>
                        <!--Content Error-->
                        <div class="row clearfix" id="content-error-box" style="display: none;">
                            <div id="content-error-list" class="col-md-12 col-sm-12 col-xs-12">
                                <div class="alert alert-danger"><strong id="txt-err-title" style="color: #FFF;" class="h4">ข้อความระบบ</strong><p id="txt-err-msg" style="color: #000;">ทดสอบ</p></div>
                            </div>
                        </div>
                        <!--Content Search-->
                        <div class="row" id="content-search-box" style="display: none;">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="x_panel">
                                    <div class="x_content">
                                        <div class="row">
                                            <div class="col-md-3 col-sm-3 col-xs-12">
                                                <div class="form-group">
                                                    <label for="sel_branch_list" class="control-label">สาขา</label>
                                                    <select id="sel_branch_list" class="form-control"></select>
                                                </div>
                                            </div>
                                            <div class="col-md-3 col-sm-3 col-xs-12">
                                                <div class="form-group">
                                                    <label for="sel_group_location" class="control-label">คลัง</label>
                                                    <select id="sel_group_location" class="form-control"></select>
                                                </div>
                                            </div>
                                            <div class="col-md-3 col-sm-3 col-xs-6">
                                                <div class="form-group">
                                                    <label for="txt-search-date-from" class="control-label">จากวันที่</label>
                                                    <div class="input-group">
                                                        <div class="input-group-addon">
                                                            <i class="fa fa-calendar text-primary"></i>
                                                        </div>
                                                        <input type="text" id="txt-search-date-from" class="form-control">
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-3 col-sm-3 col-xs-6">
                                                <div class="form-group">
                                                    <label for="txt-search-date-to" class="control-label">ถึงวันที่</label>
                                                    <div class="input-group">
                                                        <div class="input-group-addon">
                                                            <i class="fa fa-calendar text-primary"></i>
                                                        </div>
                                                        <input type="text" id="txt-search-date-to" class="form-control">
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-12 col-sm-12 col-xs-12">
                                                <div class="form-group">
                                                    <button type="button" id="btn-search" class="btn btn-primary btn-flat"><i class="fa fa-search"></i> ค้นหา</button>
                                                    <button type="button" id="btn-export-excel" class="btn btn-success btn-flat"><i class="fa fa-file-excel-o"></i> ส่งออกไฟล์ Excel</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!--Content Loading-->
                        <div class="row" id="content-loading-box">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="x_panel">
                                    <div class="x_content text-center">
                                        <h3><i class="fa fa-spinner fa-spin"></i> กำลังโหลดข้อมูลที่จำเป็นโปรดรอสักครู่...</h3>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!--Content Table-->
                        <div class="row" id="content-table-box" style="display: none;">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="x_panel">
                                    <div class="x_title">
                                        <div class="row">
                                            <div class="col-md-2 col-sm-6 col-xs-6">
                                                <div class="form-group">
                                                    <div class="input-group input-group-sm">
                                                        <select id="sel-table-rows" class="form-control">
                                                            <option value="20">20</option>
                                                            <option value="50">50</option>
                                                            <option value="70">70</option>
                                                        </select>
                                                        <span class="input-group-addon" style="color: #000;">แถว</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="clearfix"></div>
                                    </div>
                                    <div class="table-responsive">
                                        <table class="table table-condensed table-striped table-bordered text-center" style="margin: 0; padding: 0; color: #000;">
                                            <thead>
                                                <tr>
                                                    <th class="text-center vertical-center" rowspan="2"><strong>คลัง</strong></th>
                                                    <th class="text-center vertical-center" rowspan="2"><strong>พื้นที่</strong></th>
                                                    <th class="text-center vertical-center" rowspan="2"><strong>วันที่ใบจัด</strong></th>
                                                    <th class="text-center vertical-center" rowspan="2"><strong>เลขที่ใบจัด</strong></th>
                                                    <th class="text-center vertical-center" colspan="2"><strong>เวลา</strong></th>
                                                    <th class="text-center vertical-center"><strong>ระยะเวลาที่จัด</strong></th>
                                                    <th class="text-center vertical-center"><strong>เวลา</strong></th>
                                                    <th class="text-center vertical-center"><strong>ระยะเวลาส่งมอบ</strong></th>
                                                </tr>
                                                <tr>
                                                    <th class="text-center vertical-center">เริ่มจัด</th>
                                                    <th class="text-center vertical-center">จัดเสร็จ</th>
                                                    <th class="text-center vertical-center">(ชม. - นาที)</th>
                                                    <th class="text-center vertical-center">ส่งมอบ (ปิดใบจัด)</th>
                                                    <th class="text-center vertical-center">(ชม. - นาที)</th>
                                                </tr>
                                            </thead>
                                            <tbody id="content-table-list">
                                                <tr><td colspan='14'><p class='text-center'><span class='fa fa-spinner fa-spin fa-2x fa-fw'></span> กำลังโหลดข้อมูล...</p></td></tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!--Content Pagination-->
                        <div class="row" id="content-pagination-box" style="display: none;">
                            <div class="col-md-12 col-sm-12 col-xs-12 text-center" id="content-pagination-list">
                                <div class="form-inline">
                                    <div class="input-group">
                                        <div class="input-group-btn">
                                            <button id="btn-pagi-first" class="btn btn-default btn-flat" type="button"><i class="fa fa-angle-double-left"></i></button>
                                            <button id="btn-pagi-previous" class="btn btn-default btn-flat" type="button"><i class="fa fa-angle-left"></i></button>
                                        </div>
                                        <input type="text" id='txt-pagination' class="form-control text-center"placeholder="กรุณาใส่หมายเลขหน้า">
                                        <div class="input-group-btn">
                                            <button id="btn-pagi-next" class="btn btn-default btn-flat" type="button" style="margin-right: 0px;"><i class="fa fa-angle-right"></i></button>
                                            <button id="btn-pagi-last" class="btn btn-default btn-flat" type="button"><i class="fa fa-angle-double-right"></i></button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- /page content -->

                <!-- footer content -->
                <jsp:include page="${sublink}theme/footer.jsp" flush="true" />
                <!-- /footer content -->
            </div>
        </div>
        <jsp:include page="${sublink}theme/scripts.jsp" flush="true" />
    </body>

</html>