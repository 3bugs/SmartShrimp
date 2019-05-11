<?php
session_start();
require_once 'api/global.php';

error_reporting(E_ERROR | E_PARSE);
header('Content-type: text/html; charset=utf-8');
header('Expires: Sun, 01 Jan 2014 00:00:00 GMT');
header('Cache-Control: no-store, no-cache, must-revalidate');
header('Cache-Control: post-check=0, pre-check=0', FALSE);
header('Pragma: no-cache');
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
    <?php
    if (isset($_SESSION[KEY_SESSION_USER_ID])) {
        require_once('include/header.inc');
        require_once('include/sidebar.inc');
    }
    ?>

    <!-- Content Wrapper. Contains page content -->
    <div <?php if (isset($_SESSION[KEY_SESSION_USER_ID])) { ?> class="content-wrapper" <?php } ?>>
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <?php
            if (!isset($_SESSION[KEY_SESSION_USER_ID])) {
                ?>
                <div style="text-align: center">
                    <div style="height: 60px"></div>
                    <img src="images/ic_splash.png" width="300px">
                    <div style="margin-top: -50px; color: white">
                        <h1>SMART SHRIMP</h1>
                    </div>
                </div>
                <?php
            }
            ?>
        </section>

        <!-- Main content -->
        <section class="content">
            <?php
            if (!isset($_SESSION[KEY_SESSION_USER_ID])) {
                ?>
                <div class="container">
                    <div style="text-align: center">
                        <button type="button" class="btn btn-info"
                                data-toggle="modal" data-target="#loginModal"
                                style="padding: 15px 25px; margin-top: 30px;">
                            <span class="fa fa-sign-in"></span>&nbsp;
                            เข้าสู่ระบบ
                        </button>
                    </div>

                    <!-- Modal -->
                    <div class="modal fade" id="loginModal" role="dialog">
                        <div class="modal-dialog modal-md">
                            <!-- Modal content-->
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                                    <h4 class="modal-title">เข้าสู่ระบบ</h4>
                                </div>
                                <div class="modal-body">

                                    <form id="loginForm" role="form" style="margin-top: 0; margin-bottom: 0">
                                        <div class="box-body">
                                            <!--ชื่อผู้ใช้-->
                                            <div class="form-group">
                                                <label for="usernameInput">ชื่อผู้ใช้:</label>
                                                <div class="input-group">
                                                    <span class="input-group-addon">
                                                        <i class="fa fa-user"></i>
                                                    </span>
                                                    <input type="text" class="form-control" id="usernameInput"
                                                           placeholder="กรอกชื่อผู้ใช้งาน" required
                                                           oninvalid="this.setCustomValidity('กรอกชื่อผู้ใช้')"
                                                           oninput="this.setCustomValidity('')">
                                                </div>
                                            </div>
                                            <!--รหัสผ่าน-->
                                            <div class="form-group">
                                                <label for="passwordInput">รหัสผ่าน:</label>
                                                <div class="input-group">
                                                    <span class="input-group-addon">
                                                        <i class="fa fa-lock"></i>
                                                    </span>
                                                    <input type="password" class="form-control" id="passwordInput"
                                                           placeholder="กรอกรหัสผ่าน" required
                                                           oninvalid="this.setCustomValidity('กรอกรหัสผ่าน')"
                                                           oninput="this.setCustomValidity('')">
                                                </div>
                                            </div>
                                            <div id="loginResult" style="text-align: center; color: red; margin-top: 25px; margin-bottom: 20px;">
                                            </div>
                                        </div>
                                        <!-- /.box-body -->
                                        <div class="box-footer">
                                            <button id="loginButton" type="submit" class="btn btn-info pull-right">
                                                <span class="fa fa-sign-in"></span>&nbsp;
                                                ลงชื่อเข้าสู่ระบบ
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
                <?php
            } else {
                ?>
                <div style="text-align: center">
                    <img src="images/ic_splash.png" width="300px">
                    <div style="font-size: 60px; text-align: center; margin-top: 0px">
                        ยินดีต้อนรับ <?php echo $_SESSION[KEY_SESSION_USER_USERNAME]; ?></div>
                </div>
                <?php
            }
            ?>
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->

    <?php if (isset($_SESSION[KEY_SESSION_USER_ID])) require_once('include/footer.inc'); ?>
</div>
<!-- ./wrapper -->

<script>
    $(document).ready(function () {
        $('#loginForm').submit(function (e) {
            e.preventDefault();
            doLogin();
            //window.location.href = 'login.php';
        });
    });

    function doLogin() {
        $('#loginResult').text('');
        $.post(
            'api/api.php/login',
            {
                username: $('#usernameInput').val(),
                password: $('#passwordInput').val()
            }
        ).done(function (data) {
            if (data.error_code === 0) {
                if (data.login_success) {
                    location.reload(true);
                    //window.location.href = "index.php";
                } else {
                    $('#loginResult').text(data.error_message);
                }
            } else {
                $('#loginResult').text(data.error_message);
            }
        }).fail(function () {
            $('#loginResult').text('เกิดข้อผิดพลาดในการเชื่อมต่อ Server');
        });
    }
</script>

<?php require_once('include/foot.inc'); ?>
</body>
</html>
