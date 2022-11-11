/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package warehouses;

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
@WebServlet(name = "warehouse-worker-list-1", urlPatterns = {"/warehouse-worker-list-1"})
public class Worker extends HttpServlet {

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
                case "update_event_qty":
                    objResult = _updateEvetnQTY(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "close_document":
                    objResult = _closeDocument(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "confirm_document":
                    objResult = _confirmDocument(__conn, ResponseUtil.str2Json(request.getParameter("data")));
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
        __strQueryExtends += !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND (shelf_code IN " + param.getString("shelf_code") + ") " : "";

        if (!param.isNull("from_date") && !param.getString("from_date").trim().isEmpty() && !param.isNull("to_date") && !param.getString("to_date").trim().isEmpty()) {
            String __strTmpFromDate = param.getString("from_date");
            String __strTmpToDate = param.getString("to_date");
            if (__strTmpFromDate.equals(__strTmpToDate)) {
                __strQueryExtends += " AND (ref_date = '" + __strTmpFromDate + "') ";
            } else {
                __strQueryExtends += " AND (ref_date BETWEEN '" + __strTmpFromDate + "' AND '" + __strTmpToDate + "') ";
            }
        }

        if (!param.isNull("send_type") && !param.getString("send_type").trim().isEmpty()) {
            __strQueryExtends += " AND (send_type = '" + param.getString("send_type") + "')";
        }

        __strQueryExtends = __strQueryExtends.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtends;

        String __strQueryExtends2 = "";
        if (!param.isNull("customer_name") && !param.getString("customer_name").trim().isEmpty()) {
            __strQueryExtends2 += " AND cust_name LIKE '%" + param.getString("customer_name") + "%' ";
        }

        if (!param.isNull("status_type") && !param.getString("status_type").trim().isEmpty()) {
            String strStatusType = param.getString("status_type");

            switch (Integer.parseInt(strStatusType)) {
                case 0:
                    __strQueryExtends2 += " AND last_status=1";
                    break;
                case 1:
                    __strQueryExtends2 += " AND is_print=0 AND is_confirm!=1 AND last_status!=1";
                    break;
                case 2:
                    __strQueryExtends2 += " AND is_print=1 AND is_confirm!=1 AND last_status!=1";
                    break;
                case 3:
                    __strQueryExtends2 += " AND is_confirm=1 AND last_status!=1";
                    break;

            }
        }

        __strQueryExtends2 = __strQueryExtends2.equals("") ? "" : " WHERE 1=1 " + __strQueryExtends2;

        String __strQUERY = "SELECT * FROM ("
                + " SELECT ref_code,cust_code,is_print,is_confirm,last_status,send_type,doc_no,trans_flag "
                + " ,COALESCE (create_date_time, '') AS create_date_time "
                + " ,COALESCE ((SELECT SUM(sum_amount) FROM pp_trans_detail WHERE pp_trans_detail.doc_no=pp_trans.doc_no), 0) AS total_amount "
                + " ,COALESCE (car_code, '') AS car_code "
                + " ,COALESCE((SELECT name_1 FROM ar_customer WHERE ar_customer.code = pp_trans.cust_code),'') AS cust_name "
                + " ,COALESCE((SELECT DISTINCT to_char(expect_date, 'YYYY-MM-DD') FROM tms_que_shipment AS tms WHERE tms.doc_no=pp_trans.tms_que_shipment_code),'') AS expect_date "
                + " ,COALESCE((SELECT DISTINCT CAST(round_no AS text) FROM tms_que_shipment AS tms WHERE tms.doc_no=pp_trans.tms_que_shipment_code),'') AS round_no "
                + " ,COALESCE((SELECT DISTINCT to_char(receive_date, 'YYYY-MM-DD HH24:MI:SS') FROM tms_que_shipment AS tms WHERE tms.doc_no=pp_trans.tms_que_shipment_code),'') AS receive_date "
                + " FROM pp_trans " + __strQueryExtends + " AND is_close=0 AND trans_flag IN (36,44,34) "
                + " ) AS temp1 " + __strQueryExtends2 + " ORDER BY create_date_time DESC,doc_no ";

        conn.setAutoCommit(false);

        __objTMP.put("row_count", __routine._getRowCount(conn, __strQUERY));
        String __strQueryPagination = " LIMIT " + __strLimit + " OFFSET " + __strOffset;
        PreparedStatement __stmt1;

        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY + __strQueryPagination, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        String __rsHTML = "";
        String[] arrSendType = {"รับเอง", "ส่งให้"};

        while (__rsData1.next()) {
            Integer __lastStatus = __rsData1.getInt("last_status");
            Integer __isPrint = __rsData1.getInt("is_print");
            Integer __isConfirm = __rsData1.getInt("is_confirm");
            String __strRefCode = __rsData1.getString("ref_code");
            String __strDocNO = __rsData1.getString("doc_no");
            String __strTransFlag = __rsData1.getString("trans_flag");
            String __strCreateDateTime = __rsData1.getString("create_date_time").equals("") ? "-" : __rsData1.getString("create_date_time");
            String __strExpectDate = __rsData1.getString("expect_date").equals("") ? "-" : __rsData1.getString("expect_date");
            String __strRoundNO = __rsData1.getString("round_no").equals("") ? "-" : __rsData1.getString("round_no");
            String __strRecieveDate = __rsData1.getString("receive_date").equals("") ? "-" : __rsData1.getString("receive_date");
            String __strBGColor = __lastStatus == 1 ? "background-color: #FF6666; color:#FFF" : "";

            __rsHTML += "<tr style='" + __strBGColor + "'>";
            __rsHTML += "<td><h5>" + __routine._convertDateTime(__strCreateDateTime) + "</h5></td>";
            __rsHTML += "<td><h5>" + __strDocNO + "</h5></td>";
            __rsHTML += "<td><h5>" + __rsData1.getString("cust_code") + " ~ " + __rsData1.getString("cust_name") + "</h5></td>";
//            __rsHTML += "<td><h5>" + String.format("%,.2f", Float.parseFloat(__rsData1.getString("total_amount"))) + "</h5></td>";
            __rsHTML += "<td><h5>" + arrSendType[__rsData1.getInt("send_type")] + "</h5></td>";
            __rsHTML += "<td><h5>" + __rsData1.getString("car_code") + "</h5></td>";
            __rsHTML += "<td><h5>" + __routine._convertDate(__strExpectDate) + "</h5></td>";
            __rsHTML += "<td><h5>" + __strRoundNO + "</h5></td>";
            __rsHTML += "<td><h5>" + __routine._convertDateTime(__strRecieveDate) + "</h5></td>";

            if (__lastStatus == 0) {
                if (__isPrint == 1 && __isConfirm != 1) {
                    __rsHTML += "<td style='background-color: #FF8533;color: #FFF;'><h5>" + "กำลังจัด" + "</h5></td>";
                } else if (__isConfirm == 1) {
                    __rsHTML += "<td style='background-color: #00FF00;color: #FFF;'><h5>" + "จัดเสร็จ" + "</h5></td>";
                } else if (__isPrint == 0) {
                    __rsHTML += "<td style='background-color: #944DFF;color: #FFF;'><h5>" + "รอจัด" + "</h5></td>";
                }
            } else {
                __rsHTML += "<td style='background-color: red; color:#FFF'><h5>" + "ยกเลิก" + "</h5></td>";
            }

            __rsHTML += "<td><button type='button' id='btn-more' class='btn btn-info btn-flat' key_id='" + __rsData1.getString("doc_no") + "'>เพิ่มเติม</button></td>";
            if (__isPrint == 0) {
                __rsHTML += "<td><button type='button' id='btn-print' class='btn btn-success btn-flat is_print' key_id='" + __strDocNO + "' ref_code='" + __strRefCode + "' trans_flag='" + __strTransFlag + "' is_print='" + __isPrint + "'>พิมพ์</button></td>";
            } else {
                __rsHTML += "<td><button type='button' id='btn-print' class='btn btn-success btn-flat is_print' key_id='" + __strDocNO + "' ref_code='" + __strRefCode + "' trans_flag='" + __strTransFlag + "' is_print='" + __isPrint + "' disabled>พิมพ์</button></td>";
            }
            __rsHTML += "<td><button type='button' id='btn-close-doc' class='btn btn-danger btn-flat' key_id='" + __strDocNO + "' ref_code='" + __strRefCode + "' trans_flag='" + __strTransFlag + "'>ปิดใบจัด</button></td>";
            __rsHTML += "<td><button type='button' id='btn-delete' class='btn btn-danger btn-flat' key_id='" + __strRefCode + "'>ลบ</button></td>";
            __rsHTML += "</tr>";
            __rsHTML += "<tr id='" + __rsData1.getString("doc_no") + "' style='display: none;'></tr>";
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
        String __strDocNo = !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? param.getString("doc_no") : "";

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? " AND (doc_no='" + param.getString("doc_no") + "') " : "";
        __strQueryExtends += !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND (wh_code IN " + param.getString("wh_code") + " " : "";
        __strQueryExtends += !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND shelf_code IN " + param.getString("shelf_code") + ") " : "";

        __strQueryExtends = __strQueryExtends.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtends;

        String __strQUERY = "SELECT status,ic_code,ref_code,unit_code,shelf_code, qty ,event_qty ,wh_code,line_number "
                + " ,COALESCE((SELECT name_1 FROM ic_inventory WHERE ic_inventory.code = pp_trans_detail.ic_code), '') AS item_name "
                + " ,COALESCE((SELECT name_1 FROM ic_warehouse WHERE ic_warehouse.code = pp_trans_detail.wh_code),'') AS wh_name "
                + " ,COALESCE((SELECT name FROM tms_reason WHERE tms_reason.code = remark and reason_flag= 4 ),'') AS remark "
                + " ,COALESCE((SELECT name_1 FROM ic_shelf WHERE ic_shelf.code = pp_trans_detail.shelf_code AND ic_shelf.whcode= pp_trans_detail.wh_code),'')AS shelf_name "
                + " ,COALESCE((SELECT name_1 FROM ic_unit WHERE ic_unit.code = pp_trans_detail.unit_code), '')AS unit_name "
                + " FROM pp_trans_detail " + __strQueryExtends + " ORDER BY line_number";

        conn.setAutoCommit(false);
        String __rsHTML = "";
        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        __rsHTML += "<td colspan='14' style='padding: 5px 0'>";
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
        __rsHTML += "<td><strong>หน่วยนับ</strong></td>";
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
            __strDetail += "<td><h5>" + __rsData1.getString("ic_code") + " ~ " + __rsData1.getString("item_name") + "</h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("wh_code") + " ~ " + __rsData1.getString("wh_name") + "</h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("shelf_code") + " ~ " + __rsData1.getString("shelf_name") + "</h5></td>";
            __strDetail += "<td><h5>" + String.format("%.2f", Double.parseDouble(__rsData1.getString("qty"))) + "</h5></td>";
            __strDetail += "<td><input type='text' class='form-control text-center input-qty' value='" + String.format("%.2f", Double.parseDouble(__rsData1.getString("event_qty"))) + "' doc_no='" + __strDocNo + "' ref_code='" + __rsData1.getString("ref_code") + "' qty='" + __rsData1.getInt("qty") + "' remark='" + __rsData1.getString("remark") + "' ic_code='" + __rsData1.getString("ic_code") + "' status='" + __rsData1.getString("status") + "'></td>";
            __strDetail += "<td style='color: #FFF; background-color: " + __arrBgStatus[__status] + "'><h5>" + __arrStatus[__status] + "</h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("unit_name") + "</h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("remark") + "</h5></td>";

            __rsHTML += __strDetail;
            __rsHTML += "</tr>";

            __rowNumber++;
        }
        if (__strDetail.equals("")) {
            __rsHTML += "<tr><td colspan='12'>ไม่พบข้อมูล</td></tr>";
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

    private JSONObject _updateEvetnQTY(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strEventQTY = !param.isNull("event_qty") && param.getDouble("event_qty") >= 0 ? String.format("%.2f", param.getDouble("event_qty")) : "";
        Integer __strStatus = !param.isNull("status") && param.getInt("status") >= 0 ? param.getInt("status") : -1;
        String __strDocNo = !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? param.getString("doc_no") : "";
        String __strRemark = !param.isNull("remark") && !param.getString("remark").trim().isEmpty() ? param.getString("remark") : "";
        String __strIcCode = !param.isNull("ic_code") && !param.getString("ic_code").trim().isEmpty() ? param.getString("ic_code") : "";

        String __strQUERY = "UPDATE pp_trans_detail SET event_qty='" + __strEventQTY + "',remark='" + __strRemark + "',status='" + __strStatus + "' WHERE doc_no = '" + __strDocNo + "' AND ic_code='" + __strIcCode + "'";

        conn.setAutoCommit(false);
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.executeUpdate();
        __stmt1.close();
        conn.commit();
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _closeDocument(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strDocNo = !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? param.getString("doc_no") : "";
        String __strRefCode = !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? param.getString("ref_code") : "";
        Integer __strTransFlag = !param.isNull("trans_flag") ? param.getInt("trans_flag") : 0;

        String __strQUERY = "UPDATE pp_trans SET is_close=1,status=1,is_confirm=1,close_time='now()' WHERE doc_no = '" + __strDocNo + "' ";

        conn.setAutoCommit(false);
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.executeUpdate();
        __stmt1.close();

        __strQUERY = "UPDATE pp_trans_detail SET status=1,is_confirm=1 WHERE doc_no='" + __strDocNo + "' AND qty=event_qty";
        PreparedStatement __stmt2;
        __stmt2 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt2.executeUpdate();
        __stmt2.close();

        PreparedStatement __stmt3;
        __strQUERY = "INSERT INTO pp_trans_log (doc_no,ref_code,trans_flag,user_code,action) values ('" + __strDocNo + "','" + __strRefCode + "','" + __strTransFlag + "','" + __strUserCode + "','4')";
        __stmt3 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt3.executeUpdate();
        __stmt3.close();
        conn.commit();
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _confirmDocument(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strDocNo = !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? param.getString("doc_no") : "";
        String __strRefCode = !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? param.getString("ref_code") : "";
        Integer __strTransFlag = !param.isNull("trans_flag") ? param.getInt("trans_flag") : 0;

        String __strQUERY = "UPDATE pp_trans SET status=1,is_confirm=1,confirm_date_time='now()',confirm_date='now()',confirm_time='now()' WHERE doc_no='" + __strDocNo + "' ";

        conn.setAutoCommit(false);
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.executeUpdate();
        __stmt1.close();
        __strQUERY = "UPDATE pp_trans_detail SET status=1,is_confirm=1 WHERE doc_no='" + __strDocNo + "' AND qty=event_qty";
        PreparedStatement __stmt2;
        __stmt2 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt2.executeUpdate();
        __stmt2.close();
        __strQUERY = "INSERT INTO pp_trans_log (doc_no,ref_code,trans_flag,user_code,action) VALUES ('" + __strDocNo + "','" + __strRefCode + "','" + __strTransFlag + "','" + __strUserCode + "','3')";
        PreparedStatement __stmt3;
        __stmt3 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt3.executeUpdate();
        __stmt3.close();
        conn.commit();
        __objTMP.put("success", true);

        return __objTMP;
    }

}
