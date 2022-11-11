<%@page import="java.util.Properties"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    HttpSession __session = request.getSession();
    String __tmpProviderCode = __session.getAttribute("tmp_provider_code") != null ? !__session.getAttribute("tmp_provider_code").toString().isEmpty() ? __session.getAttribute("tmp_provider_code").toString() : "" : "";
    String __tmpUserCode = __session.getAttribute("tmp_user_code") != null ? !__session.getAttribute("tmp_user_code").toString().isEmpty() ? __session.getAttribute("tmp_user_code").toString() : "" : "";

%>

<%    Properties projectPpt = new Properties();
    projectPpt.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("project.properties"));
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>pickandpack | หน้าจอเข้าสู่ระบบ</title>
        <!-- Style Sheet -->
        <link rel="stylesheet" href="public/assets/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" href="public/assets/font-awesome/css/font-awesome.min.css">
        <link rel="stylesheet" href="public/assets/Ionicons/css/ionicons.min.css">
        <link rel="stylesheet" href="public/plugins/formValidation/css/formValidation.min.css">
        <link rel="stylesheet" href="public/plugins/pace/themes/white/pace-theme-minimal.css">
        <link rel="stylesheet" href="public/plugins/sweetalert/css/sweetalert2.min.css">
        <link rel="stylesheet" href="public/plugins/animate/css/animate.css">
        <link rel="stylesheet" href="public/plugins/toastr/css/toastr.min.css">
        <link rel="stylesheet" href="public/plugins/datepicker/css/bootstrap-datepicker3.min.css">
        <link rel="stylesheet" href="public/plugins/select2/css/select2.min.css">
        <link rel="stylesheet" href="public/assets/adminLTE/css/AdminLTE.min.css">
    </head>

    <body class="hold-transition login-page">
        <input type="hidden" id="version" value="<%=projectPpt.getProperty("version")%>">

        <!-- CONTENT AUTHENTICATION -->
        <div class="login-box" id="content-authentication-box">
            <div class="login-logo"><a href="#" style="color: #FFF;"><i class="fa fa-key"></i> หน้าจอเข้าสู่ระบบ</a></div>
            <form id="frm-login">
                <div class="login-box-body">
                    <p class="text-muted">ป้อนรหัสกิจการ และรหัสพนักงาน และรหัสผ่าน เพื่อเข้าสู่ระบบ ถ้าถูกต้องจะแสดงฐานข้อมูลให้เลือกใช้ต่อไป</p>
                    <hr>
                    <div class="form-group">
                        <label for="txt_provider_code" class="control-label">รหัสกิจการ</label>
                        <input type="text" id="txt_provider_code" name="txt_provider_code" class="form-control" placeholder="กรุณาใส่ รหัสกิจการ" value="<%=__tmpProviderCode%>">
                    </div>
                    <div class="form-group">
                        <label for="txt_user_code" class="control-label">รหัสพนักงาน</label>
                        <input type="text" id="txt_user_code" name="txt_user_code" class="form-control" placeholder="กรุณาใส่ รหัสพนักงาน" value="<%=__tmpUserCode%>">
                    </div>
                    <div class="form-group">
                        <label for="txt_user_password" class="control-label">รหัสผ่าน</label>
                        <input type="password" id="txt_user_password" name="txt_user_password" class="form-control" placeholder="กรุณาใส่ รหัสผ่าน">
                    </div>
                    <div class="form-group text-right">
                        <button type="submit" id="btn-submit" class="btn btn-success btn-flat">เข้าสู่ระบบ</button>
                    </div>
                </div>
            </form>
        </div>
        <!-- CONTENT DATABASE-->
        <div class="login-box" id="content-database-box" style="display: none;">
            <div class="login-logo"><a href="#" style="color: #FFF;"><i class="fa fa-database"></i> เลือกฐานข้อมูล</a></div>
            <div class="login-box-body">
                <div class="table-responsive" style="margin: 0; padding: 0;">
                    <table class="table table-bordered table-hover text-center">
                        <tbody id="content-table-list"></tbody>
                    </table>
                </div>
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12 text-center">
                        <button type="button" id="btn-relogin" class="btn btn-warning btn-flat">เข้าสู่ระบบใหม่</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- CONTENT VERIFY DATABASE-->
        <div class="login-box" id="content-verify-database-box" style="display: none;">
            <div class="login-logo"><a href="#" style="color: #FFF;"><i class="fa fa-database"></i> ตรวจสอบฐานข้อมูล</a></div>
            <div class="login-box-body">
                <pre id="log" data-stop="${param.stop}" style="border-radius: 0;padding-bottom: 0; margin-bottom: 0"></pre>
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12 text-center">
                        <!--<button type="button" id="btn-relogin" class="btn btn-warning btn-flat">เข้าสู่ระบบใหม่</button>-->
                    </div>
                </div>
            </div>
        </div>
        <!-- CONTENT BRANCH -->
        <div class="login-box" id="content-branch-box" style="display: none;">
            <div class="login-logo"><a href="#" style="color: #FFF;"><i class="fa fa-home"></i> เลือกสาขา</a></div>
            <div class="login-box-body">
                <div class="table-responsive" style="margin: 0; padding: 0;">
                    <table class="table table-bordered table-hover text-center">
                        <tbody id="content-table-list"></tbody>
                    </table>
                </div>
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12 text-center">
                        <button type="button" id="btn-relogin" class="btn btn-warning btn-flat">เข้าสู่ระบบใหม่</button>
                    </div>
                </div>
            </div>
        </div>


        <!-- CONTENT SYSTEM ID -->
        <div class="login-box" id="content-system-id-box" style="display: none;">
            <div class="login-logo"><a href="#" style="color: #FFF;"><i class="fa fa-home"></i> กำหนดหมายเลขเครื่อง</a></div>
            <div class="login-box-body">
                <p class="text-muted">กำหนดหมายเลขเครื่องเข้ากลุ่มคลังสินค้าและที่เก็บสินค้า</p>
                <hr>
                <form id="frm-system-id">
                    <div class="form-group">
                        <label for="sel_group_code" class="control-label">กลุ่มคลังสินค้าและที่เก็บสินค้า</label>
                        <div class="input-group">
                            <span class="input-group-btn">
                                <button type="button" id="btn-add-warehouse" class="btn btn-primary btn-flat">เพิ่ม</button>
                            </span>
                            <select id="sel_group_code" name="sel_group_code" class="form-control"></select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="txt_system_id" class="control-label">หมายเลขเครื่อง</label>
                        <input type="text" id="txt_system_id" name="txt_system_id" class="form-control" readonly="true">
                    </div>
                    <div class="form-group text-right">
                        <button type="button" id="btn-relogin" class="btn btn-warning btn-flat">เข้าสู่ระบบใหม่</button>
                        <button type="submit" id="btn-submit" class="btn btn-success btn-flat">บันทึก</button>
                    </div>
                </form>
            </div>
        </div>
        <!-- CONTENT WAREHOUSE -->
        <div class="login-box" id="content-warehouse-box" style="display: none;">
            <div class="login-logo"><a href="#" style="color: #FFF;"><i class="fa fa-home"></i> เพิ่มกลุ่มคลังสินค้าและสถานที่เก็บสินค้า</a></div>
            <div class="login-box-body">
                <form id="frm-warehouse">
                    <div class="form-group">
                        <label for="txt_group_code" class="control-label">รหัสกลุ่ม</label>
                        <input type="text" id="txt_group_code" name="txt_group_code" class="form-control" placeholder="กรุณาใส่ข้อมูล">
                    </div>
                    <div class="form-group">
                        <label for="txt_group_name" class="control-label">ชื่อกลุ่ม</label>
                        <input type="text" id="txt_group_name" name="txt_group_name" class="form-control" placeholder="กรุณาใส่ข้อมูล">
                    </div>
                    <div class="form-group">
                        <label for="sel_warehouse_code" class="control-label">คลังสินค้า</label>
                        <select id="sel_warehouse_code" name="sel_warehouse_code" class="form-control select2" multiple="true"></select>
                    </div>
                    <div class="form-group">
                        <label for="sel_shelf_code" class="control-label">สถานที่เก็บ</label>
                        <select id="sel_shelf_code" name="sel_shelf_code" class="form-control select2" multiple="true"></select>
                    </div>
                    <div class="form-group text-right">
                        <button type="button" id="btn-back" class="btn btn-danger btn-flat">กลับ</button>
                        <button type="submit" id="btn-submit" class="btn btn-success btn-flat">บันทึก</button>
                    </div>
                </form>
            </div>
        </div>
        <!-- CONTENT TMS CONFIG DATE -->
        <div class="login-box" id="content-tms-config-date-box" style="display: none;">
            <div class="login-logo"><a href="#" style="color: #FFF;"><i class="fa fa-home"></i> กำหนดวันที่เอกสารเริ่มต้นที่จะดึง</a></div>
            <div class="login-box-body">
                <form id="frm-tms-config-date">
                    <div class="form-group">
                        <label for="txt_begin_date" class="control-label">วันที่เริ่มต้น</label>
                        <input type="text" id="txt_begin_date" name="txt_begin_date" class="form-control select2">
                    </div>
                    <div class="form-group text-right">
                        <button type="button" id="btn-relogin" class="btn btn-warning btn-flat">เข้าสู่ระบบใหม่</button>
                        <button type="submit" id="btn-submit" class="btn btn-success btn-flat">บันทึก</button>
                    </div>
                </form>
            </div>
        </div>
        <!-- Javascript -->
        <script type="text/javascript" src="public/plugins/jquery/js/jquery.min.js"></script>
        <script type="text/javascript" src="public/assets/bootstrap/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="public/plugins/pace/js/pace.min.js"></script>
        <script type="text/javascript" src="public/plugins/backstretch/js/jquery.backstretch.min.js"></script>
        <script type="text/javascript" src="public/plugins/formValidation/js/formValidation.min.js"></script>
        <script type="text/javascript" src="public/plugins/formValidation/js/framework/bootstrap.min.js"></script>
        <script type="text/javascript" src="public/plugins/formValidation/js/language/th_TH.js"></script>
        <script type="text/javascript" src="public/plugins/fastclick/js/fastclick.js"></script>
        <script type="text/javascript" src="public/plugins/sweetalert/js/sweetalert2.min.js"></script>
        <script type="text/javascript" src="public/plugins/toastr/js/toastr.min.js"></script>
        <script type="text/javascript" src="public/plugins/datepicker/js/bootstrap-datepicker.min.js"></script>
        <script type="text/javascript" src="public/plugins/datepicker/locales/bootstrap-datepicker.th.min.js"></script>
        <script type="text/javascript" src="public/plugins/inputMask/js/jquery.inputmask.js"></script>
        <script type="text/javascript" src="public/plugins/inputMask/js/jquery.inputmask.extensions.js"></script>
        <script type="text/javascript" src="public/plugins/inputMask/js/jquery.inputmask.date.extensions.js"></script>
        <script type="text/javascript" src="public/plugins/select2/js/select2.full.min.js"></script>
        <script type="text/javascript" src="public/plugins/select2/js/locales/th.js"></script>
        <script type="text/javascript" src="public/assets/adminLTE/js/adminlte.min.js"></script>
        <!--<script type="text/javascript" src="public/assets/custom/js/custom.min.js"></script>-->
        <script type="text/javascript" src="public/pickandpack/js/login/script.js"></script>
        <script type="text/javascript">
            $(document).ready(function () {
                console.log('version: ' + $("#version").val());
            });
        </script>
    </body>
</html>