/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import java.awt.List;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ResponseUtil;
import utils._global;
import utils._routine;

/**
 *
 * @author BeamMary
 */
@WebServlet(name = "report-list-2", urlPatterns = {"/report-list-2"})
public class Report2 extends HttpServlet {

    private final _routine __routine = new _routine();
    private String __strDatabaseName;
    private String __strProviderCode;
    private String __strUserCode;
    private String __strSystemID;
    private String __strSessionID;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject objResult = new JSONObject("{'success': false}");
        HttpSession __session = request.getSession();

        if (__session.getAttribute("user_code") == null && __session.getAttribute("user_code").toString().trim().isEmpty()) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", "กรุณาทำการเข้าสู่ระบบ");
            response.getWriter().print(objResult);
            return;
        }

        this.__strDatabaseName = __session.getAttribute("database_name").toString();
        this.__strProviderCode = __session.getAttribute("provider_code").toString();
        this.__strUserCode = __session.getAttribute("user_code").toString();
        this.__strSystemID = __session.getAttribute("system_id").toString();
        this.__strSessionID = __session.getAttribute("session_id").toString();

        String __strActionName = "";
        if (request.getParameter("action_name") != null && !request.getParameter("action_name").isEmpty()) {
            __strActionName = request.getParameter("action_name");
        }
        Connection __conn = null;
        try {
            __conn = __routine._connect(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));
            switch (__strActionName) {
                case "get_main_detail":
                    objResult = _getMainDetail(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_sub_detail":
                    objResult = _getSubDetail(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_branch_list":
                    objResult = _getBranchList(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_group_location":
                    objResult = _getGroupLocation(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_zone":
                    objResult = _getZone(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
            }
        } catch (SQLException | JSONException ex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", "Exception Message: " + ex.getClass().getCanonicalName() + " :: " + ex.getMessage());
            Logger.getLogger(List.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            objResult.put("err_title", "ข้อผิดพลาดทางระบบ");
            objResult.put("err_msg", "Exception Message: " + ex.getClass().getCanonicalName() + " :: " + ex.getMessage());
            Logger.getLogger(List.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (__conn != null) {
                    __conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        response.getWriter().print(objResult);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private JSONObject _getMainDetail(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        Integer __strOffset = !param.isNull("offset") && param.getInt("offset") > 0 ? param.getInt("offset") : 0;
        Integer __strLimit = !param.isNull("limit") && param.getInt("limit") > 0 ? param.getInt("limit") : 20;

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("branch_code") && !param.getString("branch_code").trim().isEmpty() ? " AND (UPPER(branch_code)= '" + param.getString("branch_code").toUpperCase() + "') " : "";
        __strQueryExtends += !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND ((wh_code IN " + param.getString("wh_code") + ") " : "";
        __strQueryExtends += !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND (shelf_code IN " + param.getString("shelf_code") + ")) " : "";

        if (!param.isNull("from_date") && !param.getString("from_date").trim().isEmpty() && !param.isNull("to_date") && !param.getString("to_date").trim().isEmpty()) {
            String __strTmpFromDate = param.getString("from_date");
            String __strTmpToDate = param.getString("to_date");
            if (__strTmpFromDate.equals(__strTmpToDate)) {
                __strQueryExtends += " AND (doc_date = '" + __strTmpFromDate + "') ";
            } else {
                __strQueryExtends += " AND (doc_date BETWEEN '" + __strTmpFromDate + "' AND '" + __strTmpToDate + "') ";
            }
        }

        __strQueryExtends = __strQueryExtends.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtends;

        String __strQueryPagination = " LIMIT " + __strLimit + " OFFSET " + __strOffset;

        String __strQUERY = "SELECT * \n"
                + " ,(CASE WHEN to_timestamp(confirm_date_time, 'YYYY-MM-DD HH24:MI')::timestamp without time zone <> to_timestamp(create_date_time_now, 'YYYY-MM-DD HH24:MI')::timestamp without time zone THEN true ELSE false END) AS is_confirm \n"
                + " ,(CASE WHEN to_timestamp(confirm_date_time, 'YYYY-MM-DD HH24:MI')::timestamp without time zone > to_timestamp(start_date, 'YYYY-MM-DD HH24:MI')::timestamp without time zone THEN to_char((to_timestamp(confirm_date_time, 'YYYY-MM-DD HH24:MI') - to_timestamp(start_date, 'YYYY-MM-DD HH24:MI')), 'DD-HH24-MI') ELSE to_char((to_timestamp(start_date, 'YYYY-MM-DD HH24:MI') - to_timestamp(confirm_date_time, 'YYYY-MM-DD HH24:MI')), 'DD-HH24-MI') END) AS packing_time \n"
                + " ,(CASE WHEN to_timestamp(close_time, 'YYYY-MM-DD HH24:MI')::timestamp without time zone > to_timestamp(start_date, 'YYYY-MM-DD HH24:MI')::timestamp without time zone THEN to_char((to_timestamp(close_time, 'YYYY-MM-DD HH24:MI') - to_timestamp(start_date, 'YYYY-MM-DD HH24:MI')), 'DD-HH24-MI') ELSE to_char((to_timestamp(start_date, 'YYYY-MM-DD HH24:MI') - to_timestamp(close_time, 'YYYY-MM-DD HH24:MI')), 'DD-HH24-MI') END) AS sended_time \n"
                + " FROM (\n"
                + " SELECT wh_code,shelf_code,doc_no \n"
                + " ,COALESCE((SELECT name_1 FROM ic_warehouse WHERE code=wh_code), '') AS wh_name \n"
                + " ,COALESCE((SELECT name_1 FROM ic_shelf WHERE whcode=wh_code and code=shelf_code), '') AS sh_name \n"
                + " ,to_char(doc_date, 'YYYY-MM-DD') AS doc_date \n"
                + " ,COALESCE((SELECT to_char(create_datetime, 'YYYY-MM-DD HH24:MI:SS') FROM pp_trans_log WHERE pp_trans_log.ref_code=pp_trans.ref_code AND (action=1 OR action=2 OR action=5 OR action=6 OR action=7 OR action=8) ORDER BY create_datetime ASC LIMIT 1), '') AS start_date \n"
                + " ,to_char(confirm_date_time, 'YYYY-MM-DD HH24:MI:SS')AS confirm_date_time \n"
                + " ,to_char(close_time, 'YYYY-MM-DD HH24:MI:SS') AS close_time \n"
                + " ,to_char(create_date_time_now, 'YYYY-MM-DD HH24:MI:SS') AS create_date_time_now \n"
                + " FROM pp_trans \n" + __strQueryExtends + "\n"
                + " AND is_confirm=1 \n" + __strQueryPagination + "\n"
                //                + " ORDER BY create_date_time_now ASC,doc_no \n"
                + ") AS pp_trans \n"
                + "ORDER BY create_date_time_now,start_date ASC,doc_no  \n";

        String __strQUERY2 = "SELECT COUNT (*) AS count "
                + " FROM pp_trans \n" + __strQueryExtends + "\n"
                + " AND is_confirm=1 \n";
        
        System.out.println(__strQUERY);

        conn.setAutoCommit(false);
        
        __objTMP.put("row_count", __routine._getRowCount2(conn, __strQUERY2));

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        String __rsHTML = "";
        while (__rsData1.next()) {
            Boolean __isConfirm = __rsData1.getBoolean("is_confirm");
            String __strWhCode = __rsData1.getString("wh_code").equals("") ? "-" : __rsData1.getString("wh_code");
            String __strWhName = __rsData1.getString("wh_name").equals("") ? "-" : __rsData1.getString("wh_name");
            String __strShelfCode = __rsData1.getString("shelf_code").equals("") ? "-" : __rsData1.getString("shelf_code");
            String __strShelfName = __rsData1.getString("sh_name").equals("") ? "-" : __rsData1.getString("sh_name");
            String __strDocDate = __rsData1.getString("doc_date").equals("") ? "-" : __rsData1.getString("doc_date");
            String __strDocNo = __rsData1.getString("doc_no").equals("") ? "-" : __rsData1.getString("doc_no");

            String __strStartDate = __rsData1.getString("start_date") == null ? "-" : __rsData1.getString("start_date").trim().isEmpty() ? "-" : __rsData1.getString("start_date");
            String __strConfirmDateTime = __rsData1.getString("confirm_date_time") == null ? "-" : __rsData1.getString("confirm_date_time").trim().isEmpty() ? "-" : __rsData1.getString("confirm_date_time");
            String __strPackingTime = __rsData1.getString("packing_time") == null ? "-" : __rsData1.getString("packing_time").trim().isEmpty() ? "-" : __rsData1.getString("packing_time");
            String __strCloseTime = __rsData1.getString("close_time") == null ? "-" : __rsData1.getString("close_time").trim().isEmpty() ? "-" : __rsData1.getString("close_time");
            String __strSendTime = __rsData1.getString("sended_time") == null ? "-" : __rsData1.getString("sended_time").trim().isEmpty() ? "-" : __rsData1.getString("sended_time");

            __rsHTML += "<tr key_id='" + __strDocNo + "' >";
            __rsHTML += "<td><h5>" + __strWhCode + " ~ " + __strWhName + "</h5></td>";
            __rsHTML += "<td><h5>" + __strShelfCode + " ~ " + __strShelfName + "</h5></td>";
            __rsHTML += "<td><h5>" + (__strDocDate.equals("-") ? "-" : __routine._convertDate(__strDocDate)) + "</h5></td>";
            __rsHTML += "<td><h5>" + __strDocNo + " <button type='button' id='btn-more' class='btn btn-info btn-flat btn-xs'>เพิ่มเติม</button></h5></td>";
            __rsHTML += "<td><h5>" + (__strStartDate.equals("-") ? "-" : __routine._convertDateTime(__strStartDate)) + "</h5></td>";
//            __rsHTML += "<td><h5>" + (__strConfirmDateTime.equals("-") ? "-" : __routine._convertDateTime(__strConfirmDateTime)) + "</h5></td>";
            __rsHTML += "<td><h5>" + (__isConfirm ? (__strConfirmDateTime.equals("-") ? "-" : __routine._convertDateTime(__strConfirmDateTime)) : "-") + "</h5></td>";
            __rsHTML += "<td><h5>" + (__isConfirm ? !__strStartDate.equals("-") && !__strConfirmDateTime.equals("-") ? !__strPackingTime.equals("-") ? __routine._convertHours(__strPackingTime) : "-" : "-" : "-") + "</h5></td>";
//            __rsHTML += "<td><h5>" + (__isConfirm ? (__strPackingTime.equals("-") ? "-" : __routine._convertTime(__strPackingTime)) : "-") + "</h5></td>";
            __rsHTML += "<td><h5>" + (__isConfirm ? !__strStartDate.equals("-") && !__strConfirmDateTime.equals("-") ? !__strCloseTime.equals("-") ? __routine._convertDateTime(__strCloseTime) : "-" : "-" : "-") + "</h5></td>";
//            __rsHTML += "<td><h5>" + (__strCloseTime.equals("-") ? "-" : __routine._convertDateTime(__strCloseTime)) + "</h5></td>";
            __rsHTML += "<td><h5>" + (__strSendTime.equals("-") ? "-" : __strStartDate.equals("-") ? "-" : __routine._convertTime(__strSendTime)) + "</h5></td>";

            __rsHTML += "</tr>";
            __rsHTML += "<tr id='" + __strDocNo + "' style='display: none;'></tr>";
        }

        if (__rsHTML.equals("")) {
            __rsHTML = "<tr><td colspan='10'><h5>ไม่พบข้อมูล</h5></td></tr>";
        }

        __stmt1.close();
        __rsData1.close();
        conn.commit();
        __objTMP.put("success", true);
        __objTMP.put("data", __rsHTML);

        return __objTMP;
    }

    private JSONObject _getSubDetail(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? " AND doc_no='" + param.getString("doc_no") + "' " : "";
        __strQueryExtends += !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND (wh_code IN " + param.getString("wh_code") : "";
        __strQueryExtends += !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND shelf_code IN " + param.getString("shelf_code") + ") " : "";

        __strQueryExtends = __strQueryExtends.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtends;

        String __strQUERY = "SELECT doc_no,ic_code,wh_code,shelf_code,qty,event_qty,status,tms_que_shipment_code,line_number, "
                + " COALESCE((SELECT name FROM tms_reason WHERE tms_reason.code=pp_trans_detail.remark and tms_reason.reason_flag=4),'')AS remark, "
                + " COALESCE((SELECT name_1 FROM ic_inventory WHERE ic_inventory.code=ic_code),'')AS item_name, "
                + " COALESCE((SELECT name_1 FROM ic_warehouse WHERE ic_warehouse.code=pp_trans_detail.wh_code),'')AS wh_name, "
                + " COALESCE((SELECT name_1 FROM ic_shelf WHERE ic_shelf.code=pp_trans_detail.shelf_code AND ic_shelf.whcode=pp_trans_detail.wh_code),'')AS shelf_name "
                + " FROM pp_trans_detail " + __strQueryExtends + " ORDER BY line_number";

        conn.setAutoCommit(false);
        String __rsHTML = "";
        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        __rsHTML += "<td colspan='9' style='padding: 5px 0'>";
        __rsHTML += "<div>";
        __rsHTML += "<table class='table table-hover' style='margin: 0;'>";
        __rsHTML += "<tr class='text-center' style='background-color: #F5B041; color: #F8F9F9'>";
        __rsHTML += "<td><strong>ลำดับ</strong></td>";
        __rsHTML += "<td><strong>รหัสสินค้า ~ ชื่อสินค้า</strong></td>";
        __rsHTML += "<td><strong>รหัสคลัง ~ ชื่อคลัง</strong></td>";
        __rsHTML += "<td><strong>รหัสที่เก็บ ~ ชื่อที่เก็บ</strong></td>";
        __rsHTML += "<td><strong>จำนวน</strong></td>";
        __rsHTML += "<td><strong>จำนวนที่จัดได้</strong></td>";
        __rsHTML += "<td><strong>สถานะ</strong></td>";
        __rsHTML += "<td><strong>หมายเหตุ</strong></td>";
        __rsHTML += "</tr>";

        Integer __rowNumber = 0;
        String __bgColor;
        String __strDetail = "";
        Boolean isPlus = false;
        while (__rsData1.next()) {
            __strDetail = "";
            if (__rowNumber % 2 == 0) {
                __bgColor = "#FEF9E7";
            } else {
                __bgColor = "#F9E79F";
            }

            Integer __status = __rsData1.getInt("status");
            String[] __arrStatus = {"ปกติ", "ปิด", "ปิดไม่ปกติ"};
            String[] __arrBgStatus = {"#449D44", "#944DFF", "#F98180"};

            __strDetail += "<tr style='background-color: " + __bgColor + "' color: #000;>";
            if (__rowNumber == 0 && __rsData1.getInt("line_number") == 0) {
                isPlus = true;
            }
            if (isPlus) {
                __strDetail += "<td><h5><strong>" + (__rsData1.getInt("line_number") + 1) + "</strong></h5></td>";
            } else {
                __strDetail += "<td><h5><strong>" + __rsData1.getInt("line_number") + "</strong></h5></td>";
            }
            __strDetail += "<td><h5>" + __rsData1.getString("ic_code") + " ~ " + __rsData1.getString("item_name") + "</h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("wh_code") + " ~ " + __rsData1.getString("wh_name") + "</h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("shelf_code") + " ~ " + __rsData1.getString("shelf_name") + "</h5></td>";
            __strDetail += "<td><h5>" + String.format("%,.2f", Float.parseFloat(__rsData1.getString("qty"))) + "</h5></td>";
            __strDetail += "<td><h5>" + String.format("%,.2f", Float.parseFloat(__rsData1.getString("event_qty"))) + "</h5></td>";
            __strDetail += "<td style='color: #FFF; background-color: " + __arrBgStatus[__status] + "'><h5>" + __arrStatus[__status] + "</h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("remark") + "</h5></td>";

            __rsHTML += __strDetail;
            __rsHTML += "</tr>";

            __rowNumber++;
        }
        if (__strDetail.equals("")) {
            __rsHTML += "<tr><td colspan='9'>ไม่พบข้อมูล</td></tr>";
        }

        __rsHTML += "</table>";
        __rsHTML += "</div>";
        __rsHTML += "</td>";

        __stmt1.close();
        __rsData1.close();

        __objTMP.put("success", true);
        __objTMP.put("data", __rsHTML);

        return __objTMP;
    }

    private JSONObject _getBranchList(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "SELECT code, name_1 FROM erp_branch_list order by code";

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        JSONArray __arrList = ResponseUtil.query2Array(__rsData1);
        __stmt1.close();
        __rsData1.close();

        __objTMP.put("success", true);
        __objTMP.put("data", __arrList);
        return __objTMP;
    }

    private JSONObject _getGroupLocation(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "SELECT group_code, whcode, location, name FROM sml_group_location ORDER BY group_code";

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        JSONArray __arrList = ResponseUtil.query2Array(__rsData1);
        __stmt1.close();
        __rsData1.close();

        __objTMP.put("success", true);
        __objTMP.put("data", __arrList);
        return __objTMP;
    }

    private JSONObject _getZone(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code") : "";

        String __strQUERY = "SELECT group_code,whcode,location,name FROM sml_group_location WHERE group_code='" + __strGroupCode + "'";

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        JSONArray __arrList = ResponseUtil.query2Array(__rsData1);
        __stmt1.close();
        __rsData1.close();

        __objTMP.put("success", true);
        __objTMP.put("data", __arrList);
        return __objTMP;
    }

}
