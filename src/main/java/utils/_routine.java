/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author admin
 */
public class _routine {

    public String _loginGuid = "";

    public byte[] _scale(byte[] fileData, int width, int height) {
        ByteArrayInputStream __in = new ByteArrayInputStream(fileData);
        try {
            BufferedImage __img = ImageIO.read(__in);
            if (height == 0) {
                height = (width * __img.getHeight()) / __img.getWidth();
            }
            if (width == 0) {
                width = (height * __img.getWidth()) / __img.getHeight();
            }
            Image __scaledImage = __img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage __imageBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            __imageBuff.getGraphics().drawImage(__scaledImage, 0, 0, new Color(0, 0, 0), null);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            ImageIO.write(__imageBuff, "jpg", buffer);

            return buffer.toByteArray();
        } catch (Exception e) {
        }
        return null;
    }

    public byte[] _getImage(Connection conn, String id) throws Exception {
        return _getImage(conn, id, 90, 180);
    }

    public byte[] _getImage(Connection conn, String id, int width, int height)
            throws Exception, SQLException {
        try {
            byte[] __imgData = null;
            Statement __stmt = conn.createStatement();

            // Query
            String __req = "select image_file from images where image_id = '" + id + "'";
            ResultSet __rset = __stmt.executeQuery(__req);
            if (__rset.next()) {
                __imgData = __rset.getBytes("image_file");
            }
            __rset.close();
            __stmt.close();
            return _scale(__imgData, 90, 180);
        } catch (Exception ex) {
        }
        return null;
    }

    public Connection _connect() {
        return this._connect(_global._databaseName);
    }

