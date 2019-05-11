<!-- Left side column. contains the logo and sidebar -->
<aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">
        <!-- sidebar menu: : style can be found in sidebar.less -->
        <ul class="sidebar-menu" data-widget="tree" style="margin-top: 10px">
            <li>
                <a href="farm_info.php">
                    <i class="fa fa-info-circle"></i> <span>ข้อมูลฟาร์ม/บ่อเลี้ยง</span>
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="fa fa-bug"></i> <span>แหล่งพันธุ์ลูกกุ้ง</span>
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="fa fa-pencil-square-o"></i> <span>บันทึกการให้อาหารกุ้ง</span>
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="fa fa-tint"></i> <span>คุณภาพน้ำในบ่อเลี้ยง</span>
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="fa fa-th"></i> <span>สรุปผลการเลี้ยงกุ้ง</span>
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="fa fa-file-text"></i> <span>รายงานข้อมูล</span>
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="fa fa-user"></i> <span>ข้อมูลผู้ใช้งาน</span>
                </a>
            </li>
            <li>
                <a href="javascript:void(0);" id="logoutMenu">
                    <i class="fa fa-sign-out"></i> <span>ออกจากระบบ</span>
                </a>
            </li>

            <!--<li class="header" style="color: yellow; text-align: center">จัดการเนื้อหา</li>-->
            <!--<li class="treeview">
                <a href="#">
                    <i class="fa fa-file-text-o"></i> <span>บริการ/กิจกรรม</span>
                    <span class="pull-right-container">
                            <i class="fa fa-angle-left pull-right"></i>
                        </span>
                </a>
                <ul class="treeview-menu">
                    <li><a href="#"><i class="fa fa-circle-o"></i> บริการวิชาการ</a></li>
                    <li><a href="#"><i class="fa fa-circle-o"></i> บริการ In-house Training</a></li>
                    <li><a href="#"><i class="fa fa-circle-o"></i> บริการสังคม</a></li>
                    <li><a href="#"><i class="fa fa-circle-o"></i> บริการอบรมใบขับขี่</a></li>
                    <li><a href="#"><i class="fa fa-circle-o"></i> โครงการวิจัยและวิชาการ</a></li>
                    <li><a href="#"><i class="fa fa-circle-o"></i> วารสาร HR INTELLIGENCE</a></li>
                </ul>
            </li>-->
        </ul>
    </section>
    <!-- /.sidebar -->
</aside>
<script>
    $(document).ready(function () {
        $('#logoutMenu').click(function (e) {
            BootstrapDialog.show({
                title: 'Confirm Logout',
                message: 'ยืนยันออกจากระบบ?',
                buttons: [
                    {
                        label: 'ยกเลิก',
                        action: function (self) {
                            self.close();
                        }
                    },
                    {
                        label: 'ออกจากระบบ',
                        action: function (self) {
                            self.close();
                            doLogout();
                        }
                    }
                ]
            });
        });

        // Sets active link in Bootstrap menu
        // Add this code in a central place used\shared by all pages
        // like your _Layout.cshtml in ASP.NET MVC for example
        const url = (location.pathname + location.search).substr(1);
        $('a[href="' + url + '"]').parents('li,ul').addClass('active');
        //alert(url);
    });
</script>