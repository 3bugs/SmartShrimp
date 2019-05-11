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

$sql = "SELECT * FROM pond ORDER BY `number`";
if ($result = $db->query($sql)) {
    $pondList = array();
    while ($row = $result->fetch_assoc()) {
        $pond = array();
        $pond['id'] = (int)$row['id'];
        $pond['number'] = (int)$row['number'];
        $pond['area'] = (int)$row['area'];

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
                    ข้อมูลบ่อเลี้ยง
                </h1>
            </section>

            <!-- Main content -->
            <section class="content">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="box box-info">
                            <div class="box-header with-border">
                                <h3 class="box-title">ข้อมูลบ่อเลี้ยง</h3>

                                <button type="button" class="btn btn-success pull-right"
                                        data-toggle="modal" data-target="#loginModal">
                                    <span class="fa fa-plus"></span>&nbsp;
                                    เพิ่มข้อมูลบ่อ
                                </button>

                                <!-- Modal -->
                                <div class="modal fade" id="loginModal" role="dialog">
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
                                                        <!--รหัสผ่าน-->
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
                            </div>
                            <div class="box-body">
                                <table id="example1" class="table table-bordered table-striped">
                                    <thead>
                                    <tr>
                                        <th style="width: 50%; text-align: center">บ่อที่</th>
                                        <th style="width: 50%; text-align: center">พื้นที่บ่อ (ไร่)</th>
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
                                            ?>
                                            <tr style="">
                                                <td style="vertical-align: middle; text-align: center"><?php echo $pondNumber; ?></td>
                                                <td style="vertical-align: middle; text-align: center"><?php echo $pondArea; ?></td>
                                                <td style="text-align: center" nowrap>
                                                    <button type="button" class="btn btn-warning"
                                                            style="margin-left: 6px; margin-right: 3px;"
                                                            onclick="onClickEdit(this, <?php echo $pondId; ?>)">
                                                        <span class="fa fa-edit"></span>&nbsp;
                                                        แก้ไข
                                                    </button>
                                                    <button type="button" class="btn btn-danger"
                                                            style="margin-left: 3px; margin-right: 6px;"
                                                            onclick="onClickDelete(this, <?php echo $pondId; ?>)">
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
            $('#formAddPond').submit(event => {
                event.preventDefault();
                doAddPond();
            });
        });

        function doAddPond() {
            $.post(
                'api/api.php/add_pond',
                {
                    pondNumber: $('#inputPondNumber').val(),
                    pondArea: $('#inputPondArea').val(),
                }
            ).done(function (data) {
                if (data.error_code === 0) {
                    location.reload(true);
                } else {
                    $('#addPondResult').text(data.error_message);
                }
            }).fail(function () {
                $('#addPondResult').text('เกิดข้อผิดพลาดในการเชื่อมต่อ Server');
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