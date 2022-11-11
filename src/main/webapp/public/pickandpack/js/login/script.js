var SUB_LINK = "";
var SERVER_URL = "authentication-list-1";

var objListDatabase;
var tmpProviderCode = null;
var tmpUserCode = null;
var tmpUserName = null;
var tmpDatabaseName = null;
var tmpBranchCode = null;
var tmpBranchName = null;
var tmpSystemID = null;

var log = console.log.bind();
var isErr = false;

var jxLoad = null;

$(function () {
    $(document).ready(function () {
        objListDatabase = $("#content-database-2-list").clone();
        getSystemID();
        $.backstretch("public/media/img/background-1.jpg");
        Pace.restart();
        _formValidation();
        $("#txt_provider_code").focus();
    });

    $("a").on("click", function (e) {
        e.preventDefault();
    });

    $("#content-database-box").on("click", "#content-table-list tr", function () {
        tmpDatabaseName = $(this).attr("key_id");
//        _verifyDatabase();
        $("#content-database-box").fadeOut("fast");
        _getBranchList();
    });

    $("#content-branch-box").on("click", "#content-table-list tr", function () {
        tmpBranchCode = $(this).attr("key_id");
        tmpBranchName = $(this).attr("key_name");
        _checkTMSConfigDate();
    });

    $("#content-database-box").on("click", "#btn-relogin", function () {
        $("#content-database-list").empty();
        $("#content-authentication-box").fadeIn("fast");
        $("#content-database-box").fadeOut("fast");
        $("#txt_provider_code").focus();
    });

    $("#content-branch-box").on("click", "#btn-relogin", function () {
        $("#content-table-branch-list").empty();
        $("#content-authentication-box").fadeIn("fast");
        $("#content-branch-box").fadeOut("fast");
        $("#txt_provider_code").focus();
    });

    $("#content-system-id-box").on("click", "#btn-relogin", function () {
        $("#content-table-branch-list").empty();
        $("#content-authentication-box").fadeIn("fast");
        $("#content-system-id-box").fadeOut("fast");
        $("#txt_provider_code").focus();
    });

    $("#content-system-id-box").on("click", "#btn-add-warehouse", function () {
        _getWareHouse();
    });

    $("#content-warehouse-box").on("click", "#btn-back", function () {
        $("#content-system-id-box").fadeIn('fast');
        $("#content-warehouse-box").fadeOut('fast');
        _clearFormValues("#frm-warehouse");
    });

    $("#content-warehouse-box").on("change", "#sel_warehouse_code", function () {
        var tmpVal = $(this).val();
        if (tmpVal !== null) {
            $("#sel_shelf_code").attr("disabled", false);
            _getShelfCode(_addCommaData(tmpVal, 2));
        } else {
            $("#sel_shelf_code").empty().attr("disabled", true);
        }
    });
});

function _login() {
    var sendData = {
        provider_code: $("#txt_provider_code").val(),
        user_code: $("#txt_user_code").val(),
        user_password: $("#txt_user_password").val()
    };
    $.ajax({
        url: SUB_LINK + SERVER_URL + "?action_name=authentication",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        beforeSend: function () {
            $("#btn-submit").html("<i class='fa fa-spin fa-refresh'></i> กำลังตรวจสอบ");
        },
        success: function (response) {
            if (response.success) {
                if (response.is_authen) {
                    _clearFormValues("#frm-login");
                    tmpProviderCode = response.provider_code;
                    tmpUserCode = response.user_code;
                    tmpUserName = response.user_name;
                    $("#content-database-box").find("#content-table-list").html(response.data);
                    $("#content-database-box").fadeIn("fast");
                    $("#content-authentication-box").fadeOut("fast");
                } else {
                    _alertDialogMessage("เข้าสู่ระบบไม่สำเร็จ", "error");
                    $("#txt_provider_code").focus();
                }
            } else {
                _alertDialogMessage("ไม่สามารถเชื่อมต่อฐานข้อมูลได้", "error");
            }
        },
        complete: function () {
            $("#btn-submit").html("เข้าสู่ระบบ").removeAttr("disabled").removeClass("disabled");
        }
    });
}

function _verifyDatabase() {
    var sendData = {
        database_name: tmpDatabaseName,
        provider_code: tmpProviderCode
    };
    $.ajax({
        url: "verify-db-process",
        method: "POST",
        data: sendData,
        beforeSend: function () {
            $("#content-verify-database-box").fadeIn("fast");
            $("#content-database-box").fadeOut("fast");
        },
        success: function (response) {
            jxLoad = setTimeout(function () {
                var stop = $('#log').data('stop');
                if (stop !== 1 && !isErr) {
                    _getBranchList();
                } else {
                    console.log(response);
                }
            }, 500);
        }
    });
    _getVerifyLogs();
}

