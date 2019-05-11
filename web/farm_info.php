<?php
require_once 'api/global.php';

error_reporting(E_ERROR | E_PARSE);
header('Content-type: text/html; charset=utf-8');
header('Expires: Sun, 01 Jan 2014 00:00:00 GMT');
header('Cache-Control: no-store, no-cache, must-revalidate');
header('Cache-Control: post-check=0, pre-check=0', FALSE);
header('Pragma: no-cache');

session_start();
if (!isset($_SESSION[KEY_SESSION_USER_ID])) {
    header('Location: login.php');
    exit();
}

require_once 'api/db_config.php';
$db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

if ($db->connect_errno) {
    echo 'การเชื่อมต่อฐานข้อมูลล้มเหลว!';
    exit();
}
$db->set_charset("utf8");

$sql = "SELECT * FROM `farm`";
$farmInfo = array();
if ($result = $db->query($sql)) {
    $row = $result->fetch_assoc();
    $farmInfo['name'] = $row['name'];
    $farmInfo['address'] = $row['address'];
    $farmInfo['sub_district'] = $row['sub_district'];
    $farmInfo['district'] = $row['district'];
    $farmInfo['province'] = $row['province'];
    $farmInfo['postal_code'] = $row['postal_code'];
    $farmInfo['farm_reg_id'] = $row['farm_reg_id'];
    $result->close();
} else {
    echo 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
    exit();
}

$sql = "SELECT * FROM `pond` ORDER BY `number`";
if ($result = $db->query($sql)) {
    $pondList = array();
    while ($row = $result->fetch_assoc()) {
        $pond = array();
        $pond['id'] = (int)$row['id'];
        $pond['number'] = (int)$row['number'];
        $pond['area'] = (int)$row['area'];
        $pond['initial_shrimp_count'] = (int)$row['initial_shrimp_count'];
        array_push($pondList, $pond);
    }
    $result->close();
} else {
    echo 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
    exit();
}

