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
@WebServlet(name = "report-list-3", urlPatterns = {"/report-list-3"})
public class Report3 extends HttpServlet {

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
                case "get_branch_list":
                    objResult = _getBranchList(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_department_list":
                    objResult = _getDepartmentList(__conn, ResponseUtil.str2Json(request.getParameter("data")));
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
        conn.setAutoCommit(false);
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("branch_code") && !param.getString("branch_code").trim().isEmpty() ? " AND (UPPER(branch_code)= '" + param.getString("branch_code").toUpperCase() + "') " : "";
        __strQueryExtends += !param.isNull("department_code") && !param.getString("department_code").trim().isEmpty() ? " AND (UPPER(department_code)= '" + param.getString("department_code").toUpperCase() + "') " : "";

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
        String __strQUERY;

        __strQUERY = "SELECT * FROM tms_config_date WHERE date_flag=2";
        PreparedStatement __stmtGetTMSConfigDate;
        ResultSet __rsDataTMSConfigDate;
        __stmtGetTMSConfigDate = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsDataTMSConfigDate = __stmtGetTMSConfigDate.executeQuery();

        String __strBeginDate = "";
        while (__rsDataTMSConfigDate.next()) {
            __strBeginDate = __rsDataTMSConfigDate.getString("begin_date").equals("") ? "" : __rsDataTMSConfigDate.getString("begin_date");
        }

        __stmtGetTMSConfigDate.close();
        __rsDataTMSConfigDate.close();

        __strQUERY = "SELECT * FROM ("
                + " SELECT branch_code ,ref_code ,qty,unit_code "
                + " ,(SELECT department_code FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no) AS department_code "
                + " ,(SELECT name_1 FROM erp_department_list WHERE code= (SELECT department_code FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no)) AS department_name "
                + " ,(SELECT sale_code FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no) AS sale_code "
                + " ,(SELECT name_1 FROM erp_user WHERE code = (SELECT sale_code FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no)) AS sale_name "
                + " ,ic_code,(SELECT name_1 FROM ic_inventory WHERE ic_inventory.code=pp_trans_detail.ic_code) AS item_name "
                + " ,(SELECT name_1 FROM ic_unit WHERE code=unit_code)AS unit_name "
                + " ,(SELECT doc_date FROM ic_trans WHERE ic_trans.doc_no=pp_trans_detail.ref_code AND trans_flag=34) AS rev_date "
                + " ,(SELECT distinct doc_date "
                + " FROM ic_trans_detail WHERE trans_flag in (12, 310) "
                + " AND (SELECT ref_doc_no FROM ic_trans_detail as po WHERE po.trans_flag = 6 AND po.doc_no = ic_trans_detail.ref_doc_no AND po.line_number = ic_trans_detail.ref_row)=pp_trans_detail.ref_code AND doc_date >= '" + __strBeginDate + "' LIMIT 1)AS rec_date "
                + " ,array_to_string(array((SELECT distinct doc_no "
                + " FROM ic_trans_detail WHERE trans_flag in (12, 310) "
                + " AND (SELECT ref_doc_no FROM ic_trans_detail as po WHERE po.trans_flag = 6 AND po.doc_no = ic_trans_detail.ref_doc_no AND po.line_number = ic_trans_detail.ref_row)=pp_trans_detail.ref_code AND doc_date >= '" + __strBeginDate + "' )),',')AS rec_code "
                + " ,doc_date,doc_no,to_char((now()-(SELECT distinct doc_date "
                + " FROM ic_trans_detail WHERE trans_flag in (12, 310) "
                + " AND (SELECT ref_doc_no FROM ic_trans_detail as po WHERE po.trans_flag = 6 AND po.doc_no = ic_trans_detail.ref_doc_no AND po.line_number = ic_trans_detail.ref_row)=pp_trans_detail.ref_code AND doc_date >= '" + __strBeginDate + "' LIMIT 1)),'DD-HH24-MI')AS out_day "
                + " ,(qty-event_qty) AS total_qty "
                + " FROM pp_trans_detail WHERE doc_no in ((SELECT doc_no FROM pp_trans WHERE trans_flag=34 AND is_close!=1 order by doc_date desc,doc_no)))AS res " + __strQueryExtends + " AND rec_code != '' ";

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        String __rsHTML = "";
        while (__rsData1.next()) {
            String __strDepartmentCode = __rsData1.getString("department_code").equals("") ? "-" : __rsData1.getString("department_code");
            String __strDepartmentName = __rsData1.getString("department_name").equals("") ? "-" : __rsData1.getString("department_name");
            String __strSaleCode = __rsData1.getString("sale_code").equals("") ? "-" : __rsData1.getString("sale_code");
            String __strSaleName = __rsData1.getString("sale_name").equals("") ? "-" : __rsData1.getString("sale_name");
            String __strItemCode = __rsData1.getString("ic_code").equals("") ? "-" : __rsData1.getString("ic_code");
            String __strItemName = __rsData1.getString("item_name").equals("") ? "-" : __rsData1.getString("item_name");
            String __strQTY = __rsData1.getString("qty").equals("") ? "-" : String.format("%,.2f", Float.parseFloat(__rsData1.getString("qty")));
            String __strUnitCode = __rsData1.getString("unit_code").equals("") ? "-" : __rsData1.getString("unit_code");
            String __strUnitName = __rsData1.getString("unit_name").equals("") ? "-" : __rsData1.getString("unit_name");
            String __strRefDate = __rsData1.getString("rev_date").equals("") ? "-" : __rsData1.getString("rev_date");
            String __strRefCode = __rsData1.getString("ref_code").equals("") ? "-" : __rsData1.getString("ref_code");
            String __strReCDate = __rsData1.getString("rec_date").equals("") ? "-" : __rsData1.getString("rec_date");
            String __strReCCode = __rsData1.getString("rec_code").equals("") ? "-" : __rsData1.getString("rec_code");
            String __strDocDate = __rsData1.getString("doc_date").equals("") ? "-" : __rsData1.getString("doc_date");
            String __strDocNo = __rsData1.getString("doc_no").equals("") ? "-" : __rsData1.getString("doc_no");
            String __strOutDay = __rsData1.getString("out_day").equals("") ? "-" : __rsData1.getString("out_day");
            String __strTotalQTY = __rsData1.getString("total_qty").equals("") ? "-" : __rsData1.getString("total_qty");

            __rsHTML += "<tr>";
            __rsHTML += "<td><h5>" + __strDepartmentCode + " ~ " + __strDepartmentName + "</h5></td>";
            __rsHTML += "<td><h5>" + __strSaleCode + " ~ " + __strSaleName + "</h5></td>";
            __rsHTML += "<td><h5>" + __strItemCode + "</h5></td>";
            __rsHTML += "<td><h5>" + __strItemName + "</h5></td>";
            __rsHTML += "<td><h5>" + __strQTY + "</h5></td>";
            __rsHTML += "<td><h5>" + __strUnitCode + " ~ " + __strUnitName + "</h5></td>";
            __rsHTML += "<td><h5>" + __routine._convertDate(__strRefDate) + "</h5></td>";
            __rsHTML += "<td><h5>" + __strRefCode + "</h5></td>";
            __rsHTML += "<td><h5>" + __routine._convertDate(__strReCDate) + "</h5></td>";
            __rsHTML += "<td><h5>" + __strReCCode + "</h5></td>";
            __rsHTML += "<td><h5>" + __routine._convertDate(__strDocDate) + "</h5></td>";
            __rsHTML += "<td><h5>" + __strDocNo + "</h5></td>";
            __rsHTML += "<td><h5>" + __routine._convertTime(__strOutDay) + "</h5></td>";
            __rsHTML += "<td><h5>" + __strTotalQTY + "</h5></td>";

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

    private JSONObject _getDepartmentList(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "SELECT code, name_1 FROM erp_department_list ORDER BY code ASC";

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
