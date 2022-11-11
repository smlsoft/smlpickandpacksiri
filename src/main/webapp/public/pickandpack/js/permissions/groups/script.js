var SERVER_URL = "permission-group-list-1";
var CURRENT_PAGE = 0;
var TOTAL_PAGE = 20;
var PAGE_SIZE = 0;
var PAGI_CURRENT_VALUE = null;
var tmpKeyID = null;
var tmpGroupCode = null;
var tmpGroupName = null;
var tmpPageCode = null;
var tmpUserCode = null;
var tmpActionName = null;

$(function () {
    $(document).ready(function () {
        _formValidation();
        objErr = $("#content-error-list").clone();
        objPagination = $("#content-pagination-list").clone();
        setTimeout(function () {
            tmpActionName = "main_detail";
            $("#content-search-box").fadeIn("fast");
            $("#content-table-group-box").fadeIn("fast");
            $("#content-pagination-box").fadeIn("fast");
            $("#content-loading-box").fadeOut("fast");
            _refreshPAGE();
        }, 4000);
    });
    $("#content-search-box").on("keypress", "#txt-search", function (e) {
        if (e.keyCode === 13) {
            var search_value = $("#txt-search").val().trim();
            var sendData = {};
            switch (tmpActionName) {
                case "main_detail":
                    if (search_value !== "") {
                        sendData.group_code = search_value;
                    }
                    _getMainDetail(sendData);
                    break;
                case "user_group":
                    if (search_value !== "") {
                        sendData.group_code = search_value;
                    }
                    _findUserGroup(sendData);
                    break;
                case "user_list":
                    if (search_value !== "") {
                        sendData.search_value = search_value;
                    }
                    _getUserList(sendData);
                    break;
            }
        }
    });

    $("#content-search-box").on("click", "#btn-search", function () {
        CURRENT_PAGE = 0;
        var search_value = $("#txt-search").val().trim();
        var sendData = {};
        switch (tmpActionName) {
            case "main_detail":
                if (search_value !== "") {
                    sendData.group_code = search_value;
                }
                _getMainDetail(sendData);
                break;
            case "user_group":
                if (search_value !== "") {
                    sendData.group_code = search_value;
                }
                _findUserGroup({});
                break;
            case "user_list":
                if (search_value !== "") {
                    sendData.search_value = search_value;
                }
                _getUserList(sendData);
                break;
        }
    });
    // ##############################################################################################################################

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
                switch (tmpActionName) {
                    case "main_detail":
                        _refreshPAGE();
                        break;
                    case "user_group":
                        _findUserGroup({});
                        break;
                    case "user_list":
                        _getUserList({});
                        break;
                }
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
            switch (tmpActionName) {
                case "main_detail":
                    _refreshPAGE();
                    break;
                case "user_group":
                    _findUserGroup({});
                    break;
                case "user_list":
                    _getUserList({});
                    break;
            }
        }
    });
    // ##############################################################################################################################

    $("#content-form-group-box").on("click", "#btn-cancel", function () {
        __clearFormValues("#frm-group");
        _refreshPAGE();
        $("#content-form-group-box").fadeOut("fast");
        $("#content-table-group-box").fadeIn("fast");
        $("#content-search-box").fadeIn("fast");
        $("#content-pagination-box").fadeIn("fast");
        $("#frm-group").find("#txt_group_code").removeAttr("disabled");
    });

    $("#content-table-group-box").on("change", "#sel-table-rows", function () {
        __updateActiveTimes();
        CURRENT_PAGE = 0;
        TOTAL_PAGE = parseInt($(this).val());
        _refreshPAGE();
    });

    $("#content-table-group-box").on("click", "#btn-add", function () {
        __updateActiveTimes();
        $("#content-form-group-box").fadeIn("fast");
        $("#content-table-group-box").fadeOut("fast");
        $("#content-search-box").fadeOut("fast");
        $("#content-pagination-box").fadeOut("fast");
        $("#frm-group").find("#txt_group_code").focus();
    });

    $("#content-table-group-box").on("click", "#btn-refresh", function () {
        __updateActiveTimes();
        _refreshPAGE();
    });

    $("#content-table-group-box").on("click", "#content-table-list #btn-manage", function () {
        __updateActiveTimes();
        var _status = __checkPermUpdate();
        if (_status) {
            CURRENT_PAGE = 0;
            PAGE_SIZE = 0;
            TOTAL_PAGE = 20;
            tmpKeyID = $(this).attr("key_id");
            tmpGroupCode = $(this).attr("key_id");
            tmpGroupName = $(this).attr("key_name");
            $("#content-table-group-box").fadeOut("fast");
            $("#content-search-box").fadeOut("fast");
            $("#txt-search").val('');
            _findPermGroup();
            _findUserGroup({});
        }
    });

    $("#content-table-group-box").on("click", "#content-table-list #btn-edit", function () {
        __updateActiveTimes();
        var _status = __checkPermUpdate();
        if (_status) {
            tmpKeyID = $(this).attr("key_id");
            _findGroup();
        }
    });

    $("#content-table-group-box").on("click", "#content-table-list #btn-delete", function () {
        __updateActiveTimes();
        var _status = __checkPermDelete();
        if (_status) {
            tmpKeyID = $(this).attr("key_id");
            swal(__getDialogOption("คุณต้องการลบข้อมูล ใช่หรือไม่?")).then(function () {
                _deleteGroup();
            }, function (dissmiss) {
                if (dissmiss === "cancel") {
                }
            });
        }
    });

    // ##############################################################################################################################

    $("#content-table-permission-group-box").on("click", "#btn-add", function () {
        __updateActiveTimes();
        CURRENT_PAGE = 0;
        PAGE_SIZE = 0;
        TOTAL_PAGE = 20;
        _getPageList();
    });

    $("#content-table-permission-group-box").on("click", "#btn-back", function () {
        __updateActiveTimes();
        CURRENT_PAGE = 0;
        PAGE_SIZE = 0;
        TOTAL_PAGE = 20;
        $("#content-table-permission-group-box").fadeOut("fast");
        $("#content-table-user-group-box").fadeOut("fast");
        $("#content-table-user-group-box").fadeOut("fast");
        $("#content-table-group-box").fadeIn("fast");
        $("#content-search-box").fadeIn("fast");
        _refreshPAGE();
    });

    $("#content-table-permission-group-box").on("ifChanged", "#content-table-list input.chb_r_status.flat", function () {
        __updateActiveTimes();
        var sendData = {};
        var page_code = $(this).parents("tr").attr("page_code");
        var group_code = $(this).parents("tr").attr("group_code");
        var status = false;
        if ($(this).is(":checked")) {
            status = true;
        }
        sendData.page_code = page_code;
        sendData.group_code = group_code;
        sendData.status = status;
        sendData.update_key = "read";
        _updatePermGroup(sendData);
    });

    $("#content-table-permission-group-box").on("ifChanged", "#content-table-list input.chb_c_status.flat", function () {
        __updateActiveTimes();
        var sendData = {};
        var page_code = $(this).parents("tr").attr("page_code");
        var group_code = $(this).parents("tr").attr("group_code");
        var status = false;
        if ($(this).is(":checked")) {
            status = true;
        }
        sendData.page_code = page_code;
        sendData.group_code = group_code;
        sendData.status = status;
        sendData.update_key = "create";
        _updatePermGroup(sendData);
    });

    $("#content-table-permission-group-box").on("ifChanged", "#content-table-list input.chb_u_status.flat", function () {
        __updateActiveTimes();
        var sendData = {};
        var page_code = $(this).parents("tr").attr("page_code");
        var group_code = $(this).parents("tr").attr("group_code");
        var status = false;
        if ($(this).is(":checked")) {
            status = true;
        }
        sendData.page_code = page_code;
        sendData.group_code = group_code;
        sendData.status = status;
        sendData.update_key = "update";
        _updatePermGroup(sendData);
    });

    $("#content-table-permission-group-box").on("ifChanged", "#content-table-list input.chb_d_status.flat", function () {
        __updateActiveTimes();
        var sendData = {};
        var page_code = $(this).parents("tr").attr("page_code");
        var group_code = $(this).parents("tr").attr("group_code");
        var status = false;
        if ($(this).is(":checked")) {
            status = true;
        }
        sendData.page_code = page_code;
        sendData.group_code = group_code;
        sendData.status = status;
        sendData.update_key = "delete";
        _updatePermGroup(sendData);
    });

    $("#content-table-permission-group-box").on("ifChanged", "#content-table-list input.chb_p_status.flat", function () {
        __updateActiveTimes();
        var sendData = {};
        var page_code = $(this).parents("tr").attr("page_code");
        var group_code = $(this).parents("tr").attr("group_code");
        var status = false;
        if ($(this).is(":checked")) {
            status = true;
        }
        sendData.page_code = page_code;
        sendData.group_code = group_code;
        sendData.status = status;
        sendData.update_key = "print";
        _updatePermGroup(sendData);
    });

    $("#content-table-permission-group-box").on("click", "#content-table-list #btn-remove", function () {
        __updateActiveTimes();
        var sendData = {};
        var page_code = $(this).parents("tr").attr("page_code");
        var group_code = $(this).parents("tr").attr("group_code");
        sendData.page_code = page_code;
        sendData.group_code = group_code;
        swal(__getDialogOption('คุณต้องการนำออก ใช่หรือไม่?')).then(function () {
            _deletePermGroup(sendData);
        }, function (dissmiss) {
            if (dissmiss === "cancel") {
            }
        });
    });

    $("#content-table-page-box").on("click", "#btn-back", function () {
        __clearFormValues("#frm-group");
        _findPermGroup();
        _findUserGroup({});
        $("#content-table-page-box").fadeOut("fast");
        $("#content-pagination-box").fadeIn("fast");
    });

    $("#content-table-page-box").on("click", "#content-table-list #btn-import", function () {
        __updateActiveTimes();
        tmpPageCode = $(this).attr("key_id");
        _createPermGroup();
    });

    // ##############################################################################################################################

    $("#content-table-user-group-box").on("click", "#btn-add", function () {
        __updateActiveTimes();
        CURRENT_PAGE = 0;
        PAGE_SIZE = 0;
        TOTAL_PAGE = 20;
        $("#txt-search").val('');
        _getUserList({});
    });

    $("#content-table-user-group-box").on("change", "#sel-table-rows", function () {
        __updateActiveTimes();
        CURRENT_PAGE = 0;
        TOTAL_PAGE = parseInt($(this).val());
        _findUserGroup({});
    });

    $("#content-table-user-list-box").on("change", "#sel-table-rows", function () {
        __updateActiveTimes();
        CURRENT_PAGE = 0;
        TOTAL_PAGE = parseInt($(this).val());
        _getUserList({});
    });

    $("#content-table-user-list-box").on("click", "#btn-back", function () {
        __updateActiveTimes();
        CURRENT_PAGE = 0;
        PAGE_SIZE = 0;
        TOTAL_PAGE = 20;
        _findPermGroup();
        _findUserGroup({});
        $("#content-table-user-list-box").fadeOut("fast");
        $("#content-search-box").fadeOut("fast");
    });

    $("#content-table-user-list-box").on("click", "#content-table-list #btn-import", function () {
        __updateActiveTimes();
        CURRENT_PAGE = 0;
        PAGE_SIZE = 0;
        TOTAL_PAGE = 20;
        tmpUserCode = $(this).attr("key_id");
        _createUserGroup();
    });

    $("#content-table-user-group-box").on("click", "#content-table-list #btn-remove", function () {
        __updateActiveTimes();
        var sendData = {};
        var user_code = $(this).parents("tr").attr("user_code");
        var group_code = $(this).parents("tr").attr("group_code");
        sendData.user_code = user_code;
        sendData.group_code = group_code;
        swal(__getDialogOption('คุณต้องการนำออก ใช่หรือไม่?')).then(function () {
            _deleteUserGroup(sendData);
        }, function (dissmiss) {
            if (dissmiss === "cancel") {
            }
        });
    });
});

