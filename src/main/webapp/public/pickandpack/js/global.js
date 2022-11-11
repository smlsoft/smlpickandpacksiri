var __SUB_LINK = null;

var __groupCode = null;
var __whCode = null;
var __shelfCode = null;
var __allWhCode = null;
var __allShelfCode = null;

var __pmGRead = null;
var __pmGAdd = null;
var __pmGUpdate = null;
var __pmGDelete = null;
var __pmGPrint = null;
var __pmURead = null;
var __pmUAdd = null;
var __pmUUpdate = null;
var __pmUDelete = null;
var __pmUPrint = null;
var __tmpUserCode = null;
var __tmpPageCode = null;

$(function () {
    $(document).ready(function () {
        __SUB_LINK = $("#h_sub_link").val();
        __tmpUserCode = $("#h_user_code").val().toLowerCase();
        __tmpPageCode = $("#h_page_code").val();
        if (__tmpUserCode !== "superadmin") {
            __getPermissions();
        } else {
            __pmURead = true;
            __pmUAdd = true;
            __pmUUpdate = true;
            __pmUDelete = true;
            __pmUPrint = true;

            __pmGRead = true;
            __pmGAdd = true;
            __pmGUpdate = true;
            __pmGDelete = true;
            __pmGPrint = true;
        }
        __refreshGlobal();
        __updateActiveTimes();
        setTimeout(__checkActiveUsers, 1500);
        setTimeout(__refreshCookie, 2500);
    });
});

function __refreshGlobal() {
    if (__groupCode === null || __whCode === null || __shelfCode === null || __allWhCode === null || __allShelfCode === null) {
        __getConfigs();
    }
    setTimeout(function () {
        var data = {ข้อมูลทั่วไป:
                    [
                        {ข้อมูลคลังสินค้า: [{กลุ่มคลังสินค้า: __groupCode, รหัสคลังสินค้า: __whCode, สถานที่เก็บสินค้า: __shelfCode}]},
                        {ข้อมูลคลังสินค้าทั้งหมด: [{รหัสคลังสินค้า: __allWhCode, สถานที่เก็บสินค้า: __allShelfCode}]},
                        {ข้อมูลสิทธิ์การเข้าถึง_แบบเดี่ยว: [{การอ่าน: __pmURead, การเพิ่มข้อมูล: __pmUAdd, การแก้ไขข้อมูล: __pmUUpdate, การลบข้อมูล: __pmUDelete, การพิมพ์ซ้ำ: __pmUPrint}]},
                        {ข้อมูลสิทธิ์การเข้าถึง_แบบกลุ่ม: [{การอ่าน: __pmGRead, การเพิ่มข้อมูล: __pmGAdd, การแก้ไขข้อมูล: __pmGUpdate, การลบข้อมูล: __pmGDelete, การพิมพ์ซ้ำ: __pmGPrint}]}
                    ]
        };
        console.clear();
        console.log(data);
    }, 5000);
    __checkGlobalCancelDocument();

    setTimeout(__autoApprove, 10000);
    setTimeout(__refreshGlobal, 120000);
}

function __getConfigs() {
    var sendData = {};
    $.ajax({
        url: __SUB_LINK + "global-services-1" + "?action_name=get_configs",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __groupCode = response.group_code;
                __whCode = response.wh_code;
                __shelfCode = response.shelf_code;
                __allWhCode = [response.arr_wh_code].join(",");
                __allShelfCode = [response.arr_shelf_code].join(",");
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function __checkActiveUsers() {
    __activeUsers();
    setTimeout(__checkActiveUsers, 180000);
}

function __activeUsers() {
    var sendData = {};
    $.ajax({
        url: __SUB_LINK + "global-services-1" + "?action_name=active_users",
        type: 'POST',
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __alertToastMessage("ตรวจสอบสิทธิ์เรียบร้อย.", "success");
            } else {

            }
        }
    });
}

function __updateActiveTimes() {
    var myDate = new Date();
    myDate.setTime(myDate.getTime() + 15 * 60 * 1000);
    document.cookie = "activetime=yes; expires=" + myDate.toUTCString();
}

function __refreshCookie() {
    var myCookie = __getCookie("activetime");
    if (myCookie === "") {
        window.location.href = __SUB_LINK + "logout.jsp";
    }
    setTimeout(__refreshCookie, 10000);
}

// ############################################################################

function __getPermissions() {
    var sendData = {
        page_code: __tmpPageCode
    };
    $.ajax({
        url: __SUB_LINK + "global-services-1" + "?action_name=get_permission",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                if (response.data_1.length > 0) {
                    $.each(response.data_1, function (key, obj) {
                        __pmURead = obj.is_read;
                        __pmUAdd = obj.is_create;
                        __pmUUpdate = obj.is_update;
                        __pmUDelete = obj.is_delete;
                        __pmUPrint = obj.is_re_print;
                    });
                } else {
                    __pmURead = false;
                    __pmUAdd = false;
                    __pmUUpdate = false;
                    __pmUDelete = false;
                    __pmUPrint = false;
                }

                if (response.data_2.length > 0) {
                    $.each(response.data_2, function (key, obj) {
                        __pmGRead = obj.is_read;
                        __pmGAdd = obj.is_create;
                        __pmGUpdate = obj.is_update;
                        __pmGDelete = obj.is_delete;
                        __pmGPrint = obj.is_re_print;
                    });
                } else {
                    __pmGRead = false;
                    __pmGAdd = false;
                    __pmGUpdate = false;
                    __pmGDelete = false;
                    __pmGPrint = false;
                }


                __alertToastMessage("ดึงข้อมูลสิทธิ์เรียบร้อย", "success");
            } else {
                __showErrorBox(response);
            }
        }, complete: function () {
            __checkPermRead();
        }
    });
}

