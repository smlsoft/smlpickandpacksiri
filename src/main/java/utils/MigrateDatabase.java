package utils;

import Model.MigrateColumnModel;
import Model.MigrateIndexModel;
import Model.MigrateTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.sql.PreparedStatement;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

public class MigrateDatabase {
    
    Vector<MigrateTableModel> tables;
    Vector<MigrateTableModel> tablesProvider;
    HttpSession session = null;
    
    StringBuilder logVerify = new StringBuilder();
    
    private void structTable() {
        tables = new Vector<MigrateTableModel>();
        tablesProvider = new Vector<MigrateTableModel>();

        // =============================== pp_trans ============================
        MigrateTableModel ppTrans = new MigrateTableModel("pp_trans");
        tables.add(ppTrans);
        
        ppTrans.addColumns(new MigrateColumnModel("ignore_sync", "integer", "DEFAULT 0"));
        ppTrans.addColumns(new MigrateColumnModel("is_lock_record", "integer", "DEFAULT 0"));
        ppTrans.addColumns(new MigrateColumnModel("roworder", "serial"));
        ppTrans.addColumns(new MigrateColumnModel("doc_no", "character varying", 100, "NOT NULL DEFAULT ''::character varying"));
        ppTrans.addColumns(new MigrateColumnModel("doc_date", "date", "NOT NULL"));
        ppTrans.addColumns(new MigrateColumnModel("ref_code", "character varying", 25, "NOT NULL DEFAULT ''::character varying"));
        ppTrans.addColumns(new MigrateColumnModel("ref_date", "date", "NOT NULL"));
        ppTrans.addColumns(new MigrateColumnModel("due_date", "date"));
        ppTrans.addColumns(new MigrateColumnModel("due_date", "date"));
        ppTrans.addColumns(new MigrateColumnModel("cust_code", "character varying", 25, "NOT NULL DEFAULT ''::character varying"));
        ppTrans.addColumns(new MigrateColumnModel("sale_code", "character varying", 25, "DEFAULT ''::character varying"));
        ppTrans.addColumns(new MigrateColumnModel("status", "integer", "DEFAULT 0"));
        ppTrans.addColumns(new MigrateColumnModel("persent", "numeric", "DEFAULT 0.0"));
        ppTrans.addColumns(new MigrateColumnModel("remark", "character varying", 25, "DEFAULT ''::character varying"));
        ppTrans.addColumns(new MigrateColumnModel("row_number", "integer", "DEFAULT 0"));
        ppTrans.addColumns(new MigrateColumnModel("is_cancel", "integer", "DEFAULT 0"));
        ppTrans.addColumns(new MigrateColumnModel("cancel_code", "character varying", 25, "DEFAULT ''::character varying"));
        ppTrans.addColumns(new MigrateColumnModel("create_date_time_now", "timestamp", "without time zone NOT NULL DEFAULT ('now'::text)::timestamp without time zone"));
        ppTrans.addColumns(new MigrateColumnModel("sale_type", "integer"));
        ppTrans.addColumns(new MigrateColumnModel("department_code", "character varying", 255));
        ppTrans.addColumns(new MigrateColumnModel("send_type", "integer"));
        ppTrans.addColumns(new MigrateColumnModel("creator_code", "character varying", 255));
        ppTrans.addColumns(new MigrateColumnModel("branch_code", "character varying", 255));
        ppTrans.addColumns(new MigrateColumnModel("create_date_time", "character varying", 255));
        ppTrans.addColumns(new MigrateColumnModel("last_editor_code", "character varying", 255));
        ppTrans.addColumns(new MigrateColumnModel("last_edit_date", "time", "without time zone"));
        ppTrans.addColumns(new MigrateColumnModel("confirm_code", "character varying", 255));
        ppTrans.addColumns(new MigrateColumnModel("confirm_date_time", "timestamp without time zone"));
        ppTrans.addColumns(new MigrateColumnModel("cancel_date_time", "character varying", 255));
        ppTrans.addColumns(new MigrateColumnModel("trans_flag", "integer"));
        ppTrans.addColumns(new MigrateColumnModel("last_status", "integer", "DEFAULT 0"));
        ppTrans.addColumns(new MigrateColumnModel("total_amount", "numeric"));
        ppTrans.addColumns(new MigrateColumnModel("confirm_date", "date"));
        ppTrans.addColumns(new MigrateColumnModel("confirm_time", "time", "without time zone"));
        ppTrans.addColumns(new MigrateColumnModel("e_doc_no", "character varying", 255));
        ppTrans.addColumns(new MigrateColumnModel("is_close", "integer", "NOT NULL DEFAULT 0"));
        ppTrans.addColumns(new MigrateColumnModel("close_time", "timestamp", "without time zone DEFAULT now()"));
        ppTrans.addColumns(new MigrateColumnModel("b_doc_no", "character varying", 255));
        ppTrans.addColumns(new MigrateColumnModel("wh_code", "character varying", 255));
        ppTrans.addColumns(new MigrateColumnModel("shelf_code", "character varying", 255));
        ppTrans.addColumns(new MigrateColumnModel("is_print", "integer", "NOT NULL DEFAULT 0"));
        ppTrans.addColumns(new MigrateColumnModel("is_confirm", "integer", "NOT NULL DEFAULT 0"));
        ppTrans.addColumns(new MigrateColumnModel("car_code", "character varying", 255));
        ppTrans.addColumns(new MigrateColumnModel("tms_que_shipment_code", "character varying", 255));
        ppTrans.addColumns(new MigrateColumnModel("lastedit_datetime", "timestamp", "without time zone"));
        
        ppTrans.addConstraint("pp_trans_pk_primary PRIMARY KEY (doc_no)");
        ppTrans.addIndex(new MigrateIndexModel("pp_trans_doc_date_idx", new String[]{"doc_date"}));
        ppTrans.addIndex(new MigrateIndexModel("pp_trans_pk_primary_idx", new String[]{"doc_no"}));
        ppTrans.addIndex(new MigrateIndexModel("pp_trans_ref_code_idx", new String[]{"ref_code"}));
        ppTrans.addIndex(new MigrateIndexModel("pp_trans_roworder_idx", new String[]{"roworder"}));
        ppTrans.addIndex(new MigrateIndexModel("pp_trans_shelf_code_idx", new String[]{"shelf_code"}));
        ppTrans.addIndex(new MigrateIndexModel("pp_trans_wh_code_idx", new String[]{"wh_code"}));
        ppTrans.addIndex(new MigrateIndexModel("pp_trans_car_code_idx", new String[]{"car_code"}));
        // ========================== pp_trans_detail ==========================
        MigrateTableModel ppTransDetail = new MigrateTableModel("pp_trans_detail");
        tables.add(ppTransDetail);
        
        ppTransDetail.addColumns(new MigrateColumnModel("ignore_sync", "integer", "DEFAULT 0"));
        ppTransDetail.addColumns(new MigrateColumnModel("is_lock_record", "integer", "DEFAULT 0"));
        ppTransDetail.addColumns(new MigrateColumnModel("roworder", "serial"));
        ppTransDetail.addColumns(new MigrateColumnModel("row_number", "integer", "NOT NULL DEFAULT 0"));
        ppTransDetail.addColumns(new MigrateColumnModel("doc_no", "character varying", 100, "NOT NULL DEFAULT ''::character varying"));
        ppTransDetail.addColumns(new MigrateColumnModel("ic_code", "character varying", 25, "NOT NULL DEFAULT ''::character varying"));
        ppTransDetail.addColumns(new MigrateColumnModel("wh_code", "character varying", 25, "DEFAULT ''::character varying"));
        ppTransDetail.addColumns(new MigrateColumnModel("shelf_code", "character varying", 25, "DEFAULT ''::character varying"));
        ppTransDetail.addColumns(new MigrateColumnModel("unit_code", "character varying", 25, "DEFAULT ''::character varying"));
        ppTransDetail.addColumns(new MigrateColumnModel("qty", "numeric", "DEFAULT 0.0"));
        ppTransDetail.addColumns(new MigrateColumnModel("event_qty", "numeric", "DEFAULT 0.0"));
        ppTransDetail.addColumns(new MigrateColumnModel("event_date_time", "date"));
        ppTransDetail.addColumns(new MigrateColumnModel("create_date_time_now", "timestamp", "without time zone NOT NULL DEFAULT ('now'::text)::timestamp without time zone"));
        ppTransDetail.addColumns(new MigrateColumnModel("department_code", "character varying", 255));
        ppTransDetail.addColumns(new MigrateColumnModel("branch_code", "character varying", 255));
        ppTransDetail.addColumns(new MigrateColumnModel("ref_code", "character varying", 255));
        ppTransDetail.addColumns(new MigrateColumnModel("doc_date", "date"));
        ppTransDetail.addColumns(new MigrateColumnModel("ref_date", "date"));
        ppTransDetail.addColumns(new MigrateColumnModel("line_number", "integer"));
        ppTransDetail.addColumns(new MigrateColumnModel("status", "integer", "NOT NULL DEFAULT 0"));
        ppTransDetail.addColumns(new MigrateColumnModel("remark", "character varying", 255));
        ppTransDetail.addColumns(new MigrateColumnModel("sum_amount", "numeric", "DEFAULT 0"));
        ppTransDetail.addColumns(new MigrateColumnModel("price", "numeric", "DEFAULT 0"));
        ppTransDetail.addColumns(new MigrateColumnModel("is_confirm", "integer", "DEFAULT 0"));
        ppTransDetail.addColumns(new MigrateColumnModel("tms_que_shipment_code", "character varying", 255));
        ppTransDetail.addColumns(new MigrateColumnModel("car_code", "character varying", 255));
        
        ppTransDetail.addConstraint("pp_trans_detail_pkey PRIMARY KEY (roworder)");
        ppTransDetail.addIndex(new MigrateIndexModel("pp_trans_detail_idx1_idx", new String[]{"doc_no"}));
        ppTransDetail.addIndex(new MigrateIndexModel("pp_trans_detail_idx2_idx", new String[]{"ic_code"}));
        ppTransDetail.addIndex(new MigrateIndexModel("pp_trans_detail_ref_code_idx", new String[]{"ref_code"}));
        ppTransDetail.addIndex(new MigrateIndexModel("pp_trans_detail_roworder_idx", new String[]{"roworder"}));
        ppTransDetail.addIndex(new MigrateIndexModel("pp_trans_detail_shelf_code_idx", new String[]{"shelf_code"}));
        ppTransDetail.addIndex(new MigrateIndexModel("pp_trans_detail_wh_code_idx", new String[]{"wh_code"}));
        ppTransDetail.addIndex(new MigrateIndexModel("pp_trans_detail_tms_que_shipment_code_idx", new String[]{"tms_que_shipment_code"}));
        ppTransDetail.addIndex(new MigrateIndexModel("pp_trans_detail_car_code_idx", new String[]{"car_code"}));
        // ======================== sml_group_permission =======================
        MigrateTableModel smlGroupPerm = new MigrateTableModel("sml_group_permission");
        
        smlGroupPerm.addColumns(new MigrateColumnModel("roworder", "serial"));
        smlGroupPerm.addColumns(new MigrateColumnModel("g_code", "character varying", 255));
        smlGroupPerm.addColumns(new MigrateColumnModel("g_name", "character varying", 255));
        smlGroupPerm.addColumns(new MigrateColumnModel("g_r_status", "boolean"));
        smlGroupPerm.addColumns(new MigrateColumnModel("g_a_status", "boolean"));
        smlGroupPerm.addColumns(new MigrateColumnModel("g_e_status", "boolean"));
        smlGroupPerm.addColumns(new MigrateColumnModel("g_d_status", "boolean"));
        smlGroupPerm.addColumns(new MigrateColumnModel("g_p_status", "boolean"));
        smlGroupPerm.addColumns(new MigrateColumnModel("p_code", "character varying", 255));
        smlGroupPerm.addColumns(new MigrateColumnModel("create_date_time_now", "timestamp", "without time zone NOT NULL DEFAULT ('now'::text)::timestamp without time zone"));
        
        smlGroupPerm.addConstraint("sml_group_permission_pk_primary PRIMARY KEY (roworder)");
        
        tablesProvider.add(smlGroupPerm);
        // ======================== sml_user_permission ========================
        MigrateTableModel smlUserPerm = new MigrateTableModel("sml_user_permission");
        
        smlUserPerm.addColumns(new MigrateColumnModel("roworder", "serial"));
        smlUserPerm.addColumns(new MigrateColumnModel("user_code", "character varying", 255));
        smlUserPerm.addColumns(new MigrateColumnModel("p_code", "character varying", 255));
        smlUserPerm.addColumns(new MigrateColumnModel("is_read", "boolean"));
        smlUserPerm.addColumns(new MigrateColumnModel("is_create", "boolean"));
        smlUserPerm.addColumns(new MigrateColumnModel("is_update", "boolean"));
        smlUserPerm.addColumns(new MigrateColumnModel("is_delete", "boolean"));
        smlUserPerm.addColumns(new MigrateColumnModel("is_re_print", "boolean"));
        
        smlUserPerm.addConstraint("sml_user_permission_pkey PRIMARY KEY (roworder)");
        
        tablesProvider.add(smlUserPerm);
        // ========================== sml_web_pages ============================
        MigrateTableModel smlWebPages = new MigrateTableModel("sml_web_pages");
        tablesProvider.add(smlWebPages);
        
        smlWebPages.addColumns(new MigrateColumnModel("roworder", "serial"));
        smlWebPages.addColumns(new MigrateColumnModel("p_code", "character varying", 255));
        smlWebPages.addColumns(new MigrateColumnModel("p_name", "character varying", 255));
        
        smlWebPages.addConstraint("sml_web_pages_pkey PRIMARY KEY (roworder)");
        
        smlWebPages.addAfterScript("INSERT INTO sml_web_pages (roworder,p_code,p_name) VALUES ('1','P0000000001', 'ยกเลิกเอกสารใบจัด')");
        
        smlWebPages.addAfterScript("INSERT INTO sml_web_pages (roworder,p_code,p_name) VALUES ('2','P0000000002', 'ตรวจสอบสถานะใบจัดสินค้า')");
        
        smlWebPages.addAfterScript("INSERT INTO sml_web_pages (roworder,p_code,p_name) VALUES ('3','P0000000003', 'พนักงานคลัง ขาย')");
        
        smlWebPages.addAfterScript("INSERT INTO sml_web_pages (roworder,p_code,p_name) VALUES ('4','P0000000004', 'ธรุการคลัง ขาย')");
        
        smlWebPages.addAfterScript("INSERT INTO sml_web_pages (roworder,p_code,p_name) VALUES ('5','P0000000005', 'กลุ่ม คลังสินค้าและสถานที่เก็บสินค้า')");
        
        smlWebPages.addAfterScript("INSERT INTO sml_web_pages (roworder,p_code,p_name) VALUES ('6','P0000000006', 'กำหนดหมายเลขเครื่อง')");
        
        smlWebPages.addAfterScript("INSERT INTO sml_web_pages (roworder,p_code,p_name) VALUES ('7', 'P0000000007', 'กำหนดเหตุผล')");
        
        smlWebPages.addAfterScript("INSERT INTO sml_web_pages (roworder,p_code,p_name) VALUES ('8','P0000000008', 'กำหนดวันที่เอกสารเริ่มต้นที่จะดึง')");
        
        smlWebPages.addAfterScript("INSERT INTO sml_web_pages (roworder,p_code,p_name) VALUES ('9','P0000000009', 'กำหนดสิทธิ์ ตามผู้ใช้งาน')");
        
        smlWebPages.addAfterScript("INSERT INTO sml_web_pages (roworder,p_code,p_name) VALUES ('10','P0000000010', 'จัดการสิทธิ์ - แบบกลุ่ม')");
        
        smlWebPages.addAfterScript("INSERT INTO sml_web_pages (roworder,p_code,p_name) VALUES ('11','P0000000011', 'ประวัติการทำรายการ')");
        
        smlWebPages.addAfterScript("INSERT INTO sml_web_pages (roworder,p_code,p_name) VALUES ('12','P0000000012', 'รายงาน วิเคราะห์การจ่ายสินค้าขาด')");
        
        smlWebPages.addAfterScript("INSERT INTO sml_web_pages (roworder,p_code,p_name) VALUES ('13','P0000000013', 'รายงาน ระยะเวลาในการจัดสินค้าและส่งมอบสินค้า')");
        
        smlWebPages.addAfterScript("INSERT INTO sml_web_pages (roworder,p_code,p_name) VALUES ('14','P0000000014', 'รายงาน สินค้าสั่งพิเศษค้างจ่าย')");

        // ========================== sml_user_group ===========================
        MigrateTableModel smlUserGroup = new MigrateTableModel("sml_user_group");
        
        smlUserGroup.addColumns(new MigrateColumnModel("roworder", "serial"));
        smlUserGroup.addColumns(new MigrateColumnModel("g_code", "character varying", 255));
        smlUserGroup.addColumns(new MigrateColumnModel("u_code", "character varying", 255));
        
        smlUserGroup.addConstraint("sml_user_group_pkey PRIMARY KEY (roworder)");
        
        tablesProvider.add(smlUserGroup);
        // ========================== sml_group_lists ===========================
        MigrateTableModel smlGroupLists = new MigrateTableModel("sml_group_lists");
        
        smlGroupLists.addColumns(new MigrateColumnModel("code", "character varying", 255, "NOT NULL"));
        smlGroupLists.addColumns(new MigrateColumnModel("name", "character varying", 255));
        
        smlGroupLists.addConstraint("sml_group_lists_pkey PRIMARY KEY (code)");
        
        tablesProvider.add(smlGroupLists);
        // ========================== sml_group_location =======================
        MigrateTableModel smlGroupLocation = new MigrateTableModel("sml_group_location");
        
        smlGroupLocation.addColumns(new MigrateColumnModel("group_code", "character varying", 255, "NOT NULL"));
        smlGroupLocation.addColumns(new MigrateColumnModel("whcode", "character varying", 200, "NOT NULL"));
        smlGroupLocation.addColumns(new MigrateColumnModel("location", "character varying", 200, "NOT NULL"));
        smlGroupLocation.addColumns(new MigrateColumnModel("name", "character varying", 255, "NOT NULL"));
        
        smlGroupLocation.addConstraint("sml_group_location_pkey PRIMARY KEY (group_code)");
        
        tables.add(smlGroupLocation);
        // ========================== sml_group_system =========================
        MigrateTableModel smlGroupSystem = new MigrateTableModel("sml_group_system");
        
        smlGroupSystem.addColumns(new MigrateColumnModel("roworder", "serial"));
        smlGroupSystem.addColumns(new MigrateColumnModel("group_code", "character varying", 255, "NOT NULL"));
        smlGroupSystem.addColumns(new MigrateColumnModel("system_id", "character varying", 100, "NOT NULL"));
        
        smlGroupSystem.addConstraint("sml_group_system_pkey PRIMARY KEY (roworder)");
        
        tables.add(smlGroupSystem);
        // ========================== tms_config_date ==========================
        MigrateTableModel tmsConfigDate = new MigrateTableModel("tms_config_date");
        
        tmsConfigDate.addColumns(new MigrateColumnModel("roworder", "serial"));
        tmsConfigDate.addColumns(new MigrateColumnModel("begin_date", "date"));
        tmsConfigDate.addColumns(new MigrateColumnModel("date_flag", "integer", "NOT NULL"));
        tmsConfigDate.addColumns(new MigrateColumnModel("code", "character varying", 255, "NOT NULL"));
        
        tmsConfigDate.addConstraint("tms_config_date_pkey PRIMARY KEY (code, date_flag)");
        
        tables.add(tmsConfigDate);
        // ========================== pp_no_approve ==========================
        MigrateTableModel ppNoApprove = new MigrateTableModel("pp_no_approve");
        
        ppNoApprove.addColumns(new MigrateColumnModel("roworder", "serial"));
        ppNoApprove.addColumns(new MigrateColumnModel("wh_list", "character varying"));
        ppNoApprove.addColumns(new MigrateColumnModel("shelf_list", "character varying"));
        
        ppNoApprove.addConstraint("pp_no_approve_pkey PRIMARY KEY (roworder)");
        
        tables.add(ppNoApprove);

        // ========================== pp_no_approve ==========================
        MigrateTableModel pptranslog = new MigrateTableModel("pp_trans_log");
        
        pptranslog.addColumns(new MigrateColumnModel("roworder", "serial"));
        pptranslog.addColumns(new MigrateColumnModel("doc_no", "character varying", 255));
        pptranslog.addColumns(new MigrateColumnModel("ref_code", "character varying", 255));
        pptranslog.addColumns(new MigrateColumnModel("user_code", "character varying", 255));
        pptranslog.addColumns(new MigrateColumnModel("trans_flag", "integer", "NOT NULL"));
        pptranslog.addColumns(new MigrateColumnModel("action", "integer", "NOT NULL"));
        pptranslog.addColumns(new MigrateColumnModel("create_datetime", "timestamp", "without time zone NOT NULL DEFAULT ('now'::text)::timestamp without time zone"));
        pptranslog.addConstraint("pp_trans_log_pkey PRIMARY KEY (roworder)");
        
        pptranslog.addIndex(new MigrateIndexModel("pp_trans_log_ref_code_idx", new String[]{"ref_code"}));
        pptranslog.addIndex(new MigrateIndexModel("pp_trans_log_action_idx", new String[]{"action"}));
        pptranslog.addIndex(new MigrateIndexModel("pp_trans_log_create_datetime_idx", new String[]{"create_datetime"}));
        
        tables.add(pptranslog);

        // ========================== pp_user_active ==========================
        MigrateTableModel ppUserActive = new MigrateTableModel("pp_user_active");
        
        ppUserActive.addColumns(new MigrateColumnModel("roworder", "serial"));
        ppUserActive.addColumns(new MigrateColumnModel("login_time", "timestamp", "without time zone NOT NULL DEFAULT ('now'::text)::timestamp without time zone"));
        ppUserActive.addColumns(new MigrateColumnModel("session_id", "character varying", 255));
        ppUserActive.addColumns(new MigrateColumnModel("last_accessed_time", "timestamp", "without time zone NOT NULL DEFAULT ('now'::text)::timestamp without time zone"));
        ppUserActive.addColumns(new MigrateColumnModel("user_code", "character varying", 255));
        ppUserActive.addConstraint("pp_user_active_pkey PRIMARY KEY (roworder)");
        
        ppUserActive.addIndex(new MigrateIndexModel("idx_last_accessed_time", new String[]{"last_accessed_time"}));
        ppUserActive.addIndex(new MigrateIndexModel("idx_login_time", new String[]{"login_time"}));
        ppUserActive.addIndex(new MigrateIndexModel("idx_session_id", new String[]{"session_id"}));
        ppUserActive.addIndex(new MigrateIndexModel("idx_user_code", new String[]{"user_code"}));
        
        tables.add(ppUserActive);
        
    }
    