function _getVerifyLogs() {
    var sendData = {};
    $.ajax({
        url: SUB_LINK + "verify-log",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            $('#log').text('');
            var logs = response.split('\n');
            var isComplete = false;
            for (var i = 0; i < logs.length; i++) {
                (function (i) {
                    if (logs[i] !== '') {
                        var strLog = logs[i].split(',');

                        if (strLog[0] !== 'complete') {
                            isErr = strLog[2] === 0;
                            $('#log').append(_convertVerifyLogs(strLog[0]) + ' : ' + strLog[1] + ' <span style="color: ' + ((parseInt(strLog[2]) === 1 ? 'green' : 'red')) + ';"> ' + (parseInt(strLog[2]) === 1 ? 'done' : 'fail') + '</span>\n');
                            if (jxLoad !== null && isErr) {
                                clearTimeout(jxLoad);
                                log('cancel');
                            }
                        } else if (strLog[0] === 'wait') {
                        } else {
                            isComplete = true;
                            $('#log').append('<span style="color: green;">Complete</span>\n\n');
                        }
                    }
                })(i);
            }
            if (!isComplete) {
                setTimeout(_getVerifyLogs, 500);
            }
        }
    });
}

function _getBranchList() {
    var sendData = {
        database_name: tmpDatabaseName,
        provider_code: tmpProviderCode,
        user_code: tmpUserCode
    };
    $.ajax({
        url: SUB_LINK + SERVER_URL + "?action_name=get_branch_list",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                switch (response.change_branch_code) {
                    case 0:
                        tmpBranchCode = response.branch_code;
                        tmpBranchName = response.branch_name;
                        _checkTMSConfigDate();
                        $("#content-verify-database-box").fadeOut("fast");
                        break;
                    case 1:
                        $("#content-branch-box").find("#content-table-list").html(response.data);
                        $("#content-branch-box").fadeIn("fast");
                        $("#content-verify-database-box").fadeOut("fast");
                        break;
                }
            } else {
                console.log(response);
            }
        }
    });
}

function _getGroupLocation() {
    var sendData = {
        database_name: tmpDatabaseName,
        provider_code: tmpProviderCode
    };
    $.ajax({
        url: SUB_LINK + SERVER_URL + "?action_name=get_group_location",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            var __rsHTML = "";
            if (response.success) {
                if (response.data.length > 0) {
                    __rsHTML += "<option value=''>เลือกข้อมูล</option>";
                    $.each(response.data, function (key, obj) {
                        __rsHTML += "<option key_id='" + key + "' value='" + obj.group_code + "'>" + obj.group_code + " ~ " + obj.name + "</option>";
                    });
                } else {
                    __rsHTML += "<option value=''>ไม่พบข้อมูล</option>";
                }
                $("#sel_group_code").html(__rsHTML);
                $("#txt_system_id").val(tmpSystemID);
                $("#content-branch-box").fadeOut('fast');
                $("#content-system-id-box").fadeIn('fast');
            } else {

            }
        }
    });
}

function _getWareHouse() {
    var sendData = {
        database_name: tmpDatabaseName,
        provider_code: tmpProviderCode
    };
    $.ajax({
        url: SUB_LINK + SERVER_URL + "?action_name=get_warehouse",
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

            }
        }
    });
}

function _getShelfCode(warehouse_code) {
    var sendData = {
        database_name: tmpDatabaseName,
        provider_code: tmpProviderCode,
        wh_code: warehouse_code
    };
    $.ajax({
        url: SUB_LINK + SERVER_URL + "?action_name=get_shelf_code",
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

            }
        }
    });
}

function _checkTMSConfigDate() {
    var sendData = {
        database_name: tmpDatabaseName,
        provider_code: tmpProviderCode
    };
    $.ajax({
        url: SUB_LINK + SERVER_URL + "?action_name=find_tms_config_date",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                if (response.row_count > 0) {
                    _alertToastMessage("การ กำหนดวันที่เอกสารเริ่มต้นที่จะดึง เรียบร้อย.", "success");
                    _checkSystemID();
                } else {
                    $("#txt_begin_date").inputmask('dd-mm-yyyy', {'placeholder': 'วว-ดด-ปปปป'});
                    $("#txt_begin_date").datepicker(_getDatePickerOption());
                    $("#txt_begin_date").datepicker('update', new Date());
                    $("#content-tms-config-date-box").fadeIn("fast");
                    $("#content-branch-box").fadeOut("fast");
                }
            } else {

            }
        }
    });
}

