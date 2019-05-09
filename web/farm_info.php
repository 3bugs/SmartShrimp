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

$sql = "SELECT * FROM farm";
if ($result = $db->query($sql)) {
    $row = $result->fetch_assoc();
    $farmName = $row['name'];
    $address = $row['address'];
    $subDistrict = $row['sub_district'];
    $district = $row['district'];
    $province = $row['province'];
    $postalCode = $row['postal_code'];
    $farmRegId = $row['farm_reg_id'];
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
                    ข้อมูลฟาร์ม
                </h1>
            </section>

            <!-- Main content -->
            <section class="content">
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
                                            <input type="text" class="form-control" id="inputFarmName" placeholder="ชื่อฟาร์ม" required
                                                   value="<?php echo valueOrEmptyString($farmName); ?>"
                                                   oninvalid="this.setCustomValidity('กรอกชื่อฟาร์ม')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="inputAddress" class="col-sm-2 control-label">ที่อยู่</label>

                                        <div class="col-sm-10">
                                            <input type="text" class="form-control" id="inputAddress" placeholder="ที่อยู่" required
                                                   value="<?php echo valueOrEmptyString($address); ?>"
                                                   oninvalid="this.setCustomValidity('กรอกที่อยู่')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="inputSubDistrict" class="col-sm-2 control-label">แขวง/ตำบล</label>

                                        <div class="col-sm-10">
                                            <input type="text" class="form-control" id="inputSubDistrict" placeholder="แขวง/ตำบล" required
                                                   value="<?php echo valueOrEmptyString($subDistrict); ?>"
                                                   oninvalid="this.setCustomValidity('กรอกแขวง/ตำบล')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="inputDistrict" class="col-sm-2 control-label">เขต/อำเภอ</label>

                                        <div class="col-sm-10">
                                            <input type="text" class="form-control" id="inputDistrict" placeholder="เขต/อำเภอ" required
                                                   value="<?php echo valueOrEmptyString($district); ?>"
                                                   oninvalid="this.setCustomValidity('กรอกเขต/อำเภอ')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="inputProvince" class="col-sm-2 control-label">จังหวัด</label>

                                        <div class="col-sm-10">
                                            <input type="text" class="form-control" id="inputProvince" placeholder="จังหวัด" required
                                                   value="<?php echo valueOrEmptyString($province); ?>"
                                                   oninvalid="this.setCustomValidity('กรอกจังหวัด')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="inputPostalCode" class="col-sm-2 control-label">รหัสไปรษณีย์</label>

                                        <div class="col-sm-10">
                                            <input type="text" class="form-control" id="inputPostalCode" placeholder="รหัสไปรษณีย์" required
                                                   value="<?php echo valueOrEmptyString($postalCode); ?>"
                                                   oninvalid="this.setCustomValidity('กรอกรหัสไปรษณีย์')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="inputFarmRegId" class="col-sm-2 control-label">เลขที่ทะเบียนฟาร์ม</label>

                                        <div class="col-sm-10">
                                            <input type="text" class="form-control" id="inputFarmRegId" placeholder="เลขที่ทะเบียนฟาร์ม" required
                                                   value="<?php echo valueOrEmptyString($farmRegId); ?>"
                                                   oninvalid="this.setCustomValidity('กรอกเลขที่ทะเบียนฟาร์ม')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                    <!--<div class="form-group">
                                        <div class="col-sm-offset-2 col-sm-10">
                                            <div class="checkbox">
                                                <label>
                                                    <input type="checkbox"> Remember me
                                                </label>
                                            </div>
                                        </div>
                                    </div>-->
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
        });

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
    </script>

    <?php require_once('include/foot.inc'); ?>
    </body>
    </html>

<?php
$db->close();

function valueOrEmptyString($value) {
    return isset($value) ? $value : '';
}
?>