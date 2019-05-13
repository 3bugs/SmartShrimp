<?php
session_start();
require_once 'global.php';

error_reporting(E_ERROR | E_PARSE);
header('Content-type: application/json; charset=utf-8');

header('Expires: Sun, 01 Jan 2014 00:00:00 GMT');
header('Cache-Control: no-store, no-cache, must-revalidate');
header('Cache-Control: post-check=0, pre-check=0', FALSE);
header('Pragma: no-cache');

$response = array();

$request = explode('/', trim($_SERVER['PATH_INFO'], '/'));
$action = strtolower(array_shift($request));
$id = array_shift($request);

require_once 'db_config.php';
$db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

if ($db->connect_errno) {
    $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
    $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
    $response[KEY_ERROR_MESSAGE_MORE] = $db->connect_error;
    echo json_encode($response);
    exit();
}
$db->set_charset("utf8");

//sleep(1); //todo:

switch ($action) {
    case 'login':
        doLogin();
        break;
    case 'logout':
        doLogout();
        break;
    case 'register':
        doRegister();
        break;
    case 'get_farm_info':
        doGetFarmInfo();
        break;
    case 'save_farm_info':
        doSaveFarmInfo();
        break;
    case 'get_pond':
        doGetPond();
        break;
    case 'add_pond':
        doAddPond();
        break;
    case 'update_pond':
        doUpdatePond();
        break;
    case 'delete_pond':
        doDeletePond();
        break;
    case 'get_feeding':
        doGetFeedingByPond();
        break;
    case 'add_feeding':
        doAddFeeding();
        break;
    case 'update_feeding':
        doUpdateFeeding();
        break;
    default:
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'No action specified or invalid action.';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
        break;
}

$db->close();
echo json_encode($response);
exit();

function doLogin()
{
    global $db, $response;

    $username = $db->real_escape_string($_POST['username']);
    $password = $db->real_escape_string($_POST['password']);

    $selectUserSql = "SELECT * FROM `user` WHERE `username` = '$username' AND `password` = '$password'";

    $selectUserResult = $db->query($selectUserSql);
    if ($selectUserResult) {
        if ($selectUserResult->num_rows > 0) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = '';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
            $response[KEY_LOGIN_SUCCESS] = TRUE;
            $user = fetchUser($selectUserResult);
            $response['user'] = $user;

            $_SESSION[KEY_SESSION_USER_ID] = $user['id'];
            $_SESSION[KEY_SESSION_USER_USERNAME] = $user['username'];
            $_SESSION[KEY_SESSION_USER_EMAIL] = $user['email'];
        } else {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = 'ชื่อผู้ใช้ หรือรหัสผ่าน ไม่ถูกต้อง';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
            $response[KEY_LOGIN_SUCCESS] = FALSE;
        }
        $selectUserResult->close();
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการอ่านข้อมูลบัญชีผู้ใช้';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $selectUserSql";
    }
}

function doLogout()
{
    global $response;

    session_destroy();
    $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
    $response[KEY_ERROR_MESSAGE] = 'ออกจากระบบสำเร็จ';
    $response[KEY_ERROR_MESSAGE_MORE] = '';
}

function fetchUser($selectUserResult)
{
    $row = $selectUserResult->fetch_assoc();

    $user = array();
    $user['id'] = (int)$row['id'];
    $user['username'] = $row['username'];
    $user['email'] = $row['email'];
    $user['first_name'] = $row['first_name'];
    $user['last_name'] = $row['last_name'];
    $user['address'] = $row['address'];
    $user['created_at'] = $row['created_at'];

    return $user;
}

