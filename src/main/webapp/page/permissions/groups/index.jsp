<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../../../global.jsp"%>
<%    String __strPageCode = "P0000000010";
    request.setAttribute("sublink", "../../../");
    request.setAttribute("title", "กำหนดสิทธิ์ ตามกลุ่มผู้ใช้งาน");
//    request.setAttribute("css", Arrays.asList("css/sweetalert.css", "css/bootstrap-datetimepicker.min.css"));
    request.setAttribute("js", Arrays.asList(
            "../../../public/pickandpack/js/global.js",
            "../../../public/pickandpack/js/permissions/groups/script.js"));
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
                                                    <label class="control-label">ค้นหาข้อมูล</label>
                                                    <div class="input-group">
                                                        <input type="text" id="txt-search" class="form-control"  placeholder="ใส่ข้อมูลค้นหา">
                                                        <span class="input-group-btn">
                                                            <button type="button" id="btn-search" class="btn btn-primary btn-flat"><i class="fa fa-search"></i></button>
                                                        </span>
                                                    </div>
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
                        <!--Content Form Group-->
                        <div class="row" id="content-form-group-box" style="display: none;">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="x_panel">
                                    <div class="x_title">
                                        <h2>ฟอร์ม กลุ่มผู้ใช้งาน</h2>
                                        <div class="clearfix"></div>
                                    </div>
                                    <div class="x_content">
                                        <form id="frm-group">
                                            <div class="row">
                                                <div class="col-md-3 col-sm-4 col-xs-12">
                                                    <div class="form-group">
                                                        <label for="txt_group_code" class="control-label">รหัสกลุ่มผู้ใช้งาน</label>
                                                        <input type="text" id="txt_group_code" name="txt_group_code" class="form-control" placeholder="กรุณาใส่ข้อมูล">
                                                    </div>
                                                </div>
                                                <div class="col-md-3 col-sm-4 col-xs-12">
                                                    <div class="form-group">
                                                        <label for="txt_group_name" class="control-label">ชื่อกลุ่มผู้ใช้งาน</label>
                                                        <input type="text" id="txt_group_name" name="txt_group_name" class="form-control" placeholder="กรุณาใส่ข้อมูล">
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="ln_solid"></div>
                                            <div class="row">
                                                <div class="col-md-12 col-sm-12 col-xs-12 text-right">
                                                    <div class="form-group">
                                                        <button type="submit" class="btn btn-success btn-flat">บันทึก</button>
                                                        <button type="button" id="btn-cancel" class="btn btn-danger btn-flat">ยกเลิก</button>
                                                    </div>  
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!--Content Table Group-->
                        <div class="row" id="content-table-group-box" style="display: none;">
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
                                            <div class="col-md-10 col-sm-6 col-xs-6">
                                                <ul class="nav navbar-right panel_toolbox">
                                                    <li><button type="button" id="btn-add" class="btn btn-primary btn-sm">เพิ่มข้อมูล</button></li>
                                                    <li><button type="button" id="btn-refresh" class="btn btn-success btn-sm"><i class="fa fa-refresh"></i></button></li>
                                                </ul>
                                            </div>
                                        </div>
                                        <div class="clearfix"></div>
                                    </div>
                                    <div class="table-responsive">
                                        <table class="table table-condensed table-striped text-center" style="margin: 0; padding: 0; color: #000;">
                                            <thead>
                                            <th class="text-center">รหัส ~ ชื่อ</th>
                                            <th class="text-center" colspan="3" style="width: 5%"><i class="fa fa-cog"></i></th>
                                            </thead>
                                            <tbody id="content-table-list">
                                                <tr><td colspan='4'><p class='text-center'><span class='fa fa-spinner fa-spin fa-2x fa-fw'></span> กำลังโหลดข้อมูล...</p></td></tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!--Content Table Permission Group-->
                        <div class="row" id="content-table-permission-group-box" style="display: none;">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="x_panel">
                                    <div class="x_title">
                                        <div class="row">
                                            <div class="col-md-2 col-sm-6 col-xs-6">

                                            </div>
                                            <div class="col-md-10 col-sm-6 col-xs-6">
                                                <ul class="nav navbar-right panel_toolbox">
                                                    <li><button type="button" id="btn-add" class="btn btn-primary btn-sm">เพิ่มข้อมูล</button></li>
                                                    <li><button type="button" id="btn-back" class="btn btn-danger btn-sm">กลับ</button></li>
                                                </ul>
                                            </div>
                                        </div>
                                        <div class="clearfix"></div>
                                    </div>
                                    <div class="table-responsive">
                                        <table class="table table-condensed table-striped text-center" style="margin: 0; padding: 0; color: #000;">
                                            <thead>
                                            <th class="text-center">ชื่อเพจ</th>
                                            <th class="text-center" style="width: 5%">ใช้งาน</th>
                                            <th class="text-center" style="width: 5%">เพิ่ม</th>
                                            <th class="text-center" style="width: 5%">แก้ไข</th>
                                            <th class="text-center" style="width: 5%">ลบ</th>
                                            <th class="text-center" style="width: 10%">พิมพ์อีกครั้ง</th>
                                            <th class="text-center" style="width: 5%"><i class="fa fa-cog"></i></th>
                                            </thead>
                                            <tbody id="content-table-list">
                                                <tr><td colspan='7'><p class='text-center'><span class='fa fa-spinner fa-spin fa-2x fa-fw'></span> กำลังโหลดข้อมูล...</p></td></tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!--Content Table Page-->
                        <div class="row" id="content-table-page-box" style="display: none;">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="x_panel">
                                    <div class="x_title">
                                        <div class="row">
                                            <div class="col-md-2 col-sm-6 col-xs-6">

                                            </div>
                                            <div class="col-md-10 col-sm-6 col-xs-6">
                                                <ul class="nav navbar-right panel_toolbox">
                                                    <li><button type="button" id="btn-back" class="btn btn-danger btn-sm">กลับ</button></li>
                                                </ul>
                                            </div>
                                        </div>
                                        <div class="clearfix"></div>
                                    </div>
                                    <div class="table-responsive">
                                        <table class="table table-condensed table-striped text-center" style="margin: 0; padding: 0; color: #000;">
                                            <thead>
                                            <th class="text-center">รหัสเพจ ~ ชื่อเพจ</th>
                                            <th class="text-center" style="width: 5%"><i class="fa fa-cog"></i></th>
                                            </thead>
                                            <tbody id="content-table-list">
                                                <tr><td colspan='7'><p class='text-center'><span class='fa fa-spinner fa-spin fa-2x fa-fw'></span> กำลังโหลดข้อมูล...</p></td></tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!--Content Table User Group-->
                        <div class="row" id="content-table-user-group-box" style="display: none;">
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
                                            <div class="col-md-10 col-sm-6 col-xs-6">
                                                <ul class="nav navbar-right panel_toolbox">
                                                    <li><button type="button" id="btn-add" class="btn btn-primary btn-sm">เพิ่มข้อมูล</button></li>
                                                </ul>
                                            </div>
                                        </div>
                                        <div class="clearfix"></div>
                                    </div>
                                    <div class="table-responsive">
                                        <table class="table table-condensed table-striped text-center" style="margin: 0; padding: 0; color: #000;">
                                            <thead>
                                            <th class="text-center">รหัสชื่อผู้ใช้งาน ~ ชื่อ - นามสกุล</th>
                                            <th class="text-center" style="width: 5%"><i class="fa fa-cog"></i></th>
                                            </thead>
                                            <tbody id="content-table-list">
                                                <tr><td colspan='2'><p class='text-center'><span class='fa fa-spinner fa-spin fa-2x fa-fw'></span> กำลังโหลดข้อมูล...</p></td></tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!--Content Table User List-->
                        <div class="row" id="content-table-user-list-box" style="display: none;">
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
                                            <div class="col-md-10 col-sm-6 col-xs-6">
                                                <ul class="nav navbar-right panel_toolbox">
                                                    <li><button type="button" id="btn-back" class="btn btn-danger btn-sm">กลับ</button></li>
                                                </ul>
                                            </div>
                                        </div>
                                        <div class="clearfix"></div>
                                    </div>
                                    <div class="table-responsive">
                                        <table class="table table-condensed table-striped text-center" style="margin: 0; padding: 0; color: #000;">
                                            <thead>
                                            <th class="text-center">รหัสชื่อผู้ใช้งาน ~ ชื่อ - นามสกุล</th>
                                            <th class="text-center" style="width: 5%"><i class="fa fa-cog"></i></th>
                                            </thead>
                                            <tbody id="content-table-list">
                                                <tr><td colspan='2'><p class='text-center'><span class='fa fa-spinner fa-spin fa-2x fa-fw'></span> กำลังโหลดข้อมูล...</p></td></tr>
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