function _checkSystemID() {
    tmpSystemID = _getCookie("system_id");
    var sendData = {
        database_name: tmpDatabaseName,
        provider_code: tmpProviderCode,
        system_id: tmpSystemID,
        user_code: tmpUserCode,
        user_name: tmpUserName,
        branch_code: tmpBranchCode,
        branch_name: tmpBranchName
    };
    $.ajax({
        url: SUB_LINK + SERVER_URL + "?action_name=find_system_id",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                if (response.row_count > 0) {
                    window.location.href = "index.jsp";
                } else {
                    _getGroupLocation();
                }
            } else {
                console.log(response);
            }
        }
    });
}


function _saveGroupLocation() {
    var sendData = {
        database_name: tmpDatabaseName,
        provider_code: tmpProviderCode,
        group_code: $("#txt_group_code").val(),
        group_name: $("#txt_group_name").val(),
        wh_code: _addCommaData($("#sel_warehouse_code").val(), 1),
        shelf_code: _addCommaData($("#sel_shelf_code").val(), 1)
    };
    $.ajax({
        url: SUB_LINK + SERVER_URL + "?action_name=save_group_location",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                if (response.data.length > 0) {
                    _alertDialogMessage(response.data, "error");
                    $("button[type='submit']").removeAttr("disabled").removeClass("disabled");
                } else {
                    _alertToastMessage("บันทึกข้อมูลเรียบร้อย", "success");
                    _clearFormValues("#frm-warehouse");
                    $("#content-warehouse-box").fadeOut("fast");
                    _checkSystemID();
                }
            } else {
                console.log(response);
            }
        }
    });
}

function _saveTMSConfigDate() {
    var sendData = {
        database_name: tmpDatabaseName,
        provider_code: tmpProviderCode,
        begin_date: _formatDate($("#txt_begin_date").val())
    };
    $.ajax({
        url: SUB_LINK + SERVER_URL + "?action_name=save_tms_config_date",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                _alertToastMessage("บันทึกข้อมูลเรียบร้อย.", "success");
                _clearFormValues("#frm-tms-config-date");
                $("#content-tms-config-date-box").fadeOut();
                _checkSystemID();
            } else {
                console.log(response);
            }
        }
    });
}

function _saveSystemID() {
    var sendData = {
        database_name: tmpDatabaseName,
        provider_code: tmpProviderCode,
        group_code: $("#sel_group_code").val(),
        system_id: $("#txt_system_id").val()
    };
    $.ajax({
        url: SUB_LINK + SERVER_URL + "?action_name=save_group_system",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                _alertToastMessage("บันทึกข้อมูลเรียบร้อย.", "success");
                _clearFormValues("#frm-system-id");
                $("#content-system-id-box").fadeOut('fast');
                _checkSystemID();
            } else {
                console.log(response);
            }
        }
    });
}


// ############################################################################

function _formValidation() {
    $("#frm-login").formValidation({
        framework: 'bootstrap',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        locale: 'th_TH',
        fields: {
            txt_provider_code: {
                validators: {
                    notEmpty: {}
                }
            },
            txt_user_code: {
                validators: {
                    notEmpty: {}
                }
            },
            txt_user_password: {
                validators: {
                    notEmpty: {}
                }
            }
        }
    }).on('success.form.fv', function (e) {
        e.preventDefault();
        _login();
    });
    $("#frm-system-id").formValidation({
        framework: 'bootstrap',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        locale: 'th_TH',
        fields: {
            system_id: {
                validators: {
                    notEmpty: {}
                }
            },
            sel_group_code: {
                validators: {
                    notEmpty: {}
                }
            }
        }
    }).on('success.form.fv', function (e) {
        e.preventDefault();
        swal(_getDialogOption('คุณต้องการบันทึกข้อมูล ใช่หรือไม่?')).then(function () {
            _saveSystemID();
        }, function (dissmiss) {
            if (dissmiss === "cancel") {
            }
        });
    });
    $("#frm-warehouse").formValidation({
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
        swal(_getDialogOption('คุณต้องการบันทึกข้อมูล ใช่หรือไม่?')).then(function () {
            _saveGroupLocation();
        }, function (dissmiss) {
            if (dissmiss === "cancel") {
            }
        });
    });
    $("#frm-tms-config-date").formValidation({
        framework: 'bootstrap',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        locale: 'th_TH',
        fields: {
            txt_begin_date: {
                validators: {
                    notEmpty: {}
                }
            }
        }
    }).on('success.form.fv', function (e) {
        e.preventDefault();
        swal(_getDialogOption('คุณต้องการบันทึกข้อมูล ใช่หรือไม่?')).then(function () {
            _saveTMSConfigDate();
        }, function (dissmiss) {
            if (dissmiss === "cancel") {
            }
        });
    });
}

