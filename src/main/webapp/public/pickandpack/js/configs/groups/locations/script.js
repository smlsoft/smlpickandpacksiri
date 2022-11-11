var SERVER_URL = "group-locations-list-1";

var arr_id = {};

var CURRENT_PAGE = 0;
var TOTAL_PAGE = 20;
var PAGE_SIZE = 0;
var PAGI_CURRENT_VALUE = null;

var IS_REFRESH = false;
var IS_SEARCH = false;
var IS_FIND_DATA = false;

var tmpKeyID = null;
var tmpShelfCode = null;
var tmpOldShelfCode = null;

$(function () {
    $(document).ready(function () {
//        _formValidation();
        objErr = $("#content-error-list").clone();
        objPagination = $("#content-pagination-list").clone();

        setTimeout(function () {
            $("#content-search-box").fadeIn("fast");
            $("#content-table-box").fadeIn("fast");
            $("#content-pagination-box").fadeIn("fast");
            $("#content-loading-box").fadeOut("fast");
            _refreshPAGE();
            _getWareHouse();
        }, 4000);
    });

    $("#content-search-box").on("keypress", "#txt-search", function (e) {
        if (e.keyCode === 13) {
            IS_SEARCH = true;
            CURRENT_PAGE = 0;
            var group_code = $("#txt-search").val().trim();
            var sendData = {};
            if (group_code !== "") {
                sendData.group_code = group_code;
            }
            _getMainDetail(sendData);
        }
    });

    $("#content-search-box").on("click", "#btn-search", function () {
        IS_SEARCH = true;
        CURRENT_PAGE = 0;
        var group_code = $("#txt-search").val().trim();
        var sendData = {};
        if (group_code !== "") {
            sendData.group_code = group_code;
        }
        _getMainDetail(sendData);
    });

    $("#content-form-box").on("click", "#btn-cancel", function () {
        __clearSelect2();
        __clearFormValues("#frm-group-location");
        $("#txt_group_code").attr("disabled", false).focus();
        _refreshPAGE();
        $("#content-form-box").fadeOut("fast");
        $("#content-table-box").fadeIn("fast");
        $("#content-search-box").fadeIn("fast");
        $("#content-pagination-box").fadeIn("fast");
    });

    $("#content-form-box").on("change", "#sel_warehouse_code", function () {
        var tmpVal = $(this).val();
        if (tmpVal !== null) {
            if (!IS_FIND_DATA) {
                tmpOldShelfCode = $("#sel_shelf_code").val();
            }
            _getShelfCode(__addCommaData(tmpVal, 2));
            $("#sel_shelf_code").attr("disabled", false);
        } else {
            $("#sel_shelf_code").empty().attr("disabled", true);
        }
    });

    $("#content-form-box").on("click", "#btn-submit", function () {
        _saveGroupLocation();
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

    $("#content-table-box").on("click", "#btn-add", function () {
        var _status = __checkPermAdd();
        if (_status) {
            $("#txt-search").val('');
            $("#content-form-box").fadeIn("fast");
            $("#content-table-box").fadeOut("fast");
            $("#content-search-box").fadeOut("fast");
            $("#content-pagination-box").fadeOut("fast");
            $("#txt_group_code").focus();
        }
    });

    $("#content-table-box").on("click", "#btn-refresh", function () {
        __updateActiveTimes();
        IS_REFRESH = true;
        _refreshPAGE();
    });


    $("#content-table-list").on("click", "#btn-more", function () {
        __updateActiveTimes();
        var key_id = $(this).attr("key_id");
        tmpShelfCode = $(this).attr("shelf_code");

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

    $("#content-table-list").on("click", "#btn-edit", function () {
        var _status = __checkPermUpdate();
        if (_status) {
            tmpKeyID = $(this).attr("key_id");
            _findGroupLocation();
        }
    });

    $("#content-table-list").on("click", "#btn-delete", function () {
        var _status = __checkPermDelete();
        if (_status) {
            tmpKeyID = $(this).attr("key_id");
            swal(__getDialogOption("คุณต้องการลบข้อมูล ใช่หรือไม่?")).then(function () {
                _deleteGroupLocation();
            }, function (dissmiss) {
                if (dissmiss === "cancel") {
                }
            });
        }

    });

});

function _formValidation() {
    $("#frm-group-location").formValidation({
        framework: 'bootstrap',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        locale: 'th_TH',
        fields: {
            txt_group_code: {
                validators: {
                    notEmpty: {}
                }
            },
            txt_group_name: {
                validators: {
                    notEmpty: {}
                }
            },
            sel_warehouse_code: {
                validators: {
                    notEmpty: {}
                }
            },
            sel_shelf_code: {
                validators: {
                    notEmpty: {}
                }
            }
        }
    }).on('success.form.fv', function (e) {
        e.preventDefault();
        swal(__getDialogOption('คุณต้องการบันทึกข้อมูล ใช่หรือไม่?')).then(function () {
            _saveGroupLocation();
        }, function (dissmiss) {
            if (dissmiss === "cancel") {
            }
        });
    });
}

function _refreshPAGE() {
    var sendData = {};
    var group_code = $("#txt-search").val();

    if (group_code !== "") {
        sendData.group_code = group_code;
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
            $("#content-table-list").html(__loadingText(5));
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
            CAN_SEARCH = true;
            if (IS_REFRESH || IS_SEARCH) {
                IS_REFRESH = false;
                IS_SEARCH = false;
                if (!$.isEmptyObject(arr_id)) {
                    _displayDetail(arr_id);
                }
            }
            $("html, body").animate({scrollTop: 0}, "fast");
        }
    });
}

function _getSubDetail(key_id) {
    var sendData = {};
    sendData.shelf_code = __addCommaData(tmpShelfCode.split(","), 2);
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_sub_detail",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                $("#content-table-list").find("#" + key_id).html(response.data).show('fast');
            } else {
                $("#content-table-list").find("#" + key_id).html(response.data).show('fast');
                __showErrorBox(response);
            }
        }
    });
}

