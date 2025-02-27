package com.onezed;

import com.onezed.GlobalVariable.GlobalStrings;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ExeDec {

    String string;


    public ExeDec(String string){

        this.string=string;

    }

    public String getDec()
    {
        String decTxt="";
        Encr_Decr encr_decr1=new Encr_Decr();
        try {
            String key= GlobalStrings.URL_WEBGO+GlobalStrings.URL_WEBLNK;
             decTxt=encr_decr1.decrypt(string,key);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return decTxt;

    }




}
