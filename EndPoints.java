package com.example.equityvalidation;
//defined to be used to upload images to server
public class EndPoints {
    private static final String ROOT_URL = "http://192.168.43.252/EquityAgent/api.php?apicall=";
    public static final String UPLOAD_URL = ROOT_URL + "uploadpic";
    public static final String UPLOAD_FRONTIDURL = ROOT_URL + "uploadpicfid";
    public static final String UPLOAD_BACKIDURL = ROOT_URL + "uploadpicbid";
    public static final String UPLOAD_SIGNURL = ROOT_URL + "uploadpicsign";
}
