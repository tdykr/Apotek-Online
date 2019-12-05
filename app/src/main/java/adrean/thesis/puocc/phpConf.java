package adrean.thesis.puocc;

public class phpConf {
    //192.168.43.53 hp
    //192.168.1.7 wifi rumah
    //10.107.139.86 sbucks benhil
    private final static String ip = "192.168.1.5";

    public final static String URL_GET_ALL_MEDICINE_LIST = "http://"+ ip +"/apotek/getListMedicine.php";
    public final static String URL_GET_MEDICINE_DETAIL = "http://"+ ip +"/apotek/getMedicineDetail.php";
    public final static String URL_INPUT_MED = "http://"+ ip +"/apotek/addMedicineDetail.php";
    public final static String URL_UPDATE_QT = "http://"+ ip +"/apotek/updateMedicineQt.php";
    public final static String URL_GET_MEDICINE_CATEGORY = "http://"+ ip +"/apotek/getListMedicineCategory.php";
    public final static String URL_LOGIN = "http://"+ ip +"/apotek/login.php";
    public final static String URL_REGISTER = "http://"+ ip +"/apotek/registerUser.php";
    public final static String URL_GET_LIST_MEDICINE_BY_CATEGORY = "http://"+ ip +"/apotek/getMedicineDetailByCategory.php";
    public final static String URL_ADD_TO_CART = "http://"+ ip +"/apotek/addNewCart.php";
    public final static String URL_GET_CART = "http://"+ ip +"/apotek/cartList.php";
    public final static String URL_UPDATE_CART_ORDER = "http://"+ ip +"/apotek/updateCartOrderStatus.php";

}
