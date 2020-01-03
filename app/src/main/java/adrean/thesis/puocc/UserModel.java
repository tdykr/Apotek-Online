package adrean.thesis.puocc;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
    private String userId;
    private String userPassword;
    private String userName;
    private String userEmail;
    private String userAddress;
    private String userPhone;
    private String userRole;
    private String userResponse;
    private String userMessage;

    public UserModel(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserResponse() {
        return userResponse;
    }

    public void setUserResponse(String userResponse) {
        this.userResponse = userResponse;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public UserModel(String userId, String userPassword, String userName, String userEmail, String userAddress, String userPhone, String userRole, String userResponse, String userMessage) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userAddress = userAddress;
        this.userPhone = userPhone;
        this.userRole = userRole;
        this.userResponse = userResponse;
        this.userMessage = userMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.userPassword);
        dest.writeString(this.userName);
        dest.writeString(this.userEmail);
        dest.writeString(this.userAddress);
        dest.writeString(this.userPhone);
        dest.writeString(this.userRole);
        dest.writeString(this.userResponse);
        dest.writeString(this.userMessage);
    }

    protected UserModel(Parcel in) {
        this.userId = in.readString();
        this.userPassword = in.readString();
        this.userName = in.readString();
        this.userEmail = in.readString();
        this.userAddress = in.readString();
        this.userPhone = in.readString();
        this.userRole = in.readString();
        this.userResponse = in.readString();
        this.userMessage = in.readString();
    }

    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel source) {
            return new UserModel(source);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
}