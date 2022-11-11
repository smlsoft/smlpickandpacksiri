var SERVER_URL = "warehouse-administration-list-1";

var arr_id = {};

var CURRENT_PAGE = 0;
var TOTAL_PAGE = 20;
var PAGE_SIZE = 0;
var PAGI_CURRENT_VALUE = null;

var IS_SHOWALL = false;
var IS_REFRESH = false;
var IS_SEARCH = false;
var IS_PRINT = false;
var IS_DELETE = false;
var IS_SCAN_DOCUMENT = false;
var CAN_SEARCH = false;

var tmpRefCode = null;
var tmpDocNO = null;
var tmpTransFlag = null;
var tmpIsPrint = null;
var tmpCarCode = null;
var tmpShipmentCode = null;

var tmpSendType = null;
var tmpStatusType = null;

$(function () {
    $(document).ready(function () {
        $("#content-search-box").find("#btn-scan-doc").attr("disabled", true);
        objErr = $("#content-error-list").clone();
        objPagination = $("#content-pagination-list").clone();
        $("#txt-search-date-from").inputmask('dd-mm-yyyy', {'placeholder': 'วว-ดด-ปปปป'});
        $("#txt-search-date-to").inputmask('dd-mm-yyyy', {'placeholder': 'วว-ดด-ปปปป'});
        $("#txt-search-date-from").datepicker(__getDatePickerOption());
        $("#txt-search-date-to").datepicker(__getDatePickerOption());

        $("#txt-search-date-from").datepicker('update', new Date());
        $("#txt-search-date-to").datepicker('update', new Date());

        tmpSendType = $("#sel-send-type").val();
        tmpStatusType = $("#sel-status-type").val();

        setTimeout(function () {
            $("#content-search-box").fadeIn("fast");
            $("#content-table-box").fadeIn("fast");
            $("#content-pagination-box").fadeIn("fast");
            $("#content-loading-box").fadeOut("fast");
            _refreshPAGE();
        }, 4000);



        timeout()

        function timeout() {
            setTimeout(function () {

                if (__checkPermDelete()) {
                    console.log('12345')
                    $.ajax({
                        url: __SUB_LINK + "getApproved",
                        type: "GET",
                        success: function (response) {
                            console.log(response)
                            if (response.data > 0) {
                                $('#body-detail').html(response.detail);
                                $('#confirmDeleteApprove').modal('show');
                            }
                        }
                    });
                }
                timeout();
            }, 18500);
        }


    });

    $("#delete-doc").on("click", function () {
        swal(__getDialogOption('คุณต้องการลบใบจัดทั้งหมด ใช่หรือไม่?')).then(function () {
            _deleteAllDoc();
        }, function (dissmiss) {
            if (dissmiss === "cancel") {
            }
        });
    });

    $("#content-search-box").on("keypress", "#txt-search", function (e) {
        if (e.keyCode === 13) {
            IS_SHOWALL = false;
            IS_SEARCH = true;
            CURRENT_PAGE = 0;
            var ref_code = $("#txt-search").val().trim();
            var sendData = {};
            if (ref_code !== "") {
                sendData.ref_code = ref_code;
            }
            _getMainDetail(sendData);
        }
    });

    $("#content-search-box").on("keypress", "#txt-search-customer", function (e) {
        if (e.keyCode === 13) {
            IS_SHOWALL = false;
            IS_SEARCH = true;
            CURRENT_PAGE = 0;
            var customer_name = $("#txt-search-customer").val().trim();
            var sendData = {};
            if (customer_name !== "") {
                sendData.customer_name = customer_name;
            }
            _getMainDetail(sendData);
        }
    });

    $("#content-search-box").on("click", "#btn-search", function () {
        IS_SHOWALL = false;
        IS_SEARCH = true;
        CURRENT_PAGE = 0;
        var ref_code = $("#txt-search").val().trim();
        var sendData = {};
        if (ref_code !== "") {
            sendData.ref_code = ref_code;
        }
        _getMainDetail(sendData);
    });

    $("#content-search-box").on("click", "#btn-search-customer", function () {
        IS_SHOWALL = false;
        IS_SEARCH = true;
        CURRENT_PAGE = 0;
        var customer_name = $("#txt-search-customer").val().trim();
        var sendData = {};
        if (customer_name !== "") {
            sendData.customer_name = customer_name;
        }
        _getMainDetail(sendData);
    });

    $("#content-search-box").on("change", "#txt-search-date-from", function () {
        if (CAN_SEARCH) {
            __updateActiveTimes();

            CURRENT_PAGE = 0;
            IS_SHOWALL = false;
            arr_id = {};
            _refreshPAGE();
        }
    });

    $("#content-search-box").on("change", "#txt-search-date-to", function () {
        if (CAN_SEARCH) {
            __updateActiveTimes();

            CURRENT_PAGE = 0;
            IS_SHOWALL = false;
            arr_id = {};
            _refreshPAGE();
        }
    });

    $("#content-search-box").on("change", "#sel-send-type", function () {
        __updateActiveTimes();
        CURRENT_PAGE = 0;
        tmpSendType = $(this).val();
        _refreshPAGE();
    });

    $("#content-search-box").on("change", "#sel-status-type", function () {
        __updateActiveTimes();
        CURRENT_PAGE = 0;
        tmpStatusType = $(this).val();
        _refreshPAGE();
    });

//    $("#content-search-box").on("click", "#btn-scan-doc", function () {
//        __updateActiveTimes();
//        _startQRCamera();
//    });

    $("#content-search-box").on("keypress", "#txt-scan-doc", function (e) {
        if (e.keyCode === 13) {
            __updateActiveTimes();
            IS_SCAN_DOCUMENT = true;
            var barcode_value = $(this).val();
            var type_code = barcode_value.split("-");
            switch (type_code[0]) {
                case "B":
                    _scanDocument(type_code[0], barcode_value);
                    break;
                case "E":
                    _scanDocument(type_code[0], barcode_value);
                    break;
                default :
                    __alertDialogMessage("รูปแบบใบจัดไม่ถูกต้อง", "error");
                    break;
            }
        }
    });


    $("#content-pagination-box").on("keydown", "#txt-pagination", function (e) {
        // Allow: backspace, delete, tab, escape, enter and .
        if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110]) !== -1 ||
                // Allow: Ctrl+A, Command+A
                        (e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) ||
                        // Allow: home, end, left, right, down, up
                                (e.keyCode >= 35 && e.keyCode <= 40)) {
                    // let it happen, don't do anything
                    return;
                }
                // Ensure that it is a number and stop the keypress
                if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
                    e.preventDefault();
                }
            });

    $("#content-pagination-box").on("keypress", "#txt-pagination", function (e) {
        if (e.keyCode === 13) {
            if ($(this).val() <= PAGE_SIZE && $(this).val() > 0) {
                CURRENT_PAGE = $(this).val();
                _refreshPAGE();
            } else {
                $(this).val(PAGI_CURRENT_VALUE);
            }
            $(this).blur();
        }
    });

    $("#content-pagination-box").on("focusin", "#txt-pagination", function () {
        PAGI_CURRENT_VALUE = $(this).val();
        $(this).val('');
    });

    $("#content-pagination-box").on("focusout", "#txt-pagination", function () {
        $(this).val(PAGI_CURRENT_VALUE);
    });

    $("#content-pagination-box").on("click", ".btn-pagination", function () {
        var page_id = $(this).attr("page-id");
        if (page_id >= 0) {
            CURRENT_PAGE = page_id;
            _refreshPAGE();
        }
    });


    $("#content-table-box").on("change", "#sel-table-rows", function () {
        __updateActiveTimes();
        CURRENT_PAGE = 0;
        TOTAL_PAGE = parseInt($(this).val());
        _refreshPAGE();
    });

    $("#content-table-box").on("click", "#btn-show-all", function () {
        __updateActiveTimes();

        IS_SHOWALL = true;
        CAN_SEARCH = false;
        CURRENT_PAGE = 0;
        tmpSendType = "";
        $("#sel-send-type").val('');
        $("#txt-search-customer").val('');
        $("#txt-search").val('');
        $("#txt-scan-doc").val('');
        $("#txt-search-date-from").datepicker('update', new Date());
        $("#txt-search-date-to").datepicker('update', new Date());
        _refreshPAGE();
    });

    $("#content-table-box").on("click", "#btn-refresh", function () {
        __updateActiveTimes();
        IS_REFRESH = true;
        _refreshPAGE();
    });


    $("#content-table-list").on("click", "#btn-more", function () {
        __updateActiveTimes();
        var key_id = $(this).attr("key_id");
        var car_code = $(this).attr("car_code");
        var shipment_code = $(this).attr("shipment_code");
        var result_id = key_id + "_" + car_code + "_" + shipment_code;

        if ($.isEmptyObject(arr_id[result_id])) {
            arr_id[result_id] = "1";
            _getSubDetail(result_id);
        } else {
            switch (arr_id[result_id]) {
                case "0":
                    arr_id[result_id] = "1";
                    _getSubDetail(result_id);
                    break;
                case "1":
                    arr_id[result_id] = "0";
                    $("#" + result_id).hide('fast');
                    break;
            }
        }
    });

    $("#content-table-list").on("click", "#btn-print", function () {
        __updateActiveTimes();
        IS_PRINT = true;
        var btn = $(this);
        tmpRefCode = btn.attr("key_id");
        tmpTransFlag = btn.attr("trans_flag");
        tmpIsPrint = btn.attr("is_print");
        tmpCarCode = btn.attr("car_code");
        tmpShipmentCode = btn.attr("shipment_code");

        _checkCancelDocument();
    });

    $("#content-table-list").on("click", "#btn-delete", function () {
        __updateActiveTimes();
        IS_DELETE = true;
        var btn = $(this);
        tmpRefCode = btn.attr("key_id");
        if (__checkPermDelete()) {
            swal(__getDialogOption('คุณต้องการลบเอกสาร \n' + tmpRefCode + '\n ใช่หรือไม่?')).then(function () {
                _deleteData();
            }, function (dissmiss) {
                if (dissmiss === "cancel") {
                }
            });
        }
    });
});

