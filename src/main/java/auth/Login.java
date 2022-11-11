/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auth;

import java.awt.List;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

/**
 *
 * @author BeamMary
 */
@WebServlet(name = "authentication-list-1", urlPatterns = {"/authentication-list-1"})
public class Login extends HttpServlet {

    private final _routine __routine = new _routine();
    private HttpServletRequest __request;

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

        String __strActionName = "";
        if (request.getParameter("action_name") != null && !request.getParameter("action_name").isEmpty()) {
            __strActionName = request.getParameter("action_name");
        }

        Connection __conn = null;
        this.__request = request;
        try {
            switch (__strActionName) {
                case "authentication":
                    objResult = _authentication(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_branch_list":
                    objResult = _getBrachList(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "find_tms_config_date":
                    objResult = _findTMSConfigDate(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "find_system_id":
                    objResult = _findSystemID(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_group_location":
                    objResult = _getGroupLocation(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_warehouse":
                    objResult = _getWareHouse(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_shelf_code":
                    objResult = _getShelfCode(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "save_group_location":
                    objResult = _saveGroupLocation(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "save_tms_config_date":
                    objResult = _saveTMSConfigDate(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "save_group_system":
                    objResult = _saveGroupSystem(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                default:
                    objResult.put("err_title", "ข้อความระบบ");
                    objResult.put("err_msg", "ไม่พบรายการ ที่ต้องการทำ");
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

    private JSONObject _authentication(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strProviderCode = !param.isNull("provider_code") && !param.getString("provider_code").trim().isEmpty() ? param.getString("provider_code") : "";
        String __strUserCode = !param.isNull("user_code") && !param.getString("user_code").trim().isEmpty() ? param.getString("user_code").toUpperCase() : "";
        String __strUserPassword = !param.isNull("user_password") && !param.getString("user_password").trim().isEmpty() ? param.getString("user_password") : "";
        String __strDatabaseName = "smlerpmain" + __strProviderCode.toLowerCase();

        conn = this.__routine._connect(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode.toUpperCase()));

        String __strQUERY = "SELECT user_code, user_name FROM sml_user_list WHERE UPPER(user_code)= ? AND user_password= ? ";

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.setString(1, __strUserCode);
        __stmt1.setString(2, __strUserPassword);
        __rsData1 = __stmt1.executeQuery();

        Boolean is_authen = false;
        while (__rsData1.next()) {
            __objTMP.put("provider_code", __strProviderCode);
            __objTMP.put("user_code", __strUserCode);
            __objTMP.put("user_name", __rsData1.getString("user_name"));

            is_authen = true;
        }
        __stmt1.close();
        __rsData1.close();

        if (is_authen) {

            HttpSession __session = this.__request.getSession();
            __session.setAttribute("tmp_provider_code", __strProviderCode);
            __session.setAttribute("tmp_user_code", __strUserCode);

            __strQUERY = "SELECT data_code FROM sml_database_list WHERE UPPER(data_code) IN (SELECT UPPER(data_code) FROM sml_database_list_user_and_group WHERE user_or_group_status=0 AND UPPER(user_or_group_code)= ?) OR UPPER(data_code) IN (SELECT UPPER(data_code) FROM sml_database_list_user_and_group WHERE user_or_group_status=1 AND UPPER(user_or_group_code) IN (SELECT UPPER(group_code) FROM sml_user_and_group WHERE UPPER(user_code)=?)) ORDER BY data_name";
            PreparedStatement __stmt2;
            __stmt2 = conn.prepareStatement(__strQUERY);
            __stmt2.setString(1, __strUserCode);
            __stmt2.setString(2, __strUserCode);
            ResultSet __rsData2;
            __rsData2 = __stmt2.executeQuery();

            String __rsHTML = "";
            while (__rsData2.next()) {
                try {
                    String __strDatabaseCode = __rsData2.getString("data_code").toLowerCase();
                    conn = this.__routine._connect(__strDatabaseCode, _global.FILE_CONFIG(__strProviderCode.toUpperCase()));
                    __strQUERY = "SELECT company_name_1 FROM erp_company_profile LIMIT 1";
                    PreparedStatement __stmt3;
                    ResultSet __rsData3;
                    __stmt3 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    __rsData3 = __stmt3.executeQuery();
                    while (__rsData3.next()) {
                        __rsHTML += "<tr key_id='" + __strDatabaseCode + "' style='cursor: pointer;'>";
                        __rsHTML += "<td><h5>" + __strDatabaseCode + "</h5></td>";
                        __rsHTML += "<td><h5>" + __rsData3.getString("company_name_1") + "</h5></td>";
                        __rsHTML += "</tr>";
                    }
                    __stmt3.close();
                    __rsData3.close();
                } catch (Exception ex) {

                }
            }
            __stmt2.close();
            __rsData2.close();

            __objTMP.put("is_authen", is_authen);
            __objTMP.put("data", __rsHTML);
        }
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _getBrachList(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strDatabaseName = !param.isNull("database_name") && !param.getString("database_name").trim().isEmpty() ? param.getString("database_name") : "";
        String __strProviderCode = !param.isNull("provider_code") && !param.getString("provider_code").trim().isEmpty() ? param.getString("provider_code") : "";
        String __strUserCode = !param.isNull("user_code") && !param.getString("user_code").trim().isEmpty() ? param.getString("user_code").toUpperCase() : "";

        conn = this.__routine._connect(__strDatabaseName.toLowerCase(), _global.FILE_CONFIG(__strProviderCode.toUpperCase()));

        String __strQUERY = "SELECT change_branch_code FROM erp_user WHERE UPPER(code)= ?";

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.setString(1, __strUserCode);
        __rsData1 = __stmt1.executeQuery();

        Integer __changeBranchCode = -1;
        while (__rsData1.next()) {
            __objTMP.put("change_branch_code", __rsData1.getInt("change_branch_code"));
            __changeBranchCode = __rsData1.getInt("change_branch_code");
        }
        __stmt1.close();
        __rsData1.close();

        switch (__changeBranchCode) {
            case 0:
                __strQUERY = "SELECT branch_code,COALESCE((SELECT name_1 FROM erp_branch_list WHERE erp_branch_list.code = branch_code),'') AS branch_name FROM erp_user WHERE code=UPPER('" + __strUserCode + "')";
                break;
            case 1:
                __strQUERY = "SELECT code, name_1, address_1 FROM erp_branch_list order by code";
                break;
        }

        PreparedStatement __stmt2;
        ResultSet __rsData2;
        __stmt2 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData2 = __stmt2.executeQuery();

        String __rsHTML = "";
        while (__rsData2.next()) {
            switch (__changeBranchCode) {
                case 0:
                    __objTMP.put("branch_code", __rsData2.getString("branch_code"));
                    __objTMP.put("branch_name", __rsData2.getString("branch_name"));
                    break;
                case 1:
                    __rsHTML += "<tr key_id='" + __rsData2.getString("code") + "' key_name='" + __rsData2.getString("name_1") + "' style='cursor: pointer;'>";
                    __rsHTML += "<td><h5>" + __rsData2.getString("code") + "</h5></td>";
                    __rsHTML += "<td><h5>" + __rsData2.getString("name_1") + "</h5></td>";
                    __rsHTML += "</tr>";
                    break;
            }
        }
        __stmt2.close();
        __rsData2.close();

        __objTMP.put("success", true);
        __objTMP.put("data", __rsHTML);

        return __objTMP;
    }

    private JSONObject _getGroupLocation(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strDatabaseName = !param.isNull("database_name") && !param.getString("database_name").trim().isEmpty() ? param.getString("database_name") : "";
        String __strProviderCode = !param.isNull("provider_code") && !param.getString("provider_code").trim().isEmpty() ? param.getString("provider_code") : "";

        conn = this.__routine._connect(__strDatabaseName.toLowerCase(), _global.FILE_CONFIG(__strProviderCode.toUpperCase()));

        String __strQUERY = "SELECT group_code, name FROM sml_group_location ORDER BY group_code ASC ";

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        JSONArray __arrList = ResponseUtil.query2Array(__rsData1);

        __objTMP.put("success", true);
        __objTMP.put("data", __arrList);

        return __objTMP;
    }

    private JSONObject _getWareHouse(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strDatabaseName = !param.isNull("database_name") && !param.getString("database_name").trim().isEmpty() ? param.getString("database_name") : "";
        String __strProviderCode = !param.isNull("provider_code") && !param.getString("provider_code").trim().isEmpty() ? param.getString("provider_code") : "";

        conn = this.__routine._connect(__strDatabaseName.toLowerCase(), _global.FILE_CONFIG(__strProviderCode.toUpperCase()));

        String __strQUERY = "SELECT * FROM ic_warehouse ORDER BY code ASC";

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        JSONArray __arrList = ResponseUtil.query2Array(__rsData1);

        __objTMP.put("success", true);
        __objTMP.put("data", __arrList);

        return __objTMP;
    }

    private JSONObject _getShelfCode(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strDatabaseName = !param.isNull("database_name") && !param.getString("database_name").trim().isEmpty() ? param.getString("database_name") : "";
        String __strProviderCode = !param.isNull("provider_code") && !param.getString("provider_code").trim().isEmpty() ? param.getString("provider_code") : "";

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND whcode IN " + param.getString("wh_code") : "";

        __strQueryExtends = __strQueryExtends.equals("") ? "" : " WHERE 1=1 " + __strQueryExtends;

        conn = this.__routine._connect(__strDatabaseName.toLowerCase(), _global.FILE_CONFIG(__strProviderCode.toUpperCase()));

        String __strQUERY = "SELECT code, name_1 FROM ic_shelf " + __strQueryExtends + " ORDER BY code ASC";

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        JSONArray __arrList = ResponseUtil.query2Array(__rsData1);

        __objTMP.put("success", true);
        __objTMP.put("data", __arrList);

        return __objTMP;
    }

    private JSONObject _findTMSConfigDate(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strDatabaseName = !param.isNull("database_name") && !param.getString("database_name").trim().isEmpty() ? param.getString("database_name") : "";
        String __strProviderCode = !param.isNull("provider_code") && !param.getString("provider_code").trim().isEmpty() ? param.getString("provider_code") : "";

        conn = __routine._connect(__strDatabaseName.toLowerCase(), _global.FILE_CONFIG(__strProviderCode.toUpperCase()));
        String __strQUERY = "SELECT code FROM tms_config_date WHERE date_flag=2";
        __objTMP.put("row_count", __routine._getRowCount(conn, __strQUERY));
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _findSystemID(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strDatabaseName = !param.isNull("database_name") && !param.getString("database_name").trim().isEmpty() ? param.getString("database_name") : "";
        String __strProviderCode = !param.isNull("provider_code") && !param.getString("provider_code").trim().isEmpty() ? param.getString("provider_code") : "";
        String __strUserCode = !param.isNull("user_code") && !param.getString("user_code").trim().isEmpty() ? param.getString("user_code").toUpperCase() : "";
        String __strUserName = !param.isNull("user_name") && !param.getString("user_name").trim().isEmpty() ? param.getString("user_name") : "";
        String __strSystemID = !param.isNull("system_id") && !param.getString("system_id").trim().isEmpty() ? param.getString("system_id").toUpperCase() : "";
        String __strBranchCode = !param.isNull("branch_code") && !param.getString("branch_code").trim().isEmpty() ? param.getString("branch_code").toUpperCase() : "";
        String __strBranchName = !param.isNull("branch_name") && !param.getString("branch_name").trim().isEmpty() ? param.getString("branch_name").toUpperCase() : "";

        conn = this.__routine._connect(__strDatabaseName.toLowerCase(), _global.FILE_CONFIG(__strProviderCode.toUpperCase()));

        String __strQUERY = "SELECT system_id FROM sml_group_system WHERE system_id = '" + __strSystemID + "' ";
        Integer __rowCount = this.__routine._getRowCount(conn, __strQUERY);

        if (__rowCount > 0) {
            UUID uid = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
            String __strSessionID;
            __strSessionID = uid.randomUUID().toString();
            HttpSession __session = this.__request.getSession();
            __session.setAttribute("provider_code", __strProviderCode);
            __session.setAttribute("database_name", __strDatabaseName);
            __session.setAttribute("branch_code", __strBranchCode);
            __session.setAttribute("branch_name", __strBranchName);
            __session.setAttribute("user_code", __strUserCode);
            __session.setAttribute("user_name", __strUserName);
            __session.setAttribute("system_id", __strSystemID);
            __session.setAttribute("session_id", __strSessionID);
            __session.setMaxInactiveInterval(30 * 60);
        }
        __objTMP.put("row_count", __rowCount);
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _saveGroupLocation(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strDatabaseName = !param.isNull("database_name") && !param.getString("database_name").trim().isEmpty() ? param.getString("database_name") : "";
        String __strProviderCode = !param.isNull("provider_code") && !param.getString("provider_code").trim().isEmpty() ? param.getString("provider_code") : "";
        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code") : "";
        String __strGroupName = !param.isNull("group_name") && !param.getString("group_name").trim().isEmpty() ? param.getString("group_name") : "";
        String __strWhCode = !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? param.getString("wh_code") : "";
        String __strShelfCode = !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? param.getString("shelf_code") : "";

        conn = this.__routine._connect(__strDatabaseName.toLowerCase(), _global.FILE_CONFIG(__strProviderCode.toUpperCase()));

        String __strQUERY = "SELECT group_code FROM sml_group_location WHERE group_code = '" + __strGroupCode + "'";
        Integer __rowCount = __routine._getRowCount(conn, __strQUERY);

        if (__rowCount > 0) {
            __objTMP.put("data", "มีข้อมูล " + __strGroupCode + " อยู่ในระบบแล้ว.");
        } else {
            __strQUERY = "INSERT INTO sml_group_location (group_code,name,whcode,location) VALUES ('" + __strGroupCode + "','" + __strGroupName + "','" + __strWhCode + "','" + __strShelfCode + "')";
            PreparedStatement __stmt1;
            __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            __stmt1.executeUpdate();
            __stmt1.close();
            __objTMP.put("data", "");
        }

        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _saveTMSConfigDate(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strDatabaseName = !param.isNull("database_name") && !param.getString("database_name").trim().isEmpty() ? param.getString("database_name") : "";
        String __strProviderCode = !param.isNull("provider_code") && !param.getString("provider_code").trim().isEmpty() ? param.getString("provider_code") : "";
        String __strBeginDate = !param.isNull("begin_date") && !param.getString("begin_date").trim().isEmpty() ? param.getString("begin_date") : "";

        conn = this.__routine._connect(__strDatabaseName.toLowerCase(), _global.FILE_CONFIG(__strProviderCode.toUpperCase()));

        String __strQUERY = "INSERT INTO tms_config_date (code, begin_date, date_flag) VALUES ('pick_and_pack', '" + __strBeginDate + "', 2)";
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.executeUpdate();
        __stmt1.close();
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _saveGroupSystem(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strDatabaseName = !param.isNull("database_name") && !param.getString("database_name").trim().isEmpty() ? param.getString("database_name") : "";
        String __strProviderCode = !param.isNull("provider_code") && !param.getString("provider_code").trim().isEmpty() ? param.getString("provider_code") : "";
        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code") : "";
        String __strSystemID = !param.isNull("system_id") && !param.getString("system_id").trim().isEmpty() ? param.getString("system_id").toUpperCase() : "";

        conn = this.__routine._connect(__strDatabaseName.toLowerCase(), _global.FILE_CONFIG(__strProviderCode.toUpperCase()));

        String __strQUERY = "INSERT INTO sml_group_system (group_code, system_id) VALUES ('" + __strGroupCode + "', '" + __strSystemID + "')";
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY);
        __stmt1.executeUpdate();
        __stmt1.close();
        __objTMP.put("success", true);

        return __objTMP;
    }

}