function _clearFormValues(frm) {
    var $parent = $(".form-group");
    $parent.removeClass('has-success');
    $parent.removeClass('has-error');
    $parent.find("[data-fv-icon-for]").hide();
    $parent.find('.help-block').hide();
    $(frm)[0].reset();
}

function _alertToastMessage(strMessage, strType) { // type => 'success', 'info', 'warning', 'error'
    toastr.options = {
        "closeButton": false,
        "debug": false,
        "newestOnTop": true,
        "progressBar": false,
        /*
         positionClass
         toast-top-right, toast-top-left, toast-bottom-right,
         toast-bottom-left, toast-top-full-width, toast-bottom-full-width, toast-top-center, toast-bottom-center
         */
        "positionClass": "toast-top-right",
        "preventDuplicates": false,
        "onclick": null,
        "showDuration": "300",
        "hideDuration": "1000",
        "timeOut": "5000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };
    toastr[strType](strMessage);
}

function _getDialogOption(strMessage) {
    return {
        allowOutsideClick: false,
        allowEscapeKey: false,
        title: 'ข้อความระบบ',
        text: strMessage,
        type: 'question',
        showCancelButton: true,
        confirmButtonColor: '#00a65a',
        cancelButtonColor: '#d33',
        confirmButtonText: 'ยืนยัน',
        cancelButtonText: 'ยกเลิก',
        animation: false,
        customClass: 'animated bounceIn'
    };
}

function _getDatePickerOption() {
    return {
        autoclose: true,
        todayHighlight: true,
        language: 'th',
        format: "dd-mm-yyyy"
    };
}

function _alertDialogMessage(strMessage, strType) { // type => 'success', 'info', 'warning', 'error'
    swal({
        allowOutsideClick: false,
        allowEscapeKey: false,
        title: 'ข้อความระบบ',
        type: strType,
        text: strMessage,
        confirmButtonText: 'ตกลง',
        animation: false,
        customClass: 'animated bounceIn'
    });
}

function _addCommaData(arr, type) {
    var strResult = "";
    switch (parseInt(type)) {
        case 1:
            $.each(arr, function (key, val) {
                if (key === 0) {
                    strResult += val;
                } else {
                    strResult += "," + val;
                }
            });
            break;
        case 2:
            strResult = "(";
            $.each(arr, function (key, val) {
                if (key === 0) {
                    strResult += "'" + val + "'";
                } else {
                    strResult += ",'" + val + "'";
                }
            });
            strResult += ")";
            break;
    }
    return strResult;
}

function _formatDate(date) {
    var d = new Date(date.split("-").reverse().join("-")),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();
    if (month.length < 2) {
        month = '0' + month;
    }
    if (day.length < 2) {
        day = '0' + day;
    }
    return [year, month, day].join('-');
}

function _convertVerifyLogs(str) {
    var arr = [
        {key: 't', name: 'table'},
        {key: 'c', name: 'column'}
    ];
    for (var i = 0; i < arr.length; i++) {
        var tmp = arr[i];
        if (str === tmp.key) {
            return tmp.name;
        }
    }
    return str;
}


// ############################################################################

function _getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function guid() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
    }
    return s4() + s4() + s4() + s4() + s4() + s4() + s4() + s4() + s4();
}

function getSystemID() {
    var today = new Date();
    var d = today.getDate();
    var m = today.getMonth();
    var yyyy = today.getFullYear();
    var NextYear = new Date(yyyy + 1, m, d);
    var month = new Array();
    month[0] = "Jan";
    month[1] = "Feb";
    month[2] = "Mar";
    month[3] = "Apr";
    month[4] = "May";
    month[5] = "Jun";
    month[6] = "Jul";
    month[7] = "Aug";
    month[8] = "Sep";
    month[9] = "Oct";
    month[10] = "Nov";
    month[11] = "Dec";
    tmpSystemID = _getCookie("system_id");
    if (tmpSystemID === "") {
        document.cookie = "system_id=" + guid() + "; expires=Thu, " + NextYear.getDate() + " " + month[NextYear.getMonth()] + " " + NextYear.getFullYear() + " 12:00:00 UTC";
    }
}