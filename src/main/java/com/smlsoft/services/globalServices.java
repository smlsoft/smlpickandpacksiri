package com.smlsoft.services;

import java.awt.List;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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

@WebServlet(name = "global-services-1", urlPatterns = {"/global-services-1"})
public class globalServices extends HttpServlet {

    private String error_doc_no = "";

    private final _routine __routine = new _routine();
    private String __strDatabaseName;
    private String __strProviderCode;
    private String __strUserCode;
    private String __strSystemID;
    private String __strSessionID;

    /**
     * Processes requests for both HTTP <code>GET</code> AND <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
                case "get_configs":
                    objResult = _getConfigs(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "check_print_document_1":
                    objResult = _checkPrintDocument1(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "check_print_document_2":
                    objResult = _checkPrintDocument2(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "check_cancel_document_1":
                    objResult = _checkCancelDocument1(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "check_cancel_document_2":
                    objResult = _checkCancelDocument2(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "active_users":
                    objResult = _activeUsers(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_permission":
                    objResult = _getPermissions(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "auto_approve":
                    objResult = _autoApprove(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "cancel_verify_document":
                    objResult = _cancelVerifyDocument(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "scan_document":
                    objResult = _scanDocument(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_reasons":
                    objResult = _getReasons(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "delete_document":
                    objResult = _deleteDocument(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
            }

        } catch (SQLException | JSONException ex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", "Exception Message: " + ex.getClass().getCanonicalName() + " :: " + ex.getMessage() + " :: " + error_doc_no);
            Logger.getLogger(List.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            objResult.put("err_title", "ข้อผิดพลาดทางระบบ");
            objResult.put("err_msg", "Exception Message: " + ex.getClass().getCanonicalName() + " :: " + ex.getMessage() + " :: " + error_doc_no);
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

    private JSONObject _getConfigs(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strQUERY = "SELECT SYS.group_code, SYS.system_id "
                + " ,COALESCE ((SELECT sml_group_location.whcode FROM sml_group_location WHERE SYS.group_code = sml_group_location.group_code), '') AS whcode "
                + " ,COALESCE ((SELECT sml_group_location.location FROM sml_group_location WHERE SYS.group_code = sml_group_location.group_code), '') AS location "
                + " ,COALESCE ((SELECT sml_group_location.name FROM sml_group_location WHERE SYS.group_code = sml_group_location.group_code), '') AS name "
                + " FROM sml_group_system AS SYS "
                + " WHERE system_id = '" + this.__strSystemID + "'";

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        String __strTMP1 = "";
        String __strTMP2 = "";
        String __strTMP3 = "";
        while (__rsData1.next()) {
            __strTMP1 = __rsData1.getString("group_code");
            __strTMP2 = __rsData1.getString("whcode");
            __strTMP3 = __rsData1.getString("location");
        }
        String __strGroupCode = __strTMP1;
        String[] __arrTMP2 = __strTMP2.split(",");
        String __strWhCode = "(";
        for (int i = 0; i < __arrTMP2.length; i++) {
            __strWhCode += i == 0 ? "'" + __arrTMP2[i] + "'" : ",'" + __arrTMP2[i] + "'";
        }
        __strWhCode += ")";

        String[] __arrTMP3 = __strTMP3.split(",");
        String __strLocation = "(";
        for (int i = 0; i < __arrTMP3.length; i++) {
            __strLocation += i == 0 ? "'" + __arrTMP3[i] + "'" : ",'" + __arrTMP3[i] + "'";
        }
        __strLocation += ")";

        __stmt1.close();
        __rsData1.close();

        __strQUERY = "SELECT code FROM ic_warehouse";

        PreparedStatement __stmt2;
        ResultSet __rsData2;
        __stmt2 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData2 = __stmt2.executeQuery();

        String __tmpWhCode = "";
        Integer __rowCountWhCode = 0;
        while (__rsData2.next()) {
            if (__rowCountWhCode == 0) {
                __tmpWhCode += __rsData2.getString("code");
            } else {
                __tmpWhCode += "," + __rsData2.getString("code");
            }
            __rowCountWhCode++;
        }

        __stmt2.close();
        __rsData2.close();

        __strQUERY = "SELECT code FROM ic_shelf";

        PreparedStatement __stmt3;
        ResultSet __rsData3;
        __stmt3 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData3 = __stmt3.executeQuery();

        String __tmpShelfCode = "";
        Integer __rowCountShelfCode = 0;
        while (__rsData3.next()) {
            if (__rowCountShelfCode == 0) {
                __tmpShelfCode += __rsData3.getString("code");
            } else {
                __tmpShelfCode += "," + __rsData3.getString("code");
            }
            __rowCountShelfCode++;
        }

        __stmt3.close();
        __rsData3.close();

        Set<String> __tmpArr1 = new HashSet<>(Arrays.asList(__tmpWhCode.split(",")));
        Set<String> __tmpArr2 = new HashSet<>(Arrays.asList(__tmpShelfCode.split(",")));

        __objTMP.put("group_code", __strGroupCode);
        __objTMP.put("wh_code", __strWhCode);
        __objTMP.put("shelf_code", __strLocation);

        __objTMP.put("arr_wh_code", __tmpArr1);
        __objTMP.put("arr_shelf_code", __tmpArr2);
        __objTMP.put("success", true);
        return __objTMP;
    }

    private JSONObject _getPermissions(Connection conn, JSONObject param) throws SQLException {
        conn = __routine._connect("smlerpmain" + __strProviderCode, _global.FILE_CONFIG(__strProviderCode));
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strPageCode = !param.isNull("page_code") && !param.getString("page_code").trim().isEmpty() ? param.getString("page_code") : "";
        String __strQUERY = "SELECT "
                + "  COALESCE(is_read,false) AS is_read"
                + " ,COALESCE(is_create,false) AS is_create"
                + " ,COALESCE(is_update,false) AS is_update"
                + " ,COALESCE(is_delete,false) AS is_delete"
                + " ,COALESCE(is_re_print,false) AS is_re_print, p_code, user_code FROM sml_user_permission WHERE UPPER(user_code) = '" + __strUserCode + "' AND p_code = '" + __strPageCode + "'";
        conn.setAutoCommit(false);
        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        __objTMP.put("data_1", ResponseUtil.query2Array(__rsData1));
        __stmt1.close();
        __rsData1.close();
        __strQUERY = "SELECT "
                + " COALESCE(g_r_status,false) AS is_read"
                + ",COALESCE(g_a_status,false) AS is_create"
                + ",COALESCE(g_e_status,false) AS is_update"
                + ",COALESCE(g_d_status,false) AS is_delete"
                + ",COALESCE(g_p_status,false) AS is_re_print"
                + " FROM sml_user_and_group "
                + " INNER JOIN sml_group_permission ON sml_user_and_group.group_code = sml_group_permission.g_code "
                + " WHERE sml_group_permission.p_code = '" + __strPageCode + "' "
                + " AND UPPER(sml_user_and_group.user_code) = '" + __strUserCode.toUpperCase() + "' ";
        PreparedStatement __stmt2;
        ResultSet __rsData2;
        __stmt2 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData2 = __stmt2.executeQuery();
        __objTMP.put("data_2", ResponseUtil.query2Array(__rsData2));
        __stmt2.close();
        __rsData2.close();
        conn.commit();
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _scanDocument(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strBarCode = !param.isNull("barcode") && !param.getString("barcode").trim().isEmpty() ? param.getString("barcode") : "";
        String __strTypeCode = !param.isNull("type_code") && !param.getString("type_code").trim().isEmpty() ? param.getString("type_code") : "";

        String __strQUERY;
        switch (__strTypeCode) {
            case "B":
                __strQUERY = "SELECT doc_no,trans_flag,ref_code FROM pp_trans WHERE b_doc_no='" + __strBarCode + "'";
                break;
            case "E":
                __strQUERY = "SELECT doc_no,trans_flag,ref_code FROM pp_trans WHERE e_doc_no='" + __strBarCode + "'";
                break;
            default:
                __strQUERY = "";
                break;
        }

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY);
        __rsData1 = __stmt1.executeQuery();
        __objTMP.put("data", ResponseUtil.query2Array(__rsData1));
        __stmt1.close();
        __rsData1.close();

        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _activeUsers(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strQUERY = "SELECT session_id,user_code FROM pp_user_active WHERE session_id='" + __strSessionID + "' AND  user_code='" + __strUserCode + "' ";
        Integer __rowCount1 = __routine._getRowCount(conn, __strQUERY);
        if (__rowCount1 > 0) {
            __strQUERY = "SELECT session_id,user_code FROM pp_user_active WHERE session_id='" + __strSessionID + "' AND  user_code='" + __strUserCode + "' AND last_accessed_time > NOW() - INTERVAL '1 HOUR'";
            Integer __rowCount2 = __routine._getRowCount(conn, __strQUERY);
            if (__rowCount2 > 0) {
                __strQUERY = "UPDATE  pp_user_active SET last_accessed_time='now()' WHERE session_id='" + __strSessionID + "' AND  user_code='" + __strUserCode + "'";
                PreparedStatement __stmt1;
                __stmt1 = conn.prepareStatement(__strQUERY);
                __stmt1.executeUpdate();
                __stmt1.close();
            }
        } else {
            __strQUERY = "INSERT INTO pp_user_active (session_id,user_code) VALUES ('" + __strSessionID + "','" + __strUserCode + "')";
            PreparedStatement __stmt1;
            __stmt1 = conn.prepareStatement(__strQUERY);
            __stmt1.executeUpdate();
            __stmt1.close();
        }

        __strQUERY = "DELETE FROM pp_user_active WHERE last_accessed_time  < NOW() - INTERVAL '1 HOUR'";
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY);
        __stmt1.executeUpdate();
        __stmt1.close();

        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _checkPrintDocument2(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strKeyID = !param.isNull("key_id") && !param.getString("key_id").trim().isEmpty() ? param.getString("key_id") : "";

        String __strQUERY = "SELECT doc_no,is_print FROM pp_trans WHERE doc_no='" + __strKeyID + "' and is_print = 1";
        Integer __rowCount1 = __routine._getRowCount(conn, __strQUERY);
        Integer __resultData;

        __strQUERY = "";

        if (__rowCount1 > 0) {
            __resultData = 1;
        } else {
            __resultData = 0;
        }

        __objTMP.put("row_count", __resultData);
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _checkPrintDocument1(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strKeyID = !param.isNull("key_id") && !param.getString("key_id").trim().isEmpty() ? param.getString("key_id") : "";
        String __strCarCode = !param.isNull("car_code") && !param.getString("car_code").trim().isEmpty() ? param.getString("car_code") : "";
        String __strShipmentCode = !param.isNull("shipment_code") && !param.getString("shipment_code").trim().isEmpty() ? param.getString("shipment_code") : "";
        String __strQUERY = "";
        if (__strCarCode.equals("") == false) {
            __strQUERY = "SELECT * from (SELECT ref_code,is_print FROM pp_trans WHERE ref_code='" + __strKeyID + "' and car_code = '" + __strCarCode + "' and tms_que_shipment_code = '" + __strShipmentCode + "' group by ref_code,is_print,car_code) as temp where is_print = 1";
        } else {
            __strQUERY = "SELECT * from (SELECT ref_code,max(is_print) as is_print FROM pp_trans WHERE ref_code='" + __strKeyID + "' group by ref_code) as temp where is_print = 1";
        }

        System.out.println(__strQUERY);
        Integer __rowCount1 = __routine._getRowCount(conn, __strQUERY);
        Integer __resultData;

        __strQUERY = "";

        if (__rowCount1 > 0) {
            __resultData = 1;
        } else {
            __resultData = 0;
        }

        __objTMP.put("row_count", __resultData);
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _checkCancelDocument1(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strKeyID = !param.isNull("key_id") && !param.getString("key_id").trim().isEmpty() ? param.getString("key_id") : "";

        String __strQUERY = "SELECT doc_no FROM ic_trans WHERE doc_no='" + __strKeyID + "' AND last_status=1";
        Integer __rowCount1 = __routine._getRowCount(conn, __strQUERY);
        Integer __resultData;
        if (__rowCount1 > 0) {
            __strQUERY = "SELECT doc_no FROM pp_trans WHERE ref_code='" + __strKeyID + "' AND last_status=0";
            Integer __rowCount2 = __routine._getRowCount(conn, __strQUERY);
            if (__rowCount2 > 0) {
                __resultData = 10;
            } else {
                __resultData = 11;
            }
        } else {
            __resultData = __rowCount1;
        }
        __objTMP.put("row_count", __resultData);
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _checkCancelDocument2(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");

//        String __strQUERY = "/*[PickAndPack]CHECK CANCEL DOCUMENT*/"
//                + " SELECT * FROM( "
//                + " SELECT pp_trans.last_status,pp_trans.creator_code,pp_trans.doc_no,pp_trans.status,pp_trans.doc_date,pp_trans.sale_type,pp_trans.ref_date,pp_trans.ref_code,pp_trans.sale_type,pp_trans.send_type,pp_trans.confirm_date_time "
//                + " ,COALESCE(department_code, '') AS department_code "
//                + " ,COALESCE((SELECT name_1 FROM erp_department_list WHERE erp_department_list.code = pp_trans.department_code),'') AS department_name "
//                + " ,COALESCE(cust_code, '') AS cust_code "
//                + " ,COALESCE((SELECT name_1 FROM ar_customer WHERE ar_customer.code = pp_trans.cust_code),'') AS cust_name "
//                + " ,CASE WHEN (SELECT count(*) FROM ic_trans WHERE ic_trans.doc_no=pp_trans.ref_code AND last_status=1)>0 THEN 0  ELSE 1 END AS FROM_detail "
//                + " ,COALESCE(branch_code, '') AS branch_code "
//                + " ,COALESCE((SELECT name_1 FROM erp_branch_list WHERE erp_branch_list.code = pp_trans.branch_code),'') AS branch_name "
//                + " ,COALESCE(confirm_code, '') AS confirm_code "
//                + " ,COALESCE((SELECT name_1 FROM erp_user WHERE erp_user.code = pp_trans.confirm_code),'') AS confirm_name "
//                + " ,COALESCE((SELECT wh_code FROM pp_trans_detail WHERE pp_trans_detail.doc_no = pp_trans.doc_no limit 1), '') AS wh_code "
//                + " ,COALESCE((SELECT name_1 FROM ic_warehouse WHERE ic_warehouse.code = (SELECT wh_code FROM pp_trans_detail WHERE pp_trans_detail.doc_no = pp_trans.doc_no limit 1)),'') AS wh_name "
//                + " FROM pp_trans WHERE status='1' "
//                + " ) AS pp_trans WHERE FROM_detail=0 AND last_status=0 ORDER BY FROM_detail ";
        String __strQUERY = "/*[PickAndPack]CHECK CANCEL DOCUMENT*/"
                + " WITH CANCEL AS ( "
                + " SELECT doc_no, ref_code FROM( "
                + " SELECT pp_trans.doc_no, pp_trans.ref_code, pp_trans.last_status "
                + " FROM pp_trans WHERE status=1 "
                + " AND EXISTS (SELECT doc_no FROM ic_trans WHERE ic_trans.last_status=1 AND ic_trans.doc_no=pp_trans.ref_code  and ic_trans.trans_flag=pp_trans.trans_flag) "
                + " ) AS pp_trans WHERE last_status=0) "
                + " UPDATE pp_trans SET last_status = 1 FROM cancel WHERE last_status = 0 AND pp_trans.doc_no = CANCEL.doc_no ";
        PreparedStatement __stmt1;
        System.out.println(__strQUERY);
        __stmt1 = conn.prepareStatement(__strQUERY);
        __stmt1.executeUpdate();
        __stmt1.close();
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _autoApprove(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        ArrayList<String> __arrListInsertPPtrans = new ArrayList<>();
        String __strWhCode = !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND (wh_code IN " + param.getString("wh_code") + ") " : "";
        String __strShelfCode = !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND (shelf_code IN " + param.getString("shelf_code") + ") " : "";
        String __strQUERY;
        conn.setAutoCommit(false);

        __strQUERY = "DELETE FROM pp_user_active WHERE last_accessed_time  < NOW() - INTERVAL '1 HOUR'";
        PreparedStatement __stmtDeleteUserUnActive;
        __stmtDeleteUserUnActive = conn.prepareStatement(__strQUERY);
        __stmtDeleteUserUnActive.executeUpdate();
        __stmtDeleteUserUnActive.close();

        __strQUERY = "SELECT session_id,user_code FROM pp_user_active WHERE last_accessed_time > NOW() - INTERVAL '5 MINUTE' ORDER BY login_time DESC LIMIT 1";

        PreparedStatement __stmtGetUserInActive;
        ResultSet __rsDataUserInActive;
        __stmtGetUserInActive = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsDataUserInActive = __stmtGetUserInActive.executeQuery();

        String __strSessionID_InActive = "";
        String __strUserCode_InActive = "";
        while (__rsDataUserInActive.next()) {
            __strSessionID_InActive = __rsDataUserInActive.getString("session_id").toUpperCase();
            __strUserCode_InActive = __rsDataUserInActive.getString("user_code").toUpperCase();
        }
        __rsDataUserInActive.close();
        __rsDataUserInActive.close();
        if (__strSessionID_InActive.equals(__strSessionID.toUpperCase()) && __strUserCode_InActive.equals(__strUserCode.toUpperCase())) {
//            __strQUERY = "SELECT * FROM ("
//                    + "SELECT ref_code,max(is_print) as is_print,COALESCE(CAST(lastedit_datetime AS VARCHAR), '') AS pp_last_edit  "
//                    + ",(SELECT doc_no FROM ic_trans WHERE pp_trans.ref_code=ic_trans.doc_no) as doc_no "
//                    + ",COALESCE((SELECT CAST(lastedit_datetime AS VARCHAR) FROM ic_trans WHERE pp_trans.ref_code=ic_trans.doc_no), '') AS ic_last_edit  "
//                    + "FROM pp_trans WHERE status=0 and is_close != 1 group by ref_code,pp_last_edit) AS temp1 "
//                    + "WHERE pp_last_edit <> ic_last_edit and is_print != 1 ORDER BY ref_code;";
            __strQUERY = "SELECT * FROM ( "
                    + " SELECT ref_code,max(is_print) as is_print,max(is_close) as is_close,COALESCE(CAST(lastedit_datetime AS VARCHAR), '') AS pp_last_edit  "
                    + " ,(SELECT doc_no FROM ic_trans WHERE pp_trans.ref_code=ic_trans.doc_no) as doc_no "
                    + " ,COALESCE((SELECT CAST(lastedit_datetime AS VARCHAR) FROM ic_trans WHERE pp_trans.ref_code=ic_trans.doc_no), '') AS ic_last_edit  "
                    + " FROM pp_trans WHERE status=0 group by ref_code,pp_last_edit) AS temp1 "
                    + " WHERE pp_last_edit <> ic_last_edit and is_print != 1 and is_close != 1 ORDER BY ref_code";
            PreparedStatement __stmtCheckLastEdit;
            ResultSet __rsDataCheckLastEdit;
            __stmtCheckLastEdit = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            __rsDataCheckLastEdit = __stmtCheckLastEdit.executeQuery();
            String __strRefCode = "";
            Integer __countLastEdit = 0;
            while (__rsDataCheckLastEdit.next()) {
                if (__countLastEdit == 0) {
                    __strRefCode += "'" + __rsDataCheckLastEdit.getString("ref_code") + "'";
                } else {
                    __strRefCode += ",'" + __rsDataCheckLastEdit.getString("ref_code") + "'";
                }
                __countLastEdit++;
            }
            __stmtCheckLastEdit.close();
            __rsDataCheckLastEdit.close();

            if (!__strRefCode.equals("")) {
                __strQUERY = "DELETE FROM pp_trans WHERE ref_code IN (" + __strRefCode + ")";
                PreparedStatement __stmtDeleteLastEdit;
                __stmtDeleteLastEdit = conn.prepareStatement(__strQUERY);
                __stmtDeleteLastEdit.executeUpdate();
                __stmtDeleteLastEdit.close();

                __strQUERY = "DELETE FROM pp_trans_detail WHERE ref_code IN (" + __strRefCode + ")";
                PreparedStatement __stmtDeleteLastEdit2;
                __stmtDeleteLastEdit2 = conn.prepareStatement(__strQUERY);
                __stmtDeleteLastEdit2.executeUpdate();
                __stmtDeleteLastEdit2.close();
            }

            __strQUERY = "SELECT begin_date FROM tms_config_date WHERE date_flag=2";
            String __strStartDate = "";
            PreparedStatement __stmtGetStartDate;
            __stmtGetStartDate = conn.prepareStatement(__strQUERY);
            ResultSet __rsDataStartDate;
            __rsDataStartDate = __stmtGetStartDate.executeQuery();

            while (__rsDataStartDate.next()) {
                __strStartDate = __rsDataStartDate.getString("begin_date");
            }
            __stmtGetStartDate.close();
            __rsDataStartDate.close();

            __strQUERY = "/*[PickAndPack]AUTO APPROVE 1*/\n"
                    + "SELECT is_hold,\n"
                    + "       trans_flag,\n"
                    + "       doc_date,\n"
                    + "       doc_no,\n"
                    + "       send_type\n"
                    + "FROM\n"
                    + "  (SELECT ic_trans.is_hold,\n"
                    + "          ic_trans.trans_flag,\n"
                    + "          ic_trans.doc_date,\n"
                    + "          ic_trans.doc_no,\n"
                    + "          ic_trans.send_type\n"
                    + "   FROM ic_trans\n"
                    + "   WHERE \n"
                    + "     doc_date >= '" + __strStartDate + "'\n"
                    + "     AND (is_hold = 0 OR (is_hold = 1 AND approve_status = 1))\n"
                    + "     AND last_status=0\n"
                    + "     AND pos_transfer = 0\n"
                    + "           AND ((trans_flag = '44'\n"
                    + "           AND inquiry_type IN (1,3)\n"
                    + "           AND send_type=0\n"
                    + "           AND NOT EXISTS\n"
                    + "             (SELECT doc_no\n"
                    + "              FROM pp_trans\n"
                    + "              WHERE pp_trans.ref_code = ic_trans.doc_no))\n"
                    + "          OR (trans_flag = '36'\n"
                    + "              AND inquiry_type IN (0,\n"
                    + "                                   2)\n"
                    + "              AND send_type=0\n"
                    + "              AND NOT EXISTS\n"
                    + "                (SELECT doc_no\n"
                    + "                 FROM pp_trans\n"
                    + "                 WHERE pp_trans.ref_code = ic_trans.doc_no))\n"
                    + "          OR (trans_flag = '44'\n"
                    + "              AND inquiry_type = 1\n"
                    + "              AND send_type=1\n"
                    + "              AND (\n"
                    + "                     (SELECT count(doc_no)\n"
                    + "                      FROM\n"
                    + "                        (SELECT DISTINCT doc_no,\n"
                    + "                                         wh_code,\n"
                    + "                                         shelf_code,\n"
                    + "                           (SELECT car_code\n"
                    + "                            FROM tms_que_shipment\n"
                    + "                            WHERE tms_que_shipment.doc_no = tms_que_shipment_details.doc_no)\n"
                    + "                         FROM tms_que_shipment_details\n"
                    + "                         WHERE tms_que_shipment_details.ref_doc_no = ic_trans.doc_no\n"
                    + "                           AND tms_que_shipment_details.trans_flag = ic_trans.trans_flag\n"
                    + "                           AND\n"
                    + "                             (SELECT shipment_status\n"
                    + "                              FROM tms_que_shipment\n"
                    + "                              WHERE tms_que_shipment.doc_no=tms_que_shipment_details.doc_no) >= 2\n"
                    + "                           " + __strWhCode + __strShelfCode + " )AS temp1) >\n"
                    + "                     (SELECT count(DISTINCT doc_no) AS doc_count\n"
                    + "                      FROM pp_trans\n"
                    + "                      WHERE pp_trans.ref_code = ic_trans.doc_no\n"
                    + "                        AND pp_trans.trans_flag = ic_trans.trans_flag )))\n"
                    + "          OR (trans_flag = '36'\n"
                    + "              AND inquiry_type IN (0,\n"
                    + "                                   2)\n"
                    + "              AND send_type=1\n"
                    + "              AND (\n"
                    + "                     (SELECT count(doc_no)\n"
                    + "                      FROM\n"
                    + "                        (SELECT DISTINCT doc_no,\n"
                    + "                                         wh_code,\n"
                    + "                                         shelf_code,\n"
                    + "                           (SELECT car_code\n"
                    + "                            FROM tms_que_shipment\n"
                    + "                            WHERE tms_que_shipment.doc_no = tms_que_shipment_details.doc_no)\n"
                    + "                         FROM tms_que_shipment_details\n"
                    + "                         WHERE tms_que_shipment_details.ref_doc_no = ic_trans.doc_no\n"
                    + "                           AND tms_que_shipment_details.trans_flag = ic_trans.trans_flag\n"
                    + "                           AND\n"
                    + "                             (SELECT shipment_status\n"
                    + "                              FROM tms_que_shipment\n"
                    + "                              WHERE tms_que_shipment.doc_no=tms_que_shipment_details.doc_no) >= 2\n"
                    + "                           " + __strWhCode + __strShelfCode + " )AS temp1) >\n"
                    + "                     (SELECT count(DISTINCT doc_no) AS doc_count\n"
                    + "                      FROM pp_trans\n"
                    + "                      WHERE pp_trans.ref_code = ic_trans.doc_no\n"
                    + "                        AND pp_trans.trans_flag = ic_trans.trans_flag))))\n"
                    + "     AND EXISTS\n"
                    + "       (SELECT doc_no\n"
                    + "        FROM ic_trans_detail\n"
                    + "        WHERE ic_trans_detail.doc_no=ic_trans.doc_no\n"
                    + "          AND ic_trans_detail.trans_flag=ic_trans.trans_flag\n"
                    + "          " + __strWhCode + __strShelfCode + " ) ) AS ic_trans\n"
                    + "ORDER BY doc_date DESC,\n"
                    + "         doc_no\n"
                    + "LIMIT 20\n"
                    + "OFFSET 0";
            System.out.println("__strQUERY +" + __strQUERY);
            PreparedStatement __stmtGetICTransList;
            ResultSet __rsDataICTrans;
            __stmtGetICTransList = conn.prepareStatement(__strQUERY);
            __rsDataICTrans = __stmtGetICTransList.executeQuery();

            conn.setAutoCommit(false);
            while (__rsDataICTrans.next()) {
                __arrListInsertPPtrans.add(__rsDataICTrans.getString("doc_no"));
                if (__rsDataICTrans.getString("send_type").equals("1") == false) {
                    __strQUERY = "SELECT DISTINCT wh_code,shelf_code FROM ic_trans_detail WHERE doc_no='" + __rsDataICTrans.getString("doc_no") + "'" + __strWhCode + __strShelfCode + " ORDER BY wh_code";
                    PreparedStatement __stmtGetWhCodeAndShelfCode;
                    ResultSet __rsDataWhCodeAndShelfCode;
                    __stmtGetWhCodeAndShelfCode = conn.prepareStatement(__strQUERY);
                    __rsDataWhCodeAndShelfCode = __stmtGetWhCodeAndShelfCode.executeQuery();

                    Integer __Line = 1;
                    while (__rsDataWhCodeAndShelfCode.next()) {
                        StringBuilder __strNewDocNo = new StringBuilder();
                        UUID __strUUID = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
                        String[] __arrGUID = __strUUID.randomUUID().toString().split("-");
                        __strNewDocNo.append(String.valueOf(__Line));
                        __strNewDocNo.append(__strNewDocNo).append(__arrGUID[0]);
                        String __strTmpDocNo = __rsDataICTrans.getString("doc_no") + '-';
                        if (String.valueOf(__Line).length() == 1) {
                            __strTmpDocNo += "0" + __Line;
                        } else if (String.valueOf(__Line).length() == 2) {
                            __strTmpDocNo += String.valueOf(__Line);
                        }

                        __strQUERY = "INSERT INTO pp_trans(trans_flag,doc_no,e_doc_no,b_doc_no,doc_date,ref_code,ref_date,due_date,cust_code,sale_code,status,remark,"
                                + " create_date_time_now,sale_type,send_type,department_code,branch_code,creator_code,create_date_time,confirm_code,total_amount,confirm_date,"
                                + " confirm_time,wh_code,shelf_code,confirm_date_time,lastedit_datetime) SELECT " + __rsDataICTrans.getString("trans_flag") + ",'" + __strTmpDocNo + "','E-" + __strNewDocNo + "'"
                                + " ,'B-" + __strNewDocNo + "',now(),doc_no,doc_date,due_date,cust_code,sale_code,0,remark,now(),inquiry_type,send_type,department_code,branch_code"
                                + " ,creator_code,create_datetime,'" + __strUserCode + "',total_amount,now(),now(),'" + __rsDataWhCodeAndShelfCode.getString("wh_code") + "'"
                                + " ,'" + __rsDataWhCodeAndShelfCode.getString("shelf_code") + "',now(),lastedit_datetime FROM ic_trans WHERE doc_no = '" + __rsDataICTrans.getString("doc_no") + "'";

                        PreparedStatement __stmtInsertPPTrans;
                        __stmtInsertPPTrans = conn.prepareStatement(__strQUERY);
                        __stmtInsertPPTrans.executeUpdate();
                        __stmtInsertPPTrans.close();

                        __strQUERY = "INSERT INTO pp_trans_detail (doc_no,doc_date,ref_code,ref_date,ic_code,wh_code,shelf_code,unit_code,qty,create_date_time_now,department_code,branch_code,line_number,event_qty,sum_amount,price) SELECT '" + __strTmpDocNo + "',now(),doc_no,doc_date,item_code,wh_code,shelf_code,unit_code,qty,now(),department_code,branch_code,line_number,qty,sum_amount,price FROM ic_trans_detail WHERE item_code <> '' and  item_code IS NOT NULL  and doc_no='" + __rsDataICTrans.getString("doc_no") + "' AND wh_code='" + __rsDataWhCodeAndShelfCode.getString("wh_code") + "' AND shelf_code='" + __rsDataWhCodeAndShelfCode.getString("shelf_code") + "' ";
                        PreparedStatement __stmtInsertPPTransDetails;
                        __stmtInsertPPTransDetails = conn.prepareStatement(__strQUERY);
                        __stmtInsertPPTransDetails.executeUpdate();
                        __stmtInsertPPTransDetails.close();

                        __strQUERY = "UPDATE pp_trans SET total_amount = (SELECT SUM(sum_amount) FROM pp_trans_detail WHERE doc_no='" + __strNewDocNo + "') WHERE doc_no='" + __strNewDocNo + "'";

                        PreparedStatement __stmtUpdateAmount;
                        __stmtUpdateAmount = conn.prepareStatement(__strQUERY);
                        __stmtUpdateAmount.executeUpdate();
                        __stmtUpdateAmount.close();

                        __Line++;
                    }
                    __rsDataWhCodeAndShelfCode.close();
                    __rsDataWhCodeAndShelfCode.close();
                    System.err.println(__rsDataICTrans.getString("doc_no") + ": send_type = 0");
                } else {
                    __strQUERY = "SELECT DISTINCT doc_no,tms_que_shipment_code FROM pp_trans_detail WHERE ref_code='" + __rsDataICTrans.getString("doc_no") + "'";
                    PreparedStatement __stmtGetCountDocument;
                    ResultSet __rsDataCountDocument;
                    __stmtGetCountDocument = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    __rsDataCountDocument = __stmtGetCountDocument.executeQuery();
                    __rsDataCountDocument.next();
                    Integer __Line = 0;
                    __Line = __rsDataCountDocument.getRow() + 1;

                    if (__Line == 0) {
                        __Line++;
                    }
                    __stmtGetCountDocument.close();
                    __rsDataCountDocument.close();
                    __strQUERY = "SELECT DISTINCT (SELECT car_code FROM tms_que_shipment WHERE tms_que_shipment.doc_no=tms_que_shipment_details.doc_no),doc_no,wh_code,shelf_code FROM tms_que_shipment_details WHERE ref_doc_no='" + __rsDataICTrans.getString("doc_no") + "'" + __strWhCode + __strShelfCode;
                    PreparedStatement __stmtGetTMSQueShipment;
                    ResultSet __rsDataTMSQueShipment;
                    __stmtGetTMSQueShipment = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    __rsDataTMSQueShipment = __stmtGetTMSQueShipment.executeQuery();

                    while (__rsDataTMSQueShipment.next()) {
                        StringBuilder __strNewDocNo = new StringBuilder();
                        UUID __strUUID = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
                        String[] __arrGUID = __strUUID.randomUUID().toString().split("-");
                        __strNewDocNo.append(String.valueOf(__Line));
                        __strNewDocNo.append(__strNewDocNo).append(__arrGUID[0]);
                        String __strTmpDocNo = __rsDataICTrans.getString("doc_no") + '-';
                        if (String.valueOf(__Line).length() == 1) {
                            __strTmpDocNo += "0" + __Line;
                        } else if (String.valueOf(__Line).length() == 2) {
                            __strTmpDocNo += String.valueOf(__Line);
                        }
                        __strQUERY = "SELECT doc_no FROM pp_trans WHERE doc_no='" + __strTmpDocNo + "'";
                        PreparedStatement __stmtCheckCountDocument;
                        ResultSet __rsDataCheckCountDocument;
                        __stmtCheckCountDocument = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        __rsDataCheckCountDocument = __stmtCheckCountDocument.executeQuery();
                        __rsDataCheckCountDocument.next();
                        Integer __rowCount = 0;
                        __rowCount = __rsDataCheckCountDocument.getRow();
                        if (__rowCount != 0) {
                            __Line++;
                            __rsDataTMSQueShipment.previous();
                        } else {
                            String __strQUERYCheck = "SELECT DISTINCT tms_que_shipment_code FROM pp_trans_detail WHERE pp_trans_detail.doc_no = '" + __strTmpDocNo + "' AND pp_trans_detail.car_code='" + __rsDataTMSQueShipment.getString("car_code") + "'";
                            PreparedStatement __stmtQUERYCheck;
                            ResultSet __rsDataQUERYCheck;
                            __stmtQUERYCheck = conn.prepareStatement(__strQUERYCheck, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            __rsDataQUERYCheck = __stmtQUERYCheck.executeQuery();
                            __rsDataQUERYCheck.next();
                            Integer __rowQUERYCheck = 0;
                            __rowQUERYCheck = __rsDataQUERYCheck.getRow();
                            System.out.println("rowCheck2 :" + __rowQUERYCheck);
                            error_doc_no = __strTmpDocNo;
                            if (__rowQUERYCheck > 1) {

                                String __strQUERYz = "delete  FROM pp_trans_detail WHERE doc_no='" + __strTmpDocNo + "'";
                                //System.out.println(__strQUERYz);
                                PreparedStatement __stmtDelete;
                                __stmtDelete = conn.prepareStatement(__strQUERYz);
                                __stmtDelete.executeUpdate();
                                __stmtDelete.close();
                                String __strQUERY2 = "delete  FROM pp_trans WHERE doc_no='" + __strTmpDocNo + "'";
                                //System.out.println(__strQUERY2);
                                PreparedStatement __stmtDelete2;
                                __stmtDelete2 = conn.prepareStatement(__strQUERY2);
                                __stmtDelete2.executeUpdate();
                                __stmtDelete2.close();
                            }
                            // ### INSERT pp_trans
                            __strQUERY = "INSERT INTO pp_trans(trans_flag,doc_no,e_doc_no,b_doc_no,doc_date,ref_code,ref_date,due_date,cust_code,sale_code,status,remark,"
                                    + " create_date_time_now,sale_type,send_type,department_code,branch_code,creator_code,create_date_time,confirm_code,total_amount,confirm_date,"
                                    + " confirm_time,wh_code,shelf_code,confirm_date_time,car_code,lastedit_datetime) SELECT " + __rsDataICTrans.getString("trans_flag") + ",'" + __strTmpDocNo + "','E-" + __strNewDocNo + "'"
                                    + " ,'B-" + __strNewDocNo + "',now(),doc_no,doc_date,due_date,cust_code,sale_code,0,remark,now(),inquiry_type,send_type,department_code,branch_code"
                                    + " ,creator_code,create_datetime,'" + __strUserCode + "',total_amount,now(),now(),'" + __rsDataTMSQueShipment.getString("wh_code") + "'"
                                    + " ,'" + __rsDataTMSQueShipment.getString("shelf_code") + "',now(),'" + __rsDataTMSQueShipment.getString("car_code") + "',lastedit_datetime FROM ic_trans WHERE doc_no = '" + __rsDataICTrans.getString("doc_no") + "' "
                                    + " AND not Exists(SELECT DISTINCT doc_no FROM pp_trans_detail WHERE pp_trans_detail.tms_que_shipment_code='" + __rsDataTMSQueShipment.getString("doc_no") + "' AND (SELECT car_code FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no )='" + __rsDataTMSQueShipment.getString("car_code") + "' AND wh_code='" + __rsDataTMSQueShipment.getString("wh_code") + "' AND shelf_code='" + __rsDataTMSQueShipment.getString("shelf_code") + "' AND ref_code='" + __rsDataICTrans.getString("doc_no") + "')";
                            PreparedStatement __stmtInsertPPTrans;
                            System.err.println("__strQUERY " + __strQUERY);
                            __stmtInsertPPTrans = conn.prepareStatement(__strQUERY);
                            __stmtInsertPPTrans.executeUpdate();
                            __stmtInsertPPTrans.close();

                            // ### INSERT pp_trans_detail
                            __strQUERY = "INSERT INTO pp_trans_detail (doc_no,doc_date,ref_code,ref_date,ic_code,wh_code,shelf_code,unit_code,qty,create_date_time_now,department_code,branch_code,line_number,event_qty,sum_amount,price,tms_que_shipment_code) SELECT '" + __strTmpDocNo + "',now(),ref_doc_no,doc_date,item_code,wh_code,shelf_code,unit_code,qty_deliver,now(),department_code,(SELECT branch_code FROM ic_trans WHERE ic_trans.doc_no=tms_que_shipment_details.ref_doc_no),line_number,qty_deliver,(qty_deliver*price)AS sum_amount,price,'" + __rsDataTMSQueShipment.getString("doc_no") + "' FROM tms_que_shipment_details WHERE doc_no='" + __rsDataTMSQueShipment.getString("doc_no") + "' AND ref_doc_no='" + __rsDataICTrans.getString("doc_no") + "' AND wh_code='" + __rsDataTMSQueShipment.getString("wh_code") + "' AND shelf_code='" + __rsDataTMSQueShipment.getString("shelf_code") + "' AND (SELECT car_code FROM tms_que_shipment WHERE tms_que_shipment.doc_no='" + __rsDataTMSQueShipment.getString("doc_no") + "' )='" + __rsDataTMSQueShipment.getString("car_code") + "' AND not Exists(SELECT DISTINCT doc_no FROM pp_trans_detail WHERE pp_trans_detail.tms_que_shipment_code='" + __rsDataTMSQueShipment.getString("doc_no") + "' AND (SELECT car_code FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no )='" + __rsDataTMSQueShipment.getString("car_code") + "' AND wh_code='" + __rsDataTMSQueShipment.getString("wh_code") + "' AND shelf_code='" + __rsDataTMSQueShipment.getString("shelf_code") + "' AND ref_code='" + __rsDataICTrans.getString("doc_no") + "' );";
                            System.err.println("__strQUERY " + __strQUERY);
                            PreparedStatement __stmtInsertPPTransDetails;
                            __stmtInsertPPTransDetails = conn.prepareStatement(__strQUERY);
                            __stmtInsertPPTransDetails.executeUpdate();
                            __stmtInsertPPTransDetails.close();
                            // ### UPDATE amount
                            __strQUERY = "UPDATE pp_trans SET total_amount = (SELECT SUM(sum_amount)  FROM pp_trans_detail WHERE doc_no='" + __strTmpDocNo + "' LIMIT 1) WHERE  doc_no='" + __strTmpDocNo + "'";
                            System.err.println("__strQUERY " + __strQUERY);
                            PreparedStatement __stmtUpdateAmount;
                            __stmtUpdateAmount = conn.prepareStatement(__strQUERY);
                            __stmtUpdateAmount.executeUpdate();
                            __stmtUpdateAmount.close();
                            // ### UPDATE car_code
                            __strQUERY = "UPDATE pp_trans_detail SET car_code = (SELECT DISTINCT car_code FROM pp_trans WHERE pp_trans.doc_no = '" + __strTmpDocNo + "' LIMIT 1) WHERE doc_no = '" + __strTmpDocNo + "' ";
                            System.err.println("__strQUERY " + __strQUERY);
                            PreparedStatement __stmtUpdateCarCode;
                            __stmtUpdateCarCode = conn.prepareStatement(__strQUERY);
                            __stmtUpdateCarCode.executeUpdate();
                            __stmtUpdateCarCode.close();
                            // ### UPDATE tms_que_shipment_code
                            __strQUERY = "UPDATE pp_trans SET tms_que_shipment_code = (SELECT DISTINCT tms_que_shipment_code FROM pp_trans_detail WHERE pp_trans_detail.doc_no = '" + __strTmpDocNo + "' AND pp_trans_detail.car_code='" + __rsDataTMSQueShipment.getString("car_code") + "' LIMIT 1) WHERE doc_no = '" + __strTmpDocNo + "' AND car_code='" + __rsDataTMSQueShipment.getString("car_code") + "'";
                            System.err.println("__strQUERY " + __strQUERY);
                            PreparedStatement __stmtTMSQueShipmentCode;
                            __stmtTMSQueShipmentCode = conn.prepareStatement(__strQUERY);
                            __stmtTMSQueShipmentCode.executeUpdate();
                            __stmtTMSQueShipmentCode.close();

                        }
                    }
                    __stmtGetTMSQueShipment.close();
                    __rsDataTMSQueShipment.close();
                    System.err.println("send_type = 1");
                }
            }
            __rsDataICTrans.close();

            __strQUERY = "/*[PickAndPack]AUTO APPROVE 2*/\n"
                    + "SELECT DISTINCT reserve_doc_no AS doc_no,\n"
                    + "                doc_ref,\n"
                    + "                wh_code,\n"
                    + "  (SELECT send_type\n"
                    + "   FROM ic_trans\n"
                    + "   WHERE ic_trans.doc_no=reserve_doc_no),shelf_code,\n"
                    + "                                         34 AS trans_flag\n"
                    + "FROM\n"
                    + "  (SELECT doc_no,\n"
                    + "          ref_doc_no,\n"
                    + "          wh_code,\n"
                    + "          shelf_code,\n"
                    + "          trans_flag,\n"
                    + "          ref_doc_no || '_' || doc_no || '_' AS doc_ref,\n"
                    + "\n"
                    + "     (SELECT ref_doc_no\n"
                    + "      FROM ic_trans_detail AS po\n"
                    + "      WHERE po.trans_flag = 6\n"
                    + "        AND po.doc_no = ic_trans_detail.ref_doc_no\n"
                    + "        AND po.line_number = ic_trans_detail.ref_row) AS reserve_doc_no\n"
                    + "   FROM ic_trans_detail\n"
                    + "   WHERE trans_flag IN (12,\n"
                    + "                        310)\n"
                    + "     AND doc_date >= '" + __strStartDate + "' )AS rec\n"
                    + "WHERE exists\n"
                    + "    (SELECT doc_no\n"
                    + "     FROM ic_trans\n"
                    + "     WHERE ic_trans.trans_flag = 34\n"
                    + "       AND ic_trans.last_status = 0\n"
                    + "       AND ic_trans.doc_no = rec.reserve_doc_no\n"
                    + "       AND ic_trans.doc_success = 0\n"
                    + "       AND ic_trans.inquiry_type IN (0, 2)\n"
                    + "       AND ((ic_trans.send_type=0\n"
                    + "             AND NOT EXISTS\n"
                    + "               (SELECT doc_no\n"
                    + "                FROM pp_trans\n"
                    + "                WHERE pp_trans.ref_code = ic_trans.doc_no))\n"
                    + "            OR (ic_trans.send_type=1\n"
                    + "                AND (\n"
                    + "                       (SELECT count(doc_no)\n"
                    + "                        FROM\n"
                    + "                          (SELECT DISTINCT doc_no, wh_code, shelf_code,\n"
                    + "                             (SELECT car_code\n"
                    + "                              FROM tms_que_shipment\n"
                    + "                              WHERE tms_que_shipment.doc_no = tms_que_shipment_details.doc_no)\n"
                    + "                           FROM tms_que_shipment_details\n"
                    + "                           WHERE tms_que_shipment_details.ref_doc_no = ic_trans.doc_no\n"
                    + "                             AND tms_que_shipment_details.trans_flag = ic_trans.trans_flag\n"
                    + "                             " + __strWhCode + __strShelfCode + " ) AS temp1) >\n"
                    + "                       (SELECT count(DISTINCT doc_no) AS doc_count\n"
                    + "                        FROM pp_trans\n"
                    + "                        WHERE pp_trans.ref_code = ic_trans.doc_no\n"
                    + "                          AND pp_trans.trans_flag = ic_trans.trans_flag))))\n"
                    + "       AND (ic_trans.is_hold = 0\n"
                    + "            OR (ic_trans.is_hold = 1\n"
                    + "                AND ic_trans.approve_status = 1))\n"
                    + "       AND EXISTS\n"
                    + "         (SELECT doc_no\n"
                    + "          FROM ic_trans_detail\n"
                    + "          WHERE ic_trans_detail.doc_no=ic_trans.doc_no\n"
                    + "            AND ic_trans_detail.trans_flag=ic_trans.trans_flag\n"
                    + "            " + __strWhCode + __strShelfCode + " )\n"
                    + "     ORDER BY doc_date DESC,doc_no)";

            __stmtGetICTransList = conn.prepareStatement(__strQUERY);
            __rsDataICTrans = __stmtGetICTransList.executeQuery();
            String __strOldDocNo = "";
            System.out.println("__strQUERY2 " + __strQUERY);
            while (__rsDataICTrans.next()) {
                if (__rsDataICTrans.getString("send_type").equals("1") == false) {
                    if (__strOldDocNo.equals(__rsDataICTrans.getString("doc_no")) == false) {
                        __strOldDocNo = __rsDataICTrans.getString("doc_no");
                        __strQUERY = "/*[PickAndPack]AUTO APPROVE 3*/"
                                + " SELECT DISTINCT wh_code,shelf_code FROM ( "
                                + " SELECT  wh_code,shelf_code "
                                + " ,(SELECT ref_doc_no FROM ic_trans_detail AS po WHERE po.trans_flag = 6 AND po.doc_no = ic_trans_detail.ref_doc_no AND po.line_number = ic_trans_detail.ref_row) AS reserve_doc_no "
                                + " FROM ic_trans_detail WHERE trans_flag in (12, 310) "
                                + " AND doc_date >= '" + __strStartDate + "' "
                                + " )AS rec WHERE exists( SELECT doc_no FROM ic_trans WHERE ic_trans.trans_flag = 34 AND ic_trans.last_status = 0 AND ic_trans.doc_no = rec.reserve_doc_no AND ic_trans.doc_success = 0 "
                                + " AND ic_trans.inquiry_type in (0,2) AND (ic_trans.send_type=0  or ( ic_trans.send_type=1 AND EXISTS (SELECT doc_no FROM tms_que_shipment_details WHERE  tms_que_shipment_details.ref_doc_no = COALESCE((SELECT DISTINCT ON (doc_no) doc_no FROM tms_send_assign_detail WHERE tms_send_assign_detail.ref_doc_no = ic_trans.doc_no), ic_trans.doc_no) AND tms_que_shipment_details.trans_flag = ic_trans.trans_flag limit 1))) "
                                + " AND (ic_trans.is_hold = 0 or (ic_trans.is_hold = 1 AND ic_trans.approve_status = 1)) "
                                + " AND rec.reserve_doc_no='" + __rsDataICTrans.getString("doc_no") + "' " + __strWhCode + __strShelfCode + "  ORDER BY wh_code)";

                        PreparedStatement __stmtGetWhCodeAndShelfCode;
                        ResultSet __rsDataWhCodeAndShelfCode;
                        __stmtGetWhCodeAndShelfCode = conn.prepareStatement(__strQUERY);
                        __rsDataWhCodeAndShelfCode = __stmtGetWhCodeAndShelfCode.executeQuery();
                        Integer __Line = 1;
                        while (__rsDataWhCodeAndShelfCode.next()) {
                            StringBuilder __strNewDocNo = new StringBuilder();
                            UUID __strUUID = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
                            String[] __arrGUID = __strUUID.randomUUID().toString().split("-");
                            __strNewDocNo.append(String.valueOf(__Line));
                            __strNewDocNo.append(__strNewDocNo).append(__arrGUID[0]);
                            String __strTmpDocNo = __rsDataICTrans.getString("doc_no") + '-';
                            if (String.valueOf(__Line).length() == 1) {
                                __strTmpDocNo += "0" + __Line;
                            } else if (String.valueOf(__Line).length() == 2) {
                                __strTmpDocNo += String.valueOf(__Line);
                            }
                            // ### INSERT pp_trans
                            __strQUERY = "INSERT INTO pp_trans(trans_flag,doc_no,e_doc_no,b_doc_no,doc_date,ref_code,ref_date,due_date,cust_code,sale_code,status,remark,"
                                    + " create_date_time_now,sale_type,send_type,department_code,branch_code,creator_code,create_date_time,confirm_code,total_amount,confirm_date,"
                                    + " confirm_time,wh_code,shelf_code,confirm_date_time,lastedit_datetime) SELECT " + __rsDataICTrans.getString("trans_flag") + ",'" + __strTmpDocNo + "','E-" + __strNewDocNo + "'"
                                    + " ,'B-" + __strNewDocNo + "',now(),'" + __rsDataICTrans.getString("doc_no") + "',doc_date,due_date,cust_code,sale_code,0,remark,now(),inquiry_type,send_type,department_code,branch_code"
                                    + " ,creator_code,create_datetime,'" + __strUserCode + "',total_amount,now(),now(),'" + __rsDataWhCodeAndShelfCode.getString("wh_code") + "'"
                                    + " ,'" + __rsDataWhCodeAndShelfCode.getString("shelf_code") + "',now(),lastedit_datetime FROM ic_trans WHERE doc_no = '" + __rsDataICTrans.getString("doc_no") + "'";

                            PreparedStatement __stmtInsertPPTrans;
                            __stmtInsertPPTrans = conn.prepareStatement(__strQUERY);
                            __stmtInsertPPTrans.executeUpdate();
                            __stmtInsertPPTrans.close();
                            // ### UPDATE pp_trans_detail
                            __strQUERY = "INSERT INTO pp_trans_detail (doc_no,doc_date,ref_code,ref_date,ic_code,wh_code,shelf_code,unit_code,qty,create_date_time_now,department_code,branch_code,line_number,event_qty,sum_amount,price) "
                                    + " SELECT doc_no,now(),ref_code,doc_date,item_code, wh_code, shelf_code,unit_code, qty, now(),department_code,branch_code,line_number,event_qty,sum_amount,price FROM ( "
                                    + " SELECT '" + __strTmpDocNo + "' AS doc_no,now(),'" + __rsDataICTrans.getString("doc_no") + "' AS ref_code,doc_date, item_code, wh_code, shelf_code,unit_code, qty, now(),department_code,branch_code,line_number,qty AS event_qty,sum_amount,price "
                                    + " , (SELECT ref_doc_no FROM ic_trans_detail AS po WHERE po.trans_flag = 6 AND po.doc_no = ic_trans_detail.ref_doc_no AND po.line_number = ic_trans_detail.ref_row) AS reserve_doc_no "
                                    + " FROM ic_trans_detail WHERE trans_flag in (12, 310) "
                                    + " AND doc_date >= '" + __strStartDate + "' "
                                    + " ) AS rec WHERE exists( SELECT doc_no FROM ic_trans WHERE ic_trans.trans_flag = 34 AND ic_trans.last_status = 0 AND ic_trans.doc_no = rec.reserve_doc_no AND ic_trans.doc_success = 0)"
                                    + " AND rec.reserve_doc_no='" + __rsDataICTrans.getString("doc_no") + "' AND wh_code='" + __rsDataWhCodeAndShelfCode.getString("wh_code") + "' AND shelf_code='" + __rsDataWhCodeAndShelfCode.getString("shelf_code") + "'";
                            PreparedStatement __stmtInsertPPTransDetails;
                            __stmtInsertPPTransDetails = conn.prepareStatement(__strQUERY);
                            __stmtInsertPPTransDetails.executeUpdate();
                            __stmtInsertPPTransDetails.close();
                            // ### UPDATE amount
                            __strQUERY = "UPDATE pp_trans SET total_amount = (SELECT SUM(sum_amount)  FROM pp_trans_detail WHERE doc_no='" + __strTmpDocNo + "') WHERE  doc_no='" + __strTmpDocNo + "'";
                            PreparedStatement __stmtUpdateAmount;
                            __stmtUpdateAmount = conn.prepareStatement(__strQUERY);
                            __stmtUpdateAmount.executeUpdate();
                            __stmtUpdateAmount.close();

                            __Line++;
                        }
                    }
                } else {
                    if (__strOldDocNo.equals(__rsDataICTrans.getString("doc_no")) == false) {
                        __strOldDocNo = __rsDataICTrans.getString("doc_no");
                        __strQUERY = "SELECT DISTINCT doc_no,tms_que_shipment_code FROM pp_trans_detail WHERE ref_code='" + __rsDataICTrans.getString("doc_no") + "'";
                        PreparedStatement __stmtGetCountDocument;
                        ResultSet __rsDataCountDocument;
                        __stmtGetCountDocument = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        __rsDataCountDocument = __stmtGetCountDocument.executeQuery();
                        __rsDataCountDocument.next();
                        Integer __Line = 0;
                        __Line = __rsDataCountDocument.getRow() + 1;

                        if (__Line == 0) {
                            __Line++;
                        }
                        __stmtGetCountDocument.close();
                        __rsDataCountDocument.close();
                        __strQUERY = "SELECT DISTINCT (SELECT car_code FROM tms_que_shipment WHERE tms_que_shipment.doc_no=tms_que_shipment_details.doc_no),doc_no,wh_code,shelf_code FROM tms_que_shipment_details WHERE ref_doc_no=COALESCE((SELECT DISTINCT ON (doc_no) doc_no FROM tms_send_assign_detail WHERE tms_send_assign_detail.ref_doc_no = '" + __rsDataICTrans.getString("doc_no") + "'), '" + __rsDataICTrans.getString("doc_no") + "')" + __strWhCode + __strShelfCode;
                        PreparedStatement __stmtGetTMSQueShipment;
                        ResultSet __rsDataTMSQueShipment;
                        __stmtGetTMSQueShipment = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        __rsDataTMSQueShipment = __stmtGetTMSQueShipment.executeQuery();

                        while (__rsDataTMSQueShipment.next()) {
                            StringBuilder __strNewDocNo = new StringBuilder();
                            UUID __strUUID = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
                            String[] __arrGUID = __strUUID.randomUUID().toString().split("-");
                            __strNewDocNo.append(String.valueOf(__Line));
                            __strNewDocNo.append(__strNewDocNo).append(__arrGUID[0]);
                            String __strTmpDocNo = __rsDataICTrans.getString("doc_no") + '-';
                            if (String.valueOf(__Line).length() == 1) {
                                __strTmpDocNo += "0" + __Line;
                            } else if (String.valueOf(__Line).length() == 2) {
                                __strTmpDocNo += String.valueOf(__Line);
                            }

                            __strQUERY = "SELECT doc_no FROM pp_trans WHERE doc_no='" + __strTmpDocNo + "'";
                            PreparedStatement __stmtCheckCountDocument;
                            ResultSet __rsDataCheckCountDocument;
                            __stmtCheckCountDocument = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            __rsDataCheckCountDocument = __stmtCheckCountDocument.executeQuery();
                            __rsDataCheckCountDocument.next();
                            Integer __rowCount = 0;
                            __rowCount = __rsDataCheckCountDocument.getRow();
                            error_doc_no = __strTmpDocNo;
                            if (__rowCount != 0) {
                                __Line++;
                                __rsDataTMSQueShipment.previous();
                            } else {
                                String __strQUERYCheck = "SELECT DISTINCT tms_que_shipment_code FROM pp_trans_detail WHERE pp_trans_detail.doc_no = '" + __strTmpDocNo + "' AND pp_trans_detail.car_code='" + __rsDataTMSQueShipment.getString("car_code") + "'";
                                PreparedStatement __stmtQUERYCheck;

                                __strQUERY = "INSERT INTO pp_trans(trans_flag,doc_no,e_doc_no,b_doc_no,doc_date,ref_code,ref_date,due_date,cust_code,sale_code,status,remark,"
                                        + " create_date_time_now,sale_type,send_type,department_code,branch_code,creator_code,create_date_time,confirm_code,total_amount,confirm_date,"
                                        + " confirm_time,wh_code,shelf_code,confirm_date_time,car_code,lastedit_datetime) SELECT " + __rsDataICTrans.getString("trans_flag") + ",'" + __strTmpDocNo + "','E-" + __strNewDocNo + "'"
                                        + " ,'B-" + __strNewDocNo + "',now(),doc_no,doc_date,due_date,cust_code,sale_code,0,remark,now(),inquiry_type,send_type,department_code,branch_code"
                                        + " ,creator_code,create_datetime,'" + __strUserCode + "',total_amount,now(),now(),'" + __rsDataTMSQueShipment.getString("wh_code") + "'"
                                        + " ,'" + __rsDataTMSQueShipment.getString("shelf_code") + "',now(),'" + __rsDataTMSQueShipment.getString("car_code") + "',lastedit_datetime FROM ic_trans WHERE doc_no = '" + __rsDataICTrans.getString("doc_no") + "'"
                                        + " AND not Exists(SELECT DISTINCT doc_no FROM pp_trans_detail WHERE pp_trans_detail.tms_que_shipment_code='" + __rsDataTMSQueShipment.getString("doc_no") + "' AND (SELECT car_code FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no )='" + __rsDataTMSQueShipment.getString("car_code") + "' AND wh_code='" + __rsDataTMSQueShipment.getString("wh_code") + "' AND shelf_code='" + __rsDataTMSQueShipment.getString("shelf_code") + "' AND ref_code='" + __rsDataICTrans.getString("doc_no") + "')";
                                System.out.println(__strQUERY);
                                PreparedStatement __stmtInsertPPTrans;
                                __stmtInsertPPTrans = conn.prepareStatement(__strQUERY);

                                __stmtInsertPPTrans.executeUpdate();
                                __stmtInsertPPTrans.close();
                                __strQUERY = "INSERT INTO pp_trans_detail (doc_no,doc_date,ref_code,ref_date,ic_code,wh_code,shelf_code,unit_code,qty,create_date_time_now,department_code,branch_code,line_number,event_qty,sum_amount,price,tms_que_shipment_code) SELECT '" + __strTmpDocNo + "',now(),COALESCE((SELECT DISTINCT ON (doc_no) ref_doc_no FROM tms_send_assign_detail WHERE tms_send_assign_detail.ref_doc_no = '" + __rsDataICTrans.getString("doc_no") + "'), '" + __rsDataICTrans.getString("doc_no") + "'),doc_date,item_code,wh_code,shelf_code,unit_code,qty_deliver,now(),department_code,(SELECT branch_code FROM ic_trans WHERE ic_trans.doc_no=tms_que_shipment_details.ref_doc_no),line_number,qty_deliver,(qty_deliver*price)AS sum_amount,price,'" + __rsDataTMSQueShipment.getString("doc_no") + "' FROM tms_que_shipment_details WHERE doc_no='" + __rsDataTMSQueShipment.getString("doc_no") + "' AND ref_doc_no=COALESCE((SELECT DISTINCT ON (doc_no) doc_no FROM tms_send_assign_detail WHERE tms_send_assign_detail.ref_doc_no = '" + __rsDataICTrans.getString("doc_no") + "'), '" + __rsDataICTrans.getString("doc_no") + "') AND wh_code='" + __rsDataTMSQueShipment.getString("wh_code") + "' AND shelf_code='" + __rsDataTMSQueShipment.getString("shelf_code") + "' AND (SELECT car_code FROM tms_que_shipment WHERE tms_que_shipment.doc_no='" + __rsDataTMSQueShipment.getString("doc_no") + "' )='" + __rsDataTMSQueShipment.getString("car_code") + "' AND not Exists(SELECT DISTINCT doc_no FROM pp_trans_detail WHERE pp_trans_detail.tms_que_shipment_code='" + __rsDataTMSQueShipment.getString("doc_no") + "' AND (SELECT car_code FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no )='" + __rsDataTMSQueShipment.getString("car_code") + "' AND wh_code='" + __rsDataTMSQueShipment.getString("wh_code") + "' AND shelf_code='" + __rsDataTMSQueShipment.getString("shelf_code") + "' AND ref_code='" + __rsDataICTrans.getString("doc_no") + "');";
                                System.out.println(__strQUERY);
                                PreparedStatement __stmtInsertPPTransDetails;
                                __stmtInsertPPTransDetails = conn.prepareStatement(__strQUERY);

                                __stmtInsertPPTransDetails.executeUpdate();
                                __stmtInsertPPTransDetails.close();
                                // ### UPDATE amount
                                __strQUERY = "UPDATE pp_trans SET total_amount = (SELECT SUM(sum_amount)  FROM pp_trans_detail WHERE doc_no='" + __strTmpDocNo + "') WHERE  doc_no='" + __strTmpDocNo + "'";
                                System.out.println(__strQUERY);
                                PreparedStatement __stmtUpdateAmount;
                                __stmtUpdateAmount = conn.prepareStatement(__strQUERY);

                                __stmtUpdateAmount.executeUpdate();
                                __stmtUpdateAmount.close();
                                // ### UPDATE car_code
                                __strQUERY = "UPDATE pp_trans_detail SET car_code = (SELECT car_code FROM pp_trans WHERE pp_trans.doc_no = '" + __strTmpDocNo + "') WHERE doc_no = '" + __strTmpDocNo + "' ";
                                System.out.println(__strQUERY);
                                PreparedStatement __stmtUpdateCarCode;
                                __stmtUpdateCarCode = conn.prepareStatement(__strQUERY);

                                __stmtUpdateCarCode.executeUpdate();
                                __stmtUpdateCarCode.close();

                                ResultSet __rsDataQUERYCheck;
                                __stmtQUERYCheck = conn.prepareStatement(__strQUERYCheck, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                                __rsDataQUERYCheck = __stmtQUERYCheck.executeQuery();
                                __rsDataQUERYCheck.next();
                                Integer __rowQUERYCheck = 0;
                                __rowQUERYCheck = __rsDataQUERYCheck.getRow();
                                System.out.println("rowCheck :" + __rowQUERYCheck);
                                if (__rowQUERYCheck > 1) {

                                    String __strQUERYz = "delete  FROM pp_trans_detail WHERE doc_no='" + __strTmpDocNo + "'";
                                    System.out.println(__strQUERYz);
                                    PreparedStatement __stmtDelete;
                                    __stmtDelete = conn.prepareStatement(__strQUERYz);
                                    __stmtDelete.executeUpdate();
                                    __stmtDelete.close();
                                    String __strQUERY2 = "delete  FROM pp_trans WHERE doc_no='" + __strTmpDocNo + "'";
                                    System.out.println(__strQUERY2);
                                    PreparedStatement __stmtDelete2;
                                    __stmtDelete2 = conn.prepareStatement(__strQUERY2);
                                    __stmtDelete2.executeUpdate();
                                    __stmtDelete2.close();

                                    __strQUERY = "INSERT INTO pp_trans(trans_flag,doc_no,e_doc_no,b_doc_no,doc_date,ref_code,ref_date,due_date,cust_code,sale_code,status,remark,"
                                            + " create_date_time_now,sale_type,send_type,department_code,branch_code,creator_code,create_date_time,confirm_code,total_amount,confirm_date,"
                                            + " confirm_time,wh_code,shelf_code,confirm_date_time,car_code,lastedit_datetime) SELECT " + __rsDataICTrans.getString("trans_flag") + ",'" + __strTmpDocNo + "','E-" + __strNewDocNo + "'"
                                            + " ,'B-" + __strNewDocNo + "',now(),doc_no,doc_date,due_date,cust_code,sale_code,0,remark,now(),inquiry_type,send_type,department_code,branch_code"
                                            + " ,creator_code,create_datetime,'" + __strUserCode + "',total_amount,now(),now(),'" + __rsDataTMSQueShipment.getString("wh_code") + "'"
                                            + " ,'" + __rsDataTMSQueShipment.getString("shelf_code") + "',now(),'" + __rsDataTMSQueShipment.getString("car_code") + "',lastedit_datetime FROM ic_trans WHERE doc_no = '" + __rsDataICTrans.getString("doc_no") + "'"
                                            + " AND not Exists(SELECT DISTINCT doc_no FROM pp_trans_detail WHERE pp_trans_detail.tms_que_shipment_code='" + __rsDataTMSQueShipment.getString("doc_no") + "' AND (SELECT car_code FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no )='" + __rsDataTMSQueShipment.getString("car_code") + "' AND wh_code='" + __rsDataTMSQueShipment.getString("wh_code") + "' AND shelf_code='" + __rsDataTMSQueShipment.getString("shelf_code") + "' AND ref_code='" + __rsDataICTrans.getString("doc_no") + "')";
                                    System.out.println(__strQUERY);
                                    PreparedStatement __stmtInsertPPTrans2;
                                    __stmtInsertPPTrans2 = conn.prepareStatement(__strQUERY);

                                    __stmtInsertPPTrans2.executeUpdate();
                                    __stmtInsertPPTrans2.close();
                                    __strQUERY = "INSERT INTO pp_trans_detail (doc_no,doc_date,ref_code,ref_date,ic_code,wh_code,shelf_code,unit_code,qty,create_date_time_now,department_code,branch_code,line_number,event_qty,sum_amount,price,tms_que_shipment_code) SELECT '" + __strTmpDocNo + "',now(),COALESCE((SELECT DISTINCT ON (doc_no) ref_doc_no FROM tms_send_assign_detail WHERE tms_send_assign_detail.ref_doc_no = '" + __rsDataICTrans.getString("doc_no") + "'), '" + __rsDataICTrans.getString("doc_no") + "'),doc_date,item_code,wh_code,shelf_code,unit_code,qty_deliver,now(),department_code,(SELECT branch_code FROM ic_trans WHERE ic_trans.doc_no=tms_que_shipment_details.ref_doc_no),line_number,qty_deliver,(qty_deliver*price)AS sum_amount,price,'" + __rsDataTMSQueShipment.getString("doc_no") + "' FROM tms_que_shipment_details WHERE doc_no='" + __rsDataTMSQueShipment.getString("doc_no") + "' AND ref_doc_no=COALESCE((SELECT DISTINCT ON (doc_no) doc_no FROM tms_send_assign_detail WHERE tms_send_assign_detail.ref_doc_no = '" + __rsDataICTrans.getString("doc_no") + "'), '" + __rsDataICTrans.getString("doc_no") + "') AND wh_code='" + __rsDataTMSQueShipment.getString("wh_code") + "' AND shelf_code='" + __rsDataTMSQueShipment.getString("shelf_code") + "' AND (SELECT car_code FROM tms_que_shipment WHERE tms_que_shipment.doc_no='" + __rsDataTMSQueShipment.getString("doc_no") + "' )='" + __rsDataTMSQueShipment.getString("car_code") + "' AND not Exists(SELECT DISTINCT doc_no FROM pp_trans_detail WHERE pp_trans_detail.tms_que_shipment_code='" + __rsDataTMSQueShipment.getString("doc_no") + "' AND (SELECT car_code FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no )='" + __rsDataTMSQueShipment.getString("car_code") + "' AND wh_code='" + __rsDataTMSQueShipment.getString("wh_code") + "' AND shelf_code='" + __rsDataTMSQueShipment.getString("shelf_code") + "' AND ref_code='" + __rsDataICTrans.getString("doc_no") + "');";
                                    System.out.println(__strQUERY);
                                    PreparedStatement __stmtInsertPPTransDetails2;
                                    __stmtInsertPPTransDetails2 = conn.prepareStatement(__strQUERY);

                                    __stmtInsertPPTransDetails2.executeUpdate();
                                    __stmtInsertPPTransDetails2.close();
                                    // ### UPDATE amount
                                    __strQUERY = "UPDATE pp_trans SET total_amount = (SELECT SUM(sum_amount)  FROM pp_trans_detail WHERE doc_no='" + __strTmpDocNo + "') WHERE  doc_no='" + __strTmpDocNo + "'";
                                    System.out.println(__strQUERY);
                                    PreparedStatement __stmtUpdateAmount2;
                                    __stmtUpdateAmount2 = conn.prepareStatement(__strQUERY);

                                    __stmtUpdateAmount2.executeUpdate();
                                    __stmtUpdateAmount2.close();
                                    // ### UPDATE car_code
                                    __strQUERY = "UPDATE pp_trans_detail SET car_code = (SELECT car_code FROM pp_trans WHERE pp_trans.doc_no = '" + __strTmpDocNo + "') WHERE doc_no = '" + __strTmpDocNo + "' ";
                                    System.out.println(__strQUERY);
                                    PreparedStatement __stmtUpdateCarCode2;
                                    __stmtUpdateCarCode2 = conn.prepareStatement(__strQUERY);

                                    __stmtUpdateCarCode2.executeUpdate();
                                    __stmtUpdateCarCode2.close();

                                    __strQUERY = "UPDATE pp_trans SET tms_que_shipment_code = (SELECT DISTINCT tms_que_shipment_code FROM pp_trans_detail WHERE pp_trans_detail.doc_no = '" + __strTmpDocNo + "' AND pp_trans_detail.car_code='" + __rsDataTMSQueShipment.getString("car_code") + "') WHERE doc_no = '" + __strTmpDocNo + "' AND car_code='" + __rsDataTMSQueShipment.getString("car_code") + "'";
                                    System.out.println(__strQUERY);
                                    PreparedStatement __stmtTMSQueShipmentCode2;
                                    __stmtTMSQueShipmentCode2 = conn.prepareStatement(__strQUERY);

                                    __stmtTMSQueShipmentCode2.executeUpdate();
                                    __stmtTMSQueShipmentCode2.close();
                                } else {
                                    // ### UPDATE tms_que_shipment_code
                                    __strQUERY = "UPDATE pp_trans SET tms_que_shipment_code = (SELECT DISTINCT tms_que_shipment_code FROM pp_trans_detail WHERE pp_trans_detail.doc_no = '" + __strTmpDocNo + "' AND pp_trans_detail.car_code='" + __rsDataTMSQueShipment.getString("car_code") + "') WHERE doc_no = '" + __strTmpDocNo + "' AND car_code='" + __rsDataTMSQueShipment.getString("car_code") + "'";
                                    System.out.println(__strQUERY);
                                    PreparedStatement __stmtTMSQueShipmentCode2;
                                    __stmtTMSQueShipmentCode2 = conn.prepareStatement(__strQUERY);

                                    __stmtTMSQueShipmentCode2.executeUpdate();
                                    __stmtTMSQueShipmentCode2.close();
                                }

                                __Line++;
                            }

                            __stmtCheckCountDocument.close();
                            __rsDataCheckCountDocument.close();
                        }
                    }
                }
            }

            conn.commit();
            __objTMP.put("auto_approve_status", true);
            __objTMP.put("doc_no_approved", __arrListInsertPPtrans);
            System.err.println("auto approved: process completed.");
        } else {
            __objTMP.put("auto_approve_status", false);
            conn.commit();
        }

        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _cancelVerifyDocument(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strRefCode = !param.isNull("key_id") && !param.getString("key_id").trim().isEmpty() ? param.getString("key_id") : "";

        String __strQUERY = "UPDATE pp_trans SET last_status=0 WHERE ref_code='" + __strRefCode + "' ";
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.executeUpdate();
        __stmt1.close();

        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _getReasons(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strQUERY = "SELECT roworder,code,name FROM tms_reason WHERE reason_flag=4 ORDER BY code";
        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        JSONArray __arrList = ResponseUtil.query2Array(__rsData1);
        __stmt1.close();
        __rsData1.close();

        __objTMP.put("data", __arrList);
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _deleteDocument(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strRefCode = !param.isNull("key_id") && !param.getString("key_id").trim().isEmpty() ? param.getString("key_id") : "";
        String __strActioName = !param.isNull("action_name") && !param.getString("action_name").trim().isEmpty() ? param.getString("action_name") : "";

        String __strQUERY;
        conn.setAutoCommit(false);

        // ### GET DOC_NO
        __strQUERY = "SELECT doc_no FROM pp_trans WHERE ref_code='" + __strRefCode + "' ORDER BY doc_no";
        PreparedStatement __stmtGetDocNo;
        ResultSet __rsDataGetDocNo;
        __stmtGetDocNo = conn.prepareStatement(__strQUERY);
        __rsDataGetDocNo = __stmtGetDocNo.executeQuery();
        while (__rsDataGetDocNo.next()) {
            String __strStatus;
            if (__strActioName.equals("worker")) {
                __strStatus = "9";
            } else {
                __strStatus = "10";
            }
            // ### INSERT LOG
            __strQUERY = "INSERT INTO pp_trans_log (doc_no,ref_code,trans_flag,user_code,action) VALUES ('" + __rsDataGetDocNo.getString("doc_no") + "','" + __strRefCode + "','" + "44" + "','" + __strUserCode + "','" + __strStatus + "')";
            PreparedStatement __stmtInsertLog;
            __stmtInsertLog = conn.prepareStatement(__strQUERY);
            __stmtInsertLog.executeUpdate();
            __stmtInsertLog.close();
        }
        __stmtGetDocNo.close();
        __rsDataGetDocNo.close();

        PreparedStatement __stmt1;
        __strQUERY = "DELETE FROM pp_trans WHERE ref_code='" + __strRefCode + "' ";
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.executeUpdate();
        __stmt1.close();

        PreparedStatement __stmt2;
        __strQUERY = "DELETE FROM pp_trans_detail WHERE ref_code='" + __strRefCode + "' ";
        __stmt2 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt2.executeUpdate();
        __stmt2.close();
        conn.commit();
        __objTMP.put("success", true);

        return __objTMP;
    }

}
