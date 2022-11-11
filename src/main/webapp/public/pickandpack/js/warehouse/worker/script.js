var SERVER_URL = "warehouse-worker-list-1";

var arr_id = {};

var objPagination = null;
var objErr = null;

var CURRENT_PAGE = 0;
var TOTAL_PAGE = 20;
var PAGE_SIZE = 0;
var PAGI_CURRENT_VALUE = null;

var IS_SHOWALL = false;
var IS_REFRESH = false;
var IS_SEARCH = false;
var IS_PRINT = false;
var IS_SCAN_DOCUMENT = false;
var IS_CLOSE_DOCUMENT = false;
var IS_CONFIRM_DOCUMENT = false;
var IS_UPDATE_EVENT_QTY = false;
var CAN_SEARCH = false;

var tmpRefCode = null;
var tmpDocNo = null;
var tmpQTY = null;
var tmpEventQTY = null;
var tmpRemark = null;
var tmpStatus = null;
var tmpIcCode = null;
var tmpTransFlag = null;
var tmpIsPrint = null;

var tmpSendType = null;
var tmpStatusType = null;

$(function () {
    $(document).ready(function () {
        _formValidation();

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

        setTimeout(function () {
            $("#content-search-box").fadeIn("fast");
            $("#content-table-box").fadeIn("fast");
            $("#content-pagination-box").fadeIn("fast");
            $("#content-loading-box").fadeOut("fast");
            _refreshPAGE();
        }, 4000);
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

    $("#content-table-list").on("keypress", ".input-qty", function (e) {
        if (e.keyCode === 13) {
            IS_UPDATE_EVENT_QTY = true;
            __updateActiveTimes();
            var _input = $(this);
            tmpEventQTY = _input.val();
            tmpDocNo = _input.attr("doc_no");
            tmpRefCode = _input.attr("ref_code");
            tmpQTY = _input.attr("qty");
            tmpRemark = _input.attr("remark");
            tmpStatus = _input.attr("status");
            tmpIcCode = _input.attr("ic_code");

            _checkCancelDocument();
        }
    });


    $("#content-table-list").on("click", "#btn-more", function () {
        __updateActiveTimes();
        var key_id = $(this).attr("key_id");

        if ($.isEmptyObject(arr_id[key_id])) {
            arr_id[key_id] = "1";
            _getSubDetail(key_id);
        } else {
            switch (arr_id[key_id]) {
                case "0":
                    arr_id[key_id] = "1";
                    _getSubDetail(key_id);
                    break;
                case "1":
                    arr_id[key_id] = "0";
                    $("#" + key_id).hide('fast');
                    break;
            }
        }
    });

    $("#content-table-list").on("click", "#btn-print", function () {
        __updateActiveTimes();
        IS_PRINT = true;
        var btn = $(this);
        tmpDocNo = btn.attr("key_id");
        tmpRefCode = btn.attr("ref_code");
        tmpTransFlag = btn.attr("trans_flag");
        tmpIsPrint = btn.attr("is_print");

        _checkCancelDocument();
    });

    $("#content-table-list").on("click", "#btn-close-doc", function () {
        __updateActiveTimes();

        IS_CLOSE_DOCUMENT = true;
        var btn = $(this);
        tmpDocNo = btn.attr("key_id");
        tmpRefCode = btn.attr("ref_code");
        tmpTransFlag = btn.attr("trans_flag");
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

function _formValidation() {
    $("#frm-mod-reason").formValidation({
        framework: 'bootstrap',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        locale: 'th_TH',
        fields: {
            sel_mod_reason: {
                validators: {
                    notEmpty: {}
                }
            }
        }
    }).on('success.form.fv', function (e) {
        e.preventDefault();
        swal(__getDialogOption('ข้อความระบบ', 'คุณต้องการบันทึกข้อมูล ใช่หรือไม่?')).then(function () {
            tmpRemark = $("#sel_mod_reason").val();
            $("#content-mod-reason-box").modal("hide");
            _updateEventQTY();
            __clearFormValues("#frm-mod-reason");
        }, function (dissmiss) {
            if (dissmiss === "cancel") {
            }
        });
    });
}

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
                    _scanDocument(type_code[0], barcode_value);
                    break;
                case "E":
                    _scanDocument(type_code[0], barcode_value);
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
            $("#content-table-list").html(__loadingText(13));
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
            if (IS_REFRESH || IS_PRINT || IS_SEARCH || IS_UPDATE_EVENT_QTY || IS_CONFIRM_DOCUMENT || IS_SCAN_DOCUMENT) {
                IS_REFRESH = false;
                IS_PRINT = false;
                IS_SEARCH = false;
                IS_UPDATE_EVENT_QTY = false;
                IS_CONFIRM_DOCUMENT = false;
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
    var sendData = {
        doc_no: KEY_ID,
        wh_code: __whCode,
        shelf_code: __shelfCode
    };
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

function _getReason() {
    var sendData = {};
    $.ajax({
        url: __SUB_LINK + "global-services-1" + "?action_name=get_reasons",
        type: 'GET',
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                var __rsHTML = "";
                if (response.data.length > 0) {
                    __rsHTML = "<option value=''>กรุณาเลือกเหตุผลการไม่อนุมัติ</option>";
                    $.each(response.data, function (key, obj) {
                        __rsHTML += "<option value='" + obj.code + "'>" + obj.name + "</option>";
                    });
                } else {
                    __rsHTML = "<option value=''>ไม่พบข้อมูล</option>";
                }
                $("#sel_mod_reason").html(__rsHTML);
                $("#content-mod-reason-box").modal('show');
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
                    if (IS_CONFIRM_DOCUMENT) {
                        swal(__getDialogOption("คอนเฟิร์มใบจัด", "เอกสารเลขที่ " + tmpDocNo + " ใช่หรือไม่?")).then(function () {
                            _confirmDocument();
                        }, function (dissmiss) {
                            if (dissmiss === "cancel") {
                            }
                        });
                    }
                    if (IS_CLOSE_DOCUMENT) {
                        swal(__getDialogOption("ปิดใบจัด", "เอกสารเลขที่ " + tmpDocNo + " ใช่หรือไม่?")).then(function () {
                            _closeDocument();
                        }, function (dissmiss) {
                            if (dissmiss === "cancel") {
                            }
                        });
                    }
                    if (IS_UPDATE_EVENT_QTY) {
                        if (parseFloat(tmpQTY) !== parseFloat(tmpEventQTY)) {
                            if (parseFloat(tmpEventQTY) < parseFloat(tmpQTY)) {
                                _getReason();
                            } else {
                                __alertDialogMessage("จำนวนที่จัดได้	 ต้องไม่มากกว่า ยอดสั่งจัด", "error");
                            }
                        } else {
                            _updateEventQTY();
                        }
                    }
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

function _updateEventQTY() {
    var sendData = {
        doc_no: tmpDocNo,
        event_qty: tmpEventQTY,
        remark: tmpRemark !== null ? tmpRemark : "-",
        status: parseInt(tmpStatus),
        ic_code: tmpIcCode
    };

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=update_event_qty",
        method: "POST",
        cache: false,
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                tmpRemark = null;
                __alertToastMessage("แก้ไขข้อมูลเรียบร้อย.", "success");
                _refreshPAGE();
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _closeDocument() {
    var sendData = {
        doc_no: tmpDocNo,
        trans_flag: tmpTransFlag,
        ref_code: tmpRefCode
    };
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=close_document",
        method: 'POST',
        cache: false,
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                $("#txt-scan-doc").val('');
                __alertToastMessage("ปิดใบจัดเรียบร้อย", "success");
                _refreshPAGE();
                IS_CLOSE_DOCUMENT = false;
            } else {
                __showErrorBox(response);
            }
        },
        error: function () {
            __alertDialogMessage("ทำรายการล้มเหลว", "error");
        }
    });
}

function _confirmDocument() {
    __updateActiveTimes();
    var sendData = {
        doc_no: tmpDocNo,
        trans_flag: tmpTransFlag,
        ref_code: tmpRefCode
    };
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=confirm_document",
        method: 'POST',
        cache: false,
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                $("#txt-scan-doc").val('');
                __alertToastMessage("คอนเฟิร์มใบจัดเรียบร้อย.", "success");
                _refreshPAGE();
                IS_CONFIRM_DOCUMENT = false;
            } else {
                __showErrorBox(response);
            }
        },
        error: function () {
            __alertDialogMessage("ทำรายการล้มเหลว", "error");
        }
    });
}

function _scanDocument(type_code, barcode) {
    var sendData = {
        type_code: type_code,
        barcode: barcode
    };
    $.ajax({
        url: __SUB_LINK + 'global-services-1' + "?action_name=scan_document",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                if (response.data.length > 0) {
                    CURRENT_PAGE = 0;
                    IS_SHOWALL = false;
                    $.each(response.data, function (key, obj) {
                        tmpDocNo = obj.doc_no;
                        tmpRefCode = obj.ref_code;
                        tmpTransFlag = obj.trans_flag;

                    });
                    _getMainDetail({ref_code: tmpRefCode, wh_code: __whCode});
                    _checkType(type_code);
                }
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _checkType(document_type) {
    switch (document_type) {
        case "E":
            IS_CLOSE_DOCUMENT = true;
            break;
        case "B":
            IS_CONFIRM_DOCUMENT = true;
            break;
    }
    _checkCancelDocument();
}

function _startQRCamera() {
    var targetURL = "https://cloud2.smlsoft.com/simpleqrcam/";
    var currentURL = window.shelf_code.href;

    var newURL = currentURL.split("?");

    window.shelf_code.href = targetURL + "?pickandpack=" + newURL[0];
}

function _printData() {

    var sendDatax = {
        key_id: tmpDocNo
    };

    var targetURL = "../print/index.jsp";
    var sendData = [
        'ref_code=' + tmpRefCode,
        'doc_no=' + tmpDocNo,
        'wh_code=' + __whCode,
        'shelf_code=' + __shelfCode,
        'action_name=' + 'worker',
        'trans_flag=' + tmpTransFlag,
        'is_print=' + tmpIsPrint
    ].join('&');

    $.ajax({
        url: __SUB_LINK + "global-services-1" + "?action_name=check_print_document_2",
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
        action_name: "worker"
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