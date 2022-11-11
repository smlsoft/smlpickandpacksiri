var SERVER_URL = "display-status-list-1";

var CURRENT_PAGE = 0;
var TOTAL_PAGE = 20;
var PAGE_SIZE = 0;
var PAGI_CURRENT_VALUE = null;

var IS_SHOWALL = false;
var IS_REFRESH = false;
var IS_SEARCH = false;
var CAN_SEARCH = false;

$(function () {
    $(document).ready(function () {
        objErr = $("#content-error-list").clone();
        objPagination = $("#content-pagination-list").clone();

        $("#txt-search-date-from").inputmask('dd-mm-yyyy', {'placeholder': 'วว-ดด-ปปปป'});
        $("#txt-search-date-to").inputmask('dd-mm-yyyy', {'placeholder': 'วว-ดด-ปปปป'});
        $("#txt-search-date-from").datepicker(__getDatePickerOption());
        $("#txt-search-date-to").datepicker(__getDatePickerOption());

        $("#txt-search-date-from").datepicker('update', new Date());
        $("#txt-search-date-to").datepicker('update', new Date());

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

function _refreshPAGE() {
    var sendData = {};
    if (IS_SHOWALL) {
        _getMainDetail(sendData);
    } else {
        var ref_code = $("#txt-search").val();
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
        _getMainDetail(sendData);
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

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_main_detail",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        beforeSend: function () {
            $("#content-table-list").html(__loadingText(3));
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
            $("html, body").animate({scrollTop: 0}, "fast");
        }
    });
}