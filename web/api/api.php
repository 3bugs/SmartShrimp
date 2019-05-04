<?php
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

sleep(1); //todo:

switch ($action) {
    case 'login':
        doLogin();
        break;
    case 'register':
        doRegister();
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
            $response['user'] = fetchUser($selectUserResult);
        } else {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = '';
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

function fetchUser($selectUserResult)
{
    $row = $selectUserResult->fetch_assoc();

    $user = array();
    $user['id'] = (int)$row['id'];
    $user['username'] = $row['username'];
    $user['email'] = $row['email'];
    $user['created_at'] = $row['created_at'];

    return $user;
}

function doRegister()
{
    global $db, $response;

    $username = $db->real_escape_string($_POST['username']);
    $password = $db->real_escape_string($_POST['password']);
    $email = $db->real_escape_string($_POST['email']);

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

    $insertUserSql = "INSERT INTO `user` (`username`, `password`, `email`) VALUES ('$username', '$password', '$email')";
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