function _formValidation() {
    $("#frm-group").formValidation({
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
            }
        }
    }).on('success.form.fv', function (e) {
        e.preventDefault();
        swal(__getDialogOption('คุณต้องการบันทึกข้อมูล ใช่หรือไม่?')).then(function () {
            _saveGroup();
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
    tmpActionName = "main_detail";
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
            $("#content-table-group-box").find("#content-table-list").html(__loadingText(4));
            $("#content-pagination-box").empty();
        },
        success: function (response) {
            $("#content-table-list").empty();
            if (response.success) {
                if (response.row_count > 0) {
                    $("#content-pagination-box").append(__createPagination(response.row_count));
                }
                $("#content-table-group-box").find("#content-table-list").html(response.data);
            } else {
                __showErrorBox(response);
                $("#content-table-group-box").find("#content-table-list").html(response.data);
            }
        },
        complete: function () {
            $("html, body").animate({scrollTop: 0}, "fast");
        }
    });
}

function _findGroup() {
    var sendData = {group_code: tmpKeyID};
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=find_group",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                $.each(response.data, function (key, obj) {
                    $("#frm-group").find("#txt_group_code").val(obj.group_code).attr("disabled", true);
                    $("#frm-group").find("#txt_group_name").val(obj.group_name);
                });
                $("#content-form-group-box").fadeIn("fast");
                $("#content-table-group-box").fadeOut("fast");
                $("#content-search-box").fadeOut("fast");
                $("#content-pagination-box").fadeOut("fast");
            } else {
                __showErrorBox(response);
            }
        },
        complete: function () {
            $("html, body").animate({scrollTop: 0}, "fast");
        }
    });
}

