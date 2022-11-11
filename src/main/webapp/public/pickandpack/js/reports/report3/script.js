var SERVER_URL = "report-list-3";

var tmpBranchCode = null;
var tmpDepartmentCode = null;
var tmpFromDate = null;
var tmpToDate = null;

$(function () {
    $(document).ready(function () {
        objErr = $("#content-error-list").clone();
        $("#txt-search-date-from").inputmask('dd-mm-yyyy', {'placeholder': 'วว-ดด-ปปปป'});
        $("#txt-search-date-to").inputmask('dd-mm-yyyy', {'placeholder': 'วว-ดด-ปปปป'});
        $("#txt-search-date-from").datepicker(__getDatePickerOption());
        $("#txt-search-date-to").datepicker(__getDatePickerOption());

        $("#txt-search-date-from").datepicker('update', new Date());
        $("#txt-search-date-to").datepicker('update', new Date());

        setTimeout(function () {
            $("#content-search-box").fadeIn("fast");
            $("#content-loading-box").fadeOut("fast");
            _getBranchList();
            _getDepartmentList();
        }, 4000);


    });

    $("#content-search-box").on("click", "#btn-search", function () {
        if (tmpBranchCode && tmpDepartmentCode) {
            tmpFromDate = __formatDate($("#txt-search-date-from").val());
            tmpToDate = __formatDate($("#txt-search-date-to").val());
            var sendData = {};
            if (tmpFromDate !== "NaN-NaN-NaN" && tmpToDate !== "NaN-NaN-NaN") {
                sendData.branch_code = tmpBranchCode;
                if (tmpDepartmentCode !== "all") {
                    sendData.department_code = tmpDepartmentCode;
                }
                sendData.from_date = tmpFromDate;
                sendData.to_date = tmpToDate;
                _getMainDetail(sendData);
            } else {
                __alertDialogMessage("ข้อมูลวันที่ไม่ถูกต้อง.", "error");
            }
        } else {
            __alertDialogMessage("กรุณาเลือกข้อมูลให้ครบด้วยครับ.", "error");
        }
    });

    $("#content-search-box").on("click", "#btn-export-excel", function () {
        if (tmpBranchCode && tmpDepartmentCode) {
            tmpFromDate = __formatDate($("#txt-search-date-from").val());
            tmpToDate = __formatDate($("#txt-search-date-to").val());
            if (tmpFromDate !== "NaN-NaN-NaN" && tmpToDate !== "NaN-NaN-NaN") {
                var sendData = {
                    branch_code: tmpBranchCode,
                    department_code: tmpDepartmentCode,
                    from_date: tmpFromDate,
                    to_date: tmpToDate
                };
                _exportTo(sendData);
            } else {
                __alertDialogMessage("ข้อมูลวันที่ไม่ถูกต้อง.", "error");
            }
        } else {
            __alertDialogMessage("กรุณาเลือกข้อมูลให้ครบด้วยครับ.", "error");
        }
    });

    $("#content-search-box").on("change", "#txt-search-date-from", function () {
        __updateActiveTimes();
    });

    $("#content-search-box").on("change", "#txt-search-date-to", function () {
        __updateActiveTimes();
    });

    $("#content-search-box").on("change", "#sel_branch_list", function () {
        __updateActiveTimes();
        tmpBranchCode = $(this).val();
    });

    $("#content-search-box").on("change", "#sel_department_list", function () {
        __updateActiveTimes();
        tmpDepartmentCode = $(this).val();
    });
});

function _getMainDetail(sendData) {

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_main_detail",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        beforeSend: function () {
            $("#content-table-box").fadeIn("fast");
            $("#content-table-list").html(__loadingText(14));
        },
        success: function (response) {
            $("#content-table-list").empty();
            if (response.success) {
                $("#content-table-list").html(response.data);
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

function _getBranchList() {
    var sendData = {};

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_branch_list",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            var __rsHTML = "";
            if (response.success) {
                if (response.data.length > 0) {
                    __rsHTML += "<option value=''>กรุณาเลือกข้อมูล</option>";
                    $.each(response.data, function (key, obj) {
                        __rsHTML += "<option value='" + obj.code + "'>" + obj.code + " ~ " + obj.name_1 + "</option>";
                    });
                } else {
                    __rsHTML += "<option value=''>ไม่พบข้อมูล</option>";
                }
                $("#sel_branch_list").html(__rsHTML);
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _getDepartmentList() {
    var sendData = {};

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_department_list",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            var __rsHTML = "";
            if (response.success) {
                if (response.data.length > 0) {
                    __rsHTML += "<option value=''>กรุณาเลือกข้อมูล</option>";
                    __rsHTML += "<option value='all'>ทุกแผนก</option>";
                    $.each(response.data, function (key, obj) {
                        __rsHTML += "<option value='" + obj.code + "'>" + obj.code + " ~ " + obj.name_1 + "</option>";
                    });
                } else {
                    __rsHTML += "<option value=''>ไม่พบข้อมูล</option>";
                }
                $("#sel_department_list").html(__rsHTML);
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _exportTo(sendData) {
    var targetURL = __SUB_LINK + "export-file-1";
    window.location.href = targetURL + "?" + "action_name=report_3" + "&from_date=" + tmpFromDate + "&to_date=" + tmpToDate + "&data=" + JSON.stringify(sendData);
}