<?php
session_start();
require_once 'global.php';
require_once 'jwt.php';

error_reporting(E_ERROR | E_PARSE);
header('Access-Control-Allow-Origin: *');
header('Content-type: application/json; charset=utf-8');

header('Expires: Sun, 01 Jan 2014 00:00:00 GMT');
header('Cache-Control: no-store, no-cache, must-revalidate');
header('Cache-Control: post-check=0, pre-check=0', FALSE);
header('Pragma: no-cache');

$response = array();

$request = explode('/', trim($_SERVER['PATH_INFO'], '/'));
$action = strtolower(array_shift($request));
$id = array_shift($request);

require_once '../include/db_config.inc';
$db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

if ($db->connect_errno) {
    $response[KEY_ERROR_CODE] = ERROR_CODE_CONNECT_DB_FAILED;
    $response[KEY_ERROR_MESSAGE] = 'การเชื่อมต่อฐานข้อมูลล้มเหลว';
    $response[KEY_ERROR_MESSAGE_MORE] = $db->connect_error;
    echo json_encode($response);
    exit();
}
$db->set_charset("utf8");

switch ($action) {
    case 'register_member':
        if (isset($_POST['address'])) {
            doRegisterMember(
                $db->escape_string($_POST['title']),
                $db->escape_string($_POST['first_name']),
                $db->escape_string($_POST['last_name']),
                $db->escape_string($_POST['age']),
                $db->escape_string($_POST['job_position']),
                $db->escape_string($_POST['organization_name']),
                $db->escape_string($_POST['organization_type']),
                $db->escape_string($_POST['phone']),
                $db->escape_string($_POST['email']),
                $db->escape_string($_POST['password']),

                $db->escape_string($_POST['address']),
                $db->escape_string($_POST['sub_district']),
                $db->escape_string($_POST['district']),
                $db->escape_string($_POST['province']),
                $db->escape_string($_POST['postal_code']),
                $db->escape_string($_POST['organization_phone']),
                $db->escape_string($_POST['tax_id'])
            );
        } else {
            doRegisterMember(
                $db->escape_string($_POST['title']),
                $db->escape_string($_POST['first_name']),
                $db->escape_string($_POST['last_name']),
                $db->escape_string($_POST['age']),
                $db->escape_string($_POST['job_position']),
                $db->escape_string($_POST['organization_name']),
                $db->escape_string($_POST['organization_type']),
                $db->escape_string($_POST['phone']),
                $db->escape_string($_POST['email']),
                $db->escape_string($_POST['password'])
            );
        }
        break;
    case 'login_member':
        doLoginMember(
            $db->escape_string($_POST['email']),
            $db->escape_string($_POST['password'])
        );
        break;
    case 'logout_member':
        doLogoutMember($db->escape_string($_POST['member_id']));
        break;
    case 'delete_member':
        doDeleteMember($db->escape_string($_POST['member_id']));
        break;
    case 'register_course_individual':
        doRegisterCourseIndividual(
            $db->escape_string($_POST['course_id']),
            $db->escape_string($_POST['title']),
            $db->escape_string($_POST['first_name']),
            $db->escape_string($_POST['last_name']),
            $db->escape_string($_POST['age']),
            $db->escape_string($_POST['job_position']),
            $db->escape_string($_POST['organization_name']),
            $db->escape_string($_POST['organization_type']),
            $db->escape_string($_POST['phone']),
            $db->escape_string($_POST['email'])
        );
        break;
    case 'register_course_group':
        doRegisterCourseGroup(
            json_decode($_POST['params'], TRUE)
        );
        break;
    case 'fetch_name_titles':
        doFetchNameTitles();
        break;

    case 'login_user':
        doLoginUser($_POST['username'], $_POST['password']);
        break;
    case 'logout_user':
        doLogoutUser();
        break;

    default:
        $response[KEY_ERROR_CODE] = ERROR_CODE_INVALID_ACTION;
        $response[KEY_ERROR_MESSAGE] = 'No action specified or invalid action.';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
        break;
}

$db->close();
echo json_encode($response);
exit();