function _saveGroup() {
    __updateActiveTimes();
    var sendData = {group_code: $("#txt_group_code").val(), group_name: $("#txt_group_name").val()};
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=save_group",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __alertToastMessage("บันทึกข้อมูลเรียบร้อย", "success");
                __clearFormValues("#frm-group");
                _refreshPAGE();
                $("#content-form-group-box").fadeOut("fast");
                $("#content-table-group-box").fadeIn("fast");
                $("#content-search-box").fadeIn("fast");
                $("#content-pagination-box").fadeIn("fast");
                $("#frm-group").find("#txt_group_code").removeAttr("disabled");
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _deleteGroup() {
    __updateActiveTimes();
    var sendData = {group_code: tmpKeyID};

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=delete_group",
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


// ##################################################################################################################

function _findPermGroup() {
    var sendData = {};
    sendData.group_code = tmpKeyID;
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=find_permission_group",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        beforeSend: function () {
            $("#content-table-group-box").find("#content-table-list").html(__loadingText(7));
        },
        success: function (response) {
            if (response.success) {
                $("#content-table-permission-group-box").fadeIn("fast");
                $("#content-table-permission-group-box").find("#content-table-list").html(response.data);
                $("#content-table-permission-group-box").find("#content-table-list input.flat").iCheck({checkboxClass: "icheckbox_flat-green"});
            } else {
                __showErrorBox(response);
                $("#content-table-permission-group-box").find("#content-table-list").html(response.data);
            }
        },
        complete: function () {
            $("html, body").animate({scrollTop: 0}, "fast");
        }
    });
}

