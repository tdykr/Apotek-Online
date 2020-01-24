package adrean.thesis.puocc;

public class phpConf {
    //192.168.43.53 hp
    //192.168.1.5 wifi rumah
    //10.107.237.27 sbucks benhil
    //192.168.10.114 jatiluhur 2
    //192.168.43.93 hp icha
    // 10.107.134.194 sbux benhil

    private final static String ip = "192.168.43.53";

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
    public final static String URL_GET_LIST_HISTORY_SHOPPING_BY_TRXID = "http://"+ ip +"/apotek/getListShoppingCartByTrxId.php";
    public final static String URL_GET_LIST_ALL_TRANSACTION_APOTEKER = "http://"+ ip +"/apotek/getListAllTransactionApoteker.php";
    public final static String URL_UPDATE_CART_ORDER_STATUS_CONFIRMED = "http://"+ ip +"/apotek/updateCartOrderStatusConfirmed.php";
    public final static String URL_ADD_TRANSACTION = "http://"+ ip +"/apotek/addTransactionId.php";
    public final static String URL_GET_LIST_TRANSACTION = "http://"+ ip +"/apotek/getListTransaction.php";
    public final static String URL_UPLOAD_BILL_TRANSACTION = "http://"+ ip +"/apotek/uploadBillTrxImg.php";
    public final static String URL_UPDATE_PASSWORD = "http://"+ ip +"/apotek/updatePassword.php";
    public final static String URL_GET_LIST_STATUS = "http://"+ ip +"/apotek/getListStatus.php";
    public final static String URL_ADD_NEW_APOTEKER = "http://"+ ip +"/apotek/addApotekerUser.php";
    public final static String URL_UPDATE_QT_AFTER_STATUS_PAID = "http://"+ ip +"/apotek/updateQuantityAfterStatusPaid.php";
    public final static String URL_GET_CHART_DATA = "http://"+ ip +"/apotek/chart.php";
    public final static String URL_YEAR_LIST= "http://"+ ip +"/apotek/getTrxYearList.php";
    public final static String URL_SUMMARY= "http://"+ ip +"/apotek/summary.php";
    public final static String URL_GET_DETAIL_TRX_APOTEKER = "http://"+ ip +"/apotek/getDetailTransactionApoteker.php";
    public final static String URL_ADD_CATEGORY = "http://"+ ip +"/apotek/addCategory.php";
    public final static String URL_ADD_PRESCRIPTION= "http://"+ ip +"/apotek/addTrxPrescription.php";
    public final static String URL_END_TRX_STATUS = "http://"+ ip +"/apotek/endTransactionStatus.php";
    public final static String URL_QR_ADD_CART = "http://"+ ip +"/apotek/addQrScanTrx.php";
    public final static String URL_SELLING_REPORT_MEDICINE = "http://"+ ip +"/apotek/sellingReportMedicine.php";
    public final static String URL_SELLING_REPORT_PHARMACIST = "http://"+ ip +"/apotek/sellingReportPharmacist.php";

}
