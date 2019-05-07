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

require_once 'include/db_config.inc';
$db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

if ($db->connect_errno) {
    echo 'การเชื่อมต่อฐานข้อมูลล้มเหลว!';
    exit();
}
$db->set_charset("utf8");

$sql = "SELECT * FROM course ORDER BY begin_date";
if ($result = $db->query($sql)) {
    $courseList = array();
    while ($row = $result->fetch_assoc()) {
        $course = array();
        $course['id'] = (int)$row['id'];
        $course['name'] = $row['name'];
        $course['details'] = $row['details'];
        $course['application_fee'] = (int)$row['application_fee'];
        $course['place'] = $row['place'];
        $course['begin_date'] = $row['begin_date'];
        $course['end_date'] = $row['end_date'];

        $sql = "SELECT id FROM course_registration WHERE course_id = " . $course['id'];
        if ($resultCount = $db->query($sql)) {
            $course['registration_count'] = $resultCount->num_rows;
            $resultCount->close();
        } else {
            echo 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
            exit();
        }

        array_push($courseList, $course);
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
                    หลักสูตรบริการวิชาการ
                    <!--<small>ข้อมูลเกี่ยวกับวันเลือกตั้ง</small>-->
                </h1>
            </section>

            <!-- Main content -->
            <section class="content">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="box">
                            <div class="box-header">
                                <h3 class="box-title">&nbsp;</h3>
                                <button type="button" class="btn btn-success pull-right"
                                        onclick="">
                                    <span class="fa fa-plus"></span>&nbsp;
                                    เพิ่มหลักสูตร
                                </button>
                            </div>
                            <div class="box-body">
                                <table id="example1" class="table table-bordered table-striped">
                                    <thead>
                                    <tr>
                                        <th style="text-align: center">ชื่อหลักสูตร</th>
                                        <th style="text-align: center">วันที่อบรม</th>
                                        <th style="text-align: center">จำนวนผู้สมัคร (คน)</th>
                                        <th style="width: 10px; text-align: center">รายละเอียด</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <?php
                                    if (sizeof($courseList) == 0) {
                                        ?>
                                        <tr valign="middle">
                                            <td colspan="4" align="center">ไม่มีข้อมูล</td>
                                        </tr>
                                        <?php
                                    } else {
                                        foreach ($courseList as $course) {
                                            $courseId = $course['id'];
                                            $courseName = $course['name'];
                                            $beginDate = date_create($course['begin_date']);
                                            $endDate = date_create($course['end_date']);
                                            $courseDate = ($course['begin_date'] === $course['end_date'] ? getThaiShortDate($beginDate) : getThaiIntervalShortDate($beginDate, $endDate));

                                            $registrationCount = $course['registration_count'];
                                            ?>
                                            <tr style="">
                                                <td style="vertical-align: middle"><?php echo $courseName; ?></td>
                                                <td style="vertical-align: middle; text-align: center"><?php echo $courseDate; ?></td>
                                                <td style="vertical-align: middle; text-align: center"><?php echo $registrationCount; ?></td>

                                                <td style="width: 10px; text-align: center">
                                                    <button type="button" class="btn btn-info"
                                                            onclick="onClickDetails(this, <?php echo $courseId; ?>)">
                                                        <span class="fa fa-info"></span>&nbsp;
                                                        รายละเอียด
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

        });

        function onClickAdd() {
            window.location.href = 'election_add.php';
        }

        function doDeleteMember(memberId) {
            $.post(
                'api/api.php/delete_member',
                {
                    member_id: memberId
                }
            ).done(function (data) {
                if (data.error_code === 0) {
                    location.reload(true);
                } else {
                    BootstrapDialog.show({
                        title: 'Delete Member',
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
                    title: 'Delete Member',
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

        function onClickDelete(element, memberId, memberDisplayName) {
            BootstrapDialog.show({
                title: 'Confirm Delete Member',
                message: 'ยืนยันลบสมาชิก \'' + memberDisplayName + '\' ?',
                buttons: [
                    {
                        label: 'ยกเลิก',
                        action: function (self) {
                            self.close();
                        }
                    },
                    {
                        label: 'ลบ',
                        action: function (self) {
                            self.close();
                            doDeleteMember(memberId);
                        }
                    }
                ]
            });
        }

        function onClickEdit(element, electionId, dateString) {
            window.location.href = 'election_add.php?edit=true&election_id=' + electionId;
        }

        function doChangeStatus(electionId, newStatus) {
            $.post(
                'api/api.php/update_election_status',
                {
                    election_id: electionId,
                    new_status: newStatus
                }
            ).done(function (data) {
                if (data.error_code === 0) {
                    location.reload(true);
                } else {
                    BootstrapDialog.show({
                        title: 'Change Election Status',
                        message: data.error_message,
                        buttons: [{
                            label: 'ปิด',
                            action: function (self) {
                                self.close();
                                location.reload(true);
                            }
                        }]
                    });
                }
            }).fail(function () {
                BootstrapDialog.show({
                    title: 'Change Election Status',
                    message: 'เกิดข้อผิดพลาดในการเชื่อมต่อ Server',
                    buttons: [{
                        label: 'ปิด',
                        action: function (self) {
                            self.close();
                            location.reload(true);
                        }
                    }]
                });
            });
        }

        function onChangeStatus(element, electionId, dateString) {
            BootstrapDialog.show({
                title: 'Confirm Change Election Status',
                message: 'ยืนยันเปลี่ยนสถานะการเลือกตั้ง วันที่ ' + dateString + ' ?',
                buttons: [
                    {
                        label: 'ยกเลิก',
                        action: function (self) {
                            self.close();
                            location.reload(true);
                        }
                    },
                    {
                        label: 'เปลี่ยนสถานะ',
                        action: function (self) {
                            self.close();
                            const newStatus = element.checked ? 1 : 0;
                            doChangeStatus(electionId, newStatus);
                        }
                    }
                ]
            });
        }
    </script>

    <?php require_once('include/foot.inc'); ?>
    </body>
    </html>

<?php
$db->close();
?>