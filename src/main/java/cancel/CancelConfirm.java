/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cancel;

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
import org.json.JSONException;
import org.json.JSONObject;
import utils.ResponseUtil;
import utils._global;
import utils._routine;

/**
 *
 * @author BeamMary
 */
@WebServlet(name = "cancel-confirm-list-1", urlPatterns = {"/cancel-confirm-list-1"})
public class CancelConfirm extends HttpServlet {

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
                case "cancel_confirm_document":
                    objResult = _cancelConfirmDocument(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "re_confirm_document":
                    objResult = _reConfrim(__conn, ResponseUtil.str2Json(request.getParameter("data")));
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
        Integer __strLimit = !param.isNull("limit") && param.getInt("limit") > 0 ? param.getInt("limit") : 0;

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? " AND (ref_code='" + param.getString("ref_code") + "') " : "";
        __strQueryExtends += !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND (wh_code IN " + param.getString("wh_code") + ") " : "";

        if (!param.isNull("from_date") && !param.getString("from_date").trim().isEmpty() && !param.isNull("to_date") && !param.getString("to_date").trim().isEmpty()) {
            String __strTmpFromDate = param.getString("from_date");
            String __strTmpToDate = param.getString("to_date");
            if (__strTmpFromDate.equals(__strTmpToDate)) {
                __strQueryExtends += " AND (ref_date = '" + __strTmpFromDate + "') ";
            } else {
                __strQueryExtends += " AND (ref_date BETWEEN '" + __strTmpFromDate + "' AND '" + __strTmpToDate + "') ";
            }
        }

        __strQueryExtends = __strQueryExtends.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtends;

        String __strQUERY = "SELECT * FROM( "
                + " SELECT  last_status,creator_code,doc_no,status,sale_type,ref_date,ref_code,sale_type,send_type "
                + " ,to_char(pp_trans.confirm_date_time, 'YYYY-MM-DD HH24:MI:SS') AS confirm_date_time "
                + " ,to_char(pp_trans.doc_date, 'YYYY-MM-DD') AS doc_date "
                + " ,COALESCE(department_code, '') AS department_code"
                + " ,COALESCE((SELECT name_1 FROM erp_department_list WHERE erp_department_list.code = pp_trans.department_code),'') AS department_name "
                + " ,COALESCE(cust_code, '') AS cust_code"
                + " ,COALESCE((SELECT name_1 FROM ar_customer WHERE ar_customer.code = pp_trans.cust_code),'') AS cust_name "
                + " ,CASE WHEN (select count(*) FROM ic_trans WHERE ic_trans.doc_no=pp_trans.ref_code AND last_status=1)>0 THEN 0  ELSE 1 END AS from_detail "
                + " ,COALESCE(branch_code, '') AS branch_code,coalesce((SELECT name_1 FROM erp_branch_list WHERE erp_branch_list.code = pp_trans.branch_code),'') AS branch_name "
                + " ,COALESCE(confirm_code, '') AS confirm_code,coalesce((SELECT name_1 FROM erp_user WHERE erp_user.code = pp_trans.confirm_code),'') AS confirm_name "
                + " ,COALESCE((select wh_code FROM pp_trans_detail WHERE pp_trans_detail.doc_no = pp_trans.doc_no limit 1), '') AS wh_code "
                + " ,COALESCE((SELECT name_1 FROM ic_warehouse WHERE ic_warehouse.code = (select wh_code from pp_trans_detail WHERE pp_trans_detail.doc_no = pp_trans.doc_no limit 1)),'') AS wh_name "
                + " From pp_trans WHERE status=1 "
                + " )AS pp_trans " + __strQueryExtends + " ORDER BY confirm_date_time DESC,doc_no ";

        conn.setAutoCommit(false);
        __objTMP.put("row_count", __routine._getRowCount(conn, __strQUERY));
        String __strQueryPagination = " LIMIT " + __strLimit + " OFFSET " + __strOffset;
        PreparedStatement __stmt1;

        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY + __strQueryPagination, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        String __rsHTML = "";
        String[] __arrSendType = {"รับเอง", "ส่งให้"};
        String[] __arrSaleType = {"ขายเงินเชื่อ", "ขายเงินสด", "ขายสินค้าเงินสด (สินค้าบริการ)", "ขายสินค้าเงินเชื่อ (สินค้าบริการ)"};

        while (__rsData1.next()) {
            String __strConfirmDateTime = __rsData1.getString("confirm_date_time").equals("") ? "-" : __rsData1.getString("confirm_date_time");
            String __strDocumentDate = __rsData1.getString("doc_date").equals("") ? "-" : __rsData1.getString("doc_date");
            String __strDocNo = __rsData1.getString("doc_no").equals("") ? "-" : __rsData1.getString("doc_no");
            String __strRefCode = __rsData1.getString("ref_code").equals("") ? "-" : __rsData1.getString("ref_code");
            String __strCustomerCode = __rsData1.getString("cust_code").equals("") ? "-" : __rsData1.getString("cust_code");
            String __strCustomerName = __rsData1.getString("cust_name").equals("") ? "-" : __rsData1.getString("cust_name");
            String __strBranchCode = __rsData1.getString("branch_code").equals("") ? "-" : __rsData1.getString("branch_code");
            String __strBranchName = __rsData1.getString("branch_name").equals("") ? "-" : __rsData1.getString("branch_name");
            String __strConfirmCode = __rsData1.getString("confirm_code").equals("") ? "-" : __rsData1.getString("confirm_code");
            String __strConfirmName = __rsData1.getString("confirm_name").equals("") ? "-" : __rsData1.getString("confirm_name");

            Integer __strLastStatus = __rsData1.getInt("last_status");
            Integer __strFromDetail = __rsData1.getInt("from_detail");

            String __strBgColor;
            if (__strFromDetail == 0) {
                __strBgColor = "style='background-color: #F98180; color: #FFF' from_detail='" + "ic_trans" + "' ";
            } else if (__strFromDetail == 1 && __strLastStatus == 1) {
                __strBgColor = "style='background-color: #FFB54A; color: #FFF' from_detail='" + "pp_trans" + "' ";
            } else {
                __strBgColor = "from_detail='" + "pp_trans" + "' ";
            }

            __rsHTML += "<tr key_id='" + __strDocNo + "' ref_code='" + __strRefCode + "' " + __strBgColor + ">";
            __rsHTML += "<td><h5><strong>" + __routine._convertDateTime(__strConfirmDateTime) + "</strong></h5></td>";
            __rsHTML += "<td><h5>" + __routine._convertDate(__strDocumentDate) + "</h5></td>";
            __rsHTML += "<td><h5>" + __strDocNo + "</h5></td>";
            __rsHTML += "<td><h5>" + __strBranchCode + " ~ " + __strBranchName + "</h5></td>";
            __rsHTML += "<td><h5>" + __strCustomerCode + " ~ " + __strCustomerName + "</h5></td>";
            __rsHTML += "<td><h5>" + __strConfirmCode + " ~ " + __strConfirmName + "</h5></td>";
            __rsHTML += "<td><h5>" + "ขายสินค้า" + "</h5></td>";
            __rsHTML += "<td><h5>" + __arrSendType[__rsData1.getInt("send_type")] + "</h5></td>";
            __rsHTML += "<td><h5>" + __arrSaleType[__rsData1.getInt("sale_type")] + "</h5></td>";
            __rsHTML += "<td><button type='button' id='btn-more' class='btn btn-info btn-flat'>เพิ่มเติม</button></td>";

            if (__strFromDetail == 1 && __strLastStatus == 1) {
                __rsHTML += "<td><button type='button' id='btn-confirm-doc' class='btn btn-success btn-flat'>อนุมัติใบจัดอีกครั้ง</button></td>";
            } else {
                if (__strLastStatus == 1) {
                    __rsHTML += "<td><button type='button' id='btn-cancel-doc' class='btn btn-danger btn-flat' disabled='true'>ยกเลิกการอนุมัติ</button></td>";
                } else {
                    __rsHTML += "<td><button type='button' id='btn-cancel-doc' class='btn btn-danger btn-flat'>ยกเลิกการอนุมัติ</button></td>";
                }
            }
            __rsHTML += "</tr>";

            __rsHTML += "<tr id='" + __strDocNo + "' style='display: none;'></tr>";

        }

        if (__rsHTML.equals("")) {
            __rsHTML = "<tr><td colspan='14'><h5>ไม่พบข้อมูล</h5></td></tr>";
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
//        String __strDocNo = !param.isNull("doc_no") && param.getString("doc_no").trim().isEmpty() ? param.getString("doc_no") : "";

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? " AND (doc_no='" + param.getString("doc_no") + "') " : "";
        __strQueryExtends += !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND (wh_code IN " + param.getString("wh_code") + ") " : "";
        __strQueryExtends += !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND (shelf_code IN " + param.getString("shelf_code") + ") " : "";

        __strQueryExtends = __strQueryExtends.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtends;

        String __strQUERY = "SELECT doc_no,ic_code,wh_code,shelf_code,qty,event_qty,status,tms_que_shipment_code,line_number, "
                + " COALESCE((SELECT name FROM tms_reason WHERE tms_reason.code=pp_trans_detail.remark and tms_reason.reason_flag=4),'')AS remark, "
                + " COALESCE((SELECT name_1 FROM ic_inventory WHERE ic_inventory.code=ic_code),'')AS item_name, "
                + " COALESCE((SELECT name_1 FROM ic_warehouse WHERE ic_warehouse.code=wh_code),'')AS wh_name, "
                + " COALESCE((SELECT name_1 FROM ic_shelf WHERE ic_shelf.code=shelf_code AND ic_shelf.whcode=wh_code),'')AS shelf_name "
                + " FROM pp_trans_detail " + __strQueryExtends + " ORDER BY line_number";

        conn.setAutoCommit(false);
        String __rsHTML = "";
        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        __rsHTML += "<td colspan='13' style='padding: 5px 0'>";
        __rsHTML += "<div>";
        __rsHTML += "<table class='table table-hover' style='margin: 0;'>";
        __rsHTML += "<tr class='text-center' style='background-color: #F5B041; color: #F8F9F9'>";
        __rsHTML += "<td><strong>ลำดับ</strong></td>";
        __rsHTML += "<td><strong>รหัส</strong></td>";
        __rsHTML += "<td><strong>รหัสสินค้า ~ ชื่อสินค้า</strong></td>";
        __rsHTML += "<td><strong>รหัสคลัง ~ ชื่อคลัง</strong></td>";
        __rsHTML += "<td><strong>รหัสที่เก็บ ~ ชื่อที่เก็บ</strong></td>";
        __rsHTML += "<td><strong>จำนวน</strong></td>";
        __rsHTML += "<td><strong>จำนวนที่จัดได้</strong></td>";
        __rsHTML += "<td><strong>สถานะ</strong></td>";
        __rsHTML += "<td><strong>หมายเหตุ</strong></td>";
        __rsHTML += "</tr>";

        Integer __rowNumber = 0;
        String __strDetail = "";
        Boolean isPlus = false;
        while (__rsData1.next()) {
            if (__rowNumber == 0 && __rsData1.getInt("line_number") == 0) {
                isPlus = true;
            }
            __strDetail = "";
            String __bgColor = __rowNumber % 2 == 0 ? "#FEF9E7" : "#F9E79F";

            Integer __status = __rsData1.getInt("status");
            String[] __arrStatus = {"ปกติ", "ปิด", "ปิดไม่ปกติ"};
            String[] __arrBgStatus = {"#449D44", "#944DFF", "#F98180"};

            __strDetail += "<tr style='background-color: " + __bgColor + "' color: #000;>";
            __strDetail += "<td><h5><strong>" + (isPlus ? (__rsData1.getInt("line_number") + 1) : __rsData1.getInt("line_number")) + "</strong></h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("doc_no") + "</h5></td>";
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
            __rsHTML += "<tr><td colspan='13'>ไม่พบข้อมูล</td></tr>";
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

    private JSONObject _cancelConfirmDocument(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? " AND (doc_no='" + param.getString("doc_no") + "') " : "";
        __strQueryExtends += !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? " AND (ref_code='" + param.getString("ref_code") + "') " : "";

        __strQueryExtends = __strQueryExtends.equals("") ? "" : " WHERE 1=1 " + __strQueryExtends;

        String __strQUERY = "UPDATE pp_trans SET last_status=1 " + __strQueryExtends;

        conn.setAutoCommit(false);
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.executeUpdate();
        __stmt1.close();
        conn.commit();
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _reConfrim(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? " AND (doc_no='" + param.getString("doc_no") + "') " : "";
        __strQueryExtends += !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? " AND (ref_code='" + param.getString("ref_code") + "') " : "";

        __strQueryExtends = __strQueryExtends.equals("") ? "" : " WHERE 1=1 " + __strQueryExtends;

        String __strQUERY = "UPDATE pp_trans SET status=3,last_status=0 " + __strQueryExtends;

        conn.setAutoCommit(false);
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.executeUpdate();
        __stmt1.close();
        conn.commit();
        __objTMP.put("success", true);

        return __objTMP;
    }

}