    public Connection _connect(String databaseName) {
        StringBuilder __dbConnectUrl = new StringBuilder();
        Connection __conn = null;
        try {// PostgreSQL
            String __dbClassName = "org.postgresql.Driver";
            __dbConnectUrl.append("jdbc:postgresql");
            String __dbDataPort = "5432";
            __dbConnectUrl.append("://").append(_global._databaseServer).append(":").append(__dbDataPort);
            if (databaseName.length() != 0) {
                __dbConnectUrl.append("/").append(databaseName.toLowerCase());
            }
            Class.forName(__dbClassName).newInstance();
            __conn = DriverManager.getConnection(__dbConnectUrl.toString(), _global._databaseUserCode, _global._databaseUserPassword);
            try ( // PostgreSql
                    Statement __stmt = __conn.createStatement()) {
                __stmt.execute("set enable_seqscan=false");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException __ex) {
            String __error = __ex.getMessage();
        }
        return __conn;
    }

    public Connection _connect(String databaseName, String configFileName) {

        if (configFileName.equals("")) {
            return this._connect(databaseName);
        }

        String databaseServer = "";
        String databaseUserCode = "";
        String databaseUserPassword = "";
        String databasePort = "";
        try {
            String __xReloadFile = _readXmlFile(configFileName);
            DocumentBuilderFactory __docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder __docBuilder = __docBuilderFactory.newDocumentBuilder();
            Document __doc = __docBuilder.parse(new InputSource(new StringReader(__xReloadFile)));
            __doc.getDocumentElement().normalize();
            NodeList __listOfData = __doc.getElementsByTagName("node");
            Node __firstNode = __listOfData.item(0);
            if (__firstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element __firstElement = (Element) __firstNode;
                // ---
                databaseServer = _xmlGetNodeValue(__firstElement, "server");
                databaseUserCode = _xmlGetNodeValue(__firstElement, "user");
                databaseUserPassword = _xmlGetNodeValue(__firstElement, "password");
                databasePort = _xmlGetNodeValue(__firstElement, "port");

            }

        } catch (SAXParseException __err) {
//            System.out.println("** Parsing error" + ", line " + __err.getLineNumber() + ", uri " + __err.getSystemId());
//            System.out.println(" " + __err.getMessage());
        } catch (SAXException __ex) {
            Exception __x = __ex.getException();
        } catch (Throwable __t) {
        }

        return this._connect(databaseServer, databaseName, databaseUserCode, databaseUserPassword, databasePort);
    }

    public Connection _connect(String databaseServer, String databaseName, String databaseUserCode, String databaseUserPassword, String strDatabasePort) {
        StringBuilder __dbConnectUrl = new StringBuilder();
        Connection __conn = null;

        try {// PostgreSQL
            String __dbClassName = "org.postgresql.Driver";
            __dbConnectUrl.append("jdbc:postgresql");
            String __dbDataPort = "5432";
            if (!strDatabasePort.equals("")) {
                __dbDataPort = strDatabasePort;
            }
            __dbConnectUrl.append("://").append(databaseServer).append(":").append(__dbDataPort);
            if (databaseName.length() != 0) {
                __dbConnectUrl.append("/").append(databaseName.toLowerCase());
            }
            Class.forName(__dbClassName).newInstance();
//            System.out.println(__dbConnectUrl.toString()+", "+ _global._databaseUserCode+", "+  _global._databaseUserPassword);
            __conn = DriverManager.getConnection(__dbConnectUrl.toString(), databaseUserCode, databaseUserPassword);
            try ( // PostgreSql
                    Statement __stmt = __conn.createStatement()) {
                __stmt.execute("set enable_seqscan=false");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException __ex) {
            String __error = __ex.getMessage();
            System.out.println("failed connect :: " + __dbConnectUrl.toString() + ", " + databaseUserCode + ", " + databaseUserPassword);
        }
        return __conn;
    }

    /**
     * อ่านไฟล์ xml
     *
     * @param xmlName ไฟล์ xml ที่ต้องการอ่าน
     * @return
     */
    public String _readXmlFile(String xmlName) {
        String __readLine = "";
        try {
            // Reader __input = new InputStreamReader(new FileInputStream(xmlName));
            //     BufferedReader __in = new BufferedReader(__input);
            String __tempDir = System.getProperty("java.io.tmpdir");
            BufferedReader __in = new BufferedReader(new InputStreamReader(new FileInputStream(__tempDir + "/" + xmlName), "UTF8"));
            char[] __cBuf = new char[65536];
            StringBuilder __stringBuf = new StringBuilder();
            int __readThisTime = 0;
            while (__readThisTime != -1) {
                try {
                    __readThisTime = __in.read(__cBuf, 0, 65536);
                    __stringBuf.append(__cBuf, 0, __readThisTime);
                } catch (Exception __ex) {
                }
            } // end while
            __readLine = __stringBuf.toString();
            __in.close();
        } catch (Exception __ex) {
            System.out.println("_readXmlFile:" + __ex.getMessage());
            __readLine = __ex.getMessage();
        }
        return __readLine;
    }

    // ดึงตัวแปรจาก Tag XML
    /**
     * ตัวช่วยสำหรับดึงค่า Value ออกจาก XML
     *
     * @param firstElement Element ที่ต้องการ
     * @param tagName ชื่อ Tag ที่ต้องการ
     * @return ข้อมูลที่อยู่ระหว่าง Tag (Value)
     */
    //private
    public String _xmlGetNodeValue(Element firstElement, String tagName) {
        try {
            NodeList __firstNameList = firstElement.getElementsByTagName(tagName);
            if (__firstNameList.getLength() > 0) {
                Element __firstNameElement = (Element) __firstNameList.item(0);
                NodeList __textFNList = __firstNameElement.getChildNodes();
                if (__textFNList.getLength() > 0) {
                    Node __getData = __textFNList.item(0);
                    return __getData.getNodeValue().trim();
                }
            }
        } catch (Exception __ex) {
            System.out.println("_xmlGetNodeValue:" + __ex.getMessage());
        }
        return "";
    }

    public Integer _getRowCount(Connection conn, String __strQUERY) throws SQLException {
        Integer __rowCount;
        PreparedStatement __stmt1;
        ResultSet __rsData1;

        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        __rsData1.last();
        __rowCount = __rsData1.getRow();
        __stmt1.close();
        __rsData1.close();

        return __rowCount;
    }

    public Integer _getRowCount2(Connection conn, String __strQUERY) throws SQLException {
        Integer __rowCount = 0;
        PreparedStatement __stmt1;
        ResultSet __rsData1;

        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        while (__rsData1.next()) {
            __rowCount = __rsData1.getInt("count");
        }

        __stmt1.close();
        __rsData1.close();

        return __rowCount;
    }

    public String _convertHours(String strDate) throws Exception {
        String hourwork = "";
        String[] arr_time = strDate.split(Pattern.quote("-"));
        String _day = arr_time[0];
        String _hour = arr_time[1];
        String _min = arr_time[2];

        if (!_day.equals("00")) {
            hourwork = _day + "วัน ";
        }
        if (!_hour.equals("00")) {
            hourwork += _hour + "ชม. ";
        }

        hourwork += _min + "นาที";

        return hourwork;
    }

    public String _convertDateTime(String strDate) throws Exception {
        String __rsDate;
        if (!strDate.equals("-")) {
            String[] arr_time = strDate.split(Pattern.quote(":"));
            String[] arr_date = arr_time[0].split(Pattern.quote("-"));
            String[] arr_date_2 = arr_date[2].split(" "); // get day

            String _year = arr_date[0];
            String _month = "";
            String _day = arr_date_2[0];

            switch (arr_date[1]) { // month
                case "01":
                    _month = "ม.ค.";
                    break;
                case "02":
                    _month = "ก.พ.";
                    break;
                case "03":
                    _month = "มี.ค.";
                    break;
                case "04":
                    _month = "เม.ย.";
                    break;
                case "05":
                    _month = "พ.ค.";
                    break;
                case "06":
                    _month = "มิ.ย.";
                    break;
                case "07":
                    _month = "ก.ค.";
                    break;
                case "08":
                    _month = "ส.ค.";
                    break;
                case "09":
                    _month = "ก.ย.";
                    break;
                case "10":
                    _month = "ต.ค.";
                    break;
                case "11":
                    _month = "พ.ย.";
                    break;
                case "12":
                    _month = "ธ.ค.";
                    break;
            }

            if (arr_date_2[1].equals("00") && arr_time[1].equals("00")) {
                __rsDate = _day + " " + _month + " " + _year;
            } else {
                __rsDate = _day + " " + _month + " " + _year + " เวลา " + arr_date_2[1] + ":" + arr_time[1];
            }
        } else {
            __rsDate = strDate;
        }

        return __rsDate;
    }

    public String _convertTime(String strTime) throws Exception {
        String __rsTime;
        if (!strTime.equals("-")) {

            String[] arr_time = strTime.split("-");
            String __strDays = arr_time[0];
            String __strHours = arr_time[1];
            String __strMinutes = arr_time[2];
            __strDays = __strDays.equals("00") ? "" : Integer.parseInt(__strDays) + " วัน - ";
            __strHours = __strHours.equals("00") ? "" : Integer.parseInt(__strHours) + " ชม. - ";
            __strMinutes = __strMinutes.equals("00") ? "" : Integer.parseInt(__strMinutes) + " นาที";

            if (__strDays.equals("") && __strHours.equals("") && __strMinutes.equals("")) {
                __rsTime = "0 นาที";
            } else {
                __rsTime = __strDays + __strHours + __strMinutes;
            }
        } else {
            __rsTime = "0 นาที";
        }

        return __rsTime;
    }

    public String _convertDate(String strDate) throws Exception {
        String __rsDate;
        if (!strDate.equals("-")) {
            String[] arr_date = strDate.split(Pattern.quote("-"));

            String _year = arr_date[0];
            String _month = "";
            String _day = arr_date[2];

            switch (arr_date[1]) { // month
                case "01":
                    _month = "ม.ค.";
                    break;
                case "02":
                    _month = "ก.พ.";
                    break;
                case "03":
                    _month = "มี.ค.";
                    break;
                case "04":
                    _month = "เม.ย.";
                    break;
                case "05":
                    _month = "พ.ค.";
                    break;
                case "06":
                    _month = "มิ.ย.";
                    break;
                case "07":
                    _month = "ก.ค.";
                    break;
                case "08":
                    _month = "ส.ค.";
                    break;
                case "09":
                    _month = "ก.ย.";
                    break;
                case "10":
                    _month = "ต.ค.";
                    break;
                case "11":
                    _month = "พ.ย.";
                    break;
                case "12":
                    _month = "ธ.ค.";
                    break;
            }

            __rsDate = _day + " " + _month + " " + _year;
        } else {
            __rsDate = strDate;
        }

        return __rsDate;
    }
}
