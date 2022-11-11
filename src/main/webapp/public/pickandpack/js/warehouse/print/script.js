/* global swal */

var SUB_LINK = "../../../";
var SERVER_URL = "warehouse-print-1";

var tmpActionName = null;
var tmpRefCode = null;
var tmpDocNo = null;
var tmpWhCode = null;
var tmpShelfCode = null;
var tmpTransFlag = null;
var tmpIsPrint = null;
var tmpCarCode = null;
var tmpShipmentCode = null;

var tmpSendDataVerify = {};

var tmpPrintData = null;

$(function () {
    $(document).ready(function () {
        tmpActionName = $("#h_action_name").val();
        tmpRefCode = $("#h_ref_code").val();
        tmpDocNo = $("#h_doc_no").val();
        tmpWhCode = $("#h_wh_code").val();
        tmpShelfCode = $("#h_shelf_code").val();
        tmpTransFlag = $("#h_trans_flag").val();
        tmpIsPrint = $("#h_is_print").val();
        tmpCarCode = $("#h_car_code").val();
        tmpShipmentCode = $("#h_shipment_code").val();
        _printData();
    });
});

function _printData() {
    var sendData = {
        ref_code: tmpRefCode,
        doc_no: tmpDocNo,
        wh_code: tmpWhCode,
        shelf_code: tmpShelfCode,
        car_code: tmpCarCode,
        shipment_code: tmpShipmentCode
    };
    $.ajax({
        url: SUB_LINK + SERVER_URL + "?action_name=print_data",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                if (response.data.length > 0) {
                    tmpPrintData = response.data;
                    _verifyData();
                } else {
                    swal({
                        allowOutsideClick: false,
                        allowEscapeKey: false,
                        title: "ข้อความระบบ",
                        text: "ใบจัดปิดครบแล้ว",
                        type: 'error',
                        showCancelButton: false,
                        confirmButtonColor: '#00a65a',
                        cancelButtonColor: '#d33',
                        confirmButtonText: 'รับทราบ',
                        cancelButtonText: 'ยกเลิก'
                    }).then(function () {
                        window.close();
                    });
                }
            } else {
                console.log(response);
            }
        }
    });
}

function _verifyData() {
    var _ation = "";
    tmpSendDataVerify.ref_code = tmpRefCode;
    tmpSendDataVerify.trans_flag = tmpTransFlag;
    tmpSendDataVerify.is_print = tmpIsPrint;
    switch (tmpActionName) {
        case "worker":
            _ation = "verify_data_1";
            tmpSendDataVerify.doc_no = tmpDocNo;
            break;
        case "administration":
            _ation = "verify_data_2";
            tmpSendDataVerify.car_code = tmpCarCode;
            tmpSendDataVerify.shipment_code = tmpShipmentCode;
            break;
    }
    $.ajax({
        url: SUB_LINK + SERVER_URL + "?action_name=" + _ation,
        type: "POST",
        data: {data: JSON.stringify(tmpSendDataVerify)},
        success: function (response) {
            if (response.success) {
                $("#content-list").html(tmpPrintData);
                setTimeout(function () {
                    window.print();
                    window.close();
                }, 2500);
            } else {
                console.log(response);
            }
        }
    });
}