function doRegister()
{
    global $db, $response;

    $username = $db->real_escape_string($_POST['username']);
    $password = $db->real_escape_string($_POST['password']);
    $email = $db->real_escape_string($_POST['email']);
    $firstName = $db->real_escape_string($_POST['firstName']);
    $lastName = $db->real_escape_string($_POST['lastName']);
    $address = $db->real_escape_string($_POST['address']);

    $selectExistingUserSQL = "SELECT * FROM `user` WHERE `username` = '$username'";
    $selectExistingUserResult = $db->query($selectExistingUserSQL);
    if ($selectExistingUserResult->num_rows > 0) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = "สมัครสมาชิกไม่ได้ เนื่องจากมีชื่อผู้ใช้ '$username' อยู่ในระบบแล้ว";
        $response[KEY_ERROR_MESSAGE_MORE] = '';
        $selectExistingUserResult->close();
        return;
    }
    $selectExistingUserResult->close();

    $insertUserSql = "INSERT INTO `user` (`username`, `password`, `email`, `first_name`, `last_name`, `address`) "
        . " VALUES ('$username', '$password', '$email', '$firstName', '$lastName', '$address')";
    $insertUserResult = $db->query($insertUserSql);
    if ($insertUserResult) {
        $insertId = $db->insert_id;
        $selectUserSql = "SELECT * FROM `user` WHERE `id` = $insertId";

        $selectUserResult = $db->query($selectUserSql);
        if ($selectUserResult) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = '';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
            $response['user'] = fetchUser($selectUserResult);
        }
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการบันทึกข้อมูล';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $insertUserSql";
    }
}

