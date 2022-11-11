<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../../../global.jsp"%>
<%    String __strPageCode = "P0000000004";
    request.setAttribute("sublink", "../../../");
    request.setAttribute("title", "ธรุการคลัง ขาย");
//    request.setAttribute("css", Arrays.asList("css/sweetalert.css", "css/bootstrap-datetimepicker.min.css"));
    request.setAttribute("js", Arrays.asList(
            "../../../public/pickandpack/js/global.js",
            "../../../public/pickandpack/js/warehouse/administration/script.js"));
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
                                                    <label class="control-label">สแกนใบจัด</label>
                                                    <div class="input-group">
                                                        <span class="input-group-btn">
                                                            <button type="button" id="btn-scan-doc" class="btn btn-primary btn-flat"><i class="fa fa-camera"></i></button>
                                                        </span>
                                                        <input type="text" id="txt-scan-doc" class="form-control" placeholder="ใส่ข้อมูลค้นหา">
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-3 col-sm-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="control-label">ค้นหา INV/SO</label>
                                                    <div class="input-group">
                                                        <input type="text" id="txt-search" class="form-control"  placeholder="ใส่ข้อมูลค้นหา">
                                                        <span class="input-group-btn">
                                                            <button type="button" id="btn-search" class="btn btn-primary btn-flat"><i class="fa fa-search"></i></button>
                                                        </span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-3 col-sm-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="control-label">ค้นหาลูกค้า</label>
                                                    <div class="input-group">
                                                        <input type="text" id="txt-search-customer" class="form-control"  placeholder="ใส่ข้อมูลค้นหา">
                                                        <span class="input-group-btn">
                                                            <button type="button" id="btn-search-customer" class="btn btn-primary btn-flat"><i class="fa fa-search"></i></button>
                                                        </span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3 col-sm-3 col-xs-6">
                                                <div class="form-group">
                                                    <label class="control-label">จากวันที่</label>
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
                                                    <label class="control-label">ถึงวันที่</label>
                                                    <div class="input-group">
                                                        <div class="input-group-addon">
                                                            <i class="fa fa-calendar text-primary"></i>
                                                        </div>
                                                        <input type="text" id="txt-search-date-to" class="form-control">
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-3 col-sm-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="control-label">การจัดส่ง</label>
                                                    <select class="form-control" id="sel-send-type">
                                                        <option value="">ทั้งหมด</option>
                                                        <option value="0">รับเอง</option>
                                                        <option value="1">ส่งให้</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="col-md-3 col-sm-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="control-label">สถานะ</label>
                                                    <select class="form-control" id="sel-status-type">
                                                        <option value="">ทั้งหมด</option>
                                                        <option value="0">ปกติ</option>
                                                        <option value="1">ยกเลิก</option>
                                                        <option value="2">จัดเสร็จ</option>
                                                        <option value="3">พร้อมออก</option>
                                                        <option value="4">จัดเสร็จ บางส่วน</option>
                                                        <option value="5">พร้อมออก บางส่วน</option>
                                                        <option value="6">พิมพ์ใบจัดแล้ว</option>
                                                    </select>
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
                                            <div class="col-md-10 col-sm-6 col-xs-6">
                                                <ul class="nav navbar-right panel_toolbox">
                                                    <li><button type="button" id="btn-show-all" class="btn btn-primary btn-sm">แสดงทั้งหมด</button></li>
                                                    <li><button type="button" id="btn-refresh" class="btn btn-success btn-sm"><i class="fa fa-refresh"></i></button></li>
                                                </ul>
                                            </div>
                                        </div>
                                        <div class="clearfix"></div>
                                    </div>
                                    <div class="table-responsive">
                                        <table class="table table-condensed table-striped text-center" style="margin: 0; padding: 0; color: #000;">
                                            <thead>
                                            <th class="text-center">วันที่</th>
                                            <th class="text-center">เลขที่บิล</th>
                                            <th class="text-center">ชื่อลูกค้า</th>
                                            <th class="text-center">การจัดส่ง</th>
                                            <th class="text-center">ทะเบียนรถ</th>
                                            <th class="text-center">วันที่คาดว่าจะส่ง</th>
                                            <th class="text-center">เที่ยวที่</th>
                                            <th class="text-center">วันที่และเวลาเข้ารับสินค้า</th>
                                            <th class="text-center">สถานะ</th>
                                            <th class="text-center" colspan="3" style="width: 5%"><i class="fa fa-cog"></i></th>
                                            </thead>
                                            <tbody id="content-table-list">
                                                <tr><td colspan='12'><p class='text-center'><span class='fa fa-spinner fa-spin fa-2x fa-fw'></span> กำลังโหลดข้อมูล...</p></td></tr>
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
                <div class="modal fade" id="confirmDeleteApprove" tabindex="-1" role="dialog" aria-hidden="true">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h3 class="modal-title" id="exampleModalLabel">ตรวจพบใบจัดซ้ำ</h3>
                            </div>
                            <div class="modal-body" id="body-detail">

                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-dismiss="modal">ปิด</button>
                                <button type="button" class="btn btn-warning" id="delete-doc">ลบใบจัด</button>
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