function doRegisterMember($title, $firstName, $lastName, $age, $jobPosition,
                          $organizationName, $organizationType, $phone, $email, $password,
                          $address = NULL, $subDistrict = NULL, $district = NULL, $province = NULL,
                          $postalCode = NULL, $organizationPhone = NULL, $taxId = NULL)
{
    global $db, $response;

    $sql = "SELECT id FROM member WHERE email = '$email'";
    if ($result = $db->query($sql)) {
        if ($result->num_rows > 0) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_USERNAME_ALREADY_EXISTS;
            $response[KEY_ERROR_MESSAGE] = "ไม่สามารถสมัครสมาชิกได้ เนื่องจากมีอีเมล $email ในระบบแล้ว";
            $response[KEY_ERROR_MESSAGE_MORE] = "";
            return;
        }
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SQL_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
        return;
    }
    $result->close();

    if ($address == NULL) {
        $sql = "INSERT INTO member(title, first_name, last_name, age, job_position, organization_name, organization_type, phone, email, password) "
            . " VALUES ('$title', '$firstName', '$lastName', $age, '$jobPosition', '$organizationName', '$organizationType', '$phone', '$email', '$password')";
    } else {
        $sql = "INSERT INTO member(title, first_name, last_name, age, job_position, organization_name, organization_type, phone, email, password, address, sub_district, district, province, postal_code, organization_phone, tax_id) "
            . " VALUES ('$title', '$firstName', '$lastName', $age, '$jobPosition', '$organizationName', '$organizationType', '$phone', '$email', '$password', '$address', '$subDistrict', '$district', '$province', '$postalCode', '$organizationPhone', '$taxId')";
    }
    if ($result = $db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'สมัครสมาชิกสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SQL_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล' . $db->error;
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doLoginMember($email, $password)
{
    global $db, $response;

    $sql = "SELECT * FROM member WHERE email = '$email' AND password = '$password'";
    if ($result = $db->query($sql)) {
        if ($result->num_rows == 0) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_LOGIN_FAILED;
            $response[KEY_ERROR_MESSAGE] = "อีเมล หรือรหัสผ่าน ไม่ถูกต้อง";
            $response[KEY_ERROR_MESSAGE_MORE] = "";
            $response['member_data'] = null;
        } else {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = 'เข้าสู่ระบบสำเร็จ';
            $response[KEY_ERROR_MESSAGE_MORE] = '';

            $row = $result->fetch_assoc();
            $userData = array();
            $userData['id'] = (int)$row['id'];
            $userData['title'] = $row['title'];
            $userData['first_name'] = $row['first_name'];
            $userData['last_name'] = $row['last_name'];
            $userData['age'] = (int)$row['age'];
            $userData['job_position'] = $row['job_position'];
            $userData['organization_name'] = $row['organization_name'];
            $userData['organization_type'] = (int)$row['organization_type'];
            $userData['phone'] = $row['phone'];
            $userData['email'] = $row['email'];

            $token = array();
            $token['id'] = (int)$row['id'];
            $userData['login_token'] = JWT::encode($token, SECRET_KEY);

            $response['member_data'] = $userData;
            //$_SESSION['member_data'] = $userData;
        }
        $result->close();
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SQL_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
        return;
    }
}

function doLogoutMember($memberId)
{
    global $response;

    //session_destroy();
    $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
    $response[KEY_ERROR_MESSAGE] = 'ออกจากระบบสำเร็จ';
    $response[KEY_ERROR_MESSAGE_MORE] = '';
}

function doDeleteMember($memberId)
{
    global $db, $response;

    if (!isset($_SESSION[KEY_SESSION_USER_ID]) || $_SESSION[KEY_SESSION_USER_ROLE] != 'super_admin') {
        $response[KEY_ERROR_CODE] = ERROR_CODE_PERMISSION_DENIED;
        $response[KEY_ERROR_MESSAGE] = 'คุณไม่มีสิทธิสำหรับการดำเนินการนี้';
        $response[KEY_ERROR_MESSAGE_MORE] = "";
        return;
    }

    $sql = "DELETE FROM member WHERE id = $memberId";
    if ($result = $db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'ลบสมาชิกสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SQL_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการลบสมาชิก';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doRegisterCourseIndividual($courseId, $title, $firstName, $lastName, $age,
                                    $jobPosition, $organizationName, $organizationType, $phone, $email)
{
    global $db, $response;

    $sql = "INSERT INTO course_registration (course_id) VALUES ($courseId)";
    if ($db->query($sql)) {
        $CourseRegId = $db->insert_id;
        $sql = "INSERT INTO course_trainee (course_registration_id, title, first_name, last_name, age, "
            . " job_position, organization_name, organization_type, phone, email) "
            . " VALUES ($CourseRegId, '$title', '$firstName', '$lastName', $age, "
            . " '$jobPosition', '$organizationName', $organizationType, '$phone', '$email')";
        if ($db->query($sql)) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = 'ลงทะเบียนสำเร็จ';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
        } else {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SQL_ERROR;
            $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
            $errMessage = $db->error;
            $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
            return;
        }
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SQL_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
        return;
    }
}

function doRegisterCourseGroup($params)
{
    global $db, $response;

    /*$firstName = $params['trainee_list'][1][first_name];*/
    $courseId = $params['course_id'];
    $traineeList = $params['trainee_list'];

    /*$response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
    $response[KEY_ERROR_MESSAGE] = "$traineeList";
    $response[KEY_ERROR_MESSAGE_MORE] = '';*/

    $sql = "INSERT INTO course_registration (course_id) VALUES ($courseId)";
    if ($db->query($sql)) {
        $CourseRegId = $db->insert_id;

        /*
        INSERT INTO tbl_name
            (a,b,c)
        VALUES
            (1,2,3),
            (4,5,6),
            (7,8,9);
        */

        $sql = "INSERT INTO course_trainee (course_registration_id, title, first_name, last_name, age, "
            . " job_position, organization_name, organization_type, phone, email) VALUES ";

        $traineeCount = sizeof($traineeList);
        $i = 0;
        foreach ($traineeList as $trainee) {
            $i++;
            $title = $trainee['title'];
            $firstName = $trainee['first_name'];
            $lastName = $trainee['last_name'];
            $age = $trainee['age'];
            $jobPosition = $trainee['job_position'];
            $organizationName = $trainee['organization_name'];
            $organizationType = $trainee['organization_type'];
            $phone = $trainee['phone'];
            $email = $trainee['email'];

            $sql .= "($CourseRegId, '$title', '$firstName', '$lastName', $age, "
                . " '$jobPosition', '$organizationName', $organizationType, '$phone', '$email')";
            if ($i < $traineeCount) {
                $sql .= ',';
            }
        }

        if ($db->query($sql)) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = 'ลงทะเบียนสำเร็จ';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
        } else {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SQL_ERROR;
            $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
            $errMessage = $db->error;
            $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
            return;
        }
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SQL_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
        return;
    }
}

