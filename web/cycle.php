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

    <div class="wrapper">
        <?php require_once('include/header.inc'); ?>
        <?php require_once('include/sidebar.inc'); ?>

        <!-- Content Wrapper. Contains page content -->
        <div class="content-wrapper">
            <!-- Content Header (Page header) -->
            <section class="content-header">
                <h1>
                    รอบการเลี้ยง
                </h1>
            </section>

            <!-- Main content: ข้อมูลฟาร์ม -->
            <section class="content" style="padding-bottom: 0">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="box box-info">
                            <div class="box-header with-border">
                                <h3 class="box-title">&nbsp;</h3>
                            </div>
                            <!-- /.box-header -->
                            <!-- form start -->
                            <form id="formCycle" class="form-horizontal" method="post">
                                <div class="box-body">
                                    <div class="form-group" style="margin-bottom: 0">
                                        <label for="selectPond" class="col-sm-3 control-label">รอบการเลี้ยงของบ่อที่</label>
                                        <div class="col-sm-7">
                                            <select id="selectPond" class="form-control">
                                                <option disabled selected>-- เลือกบ่อ --</option>
                                                <?php
                                                foreach ($pondList as $pond) {
                                                    ?>
                                                    <option value="<?php echo $pond['id']; ?>"><?php echo $pond['number']; ?></option>
                                                    <?php
                                                }
                                                ?>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <!-- /.box-body -->
                                <div class="box-footer">
                                    <!--<button type="submit" class="btn btn-default">Cancel</button>-->
                                    <!--<button type="submit" class="btn btn-info pull-right">
                                        <i class="fa fa-save"></i>&nbsp;&nbsp;บันทึก
                                    </button>-->
                                </div>
                                <!-- /.box-footer -->
                            </form>
                        </div>
                        <!-- /.box -->

                        <div class="box box-info">
                            <div class="box-header with-border">
                                <h3 class="box-title">&nbsp;</h3>
                            </div>
                            <!-- /.box-header -->
                            <!-- form start -->
                            <form id="formCycle" class="form-horizontal" method="post">
                                <div class="box-body">
                                    <div id="spanLoading" style="text-align: center">
                                        <img src="images/ic_loading4.gif" height="32px"/>&nbsp;รอสักครู่
                                    </div>
                                </div>
                                <!-- /.box-body -->
                                <div class="box-footer">
                                    <!--<button type="submit" class="btn btn-default">Cancel</button>-->
                                    <!--<button type="submit" class="btn btn-info pull-right">
                                        <i class="fa fa-save"></i>&nbsp;&nbsp;บันทึก
                                    </button>-->
                                </div>
                                <!-- /.box-footer -->
                            </form>
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
            $('#selectPond').on('change', function() {
                //alert(this.value);
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