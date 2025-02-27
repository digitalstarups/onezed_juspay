package com.onezed.GlobalVariable;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class GlobalVariable {
    public static int globalProfileId;
    public static String orderId;
    public static String Rs;
    public static String validity;
    public static String PayId;
    public static String RechargeNumber;
    public static String OperatorCode;
    public static ArrayList<HashMap> itemArrayList;
    public static String inhouseComRegSelectedType;
    public static String inhouseComRegSelectedCategory;
    public static String inhouseComRegSelectedTab;
    public static String inhouseCategory;
    public static String bbpsCustomername;
    public static String LocalNotificationText="Recharge";
    public static String selectedBillerName="";
    public static String selectedBillerCategory="";
    public static String selectedBillerId="";
    public static String selectedBillerType="";
    public static JSONArray tags = new JSONArray();
    public static boolean isSelected;
    public static double juspayAmount=0.0;
}