function doFetchNameTitles()
{
    global $db, $response;

    $sql = "SELECT * FROM name_title";
    if ($result = $db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'อ่านข้อมูลสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
        $response[KEY_DATA_LIST] = array();

        while ($row = $result->fetch_assoc()) {
            $nameTitle = array();
            $nameTitle['id'] = (int)$row['id'];
            $nameTitle['title'] = $row['title'];
            $nameTitle['created_at'] = $row['created_at'];
            array_push($response[KEY_DATA_LIST], $nameTitle);
        }
        $result->close();
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SQL_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
        return;
    }
}

function doLoginUser($username, $password)
{
    global $db, $response;

    $sql = "SELECT * FROM user WHERE username = '$username' AND LOWER(password) = LOWER(MD5('$password'))";
    if ($result = $db->query($sql)) {
        if ($result->num_rows > 0) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = 'เข้าสู่ระบบสำเร็จ';
            $response[KEY_ERROR_MESSAGE_MORE] = '';

            $row = $result->fetch_assoc();
            $user = array();
            $user['id'] = (int)$row['id'];
            $user['username'] = $row['username'];
            $user['first_name'] = $row['first_name'];
            $user['last_name'] = $row['last_name'];
            $user['email'] = $row['email'];
            $user['role'] = $row['role'];
            $response['user'] = $user;

            createSession($user);
        } else {
            $response[KEY_ERROR_CODE] = ERROR_CODE_LOGIN_FAILED;
            $response[KEY_ERROR_MESSAGE] = 'ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง';
            $response[KEY_ERROR_MESSAGE_MORE] = 'Username: $username, Password: $password';
        }
        $result->close();
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SQL_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function createSession($user)
{
    $_SESSION[KEY_SESSION_USER_ID] = $user['id'];
    $_SESSION[KEY_SESSION_USER_USERNAME] = $user['username'];
    $_SESSION[KEY_SESSION_USER_FIRST_NAME] = $user['first_name'];
    $_SESSION[KEY_SESSION_USER_LAST_NAME] = $user['last_name'];
    $_SESSION[KEY_SESSION_USER_EMAIL] = $user['email'];
    $_SESSION[KEY_SESSION_USER_ROLE] = $user['role'];
}

function doLogoutUser()
{
    global $response;

    if (session_destroy()) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'ออกจากระบบสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_LOGOUT_FAILED;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการออกจากระบบ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
    }
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

function createRandomString($length)
{
    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $charactersLength = strlen($characters);
    $randomString = '';
    for ($i = 0; $i < $length; $i++) {
        $randomString .= $characters[rand(0, $charactersLength - 1)];
    }
    return $randomString;
}

/*function createRandomString($length)
{
    $token = "";
    $codeAlphabet = "abcdefghijklmnopqrstuvwxyz";
    $codeAlphabet .= "0123456789";
    $max = strlen($codeAlphabet); // edited

    for ($i = 0; $i < $length; $i++) {
        $token .= $codeAlphabet[crypto_rand_secure(0, $max - 1)];
    }
    return $token;
}*/

?>