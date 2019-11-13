package adrean.thesis.puocc;

public class phpConf {
    //192.168.43.106 hp
    //192.168.1.4 wifi rumah
    private final static String ip = "192.168.1.4";

    public final static String URL_GET_ALL_MEDICINE_LIST = "http://"+ ip +"/apotek/getListMedicine.php";
    public final static String URL_GET_MEDICINE_DETAIL = "http://"+ ip +"/apotek/getMedicineDetail.php";
    public final static String URL_INPUT_MED = "http://"+ ip +"/apotek/addMedicineDetail.php";
    public final static String URL_UPDATE_QT = "http://"+ ip +"/apotek/updateMedicineQt.php";
    public final static String URL_GET_MEDICINE_CATEGORY = "http://"+ ip +"/apotek/getListMedicineCategory.php";
    public final static String URL_LOGIN = "http://"+ ip +"/apotek/login.php";

}
