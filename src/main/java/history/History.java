/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package history;

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
@WebServlet(name = "history-list-1", urlPatterns = {"/history-list-1"})
public class History extends HttpServlet {

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

        String __strRefCode = !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? param.getString("ref_code") : "";
        Integer __strOffset = !param.isNull("offset") && param.getInt("offset") > 0 ? param.getInt("offset") : 0;
        Integer __strLimit = !param.isNull("limit") && param.getInt("limit") > 0 ? param.getInt("limit") : 0;

        String __strQUERY = "SELECT doc_no,ref_code,user_code,action,to_char(create_datetime, 'YYYY-MM-DD HH24:MI:SS') AS create_datetime,(SELECT name_1 FROM erp_user WHERE erp_user.code=pp_trans_log.user_code)AS user_name FROM pp_trans_log WHERE ref_code='" + __strRefCode + "' ORDER BY create_datetime ASC,doc_no ";
        String __strQueryPagination = " LIMIT " + __strLimit + " OFFSET " + __strOffset;

        conn.setAutoCommit(false);

        __objTMP.put("row_count", __routine._getRowCount(conn, __strQUERY));

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY + __strQueryPagination, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        String __rsHTML = "";
        String[] __arrActionStatus = {"ไม่มีการทำงาน", "Print คลัง/ขาย", "Reprint คลัง/ขาย", "Conmfirm ใบจัด คลัง/ขาย", "ปิดใบจัด คลัง/ขาย", "Print ธุรการ/ขาย", "Reprint ธุรการ/ขาย", "Print ปล่อยรถ", "Reprint ปล่อยรถ", "ลบเอกสาร คลัง/ขาย", "ลบเอกสาร ธุรการ/ขาย"};
        while (__rsData1.next()) {
            __rsHTML += "<tr>";
            __rsHTML += "<td><h5>" + __rsData1.getString("doc_no") + "</h5></td>";
            __rsHTML += "<td><h5>" + __rsData1.getString("ref_code") + "</h5></td>";
            __rsHTML += "<td><h5>" + __rsData1.getString("user_code") + " ~ " + __rsData1.getString("user_name") + "</h5></td>";
            __rsHTML += "<td><h5>" + __arrActionStatus[__rsData1.getInt("action")] + "</h5></td>";
            __rsHTML += "<td><h5>" + __routine._convertDateTime(__rsData1.getString("create_datetime")) + "</h5></td>";
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
