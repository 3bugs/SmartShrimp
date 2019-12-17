<?php
require_once 'include/head_php.php';

$sql = "SELECT * FROM `hatchery` ORDER BY `id`";
if ($result = $db->query($sql)) {
    $hatcheryList = array();
    while ($hatchery = $result->fetch_assoc()) {
        array_push($hatcheryList, $hatchery);
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
        <!-- DataTables -->
        <link rel="stylesheet" href="bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
        <style>

        </style>
    </head>
    <body class="hold-transition skin-blue sidebar-mini">

    <!-- Add Hatchery Modal -->
    <div class="modal fade" id="addModal" role="dialog">
        <div class="modal-dialog modal-lg">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;
                    </button>
                    <h4 class="modal-title">เพิ่มแหล่งพันธุ์ลูกกุ้ง</h4>
                </div>
                <div class="modal-body">
                    <form id="formAdd" role="form"
                          style="margin-top: 0; margin-bottom: 0">
                        <div class="box-body">

                            <!--ชื่อโรงเพาะฟัก-->
                            <div class="form-group">
                                <label for="inputHatcheryName">ชื่อโรงเพาะฟัก:</label>
                                <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-font"></i>
                                    </span>
                                    <input type="text" class="form-control"
                                           id="inputHatcheryName"
                                           placeholder="กรอกชื่อโรงเพาะฟัก" required
                                           oninvalid="this.setCustomValidity('กรอกชื่อโรงเพาะฟัก')"
                                           oninput="this.setCustomValidity('')">
                                </div>
                            </div>

                            <!--เจ้าของ, เลขที่ใบกำกับ-->
                            <div class="row">
                                <!--เจ้าของ-->
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="inputOwner">ชื่อเจ้าของ:</label>
                                        <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-user"></i>
                                    </span>
                                            <input type="text" class="form-control"
                                                   id="inputOwner"
                                                   placeholder="กรอกชื่อเจ้าของโรงเพาะฟัก" required
                                                   oninvalid="this.setCustomValidity('กรอกชื่อเจ้าของโรงเพาะฟัก')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>

                                <!--เลขที่ใบกำกับ-->
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="inputFmdNumber">เลขที่ใบกำกับพันธุ์ลูกกุ้ง:</label>
                                        <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-tag"></i>
                                    </span>
                                            <input type="text" class="form-control"
                                                   id="inputFmdNumber"
                                                   placeholder="กรอกเลขที่ใบกำกับพันธุ์ลูกกุ้ง" required
                                                   oninvalid="this.setCustomValidity('กรอกเลขที่ใบกำกับพันธุ์ลูกกุ้ง')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!--ที่อยู่, แขวง/ตำบล-->
                            <div class="row">
                                <!--ที่อยู่-->
                                <div class="col-md-8">
                                    <div class="form-group">
                                        <label for="inputAddress">ที่อยู่:</label>
                                        <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-envelope"></i>
                                    </span>
                                            <input type="text" class="form-control"
                                                   id="inputAddress"
                                                   placeholder="กรอกที่อยู่" required
                                                   oninvalid="this.setCustomValidity('กรอกที่อยู่')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>

                                <!--แขวง/ตำบล-->
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="inputSubDistrict">แขวง/ตำบล:</label>
                                        <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-envelope"></i>
                                    </span>
                                            <input type="text" class="form-control"
                                                   id="inputSubDistrict"
                                                   placeholder="กรอกแขวง/ตำบล" required
                                                   oninvalid="this.setCustomValidity('กรอกแขวง/ตำบล')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!--เขต/อำเภอ, จังหวัด, รหัสไปรษณีย์-->
                            <div class="row">
                                <!--เขต/อำเภอ-->
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="inputDistrict">เขต/อำเภอ:</label>
                                        <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-envelope"></i>
                                    </span>
                                            <input type="text" class="form-control"
                                                   id="inputDistrict"
                                                   placeholder="กรอกเขต/อำเภอ" required
                                                   oninvalid="this.setCustomValidity('กรอกเขต/อำเภอ')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>

                                <!--จังหวัด-->
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="inputProvince">จังหวัด:</label>
                                        <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-envelope"></i>
                                    </span>
                                            <input type="text" class="form-control"
                                                   id="inputProvince"
                                                   placeholder="กรอกจังหวัด" required
                                                   oninvalid="this.setCustomValidity('กรอกจังหวัด')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>

                                <!--รหัสไปรษณีย์-->
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="inputPostalCode">รหัสไปรษณีย์:</label>
                                        <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-envelope"></i>
                                    </span>
                                            <input type="text" class="form-control"
                                                   id="inputPostalCode"
                                                   placeholder="กรอกรหัสไปรษณีย์" required
                                                   oninvalid="this.setCustomValidity('กรอกรหัสไปรษณีย์')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div id="divLoading" style="text-align: center;">
                                <img src="images/ic_loading4.gif" height="32px"/>&nbsp;รอสักครู่
                            </div>
                            <div id="addResult"
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

    <!-- Edit Hatchery Modal -->
    <div class="modal fade" id="editModal" role="dialog">
        <div class="modal-dialog modal-lg">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;
                    </button>
                    <h4 class="modal-title">แก้ไขแหล่งพันธุ์ลูกกุ้ง</h4>
                </div>
                <div class="modal-body">
                    <form id="formEdit" role="form"
                          style="margin-top: 0; margin-bottom: 0">
                        <div class="box-body">
                            <input type="hidden" id="inputId">

                            <!--ชื่อโรงเพาะฟัก-->
                            <div class="form-group">
                                <label for="inputHatcheryName">ชื่อโรงเพาะฟัก:</label>
                                <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-font"></i>
                                    </span>
                                    <input type="text" class="form-control"
                                           id="inputHatcheryName"
                                           placeholder="กรอกชื่อโรงเพาะฟัก" required
                                           oninvalid="this.setCustomValidity('กรอกชื่อโรงเพาะฟัก')"
                                           oninput="this.setCustomValidity('')">
                                </div>
                            </div>

                            <!--เจ้าของ, เลขที่ใบกำกับ-->
                            <div class="row">
                                <!--เจ้าของ-->
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="inputOwner">ชื่อเจ้าของ:</label>
                                        <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-user"></i>
                                    </span>
                                            <input type="text" class="form-control"
                                                   id="inputOwner"
                                                   placeholder="กรอกชื่อเจ้าของโรงเพาะฟัก" required
                                                   oninvalid="this.setCustomValidity('กรอกชื่อเจ้าของโรงเพาะฟัก')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>

                                <!--เลขที่ใบกำกับ-->
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="inputFmdNumber">เลขที่ใบกำกับพันธุ์ลูกกุ้ง:</label>
                                        <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-tag"></i>
                                    </span>
                                            <input type="text" class="form-control"
                                                   id="inputFmdNumber"
                                                   placeholder="กรอกเลขที่ใบกำกับพันธุ์ลูกกุ้ง" required
                                                   oninvalid="this.setCustomValidity('กรอกเลขที่ใบกำกับพันธุ์ลูกกุ้ง')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!--ที่อยู่, แขวง/ตำบล-->
                            <div class="row">
                                <!--ที่อยู่-->
                                <div class="col-md-8">
                                    <div class="form-group">
                                        <label for="inputAddress">ที่อยู่:</label>
                                        <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-envelope"></i>
                                    </span>
                                            <input type="text" class="form-control"
                                                   id="inputAddress"
                                                   placeholder="กรอกที่อยู่" required
                                                   oninvalid="this.setCustomValidity('กรอกที่อยู่')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>

                                <!--แขวง/ตำบล-->
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="inputSubDistrict">แขวง/ตำบล:</label>
                                        <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-envelope"></i>
                                    </span>
                                            <input type="text" class="form-control"
                                                   id="inputSubDistrict"
                                                   placeholder="กรอกแขวง/ตำบล" required
                                                   oninvalid="this.setCustomValidity('กรอกแขวง/ตำบล')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!--เขต/อำเภอ, จังหวัด, รหัสไปรษณีย์-->
                            <div class="row">
                                <!--เขต/อำเภอ-->
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="inputDistrict">เขต/อำเภอ:</label>
                                        <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-envelope"></i>
                                    </span>
                                            <input type="text" class="form-control"
                                                   id="inputDistrict"
                                                   placeholder="กรอกเขต/อำเภอ" required
                                                   oninvalid="this.setCustomValidity('กรอกเขต/อำเภอ')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>

                                <!--จังหวัด-->
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="inputProvince">จังหวัด:</label>
                                        <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-envelope"></i>
                                    </span>
                                            <input type="text" class="form-control"
                                                   id="inputProvince"
                                                   placeholder="กรอกจังหวัด" required
                                                   oninvalid="this.setCustomValidity('กรอกจังหวัด')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>

                                <!--รหัสไปรษณีย์-->
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="inputPostalCode">รหัสไปรษณีย์:</label>
                                        <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-envelope"></i>
                                    </span>
                                            <input type="text" class="form-control"
                                                   id="inputPostalCode"
                                                   placeholder="กรอกรหัสไปรษณีย์" required
                                                   oninvalid="this.setCustomValidity('กรอกรหัสไปรษณีย์')"
                                                   oninput="this.setCustomValidity('')">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div id="divLoading" style="text-align: center;">
                                <img src="images/ic_loading4.gif" height="32px"/>&nbsp;รอสักครู่
                            </div>
                            <div id="editResult"
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
                    แหล่งพันธุ์ลูกกุ้ง
                </h1>
            </section>

            <!-- Main content: แหล่งพันธุ์ลูกกุ้ง -->
            <section class="content">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="box box-info">
                            <div class="box-header with-border">
                                <h3 class="box-title">&nbsp;</h3>
                                <button type="button" class="btn btn-success pull-right"
                                        data-toggle="modal" data-target="#addModal">
                                    <span class="fa fa-plus"></span>&nbsp;
                                    เพิ่มแหล่งพันธุ์ลูกกุ้ง
                                </button>
                            </div>
                            <div class="box-body">
                                <table id="tableHatchery" class="table table-bordered table-striped">
                                    <thead>
                                    <tr>
                                        <th style="width: 30%; text-align: center">ชื่อโรงเพาะฟัก</th>
                                        <th style="width: 15%; text-align: center">เจ้าของ</th>
                                        <th style="width: 15%; text-align: center">เลขที่ใบกำกับพันธุ์</th>
                                        <th style="width: 40%; text-align: center">ที่อยู่</th>
                                        <th style="text-align: center" nowrap>จัดการ</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <?php
                                    if (sizeof($hatcheryList) == 0) {
                                        ?>
                                        <!--<tr valign="middle">
                                            <td colspan="3" align="center">ไม่มีข้อมูลบ่อเลี้ยง</td>
                                        </tr>-->
                                        <?php
                                    } else {
                                        foreach ($hatcheryList as $hatchery) {
                                            $id = (int)$hatchery['id'];
                                            $hatcheryName = $hatchery['name'];
                                            $owner = $hatchery['owner'];
                                            $fmdNumber = $hatchery['fmd_no'];
                                            $fullAddress = "{$hatchery['address']} {$hatchery['sub_district']} {$hatchery['district']} {$hatchery['province']} {$hatchery['postal_code']}";
                                            ?>
                                            <tr style="">
                                                <td style="vertical-align: top; text-align: left"><?= $hatcheryName; ?></td>
                                                <td style="vertical-align: top; text-align: left"><?= $owner; ?></td>
                                                <td style="vertical-align: top; text-align: center"><?= $fmdNumber; ?></td>
                                                <td style="vertical-align: top; text-align: left"><?= $fullAddress; ?></td>
                                                <td style="text-align: center" nowrap>
                                                    <button type="button" class="btn btn-warning"
                                                            style="margin-left: 6px; margin-right: 3px;"
                                                            onclick="onClickEdit(
                                                                    this,
                                                            <?= $id; ?>,
                                                                    '<?= $hatcheryName; ?>',
                                                                    '<?= $owner; ?>',
                                                                    '<?= $fmdNumber; ?>',
                                                                    '<?= $hatchery['address']; ?>',
                                                                    '<?= $hatchery['sub_district']; ?>',
                                                                    '<?= $hatchery['district']; ?>',
                                                                    '<?= $hatchery['province']; ?>',
                                                                    '<?= $hatchery['postal_code']; ?>'
                                                                    )">
                                                        <span class="fa fa-edit"></span>&nbsp;
                                                        แก้ไข
                                                    </button>
                                                    <button type="button" class="btn btn-danger"
                                                            style="margin-left: 3px; margin-right: 6px;"
                                                            onclick="onClickDelete(
                                                                this, 
                                                            <?= $id; ?>, 
                                                                    '<?= $hatcheryName; ?>',
                                                                    '<?= $owner; ?>',
                                                                    '<?= $fmdNumber; ?>',
                                                                    '<?= $hatchery['address']; ?>',
                                                                    '<?= $hatchery['sub_district']; ?>',
                                                                    '<?= $hatchery['district']; ?>',
                                                                    '<?= $hatchery['province']; ?>',
                                                                    '<?= $hatchery['postal_code']; ?>'
                                                                    )">
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
            $('#formAdd').submit(event => {
                event.preventDefault();
                doAddHatchery();
            });
            $('#formEdit').submit(event => {
                event.preventDefault();
                doUpdateHatchery();
            });

            $('#formEdit #divLoading').hide();
            $('#formAdd #divLoading').hide();

            $('#tableHatchery').DataTable({
                stateSave: true,
                stateDuration: -1, // sessionStorage
                order: [[0, 'desc']],
                language: {
                    lengthMenu: "แสดงหน้าละ _MENU_ แถวข้อมูล",
                    zeroRecords: "ไม่มีข้อมูล",
                    emptyTable: "ไม่มีข้อมูล",
                    info: "หน้าที่ _PAGE_ จากทั้งหมด _PAGES_ หน้า",
                    infoEmpty: "แสดง 0 แถวข้อมูล",
                    infoFiltered: "(กรองจากทั้งหมด _MAX_ แถวข้อมูล)",
                    search: "ค้นหา:",
                    thousands: ",",
                    loadingRecords: "รอสักครู่...",
                    processing: "กำลังประมวลผล...",
                    paginate: {
                        first: "หน้าแรก",
                        last: "หน้าสุดท้าย",
                        next: "ถัดไป",
                        previous: "ก่อนหน้า"
                    },
                },
                drawCallback: function (row, data) {
                    //$('.my-toggle').bootstrapToggle();
                }
            });
        });

        function onClickEdit(element, id, name, owner, fmdNumber, address, subDistrict, district, province, postalCode) {
            $('#formEdit #inputId').val(id);
            $('#formEdit #inputHatcheryName').val(name);
            $('#formEdit #inputOwner').val(owner);
            $('#formEdit #inputFmdNumber').val(fmdNumber);
            $('#formEdit #inputAddress').val(address);
            $('#formEdit #inputSubDistrict').val(subDistrict);
            $('#formEdit #inputDistrict').val(district);
            $('#formEdit #inputProvince').val(province);
            $('#formEdit #inputPostalCode').val(postalCode);

            $('#formEdit #editResult').text('');
            $('#editModal').modal('show');
        }

        function onClickDelete(element, id, name) {
            BootstrapDialog.show({
                title: 'Confirm Delete',
                message: 'ยืนยันลบ ' + name + '?',
                buttons: [{
                    label: 'ลบ',
                    action: function (self) {
                        doDeleteHathery(id);
                        self.close();
                    },
                    cssClass: 'btn-primary'
                }, {
                    label: 'ยกเลิก',
                    action: function (self) {
                        self.close();
                    }
                }]
            });
        }

        function doDeleteHathery(id) {
            $.post(
                'api/api.php/delete_hatchery',
                {
                    hatchery_id: id,
                }
            ).done(function (data) {
                if (data.error_code === 0) {
                    location.reload(true);
                } else {
                    BootstrapDialog.show({
                        title: 'Delete Error',
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
                    title: 'Delete Error',
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

        function doAddHatchery() {
            $('#formAdd #divLoading').show();

            $.post(
                'api/api.php/add_hatchery',
                {
                    name: $('#formAdd #inputHatcheryName').val(),
                    owner: $('#formAdd #inputOwner').val(),
                    fmd_no: $('#formAdd #inputFmdNumber').val(),
                    address: $('#formAdd #inputAddress').val(),
                    sub_district: $('#formAdd #inputSubDistrict').val(),
                    district: $('#formAdd #inputDistrict').val(),
                    province: $('#formAdd #inputProvince').val(),
                    postal_code: $('#formAdd #inputPostalCode').val(),
                }
            ).done(function (data) {
                $('#formAdd #divLoading').hide();

                if (data.error_code === 0) {
                    location.reload(true);
                } else {
                    $('#formAdd #addResult').text(data.error_message);
                }
            }).fail(function () {
                $('#formAdd #divLoading').hide();
                $('#formAdd #addResult').text('เกิดข้อผิดพลาดในการเชื่อมต่อ Server');
            });
        }

        function doUpdateHatchery() {
            $('#formEdit #divLoading').show();

            $.post(
                'api/api.php/update_hatchery',
                {
                    hatchery_id: $('#formEdit #inputId').val(),
                    name: $('#formEdit #inputHatcheryName').val(),
                    owner: $('#formEdit #inputOwner').val(),
                    fmd_no: $('#formEdit #inputFmdNumber').val(),
                    address: $('#formEdit #inputAddress').val(),
                    sub_district: $('#formEdit #inputSubDistrict').val(),
                    district: $('#formEdit #inputDistrict').val(),
                    province: $('#formEdit #inputProvince').val(),
                    postal_code: $('#formEdit #inputPostalCode').val(),
                }
            ).done(function (data) {
                $('#formEdit #divLoading').hide();

                if (data.error_code === 0) {
                    location.reload(true);
                } else {
                    $('#formEdit #editResult').text(data.error_message);
                }
            }).fail(function () {
                $('#formEdit #divLoading').hide();
                $('#formEdit #editResult').text('เกิดข้อผิดพลาดในการเชื่อมต่อ Server');
            });
        }
    </script>

    <?php require_once('include/foot.inc'); ?>
    <!-- DataTables -->
    <script src="bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
    <script src="bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
    </body>
    </html>

<?php
require_once 'include/foot_php.inc';

function valueOrEmptyString($value)
{
    return isset($value) ? $value : '';
}

?>