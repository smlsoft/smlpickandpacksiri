/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configs;

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
@WebServlet(name = "approved-date-list-1", urlPatterns = {"/approved-date-list-1"})
public class ApprovedDate extends HttpServlet {

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
                case "find_begin_date":
                    objResult = _findTMSConfigDate(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "save_begin_date":
                    objResult = _saveTMSConfigDate(__conn, ResponseUtil.str2Json(request.getParameter("data")));
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

        String __strQUERY = "SELECT roworder,to_char(begin_date, 'YYYY-MM-DD') AS begin_date FROM tms_config_date WHERE date_flag=2 ORDER BY roworder";

        conn.setAutoCommit(false);
        __objTMP.put("row_count", __routine._getRowCount(conn, __strQUERY));
        String __strQueryPagination = " LIMIT " + __strLimit + " OFFSET " + __strOffset;
        PreparedStatement __stmt1;

        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY + __strQueryPagination, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        String __rsHTML = "";
        while (__rsData1.next()) {
            __rsHTML += "<tr>";
            __rsHTML += "<td><h5>" + __routine._convertDate(__rsData1.getString("begin_date")) + "</h5></td>";
            __rsHTML += "<td><button type='button' id='btn-edit' class='btn btn-warning btn-flat' key_id='" + __rsData1.getString("roworder") + "'>แก้ไข</button></td>";
            __rsHTML += "</tr>";
        }

        if (__rsHTML.equals("")) {
            __rsHTML = "<tr><td colspan='2'><h5>ไม่พบข้อมูล</h5></td></tr>";
        }

        __stmt1.close();
        __rsData1.close();
        conn.commit();
        __objTMP.put("success", true);
        __objTMP.put("data", __rsHTML);

        return __objTMP;
    }

    private JSONObject _findTMSConfigDate(Connection conn, JSONObject param) throws SQLException {

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("key_id") && !param.getString("key_id").trim().isEmpty() ? " AND (roworder='" + param.getString("key_id") + "') " : "";

        __strQueryExtends = __strQueryExtends.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtends;

        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "SELECT roworder,to_char(begin_date, 'DD-MM-YYYY') AS begin_date FROM tms_config_date " + __strQueryExtends + " AND date_flag=2 ORDER BY roworder";
        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        __objTMP.put("data", ResponseUtil.query2Array(__rsData1));
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _saveTMSConfigDate(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strRowOrder = !param.isNull("key_id") && !param.getString("key_id").trim().isEmpty() ? param.getString("key_id") : "";
        String __strBeginDate = !param.isNull("begin_date") && !param.getString("begin_date").trim().isEmpty() ? param.getString("begin_date") : "";

        String __strQUERY = "SELECT roworder FROM tms_config_date WHERE roworder='" + __strRowOrder + "' AND date_flag=2 ";

        conn.setAutoCommit(false);
        Integer __rowCount = __routine._getRowCount(conn, __strQUERY);
        if (__rowCount > 0) {
            __strQUERY = "UPDATE tms_config_date SET code='pick_and_pack',begin_date='" + __strBeginDate + "' WHERE roworder='" + __strRowOrder + "' AND date_flag=2 ";
            PreparedStatement __stmt1;
            __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            __stmt1.executeUpdate();
            __stmt1.close();
        } else {
            __strQUERY = "INSERT INTO tms_config_date (code, begin_date, date_flag) VALUES ('pick_and_pack', '" + __strBeginDate + "', 2)";
            PreparedStatement __stmt1;
            __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            __stmt1.executeUpdate();
            __stmt1.close();
        }
        conn.commit();
        __objTMP.put("success", true);

        return __objTMP;
    }

}