function _refreshPAGE() {
    var sendData = {};
    if (IS_SHOWALL) {
        _getMainDetail(sendData);
    } else {
        var ref_code = $("#txt-search").val();
        var scan_doc = $("#txt-scan-doc").val();
        var from_date = __formatDate($("#txt-search-date-from").val());
        var to_date = __formatDate($("#txt-search-date-to").val());

        if (ref_code !== "") {
            sendData.ref_code = ref_code;
        } else {
            if (from_date !== "NaN-NaN-NaN" && to_date !== "NaN-NaN-NaN") {
                sendData.from_date = from_date;
                sendData.to_date = to_date;
            }
        }

        if (scan_doc !== "") {
            var barcode_value = $("#txt-scan-doc").val();
            var type_code = barcode_value.split("-");
            switch (type_code[0]) {
                case "B":
                    _scanDocument2(type_code[0], barcode_value);
                    break;
                case "E":
                    _scanDocument2(type_code[0], barcode_value);
                    break;
                default :
                    __alertDialogMessage("รูปแบบใบจัดไม่ถูกต้อง", "error");
                    break;
            }
        } else {
            _getMainDetail(sendData);
        }
    }
    CAN_SEARCH = true;
}

function _getMainDetail(sendData) {
    var _offset = 0;
    if (CURRENT_PAGE > 0) {
        _offset = (CURRENT_PAGE - 1) * TOTAL_PAGE;
    }
    sendData.wh_code = __whCode;
    sendData.shelf_code = __shelfCode;
    sendData.offset = _offset;
    sendData.limit = TOTAL_PAGE;
    sendData.send_type = tmpSendType;
    sendData.status_type = tmpStatusType;

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_main_detail",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        beforeSend: function () {
            $("#content-table-list").html(__loadingText(11));
            $("#content-pagination-box").empty();
        },
        success: function (response) {
            $("#content-table-list").empty();
            if (response.success) {
                $("#content-table-list").html(response.data);
                if (__checkPermPrint()) {
                    $("#content-table-list").find(".is_print").removeAttr("disabled");
                }
                if (response.row_count > 0) {
                    $("#content-pagination-box").append(__createPagination(response.row_count));
                }
            } else {
                $("#content-table-list").html(response.data);
                __showErrorBox(response);
            }
        },
        complete: function () {
            CAN_SEARCH = true;
            if (IS_REFRESH || IS_PRINT || IS_SEARCH || IS_SCAN_DOCUMENT) {
                IS_REFRESH = false;
                IS_PRINT = false;
                IS_SEARCH = false;
                IS_SCAN_DOCUMENT = false;
                if (!$.isEmptyObject(arr_id)) {
                    _displayDetail(arr_id);
                }
            }
            $("html, body").animate({scrollTop: 0}, "fast");
        }
    });
}