function __checkPermRead() {
    if (__tmpPageCode !== "P0000000000") {
        if (!__pmURead) {
            if (!__pmGRead) {
                window.location.href = __SUB_LINK + "index.jsp";
            }
        }
    }
}

function __checkPermAdd() {
    if (!__pmUAdd) {
        if (__pmGAdd) {
            return true;
        } else {
            swal(__getDialogOption2("ข้อความระบบ", "คุณไม่มีสิทธิ์ในการเพิ่มข้อมูล")).then(function ()
            {
                return false;
            });
        }
    } else {
        return true;
    }
}

function __checkPermUpdate() {
    if (!__pmUUpdate) {
        if (__pmGUpdate) {
            return true;
        } else {
            swal(__getDialogOption2("ข้อความระบบ", "คุณไม่มีสิทธิ์ในการแก้ไขข้อมูล")).then(function ()
            {
                return false;
            });
        }
    } else {
        return true;
    }
}

function __checkPermDelete() {
    if (!__pmUDelete) {
        if (__pmGDelete) {
            return true;
        } else {
            swal(__getDialogOption2("ข้อความระบบ", "คุณไม่มีสิทธิ์ในการลบข้อมูล")).then(function ()
            {
                return false;
            });
        }
    } else {
        return true;
    }
}

function __checkPermPrint() {
    if (!__pmUPrint) {
        if (__pmGPrint) {
            return true;
        } else {
            return false;
        }
    } else {
        return true;
    }
}

