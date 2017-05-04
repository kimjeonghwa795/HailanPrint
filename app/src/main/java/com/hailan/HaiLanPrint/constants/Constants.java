package com.hailan.HaiLanPrint.constants;

/**
 * Created by yoghourt on 5/5/16.
 */
public final class Constants {

//    public static final String HOST = "http://hailan.082818.com/";
    public static final String HOST = "http://www.haiprint.com/";

//    public static final String FILE_HOST = "http://hailan.082818.com/";
    public static final String FILE_HOST = "http://www.haiprint.com/";

    // APP在sdcar的目录
    public static final String APP_PATH = "/mdground_hailan_print";
    // Preference文件名
    public static String PREFERENCE = "hailan_print";
    // glide图片缓存文件夹
    public static String GLIDE_DISK_CACHE_FILE_NAME = "Glide_cache_image";

    // 数据库名字
    public static final String DATABASE_NAME = "mdground";
    //照片储存文件夹
    public static final String PHOTO_FILE = "/baoprint";

    public static final String KEY_IS_FIRST_LAUNCH = "key_is_first_launch";

    // 支付宝
    // 商户PID
    public static final String PARTNER = "2088001485268815";
    // 商户收款账号
    public static final String SELLER = "2088001485268815";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAOpoaDusZHhV4hCmFNhjsiG398/6s1aZpQ5tbkrLcNLrJu511lpBqSS5t8tyqQv6npSpecumFrT/OPeDV5mRd1/JOnpnDrkmrcLrmanTxZXl9sVwQYCkXRhmRlj4Q41kQqWfqKnAep8OuGjKqs0LBEahaUzIRR7Z3atR7A4mBKx5AgMBAAECgYBnxJh/8iz36G0PGbaYpGmy65HUjBLf/1xGKsNscOsdM+QkJB4kDnUmQODQO44CM4wIG45DTN84eNNrHsWkwfSppad5agHxkz5ASjUZy2VnWuw4F1NVn6yoCCftgZ/E1nIUjrAFaUS+f5q0k9zVnQew8Efnn9FsMdGZk9gVJ8MeQQJBAPo+ZZ8d8DPNFw2V6hffk/nN7GWdjbO8LqKgrt6duH6sPQSj5zfyXWJIAs6bMI2rrvQYtxTEGazFeUK+xvmujN0CQQDvzMH/2syk8jerbG+kwGStVj8xAxAISrk5tjQXxA7wU7Vd860G3zowFN/6tRCPlRt4t3kCGS4XMgCG0JUkkaZNAkAoZ+bUuGrOknEEeo5f6HPmQ//FsJjuypXlIHjb7S+bFIiNtUFiYTn+4o3BeoFufqgzNcJqEffrU79urYtHSY3pAkBqUN2yEc+pUbpcxja5VJNORm69zB3Pj1GPxLhT0RjOdE502RRdd5rUtmyXOq+c+P61BqMfywokPob9PnzhObIBAkBEQVyk22guhYNqHu4AVnVaOeMWv2QUjaJ143ZdgafGqlL/e0gVVdxOGu9YVMDcao3PRRhuVAIuSgXMFP6nxgJR";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDqaGg7rGR4VeIQphTYY7Iht/fP+rNWmaUObW5Ky3DS6ybuddZaQakkubfLcqkL+p6UqXnLpha0/zj3g1eZkXdfyTp6Zw65Jq3C65mp08WV5fbFcEGApF0YZkZY+EONZEKln6ipwHqfDrhoyqrNCwRGoWlMyEUe2d2rUewOJgSseQIDAQAB";

    // application
    public static final String KEY_ALREADY_LOGIN_USER = "key_already_login_user";
    public static final String KEY_AUTO_LOGIN = "key_auto_login";
    public static final String KEY_ALREADY_CHOOSED_PRODUCT_TYPE = "key_already_choosed_product_type";
    public static final String KEY_ALREADY_CHOOSED_MEASUREMENT = "key_already_choosed_measurement";
    public static final String KEY_ALREADY_CHOOSED_TEMPLATE = "key_already_choosed_template";
    public static final String KEY_PHOTO_TYPE_EXPLAIN_ARRAYLIST = "key_photo_type_explain_arraylist";

    public static final String KEY_NEW_USER = "key_new_user";
    public static final String KEY_PHONE = "key_phone";
    public static final String KEY_CLOUD_IMAGE = "key_cloud_image";
    public static final String KEY_PREVIEW_IMAGE_LIST = "key_preview_image_list";
    public static final String KEY_PREVIEW_IMAGE = "key_preview_image";
    public static final String KEY_PREVIEW_IMAGE_POSITION = "key_preview_image_position";
    public static final String KEY_SELECT_IMAGE = "key_select_image";
    public static final String KEY_ALBUM = "key_album";
    public static final String KEY_DELIVERY_ADDRESS = "key_delivery_address";
    public static final String KEY_MAX_IMAGE_NUM = "key_max_image_num";
    public static final String KEY_ORDER_WORK = "key_order_work";
    public static final String KEY_ORDER_WORK_LIST = "key_order_work_list";
    public static final String KEY_ORDER_INFO = "key_order_info";
    public static final String KEY_ORDER_URL = "key_order_url";
    public static final String KEY_WORK_INFO = "key_work_info";
    public static final String KEY_MESSAGE = "key_message";
    public static final String KEY_SELECTED_COUPON = "key_selected_coupon";
    public static final String KEY_ORDER_AMOUNT_FEE = "key_order_amount_fee";
    public static final String KEY_COUPON_LIST = "key_coupon_list";
    public static final String KEY_FROM_PAYMENT_SUCCESS = "key_from_payment_success";
    public static final String KEY_WORKS_DETAILS = "key_from_works_details";
    public static final String KEY_ONLY_WIFI_UPLOAD = "key_only_wifi_upload";
    public static final String KEY_CHANGE_PASSWORD = "key_change_password";
    public static final String KEY_COVER_IMAGE_LIST = "key_cover_image_list";
    public static final String KEY_PUSH_MESSAGE = "key_push_message";//消息推送
    public static int MAX_DELIVERY_ADDRESS = 10; // 最多十个收货地址
    public static final String ACTION_PUSH = "action_push";
    public static final String KEY_INVITATIONCODE = "key_invitationcode";//登陆返回的邀请码
    public static final String KEY_PROVINCE = "key_province";//省
    public static final String KEY_CITY = "key_city";//市
    public static final String KEY_COUNTY = "key_county";//区

    public static int ITEM_LEFT_TO_LOAD_MORE = 3;

    public static int PRINT_PHOTO_MAX_SELECT_IMAGE_NUM = 100; // 冲印相片最多选择10张
    public static int PICTURE_FRAME_MAX_SELECT_IMAGE_NUM = 1; // 相框
    public static int MAGIC_CUP_MAX_SELECT_IMAGE_NUM = 1; // 魔术杯最多选择一张
    public static int PUZZLEL_MAX_SELECT_IMAGE_NUM = 1; // 拼图最多选择一张
    public static int PHONE_SHELL_MAX_SELECT_IMAGE_NUM = 1; // 手机壳
    public static int ENGRAVING_MAX_SELECT_IMAGE_NUM = 100; // 版画

}