?>
    <!DOCTYPE html>
    <html lang="th">
    <head>
        <?php require_once('include/head.inc'); ?>
        <style>

        </style>
    </head>
    <body class="hold-transition skin-blue sidebar-mini">

    <!-- Add Pond Modal -->
    <div class="modal fade" id="addPondModal" role="dialog">
        <div class="modal-dialog modal-md">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;
                    </button>
                    <h4 class="modal-title">เพิ่มข้อมูลบ่อ</h4>
                </div>
                <div class="modal-body">
                    <form id="formAddPond" role="form"
                          style="margin-top: 0; margin-bottom: 0">
                        <div class="box-body">
                            <!--บ่อที่-->
                            <div class="form-group">
                                <label for="inputPondNumber">บ่อที่:</label>
                                <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-hashtag"></i>
                                    </span>
                                    <input type="number" class="form-control"
                                           id="inputPondNumber"
                                           placeholder="กรอกหมายเลขบ่อ" required
                                           oninvalid="this.setCustomValidity('กรอกหมายเลขบ่อ')"
                                           oninput="this.setCustomValidity('')">
                                </div>
                            </div>
                            <!--พื้นที่บ่อ-->
                            <div class="form-group">
                                <label for="inputPondArea">พื้นที่บ่อ (ไร่):</label>
                                <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-windows"></i>
                                    </span>
                                    <input type="number" class="form-control"
                                           id="inputPondArea"
                                           placeholder="กรอกพื้นที่บ่อ (ไร่)" required
                                           oninvalid="this.setCustomValidity('กรอกพื้นที่บ่อ (ไร่)')"
                                           oninput="this.setCustomValidity('')">
                                </div>
                            </div>
                            <!--จำนวนกุ้งที่ปล่อย-->
                            <div class="form-group">
                                <label for="inputInitialShrimpCount">จำนวนกุ้งที่ปล่อย:</label>
                                <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-bug"></i>
                                    </span>
                                    <input type="number" class="form-control"
                                           id="inputInitialShrimpCount"
                                           placeholder="จำนวนกุ้งที่ปล่อย" required
                                           oninvalid="this.setCustomValidity('กรอกจำนวนกุ้งที่ปล่อย')"
                                           oninput="this.setCustomValidity('')">
                                </div>
                            </div>
                            <div id="addPondResult"
                                 style="text-align: center; color: red; margin-top: 25px; margin-bottom: 20px;">
                            </div>
                        </div>
                        <!-- /.box-body -->
                        <div class="box-footer">
                            <button id="buttonSave" type="submit"
                                    class="btn btn-info pull-right">
                                <span class="fa fa-save"></span>&nbsp;
                                บันทึก
                            </button>
                        </div>
                        <!-- /.box-footer -->
                    </form>
                </div>
                <!--<div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>-->
            </div>
        </div>
    </div>

    <!-- Edit Pond Modal -->
    <div class="modal fade" id="editPondModal" role="dialog">
        <div class="modal-dialog modal-md">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;
                    </button>
                    <h4 class="modal-title">แก้ไขข้อมูลบ่อ</h4>
                </div>
                <div class="modal-body">
                    <form id="formEditPond" role="form"
                          style="margin-top: 0; margin-bottom: 0">
                        <div class="box-body">
                            <input type="hidden" id="inputPondId">
                            <!--บ่อที่-->
                            <div class="form-group">
                                <label for="inputPondNumber">บ่อที่:</label>
                                <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-hashtag"></i>
                                    </span>
                                    <input type="number" class="form-control"
                                           id="inputPondNumber"
                                           placeholder="กรอกหมายเลขบ่อ" required
                                           oninvalid="this.setCustomValidity('กรอกหมายเลขบ่อ')"
                                           oninput="this.setCustomValidity('')">
                                </div>
                            </div>
                            <!--พื้นที่บ่อ-->
                            <div class="form-group">
                                <label for="inputPondArea">พื้นที่บ่อ (ไร่):</label>
                                <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-windows"></i>
                                    </span>
                                    <input type="number" class="form-control"
                                           id="inputPondArea"
                                           placeholder="กรอกพื้นที่บ่อ (ไร่)" required
                                           oninvalid="this.setCustomValidity('กรอกพื้นที่บ่อ (ไร่)')"
                                           oninput="this.setCustomValidity('')">
                                </div>
                            </div>
                            <!--จำนวนกุ้งที่ปล่อย-->
                            <div class="form-group">
                                <label for="inputInitialShrimpCount">จำนวนกุ้งที่ปล่อย:</label>
                                <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-bug"></i>
                                    </span>
                                    <input type="number" class="form-control"
                                           id="inputInitialShrimpCount"
                                           placeholder="จำนวนกุ้งที่ปล่อย" required
                                           oninvalid="this.setCustomValidity('กรอกจำนวนกุ้งที่ปล่อย')"
                                           oninput="this.setCustomValidity('')">
                                </div>
                            </div>
                            <div id="editPondResult"
                                 style="text-align: center; color: red; margin-top: 25px; margin-bottom: 20px;">
                            </div>
                        </div>
                        <!-- /.box-body -->
                        <div class="box-footer">
                            <button id="buttonEditSave" type="submit"
                                    class="btn btn-info pull-right">
                                <span class="fa fa-save"></span>&nbsp;
                                บันทึก
                            </button>
                        </div>
                        <!-- /.box-footer -->
                    </form>
                </div>
                <!--<div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>-->
            </div>
        </div>
    </div>

    <div class="wrapper">
        <?php require_once('include/header.inc'); ?>
        <?php require_once('include/sidebar.inc'); ?>

        <!-- Content Wrapper. Contains page content -->
        <div class="content-wrapper">
            <!-- Content Header (Page header) -->
            <section class="content-header">
                <h1>
                    ข้อมูลฟาร์ม/บ่อเลี้ยง
                </h1>
            </section>

            <!-- Main content: ข้อมูลฟาร์ม -->
            <section class="content" style="padding-bottom: 0">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="box box-info">
                            <div class="box-header with-border">
                                <h3 class="box-title">ข้อมูลฟาร์ม</h3>
                            </div>
                            <!-- /.box-header -->
                            <!-- form start -->
                            <form id="formFarmInfo" class="form-horizontal" method="post">
                                <div class="box-body">
                                    <div class="form-group">
                                        <label for="inputFarmName" class="col-sm-2 control-label">ชื่อฟาร์ม</label>

                                        <div class="col-sm-10">
                                            <input type="text" class="form-control" id="inputFarmName"
                                                   placeholder="ชื่อฟาร์ม" required
                                                   value="<?php echo valueOrEmptyString($farmInfo['name']); ?>"
                                                   oninvalid="this.setCustomValidity('กรอกชื่อฟาร์ม')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="inputAddress" class="col-sm-2 control-label">ที่อยู่</label>

                                        <div class="col-sm-10">
                                            <input type="text" class="form-control" id="inputAddress"
                                                   placeholder="ที่อยู่" required
                                                   value="<?php echo valueOrEmptyString($farmInfo['address']); ?>"
                                                   oninvalid="this.setCustomValidity('กรอกที่อยู่')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="inputSubDistrict" class="col-sm-2 control-label">แขวง/ตำบล</label>

                                        <div class="col-sm-10">
                                            <input type="text" class="form-control" id="inputSubDistrict"
                                                   placeholder="แขวง/ตำบล" required
                                                   value="<?php echo valueOrEmptyString($farmInfo['sub_district']); ?>"
                                                   oninvalid="this.setCustomValidity('กรอกแขวง/ตำบล')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="inputDistrict" class="col-sm-2 control-label">เขต/อำเภอ</label>

                                        <div class="col-sm-10">
                                            <input type="text" class="form-control" id="inputDistrict"
                                                   placeholder="เขต/อำเภอ" required
                                                   value="<?php echo valueOrEmptyString($farmInfo['district']); ?>"
                                                   oninvalid="this.setCustomValidity('กรอกเขต/อำเภอ')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="inputProvince" class="col-sm-2 control-label">จังหวัด</label>

                                        <div class="col-sm-10">
                                            <input type="text" class="form-control" id="inputProvince"
                                                   placeholder="จังหวัด" required
                                                   value="<?php echo valueOrEmptyString($farmInfo['province']); ?>"
                                                   oninvalid="this.setCustomValidity('กรอกจังหวัด')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="inputPostalCode" class="col-sm-2 control-label">รหัสไปรษณีย์</label>

                                        <div class="col-sm-10">
                                            <input type="text" class="form-control" id="inputPostalCode"
                                                   placeholder="รหัสไปรษณีย์" required
                                                   value="<?php echo valueOrEmptyString($farmInfo['postal_code']); ?>"
                                                   oninvalid="this.setCustomValidity('กรอกรหัสไปรษณีย์')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="inputFarmRegId"
                                               class="col-sm-2 control-label">เลขที่ทะเบียนฟาร์ม</label>

                                        <div class="col-sm-10">
                                            <input type="text" class="form-control" id="inputFarmRegId"
                                                   placeholder="เลขที่ทะเบียนฟาร์ม" required
                                                   value="<?php echo valueOrEmptyString($farmInfo['farm_reg_id']); ?>"
                                                   oninvalid="this.setCustomValidity('กรอกเลขที่ทะเบียนฟาร์ม')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>
                                <!-- /.box-body -->
                                <div class="box-footer">
                                    <!--<button type="submit" class="btn btn-default">Cancel</button>-->
                                    <button type="submit" class="btn btn-info pull-right">
                                        <i class="fa fa-save"></i>&nbsp;&nbsp;บันทึก
                                    </button>
                                </div>
                                <!-- /.box-footer -->
                            </form>
                        </div>
                    </div>
                    <!-- /.col -->
                </div>
                <!-- /.row -->
            </section>
            <!-- /.content -->

            <!-- Main content: ข้อมูลบ่อเลี้ยง -->
            <section class="content" style="padding-top: 0">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="box box-info">
                            <div class="box-header with-border">
                                <h3 class="box-title">ข้อมูลบ่อเลี้ยง</h3>
                                <button type="button" class="btn btn-success pull-right"
                                        data-toggle="modal" data-target="#addPondModal">
                                    <span class="fa fa-plus"></span>&nbsp;
                                    เพิ่มข้อมูลบ่อ
                                </button>
                            </div>
                            <div class="box-body">
                                <table id="example1" class="table table-bordered table-striped">
                                    <thead>
                                    <tr>
                                        <th style="width: 33%; text-align: center">บ่อที่</th>
                                        <th style="width: 33%; text-align: center">พื้นที่บ่อ (ไร่)</th>
                                        <th style="width: 33%; text-align: center">จำนวนกุ้งที่ปล่อย</th>
                                        <th style="text-align: center" nowrap>จัดการ</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <?php
                                    if (sizeof($pondList) == 0) {
                                        ?>
                                        <tr valign="middle">
                                            <td colspan="3" align="center">ไม่มีข้อมูลบ่อเลี้ยง</td>
                                        </tr>
                                        <?php
                                    } else {
                                        foreach ($pondList as $pond) {
                                            $pondId = $pond['id'];
                                            $pondNumber = $pond['number'];
                                            $pondArea = $pond['area'];
                                            $initialShrimpCount = $pond['initial_shrimp_count'];
                                            ?>
                                            <tr style="">
                                                <td style="vertical-align: middle; text-align: center"><?php echo $pondNumber; ?></td>
                                                <td style="vertical-align: middle; text-align: center"><?php echo number_format($pondArea); ?></td>
                                                <td style="vertical-align: middle; text-align: center"><?php echo number_format($initialShrimpCount); ?></td>
                                                <td style="text-align: center" nowrap>
                                                    <button type="button" class="btn btn-warning"
                                                            style="margin-left: 6px; margin-right: 3px;"
                                                            onclick="onClickEdit(this, <?php echo $pondId; ?>, <?php echo $pondNumber; ?>, <?php echo $pondArea; ?>, <?php echo $initialShrimpCount; ?>)">
                                                        <span class="fa fa-edit"></span>&nbsp;
                                                        แก้ไข
                                                    </button>
                                                    <button type="button" class="btn btn-danger"
                                                            style="margin-left: 3px; margin-right: 6px;"
                                                            onclick="onClickDelete(this, <?php echo $pondId; ?>, <?php echo $pondNumber; ?>, <?php echo $pondArea; ?>)">
                                                        <span class="fa fa-remove"></span>&nbsp;
                                                        ลบ
                                                    </button>
                                                </td>
                                            </tr>
                                            <?php
                                        }
                                    }
                                    ?>
                                    </tbody>
                                </table>
                            </div>
                            <!-- /.box-body -->
                        </div>
                        <!-- /.box -->
                    </div>
                    <!-- /.col -->
                </div>
                <!-- /.row -->
            </section>
            <!-- /.content -->
        </div>
        <!-- /.content-wrapper -->

        <?php require_once('include/footer.inc'); ?>
    </div>
    <!-- ./wrapper -->

    <script>
        $(document).ready(function () {
            $('#formFarmInfo').submit(event => {
                event.preventDefault();
                doSaveFarmInfo();
            });
            $('#formAddPond').submit(event => {
                event.preventDefault();
                doAddPond();
            });
            $('#formEditPond').submit(event => {
                event.preventDefault();
                doUpdatePond();
            });
        });

        function onClickEdit(element, pondId, pondNumber, pondArea, initialShrimpCount) {
            $('#formEditPond #inputPondId').val(pondId);
            $('#formEditPond #inputPondNumber').val(pondNumber);
            $('#formEditPond #inputPondArea').val(pondArea);
            $('#formEditPond #inputInitialShrimpCount').val(initialShrimpCount);
            $('#formEditPond #editPondResult').text('');
            $('#editPondModal').modal('show');
        }

        function onClickDelete(element, pondId, pondNumber, pondArea) {
            BootstrapDialog.show({
                title: 'Confirm Delete Pond',
                message: 'ยืนยันลบข้อมูลบ่อที่ ' + pondNumber + ' (พื้นที่ ' + pondArea + ' ไร่)?',
                buttons: [{
                    label: 'ลบ',
                    action: function (self) {
                        doDeletePond(pondId);
                        self.close();
                    },
                    cssClass: 'btn-primary'
                },{
                    label: 'ยกเลิก',
                    action: function (self) {
                        self.close();
                    }
                }]
            });
        }

        function doDeletePond(pondId) {
            $.post(
                'api/api.php/delete_pond',
                {
                    pondId: pondId,
                }
            ).done(function (data) {
                if (data.error_code === 0) {
                    location.reload(true);
                } else {
                    BootstrapDialog.show({
                        title: 'Delete Pond',
                        message: data.error_message,
                        buttons: [{
                            label: 'ปิด',
                            action: function (self) {
                                self.close();
                            }
                        }]
                    });
                }
            }).fail(function () {
                BootstrapDialog.show({
                    title: 'Delete Pond',
                    message: 'เกิดข้อผิดพลาดในการเชื่อมต่อ Server',
                    buttons: [{
                        label: 'ปิด',
                        action: function (self) {
                            self.close();
                        }
                    }]
                });
            });
        }

        function doSaveFarmInfo() {
            $.post(
                'api/api.php/save_farm_info',
                {
                    farmName: $('#inputFarmName').val(),
                    address: $('#inputAddress').val(),
                    subDistrict: $('#inputSubDistrict').val(),
                    district: $('#inputDistrict').val(),
                    province: $('#inputProvince').val(),
                    postalCode: $('#inputPostalCode').val(),
                    farmRegId: $('#inputFarmRegId').val(),
                }
            ).done(function (data) {
                if (data.error_code === 0) {
                    BootstrapDialog.show({
                        title: 'Farm Information',
                        message: data.error_message,
                        buttons: [{
                            label: 'ปิด',
                            action: function (self) {
                                self.close();
                                location.reload(true);
                            }
                        }]
                    });
                } else {
                    BootstrapDialog.show({
                        title: 'Farm Information',
                        message: data.error_message,
                        buttons: [{
                            label: 'ปิด',
                            action: function (self) {
                                self.close();
                            }
                        }]
                    });
                }
            }).fail(function () {
                BootstrapDialog.show({
                    title: 'Farm Information',
                    message: 'เกิดข้อผิดพลาดในการเชื่อมต่อ Server',
                    buttons: [{
                        label: 'ปิด',
                        action: function (self) {
                            self.close();
                        }
                    }]
                });
            });
        }

        function doAddPond() {
            $.post(
                'api/api.php/add_pond',
                {
                    pondNumber: $('#formAddPond #inputPondNumber').val(),
                    pondArea: $('#formAddPond #inputPondArea').val(),
                    initialShrimpCount: $('#formAddPond #inputInitialShrimpCount').val(),
                }
            ).done(function (data) {
                if (data.error_code === 0) {
                    location.reload(true);
                } else {
                    $('#formAddPond #addPondResult').text(data.error_message);
                }
            }).fail(function () {
                $('#formAddPond #addPondResult').text('เกิดข้อผิดพลาดในการเชื่อมต่อ Server');
            });
        }

        function doUpdatePond() {
            $.post(
                'api/api.php/update_pond',
                {
                    pondId: $('#formEditPond #inputPondId').val(),
                    pondNumber: $('#formEditPond #inputPondNumber').val(),
                    pondArea: $('#formEditPond #inputPondArea').val(),
                    initialShrimpCount: $('#formEditPond #inputInitialShrimpCount').val(),
                }
            ).done(function (data) {
                if (data.error_code === 0) {
                    location.reload(true);
                } else {
                    $('#formEditPond #editPondResult').text(data.error_message);
                }
            }).fail(function () {
                $('#formEditPond #editPondResult').text('เกิดข้อผิดพลาดในการเชื่อมต่อ Server');
            });
        }
    </script>

    <?php require_once('include/foot.inc'); ?>
    </body>
    </html>

<?php
$db->close();

function valueOrEmptyString($value)
{
    return isset($value) ? $value : '';
}

?>