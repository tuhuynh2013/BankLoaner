package com.example.tuhuynh.myapplication.connecthandler;

public class URLs {

    private static final String ROOT_URL = "http://10.0.2.2/loginphp/Api.php?apicall=";
    public static final String URL_REGISTER = ROOT_URL + "signup";
    public static final String URL_LOGIN = ROOT_URL + "login";
    public static final String URL_GET_BANKS = ROOT_URL + "getbanks";
    public static final String URL_GET_CUSTOMER_PROFILE = ROOT_URL + "customerprofile";
    public static final String URL_UPDATE_USER_PROFILE = ROOT_URL + "updateuserprofile";
    public static final String URL_UPDATE_CUSTOMER_PROFILE = ROOT_URL + "updatecustomerprofile";
    public static final String URL_CHANGE_PASSWORD = ROOT_URL + "changepassword";
    public static final String URL_SUBMIT_APPLICATION = ROOT_URL + "submitapplication";
    public static final String URL_GET_CUSTOMER_APPLICATIONS = ROOT_URL + "getcustomerapplications";

    /*Agent activities*/
    public static final String URL_GET_ASSIGNED_APPLICATIONS = ROOT_URL + "getassignedapplications";
    public static final String URL_UPDATE_APPLICATION_STATUS = ROOT_URL + "updateapplicationstatus";

}