function _getSubDetail(KEY_ID) {
    var split_key_id = KEY_ID.split("_");
    var car_code = "";
    var tmp = $("." + split_key_id[0]);
    for (i = 0; i < tmp.length; i++) {
        var _selector = $("." + split_key_id[0] + ":eq(" + i + ")").find("td:nth-child(5)").find("h5").text();
        if (split_key_id[1] === _selector) {
            car_code = split_key_id[1];
            break;
        }
    }

    if (car_code === "") {
        car_code = split_key_id[1].replace("-", " ");
    }

    var sendData = {};
    sendData.ref_code = split_key_id[0];
    sendData.wh_code = __whCode;
    sendData.shelf_code = __shelfCode;
    sendData.car_code = split_key_id[1] !== "null" ? car_code : "";
    sendData.shipment_code = split_key_id[2] !== "null" ? split_key_id[2] : "";
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_sub_detail",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                $("#content-table-list").find("#" + KEY_ID).html(response.data).show('fast');
            } else {
                $("#content-table-list").find("#" + KEY_ID).html(response.data).show('fast');
                __showErrorBox(response);
            }
        }
    });
}

function _displayDetail(key_id) {
    $.each(key_id, function (key, obj) {
        if (obj === "1") {
            _getSubDetail(key);
        }
    });
}

