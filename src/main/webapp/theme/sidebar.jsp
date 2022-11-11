<%@page import="java.util.Properties"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Properties projectPpt = new Properties();
    projectPpt.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("project.properties"));
%>
<div class="col-md-3 left_col">
    <div class="left_col scroll-view">
        <div class="navbar nav_title" style="border: 0;">
            <a href="${sublink}index.jsp" class="site_title">SML<span> Pick And Pack</span></a>
        </div>

        <div class="clearfix"></div>

        <!-- menu profile quick info -->
        <div class="profile clearfix">
            <div class="profile_pic">
                <img src="${sublink}public/media/img/icon-user.png" class="img-circle profile_img" alt="">
            </div>
            <div class="profile_info">
                <span>ยินดีต้อนรับ,</span>
                <h2>${user_name}</h2>
            </div>
            <div class="clearfix"></div>
        </div>
        <!-- /menu profile quick info -->

        <br />

        <!-- sidebar menu -->
        <div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
            <div class="menu_section">
                <h3 style="text-transform: none;">Version :: <%=projectPpt.getProperty("version")%></h3>
                <p/>
                <h3>ฐานข้อมูล :: ${database_name}</h3>
                <p/>
                <h3>สาขา :: ${branch_code} ~ ${branch_name}</h3>
                <ul class="nav side-menu">
                    <li><a href="${sublink}index.jsp"><i class="fa fa-home"></i> หน้าแรก</a></li>
                    <li><a href="${sublink}page/cancel/confrim/"><i class="fa fa-ban"></i> ยกเลิกเอกสารใบจัด</a></li>
                    <li><a href="${sublink}page/warehouse/status/"><i class="fa fa-search"></i> ตรวจสอบสถานะใบจัดสินค้า</a></li>
                    <li><a href="${sublink}page/warehouse/worker/"><i class="fa fa-archive"></i> พนักงานคลัง ขาย</a></li>
                    <li><a href="${sublink}page/warehouse/administration/"><i class="fa fa-archive"></i> ธุรการคลัง ขาย</a></li>
                    <li><a><i class="fa fa-file"></i> รายงาน <span class="fa fa-chevron-down"></span></a>
                        <ul class="nav child_menu">
                            <li><a href="${sublink}page/reports/report1/">วิเคราะห์การจ่ายสินค้าขาด</a></li>
                            <li><a href="${sublink}page/reports/report2/">ระยะเวลาในการจัดสินค้าและส่งมอบสินค้า</a></li>
                            <li><a href="${sublink}page/reports/report3/">สินค้าสั่งพิเศษค้างจ่าย</a></li>
                        </ul>
                    </li>
                    <li><a href="${sublink}page/history/index.jsp"><i class="fa fa-clock-o"></i> ประวัติการทำรายการ</a></li>
                </ul>
            </div>
            <div class="menu_section">
                <h3>ตั่งค่าทั่วไป</h3>
                <ul class="nav side-menu">
                    <li><a><i class="fa fa-cogs"></i> ตั่งค่า <span class="fa fa-chevron-down"></span></a>
                        <ul class="nav child_menu">
                            <li><a href="${sublink}page/configs/groups/locations/">กลุ่ม คลังสินค้าและสถานที่เก็บสินค้า</a></li>
                            <li><a href="${sublink}page/configs/groups/systems/">กำหนดหมายเลขเครื่อง</a></li>
                            <li><a href="${sublink}page/configs/reason/">กำหนดเหตุผล</a></li>
                            <li><a href="${sublink}page/configs/approved/date/">กำหนดวันที่เอกสารเริ่มต้นที่จะดึง</a></li>
                        </ul>
                    </li>
                    <li><a><i class="fa fa-user"></i> จัดการสิทธิ์ <span class="fa fa-chevron-down"></span></a>
                        <ul class="nav child_menu">
                            <li><a href="${sublink}page/permissions/users/">แบบเดี่ยว</a></li>
                            <li><a href="${sublink}page/permissions/groups/">แบบกลุ่ม</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
        <!-- /sidebar menu -->

        <!-- /menu footer buttons -->
        <div class="sidebar-footer hidden-small">
            <a data-toggle="tooltip" data-placement="top" title="none"><span class="fa fa-close" aria-hidden="true"></span></a>
            <a data-toggle="tooltip" data-placement="top" title="none"><span class="fa fa-close" aria-hidden="true"></span></a>
            <a data-toggle="tooltip" data-placement="top" title="none"><span class="fa fa-close" aria-hidden="true"></span></a>
            <a data-toggle="tooltip" data-placement="top" title="none"><span class="fa fa-close" aria-hidden="true"></span></a>
        </div>
        <!-- /menu footer buttons -->
    </div>
</div>