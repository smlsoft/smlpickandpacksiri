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
@WebServlet(name = "permission-group-list-1", urlPatterns = {"/permission-group-list-1"})
public class Groups extends HttpServlet {

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
                case "find_group":
                    objResult = _findGroup(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "save_group":
                    objResult = _saveGroup(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "delete_group":
                    objResult = _deleteGroup(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "find_permission_group":
                    objResult = _findPermGroup(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_page_list":
                    objResult = _getPageList(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "create_permission_group":
                    objResult = _createPermGroup(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "update_permission_group":
                    objResult = _updatePermGroup(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "delete_permission_group":
                    objResult = _deletePermGroup(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "find_user_group":
                    objResult = _findUserGroup(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_user_list":
                    objResult = _getUserList(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "create_user_group":
                    objResult = _createUserGroup(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "delete_user_group":
                    objResult = _deleteUserGroup(__conn, ResponseUtil.str2Json(request.getParameter("data")));
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
        __strQueryExtends += !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? " AND (group_code='" + param.getString("group_code") + "') OR (group_name LIKE '%" + param.getString("group_code") + "%') " : "";

        __strQueryExtends = __strQueryExtends.equals("") ? "" : " WHERE 1=1 " + __strQueryExtends;

        String __strQUERY = "SELECT group_code,group_name FROM sml_group_list " + __strQueryExtends + " ORDER BY group_code ASC";
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
            __rsHTML += "<td><h5>" + __rsData1.getString("group_code") + " ~ " + __rsData1.getString("group_name") + "</h5></td>";
            __rsHTML += "<td><button type='button' id='btn-manage' class='btn btn-info btn-flat' key_id='" + __rsData1.getString("group_code") + "' key_name='" + __rsData1.getString("group_name") + "'>จัดการสิทธิ์</button></td>";
            __rsHTML += "<td><button type='button' id='btn-edit' class='btn btn-warning btn-flat' key_id='" + __rsData1.getString("group_code") + "'>แก้ไข</button></td>";
            __rsHTML += "<td><button type='button' id='btn-delete' class='btn btn-danger btn-flat' key_id='" + __rsData1.getString("group_code") + "'>ลบ</button></td>";
            __rsHTML += "</tr>";
        }

        if (__rsHTML.equals("")) {
            __rsHTML = "<tr><td colspan='4'><h5>ไม่พบข้อมูล</h5></td></tr>";
        }

        __stmt1.close();
        __rsData1.close();
        conn.commit();
        __objTMP.put("success", true);
        __objTMP.put("data", __rsHTML);

        return __objTMP;
    }

    private JSONObject _findGroup(Connection conn, JSONObject param) throws SQLException {

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? " AND (group_code='" + param.getString("group_code") + "') " : "";

        __strQueryExtends = __strQueryExtends.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtends;

        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "SELECT group_code,group_name FROM sml_group_list " + __strQueryExtends + " ORDER BY group_code ASC";
        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        __objTMP.put("data", ResponseUtil.query2Array(__rsData1));
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _saveGroup(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code").toUpperCase() : "";
        String __strGroupName = !param.isNull("group_name") && !param.getString("group_name").trim().isEmpty() ? param.getString("group_name") : "";

        String __strQUERY = "SELECT group_code FROM sml_group_list WHERE group_code='" + __strGroupCode + "' ORDER BY group_code ASC";

        conn.setAutoCommit(false);
        Integer __rowCount = __routine._getRowCount(conn, __strQUERY);
        if (__rowCount == 0) {
            __strQUERY = "INSERT INTO sml_group_list (group_code, group_name) VALUES ('" + __strGroupCode + "', '" + __strGroupName + "')";

            PreparedStatement __stmt1;
            __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            __stmt1.executeUpdate();
            __stmt1.close();
        }
        __strQUERY = "UPDATE sml_group_list SET group_name='" + __strGroupName + "' WHERE group_code='" + __strGroupCode + "' ";
        PreparedStatement __stmt2;
        __stmt2 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt2.executeUpdate();
        __stmt2.close();
        conn.commit();
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _deleteGroup(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code").toUpperCase() : "";

        String __strQUERY;

        conn.setAutoCommit(false);
        __strQUERY = "DELETE FROM sml_group_list WHERE group_code='" + __strGroupCode + "'";
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.executeUpdate();
        __stmt1.close();
        __strQUERY = "DELETE FROM sml_user_and_group WHERE group_code='" + __strGroupCode + "'";
        PreparedStatement __stmt2;
        __stmt2 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt2.executeUpdate();
        __stmt2.close();
        __strQUERY = "DELETE FROM sml_group_permission WHERE g_code='" + __strGroupCode + "'";
        PreparedStatement __stmt3;
        __stmt3 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt3.executeUpdate();
        __stmt3.close();
        conn.commit();
        __objTMP.put("success", true);

        return __objTMP;
    }

    // ##################################################################################################################################################
    private JSONObject _findPermGroup(Connection conn, JSONObject param) throws SQLException {

        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code").toUpperCase() : "";

        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "SELECT p_code "
                + " ,COALESCE(g_r_status, 'f') AS is_read "
                + " ,COALESCE(g_a_status, 'f') AS is_create "
                + " ,COALESCE(g_e_status,'f') AS is_update "
                + " ,COALESCE(g_d_status,'f') AS is_delete "
                + " ,COALESCE(g_p_status, 'f') AS is_re_print "
                + " ,COALESCE ((SELECT p_name FROM sml_web_pages AS WP WHERE WP.p_code = sml_group_permission.p_code), '') AS p_name "
                + " FROM sml_group_permission WHERE g_code = '" + __strGroupCode + "' ORDER BY p_code ASC ";

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

            __rsHTML += "<tr page_code='" + __rsData1.getString("p_code") + "' group_code='" + __strGroupCode + "' >";
            __rsHTML += "<td><h5>" + __rsData1.getString("p_name") + "</h5></td>";
            __rsHTML += "<td><h5><input type='checkbox' class='chb_r_status flat'" + is_read + "></input></h5></td>";
            __rsHTML += "<td><h5><input type='checkbox' class='chb_c_status flat'" + is_create + "></input></h5></td>";
            __rsHTML += "<td><h5><input type='checkbox' class='chb_u_status flat'" + is_update + "></input></h5></td>";
            __rsHTML += "<td><h5><input type='checkbox' class='chb_d_status flat'" + is_delete + "></input></h5></td>";
            __rsHTML += "<td><h5><input type='checkbox' class='chb_p_status flat'" + is_re_print + "></input></h5></td>";
            __rsHTML += "<td><button type='button' id='btn-remove' class='btn btn-danger btn-flat' key_id='" + __rsData1.getString("p_code") + "'>นำออก</td>";
            __rsHTML += "</tr>";
        }

        if (__rsHTML.equals("")) {
            __rsHTML = "<tr><td colspan='7'><h5>ไม่พบข้อมูล</h5></td></tr>";
        }

        __stmt1.close();
        __rsData1.close();

        __objTMP.put("success", true);
        __objTMP.put("data", __rsHTML);
        return __objTMP;
    }

    private JSONObject _getPageList(Connection conn, JSONObject param) throws SQLException {

        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code") : "";

        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "SELECT p_code,p_name FROM sml_web_pages WHERE NOT EXISTS (SELECT p_code,g_code FROM sml_group_permission WHERE sml_group_permission.p_code=sml_web_pages.p_code AND sml_group_permission.g_code='" + __strGroupCode + "')";

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        String __rsHTML = "";
        while (__rsData1.next()) {
            __rsHTML += "<tr>";
            __rsHTML += "<td><h5>" + __rsData1.getString("p_code") + " ~ " + __rsData1.getString("p_name") + "</h5></td>";
            __rsHTML += "<td><button type='button' id='btn-import' class='btn btn-success btn-flat' key_id='" + __rsData1.getString("p_code") + "'>นำเข้า</button></td>";
            __rsHTML += "</tr>";
        }

        if (__rsHTML.equals("")) {
            __rsHTML = "<tr><td colspan='2'><h5>ไม่พบข้อมูล</h5></td></tr>";
        }

        __stmt1.close();
        __rsData1.close();

        __objTMP.put("success", true);
        __objTMP.put("data", __rsHTML);
        return __objTMP;
    }

    private JSONObject _createPermGroup(Connection conn, JSONObject param) throws SQLException {

        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code") : "";
        String __strGroupName = !param.isNull("group_name") && !param.getString("group_name").trim().isEmpty() ? param.getString("group_name") : "";
        String __strPageCode = !param.isNull("page_code") && !param.getString("page_code").trim().isEmpty() ? param.getString("page_code") : "";

        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "INSERT INTO sml_group_permission (g_code,g_name,p_code,g_r_status,g_a_status,g_e_status,g_d_status,g_p_status) VALUES ('" + __strGroupCode + "', '" + __strGroupName + "', '" + __strPageCode + "', 'false','false','false','false','false')";

        conn.setAutoCommit(false);
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.executeUpdate();
        __stmt1.close();
        conn.commit();

        __objTMP.put("success", true);
        return __objTMP;
    }

    private JSONObject _updatePermGroup(Connection conn, JSONObject param) throws SQLException {

        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code") : "";
        String __strPageCode = !param.isNull("page_code") && !param.getString("page_code").trim().isEmpty() ? param.getString("page_code") : "";
        String __strStatus = !param.isNull("status") ? String.valueOf(param.getBoolean("status")) : "false";
        String __strUpdateKey = !param.isNull("update_key") && !param.getString("update_key").trim().isEmpty() ? param.getString("update_key") : "";

        switch (__strUpdateKey) {
            case "read":
                __strStatus = " g_r_status='" + __strStatus + "' ";
                break;
            case "create":
                __strStatus = " g_a_status='" + __strStatus + "' ";
                break;
            case "update":
                __strStatus = " g_e_status='" + __strStatus + "' ";
                break;
            case "delete":
                __strStatus = " g_d_status='" + __strStatus + "' ";
                break;
            case "print":
                __strStatus = " g_p_status='" + __strStatus + "' ";
                break;
        }

        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "UPDATE sml_group_permission SET " + __strStatus + " WHERE g_code='" + __strGroupCode + "' AND p_code='" + __strPageCode + "' ";

        conn.setAutoCommit(false);
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.executeUpdate();
        __stmt1.close();
        conn.commit();

        __objTMP.put("success", true);
        return __objTMP;
    }

    private JSONObject _deletePermGroup(Connection conn, JSONObject param) throws SQLException {

        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code") : "";
        String __strPageCode = !param.isNull("page_code") && !param.getString("page_code").trim().isEmpty() ? param.getString("page_code") : "";

        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "DELETE FROM sml_group_permission WHERE g_code='" + __strGroupCode + "' AND p_code='" + __strPageCode + "' ";

        conn.setAutoCommit(false);
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.executeUpdate();
        __stmt1.close();
        conn.commit();

        __objTMP.put("success", true);
        return __objTMP;
    }

    // ##################################################################################################################################################
    private JSONObject _findUserGroup(Connection conn, JSONObject param) throws SQLException {
        Integer __strOffset = !param.isNull("offset") && param.getInt("offset") > 0 ? param.getInt("offset") : 0;
        Integer __strLimit = !param.isNull("limit") && param.getInt("limit") > 0 ? param.getInt("limit") : 0;

        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code").toUpperCase() : "";

        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "SELECT user_code,group_code,COALESCE((SELECT user_name FROM sml_user_list WHERE UPPER(sml_user_list.user_code)=sml_user_and_group.user_code), '') AS user_name FROM sml_user_and_group WHERE group_code='" + __strGroupCode + "' ORDER BY user_code ASC";
        String __strQueryPagination = " LIMIT " + __strLimit + " OFFSET " + __strOffset;

        conn.setAutoCommit(false);
        __objTMP.put("row_count", __routine._getRowCount(conn, __strQUERY));
        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY + __strQueryPagination, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        String __rsHTML = "";
        while (__rsData1.next()) {
            __rsHTML += "<tr group_code='" + __rsData1.getString("group_code") + "' user_code='" + __rsData1.getString("user_code") + "'>";
            __rsHTML += "<td><h5>" + __rsData1.getString("user_code") + " ~ " + __rsData1.getString("user_name") + "</h5></td>";
            __rsHTML += "<td><button type='button' id='btn-remove' class='btn btn-danger btn-flat' key_id='" + __rsData1.getString("user_code") + "'>นำออก</td>";
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

    private JSONObject _getUserList(Connection conn, JSONObject param) throws SQLException {
        Integer __strOffset = !param.isNull("offset") && param.getInt("offset") > 0 ? param.getInt("offset") : 0;
        Integer __strLimit = !param.isNull("limit") && param.getInt("limit") > 0 ? param.getInt("limit") : 0;

        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code") : "";

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("search_value") && !param.getString("search_value").trim().isEmpty() ? " AND ((UPPER(sml_user_list.user_code)='" + param.getString("search_value") + "') OR (sml_user_list.user_name LIKE '%" + param.getString("search_value") + "%')) " : "";

        __strQueryExtends = __strQueryExtends.equals("") ? "" : __strQueryExtends;

        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "SELECT user_code,user_name FROM sml_user_list WHERE NOT EXISTS (SELECT user_code,group_code FROM sml_user_and_group WHERE UPPER(sml_user_and_group.user_code)=UPPER(sml_user_list.user_code) AND sml_user_and_group.group_code='" + __strGroupCode + "') " + __strQueryExtends + " ORDER BY sml_user_list.user_code";
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
            __rsHTML += "<td><h5>" + __rsData1.getString("user_code") + " ~ " + __rsData1.getString("user_name") + "</h5></td>";
            __rsHTML += "<td><button type='button' id='btn-import' class='btn btn-success btn-flat' key_id='" + __rsData1.getString("user_code") + "'>นำเข้า</button></td>";
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

    private JSONObject _createUserGroup(Connection conn, JSONObject param) throws SQLException {

        String __strKeyID = !param.isNull("user_code") && !param.getString("user_code").trim().isEmpty() ? param.getString("user_code") : "";
        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code") : "";

        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "INSERT INTO sml_user_and_group (user_code, group_code) VALUES ('" + __strKeyID.toUpperCase() + "', '" + __strGroupCode.toUpperCase() + "')";

        conn.setAutoCommit(false);
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.executeUpdate();
        __stmt1.close();
        conn.commit();

        __objTMP.put("success", true);
        return __objTMP;
    }

    private JSONObject _deleteUserGroup(Connection conn, JSONObject param) throws SQLException {

        String __strKeyID = !param.isNull("user_code") && !param.getString("user_code").trim().isEmpty() ? param.getString("user_code") : "";
        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code") : "";

        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "DELETE FROM sml_user_and_group WHERE user_code = '" + __strKeyID.toUpperCase() + "' AND group_code = '" + __strGroupCode.toUpperCase() + "'";

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
