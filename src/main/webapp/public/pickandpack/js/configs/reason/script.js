var SERVER_URL = "reason-list-1";

var CURRENT_PAGE = 0;
var TOTAL_PAGE = 20;
var PAGE_SIZE = 0;
var PAGI_CURRENT_VALUE = null;

var tmpKeyID = null;

$(function () {
    $(document).ready(function () {
        _formValidation();
        objErr = $("#content-error-list").clone();
        objPagination = $("#content-pagination-list").clone();

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
            CURRENT_PAGE = 0;
            var reason_code = $("#txt-search").val().trim();
            var sendData = {};
            if (reason_code !== "") {
                sendData.reason_code = reason_code;
            }
            _getMainDetail(sendData);
        }
    });

    $("#content-search-box").on("click", "#btn-search", function () {
        CURRENT_PAGE = 0;
        var reason_code = $("#txt-search").val().trim();
        var sendData = {};
        if (reason_code !== "") {
            sendData.reason_code = reason_code;
        }
        _getMainDetail(sendData);
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


    $("#content-form-box").on("click", "#btn-cancel", function () {
        __clearFormValues("#frm-reason");
        _refreshPAGE();
        $("#content-form-box").fadeOut("fast");
        $("#content-table-box").fadeIn("fast");
        $("#content-search-box").fadeIn("fast");
        $("#content-pagination-box").fadeIn("fast");
    });

    $("#content-table-box").on("change", "#sel-table-rows", function () {
        __updateActiveTimes();
        CURRENT_PAGE = 0;
        TOTAL_PAGE = parseInt($(this).val());
        _refreshPAGE();
    });

    $("#content-table-box").on("click", "#btn-add", function () {
        var _status = __checkPermAdd();
        if (_status) {
            $("#txt-search").val('');
            $("#content-form-box").fadeIn("fast");
            $("#content-table-box").fadeOut("fast");
            $("#content-search-box").fadeOut("fast");
            $("#content-pagination-box").fadeOut("fast");
            $("#txt_reason_code").attr("disabled", false).focus();
        }
    });

    $("#content-table-box").on("click", "#btn-refresh", function () {
        __updateActiveTimes();
        _refreshPAGE();
    });

    $("#content-table-list").on("click", "#btn-edit", function () {
        var _status = __checkPermUpdate();
        if (_status) {
            tmpKeyID = $(this).attr("key_id");
            _findReason();
        }
    });

    $("#content-table-list").on("click", "#btn-delete", function () {
        var _status = __checkPermDelete();
        if (_status) {
            tmpKeyID = $(this).attr("key_id");
            swal(__getDialogOption("คุณต้องการลบข้อมูล ใช่หรือไม่?")).then(function () {
                _deleteReason();
            }, function (dissmiss) {
                if (dissmiss === "cancel") {
                }
            });
        }
    });

});

function _formValidation() {
    $("#frm-reason").formValidation({
        framework: 'bootstrap',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        locale: 'th_TH',
        fields: {
            txt_reason_code: {
                validators: {
                    notEmpty: {}
                }
            },
            txt_reason_name: {
                validators: {
                    notEmpty: {}
                }
            }
        }
    }).on('success.form.fv', function (e) {
        e.preventDefault();
        swal(__getDialogOption('คุณต้องการบันทึกข้อมูล ใช่หรือไม่?')).then(function () {
            _saveReason();
        }, function (dissmiss) {
            if (dissmiss === "cancel") {
            }
        });
    });
}

function _refreshPAGE() {
    var sendData = {};
    var reason_code = $("#txt-search").val();

    if (reason_code !== "") {
        sendData.reason_code = reason_code;
    }
    _getMainDetail(sendData);
}

function _getMainDetail(sendData) {
    var _offset = 0;
    if (CURRENT_PAGE > 0) {
        _offset = (CURRENT_PAGE - 1) * TOTAL_PAGE;
    }
    sendData.offset = _offset;
    sendData.limit = TOTAL_PAGE;

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_main_detail",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        beforeSend: function () {
            $("#content-table-list").html(__loadingText(4));
            $("#content-pagination-box").empty();
        },
        success: function (response) {
            $("#content-table-list").empty();
            if (response.success) {
                $("#content-table-list").html(response.data);
                if (response.row_count > 0) {
                    $("#content-pagination-box").append(__createPagination(response.row_count));
                }
            } else {
                $("#content-table-list").html(response.data);
                __showErrorBox(response);
            }
        },
        complete: function () {
            $("html, body").animate({scrollTop: 0}, "fast");
        }
    });
}

function _findReason() {
    var sendData = {};
    sendData.key_id = tmpKeyID;
    console.log(sendData);

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=find_reason",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                $.each(response.data, function (key, obj) {
                    $("#txt_reason_code").val(obj.code);
                    $("#txt_reason_name").val(obj.name);
                });
                $("#content-form-box").fadeIn("fast");
                $("#content-table-box").fadeOut("fast");
                $("#content-search-box").fadeOut("fast");
                $("#content-pagination-box").fadeOut("fast");
                $("#txt_reason_name").focus();
                $("#txt_reason_code").attr("disabled", true);
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _saveReason() {
    var sendData = {};
    sendData.reason_code = $("#txt_reason_code").val();
    sendData.reason_name = $("#txt_reason_name").val();

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=save_reason",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __alertToastMessage("บันทึกข้อมูลเรียบร้อย.", "success");
                __clearFormValues("#frm-reason");
                _refreshPAGE();
                $("#content-form-box").fadeOut("fast");
                $("#content-table-box").fadeIn("fast");
                $("#content-search-box").fadeIn("fast");
                $("#content-pagination-box").fadeIn("fast");
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _deleteReason() {
    var sendData = {};
    sendData.key_id = tmpKeyID;

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=delete_reason",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __alertToastMessage("ลบข้อมูลเรียบร้อย.", "success");
                _refreshPAGE();
            } else {
                __showErrorBox(response);
            }
        }
    });
}