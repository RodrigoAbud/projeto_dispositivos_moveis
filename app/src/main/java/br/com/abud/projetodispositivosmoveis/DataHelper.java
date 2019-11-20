package br.com.abud.projetodispositivosmoveis;

import java.text.SimpleDateFormat;

public class DataHelper {

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public  static String format(java.util.Date date){
        return sdf.format(date);
    }
}