function __checkGlobalCancelDocument() {
    var sendData = {};

    $.ajax({
        url: __SUB_LINK + "global-services-1" + "?action_name=check_cancel_document_2",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __alertToastMessage("ตรวจสอบการยกเลิกเอกสาร เรียบร้อย.", "success");
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function __autoApprove() {
    var sendData = {
        wh_code: __addCommaData(__allWhCode.split(','), 2),
        shelf_code: __addCommaData(__allShelfCode.split(','), 2)
    };

    $.ajax({
        url: __SUB_LINK + "global-services-1" + "?action_name=auto_approve",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                var data = {};
                if (response.auto_approve_status) {
                    data = {สถานะอนุมัติเอกสารอัตโมมัติ:
                                [
                                    {เอกสารที่อนุมัติ: [{จำนวน: response.doc_no_approved.length, หมายเลขเอกสาร: response.doc_no_approved}]},
                                    {สถานะการอนุมัติ: response.success ? "สำเร็จ" : ""},
                                    {สิทธิ์การอนุมัติ: response.auto_approve_status ? "มีสิทธิ์" : "ไม่มีสิทธิ์"}
                                ]
                    };
                } else {
                    data = {สถานะอนุมัติเอกสารอัตโมมัติ:
                                [
                                    {สถานะการอนุมัติ: response.success ? "สำเร็จ" : ""},
                                    {สิทธิ์การอนุมัติ: response.auto_approve_status ? "มีสิทธิ์" : "ไม่มีสิทธิ์"}
                                ]
                    };
                }
                console.log(data);
            } else {
                __showErrorBox(response);
            }
        }
    });
}

// ############################################################################

function __alertToastMessage(strMessage, strType) { // type => 'success', 'info', 'warning', 'error'
    toastr.options = {
        closeButton: false,
        debug: false,
        newestOnTop: true,
        progressBar: false,
        /*
         positionClass
         toast-top-right, toast-top-left, toast-bottom-right,
         toast-bottom-left, toast-top-full-width, toast-bottom-full-width, toast-top-center, toast-bottom-center
         */
        positionClass: "toast-top-right",
        preventDuplicates: false,
        onclick: null,
        showDuration: "300",
        hideDuration: "1000",
        timeOut: "5000",
        extendedTimeOut: "1000",
        showEasing: "swing",
        hideEasing: "linear",
        showMethod: "fadeIn",
        hideMethod: "fadeOut"
    };
    toastr[strType](strMessage);
}

function __getDialogOption(strTitle, strMessage) {
    return {
        allowOutsideClick: false,
        allowEscapeKey: false,
        title: strTitle,
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

function __getDialogOption2(strTitle, strMessage) {
    return {
        allowOutsideClick: false,
        allowEscapeKey: false,
        title: strTitle,
        text: strMessage,
        type: 'error',
        showCancelButton: false,
        confirmButtonColor: '#00a65a',
        cancelButtonColor: '#d33',
        confirmButtonText: 'รับทราบ',
        cancelButtonText: 'ยกเลิก',
        animation: false,
        customClass: 'animated bounceIn'
    };
}

function __getDatePickerOption() {
    return {
        autoclose: true,
        todayHighlight: true,
        language: 'th',
        format: "dd-mm-yyyy"
    };
}

function __alertDialogMessage(strMessage, strType) { // type => 'success', 'info', 'warning', 'error'
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

function __loadingText(width) {
    return "<tr><td colspan='" + width + "'><p class='text-center'><span class='fa fa-spinner fa-spin fa-2x fa-fw'></span> กำลังโหลดข้อมูล...</p></td></tr>";
}

function __getCookie(cname) {
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

function __formatDate(date) {
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

function __createPagination(total_records) {
    var objPagiList = objPagination.clone();
    $("#content-pagination").empty();
    var page = parseInt(CURRENT_PAGE) === 0 ? 1 : CURRENT_PAGE;
    var total_pages = total_records > 0 ? parseInt(total_records / TOTAL_PAGE) : 0;
    if (parseInt(total_pages) === 0) {
        total_pages = 1;
    } else {
        var total_pages2 = total_records > 0 ? parseFloat(total_records / TOTAL_PAGE) : 0.00;
        total_pages2 = total_pages2.toFixed(2); // ทำให้เป็นทศนิยม 2 ตำแหน่ง
        var split_total_page2 = total_pages2.toString().split('.');
        if (split_total_page2[1] > 0) {
            total_pages += 1;
        }
    }

    PAGE_SIZE = total_pages;
    // CREATE HTML
    if (parseInt(page) === 1) {
        objPagiList.find("#btn-pagi-first").attr('page-number', -1).addClass("disabled");
        objPagiList.find("#btn-pagi-previous").attr("page-number", -1).addClass("disabled");
    } else {
        objPagiList.find("#btn-pagi-first").attr('page-id', 1).addClass("btn-pagination");
        objPagiList.find("#btn-pagi-previous").attr("page-id", parseInt(page) - 1).addClass("btn-pagination");
    }
    if (parseInt(page) === parseInt(total_pages)) {
        objPagiList.find("#btn-pagi-next").attr("page-id", -1).addClass("btn-pagination disabled");
        objPagiList.find("#btn-pagi-last").attr('page-id', -1).addClass("btn-pagination disabled");
    } else {
        objPagiList.find("#btn-pagi-next").attr("page-id", parseInt(page) + 1).addClass("btn-pagination");
        objPagiList.find("#btn-pagi-last").attr('page-id', total_pages).addClass("btn-pagination");
    }

    objPagiList.find("#txt-pagination").val("หน้า " + page + " ถึง " + total_pages);
    // return HTML
    return objPagiList;
}

function __showErrorBox(response) {
    __alertDialogMessage("การดึงข้อมูลไม่สำเร็จ", "error");
    var tmpErr = objErr.clone();
    tmpErr.find("#txt-err-title").text(response.err_title);
    tmpErr.find("#txt-err-msg").text(response.err_msg);
    $("#content-error-box").empty().append(tmpErr).show('fast');
}

function __clearFormValues(frm) {
    var $parent = $(".form-group");
    $parent.removeClass('has-success');
    $parent.removeClass('has-error');
    $parent.find("[data-fv-icon-for]").hide();
    $parent.find('.help-block').hide();
    $(frm)[0].reset();
}

function __clearSelect2() {
    $(".select2").val(null).trigger("change");
}

function __addCommaData(arr, type) {
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