function _scanDocument(type_code, barcode) {
    var sendData = {
        type_code: type_code,
        barcode: barcode
    };

    $.ajax({
        url: __SUB_LINK + "global-services-1" + "?action_name=scan_document",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                if (response.data.length > 0) {
                    CURRENT_PAGE = 0;
                    IS_SHOWALL = false;
                    $.each(response.data, function (key, obj) {
                        tmpDocNO = obj.doc_no;
                        tmpRefCode = obj.ref_code;
                        tmpTransFlag = obj.trans_flag;

                    });
                    _getMainDetail({ref_code: tmpRefCode});
                }
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _checkCancelDocument() {
    var sendData = {
        key_id: tmpRefCode
    };
    $.ajax({
        url: __SUB_LINK + "global-services-1" + "?action_name=check_cancel_document_1",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                if (response.row_count > 0) {
                    if (response.row_count === 10) {
                        swal(__getDialogOption("เอกสารถูกยกเลิกแล้ว", "ต้องการยกเลิกใบจัด " + tmpRefCode + " ใช่หรือไม่?")).then(function () {
                            _cancelVerifyDocument();
                        }, function (dissmiss) {
                            if (dissmiss === "cancel") {
                            }
                        });
                    } else {
                        __alertDialogMessage("เอกสารถูกยกเลิกแล้ว", "error");
                    }
                } else {
                    if (IS_PRINT) {
                        _printData();
                    }
                }
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _cancelVerifyDocument() {
    var sendData = {
        key_id: tmpRefCode
    };
    $.ajax({
        url: __SUB_LINK + "global-services-1" + "?action_name=cancel_verify_document",
        method: "POST",
        cache: false,
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __alertToastMessage("ยกเลิกเอกสาร " + tmpRefCode + " เรียบร้อย.", "success");
            } else {
                __showErrorBox(response);
            }
        },
        error: function () {
            __alertDialogMessage("ทำรายการล้มเหลว", "error");
        },
        complete: function () {
            _refreshPAGE();
        }
    });
}

function _printData() {
    var strCarCode = tmpCarCode !== 'null' ? tmpCarCode : '';
    var strShipmentCode = tmpShipmentCode !== 'null' ? tmpShipmentCode : '';

    var _carCode = "";
    var tmp = $("." + tmpRefCode);
    for (i = 0; i < tmp.length; i++) {
        var _selector = $("." + tmpRefCode + ":eq(" + i + ")").find("td:nth-child(5)").find("h5").text();
        if (strCarCode === _selector) {
            _carCode = strCarCode;
            break;
        } else {
            _carCode = _selector;
        }
    }

    if (_carCode === "") {
        _carCode = _carCode.replace("-", " ");
    }

    var sendDatax = {
        key_id: tmpRefCode,
        car_code: _carCode
    };
    var targetURL = "../print/index.jsp";
    var sendData = [
        'ref_code=' + tmpRefCode,
        'wh_code=' + __whCode,
        'shelf_code=' + __shelfCode,
        'action_name=' + 'administration',
        'trans_flag=' + tmpTransFlag,
        'is_print=' + tmpIsPrint,
        'car_code=' + _carCode,
        'shipment_code=' + strShipmentCode
    ].join('&');
    $.ajax({
        url: __SUB_LINK + "global-services-1" + "?action_name=check_print_document_1",
        type: "GET",
        data: {data: JSON.stringify(sendDatax)},
        success: function (response) {
            if (response.success) {
                if (response.row_count > 0) {
                    if (__checkPermPrint()) {
                        window.open(targetURL + "?" + sendData, "_blank");
                    } else {
                        __alertDialogMessage("เอกสารถูกพิมพ์แล้ว", "error");
                    }
                } else {
                    window.open(targetURL + "?" + sendData, "_blank");
                }
            } else {
                __showErrorBox(response);
            }
        }
    });


    setTimeout(function () {
        _refreshPAGE();
    }, 1500);
}

function _deleteData() {
    var sendData = {
        key_id: tmpRefCode,
        action_name: "administration"
    };
    $.ajax({
        url: __SUB_LINK + 'global-services-1' + "?action_name=delete_document",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __alertToastMessage("ลบเอกสาร " + tmpRefCode + " เรียบร้อย.", "success");
            } else {
                __showErrorBox(response);
            }
        },
        complete: function () {
            _refreshPAGE();
        }
    });
}

function _deleteAllDoc() {
    var sendData = {
        key_id: tmpRefCode,
        action_name: "administration"
    };
    $.ajax({
        url: __SUB_LINK + 'delApprovedDuplicate',
        type: "GET",
        success: function (response) {
            if (response.success) {
                __alertToastMessage("ลบเอกสารเรียบร้อย.", "success");
                $('#confirmDeleteApprove').modal('hide')
            } else {
                __showErrorBox(response);
            }
        },
        complete: function () {
            _refreshPAGE();
        }
    });
}