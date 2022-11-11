/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package permissions;

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
@WebServlet(name = "permission-user-list-1", urlPatterns = {"/permission-user-list-1"})
public class Users extends HttpServlet {

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
            __conn = __routine._connect("smlerpmain" + __strProviderCode, _global.FILE_CONFIG(__strProviderCode));
            switch (__strActionName) {
                case "get_main_detail":
                    objResult = _getMainDetail(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "find_permission_user":
                    objResult = _findUserPerm(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "update_permission_user":
                    objResult = _saveUserPerm(__conn, ResponseUtil.str2Json(request.getParameter("data")));
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
        __strQueryExtends += !param.isNull("user_code") && !param.getString("user_code").trim().isEmpty() ? " AND (UPPER(user_code)='" + param.getString("user_code") + "')  OR (user_name LIKE '%" + param.getString("user_code") + "%') " : "";
        __strQueryExtends += !this.__strUserCode.equals("") ? " AND UPPER(user_code) <> '" + this.__strUserCode + "' " : "";

        __strQueryExtends = __strQueryExtends.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtends;

        String __strQUERY = "SELECT user_code, user_name FROM sml_user_list " + __strQueryExtends + " ORDER BY user_code ASC";

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
            __rsHTML += "<td><h5>" + __rsData1.getString("user_code") + "</h5></td>";
            __rsHTML += "<td><h5>" + __rsData1.getString("user_name") + "</h5></td>";
            __rsHTML += "<td><button type='button' id='btn-edit' class='btn btn-warning btn-flat' key_id='" + __rsData1.getString("user_code") + "'>จัดการสิทธิ์</button></td>";
            __rsHTML += "</tr>";
        }

        if (__rsHTML.equals("")) {
            __rsHTML = "<tr><td colspan='3'><h5>ไม่พบข้อมูล</h5></td></tr>";
        }

        __stmt1.close();
        __rsData1.close();
        conn.commit();
        __objTMP.put("success", true);
        __objTMP.put("data", __rsHTML);

        return __objTMP;
    }

    private JSONObject _findUserPerm(Connection conn, JSONObject param) throws SQLException {

        String __strKeyID = !param.isNull("user_code") && !param.getString("user_code").trim().isEmpty() ? param.getString("user_code").toUpperCase() : "";

        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "SELECT WP.p_code, WP.p_name , "
                + " COALESCE ((SELECT is_read FROM sml_user_permission AS UP WHERE WP.p_code=UP.p_code AND UPPER(UP.user_code)='" + __strKeyID + "'), 'f') AS is_read, "
                + " COALESCE ((SELECT is_create FROM sml_user_permission AS UP WHERE WP.p_code=UP.p_code AND UPPER(UP.user_code)='" + __strKeyID + "'), 'f') AS is_create, "
                + " COALESCE ((SELECT is_update FROM sml_user_permission AS UP WHERE WP.p_code=UP.p_code AND UPPER(UP.user_code)='" + __strKeyID + "'), 'f') AS is_update, "
                + " COALESCE ((SELECT is_delete FROM sml_user_permission AS UP WHERE WP.p_code=UP.p_code AND UPPER(UP.user_code)='" + __strKeyID + "'), 'f') AS is_delete, "
                + " COALESCE ((SELECT is_re_print FROM sml_user_permission AS UP WHERE WP.p_code=UP.p_code AND UPPER(UP.user_code)='" + __strKeyID + "'), 'f') AS is_re_print, "
                + " COALESCE ((SELECT roworder FROM sml_user_permission AS UP WHERE WP.p_code=UP.p_code AND UPPER(UP.user_code)='" + __strKeyID + "'), 0) AS roworder "
                + " FROM sml_web_pages AS WP ORDER BY WP.p_code ASC";

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        String __rsHTML = "";
        while (__rsData1.next()) {
            String is_read = __rsData1.getBoolean("is_read") ? "checked" : "";
            String is_create = __rsData1.getBoolean("is_create") ? "checked" : "";
            String is_update = __rsData1.getBoolean("is_update") ? "checked" : "";
            String is_delete = __rsData1.getBoolean("is_delete") ? "checked" : "";
            String is_re_print = __rsData1.getBoolean("is_re_print") ? "checked" : "";

            __rsHTML += "<tr page_code='" + __rsData1.getString("p_code") + "' user_code='" + __strKeyID + "' >";
            __rsHTML += "<td><h5>" + __rsData1.getString("p_name") + "</h5></td>";
            __rsHTML += "<td><h5><input type='checkbox' class='chb_r_status flat'" + is_read + "></h5></td>";
            __rsHTML += "<td><h5><input type='checkbox' class='chb_c_status flat'" + is_create + "></h5></td>";
            __rsHTML += "<td><h5><input type='checkbox' class='chb_u_status flat'" + is_update + "></h5></td>";
            __rsHTML += "<td><h5><input type='checkbox' class='chb_d_status flat'" + is_delete + "></h5></td>";
            __rsHTML += "<td><h5><input type='checkbox' class='chb_p_status flat'" + is_re_print + "></h5></td>";
            __rsHTML += "</tr>";
        }

        if (__rsHTML.equals("")) {
            __rsHTML = "<tr><td colspan='6'><h5>ไม่พบข้อมูล</h5></td></tr>";
        }

        __stmt1.close();
        __rsData1.close();

        __objTMP.put("success", true);
        __objTMP.put("data", __rsHTML);
        return __objTMP;
    }

    private JSONObject _saveUserPerm(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strKeyID = !param.isNull("user_code") && !param.getString("user_code").trim().isEmpty() ? param.getString("user_code").toUpperCase() : "";
        String __strPageCode = !param.isNull("page_code") && !param.getString("page_code").trim().isEmpty() ? param.getString("page_code") : "";
        String __strStatus = !param.isNull("status") ? String.valueOf(param.getBoolean("status")) : "false";
        String __strUpdateKey = !param.isNull("update_key") && !param.getString("update_key").trim().isEmpty() ? param.getString("update_key") : "";

        switch (__strUpdateKey) {
            case "read":
                __strStatus = " is_read='" + __strStatus + "' ";
                break;
            case "create":
                __strStatus = " is_create='" + __strStatus + "' ";
                break;
            case "update":
                __strStatus = " is_update='" + __strStatus + "' ";
                break;
            case "delete":
                __strStatus = " is_delete='" + __strStatus + "' ";
                break;
            case "print":
                __strStatus = " is_re_print='" + __strStatus + "' ";
                break;
        }

        String __strQUERY = "SELECT user_code FROM sml_user_permission WHERE user_code='" + __strKeyID + "' AND p_code='" + __strPageCode + "' ORDER BY user_code ASC ";

        conn.setAutoCommit(false);
        Integer __rowCount = __routine._getRowCount(conn, __strQUERY);
        if (__rowCount == 0) {
            __strQUERY = "INSERT INTO sml_user_permission (user_code, p_code, is_read, is_create, is_update, is_delete, is_re_print) VALUES ('" + __strKeyID + "', '" + __strPageCode + "', 'false', 'false', 'false', 'false', 'false')";

            PreparedStatement __stmt1;
            __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            __stmt1.executeUpdate();
            __stmt1.close();
        }
        __strQUERY = "UPDATE sml_user_permission SET " + __strStatus + " WHERE user_code='" + __strKeyID + "' AND p_code='" + __strPageCode + "' ";
        PreparedStatement __stmt2;
        __stmt2 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt2.executeUpdate();
        __stmt2.close();
        conn.commit();
        __objTMP.put("success", true);

        return __objTMP;
    }

}
