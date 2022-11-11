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
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ResponseUtil;
import utils._global;
import utils._routine;

/**
 *
 * @author BeamMary
 */
@WebServlet(name = "export-file-1", urlPatterns = {"/export-file-1"})
public class exportFile extends HttpServlet {

    private final _routine __routine = new _routine();
    private String __strDatabaseName;
    private String __strProviderCode;
    private String __strUserCode;
    private String __strSystemID;
    private String __strSessionID;

    private XSSFWorkbook __workBook;

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

        String __strFromDate = "";
        if (request.getParameter("from_date") != null && !request.getParameter("from_date").isEmpty()) {
            __strFromDate = request.getParameter("from_date");
        }

        String __strToDate = "";
        if (request.getParameter("to_date") != null && !request.getParameter("to_date").isEmpty()) {
            __strToDate = request.getParameter("to_date");
        }

        Connection __conn = null;
        try {
            __conn = __routine._connect(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));
            response.setContentType("application/force-download");
            response.setContentType("application/octet-stream");
            response.setContentType("application/download");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=report " + __strFromDate + "_" + __strToDate + ".xlsx");
            switch (__strActionName) {
                case "report_1":
                    _exportFileReport1(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "report_2":
                    _exportFileReport2(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "report_3":
                    _exportFileReport3(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
            }
        } catch (SQLException | JSONException | IOException ex) {
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
                    __workBook.write(response.getOutputStream());
                    __workBook.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
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

    private void _exportFileReport1(Connection conn, JSONObject param) throws SQLException, Exception, IOException {
        String __strBranchCode = !param.isNull("branch_code") && !param.getString("branch_code").trim().isEmpty() ? param.getString("branch_code") : "";
        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code").equals("all") ? "คลัง ทั้งหมด" : "คลัง " + param.getString("group_code") : "";
        String __strFromDate = !param.isNull("from_date") && !param.getString("from_date").trim().isEmpty() ? param.getString("from_date") : "";
        String __strToDate = !param.isNull("to_date") && !param.getString("to_date").trim().isEmpty() ? param.getString("to_date") : "";

        String __strQUERY;
        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("branch_code") && !param.getString("branch_code").trim().isEmpty() ? " AND (UPPER(branch_code)= '" + param.getString("branch_code").toUpperCase() + "') " : "";
        __strQueryExtends += !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND (wh_code IN " + param.getString("wh_code") + ") " : "";
        __strQueryExtends += !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND (shelf_code IN " + param.getString("shelf_code") + ") " : "";

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
        __workBook = new XSSFWorkbook();
        Sheet __sheet = __workBook.createSheet("รายงานสินค้าจัดขาด");

        conn.setAutoCommit(false);
        __strQUERY = "SELECT code,name_1 FROM erp_branch_list WHERE code='" + __strBranchCode + "' ";
        PreparedStatement __stmtGetBranch;
        ResultSet __rsDataBranch;
        __stmtGetBranch = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsDataBranch = __stmtGetBranch.executeQuery();

        PrintSetup printSetup = __sheet.getPrintSetup();
        printSetup.setLandscape(true);

        // กำหนดตัวหนังสือ
        XSSFFont __font = __workBook.createFont();
        __font.setFontHeightInPoints((short) 11);
        __font.setFontName("Tahoma");
        __font.setColor(IndexedColors.BLACK.getIndex());
        __font.setBold(true);
        __font.setItalic(false);

        CellStyle rightAligned = __workBook.createCellStyle();
        rightAligned.setAlignment(CellStyle.ALIGN_RIGHT);

        CellStyle centerAligned = __workBook.createCellStyle();
        centerAligned.setAlignment(CellStyle.ALIGN_CENTER);

        CellStyle leftAligned = __workBook.createCellStyle();
        leftAligned.setAlignment(CellStyle.ALIGN_LEFT);

        CellStyle __fontbold = __workBook.createCellStyle();
        __fontbold.setFont(__font);

        // กำหนดหัวเอกสาร
        Row __row0 = __sheet.createRow(1);
        Cell __cellTxtLabel = __row0.createCell(0);
        __cellTxtLabel.setCellValue("รายงานวิเคราะห์การจ่ายสินค้าขาด");
        __cellTxtLabel.setCellStyle(__fontbold);

        Row __row2 = __sheet.createRow(2);
        Cell __cell2 = __row2.createCell(0);
        while (__rsDataBranch.next()) {
            __cell2.setCellValue("สาขา " + __rsDataBranch.getString("code") + " ~ " + __rsDataBranch.getString("name_1"));
        }
        __cell2.setCellStyle(__fontbold);
        __stmtGetBranch.close();
        __rsDataBranch.close();

        Row __row3 = __sheet.createRow(3);
        Cell __cell3 = __row3.createCell(0);
        __cell3.setCellValue(__strGroupCode);
        __cell3.setCellStyle(__fontbold);

        Row __row4 = __sheet.createRow(4);
        Cell __cell4 = __row4.createCell(0);
        __cell4.setCellValue("วันที่ " + __routine._convertDate(__strFromDate) + "  ถึงวันที่ " + __routine._convertDate(__strToDate));
        __cell4.setCellStyle(__fontbold);

        CellStyle __cellStyle1 = __workBook.createCellStyle();
        __cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        __cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
        __cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
        __cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        __cellStyle1.setFillForegroundColor(IndexedColors.YELLOW.index);
        __cellStyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        __cellStyle1.setFont(__font);

        Row row5 = __sheet.createRow(6);
        Cell cell50 = row5.createCell(0);
        cell50.setCellValue("พื้นที่เก็บสินค้า");
        cell50.setCellStyle(__cellStyle1);
        Cell cell51 = row5.createCell(1);
        cell51.setCellValue("วันที่");
        cell51.setCellStyle(__cellStyle1);
        Cell cell52 = row5.createCell(2);
        cell52.setCellValue("วันที่ใบจัด");
        cell52.setCellStyle(__cellStyle1);
        Cell cell53 = row5.createCell(3);
        cell53.setCellValue("เลขที่ใบจัด");
        cell53.setCellStyle(__cellStyle1);
        Cell cell54 = row5.createCell(4);
        cell54.setCellValue("รหัสสินค้า");
        cell54.setCellStyle(__cellStyle1);
        Cell cell55 = row5.createCell(5);
        cell55.setCellValue("ชื่อสินค้า");
        cell55.setCellStyle(__cellStyle1);
        Cell cell56 = row5.createCell(6);
        cell56.setCellValue("ยอดสั่งจัด");
        cell56.setCellStyle(__cellStyle1);
        Cell cell57 = row5.createCell(7);
        cell57.setCellValue("จำนวนที่จัดได้");
        cell57.setCellStyle(__cellStyle1);
        Cell cell58 = row5.createCell(8);
        cell58.setCellValue("จำนวนที่ขาด");
        cell58.setCellStyle(__cellStyle1);
        Cell cell59 = row5.createCell(9);
        cell59.setCellValue("สาเหตุที่จัดขาด");
        cell59.setCellStyle(__cellStyle1);

        __strQUERY = "SELECT wh_code,shelf_code,ref_date,doc_date,doc_no,ic_code,qty,event_qty,(qty-event_qty)AS total_qty "
                + " ,COALESCE((SELECT name_1 FROM ic_warehouse WHERE code=wh_code), '') AS wh_name "
                + " ,COALESCE((SELECT name_1 FROM ic_shelf WHERE whcode=wh_code and code=shelf_code), '') AS sh_name "
                + " ,COALESCE((SELECT name_1 FROM ic_inventory WHERE ic_inventory.code=pp_trans_detail.ic_code), '') AS item_name "
                + " ,COALESCE((SELECT name FROM tms_reason WHERE tms_reason.code=pp_trans_detail.remark),'') AS remark "
                + " FROM pp_trans_detail " + __strQueryExtends + " AND (qty-event_qty)>0"
                + " AND (SELECT is_confirm FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no)=1 "
                + " ORDER BY doc_date DESC,doc_no ";

        PreparedStatement __stmtGetDetail;
        ResultSet __rsDataDetail;
        __stmtGetDetail = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsDataDetail = __stmtGetDetail.executeQuery();

        Integer __row = 7;
        while (__rsDataDetail.next()) {
            String __strWhName = __rsDataDetail.getString("wh_name").equals("") ? "-" : __rsDataDetail.getString("wh_name");
            String __strShelfName = __rsDataDetail.getString("sh_name").equals("") ? "-" : __rsDataDetail.getString("sh_name");
            String __strDocDate = __rsDataDetail.getString("doc_date").equals("") ? "-" : __rsDataDetail.getString("doc_date");
            String __strRefDate = __rsDataDetail.getString("ref_date").equals("") ? "-" : __rsDataDetail.getString("ref_date");
            String __strDocNo = __rsDataDetail.getString("doc_no").equals("") ? "-" : __rsDataDetail.getString("doc_no");
            String __strItemCode = __rsDataDetail.getString("ic_code").equals("") ? "-" : __rsDataDetail.getString("ic_code");
            String __strItemName = __rsDataDetail.getString("item_name").equals("") ? "-" : __rsDataDetail.getString("item_name");
            String __strQTY = __rsDataDetail.getString("qty").equals("") ? "-" : String.format("%,.2f", Float.parseFloat(__rsDataDetail.getString("qty")));
            String __strEventQTY = __rsDataDetail.getString("event_qty").equals("") ? "-" : String.format("%,.2f", Float.parseFloat(__rsDataDetail.getString("event_qty")));
            String __strTotalQTY = __rsDataDetail.getString("total_qty").equals("") ? "-" : String.format("%,.2f", Float.parseFloat(__rsDataDetail.getString("total_qty")));
            String __strRemark = __rsDataDetail.getString("remark").equals("") ? "-" : __rsDataDetail.getString("remark");

            Row row = __sheet.createRow(__row);
            Cell cellx = row.createCell(0);
            cellx.setCellValue(__strWhName + " ~ " + __strShelfName);

            Cell cellx1 = row.createCell(1);
            cellx1.setCellValue(__routine._convertDate(__strRefDate));

            Cell cellx2 = row.createCell(2);
            cellx2.setCellValue(__routine._convertDate(__strDocDate));

            Cell cellx3 = row.createCell(3);
            cellx3.setCellValue(__strDocNo);

            Cell cellx4 = row.createCell(4);
            cellx4.setCellValue(__strItemCode);

            Cell cellx5 = row.createCell(5);
            cellx5.setCellValue(__strItemName);

            Cell cellx6 = row.createCell(6);
            cellx6.setCellValue(__strQTY);
            cellx6.setCellStyle(rightAligned);

            Cell cellx7 = row.createCell(7);
            cellx7.setCellValue(__strEventQTY);
            cellx7.setCellStyle(rightAligned);

            Cell cellx8 = row.createCell(8);
            cellx8.setCellValue(__strTotalQTY);
            cellx8.setCellStyle(rightAligned);

            Cell cellx9 = row.createCell(9);
            cellx9.setCellValue(__strRemark);

            __row++;
        }

        __stmtGetDetail.close();
        __rsDataDetail.close();

        for (int colNum = 0; colNum < row5.getLastCellNum(); colNum++) {
            __workBook.getSheetAt(0).autoSizeColumn(colNum);
        }

        conn.commit();

    }

    private void _exportFileReport2(Connection conn, JSONObject param) throws SQLException, Exception, IOException {
        conn.setAutoCommit(false);

        String __strBranchCode = !param.isNull("branch_code") && !param.getString("branch_code").trim().isEmpty() ? param.getString("branch_code") : "";
        String __strGroupCode = !param.isNull("group_code") && !param.getString("group_code").trim().isEmpty() ? param.getString("group_code").equals("all") ? "คลัง ทั้งหมด" : "คลัง " + param.getString("group_code") : "";
        String __strFromDate = !param.isNull("from_date") && !param.getString("from_date").trim().isEmpty() ? param.getString("from_date") : "";
        String __strToDate = !param.isNull("to_date") && !param.getString("to_date").trim().isEmpty() ? param.getString("to_date") : "";

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("branch_code") && !param.getString("branch_code").trim().isEmpty() ? " AND (UPPER(branch_code)= '" + param.getString("branch_code").toUpperCase() + "') " : "";
        __strQueryExtends += !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND ((wh_code IN " + param.getString("wh_code") + ") " : "";
        __strQueryExtends += !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND (shelf_code IN " + param.getString("shelf_code") + ")) " : "";

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

        __workBook = new XSSFWorkbook();
        Sheet __sheet = __workBook.createSheet("PAGE " + 1);

        PrintSetup printSetup = __sheet.getPrintSetup();
        printSetup.setLandscape(true);

        // กำหนดตัวหนังสือ
        XSSFFont __font = __workBook.createFont();
        __font.setFontHeightInPoints((short) 11);
        __font.setFontName("Tahoma");
        __font.setColor(IndexedColors.BLACK.getIndex());
        __font.setBold(true);
        __font.setItalic(false);

        CellStyle rightAligned = __workBook.createCellStyle();
        rightAligned.setAlignment(CellStyle.ALIGN_RIGHT);

        CellStyle centerAligned = __workBook.createCellStyle();
        centerAligned.setAlignment(CellStyle.ALIGN_CENTER);

        CellStyle leftAligned = __workBook.createCellStyle();
        leftAligned.setAlignment(CellStyle.ALIGN_LEFT);

        CellStyle __fontbold = __workBook.createCellStyle();
        __fontbold.setFont(__font);

        __sheet.addMergedRegion(new CellRangeAddress(6, 6, 4, 5));
        __sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));
        __sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 5));
        __sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 5));
        __sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 5));
        __sheet.addMergedRegion(new CellRangeAddress(6, 7, 0, 0));
        __sheet.addMergedRegion(new CellRangeAddress(6, 7, 1, 1));
        __sheet.addMergedRegion(new CellRangeAddress(6, 7, 2, 2));
        __sheet.addMergedRegion(new CellRangeAddress(6, 7, 3, 3));
        __sheet.addMergedRegion(new CellRangeAddress(6, 7, 6, 6));
        __sheet.addMergedRegion(new CellRangeAddress(6, 7, 8, 8));
        __sheet.addMergedRegion(new CellRangeAddress(6, 7, 9, 9));

        // กำหนดหัวเอกสาร
        Row __row0 = __sheet.createRow(1);
        Cell __cellTxtLabel = __row0.createCell(0);
        __cellTxtLabel.setCellValue("รายงานระยะเวลาในการจัดสินค้าและส่งมอบสินค้า");
        __cellTxtLabel.setCellStyle(__fontbold);

        Row __row2 = __sheet.createRow(2);
        Cell __cell2 = __row2.createCell(0);

        String __strQUERY = "SELECT code,name_1 FROM erp_branch_list WHERE code='" + __strBranchCode + "' ";
        PreparedStatement __stmtGetBranch;
        ResultSet __rsDataBranch;
        __stmtGetBranch = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsDataBranch = __stmtGetBranch.executeQuery();

        while (__rsDataBranch.next()) {
            __cell2.setCellValue("สาขา " + __rsDataBranch.getString("code") + " ~ " + __rsDataBranch.getString("name_1"));
        }
        __cell2.setCellStyle(__fontbold);
        __stmtGetBranch.close();
        __rsDataBranch.close();

        Row __row3 = __sheet.createRow(3);
        Cell __cell3 = __row3.createCell(0);
        __cell3.setCellValue(__strGroupCode);
        __cell3.setCellStyle(__fontbold);

        Row __row4 = __sheet.createRow(4);
        Cell __cell4 = __row4.createCell(0);
        __cell4.setCellValue("วันที่ " + __strFromDate + "  ถึงวันที่ " + __strToDate);
        __cell4.setCellStyle(__fontbold);

        CellStyle cellStyle1 = __workBook.createCellStyle();
        cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setFillForegroundColor(IndexedColors.YELLOW.index);
        cellStyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle1.setFont(__font);

        CellStyle cellcenter = __workBook.createCellStyle();
        cellcenter.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellcenter.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellcenter.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellcenter.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellcenter.setFillForegroundColor(IndexedColors.YELLOW.index);
        cellcenter.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellcenter.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
        cellcenter.setVerticalAlignment(VerticalAlignment.CENTER);
        cellcenter.setFont(__font);

        Row row5 = __sheet.createRow(6);
        Cell cell50 = row5.createCell(0);
        cell50.setCellValue("คลัง");
        cell50.setCellStyle(cellcenter);
        Cell cell51 = row5.createCell(1);
        cell51.setCellValue("พื้นที่");
        cell51.setCellStyle(cellcenter);
        Cell cell52 = row5.createCell(2);
        cell52.setCellValue("วันที่ใบจัด");
        cell52.setCellStyle(cellcenter);
        Cell cell53 = row5.createCell(3);
        cell53.setCellValue("เลขที่ใบจัด");
        cell53.setCellStyle(cellcenter);
        Cell cell54 = row5.createCell(4);

        cell54.setCellValue("เวลา");
        cell54.setCellStyle(cellcenter);
        Cell cell55 = row5.createCell(5);
        cell55.setCellStyle(cellcenter);

        Cell cell57 = row5.createCell(6);
        cell57.setCellValue("ระยะเวลาที่จัด(ชม. - นาที)");
        cell57.setCellStyle(cellcenter);

        Cell cell58 = row5.createCell(7);
        cell58.setCellValue("เวลา");
        cell58.setCellStyle(cellcenter);

        Cell cell59 = row5.createCell(8);
        cell59.setCellValue("ระยะเวลาส่งมอบ(ชม. - นาที)");
        cell59.setCellStyle(cellcenter);

        Row row7 = __sheet.createRow(7);
        Cell cell70 = row7.createCell(0);
        cell70.setCellStyle(cellcenter);
        Cell cell71 = row7.createCell(1);
        cell71.setCellStyle(cellcenter);
        Cell cell72 = row7.createCell(2);
        cell72.setCellStyle(cellcenter);
        Cell cell73 = row7.createCell(3);
        cell73.setCellStyle(cellcenter);
        Cell cell74 = row7.createCell(4);
        cell74.setCellValue("เริ่มจัด");
        cell74.setCellStyle(cellcenter);
        Cell cell75 = row7.createCell(5);
        cell75.setCellValue("จัดเสร็จ");
        cell75.setCellStyle(cellcenter);
        Cell cell76 = row7.createCell(6);
        cell76.setCellStyle(cellcenter);
        Cell cell77 = row7.createCell(7);
        cell77.setCellValue("ส่งมอบ (ปิดใบจัด)");
        cell77.setCellStyle(cellcenter);
        Cell cell78 = row7.createCell(8);
        cell78.setCellStyle(cellcenter);

        __strQUERY = "SELECT * \n"
                + " ,(CASE WHEN to_timestamp(confirm_date_time, 'YYYY-MM-DD HH24:MI')::timestamp without time zone <> to_timestamp(create_date_time_now, 'YYYY-MM-DD HH24:MI')::timestamp without time zone THEN true ELSE false END) AS is_confirm \n"
                + " ,(CASE WHEN to_timestamp(confirm_date_time, 'YYYY-MM-DD HH24:MI')::timestamp without time zone > to_timestamp(start_date, 'YYYY-MM-DD HH24:MI')::timestamp without time zone THEN to_char((to_timestamp(confirm_date_time, 'YYYY-MM-DD HH24:MI') - to_timestamp(start_date, 'YYYY-MM-DD HH24:MI')), 'DD-HH24-MI') ELSE to_char((to_timestamp(start_date, 'YYYY-MM-DD HH24:MI') - to_timestamp(confirm_date_time, 'YYYY-MM-DD HH24:MI')), 'DD-HH24-MI') END) AS packing_time \n"
                + " ,(CASE WHEN to_timestamp(close_time, 'YYYY-MM-DD HH24:MI')::timestamp without time zone > to_timestamp(start_date, 'YYYY-MM-DD HH24:MI')::timestamp without time zone THEN to_char((to_timestamp(close_time, 'YYYY-MM-DD HH24:MI') - to_timestamp(start_date, 'YYYY-MM-DD HH24:MI')), 'DD-HH24-MI') ELSE to_char((to_timestamp(start_date, 'YYYY-MM-DD HH24:MI') - to_timestamp(close_time, 'YYYY-MM-DD HH24:MI')), 'DD-HH24-MI') END) AS sended_time \n"
                + " FROM (\n"
                + " SELECT wh_code,shelf_code,doc_no \n"
                + " ,COALESCE((SELECT name_1 FROM ic_warehouse WHERE code=wh_code), '') AS wh_name \n"
                + " ,COALESCE((SELECT name_1 FROM ic_shelf WHERE whcode=wh_code and code=shelf_code), '') AS sh_name \n"
                + " ,to_char(doc_date, 'YYYY-MM-DD') AS doc_date \n"
                + " ,COALESCE((SELECT to_char(create_datetime, 'YYYY-MM-DD HH24:MI:SS') FROM pp_trans_log WHERE pp_trans_log.ref_code=pp_trans.ref_code AND (action=1 OR action=2 OR action=5 OR action=6 OR action=7 OR action=8) ORDER BY create_datetime ASC LIMIT 1), '') AS start_date \n"
                + " ,to_char(confirm_date_time, 'YYYY-MM-DD HH24:MI:SS')AS confirm_date_time \n"
                + " ,to_char(close_time, 'YYYY-MM-DD HH24:MI:SS') AS close_time \n"
                + " ,to_char(create_date_time_now, 'YYYY-MM-DD HH24:MI:SS') AS create_date_time_now \n"
                + " FROM pp_trans \n" + __strQueryExtends + "\n"
                + " AND is_confirm=1 \n "
                //                + " ORDER BY create_date_time_now ASC,doc_no \n"
                + ") AS pp_trans \n"
                + "ORDER BY create_date_time_now,start_date ASC,doc_no  \n";

        PreparedStatement __stmtGetDetail;
        ResultSet __rsDataDetail;
        __stmtGetDetail = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsDataDetail = __stmtGetDetail.executeQuery();

        Integer __row = 8;
        while (__rsDataDetail.next()) {
            Boolean __isConfirm = __rsDataDetail.getBoolean("is_confirm");
            String __strWhCode = __rsDataDetail.getString("wh_code").equals("") ? "-" : __rsDataDetail.getString("wh_code");
            String __strWhName = __rsDataDetail.getString("wh_name").equals("") ? "-" : __rsDataDetail.getString("wh_name");
            String __strShelfCode = __rsDataDetail.getString("shelf_code").equals("") ? "-" : __rsDataDetail.getString("shelf_code");
            String __strShelfName = __rsDataDetail.getString("sh_name").equals("") ? "-" : __rsDataDetail.getString("sh_name");
            String __strDocDate = __rsDataDetail.getString("doc_date").equals("") ? "-" : __rsDataDetail.getString("doc_date");
            String __strDocNo = __rsDataDetail.getString("doc_no").equals("") ? "-" : __rsDataDetail.getString("doc_no");

            String __strStartDate = __rsDataDetail.getString("start_date") == null ? "-" : __rsDataDetail.getString("start_date").trim().isEmpty() ? "-" : __rsDataDetail.getString("start_date");
            String __strConfirmDateTime = __rsDataDetail.getString("confirm_date_time") == null ? "-" : __rsDataDetail.getString("confirm_date_time").trim().isEmpty() ? "-" : __rsDataDetail.getString("confirm_date_time");
            String __strPackingTime = __rsDataDetail.getString("packing_time") == null ? "-" : __rsDataDetail.getString("packing_time").trim().isEmpty() ? "-" : __rsDataDetail.getString("packing_time");
            String __strCloseTime = __rsDataDetail.getString("close_time") == null ? "-" : __rsDataDetail.getString("close_time").trim().isEmpty() ? "-" : __rsDataDetail.getString("close_time");
            String __strSendTime = __rsDataDetail.getString("sended_time") == null ? "-" : __rsDataDetail.getString("sended_time").trim().isEmpty() ? "-" : __rsDataDetail.getString("sended_time");

            Row row = __sheet.createRow(__row);
            Cell cellx = row.createCell(0);
            cellx.setCellValue(__strWhCode + " ~ " + __strWhName);

            Cell cellx1 = row.createCell(1);
            cellx1.setCellValue(__strShelfCode + " ~ " + __strShelfName);

            Cell cellx2 = row.createCell(2);
            cellx2.setCellValue((__strDocDate.equals("-") ? "-" : __routine._convertDate(__strDocDate)));

            Cell cellx3 = row.createCell(3);
            cellx3.setCellValue(__strDocNo);

            Cell cellx4 = row.createCell(4);
            cellx4.setCellValue((__strStartDate.equals("-") ? "-" : __routine._convertDateTime(__strStartDate)));

            Cell cellx5 = row.createCell(5);
            cellx5.setCellValue((__isConfirm ? (__strConfirmDateTime.equals("-") ? "-" : __routine._convertDateTime(__strConfirmDateTime)) : "-"));

            Cell cellx6 = row.createCell(6);
            cellx6.setCellValue((__isConfirm ? !__strStartDate.equals("-") && !__strConfirmDateTime.equals("-") ? !__strPackingTime.equals("-") ? __routine._convertDateTime(__strConfirmDateTime) : "-" : "-" : "-"));
            cellx6.setCellStyle(rightAligned);

            Cell cellx7 = row.createCell(7);
            cellx7.setCellValue((__isConfirm ? !__strStartDate.equals("-") && !__strConfirmDateTime.equals("-") ? !__strCloseTime.equals("-") ? __routine._convertDateTime(__strCloseTime) : "-" : "-" : "-"));
            cellx7.setCellStyle(rightAligned);

            Cell cellx8 = row.createCell(8);
            cellx8.setCellValue((__strSendTime.equals("-") ? "-" : __routine._convertTime(__strSendTime)));
            cellx8.setCellStyle(rightAligned);

            __row++;
        }

        __sheet.autoSizeColumn(0, true);
        __sheet.autoSizeColumn(1, true);
        __sheet.autoSizeColumn(2, true);
        __sheet.autoSizeColumn(3, true);
        __sheet.autoSizeColumn(4, true);
        __sheet.autoSizeColumn(5, true);
        __sheet.autoSizeColumn(6, true);
        __sheet.autoSizeColumn(7, true);
        __sheet.autoSizeColumn(8, true);
        __sheet.autoSizeColumn(9, true);

        __stmtGetDetail.close();
        __rsDataDetail.close();

        for (int colNum = 0; colNum < row5.getLastCellNum(); colNum++) {
            __workBook.getSheetAt(0).autoSizeColumn(colNum);
        }
        conn.commit();

    }

    private void _exportFileReport3(Connection conn, JSONObject param) throws SQLException, Exception, IOException {
        conn.setAutoCommit(false);
        String __strBranchCode = !param.isNull("branch_code") && !param.getString("branch_code").trim().isEmpty() ? param.getString("branch_code") : "";
        String __strDepartmentCode = !param.isNull("department_code") && !param.getString("department_code").trim().isEmpty() ? param.getString("department_code").equals("all") ? "แผนก ทั้งหมด" : "แผนก " + param.getString("department_code") : "";
        String __strFromDate = !param.isNull("from_date") && !param.getString("from_date").trim().isEmpty() ? param.getString("from_date") : "";
        String __strToDate = !param.isNull("to_date") && !param.getString("to_date").trim().isEmpty() ? param.getString("to_date") : "";

        String __strQUERY;
        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("branch_code") && !param.getString("branch_code").trim().isEmpty() ? " AND (UPPER(branch_code)= '" + param.getString("branch_code").toUpperCase() + "') " : "";
        __strQueryExtends += !param.isNull("department_code") && !param.getString("department_code").trim().isEmpty() ? param.getString("department_code").equals("all") ? "" : " AND (UPPER(department_code)= '" + param.getString("department_code").toUpperCase() + "') " : "";

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

        __workBook = new XSSFWorkbook();
        Sheet __sheet = __workBook.createSheet("รายงานสินค้าสั่งพิเศษ ค้างจ่าย");
        __strQUERY = "SELECT code,name_1 FROM erp_branch_list WHERE code='" + __strBranchCode + "' ";
        PreparedStatement __stmtGetBranch;
        ResultSet __rsDataBranch;
        __stmtGetBranch = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsDataBranch = __stmtGetBranch.executeQuery();

        PrintSetup printSetup = __sheet.getPrintSetup();
        printSetup.setLandscape(true);

        // กำหนดตัวหนังสือ
        XSSFFont __font = __workBook.createFont();
        __font.setFontHeightInPoints((short) 11);
        __font.setFontName("Tahoma");
        __font.setColor(IndexedColors.BLACK.getIndex());
        __font.setBold(true);
        __font.setItalic(false);

        CellStyle rightAligned = __workBook.createCellStyle();
        rightAligned.setAlignment(CellStyle.ALIGN_RIGHT);

        CellStyle centerAligned = __workBook.createCellStyle();
        centerAligned.setAlignment(CellStyle.ALIGN_CENTER);

        CellStyle leftAligned = __workBook.createCellStyle();
        leftAligned.setAlignment(CellStyle.ALIGN_LEFT);

        CellStyle __fontbold = __workBook.createCellStyle();
        __fontbold.setFont(__font);

        __sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));
        __sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 5));
        __sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 5));
        __sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 5));

        // กำหนดหัวเอกสาร
        Row __row0 = __sheet.createRow(1);
        Cell __cellTxtLabel = __row0.createCell(0);
        __cellTxtLabel.setCellValue("สินค้าสั่งพิเศษ ค้างจ่าย");
        __cellTxtLabel.setCellStyle(__fontbold);

        Row __row2 = __sheet.createRow(2);
        Cell __cell2 = __row2.createCell(0);
        while (__rsDataBranch.next()) {
            __cell2.setCellValue("สาขา " + __rsDataBranch.getString("code") + " ~ " + __rsDataBranch.getString("name_1"));
        }
        __cell2.setCellStyle(__fontbold);
        __stmtGetBranch.close();
        __rsDataBranch.close();

        Row __row3 = __sheet.createRow(3);
        Cell __cell3 = __row3.createCell(0);
        __cell3.setCellValue(__strDepartmentCode);
        __cell3.setCellStyle(__fontbold);

        Row __row4 = __sheet.createRow(4);
        Cell __cell4 = __row4.createCell(0);
        __cell4.setCellValue("วันที่ " + __strFromDate + "  ถึงวันที่ " + __strToDate);
        __cell4.setCellStyle(__fontbold);

        CellStyle cellStyle1 = __workBook.createCellStyle();
        cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setFillForegroundColor(IndexedColors.YELLOW.index);
        cellStyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle1.setFont(__font);

        CellStyle cellcenter = __workBook.createCellStyle();
        cellcenter.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellcenter.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellcenter.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellcenter.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellcenter.setFillForegroundColor(IndexedColors.YELLOW.index);
        cellcenter.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellcenter.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
        cellcenter.setVerticalAlignment(VerticalAlignment.CENTER);
        cellcenter.setFont(__font);

        Row row5 = __sheet.createRow(6);
        Cell cell50 = row5.createCell(0);
        cell50.setCellValue("แผนกขาย");
        cell50.setCellStyle(cellcenter);
        Cell cell51 = row5.createCell(1);
        cell51.setCellValue("พนักงานขาย");
        cell51.setCellStyle(cellcenter);
        Cell cell52 = row5.createCell(2);
        cell52.setCellValue("รหัสสินค้า");
        cell52.setCellStyle(cellcenter);
        Cell cell53 = row5.createCell(3);
        cell53.setCellValue("ชื่อสินค้า");
        cell53.setCellStyle(cellcenter);
        Cell cell54 = row5.createCell(4);

        cell54.setCellValue("จำนวนสั่งจอง");
        cell54.setCellStyle(cellcenter);
        Cell cell55 = row5.createCell(5);
        cell55.setCellValue("หน่วย");
        cell55.setCellStyle(cellcenter);

        Cell cell57 = row5.createCell(6);
        cell57.setCellValue("วันที่ใบสั่งจอง");
        cell57.setCellStyle(cellcenter);

        Cell cell58 = row5.createCell(7);
        cell58.setCellValue("เลขที่ใบสั่งจอง");
        cell58.setCellStyle(cellcenter);

        Cell cell59 = row5.createCell(8);
        cell59.setCellValue("วันที่รับสินค้า");
        cell59.setCellStyle(cellcenter);

        Cell cell511 = row5.createCell(9);
        cell511.setCellValue("เลขที่ใบรับ");
        cell511.setCellStyle(cellcenter);

        Cell cell512 = row5.createCell(10);
        cell512.setCellValue("วันที่ใบจัด");
        cell512.setCellStyle(cellcenter);

        Cell cell513 = row5.createCell(11);
        cell513.setCellValue("เลขที่ใบจัด");
        cell513.setCellStyle(cellcenter);

        Cell cell514 = row5.createCell(12);
        cell514.setCellValue("จำนวนวันที่ค้าง(จ่าย)");
        cell514.setCellStyle(cellcenter);

        Cell cell515 = row5.createCell(13);
        cell515.setCellValue("จำนวนคงค้าง");
        cell515.setCellStyle(cellcenter);

        __strQUERY = "SELECT * FROM tms_config_date WHERE date_flag=2";
        PreparedStatement __stmtGetTMSConfigDate;
        ResultSet __rsDataTMSConfigDate;
        __stmtGetTMSConfigDate = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsDataTMSConfigDate = __stmtGetTMSConfigDate.executeQuery();

        String __strBeginDate = "";
        while (__rsDataTMSConfigDate.next()) {
            __strBeginDate = __rsDataTMSConfigDate.getString("begin_date").equals("") ? "" : __rsDataTMSConfigDate.getString("begin_date");
        }

        __stmtGetTMSConfigDate.close();
        __rsDataTMSConfigDate.close();

        __strQUERY = "SELECT * FROM ("
                + " SELECT branch_code ,ref_code ,qty,unit_code "
                + " ,(SELECT department_code FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no) AS department_code "
                + " ,(SELECT name_1 FROM erp_department_list WHERE code= (SELECT department_code FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no)) AS department_name "
                + " ,(SELECT sale_code FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no) AS sale_code "
                + " ,(SELECT name_1 FROM erp_user WHERE code = (SELECT sale_code FROM pp_trans WHERE pp_trans.doc_no=pp_trans_detail.doc_no)) AS sale_name "
                + " ,ic_code,(SELECT name_1 FROM ic_inventory WHERE ic_inventory.code=pp_trans_detail.ic_code) AS item_name "
                + " ,(SELECT name_1 FROM ic_unit WHERE code=unit_code)AS unit_name "
                + " ,(SELECT doc_date FROM ic_trans WHERE ic_trans.doc_no=pp_trans_detail.ref_code AND trans_flag=34) AS rev_date "
                + " ,(SELECT distinct doc_date "
                + " FROM ic_trans_detail WHERE trans_flag in (12, 310) "
                + " AND (SELECT ref_doc_no FROM ic_trans_detail as po WHERE po.trans_flag = 6 AND po.doc_no = ic_trans_detail.ref_doc_no AND po.line_number = ic_trans_detail.ref_row)=pp_trans_detail.ref_code AND doc_date >= '" + __strBeginDate + "' LIMIT 1)AS rec_date "
                + " ,array_to_string(array((SELECT distinct doc_no "
                + " FROM ic_trans_detail WHERE trans_flag in (12, 310) "
                + " AND (SELECT ref_doc_no FROM ic_trans_detail as po WHERE po.trans_flag = 6 AND po.doc_no = ic_trans_detail.ref_doc_no AND po.line_number = ic_trans_detail.ref_row)=pp_trans_detail.ref_code AND doc_date >= '" + __strBeginDate + "' )),',')AS rec_code "
                + " ,doc_date,doc_no,to_char((now()-(SELECT distinct doc_date "
                + " FROM ic_trans_detail WHERE trans_flag in (12, 310) "
                + " AND (SELECT ref_doc_no FROM ic_trans_detail as po WHERE po.trans_flag = 6 AND po.doc_no = ic_trans_detail.ref_doc_no AND po.line_number = ic_trans_detail.ref_row)=pp_trans_detail.ref_code AND doc_date >= '" + __strBeginDate + "' LIMIT 1)),'DD-HH24-MI')AS out_day "
                + " ,(qty-event_qty) AS total_qty "
                + " FROM pp_trans_detail WHERE doc_no in ((SELECT doc_no FROM pp_trans WHERE trans_flag=34 AND is_close!=1 order by doc_date desc,doc_no)))AS res " + __strQueryExtends + " AND rec_code != '' ";

        PreparedStatement __stmtGetDetail;
        ResultSet __rsDataDetail;
        __stmtGetDetail = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsDataDetail = __stmtGetDetail.executeQuery();

        Integer __row = 7;
        while (__rsDataDetail.next()) {
            String __strDepartmentCodeX = __rsDataDetail.getString("department_code").equals("") ? "-" : __rsDataDetail.getString("department_code");
            String __strDepartmentName = __rsDataDetail.getString("department_name").equals("") ? "-" : __rsDataDetail.getString("department_name");
            String __strSaleCode = __rsDataDetail.getString("sale_code").equals("") ? "-" : __rsDataDetail.getString("sale_code");
            String __strSaleName = __rsDataDetail.getString("sale_name").equals("") ? "-" : __rsDataDetail.getString("sale_name");
            String __strItemCode = __rsDataDetail.getString("ic_code").equals("") ? "-" : __rsDataDetail.getString("ic_code");
            String __strItemName = __rsDataDetail.getString("item_name").equals("") ? "-" : __rsDataDetail.getString("item_name");
            String __strQTY = __rsDataDetail.getString("qty").equals("") ? "-" : String.format("%,.2f", Float.parseFloat(__rsDataDetail.getString("qty")));
            String __strUnitCode = __rsDataDetail.getString("unit_code").equals("") ? "-" : __rsDataDetail.getString("unit_code");
            String __strUnitName = __rsDataDetail.getString("unit_name").equals("") ? "-" : __rsDataDetail.getString("unit_name");
            String __strRefDate = __rsDataDetail.getString("rev_date").equals("") ? "-" : __rsDataDetail.getString("rev_date");
            String __strRefCode = __rsDataDetail.getString("ref_code").equals("") ? "-" : __rsDataDetail.getString("ref_code");
            String __strReCDate = __rsDataDetail.getString("rec_date").equals("") ? "-" : __rsDataDetail.getString("rec_date");
            String __strReCCode = __rsDataDetail.getString("rec_code").equals("") ? "-" : __rsDataDetail.getString("rec_code");
            String __strDocDate = __rsDataDetail.getString("doc_date").equals("") ? "-" : __rsDataDetail.getString("doc_date");
            String __strDocNo = __rsDataDetail.getString("doc_no").equals("") ? "-" : __rsDataDetail.getString("doc_no");
            String __strOutDay = __rsDataDetail.getString("out_day").equals("") ? "-" : __rsDataDetail.getString("out_day");
            String __strTotalQTY = __rsDataDetail.getString("total_qty").equals("") ? "-" : __rsDataDetail.getString("total_qty");

            Row row = __sheet.createRow(__row);
            Cell cellx = row.createCell(0);
            cellx.setCellValue(__strDepartmentCodeX + " ~ " + __strDepartmentName);

            Cell cellx1 = row.createCell(1);
            cellx1.setCellValue(__strSaleCode + " ~ " + __strSaleName);

            Cell cellx2 = row.createCell(2);
            cellx2.setCellValue(__strItemCode);

            Cell cellx3 = row.createCell(3);
            cellx3.setCellValue(__strItemName);

            Cell cellx4 = row.createCell(4);
            cellx4.setCellValue(__strQTY);
            cellx4.setCellStyle(rightAligned);

            Cell cellx5 = row.createCell(5);
            cellx5.setCellValue(__strUnitCode + " ~ " + __strUnitName);
            cellx5.setCellStyle(centerAligned);

            Cell cellx6 = row.createCell(6);
            cellx6.setCellValue(__routine._convertDate(__strRefDate));
            cellx6.setCellStyle(centerAligned);

            Cell cellx7 = row.createCell(7);
            cellx7.setCellValue(__strRefCode);

            Cell cellx9 = row.createCell(8);
            cellx9.setCellValue(__routine._convertDate(__strReCDate));

            Cell cellx10 = row.createCell(9);
            cellx10.setCellValue(__strReCCode);

            Cell cellx11 = row.createCell(10);
            cellx11.setCellValue(__routine._convertDate(__strDocDate));

            Cell cellx12 = row.createCell(11);
            cellx12.setCellValue(__strDocNo);

            Cell cellx13 = row.createCell(12);
            cellx13.setCellValue(__routine._convertTime(__strOutDay));
            cellx13.setCellStyle(rightAligned);

            Cell cellx14 = row.createCell(13);
            cellx14.setCellValue(__strTotalQTY);
            cellx14.setCellStyle(rightAligned);

            __row++;
        }

        __sheet.autoSizeColumn(0, true);
        __sheet.autoSizeColumn(1, true);
        __sheet.autoSizeColumn(2, true);
        __sheet.autoSizeColumn(3, true);
        __sheet.autoSizeColumn(4, true);
        __sheet.autoSizeColumn(5, true);
        __sheet.autoSizeColumn(6, true);
        __sheet.autoSizeColumn(7, true);
        __sheet.autoSizeColumn(8, true);
        __sheet.autoSizeColumn(9, true);
        __sheet.autoSizeColumn(10, true);
        __sheet.autoSizeColumn(11, true);
        __sheet.autoSizeColumn(12, true);
        __sheet.autoSizeColumn(13, true);
        __sheet.autoSizeColumn(14, true);

        __stmtGetDetail.close();
        __rsDataDetail.close();

        for (int colNum = 0; colNum < row5.getLastCellNum(); colNum++) {
            __workBook.getSheetAt(0).autoSizeColumn(colNum);
        }

        conn.commit();

    }
}
