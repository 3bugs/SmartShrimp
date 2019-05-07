<!-- Left side column. contains the logo and sidebar -->
<aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">
        <!-- sidebar menu: : style can be found in sidebar.less -->
        <ul class="sidebar-menu" data-widget="tree">
            <li>
                <a href="member.php">
                    <i class="fa fa-user"></i> <span>สมาชิก</span>
                </a>
            </li>
            <li>
                <a href="course.php">
                    <i class="fa fa-pencil"></i> <span>บริการวิชาการ</span>
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="fa fa-money"></i> <span>การแจ้งโอนเงิน</span>
                </a>
            </li>
            <li class="header" style="color: yellow; text-align: center">จัดการเนื้อหา</li>
            <li class="treeview">
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
            </li>
            <li class="treeview">
                <a href="#">
                    <i class="fa fa-newspaper-o"></i> <span>ข่าวประชาสัมพันธ์</span>
                    <span class="pull-right-container">
                            <i class="fa fa-angle-left pull-right"></i>
                        </span>
                </a>
                <ul class="treeview-menu">
                    <li><a href="#"><i class="fa fa-circle-o"></i> ข่าวโครงการฝึกอบรม</a></li>
                    <li><a href="#"><i class="fa fa-circle-o"></i> ข่าวประชาสัมพันธ์</a></li>
                </ul>
            </li>
            <li>
                <a href="#">
                    <i class="fa fa-image"></i> <span>ภาพกิจกรรม</span>
                </a>
            </li>
            <!--<li>
                <a href="#">
                    <i class="fa fa-link"></i> <span>WEB LINKS</span>
                </a>
            </li>-->
        </ul>
    </section>
    <!-- /.sidebar -->
</aside>
<script>
    $(document).ready(function () {
        // Sets active link in Bootstrap menu
        // Add this code in a central place used\shared by all pages
        // like your _Layout.cshtml in ASP.NET MVC for example
        const url = (location.pathname+location.search).substr(1);
        $('a[href="' + url + '"]').parents('li,ul').addClass('active');
        //alert(url);
    });
</script>