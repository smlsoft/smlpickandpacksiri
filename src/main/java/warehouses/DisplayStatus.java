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
@WebServlet(name = "display-status-list-1", urlPatterns = {"/display-status-list-1"})
public class DisplayStatus extends HttpServlet {

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

        String __strWhCode = !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND wh_code IN " + param.getString("wh_code") : "";
        String __strShelfCode = !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND shelf_code IN " + param.getString("shelf_code") : "";
        Integer __strOffset = !param.isNull("offset") && param.getInt("offset") > 0 ? param.getInt("offset") : 0;
        Integer __strLimit = !param.isNull("limit") && param.getInt("limit") > 0 ? param.getInt("limit") : 0;

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? " AND ref_code='" + param.getString("ref_code") + "' " : "";
        __strQueryExtends += !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND wh_code IN " + param.getString("wh_code") : "";
        __strQueryExtends += !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND shelf_code IN " + param.getString("shelf_code") : "";

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
                + " SELECT DISTINCT ref_code,doc_date,to_char(create_date_time_now, 'DD/MM/YYYY HH24:MI')AS create_datetime,cust_code "
                + " ,COALESCE((SELECT name_1 FROM ar_customer WHERE ar_customer.code=pp_trans.cust_code),'')AS cust_name "
                + " ,COALESCE((SELECT car_code FROM tms_que_shipment WHERE tms_que_shipment.doc_no = COALESCE((SELECT doc_no FROM tms_que_shipment_details WHERE tms_que_shipment_details.ref_doc_no = pp_trans.ref_code LIMIT 1),'') LIMIT 1),'') AS car_code "
                + " FROM pp_trans " + __strQueryExtends
                + " AND is_close=0 "
                + " AND trans_flag in (36,44,34)) AS pp_trans ORDER BY create_datetime";

        String __strQueryPagination = " LIMIT " + __strLimit + " OFFSET " + __strOffset;

        conn.setAutoCommit(false);

        __objTMP.put("row_count", __routine._getRowCount(conn, __strQUERY));

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY + __strQueryPagination, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        String __rsHTML = "";
        while (__rsData1.next()) {
            __rsHTML += "<tr>";
            __rsHTML += "<td><h5>" + __rsData1.getString("cust_name") + "</h5></td>";
            __rsHTML += "<td><h5>" + __rsData1.getString("ref_code") + "</h5></td>";

            __strQUERY = "SELECT doc_no,is_confirm,status,age(confirm_date_time,create_date_time_now) "
                    + " ,COALESCE((SELECT wh_code FROM pp_trans_detail WHERE pp_trans_detail.doc_no = pp_trans.doc_no limit 1), '') AS wh_code "
                    + " ,COALESCE((SELECT name_1 FROM ic_warehouse WHERE ic_warehouse.code = (SELECT wh_code from pp_trans_detail WHERE pp_trans_detail.doc_no = pp_trans.doc_no limit 1)),'') AS wh_name "
                    + " FROM pp_trans WHERE ref_code = '" + __rsData1.getString("ref_code") + "' " + __strWhCode + __strShelfCode + " ORDER BY wh_code";

            PreparedStatement __stmt2;
            ResultSet __rsData2;
            __stmt2 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            __rsData2 = __stmt2.executeQuery();

            Integer __status = 0;
            Integer __isConfirm = 1;
            while (__rsData2.next()) {
                __status = __rsData2.getInt("status") > 0 ? 2 : 0;
                __isConfirm = __rsData2.getInt("is_confirm") == 0 ? 0 : 1;
            }

            __stmt2.close();
            __rsData2.close();

            switch (__isConfirm) {
                case 0:
                    if (__status > 0) {
                        __rsHTML += "<td style='background-color: #F5B041; color: #FFF;'><h5>กำลังจัด</h5></td>";
                    } else {
                        __rsHTML += "<td style='background-color: #99A3A4; color: #FFF;'><h5>รอจัด</h5></td>";
                    }
                    break;
                case 1:
                    __rsHTML += "<td style='background-color: #58D68D; color: #FFF;'><h5>จัดเสร็จ</h5></td>";
                    break;
            }
            __rsHTML += "</tr>";
        }

        if (__rsHTML.equals("")) {
            __rsHTML = "<tr><td colspan='12'><h5>ไม่พบข้อมูล</h5></td></tr>";
        }

        __stmt1.close();
        __rsData1.close();
        conn.commit();

        __objTMP.put("success", true);
        __objTMP.put("data", __rsHTML);

        return __objTMP;
    }

}