function doGetFarmInfo()
{
    global $db, $response;

    $sql = "SELECT * FROM `farm`";
    if ($result = $db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'อ่านข้อมูลฟาร์มสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';

        $farmList = array();
        while ($row = $result->fetch_assoc()) {
            $farm = array();
            $farm['name'] = $row['name'];
            $farm['address'] = $row['address'];
            $farm['sub_district'] = $row['sub_district'];
            $farm['district'] = $row['district'];
            $farm['province'] = $row['province'];
            $farm['postal_code'] = $row['postal_code'];
            $farm['farm_reg_id'] = $row['farm_reg_id'];
            array_push($farmList, $farm);
        }
        $result->close();
        $response[KEY_DATA_LIST] = $farmList;
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการอ่านข้อมูลฟาร์ม';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doSaveFarmInfo()
{
    global $db, $response;

    $farmName = $db->real_escape_string($_POST['farmName']);
    $address = $db->real_escape_string($_POST['address']);
    $subDistrict = $db->real_escape_string($_POST['subDistrict']);
    $district = $db->real_escape_string($_POST['district']);
    $province = $db->real_escape_string($_POST['province']);
    $postalCode = $db->real_escape_string($_POST['postalCode']);
    $farmRegId = $db->real_escape_string($_POST['farmRegId']);

    $selectExistingFarmSQL = "SELECT * FROM `farm`";
    $selectExistingFarmResult = $db->query($selectExistingFarmSQL);
    $count = $selectExistingFarmResult->num_rows;
    $selectExistingFarmResult->close();
    if ($count > 0) {
        $sql = "UPDATE `farm` SET name='$farmName', address='$address', sub_district='$subDistrict', district='$district', province='$province', postal_code='$postalCode', farm_reg_id='$farmRegId'";
    } else {
        $sql = "INSERT INTO `farm` (name, address, sub_district, district, province, postal_code, farm_reg_id) VALUES ('$farmName', '$address', '$subDistrict', '$district', '$province', '$postalCode', '$farmRegId')";
    }
    if ($db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'บันทึกข้อมูลฟาร์มสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการบันทึกข้อมูลฟาร์ม';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doGetPond()
{
    global $db, $response;

    $sql = "SELECT * FROM `pond` ORDER BY `number`";
    if ($result = $db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'อ่านข้อมูลบ่อเลี้ยงสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';

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
        $response[KEY_DATA_LIST] = $pondList;
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการอ่านข้อมูลบ่อเลี้ยง';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doAddPond()
{
    global $db, $response;

    $pondNumber = $db->real_escape_string($_POST['pondNumber']);
    $pondArea = $db->real_escape_string($_POST['pondArea']);
    $initialShrimpCount = $db->real_escape_string($_POST['initialShrimpCount']);

    if ($pondNumber <= 0) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'หมายเลขบ่อต้องเป็นเลขบวก (1, 2, 3, ...)';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
        return;
    }

    $selectExistingPondNumberSQL = "SELECT * FROM `pond` WHERE `number`=$pondNumber";
    $selectExistingPondNumberResult = $db->query($selectExistingPondNumberSQL);
    $count = $selectExistingPondNumberResult->num_rows;
    $selectExistingPondNumberResult->close();
    if ($count > 0) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'ไม่สามารถเพิ่มข้อมูลบ่อได้ เนื่องจากมีหมายเลขบ่อที่ระบุแล้ว (หมายเลขบ่อห้ามซ้ำ)';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
    } else {
        $sql = "INSERT INTO `pond` (number, area, initial_shrimp_count) VALUES ($pondNumber, $pondArea, $initialShrimpCount)";
        if ($db->query($sql)) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = 'เพิ่มข้อมูลบ่อเลี้ยงสำเร็จ';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
        } else {
            $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
            $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการบันทึกข้อมูลบ่อเลี้ยง';
            $errMessage = $db->error;
            $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
        }
    }
}

function doUpdatePond()
{
    global $db, $response;

    $pondId = (int)$db->real_escape_string($_POST['pondId']);
    $pondNumber = $db->real_escape_string($_POST['pondNumber']);
    $pondArea = $db->real_escape_string($_POST['pondArea']);
    $initialShrimpCount = $db->real_escape_string($_POST['initialShrimpCount']);

    $selectExistingPondNumberSQL = "SELECT * FROM `pond` WHERE `number`=$pondNumber";
    $selectExistingPondNumberResult = $db->query($selectExistingPondNumberSQL);
    if ($selectExistingPondNumberResult->num_rows > 0) {
        $row = $selectExistingPondNumberResult->fetch_assoc();
        if ($pondId !== (int)$row['id']) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
            $response[KEY_ERROR_MESSAGE] = 'ไม่สามารถบันทึกข้อมูลบ่อได้ เนื่องจากมีหมายเลขบ่อที่ระบุแล้ว (หมายเลขบ่อห้ามซ้ำ)';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
            $selectExistingPondNumberResult->close();
            return;
        }
    }
    $selectExistingPondNumberResult->close();

    $sql = "UPDATE `pond` SET number=$pondNumber, area=$pondArea, initial_shrimp_count=$initialShrimpCount WHERE id=$pondId";
    if ($db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'แก้ไขข้อมูลบ่อเลี้ยงสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการบันทึกข้อมูลบ่อเลี้ยง';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doDeletePond()
{
    global $db, $response;

    $pondId = $db->real_escape_string($_POST['pondId']);

    $sql = "DELETE FROM `pond` WHERE id=$pondId";
    if ($db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'ลบข้อมูลบ่อเลี้ยงสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการลบข้อมูลบ่อเลี้ยง';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doGetFeedingByPond()
{
    global $db, $response;

    $pondId = $db->real_escape_string($_POST['pondId']);

    $sql = "SELECT * FROM `feeding` WHERE `pond_id`=$pondId ORDER BY `feed_date` DESC";
    if ($result = $db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'อ่านข้อมูลการให้อาหารสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';

        $feedingList = array();
        while ($row = $result->fetch_assoc()) {
            $feeding = array();
            $feeding['id'] = (int)$row['id'];
            $feeding['pond_id'] = (int)$row['pond_id'];
            $feeding['feed_date'] = $row['feed_date'];
            $feeding['first_feed'] = (int)$row['first_feed'];
            $feeding['second_feed'] = (int)$row['second_feed'];
            $feeding['third_feed'] = (int)$row['third_feed'];
            array_push($feedingList, $feeding);
        }
        $response[KEY_DATA_LIST] = $feedingList;
        $result->close();
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการอ่านข้อมูลการให้อาหาร';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doAddFeeding()
{
    global $db, $response;

    $pondId = $db->real_escape_string($_POST['pondId']);
    $feedDate = $db->real_escape_string($_POST['feedDate']);
    $firstFeed = $db->real_escape_string($_POST['firstFeed']);
    $secondFeed = $db->real_escape_string($_POST['secondFeed']);
    $thirdFeed = $db->real_escape_string($_POST['thirdFeed']);

    $selectExistingFeedDateSql = "SELECT id FROM `feeding` WHERE feed_date='$feedDate'";
    if ($selectExistingFeedDateResult = $db->query($selectExistingFeedDateSql)) {
        if ($selectExistingFeedDateResult->num_rows > 0) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
            $response[KEY_ERROR_MESSAGE] = 'มีข้อมูลการให้อาหารในวันดังกล่าวแล้ว กรุณาย้อนไปหน้าแสดงรายการ แล้วกดที่วันที่นั้นหากต้องการกรอกมื้ออาหารที่ยังไม่ได้กรอก';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
            $selectExistingFeedDateResult->close();
            return;
        }
        $selectExistingFeedDateResult->close();

        $sql = "INSERT INTO `feeding` (`pond_id`, `feed_date`, `first_feed`, `second_feed`, `third_feed`) "
            . " VALUES ($pondId, '$feedDate', $firstFeed, $secondFeed, $thirdFeed)";
        if ($result = $db->query($sql)) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = 'บันทึกข้อมูลการให้อาหารสำเร็จ';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
        } else {
            $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
            $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการบันทึกข้อมูลการให้อาหาร';
            $errMessage = $db->error;
            $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
        }
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการบันทึกข้อมูลการให้อาหาร';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $selectExistingFeedDateSql";
    }
}

function doUpdateFeeding()
{
    global $db, $response;

    $feedingId = $db->real_escape_string($_POST['feedingId']);
    $firstFeed = $db->real_escape_string($_POST['firstFeed']);
    $secondFeed = $db->real_escape_string($_POST['secondFeed']);
    $thirdFeed = $db->real_escape_string($_POST['thirdFeed']);

    $sql = "UPDATE `feeding` SET `first_feed`=$firstFeed, `second_feed`=$secondFeed, `third_feed`=$thirdFeed "
        . " WHERE `id`=$feedingId";
    if ($result = $db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'บันทึกข้อมูลการให้อาหารสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการบันทึกข้อมูลการให้อาหาร';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doUpdateProfileImage($userType)
{
    global $db, $response;

    $table = '';
    if ($userType == USER_TYPE_ELDERLY) {
        $table = 'elderly';
    } else if ($userType == USER_TYPE_EMPLOYER) {
        $table = 'employer';
    }

    $userId = $_POST['user_id'];

    $imageFilename = createRandomString(16) . '.jpg';
    $dest = '../images/user/' . $imageFilename;

    if (!moveUploadedFile('profile_image', $dest)) {
        $response[KEY_ERROR_CODE] = 9; //ERROR_CODE_FILE_UPLOAD_ERROR;
        $response[KEY_ERROR_MESSAGE] = "เกิดข้อผิดพลาดในการบันทึกไฟล์รูปภาพ";
        $response[KEY_ERROR_MESSAGE_MORE] = getUploadErrorMessage($_FILES['profile_image']['error']);
    } else {
        $updateUserSql = "UPDATE `$table` SET image = '$imageFilename' WHERE id = $userId ";
        $updateUserResult = $db->query($updateUserSql);
        if ($updateUserResult) {
            $selectUserSql = "SELECT * FROM `$table` WHERE id = $userId ";
            $selectUserResult = $db->query($selectUserSql);

            if ($selectUserResult) {
                $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
                $response[KEY_ERROR_MESSAGE] = '';
                $response[KEY_ERROR_MESSAGE_MORE] = '';
                $response['user'] = fetchUser($userType, $selectUserResult);
            } else {
                $response[KEY_ERROR_CODE] = ERROR_CODE_SQL_ERROR;
                $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
                $errMessage = $db->error;
                $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $selectUserSql";
            }
        } else {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SQL_ERROR;
            $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
            $errMessage = $db->error;
            $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $updateUserSql";
        }
    }
}

function createRandomString($length)
{
    $key = '';
    $keys = array_merge(range(0, 9), range('a', 'z'));

    for ($i = 0; $i < $length; $i++) {
        $key .= $keys[array_rand($keys)];
    }

    return $key;
}

function moveUploadedFile($key, $dest)
{
    global $response;

    $response['name'] = $_FILES[$key]['name'];
    $response['type'] = $_FILES[$key]['type'];
    $response['size'] = $_FILES[$key]['size'];
    $response['tmp_name'] = $_FILES[$key]['tmp_name'];

    $src = $_FILES[$key]['tmp_name'];
    $response['upload_src'] = $src;

    $response['upload_dest'] = $dest;

    return move_uploaded_file($src, $dest);
}

function getUploadErrorMessage($errCode)
{
    $message = '';
    switch ($errCode) {
        case UPLOAD_ERR_OK:
            break;
        case UPLOAD_ERR_INI_SIZE:
        case UPLOAD_ERR_FORM_SIZE:
            $message .= 'File too large (limit of ' . get_max_upload() . ' bytes).';
            break;
        case UPLOAD_ERR_PARTIAL:
            $message .= 'File upload was not completed.';
            break;
        case UPLOAD_ERR_NO_FILE:
            $message .= 'Zero-length file uploaded.';
            break;
        default:
            $message .= 'Internal error #' . $errCode;
            break;
    }
    return $message;
}

?>
