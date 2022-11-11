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

@WebServlet(name = "warehouse-administration-list-1", urlPatterns = {"/warehouse-administration-list-1"})
public class Administration extends HttpServlet {

    private final _routine __routine = new _routine();
    private String __strDatabaseName;
    private String __strProviderCode;
    private String __strUserCode;
    private String __strSystemID;
    private String __strSessionID;

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

    private JSONObject _getMainDetail(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strWhCode = !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND (wh_code IN " + param.getString("wh_code") : "";
        String __strShelfCode = !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND shelf_code IN " + param.getString("shelf_code") + ") " : "";
        Integer __strOffset = !param.isNull("offset") && param.getInt("offset") > 0 ? param.getInt("offset") : 0;
        Integer __strLimit = !param.isNull("limit") && param.getInt("limit") > 0 ? param.getInt("limit") : 0;

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND (wh_code IN " + param.getString("wh_code") : "";
        __strQueryExtends += !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND shelf_code IN " + param.getString("shelf_code") + ") " : "";
        __strQueryExtends += !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? " AND ref_code='" + param.getString("ref_code") + "' " : "";
        __strQueryExtends += !param.isNull("car_code") && !param.getString("car_code").trim().isEmpty() ? " AND '" + param.getString("car_code") + "'=(SELECT DISTINCT replace(car_code, ' ', '') FROM tms_que_shipment as tms WHERE tms.doc_no='" + param.getString("shipment_code") + "') " : "";
        __strQueryExtends += !param.isNull("shipment_code") && !param.getString("shipment_code").trim().isEmpty() ? " AND tms_que_shipment_code ='" + param.getString("shipment_code") + "' " : "";

        if (!param.isNull("from_date") && !param.getString("from_date").trim().isEmpty() && !param.isNull("to_date") && !param.getString("to_date").trim().isEmpty()) {
            String __strTmpFromDate = param.getString("from_date");
            String __strTmpToDate = param.getString("to_date");
            if (__strTmpFromDate.equals(__strTmpToDate)) {
                __strQueryExtends += " AND (ref_date = '" + __strTmpFromDate + "') ";
            } else {
                __strQueryExtends += " AND (ref_date BETWEEN '" + __strTmpFromDate + "' AND '" + __strTmpToDate + "') ";
            }
        }

        if (!param.isNull("send_type") && !param.getString("send_type").trim().isEmpty()) {
            __strQueryExtends += " AND (send_type = '" + param.getString("send_type") + "')";
        }

        String __strQueryExtends2 = "";
        if (!param.isNull("customer_name") && !param.getString("customer_name").trim().isEmpty()) {
            __strQueryExtends2 += " AND cust_name LIKE '%" + param.getString("customer_name") + "%' ";
        }

        if (!param.isNull("status_type") && !param.getString("status_type").trim().isEmpty()) {
            String strStatusType = param.getString("status_type");

            switch (Integer.parseInt(strStatusType)) {
                case 0:
                    __strQueryExtends += " AND status=0 ";
                    __strQueryExtends2 += " AND last_status!=1 AND status_detail='0' ";
                    break;
                case 1:
                    __strQueryExtends2 += " AND last_status=1";
                    break;
                case 2:
                    __strQueryExtends += " AND status=1 ";
                    __strQueryExtends2 += " AND trans_flag=44 AND status_detail='1' AND last_status!=1";
                    break;
                case 3:
                    __strQueryExtends += " AND status=1 ";
                    __strQueryExtends2 += " AND trans_flag!=44 AND status_detail='1' AND last_status!=1";
                    break;
                case 4:
                    __strQueryExtends += " AND status=1 ";
                    __strQueryExtends2 += " AND trans_flag=44 AND status_detail='1,0' AND last_status!=1";
                    break;
                case 5:
                    __strQueryExtends += " AND status=1 ";
                    __strQueryExtends2 += " AND trans_flag!=44 AND status_detail='1,0' AND last_status!=1";
                    break;
                case 6:
                    __strQueryExtends += " AND status=2 AND last_status!=1";
                    break;

            }
        }

        __strQueryExtends = __strQueryExtends.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtends;

        String __strQUERY = "SELECT * FROM"
                + "("
                + " SELECT DISTINCT ref_code,doc_date,trans_flag,last_status,send_type"
                + " ,COALESCE(create_date_time, '') AS create_date_time "
                + " ,COALESCE((tms_que_shipment_code),'') AS tms_que_shipment_code "
                + " ,COALESCE((SELECT name_1 FROM ar_customer where code=cust_code),'') AS cust_name "
                + " ,COALESCE((SELECT DISTINCT replace(car_code, ' ', '') FROM tms_que_shipment AS tms WHERE tms.doc_no=pp_trans.tms_que_shipment_code),'') AS car_code "
                + " ,COALESCE((SELECT DISTINCT to_char(expect_date, 'YYYY-MM-DD') FROM tms_que_shipment AS tms WHERE tms.doc_no=pp_trans.tms_que_shipment_code),'') AS expect_date "
                + " ,COALESCE((SELECT DISTINCT CAST(round_no AS text) FROM tms_que_shipment AS tms WHERE tms.doc_no=pp_trans.tms_que_shipment_code),'') AS round_no "
                + " ,COALESCE((SELECT DISTINCT to_char(receive_date, 'YYYY-MM-DD HH24:MI:SS') FROM tms_que_shipment AS tms WHERE tms.doc_no=pp_trans.tms_que_shipment_code),'') AS receive_date "
                + " ,COALESCE((SELECT DISTINCT array_to_string (array (SELECT DISTINCT status FROM pp_trans_detail WHERE pp_trans_detail.ref_code=pp_trans.ref_code AND pp_trans_detail.car_code=pp_trans.car_code " + __strWhCode + __strShelfCode + " ORDER BY status DESC), ','))) AS status_detail "
                + " ,CASE "
                + " WHEN (SELECT COALESCE((select count(doc_no) FROM ic_trans_detail WHERE ic_trans_detail.ref_doc_no=pp_trans.ref_code AND ic_trans_detail.trans_flag IN (37,44) AND ic_trans_detail.last_status=0),0))=0  THEN '0' "
                + " WHEN (SELECT COALESCE((select count(doc_no) FROM ic_trans_detail WHERE ic_trans_detail.ref_doc_no=pp_trans.ref_code AND ic_trans_detail.trans_flag IN (37,44) AND ic_trans_detail.last_status=0),0))>0 /*AND is_print=1*/  THEN '0' " // ลบ is_print=1 ออก Destiny Request มา
                + " WHEN (SELECT COALESCE(sum(qty * (stand_value/divide_value)),0) FROM ic_trans_detail WHERE ic_trans_detail.doc_no=pp_trans.ref_code AND ic_trans_detail.trans_flag in (36,34) AND ic_trans_detail.last_status=0 AND ic_trans_detail.item_code <> '') =  (SELECT COALESCE(sum(qty * (stand_value/divide_value)),0) FROM ic_trans_detail WHERE ic_trans_detail.ref_doc_no=pp_trans.ref_code AND ic_trans_detail.trans_flag in (37,44) AND ic_trans_detail.last_status=0) "
                + " THEN '1' "
                + " ELSE '2' "
                + " END AS main_status "
                + " FROM pp_trans " + __strQueryExtends
                + " AND (is_close=0) "
                + " AND ((trans_flag=36 AND sale_type=0) OR (trans_flag=44 AND sale_type IN (1,3) AND is_close=0) OR (trans_flag=34)) "
                + " AND  EXISTS (SELECT * FROM (SELECT doc_no,trans_flag FROM ic_trans WHERE (trans_flag=36) OR (trans_flag=44) OR trans_flag=34) AS ic_trans WHERE ic_trans.doc_no = pp_trans.ref_code) "
                + ") AS pp_trans WHERE main_status='0' " + __strQueryExtends2 + " ORDER BY create_date_time DESC,ref_code";

        String __strQueryPagination = " LIMIT " + __strLimit + " OFFSET " + __strOffset;

        conn.setAutoCommit(false);

        __objTMP.put("row_count", __routine._getRowCount(conn, __strQUERY));

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY + __strQueryPagination, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        String __rsHTML = "";
        String[] __arrSendType = {"รับเอง", "ส่งให้"};
        while (__rsData1.next()) {
            String __strRefCode = __rsData1.getString("ref_code");
            String __strCarCode = __rsData1.getString("car_code").equals("") ? "null" : __rsData1.getString("car_code");
            String __strShipmentCode = __rsData1.getString("tms_que_shipment_code").equals("") ? "null" : __rsData1.getString("tms_que_shipment_code");
            String __strLastStatus = __rsData1.getString("last_status");
            String __strTransFlag = __rsData1.getString("trans_flag");
            String __strCreateDateTime = __rsData1.getString("create_date_time").equals("") ? "-" : __rsData1.getString("create_date_time");
            String __strExpectDate = __rsData1.getString("expect_date").equals("") ? "-" : __rsData1.getString("expect_date");
            String __strRecieveDate = __rsData1.getString("receive_date").equals("") ? "-" : __rsData1.getString("receive_date");
            String __strRoundNo = __rsData1.getString("round_no").equals("") ? "-" : __rsData1.getString("round_no");
            String __strStatusDetail = __rsData1.getString("status_detail").equals("") ? "-" : __rsData1.getString("status_detail");

            if (__rsData1.getString("last_status").equals("1")) {
                __rsHTML += "<tr class='" + __strRefCode + "' car_code='" + __rsData1.getString("car_code") + "' shipment_code='" + __strShipmentCode + "' style='background-color: #FF6666; color:#FFF' key_id='" + __strRefCode + "'>";
            } else {
                __rsHTML += "<tr class='" + __strRefCode + "' car_code='" + __rsData1.getString("car_code") + "' shipment_code='" + __strShipmentCode + "'>";
            }
            __rsHTML += "<td><h5><strong>" + __routine._convertDateTime(__strCreateDateTime) + "</strong></h5></td>";
            __rsHTML += "<td><h5>" + __rsData1.getString("ref_code") + "</h5></td>";
            __rsHTML += "<td><h5>" + __rsData1.getString("cust_name") + "</h5></td>";
            __rsHTML += "<td><h5>" + __arrSendType[__rsData1.getInt("send_type")] + "</h5></td>";
            __rsHTML += "<td><h5>" + __rsData1.getString("car_code") + "</h5></td>";
            __rsHTML += "<td><h5>" + __routine._convertDate(__strExpectDate) + "</h5></td>";
            __rsHTML += "<td><h5>" + __strRoundNo + "</h5></td>";
            __rsHTML += "<td><h5>" + __routine._convertDateTime(__strRecieveDate) + "</h5></td>";

            Integer __isPrint = 0;
            if (__strLastStatus.equals("1")) {
                __rsHTML += "<td style='background-color: red; color:#FFF'><h5>ยกเลิก</h5></td>";
            } else {
                String __strTmpCarCode = __strCarCode;
                String __strTmpShipmentCode = __strShipmentCode;
                __strTmpCarCode = __strCarCode.equals("null") ? "" : " AND '" + __strTmpCarCode + "'=(SELECT DISTINCT replace(car_code, ' ', '') FROM tms_que_shipment AS tms WHERE tms.doc_no='" + __strShipmentCode + "') ";
                __strTmpShipmentCode = __strShipmentCode.equals("null") ? "" : " AND tms_que_shipment_code='" + __strTmpShipmentCode + "'";

                __strQUERY = "SELECT array_to_string(array(SELECT DISTINCT status FROM pp_trans WHERE ref_code='" + __strRefCode + "'" + __strTmpCarCode + __strTmpShipmentCode + " AND is_close=0 ORDER BY status DESC), ',') AS status";
                PreparedStatement __stmt2;
                ResultSet __rsData2;
                __stmt2 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                __rsData2 = __stmt2.executeQuery();
                String __strTmpStatusPPTrans = "";
                while (__rsData2.next()) {
                    __strTmpStatusPPTrans = __rsData2.getString("status");
                }
                String[] sptStatusPPtrans = __strTmpStatusPPTrans.split(",");
                Integer statusPPtrans = 1;
                for (int i = 0; i < sptStatusPPtrans.length; i++) {
                    statusPPtrans = sptStatusPPtrans[i].equals("0") ? 0 : sptStatusPPtrans[i].equals("2") ? 2 : 1;
                    i = sptStatusPPtrans.length;
                }

                switch (statusPPtrans) {
                    case 0:
                        __rsHTML += "<td><h5>ปกติ</h5></td>";
                        break;
                    case 1:
                        String[] sptStatusDetail = __strStatusDetail.split(",");
                        if (sptStatusDetail.length > 1) {
                            __rsHTML += sptStatusDetail[1].equals("0") ? __strTransFlag.equals("44") ? "<td style='background-color: #58D68D;color: #FFF;text-align:center;'><h5>จัดเสร็จ บางส่วน</h5></td>" : "<td style='background-color: #58D68D;color: #FFF;text-align:center;'><h5>พร้อมออก บางส่วน</h5></td>" : "";
                        } else {
                            __rsHTML += __strTransFlag.equals("44") ? "<td style='background-color: #58D68D;color: #FFF;text-align:center;'><h5>จัดเสร็จ</h5></td>" : "<td style='background-color: #58D68D;color: #FFF;text-align:center;'><h5>พร้อมออก</h5></td>";
                        }
                        __isPrint = 1;
                        break;
                    case 2:
                        __rsHTML += "<td style='background-color: #FF5733;color: #FFF;text-align:center;'><h5>พิมพ์ใบจัดแล้ว</h5></td>";
                        __isPrint = 1;
                        break;
                }
            }
            __rsHTML += "<td><button type='button' id='btn-more' class='btn btn-info btn-flat' key_id='" + __strRefCode + "' car_code='" + __strCarCode + "' shipment_code='" + __strShipmentCode + "'>เพิ่มเติม</button></td>";

            if (__isPrint == 0) {
                __rsHTML += "<td><button type='button' id='btn-print' class='btn btn-success btn-flat is_print' key_id='" + __strRefCode + "' car_code='" + __strCarCode + "' shipment_code='" + __strShipmentCode + "' is_print='" + __isPrint + "' trans_flag='" + __rsData1.getString("trans_flag") + "'>พิมพ์</button></td>";
            } else {
                __rsHTML += "<td><button type='button' id='btn-print' class='btn btn-success btn-flat is_print' disabled key_id='" + __strRefCode + "' car_code='" + __strCarCode + "' shipment_code='" + __strShipmentCode + "' is_print='" + __isPrint + "' trans_flag='" + __rsData1.getString("trans_flag") + "'>พิมพ์</button></td>";
            }
            __rsHTML += "<td><button type='button' id='btn-delete' class='btn btn-danger btn-flat' key_id='" + __strRefCode + "'>ลบ</button></td>";
            __rsHTML += "</tr>";
            __rsHTML += "<tr id='" + __strRefCode + "_" + __strCarCode + "_" + __strShipmentCode + "' style='display: none;'></tr>";
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

    private JSONObject _getSubDetail(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? " AND ref_code='" + param.getString("ref_code") + "' " : "";
        __strQueryExtends += !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND (wh_code IN " + param.getString("wh_code") : "";
        __strQueryExtends += !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND shelf_code IN " + param.getString("shelf_code") + ") " : "";
        __strQueryExtends += !param.isNull("car_code") && !param.getString("car_code").trim().isEmpty() ? " AND '" + param.getString("car_code") + "'=(SELECT DISTINCT replace(car_code, ' ', '') FROM tms_que_shipment AS tms WHERE tms.doc_no='" + param.getString("shipment_code") + "') " : "";
        __strQueryExtends += !param.isNull("shipment_code") && !param.getString("shipment_code").trim().isEmpty() ? " AND tms_que_shipment_code ='" + param.getString("shipment_code") + "' " : "";

        __strQueryExtends = __strQueryExtends.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtends;

        String __strQUERY = "SELECT doc_no,ic_code,wh_code,shelf_code,qty,event_qty,status,tms_que_shipment_code,line_number, "
                + " COALESCE((SELECT name FROM tms_reason WHERE tms_reason.code=pp_trans_detail.remark and tms_reason.reason_flag=4),'')AS remark, "
                + " COALESCE((SELECT name_1 FROM ic_inventory WHERE ic_inventory.code=ic_code),'')AS item_name, "
                + " COALESCE((SELECT name_1 FROM ic_warehouse WHERE ic_warehouse.code=wh_code),'')AS wh_name, "
                + " COALESCE((SELECT name_1 FROM ic_shelf WHERE ic_shelf.code=shelf_code AND ic_shelf.whcode=wh_code),'')AS shelf_name "
                + " FROM pp_trans_detail " + __strQueryExtends + " ORDER BY line_number";

        conn.setAutoCommit(false);
        String __rsHTML = "";
        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        __rsHTML += "<td colspan='13' style='padding: 5px 0'>";
        __rsHTML += "<div>";
        __rsHTML += "<table class='table table-hover' style='margin: 0;'>";
        __rsHTML += "<tr class='text-center' style='background-color: #F5B041; color: #F8F9F9'>";
        __rsHTML += "<td><strong>ลำดับ</strong></td>";
        __rsHTML += "<td><strong>รหัส</strong></td>";
        __rsHTML += "<td><strong>รหัสสินค้า ~ ชื่อสินค้า</strong></td>";
        __rsHTML += "<td><strong>รหัสคลัง ~ ชื่อคลัง</strong></td>";
        __rsHTML += "<td><strong>รหัสที่เก็บ ~ ชื่อที่เก็บ</strong></td>";
        __rsHTML += "<td><strong>จำนวน</strong></td>";
        __rsHTML += "<td><strong>จำนวนที่จัดได้</strong></td>";
        __rsHTML += "<td><strong>สถานะ</strong></td>";
        __rsHTML += "<td><strong>หมายเหตุ</strong></td>";
        __rsHTML += "</tr>";

        Integer __rowNumber = 0;
        String __strDetail = "";
        Boolean isPlus = false;
        while (__rsData1.next()) {
            if (__rowNumber == 0 && __rsData1.getInt("line_number") == 0) {
                isPlus = true;
            }
            __strDetail = "";
            String __bgColor = __rowNumber % 2 == 0 ? "#FEF9E7" : "#F9E79F";

            Integer __status = __rsData1.getInt("status");
            String[] __arrStatus = {"ปกติ", "ปิด", "ปิดไม่ปกติ"};
            String[] __arrBgStatus = {"#449D44", "#944DFF", "#F98180"};

            __strDetail += "<tr style='background-color: " + __bgColor + "' color: #000;>";
            __strDetail += "<td><h5><strong>" + (isPlus ? (__rsData1.getInt("line_number") + 1) : __rsData1.getInt("line_number")) + "</strong></h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("doc_no") + "</h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("ic_code") + " ~ " + __rsData1.getString("item_name") + "</h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("wh_code") + " ~ " + __rsData1.getString("wh_name") + "</h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("shelf_code") + " ~ " + __rsData1.getString("shelf_name") + "</h5></td>";
            __strDetail += "<td><h5>" + String.format("%,.2f", Float.parseFloat(__rsData1.getString("qty"))) + "</h5></td>";
            __strDetail += "<td><h5>" + String.format("%,.2f", Float.parseFloat(__rsData1.getString("event_qty"))) + "</h5></td>";
            __strDetail += "<td style='color: #FFF; background-color: " + __arrBgStatus[__status] + "'><h5>" + __arrStatus[__status] + "</h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("remark") + "</h5></td>";

            __rsHTML += __strDetail;
            __rsHTML += "</tr>";

            __rowNumber++;
        }
        if (__strDetail.equals("")) {
            __rsHTML += "<tr><td colspan='13'>ไม่พบข้อมูล</td></tr>";
        }

        __rsHTML += "</table>";
        __rsHTML += "</div>";
        __rsHTML += "</td>";

        __stmt1.close();
        __rsData1.close();

        __objTMP.put("success", true);
        __objTMP.put("data", __rsHTML);

        return __objTMP;
    }

}