    public void verify(String provider, String dbname, HttpSession session) {
        this.session = session;
        session.setAttribute("verify_log", "");
        verify(provider, dbname);
    }
    
    public void verify(String provider, String dbname) {
        Connection __conn = null;
        Connection __connProvider = null;
        
        structTable();
        dbname = dbname.toLowerCase();
        provider = provider.toLowerCase();
        
        _routine __routine = new _routine();
        
        __conn = __routine._connect(dbname, _global.FILE_CONFIG(provider));
        __connProvider = __routine._connect("smlerpmain" + provider, _global.FILE_CONFIG(provider));
        
        try {
            
            for (MigrateTableModel table : tables) {
                verifyTable(__conn, table);
            }
            
            for (MigrateTableModel table : tablesProvider) {
                verifyTable(__connProvider, table);
            }
            afterScript(__connProvider);
            
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            logStatus("complete", "", 1);
            
            if (__conn != null) {
                try {
                    __conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
            if (__connProvider != null) {
                try {
                    __connProvider.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
    }
    
    private void afterScript(Connection __conn) {
        try {
            String sqlCheck = "SELECT module_code FROM sml_web_module WHERE module_code = 'settings'";
            
            PreparedStatement stmt = __conn.prepareStatement(sqlCheck);
            ResultSet rs = stmt.executeQuery();
            
            boolean valNotExists = true;
            while (rs.next()) {
                valNotExists = false;
            }
            
            if (valNotExists) {
                String afterScript = "DELETE FROM sml_web_module WHERE web_flag=1;";
                afterScript += "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'cargroup', 'รหัสรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'carmaster', 'รายละเอียดรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'carstatus', 'สถานะรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'cartype', 'ประเภทรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'tmsroute', 'จัดการเส้นทาง', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'drivermaster', 'จัดการพนักงานขนส่ง', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'maintenance', 'Maintenance', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'oil', 'Oil', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'caruse', 'การใช้งานรถ', '0', '1');"
                        //                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'routedetails', 'การเดินรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipment', 'คิวส่งสินค้า', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipmentapprove', 'ปล่อยรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipmentcancel', 'ยกเลิก', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipmentfinished', 'ปิดจ๊อบ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'sendassign', 'ฝากขนส่ง', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'trucktracking', 'ติดตามรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipmentreport', 'รายงานตรวจสอบ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'reasonque', 'เหตุผลจัดคิว', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'reasonapprove', 'เหตุผลปล่อยรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'reasonclosejob', 'เหตุผลปิดjob', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'reasoncaruse', 'เหตุผลรถไม่ว่าง', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'permission', 'จัดการสิทธิ์', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'group', 'กลุ่มผู้ใช้งาน', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'settings', 'ตั้งค่า', '0', '1');";
                
                PreparedStatement stmtBeforeScript = __conn.prepareStatement(afterScript);
                stmtBeforeScript.executeUpdate();
            }
            
        } catch (SQLException ex) {
//            ex.printStackTrace();
        }
    }
    
    private void verifyTable(Connection __conn, MigrateTableModel table) {
        
        try {
            PreparedStatement stmtBeforeScript = __conn.prepareStatement(table.getBeforeScript());
            stmtBeforeScript.executeUpdate();
        } catch (SQLException ex) {
            
        }
        
        try {
            
            String sqlCheckTable = "";
            
            JSONObject logTable = new JSONObject();
            
            sqlCheckTable = "SELECT table_name FROM information_schema.tables WHERE table_name = '" + table.getTableName() + "';";
            PreparedStatement stmt = __conn.prepareStatement(sqlCheckTable);
            ResultSet rs = stmt.executeQuery();
            
            table.setExists(false);
            while (rs.next()) {
                table.setExists(true);
            }
            rs.close();
            stmt.close();
            
            if (table.isExists()) {
                logStatus("t", table.getTableName(), 1);
                verifyColumn(__conn, table);
            } else {
                
                PreparedStatement stmtTable = __conn.prepareStatement(table.getCreateTableScript());
                stmtTable.executeUpdate();
                stmtTable.close();
                
                PreparedStatement stmtAfterScript = __conn.prepareStatement(table.getAfterScript());
                stmtAfterScript.executeUpdate();
                
                logStatus("t", table.getTableName(), 1);
            }
            
            String indexsScript = table.getAddIndexScript();
            
            if (!indexsScript.equals("")) {
                try {
                    PreparedStatement stmtIndexs = __conn.prepareStatement(indexsScript);
                    stmtIndexs.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            logStatus("t", table.getTableName(), 0);
            logStatus("sql_err", e.getMessage(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            PreparedStatement stmtAfterScript = __conn.prepareStatement(table.getAfterScript());
            stmtAfterScript.executeUpdate();
        } catch (SQLException ex) {
            
        }
    }
    
    private void verifyColumn(Connection __conn, MigrateTableModel table) {
        
        String columnLastCheck = "";
        try {
            
            String sqlCheckColumn = "";
            
            sqlCheckColumn = "SELECT column_name FROM information_schema.columns WHERE table_name = '" + table.getTableName() + "'";
            PreparedStatement stmtCol = __conn.prepareStatement(sqlCheckColumn);
            ResultSet rsCol = stmtCol.executeQuery();
            
            Vector<String> colExists = new Vector<String>();
            
            while (rsCol.next()) {
                colExists.add(rsCol.getString("column_name"));
            }
            
            rsCol.close();
            stmtCol.close();
            
            for (MigrateColumnModel column : table.getColumns()) {
                column.setExists(colExists.contains(column.getName()));
                if (!column.isExists()) {
                    columnLastCheck = column.getName();
                    
                    PreparedStatement stmtColAdd = __conn.prepareStatement(column.getAddColumnScript(table.getTableName()));
                    stmtColAdd.executeUpdate();
                    stmtColAdd.close();
                    
                    logStatus("c", column.getName(), 1);
                }
            }

            // set default value
            for (MigrateColumnModel column : table.getColumns()) {
                if (column.isExists()) {
                    
                    String __verifyDefaultQuery = column.getAlterColumnDefault(table.getTableName());
                    
                    if (__verifyDefaultQuery.length() > 0) {
                        PreparedStatement stmtVerifyDefaultValue = __conn.prepareStatement(__verifyDefaultQuery);
                        stmtVerifyDefaultValue.executeUpdate();
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            logStatus("c", columnLastCheck, 0);
            logStatus("sql_err", e.getMessage(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            logStatus("c", e.getMessage(), 0);
            logStatus("web_err", e.getMessage(), 0);
        }
    }
    
    private void sessionLog() {
        if (this.session != null) {
            this.session.setAttribute("verify_log", logVerify.toString());
        }
    }
    
    private void logStatus(String code, String msg, int status) {
//        logVerify.append(code + "," + msg + "," + status + "\n");
        logVerify.append(code + "," + msg + "," + status + "\n");
        sessionLog();
    }
    
}