function _getPageList() {
    var sendData = {group_code: tmpKeyID};

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_page_list",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        beforeSend: function () {
            $("#content-table-page-box").find("#content-table-list").html(__loadingText(2));
        },
        success: function (response) {
            if (response.success) {
                $("#content-table-page-box").find("#content-table-list").html(response.data);
                $("#content-table-page-box").fadeIn("fast");
                $("#content-table-permission-group-box").fadeOut("fast");
                $("#content-table-user-group-box").fadeOut("fast");
                $("#content-pagination-box").fadeOut("fast");
            } else {
                __showErrorBox(response);
                $("#content-table-page-box").find("#content-table-list").html(response.data);
            }
        },
        complete: function () {
            $("html, body").animate({scrollTop: 0}, "fast");
        }
    });
}

function _createPermGroup() {
    var sendData = {
        group_code: tmpGroupCode,
        group_name: tmpGroupName,
        page_code: tmpPageCode
    };

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=create_permission_group",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __alertToastMessage("บันทึกข้อมูลเรียบร้อย", "success");
                __clearFormValues("#frm-group");
                _findPermGroup();
                _findUserGroup({});
                $("#content-table-page-box").fadeOut("fast");
                $("#content-pagination-box").fadeIn("fast");
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _updatePermGroup(sendData) {

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=update_permission_group",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __alertToastMessage("บันทึกข้อมูลเรียบร้อย", "success");
                _findPermGroup();
                $("#content-table-page-box").fadeOut("fast");
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _deletePermGroup(sendData) {
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=delete_permission_group",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __alertToastMessage("ลบข้อมูลเรียบร้อย", "success");
                _findPermGroup();
            } else {
                __showErrorBox(response);
            }
        }
    });
}