function _getWareHouse() {
    var sendData = {};
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_warehouse",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            var __rsHTML = "";
            if (response.success) {
                if (response.data.length > 0) {
                    $.each(response.data, function (key, obj) {
                        __rsHTML += "<option key_id='" + key + "' value='" + obj.code + "'>" + obj.code + " ~ " + obj.name_1 + "</option>";
                    });
                } else {
                    __rsHTML += "<option value=''>ไม่พบข้อมูล</option>";
                }
                $("#sel_warehouse_code").css('width', '100%').select2({placeholder: 'กรุณาเลือกข้อมูล'});
                $("#sel_shelf_code").css('width', '100%').select2({placeholder: 'กรุณาเลือกข้อมูล'}).attr("disabled", true);
                ;
                $("#sel_warehouse_code").html(__rsHTML);
                $("#content-system-id-box").fadeOut('fast');
                $("#content-warehouse-box").fadeIn('fast');
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _getShelfCode(warehouse_code) {
    var sendData = {wh_code: warehouse_code};

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_shelf_code",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            var __rsHTML = "";
            if (response.success) {
                if (response.data.length > 0) {
                    $.each(response.data, function (key, obj) {
                        __rsHTML += "<option key_id='" + key + "' value='" + obj.code + "'>" + obj.code + " ~ " + obj.name_1 + "</option>";
                    });
                } else {
                    __rsHTML += "<option value=''>ไม่พบข้อมูล</option>";
                }
                $("#sel_shelf_code").css('width', '100%').select2({placeholder: 'กรุณาเลือกข้อมูล'}).attr("disabled", false);
                $("#sel_shelf_code").html(__rsHTML);
            } else {
                __showErrorBox(response);
            }
        },
        complete: function () {
            if (tmpOldShelfCode !== null && tmpOldShelfCode !== "") {
                $("#sel_shelf_code").val(tmpOldShelfCode).trigger("change");
                IS_FIND_DATA = false;
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

function _findGroupLocation() {
    var sendData = {group_code: tmpKeyID};

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=find_group_location",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                $.each(response.data, function (key, obj) {
                    IS_FIND_DATA = true;
                    tmpOldShelfCode = obj.location.split(",");
                    $("#txt_group_code").val(obj.group_code);
                    $("#txt_group_name").val(obj.name);
                    $("#sel_warehouse_code").val(obj.whcode.split(",")).trigger("change");
                });
                $("#content-form-box").fadeIn("fast");
                $("#content-table-box").fadeOut("fast");
                $("#content-search-box").fadeOut("fast");
                $("#content-pagination-box").fadeOut("fast");
                $("#txt_group_code").attr("disabled", true);
                $("#txt_group_name").focus();
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _saveGroupLocation() {
    var sendData = {};
    sendData.group_code = $("#txt_group_code").val();
    sendData.group_name = $("#txt_group_name").val();
    sendData.wh_code = __addCommaData($("#sel_warehouse_code").val(), 1);
    sendData.shelf_code = __addCommaData($("#sel_shelf_code").val(), 1);

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=save_group_location",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __clearSelect2();
                __alertToastMessage("บันทึกข้อมูลเรียบร้อย.", "success");
                __clearFormValues("#frm-group-location");
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

function _deleteGroupLocation() {
    var sendData = {group_code: tmpKeyID};

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=delete_group_location",
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