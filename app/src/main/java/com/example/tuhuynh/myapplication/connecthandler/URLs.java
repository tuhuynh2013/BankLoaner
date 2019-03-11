package com.example.tuhuynh.myapplication.connecthandler;


public class URLs {

    private static final String ROOT_URL = "http://10.0.2.2/loginphp/Api.php?apicall=";
    public static final String URL_CUSTOMER_REGISTER = ROOT_URL + "customerregister";
    public static final String URL_AGENT_REGISTER = ROOT_URL + "agentregister";
    public static final String URL_GOOGLE_REGISTER = ROOT_URL + "googleregister";
    public static final String URL_GET_BANKS = ROOT_URL + "getbanks";
    public static final String URL_GET_SIMPLE_BANKS = ROOT_URL + "getsimplebanks";
    public static final String URL_GET_USER_PROFILE = ROOT_URL + "getuserprofile";
    public static final String URL_UPDATE_USER_PROFILE = ROOT_URL + "updateuserprofile";
    public static final String URL_UPDATE_CUSTOMER_PROFILE = ROOT_URL + "updatecustomerprofile";
    public static final String URL_UPDATE_AGENT_PROFILE = ROOT_URL + "updateagentprofile";
    public static final String URL_SUBMIT_APPLICATION = ROOT_URL + "submitapplication";
    public static final String URL_GET_CUSTOMER_APPLICATIONS = ROOT_URL + "getcustomerapplications";
    public static final String URL_GET_USER_STATUS = ROOT_URL + "getuserstatus";

    /*Agent activities*/
    public static final String URL_GET_AGENT_APPLICATIONS = ROOT_URL + "getagentapplications";
    public static final String URL_GET_SHARING_APPLICATIONS = ROOT_URL + "getsharingapplications";
    public static final String URL_UPDATE_APPLICATION_STATUS = ROOT_URL + "updateapplicationstatus";
    public static final String URL_ASSIGN_AGENT = ROOT_URL + "assignagent";
    public static final String URL_SEND_NOTIFICATION = ROOT_URL + "sendnotification";

    /*Admin activity*/
    public static final String URL_GET_AGENTS = ROOT_URL + "getagents";
    public static final String URL_UPDATE_USER_STATUS = ROOT_URL + "updateuserstatus";

    public static final String URL_ASSIGN_TOKEN = ROOT_URL + "assigntoken";
    public static final String URL_GET_FCM_TOKENS = ROOT_URL + "gettokens";

}
