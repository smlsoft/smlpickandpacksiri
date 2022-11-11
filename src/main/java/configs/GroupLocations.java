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
@WebServlet(name = "group-locations-list-1", urlPatterns = {"/group-locations-list-1"})
public class GroupLocations extends HttpServlet {
    
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
                case "get_warehouse":
                    objResult = _getWareHouse(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_shelf_code":
                    objResult = _getShelfCode(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "find_group_location":
                    objResult = _findGroupLocation(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "save_group_location":
                    objResult = _saveGroupLocation(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "delete_group_location":
                    objResult = _deleteGroupLocation(__conn, ResponseUtil.str2Json(request.getParameter("data")));
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
        __strQueryExtends += !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? " AND (group_code='" + param.getString("group_code") + "') OR (name LIKE '%" + param.getString("group_code") + "%')" : "";
        
        __strQueryExtends = __strQueryExtends.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtends;
        
        String __strQUERY = "SELECT group_code, whcode, location, name FROM sml_group_location " + __strQueryExtends + " ORDER BY group_code DESC";
        
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
            __rsHTML += "<td><h5>" + __rsData1.getString("group_code") + " ~ " + __rsData1.getString("name") + "</h5></td>";
            __rsHTML += "<td><h5>" + __rsData1.getString("whcode") + "</h5></td>";
            __rsHTML += "<td><button type='button' id='btn-more' class='btn btn-info btn-flat' key_id='" + __rsData1.getString("group_code") + "' shelf_code='" + __rsData1.getString("location") + "'>เพิ่มเติม</button></td>";
            __rsHTML += "<td><button type='button' id='btn-edit' class='btn btn-warning btn-flat' key_id='" + __rsData1.getString("group_code") + "'>แก้ไข</button></td>";
            __rsHTML += "<td><button type='button' id='btn-delete' class='btn btn-danger btn-flat' key_id='" + __rsData1.getString("group_code") + "'>ลบ</button></td>";
            __rsHTML += "</tr>";
            __rsHTML += "<tr id='" + __rsData1.getString("group_code") + "'></tr>";
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
        
        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND (code IN " + param.getString("shelf_code") + ") " : "";
        
        __strQueryExtends = __strQueryExtends.equals("") ? "" : " WHERE 1=1 " + __strQueryExtends;
        
        String __strQUERY = "SELECT DISTINCT code,name_1 FROM ic_shelf " + __strQueryExtends;
        
        conn.setAutoCommit(false);
        String __rsHTML = "";
        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        
        __rsHTML += "<td colspan='5' style='padding: 5px 0'>";
        __rsHTML += "<div>";
        __rsHTML += "<table class='table table-hover' style='margin: 0;'>";
        __rsHTML += "<tr class='text-center' style='background-color: #F5B041; color: #F8F9F9'>";
        __rsHTML += "<td><strong>รหัสสถานที่เก็บ ~ ชื่อสถานที่เก็บ</strong></td>";
        __rsHTML += "</tr>";
        
        String __strDetail = "";
        String __bgColor;
        Integer __rowNumber = 0;
        while (__rsData1.next()) {
            if (__rowNumber % 2 == 0) {
                __bgColor = "#FEF9E7";
            } else {
                __bgColor = "#F9E79F";
            }
            __strDetail += "<tr style='background-color: " + __bgColor + "' color: #000;>";
            __strDetail += "<td><h5>" + __rsData1.getString("code") + " ~ " + __rsData1.getString("name_1") + "</h5></td>";
            __strDetail += "</tr>";
            __rowNumber++;
        }
        __stmt1.close();
        __rsData1.close();
        
        if (__strDetail.equals("")) {
            __rsHTML += "<tr><td>ไม่พบข้อมูล</td></tr>";
        } else {
            __rsHTML += __strDetail;
        }
        
        __objTMP.put("success", true);
        __objTMP.put("data", __rsHTML);
        
        return __objTMP;
    }
    
    private JSONObject _getWareHouse(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "SELECT * FROM ic_warehouse ORDER BY code";
        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        
        __objTMP.put("data", ResponseUtil.query2Array(__rsData1));
        __objTMP.put("success", true);
        
        return __objTMP;
    }
    
    private JSONObject _findGroupLocation(Connection conn, JSONObject param) throws SQLException {
        
        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? " AND (group_code='" + param.getString("group_code") + "') " : "";
        
        __strQueryExtends = __strQueryExtends.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtends;
        
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strQUERY = "SELECT group_code, whcode, location, name FROM sml_group_location " + __strQueryExtends + "  ORDER BY group_code DESC";
        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        
        __objTMP.put("data", ResponseUtil.query2Array(__rsData1));
        __objTMP.put("success", true);
        
        return __objTMP;
    }
    
    private JSONObject _getShelfCode(Connection conn, JSONObject param) throws SQLException {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        
        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND (whcode IN " + param.getString("wh_code") + ") " : "";
        
        __strQueryExtends = __strQueryExtends.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtends;
        
        String __strQUERY = "SELECT code, name_1 FROM ic_shelf " + __strQueryExtends + " ORDER BY code";
        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        
        __objTMP.put("data", ResponseUtil.query2Array(__rsData1));
        __objTMP.put("success", true);
        
        return __objTMP;
    }
    
    private JSONObject _saveGroupLocation(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        
        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code") : "";
        String __strGroupName = !param.isNull("group_name") && !param.getString("group_name").trim().isEmpty() ? param.getString("group_name") : "";
        String __strWhCode = !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? param.getString("wh_code") : "";
        String __strShelfCode = !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? param.getString("shelf_code") : "";
        
        String __strQUERY = "SELECT group_code FROM sml_group_location WHERE group_code = '" + __strGroupCode + "' ";
        
        conn.setAutoCommit(false);
        Integer __rowCount = __routine._getRowCount(conn, __strQUERY);
        if (__rowCount > 0) {
            __strQUERY = "UPDATE sml_group_location SET name='" + __strGroupName + "', whcode='" + __strWhCode + "', location='" + __strShelfCode + "' WHERE group_code='" + __strGroupCode + "' ";
            PreparedStatement __stmt1;
            __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            __stmt1.executeUpdate();
            __stmt1.close();
        } else {
            __strQUERY = "INSERT INTO sml_group_location (group_code, name, whcode, location) VALUES ('" + __strGroupCode + "', '" + __strGroupName + "', '" + __strWhCode + "' ,'" + __strShelfCode + "')";
            PreparedStatement __stmt1;
            __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            __stmt1.executeUpdate();
            __stmt1.close();
        }
        conn.commit();
        __objTMP.put("success", true);
        
        return __objTMP;
    }
    
    private JSONObject _deleteGroupLocation(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code") : "";
        
        String __strQUERY = "DELETE FROM sml_group_location WHERE group_code='" + __strGroupCode + "' ";
        
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
