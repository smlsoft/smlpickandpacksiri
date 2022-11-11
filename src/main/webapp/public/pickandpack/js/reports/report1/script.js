var SERVER_URL = "report-list-1";

var tmpBranchCode = null;
var tmpGroupCode = null;
var tmpWhCode = null;
var tmpShelfCode = null;
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
            _getGroupLocation();
        }, 4000);


    });

    $("#content-search-box").on("click", "#btn-search", function () {
        if (tmpBranchCode && tmpGroupCode) {
            tmpFromDate = __formatDate($("#txt-search-date-from").val());
            tmpToDate = __formatDate($("#txt-search-date-to").val());
            var sendData = {};
            if (tmpFromDate !== "NaN-NaN-NaN" && tmpToDate !== "NaN-NaN-NaN") {
                sendData.branch_code = tmpBranchCode;
                sendData.from_date = tmpFromDate;
                sendData.to_date = tmpToDate;
                sendData.wh_code = tmpWhCode;
                sendData.shelf_code = tmpShelfCode;

                var wh_code = tmpWhCode === "" ? "" : tmpWhCode;
                var shelf_code = tmpShelfCode === "" ? "" : tmpShelfCode;
                var sendData = {
                    branch_code: tmpBranchCode,
                    group_code: tmpGroupCode,
                    wh_code: wh_code,
                    shelf_code: shelf_code,
                    from_date: tmpFromDate,
                    to_date: tmpToDate
                };
                _getMainDetail(sendData);
            } else {
                __alertDialogMessage("ข้อมูลวันที่ไม่ถูกต้อง.", "error");
            }
        } else {
            __alertDialogMessage("กรุณาเลือกข้อมูลให้ครบด้วยครับ.", "error");
        }
    });

    $("#content-search-box").on("click", "#btn-export-excel", function () {
        if (tmpBranchCode && tmpGroupCode) {
            tmpFromDate = __formatDate($("#txt-search-date-from").val());
            tmpToDate = __formatDate($("#txt-search-date-to").val());
            if (tmpFromDate !== "NaN-NaN-NaN" && tmpToDate !== "NaN-NaN-NaN") {
                var wh_code = tmpWhCode === "" ? "" : tmpWhCode;
                var shelf_code = tmpShelfCode === "" ? "" : tmpShelfCode;
                var sendData = {
                    branch_code: tmpBranchCode,
                    group_code: tmpGroupCode,
                    wh_code: wh_code,
                    shelf_code: shelf_code,
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

    $("#content-search-box").on("change", "#sel_group_location", function () {
        __updateActiveTimes();
        tmpGroupCode = $(this).val();
        _getZone();
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
});

function _getMainDetail(sendData) {
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_main_detail",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        beforeSend: function () {
            $("#content-table-list").html(__loadingText(10));
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
            $("#content-table-box").fadeIn("fast");
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

function _getGroupLocation() {
    var sendData = {};

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_group_location",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            var __rsHTML = "";
            if (response.success) {
                if (response.data.length > 0) {
                    __rsHTML += "<option value=''>กรุณาเลือกข้อมูล</option>";
                    __rsHTML += "<option value='all'>ทุกคลังและทุกสถานที่เก็บ</option>";
                    $.each(response.data, function (key, obj) {
                        __rsHTML += "<option value='" + obj.group_code + "'>" + obj.group_code + " ~ " + obj.name + "</option>";
                    });
                } else {
                    __rsHTML += "<option value=''>ไม่พบข้อมูล</option>";
                }
                $("#sel_group_location").html(__rsHTML);
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _getZone() {
    if (tmpGroupCode === "all") {
        tmpWhCode = "";
        tmpShelfCode = "";
    } else {
        var sendData = {};
        sendData.group_code = tmpGroupCode;
        $.ajax({
            url: __SUB_LINK + SERVER_URL + "?action_name=get_zone",
            type: "GET",
            data: {data: JSON.stringify(sendData)},
            success: function (response) {
                if (response.success) {
                    $.each(response.data, function (key, obj) {
                        tmpGroupCode = obj.group_code;
                        tmpWhCode = __addCommaData(obj.whcode.split(","), 2);
                        tmpShelfCode = __addCommaData(obj.location.split(","), 2);
                    });
                } else {
                    __showErrorBox(response);
                }
            }
        });
    }
}

function _exportTo(sendData) {
    var targetURL = __SUB_LINK + "export-file-1";
    window.location.href = targetURL + "?" + "action_name=report_1" + "&from_date=" + tmpFromDate + "&to_date=" + tmpToDate + "&data=" + JSON.stringify(sendData);
}