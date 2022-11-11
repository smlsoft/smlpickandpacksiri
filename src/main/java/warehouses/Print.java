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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import utils.GenBarcode;
import utils.ResponseUtil;
import utils._global;
import utils._routine;

@WebServlet(name = "warehouse-print-1", urlPatterns = {"/warehouse-print-1"})
public class Print extends HttpServlet {

    private final _routine __routine = new _routine();
    private String __strDatabaseName;
    private String __strProviderCode;
    private String __strUserCode;
    private String __strSystemID;
    private String __strSessionID;

    private JSONObject _verifyData1(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        conn.setAutoCommit(false);
        String __strRefCode = !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? param.getString("ref_code") : "";
        String __strDocNo = !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? param.getString("doc_no") : "";
        String __isPrint = !param.isNull("is_print") && !param.getString("is_print").trim().isEmpty() ? param.getString("is_print") : "";
        String __strTransFlag = !param.isNull("trans_flag") && !param.getString("trans_flag").trim().isEmpty() ? param.getString("trans_flag") : "";

        String __strQUERY;
        // ### UPDATE STATUS
        __strQUERY = "UPDATE pp_trans SET is_print=1,status=2 WHERE doc_no=? AND is_close=0";
        PreparedStatement __stmtUpdateStatus;
        __stmtUpdateStatus = conn.prepareStatement(__strQUERY);
        __stmtUpdateStatus.setString(1, __strDocNo);
        __stmtUpdateStatus.executeUpdate();
        conn.commit();
        __stmtUpdateStatus.close();
        // ### INSERT LOG
        String __strStatus;
        if (__isPrint.equals("1")) {
            __strStatus = "2";
        } else {
            __strStatus = "1";
        }
        __strQUERY = "INSERT INTO pp_trans_log (doc_no,ref_code,trans_flag,user_code,action) VALUES ('" + __strDocNo + "','" + __strRefCode + "','" + __strTransFlag + "','" + __strUserCode + "','" + __strStatus + "')";
        PreparedStatement __stmtInsertLog;
        __stmtInsertLog = conn.prepareStatement(__strQUERY);
        __stmtInsertLog.executeUpdate();
        conn.commit();
        __stmtUpdateStatus.close();

        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _verifyData2(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");
        conn.setAutoCommit(false);
        String __strRefCode = !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? param.getString("ref_code") : "";
        String __strCarCode = !param.isNull("car_code") && !param.getString("car_code").trim().isEmpty() ? param.getString("car_code") : "";
        String __strShipmentCode = !param.isNull("shipment_code") && !param.getString("shipment_code").trim().isEmpty() ? param.getString("shipment_code") : "";
        String __isPrint = !param.isNull("is_print") && !param.getString("is_print").trim().isEmpty() ? param.getString("is_print") : "";
        String __strTransFlag = !param.isNull("trans_flag") && !param.getString("trans_flag").trim().isEmpty() ? param.getString("trans_flag") : "";

        String __strQUERY;
        String __strQueryExtend = "";
        __strQueryExtend += __strRefCode.equals("") ? "" : " AND ref_code='" + __strRefCode + "' ";
        __strQueryExtend += __strTransFlag.equals("") ? "" : " AND trans_flag='" + __strTransFlag + "' ";
        __strQueryExtend += __strCarCode.equals("") ? "" : " AND car_code='" + __strCarCode + "'";
        __strQueryExtend += __strShipmentCode.equals("") ? "" : " AND tms_que_shipment_code='" + __strShipmentCode + "' ";

        __strQueryExtend = __strQueryExtend.equals("") ? " WHERE 1=1 " : " WHERE 1=1 " + __strQueryExtend;

        // ### UPDATE STATUS
        __strQUERY = "UPDATE pp_trans SET status='2',is_print='1'" + __strQueryExtend + " AND is_close='0'";

        PreparedStatement __stmtUpdateStatus;
        __stmtUpdateStatus = conn.prepareStatement(__strQUERY);
        __stmtUpdateStatus.executeUpdate();
        __stmtUpdateStatus.close();
        // ### GET DOC_NO
        __strQUERY = "SELECT doc_no FROM pp_trans " + __strQueryExtend + " ORDER BY doc_no";
        PreparedStatement __stmtGetDocNo;
        ResultSet __rsDataGetDocNo;
        __stmtGetDocNo = conn.prepareStatement(__strQUERY);
        __rsDataGetDocNo = __stmtGetDocNo.executeQuery();
        while (__rsDataGetDocNo.next()) {
            String __strStatus;
            if (__isPrint.equals("1")) {
                __strStatus = "6";
            } else {
                __strStatus = "5";
            }
            // ### INSERT LOG
            __strQUERY = "INSERT INTO pp_trans_log (doc_no,ref_code,trans_flag,user_code,action) VALUES ('" + __rsDataGetDocNo.getString("doc_no") + "','" + __strRefCode + "','" + __strTransFlag + "','" + __strUserCode + "','" + __strStatus + "')";
            PreparedStatement __stmtInsertLog;
            __stmtInsertLog = conn.prepareStatement(__strQUERY);
            __stmtInsertLog.executeUpdate();
            __stmtInsertLog.close();
        }
        __stmtGetDocNo.close();
        __rsDataGetDocNo.close();
        conn.commit();
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _printData(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strRefCode = !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? param.getString("ref_code") : "";
        String __strDocNo = !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? " AND doc_no='" + param.getString("doc_no") + "' " : "";
        String __strWhCode = !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? param.getString("wh_code") : "";
        String __strShelfCode = !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? param.getString("shelf_code") : "";
        String __strCarCode = !param.isNull("car_code") && !param.getString("car_code").trim().isEmpty() ? " AND '" + param.getString("car_code") + "'=(SELECT DISTINCT replace(car_code, ' ', '') FROM tms_que_shipment AS tms WHERE tms.doc_no='" + param.getString("shipment_code") + "') " : "";
        String __strShipmentCode = !param.isNull("shipment_code") && !param.getString("shipment_code").trim().isEmpty() ? " AND tms_que_shipment_code = '" + param.getString("shipment_code") + "' " : "";

        conn.setAutoCommit(false);
        String __rsHTML = "";
        String __strQUERY;
        GenBarcode GenBarcode = new GenBarcode();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        __strQUERY = "SELECT wh_code, doc_no, e_doc_no, b_doc_no FROM pp_trans WHERE ref_code='" + __strRefCode + "'" + __strDocNo + " AND (wh_code IN " + __strWhCode + ") AND (shelf_code IN " + __strShelfCode + ") AND is_close=0 " + __strCarCode + __strShipmentCode;

        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        Integer __totalCount = __routine._getRowCount(conn, __strQUERY);
        Integer __Count = 1;

        while (__rsData1.next()) {

            String __strRemark;
            __strQUERY = "SELECT remark FROM ic_trans WHERE doc_no='" + __strRefCode + "'";

            PreparedStatement __stmtRemark;
            ResultSet __rsDataRemark;
            __stmtRemark = conn.prepareStatement(__strQUERY);
            __rsDataRemark = __stmtRemark.executeQuery();

            while (__rsDataRemark.next()) {
                __strRemark = __rsDataRemark.getString("remark") != null ? !__rsDataRemark.getString("remark").equals("null") ? __rsDataRemark.getString("remark") : "-" : "-";
                String __strNewDate = dateFormat.format(date);
                String __strBarCodeB = javax.xml.bind.DatatypeConverter.printBase64Binary(GenBarcode._barcodeByte(__rsData1.getString("b_doc_no"), 0, 70));
                String __strBarCodeE = javax.xml.bind.DatatypeConverter.printBase64Binary(GenBarcode._barcodeByte(__rsData1.getString("e_doc_no"), 0, 70));

                __rsHTML += "<div class='content-print-layout'>";
                __rsHTML += "<div class='divHeader' style='padding: 5px 0'>";

                __strQUERY = "SELECT company_name_1, address_1 FROM erp_company_profile";
                PreparedStatement __stmt2;
                ResultSet __rsData2;
                __stmt2 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                __rsData2 = __stmt2.executeQuery();

                while (__rsData2.next()) {
                    __rsHTML += "<div class='row' style='font-size: 18px;'>";

                    __rsHTML += "<div class='col-md-6 col-sm-6 col-xs-4'>";

                    __rsHTML += "<img src='data:image/jpg;base64," + __strBarCodeB + "'>";

                    __rsHTML += "</div>";

                    __rsHTML += "<div class='col-md-6 col-sm-6 col-xs-8'>";

                    __rsHTML += "<div class='row text-right'>";

                    __rsHTML += "<div class='col-md-12 col-sm-12 col-xs-12'  >";
                    __rsHTML += "<p style='font-size: 18px;'><strong>ใบจัดสินค้า</strong></p>";
                    __rsHTML += "</div>";
                    __rsHTML += "<div class='col-md-12 col-xs-12 col-xs-12'>";
                    __rsHTML += "<p>เลขที่เอกสาร: " + __rsData1.getString("doc_no") + "</p>";
                    __rsHTML += "</div>";

                    __rsHTML += "</div>";

                    __rsHTML += "</div>";

                    __rsHTML += "</div>";
                }
                __stmt2.close();
                __rsData2.close();

                __strQUERY = "SELECT * FROM( "
                        + " SELECT  pp_trans.doc_no,pp_trans.doc_date,pp_trans.sale_type,pp_trans.ref_date,pp_trans.ref_code,pp_trans.sale_type,pp_trans.sale_code,pp_trans.send_type,pp_trans.confirm_date_time "
                        + " ,COALESCE(department_code, '') AS department_code,COALESCE((SELECT name_1 FROM erp_department_list WHERE erp_department_list.code = pp_trans.department_code),'') AS department_name "
                        + " ,COALESCE(cust_code, '') AS cust_code,COALESCE((SELECT name_1 FROM ar_customer WHERE ar_customer.code = pp_trans.cust_code),'') AS cust_name, COALESCE((SELECT address FROM ar_customer WHERE ar_customer.code = pp_trans.cust_code),'') AS cust_add "
                        + " ,COALESCE(branch_code, '') AS branch_code,COALESCE((SELECT name_1 FROM erp_branch_list WHERE erp_branch_list.code = pp_trans.branch_code),'') AS branch_name "
                        + " ,COALESCE(sale_code, '') AS sale_code,COALESCE((SELECT name_1 FROM erp_user WHERE erp_user.code = pp_trans.sale_code),'') AS sale_name "
                        + " ,COALESCE(confirm_code, '') AS confirm_code,COALESCE((SELECT name_1 FROM erp_user WHERE erp_user.code = pp_trans.confirm_code),'') AS confirm_name "
                        + " ,COALESCE((SELECT wh_code from pp_trans_detail WHERE pp_trans_detail.doc_no = pp_trans.doc_no limit 1), '') AS wh_code,COALESCE((SELECT shelf_code from pp_trans_detail WHERE pp_trans_detail.doc_no = pp_trans.doc_no limit 1), '') AS shelf_code,COALESCE((SELECT name_1 FROM ic_warehouse WHERE ic_warehouse.code = (SELECT wh_code from pp_trans_detail WHERE pp_trans_detail.doc_no = pp_trans.doc_no limit 1)),'') AS wh_name "
                        + " ,COALESCE((SELECT DISTINCT car_code FROM tms_que_shipment AS tms WHERE tms.doc_no=pp_trans.tms_que_shipment_code),'') AS car_code "
                        + " ,COALESCE((SELECT DISTINCT to_char(receive_date, 'YYYY-MM-DD HH24:MI:SS') FROM tms_que_shipment AS tms WHERE tms.doc_no=pp_trans.tms_que_shipment_code),'') AS receive_date "
                        + " FROM pp_trans "
                        + ") AS pp_trans WHERE doc_no='" + __rsData1.getString("doc_no") + "' AND (wh_code IN " + "('" + __rsData1.getString("wh_code") + "')" + ") AND (shelf_code IN " + __strShelfCode + ")";

                PreparedStatement __stmt3;
                ResultSet __rsData3;
                __stmt3 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                __rsData3 = __stmt3.executeQuery();

                String[] __arrSendType = {"รับเอง", "ส่งให้"};
                String[] __arrSaleType = {"ขายเงินเชื่อ", "ขายเงินสด", "ขายสินค้าเงินสด (สินค้าบริการ)", "ขายสินค้าเงินเชื่อ (สินค้าบริการ)"};

                while (__rsData3.next()) {
                    String __strCustomerCode = __rsData3.getString("cust_code").equals("") ? "" : __rsData3.getString("cust_code");
                    String __strCustomerName = __rsData3.getString("cust_name").equals("") ? "" : __rsData3.getString("cust_name");
                    String __strDocDate = __rsData3.getString("doc_date").equals("") ? "" : __rsData3.getString("doc_date");
                    String __strSaleCode = __rsData3.getString("sale_code") != null ? __rsData3.getString("sale_code").equals("") ? "" : __rsData3.getString("sale_code") : "";
                    String __strSaleName = __rsData3.getString("sale_name") != null ? __rsData3.getString("sale_name").equals("") ? "" : __rsData3.getString("sale_name") : "";
                    String __strSaleType = __rsData3.getString("sale_type").equals("") ? "" : __rsData3.getString("sale_type");
                    String __strSendType = __rsData3.getString("send_type").equals("") ? "" : __rsData3.getString("send_type");
                    String __strCarCodeX = __rsData3.getString("car_code").equals("") ? "-" : __rsData3.getString("car_code");
                    String __strRecieveDate = __rsData3.getString("receive_date").equals("") ? "-" : __rsData3.getString("receive_date");

                    __rsHTML += "<div class='row' style='font-size: 13px;'>";

                    if (__strCustomerCode.equals("") && __strCustomerName.equals("")) {
                        __rsHTML += "<div class='col-md-6 col-sm-6 col-xs-6'><p>ลูกค้า: -</p></div>";
                    } else {
                        __rsHTML += "<div class='col-md-6 col-sm-6 col-xs-6'><p>ลูกค้า: " + __strCustomerCode + " " + __strCustomerName + "</p></div>";
                    }
                    __rsHTML += "<div class='col-md-6 col-sm-6 col-xs-6 text-right'  ><p>วันที่เอกสาร: " + __strDocDate + "</p></div>";

                    __rsHTML += "</div>";

                    __rsHTML += "<div class='row' style='font-size: 13px;'>";

                    if (__strSaleCode.equals("") && __strSaleName.equals("")) {
                        __rsHTML += "<div class='col-md-6 col-sm-6 col-xs-6'><p>พนักงานขาย: -</p></div>";
                    } else {
                        __rsHTML += "<div class='col-md-6 col-sm-6 col-xs-6'><p>พนักงานขาย: " + __strSaleCode + " " + __strSaleName + "</p></div>";
                    }
                    __rsHTML += "<div class='col-md-6 col-sm-6 col-xs-6 text-right'  ><p>ประเภทการขาย: " + __arrSaleType[Integer.parseInt(__strSaleType)] + "</p></div>";

                    __rsHTML += "</div>";

                    __rsHTML += "<div class='row' style='font-size: 13px;'>";

                    __rsHTML += "<div class='col-md-2 col-sm-2 col-xs-2 text-left'><p class=''>ประเภทการส่ง: " + __arrSendType[Integer.parseInt(__strSendType)] + "</p></div>";
                    __rsHTML += "<div class='col-md-2 col-sm-2 col-xs-2 text-left'><p>ทะเบียนรถ: " + __strCarCodeX + "</p></div>";
                    __rsHTML += "<div class='col-md-5 col-sm-5 col-xs-5 text-center'><p>วันที่และเวลาเข้ารับสินค้า: " + __routine._convertDateTime(__strRecieveDate) + "</p></div>";
                    __rsHTML += "<div class='col-md-2 col-sm-2 col-xs-2 text-right'><p>" + __rsData3.getString("wh_code") + "/" + __rsData3.getString("shelf_code") + "</p></div>";
                    __rsHTML += "<div class='col-md-1 col-sm-1 col-xs-1 text-right'  ><p>หน้า: " + "1" + "/" + "1" + "</p></div>";

                    __rsHTML += "</div>";
                }
                __stmt3.close();
                __rsData3.close();
                __rsHTML += "</div>"; // divHeader

                __strQUERY = "SELECT * FROM (SELECT ic_code,qty ,event_qty ,wh_code,unit_code,line_number, "
                        + " (SELECT name_1 from ic_inventory WHERE ic_inventory.code = pp_trans_detail.ic_code) AS item_name,"
                        + " (SELECT item_type from ic_inventory WHERE ic_inventory.code = pp_trans_detail.ic_code) AS item_type, "
                        + " (SELECT name_1 from ic_warehouse WHERE ic_warehouse.code = pp_trans_detail.wh_code) AS wh_name ,shelf_code, "
                        + " (SELECT name_1 from ic_shelf WHERE ic_shelf.code = pp_trans_detail.shelf_code and ic_shelf.whcode= pp_trans_detail.wh_code) AS shelf_name, "
                        + " (SELECT name_1 from ic_unit WHERE ic_unit.code = pp_trans_detail.unit_code)AS unit_name from pp_trans_detail "
                        + " WHERE doc_no = '" + __rsData1.getString("doc_no") + "' AND (wh_code IN " + "('" + __rsData1.getString("wh_code") + "')" + " AND shelf_code IN " + __strShelfCode + ")) AS DETAIL WHERE item_type NOT IN (3,5) ORDER BY line_number";
                PreparedStatement __stmt4;
                ResultSet __rsData4;
                __stmt4 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                __rsData4 = __stmt4.executeQuery();

                Integer __rowCount = __routine._getRowCount(conn, __strQUERY);

                if (__rowCount <= 13) {
                    if (__rowCount > 0 && __rowCount < 7) {
                        __rsHTML += "<div class='divBody' style='height: 340px;'>";
                    } else if (__rowCount <= 7) {
                        __rsHTML += "<div class='divBody' style='height: 350px;'>";
                    } else if (__rowCount <= 13) {
                        __rsHTML += "<div class='divBody' style='height: 580px;'>";
                    } else {
                        __rsHTML += "<div class='divBody'>";
                    }
                    __rsHTML += "<table class='table text-center'>";
                    __rsHTML += "<thead style='font-size: 16px;'>";
                    __rsHTML += "<tr>";
                    __rsHTML += "<td><strong>รหัส</strong></td>";
                    __rsHTML += "<td><strong>ชื่อสินค้า</strong></td>";
                    __rsHTML += "<td><strong>คลัง</strong></td>";
                    __rsHTML += "<td><strong>ที่เก็บ</strong></td>";
                    __rsHTML += "<td><strong>จำนวน</strong></td>";
                    __rsHTML += "<td><strong>รอจ่าย</strong></td>";
                    __rsHTML += "<td><strong>หน่วย</strong></td>";
                    __rsHTML += "</tr>";
                    __rsHTML += "</thead>";
                    __rsHTML += "<tbody style='font-size: 18px;'>";

                    while (__rsData4.next()) {
                        __rsHTML += "<tr>";
                        __rsHTML += "<td>" + (__rsData4.getString("ic_code")) + "</td>";
                        __rsHTML += "<td>" + (__rsData4.getString("item_name")) + "</td>";
                        __rsHTML += "<td>" + (__rsData4.getString("wh_code")) + "</td>";
                        __rsHTML += "<td>" + (__rsData4.getString("shelf_code")) + "</td>";
                        __rsHTML += "<td>" + (String.format("%.2f", __rsData4.getDouble("qty"))) + "</td>";
                        __rsHTML += "<td>" + (String.format("%.2f", __rsData4.getDouble("event_qty"))) + "</td>";
                        __rsHTML += "<td>" + (__rsData4.getString("unit_name")) + "</td>";
                        __rsHTML += "</tr>";
                    }

                    __rsHTML += "</tbody>";
                    __rsHTML += "</table>";
                    __rsHTML += "</div>"; // DivBody
                } else {
                    int _totalPage = (int) Math.ceil(__rowCount / 11);
                    Double _totalPageX = Double.parseDouble(String.valueOf(__rowCount)) / Double.parseDouble(String.valueOf("11"));
                    DecimalFormat df = new DecimalFormat();
                    df.applyPattern("0.00");
                    String[] arrTotalPages2 = String.valueOf(df.format(_totalPageX)).split("\\.");
                    if (Integer.parseInt(arrTotalPages2[1]) > 0) {
                        _totalPage += 1;
                    }
                    int _loopCount = 0;
                    int _loopRowCount = 0;
                    for (int _loop = 0; _loop < _totalPage; _loop++) {
                        if (_loopRowCount == 0) {
                            __rsHTML += "<div class='divBody' style='height: 500px;'>";
                        } else {
                            __rsHTML += "<div class='divBody' style='height: 530px;'>";
                        }
                        __rsHTML += "<table class='table text-center'>";
                        __rsHTML += "<thead style='font-size: 16px;'>";
                        __rsHTML += "<tr>";
                        __rsHTML += "<td><strong>รหัส</strong></td>";
                        __rsHTML += "<td><strong>ชื่อสินค้า</strong></td>";
                        __rsHTML += "<td><strong>คลัง</strong></td>";
                        __rsHTML += "<td><strong>ที่เก็บ</strong></td>";
                        __rsHTML += "<td><strong>จำนวน</strong></td>";
                        __rsHTML += "<td><strong>รอจ่าย</strong></td>";
                        __rsHTML += "<td><strong>หน่วย</strong></td>";
                        __rsHTML += "</tr>";
                        __rsHTML += "</thead>";
                        __rsHTML += "<tbody style='font-size: 18px;'>";

                        while (__rsData4.next()) {
                            __rsHTML += "<tr>";
                            __rsHTML += "<td>" + (__rsData4.getString("ic_code")) + "</td>";
                            __rsHTML += "<td>" + (__rsData4.getString("item_name")) + "</td>";
                            __rsHTML += "<td>" + (__rsData4.getString("wh_code")) + "</td>";
                            __rsHTML += "<td>" + (__rsData4.getString("shelf_code")) + "</td>";
                            __rsHTML += "<td>" + (String.format("%.2f", __rsData4.getDouble("qty"))) + "</td>";
                            __rsHTML += "<td>" + (String.format("%.2f", __rsData4.getDouble("event_qty"))) + "</td>";
                            __rsHTML += "<td>" + (__rsData4.getString("unit_name")) + "</td>";
                            __rsHTML += "</tr>";
                            _loopCount++;
                            _loopRowCount = __rsData4.getRow();
                            if (_loopCount == 10) {
                                _loopCount = 0;
                                break;
                            }
                        }
                        __rsHTML += "</tbody>";
                        __rsHTML += "</table>";
                        __rsHTML += "</div>"; // DivBody
                    }
                }
                __stmt4.close();
                __rsData4.close();

                __rsHTML += "<div class='divFooter' style='padding: 0 5px'>";

                __rsHTML += "<p style='font-size: 15px;'><strong>หมายเหตุ: </strong>" + __strRemark + "</p>";

                __rsHTML += "<div class='row text-center'>";

                __rsHTML += "<div class='col-md-4 col-xs-4'>";
                __rsHTML += "<img src='data:image/jpg;base64," + __strBarCodeE + "'>";
                __rsHTML += "</div>"; // col-md-4

                __rsHTML += "<div class='col-md-8 col-xs-8'>";

                __rsHTML += "<div class='row text-center' style='font-size: 10px;'>";
                __rsHTML += "<div class='col-md-4 col-xs-4'>........................................</div>";
                __rsHTML += "<div class='col-md-4 col-xs-4'>........................................</div>";
                __rsHTML += "<div class='col-md-4 col-xs-4'>........................................</div>";
                __rsHTML += "</div>"; // row

                __rsHTML += "<div class='row text-center' style='font-size: 10px; margin: 10px 0;'>";
                __rsHTML += "<div class='col-md-4 col-xs-4'>ผู้จัดเตรียมสินค้า / คลังสินค้า</div>";
                __rsHTML += "<div class='col-md-4 col-xs-4'>ผู้ตรวจเช็คสินค้า</div>";
                __rsHTML += "<div class='col-md-4 col-xs-4'>ผู้รับสินค้า(ขนส่ง/ลูกค้า)</div>";
                __rsHTML += "</div>"; // row

                __rsHTML += "<div class='row text-center' style='font-size: 10px;'>";
                __rsHTML += "<div class='col-md-4 col-xs-4'>........................................</div>";
                __rsHTML += "<div class='col-md-4 col-xs-4'>........................................</div>";
                __rsHTML += "<div class='col-md-4 col-xs-4'>........................................</div>";
                __rsHTML += "</div>"; // row

                __rsHTML += "</div>"; // col-md-8
                __rsHTML += "</div>"; // row

                __rsHTML += "<div class='row text-right'>";
                __rsHTML += "<div class='col-md-12 col-xs-12'  >";

                __rsHTML += "<p>ผู้พิมพ์:   " + __strUserCode + "   วันที่:    " + __strNewDate + "<p>";

                __rsHTML += "</div>"; // col-md-12
                __rsHTML += "</div>"; // row
                __rsHTML += "</div>"; // divFooter

                __rsHTML += "</div>";
                __Count++;
            }
        }
        __stmt1.close();
        __rsData1.close();
        conn.commit();

        __objTMP.put("data", __rsHTML);
        __objTMP.put("success", true);

        return __objTMP;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
                case "verify_data_1":
                    objResult = _verifyData1(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "verify_data_2":
                    objResult = _verifyData2(__conn, ResponseUtil.str2Json(request.getParameter("data")));
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
                case "print_data":
                    objResult = printData(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
            }

        } catch (JSONException ex) {
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

    private JSONObject printData(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objResponse = new JSONObject("{'success': false}");
        String __strQuery;
        String __strHTML = "";

        String __strRefCode = !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? param.getString("ref_code") : "";
        String __strDocNo = !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? " AND doc_no='" + param.getString("doc_no") + "' " : "";
        String __strWhCode = !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? param.getString("wh_code") : "";
        String __strShelfCode = !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? param.getString("shelf_code") : "";
        String __strCarCode = !param.isNull("car_code") && !param.getString("car_code").trim().isEmpty() ? " AND '" + param.getString("car_code") + "'=(SELECT DISTINCT replace(car_code, ' ', '') FROM tms_que_shipment AS tms WHERE tms.doc_no='" + param.getString("shipment_code") + "') " : "";
        String __strShipmentCode = !param.isNull("shipment_code") && !param.getString("shipment_code").trim().isEmpty() ? " AND tms_que_shipment_code = '" + param.getString("shipment_code") + "' " : "";

        GenBarcode genBarcode = new GenBarcode();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        __strQuery = "SELECT wh_code, doc_no, e_doc_no, b_doc_no FROM pp_trans WHERE ref_code='" + __strRefCode + "'" + __strDocNo + " AND (wh_code IN " + __strWhCode + ") AND (shelf_code IN " + __strShelfCode + ") AND is_close=0 " + __strCarCode + __strShipmentCode;

        PreparedStatement __stmt;
        ResultSet __rsData;
        __stmt = conn.prepareStatement(__strQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData = __stmt.executeQuery();

        JSONObject __objData = new JSONObject();
        __objData.put("shelf_code", __strShelfCode);

        while (__rsData.next()) {
            __strQuery = "SELECT remark FROM ic_trans WHERE doc_no='" + __strRefCode + "'";
            PreparedStatement __stmtRemark;
            ResultSet __rsDataRemark;
            __stmtRemark = conn.prepareStatement(__strQuery);
            __rsDataRemark = __stmtRemark.executeQuery();
            while (__rsDataRemark.next()) {
                String __strRemark = __rsDataRemark.getString("remark") != null ? !__rsDataRemark.getString("remark").equals("null") || !__rsDataRemark.getString("remark").isEmpty() ? __rsDataRemark.getString("remark") : "-" : "-";
                String newDate = dateFormat.format(date);
                String BAR_B_DOC_NO = javax.xml.bind.DatatypeConverter.printBase64Binary(genBarcode._barcodeByte(__rsData.getString("b_doc_no"), 0, 70));
                String BAR_E_DOC_NO = javax.xml.bind.DatatypeConverter.printBase64Binary(genBarcode._barcodeByte(__rsData.getString("e_doc_no"), 0, 70));

                __objData.put("barcode_b", BAR_B_DOC_NO);
                __objData.put("barcode_e", BAR_E_DOC_NO);
                __objData.put("remark", __strRemark);
                __objData.put("date", newDate);

                __strHTML += renderPrintBody(conn, __rsData, __objData);
            }
        }

        __objResponse.put("success", true);
        __objResponse.put("data", __strHTML);

        return __objResponse;
    }

    private String renderPrintHeader(Connection __conn, ResultSet __rsDataHead, JSONObject objData, Integer loopCount, Integer totalCount) throws Exception {
        String __strBarCodeB = objData.getString("barcode_b");
        String __strShelfCode = objData.getString("shelf_code");

        String __strQuery;
        String __strHTML = "";
        __strHTML += "<div class='divHeader' style='padding: 5px 0'>";
        __strHTML += "  <div class='row' style='font-size: 18px;'>";
        __strHTML += "      <div class='col-md-6 col-sm-6 col-xs-4'>";
        if (loopCount == 1) {
            __strHTML += "          <img src='data:image/jpg;base64," + __strBarCodeB + "'>";
        }
        __strHTML += "      </div>";
        __strHTML += "      <div class='col-md-6 col-sm-6 col-xs-8'>";
        __strHTML += "          <div class='row text-right'>";
        __strHTML += "              <div class='col-md-12 col-sm-12 col-xs-12'>";
        __strHTML += "                  <p style='font-size: 18px;'><strong>ใบจัดสินค้า</strong></p>";
        __strHTML += "                  <p>เลขที่เอกสาร: " + __rsDataHead.getString("doc_no") + "</p>";
        __strHTML += "              </div>";
        __strHTML += "          </div>";
        __strHTML += "      </div>";
        __strHTML += "  </div>";

        __strQuery = "SELECT * FROM( "
                + " SELECT  pp_trans.doc_no,pp_trans.doc_date,pp_trans.sale_type,pp_trans.ref_date,pp_trans.ref_code,pp_trans.sale_type,pp_trans.sale_code,pp_trans.send_type,pp_trans.confirm_date_time "
                + " ,COALESCE(department_code, '') AS department_code,COALESCE((SELECT name_1 FROM erp_department_list WHERE erp_department_list.code = pp_trans.department_code),'') AS department_name "
                + " ,COALESCE(cust_code, '') AS cust_code,COALESCE((SELECT name_1 FROM ar_customer WHERE ar_customer.code = pp_trans.cust_code),'') AS cust_name, COALESCE((SELECT address FROM ar_customer WHERE ar_customer.code = pp_trans.cust_code),'') AS cust_add "
                + " ,COALESCE(branch_code, '') AS branch_code,COALESCE((SELECT name_1 FROM erp_branch_list WHERE erp_branch_list.code = pp_trans.branch_code),'') AS branch_name "
                + " ,COALESCE(sale_code, '') AS sale_code,COALESCE((SELECT name_1 FROM erp_user WHERE erp_user.code = pp_trans.sale_code),'') AS sale_name "
                + " ,COALESCE(confirm_code, '') AS confirm_code,COALESCE((SELECT name_1 FROM erp_user WHERE erp_user.code = pp_trans.confirm_code),'') AS confirm_name "
                + " ,COALESCE((SELECT wh_code from pp_trans_detail WHERE pp_trans_detail.doc_no = pp_trans.doc_no limit 1), '') AS wh_code,COALESCE((SELECT shelf_code from pp_trans_detail WHERE pp_trans_detail.doc_no = pp_trans.doc_no limit 1), '') AS shelf_code,COALESCE((SELECT name_1 FROM ic_warehouse WHERE ic_warehouse.code = (SELECT wh_code from pp_trans_detail WHERE pp_trans_detail.doc_no = pp_trans.doc_no limit 1)),'') AS wh_name "
                + " ,COALESCE((SELECT DISTINCT car_code FROM tms_que_shipment AS tms WHERE tms.doc_no=pp_trans.tms_que_shipment_code),'') AS car_code "
                + " ,COALESCE((SELECT DISTINCT to_char(receive_date, 'YYYY-MM-DD HH24:MI:SS') FROM tms_que_shipment AS tms WHERE tms.doc_no=pp_trans.tms_que_shipment_code),'') AS receive_date "
                + " FROM pp_trans "
                + ") AS pp_trans WHERE doc_no='" + __rsDataHead.getString("doc_no") + "' AND (wh_code IN " + "('" + __rsDataHead.getString("wh_code") + "')" + ") AND (shelf_code IN " + __strShelfCode + ")";

        PreparedStatement __stmt;
        ResultSet __rsData;
        __stmt = __conn.prepareStatement(__strQuery);
        __rsData = __stmt.executeQuery();

        String[] __arrSendType = {"รับเอง", "ส่งให้"};
        String[] __arrSaleType = {"ขายเงินเชื่อ", "ขายเงินสด", "ขายสินค้าเงินสด (สินค้าบริการ)", "ขายสินค้าเงินเชื่อ (สินค้าบริการ)"};

        while (__rsData.next()) {
            String __strCustomerCode = __rsData.getString("cust_code") != null ? !__rsData.getString("cust_code").isEmpty() ? __rsData.getString("cust_code") : "" : "";
            String __strCustomerName = __rsData.getString("cust_name") != null ? !__rsData.getString("cust_name").isEmpty() ? __rsData.getString("cust_name") : "" : "";
            String __strDocDate = __rsData.getString("doc_date") != null ? !__rsData.getString("doc_date").isEmpty() ? __rsData.getString("doc_date") : "" : "";
            String __strSaleCode = __rsData.getString("sale_code") != null ? !__rsData.getString("sale_code").isEmpty() ? __rsData.getString("sale_code") : "" : "";
            String __strSaleName = __rsData.getString("sale_name") != null ? !__rsData.getString("sale_name").isEmpty() ? __rsData.getString("sale_name") : "" : "";
            String __strSaleType = __rsData.getString("sale_type") != null ? !__rsData.getString("sale_type").isEmpty() ? __rsData.getString("sale_type") : "0" : "0";
            String __strSendType = __rsData.getString("send_type") != null ? !__rsData.getString("send_type").isEmpty() ? __rsData.getString("send_type") : "0" : "0";
            String __strCarCode = __rsData.getString("car_code") != null ? !__rsData.getString("car_code").isEmpty() ? __rsData.getString("car_code") : "-" : "-";
            String __strRecieveDate = __rsData.getString("receive_date") != null ? !__rsData.getString("receive_date").isEmpty() ? __rsData.getString("receive_date") : "-" : "-";

            __strHTML += "<div class='row' style='font-size: 13px; margin-top: 5px;'>";
            __strHTML += "  <div class='col-md-6 col-sm-6 col-xs-6'>";
            __strHTML += "      <p><strong>ลูกค้า:</strong> " + (!__strCustomerCode.equals("") && !__strCustomerCode.equals("") ? __strCustomerCode + " " + __strCustomerName : "-") + "</p>";
            __strHTML += "  </div>";
            __strHTML += "  <div class='col-md-6 col-sm-6 col-xs-6 text-right'>";
            __strHTML += "      <p><strong>วันที่เอกสาร:</strong> " + __strDocDate + "</p>";
            __strHTML += "  </div>";
            __strHTML += "</div>";

            __strHTML += "<div class='row' style='font-size: 13px;'>";
            __strHTML += "  <div class='col-md-6 col-sm-6 col-xs-6'>";
            __strHTML += "      <p><strong>พนักงานขาย:</strong> " + (!__strSaleCode.equals("") && !__strSaleName.equals("") ? __strSaleCode + " " + __strSaleName : "-") + "</p>";
            __strHTML += "  </div>";
            __strHTML += "  <div class='col-md-6 col-sm-6 col-xs-6 text-right'>";
            __strHTML += "      <p><strong>ประเภทการขาย:</strong> " + __arrSaleType[Integer.parseInt(__strSaleType)] + "</p>";
            __strHTML += "  </div>";
            __strHTML += "</div>";

            __strHTML += "<div class='row' style='font-size: 13px;'>";
            __strHTML += "  <div class='col-md-2 col-sm-2 col-xs-2 text-left'>";
            __strHTML += "      <p><strong>ประเภทการส่ง:</strong> " + __arrSendType[Integer.parseInt(__strSendType)] + "</p>";
            __strHTML += "  </div>";
            __strHTML += "  <div class='col-md-2 col-sm-2 col-xs-2 text-left'>";
            __strHTML += "      <p><strong>ทะเบียนรถ:</strong> " + __strCarCode + "</p>";
            __strHTML += "  </div>";
            __strHTML += "  <div class='col-md-5 col-sm-5 col-xs-5 text-center'>";
            __strHTML += "      <p><strong>วันที่และเวลาเข้ารับสินค้า:</strong> " + __routine._convertDateTime(__strRecieveDate) + "</p>";
            __strHTML += "  </div>";
            __strHTML += "  <div class='col-md-2 col-sm-2 col-xs-2 text-right'>";
            __strHTML += "      <p>" + __rsData.getString("wh_code") + "/" + __rsData.getString("shelf_code") + "</p>";
            __strHTML += "  </div>";
            __strHTML += "  <div class='col-md-1 col-sm-1 col-xs-1 text-right'>";
            __strHTML += "      <p>หน้า: " + loopCount + "/" + totalCount + "</p>";
            __strHTML += "  </div>";
            __strHTML += "</div>";

            __strHTML += "</div>"; // close divHeader
        }

        return __strHTML;
    }

    private String renderPrintBody(Connection __conn, ResultSet __rsDataHead, JSONObject objData) throws Exception {
        String __strQuery;
        String __strHTML = "";

        String __strShelfCode = objData.getString("shelf_code");

        __strQuery = "SELECT * FROM (SELECT ic_code,qty ,event_qty ,wh_code,unit_code,line_number, "
                + " (SELECT name_1 from ic_inventory WHERE ic_inventory.code = pp_trans_detail.ic_code) AS item_name,"
                + " (SELECT item_type from ic_inventory WHERE ic_inventory.code = pp_trans_detail.ic_code) AS item_type, "
                + " (SELECT name_1 from ic_warehouse WHERE ic_warehouse.code = pp_trans_detail.wh_code) AS wh_name ,shelf_code, "
                + " (SELECT name_1 from ic_shelf WHERE ic_shelf.code = pp_trans_detail.shelf_code and ic_shelf.whcode= pp_trans_detail.wh_code) AS shelf_name, "
                + " (SELECT name_1 from ic_unit WHERE ic_unit.code = pp_trans_detail.unit_code)AS unit_name from pp_trans_detail "
                + " WHERE doc_no = '" + __rsDataHead.getString("doc_no") + "' AND (wh_code IN " + "('" + __rsDataHead.getString("wh_code") + "')" + " AND shelf_code IN " + __strShelfCode + ")) AS DETAIL WHERE item_type NOT IN (3,5) ORDER BY line_number";
        PreparedStatement __stmt;
        ResultSet __rsData;
        __stmt = __conn.prepareStatement(__strQuery);
        __rsData = __stmt.executeQuery();

        int __rowCount = __routine._getRowCount(__conn, __strQuery);
        int __divideValue = 9;
        int __itemShowValue = 9;

        Integer totalCount1 = (int) Math.ceil(__rowCount / __divideValue);
        Double totalCount2 = Double.parseDouble(String.valueOf(__rowCount)) / Double.parseDouble(String.valueOf(__divideValue));
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("0.00");
        String[] arrTotalCount2 = String.valueOf(df.format(totalCount2)).split("\\.");
        if (Integer.parseInt(arrTotalCount2[1]) > 0) {
            totalCount1 += 1;
        }
        Integer loopCount = 0;
        for (int loop = 0; loop < totalCount1; loop++) {
            __strHTML += "<div class='content-print-layout'>";
            __strHTML += renderPrintHeader(__conn, __rsDataHead, objData, (loop + 1), totalCount1); // header
            __strHTML += "<div class='divBody' style='height: 340px;'>";
            __strHTML += "  <table class='table table-condensed text-center' style='margin-bottom: 0;'>";
            __strHTML += "      <thead style='font-size: 16px;'>"
                    + "             <tr>"
                    + "                 <td><strong>รหัส</strong></td>"
                    + "                 <td><strong>ชื่อสินค้า</strong></td>"
                    + "                 <td><strong>คลัง</strong></td>"
                    + "                 <td><strong>ที่เก็บ</strong></td>"
                    + "                 <td><strong>จำนวน</strong></td>"
                    + "                 <td><strong>รอจ่าย</strong></td>"
                    + "                 <td><strong>หน่วย</strong></td>"
                    + "             </tr>";
            __strHTML += "      </thead>";
            __strHTML += "      <tbody style='font-size: 16px;'>";

            while (__rsData.next()) {
                String __strItemCode = __rsData.getString("ic_code") != null ? !__rsData.getString("ic_code").isEmpty() ? __rsData.getString("ic_code") : "" : "";
                String __strItemName = __rsData.getString("item_name") != null ? !__rsData.getString("item_name").isEmpty() ? __rsData.getString("item_name") : "" : "";
                String __strWhCode = __rsData.getString("wh_code") != null ? !__rsData.getString("wh_code").isEmpty() ? __rsData.getString("wh_code") : "" : "";
                String __strShelfCodeX = __rsData.getString("shelf_code") != null ? !__rsData.getString("shelf_code").isEmpty() ? __rsData.getString("shelf_code") : "" : "";
                String __strQTY = __rsData.getString("qty") != null ? !__rsData.getString("qty").isEmpty() ? __rsData.getString("qty") : "0.00" : "0.00";
                String __strEventQTY = __rsData.getString("event_qty") != null ? !__rsData.getString("event_qty").isEmpty() ? __rsData.getString("event_qty") : "0.00" : "0.00";
                String __strUnitName = __rsData.getString("unit_name") != null ? !__rsData.getString("unit_name").isEmpty() ? __rsData.getString("unit_name") : "" : "";

                __strHTML += "      <tr>";
                __strHTML += "          <td>" + __strItemCode + "</td>";
                __strHTML += "          <td>" + __strItemName + "</td>";
                __strHTML += "          <td>" + __strWhCode + "</td>";
                __strHTML += "          <td>" + __strShelfCodeX + "</td>";
                __strHTML += "          <td>" + String.format("%,.2f", Float.parseFloat(__strQTY)) + "</td>";
                __strHTML += "          <td>" + String.format("%,.2f", Float.parseFloat(__strEventQTY)) + "</td>";
                __strHTML += "          <td>" + __strUnitName + "</td>";
                __strHTML += "      </tr>";

                loopCount++;
                if (loopCount == __itemShowValue) {
                    loopCount = 0;
                    break;
                }
            }

            __strHTML += "      </tbody>";
            __strHTML += "  </table>";
            __strHTML += "</div>";

            if ((loop + 1) == totalCount1) {
                __strHTML += renderPrintFooter(objData); // footer
            }
            __strHTML += "</div>";
        }
        return __strHTML;
    }

    private String renderPrintFooter(JSONObject objData) throws Exception {
        String __strRemark = objData.getString("remark");
        String BAR_E_DOC_NO = objData.getString("barcode_e");
        String newDate = objData.getString("date");

        String __strHTML = "";
        __strHTML += "<div class='divFooter' style='padding: 0 5px'>";
        __strHTML += "  <div class='row' style='font-size: 15px;'>";
        __strHTML += "      <div class='col-lg-12 col-md-12 col-sm-12 col-xs-12'>";
        __strHTML += "          <p><strong>หมายเหตุ: </strong>" + __strRemark + "</p>";
        __strHTML += "      </div>";
        __strHTML += "  </div>";

        __strHTML += "  <div class='row text-center'>";
        __strHTML += "      <div class='col-lg-4 col-md-4 col-sm-4 col-xs-4'>";
        __strHTML += "          <img src='data:image/jpg;base64," + BAR_E_DOC_NO + "'>";
        __strHTML += "      </div>";
        __strHTML += "      <div class='col-lg-8 col-md-8 col-sm-8 col-xs-8'>";
        __strHTML += "          <div class='row text-center' style='font-size: 10px; margin: 10px 0;'>";
        __strHTML += "              <div class='col-lg-4 col-md-4 col-sm-4 col-xs-4'>";
        __strHTML += "                  <p>........................................</p>";
        __strHTML += "              </div>";
        __strHTML += "              <div class='col-lg-4 col-md-4 col-sm-4 col-xs-4'>";
        __strHTML += "                  <p>........................................</p>";
        __strHTML += "              </div>";
        __strHTML += "              <div class='col-lg-4 col-md-4 col-sm-4 col-xs-4'>";
        __strHTML += "                  <p>........................................</p>";
        __strHTML += "              </div>";
        __strHTML += "          </div>";
        __strHTML += "          <div class='row text-center' style='font-size: 10px; margin: 10px 0;'>";
        __strHTML += "              <div class='col-lg-4 col-md-4 col-sm-4 col-xs-4'>";
        __strHTML += "                  <p><strong>ผู้จัดเตรียมสินค้า / คลังสินค้า</strong></p>";
        __strHTML += "              </div>";
        __strHTML += "              <div class='col-lg-4 col-md-4 col-sm-4 col-xs-4'>";
        __strHTML += "                  <p><strong>ผู้ตรวจเช็คสินค้า</strong></p>";
        __strHTML += "              </div>";
        __strHTML += "              <div class='col-lg-4 col-md-4 col-sm-4 col-xs-4'>";
        __strHTML += "                  <p><strong>ผู้รับสินค้า(ขนส่ง/ลูกค้า)</strong></p>";
        __strHTML += "              </div>";
        __strHTML += "          </div>";
        __strHTML += "          <div class='row text-center' style='font-size: 10px; margin: 10px 0;'>";
        __strHTML += "              <div class='col-lg-4 col-md-4 col-sm-4 col-xs-4'>";
        __strHTML += "                  <p>........................................</p>";
        __strHTML += "              </div>";
        __strHTML += "              <div class='col-lg-4 col-md-4 col-sm-4 col-xs-4'>";
        __strHTML += "                  <p>........................................</p>";
        __strHTML += "              </div>";
        __strHTML += "              <div class='col-lg-4 col-md-4 col-sm-4 col-xs-4'>";
        __strHTML += "                  <p>........................................</p>";
        __strHTML += "              </div>";
        __strHTML += "          </div>";
        __strHTML += "      </div>";
        __strHTML += "  </div>";
        __strHTML += "  <div class='row text-right'>";
        __strHTML += "      <p><strong>ผู้พิมพ์:</strong>   " + __strUserCode + "   วันที่:    " + newDate + "<p>";
        __strHTML += "  </div>";
        __strHTML += "</div>";
        return __strHTML;
    }

}
