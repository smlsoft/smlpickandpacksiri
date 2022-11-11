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
@WebServlet(name = "report-list-1", urlPatterns = {"/report-list-1"})
public class Report1 extends HttpServlet {

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

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("branch_code") && !param.getString("branch_code").trim().isEmpty() ? " AND (UPPER(branch_code)= '" + param.getString("branch_code").toUpperCase() + "') " : "";
        __strQueryExtends += !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND (wh_code IN " + param.getString("wh_code") + ") " : "";
        __strQueryExtends += !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND (shelf_code IN " + param.getString("shelf_code") + ") " : "";

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

        String __strQUERY = "SELECT wh_code,shelf_code,ref_date,doc_date,doc_no,ic_code,qty,event_qty,(qty-event_qty)AS total_qty "
                + " ,COALESCE((SELECT name_1 FROM ic_warehouse WHERE code=wh_code), '') AS wh_name "
                + " ,COALESCE((SELECT name_1 FROM ic_shelf WHERE whcode=wh_code and code=shelf_code), '') AS sh_name "
                + " ,COALESCE((SELECT name_1 FROM ic_inventory WHERE ic_inventory.code=pp_trans_detail.ic_code), '') AS item_name "
                + " ,COALESCE((SELECT name FROM tms_reason WHERE tms_reason.code=pp_trans_detail.remark),'') AS remark "
                + " FROM pp_trans_detail " + __strQueryExtends + " AND (qty-event_qty)>0 "
                + " AND (SELECT is_confirm FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no)=1 "
                + " ORDER BY doc_date DESC,doc_no ";

        conn.setAutoCommit(false);
        __objTMP.put("row_count", __routine._getRowCount(conn, __strQUERY));

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        String __rsHTML = "";
        while (__rsData1.next()) {
            String __strWhName = __rsData1.getString("wh_name").equals("") ? "-" : __rsData1.getString("wh_name");
            String __strShelfName = __rsData1.getString("sh_name").equals("") ? "-" : __rsData1.getString("sh_name");
            String __strDocDate = __rsData1.getString("doc_date").equals("") ? "-" : __rsData1.getString("doc_date");
            String __strRefDate = __rsData1.getString("ref_date").equals("") ? "-" : __rsData1.getString("ref_date");
            String __strDocNo = __rsData1.getString("doc_no").equals("") ? "-" : __rsData1.getString("doc_no");
            String __strItemCode = __rsData1.getString("ic_code").equals("") ? "-" : __rsData1.getString("ic_code");
            String __strItemName = __rsData1.getString("item_name").equals("") ? "-" : __rsData1.getString("item_name");
            String __strQTY = __rsData1.getString("qty").equals("") ? "-" : String.format("%,.2f", Float.parseFloat(__rsData1.getString("qty")));
            String __strEventQTY = __rsData1.getString("event_qty").equals("") ? "-" : String.format("%,.2f", Float.parseFloat(__rsData1.getString("event_qty")));
            String __strTotalQTY = __rsData1.getString("total_qty").equals("") ? "-" : String.format("%,.2f", Float.parseFloat(__rsData1.getString("total_qty")));
            String __strRemark = __rsData1.getString("remark").equals("") ? "-" : __rsData1.getString("remark");

            __rsHTML += "<tr>";
            __rsHTML += "<td><h5>" + __strWhName + " ~ " + __strShelfName + "</h5></td>";
            __rsHTML += "<td><h5>" + __routine._convertDate(__strRefDate) + "</h5></td>";
            __rsHTML += "<td><h5>" + __routine._convertDate(__strDocDate) + "</h5></td>";
            __rsHTML += "<td><h5>" + __strDocNo + "</h5></td>";
            __rsHTML += "<td><h5>" + __strItemCode + "</h5></td>";
            __rsHTML += "<td><h5>" + __strItemName + "</h5></td>";
            __rsHTML += "<td><h5>" + __strQTY + "</h5></td>";
            __rsHTML += "<td><h5>" + __strEventQTY + "</h5></td>";
            __rsHTML += "<td><h5>" + __strTotalQTY + "</h5></td>";
            __rsHTML += "<td><h5>" + __strRemark + "</h5></td>";
            __rsHTML += "</tr>";
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