// ##################################################################################################################

function _findUserGroup(sendData) {
    tmpActionName = "user_group";
    var _offset = 0;
    if (CURRENT_PAGE > 0) {
        _offset = (CURRENT_PAGE - 1) * TOTAL_PAGE;
    }

    sendData.group_code = tmpKeyID;
    sendData.offset = _offset;
    sendData.limit = TOTAL_PAGE;

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=find_user_group",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        beforeSend: function () {
            $("#content-table-user-group-box").find("#content-table-list").html(__loadingText(2));
            $("#content-pagination-box").empty();
        },
        success: function (response) {
            if (response.success) {
                $("#content-table-user-group-box").fadeIn("fast");
                $("#content-table-user-group-box").find("#content-table-list").html(response.data);
                $("#content-pagination-box").append(__createPagination(response.row_count));
            } else {
                __showErrorBox(response);
                $("#content-table-user-group-box").find("#content-table-list").html(response.data);
            }
        },
        complete: function () {
            $("html, body").animate({scrollTop: 0}, "fast");
        }
    });
}

function _getUserList(sendData) {
    tmpActionName = "user_list";
    var _offset = 0;
    if (CURRENT_PAGE > 0) {
        _offset = (CURRENT_PAGE - 1) * TOTAL_PAGE;
    }
    sendData.group_code = tmpKeyID;
    sendData.offset = _offset;
    sendData.limit = TOTAL_PAGE;

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_user_list",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        beforeSend: function () {
            $("#content-table-user-list-box").find("#content-table-list").html(__loadingText(2));
            $("#content-pagination-box").empty();
        },
        success: function (response) {
            if (response.success) {
                $("#content-table-user-list-box").find("#content-table-list").html(response.data);
                $("#content-table-user-list-box").fadeIn("fast");
                $("#content-search-box").fadeIn("fast");
                $("#content-table-permission-group-box").fadeOut("fast");
                $("#content-table-user-group-box").fadeOut("fast");
                $("#content-pagination-box").append(__createPagination(response.row_count));
            } else {
                __showErrorBox(response);
                $("#content-table-page-box").find("#content-table-list").html(response.data);
            }
        },
        complete: function () {
            $("html, body").animate({scrollTop: 0}, "fast");
        }
    });
}

function _createUserGroup() {
    var sendData = {
        group_code: tmpKeyID,
        user_code: tmpUserCode
    };
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=create_user_group",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                $("#content-table-user-list-box").fadeOut("fast");
                $("#content-search-box").fadeOut("fast");
                __alertToastMessage("บันทึกข้อมูลเรียบร้อย", "success");
                _findUserGroup({});
                _findPermGroup();
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _deleteUserGroup(sendData) {
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=delete_user_group",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __alertToastMessage("ลบข้อมูลเรียบร้อย", "success");
                _findUserGroup({});
            } else {
                __showErrorBox(response);
            }
        }
    });
}