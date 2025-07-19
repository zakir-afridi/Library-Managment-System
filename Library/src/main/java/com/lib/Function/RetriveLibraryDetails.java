package com.lib.Function;

import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class RetriveLibraryDetails {
    Function fun =new Function();
    DatabaseFunction d= new DatabaseFunction();


    ConnectionTest c=new ConnectionTest();
    public  ImageView retriveLogo(){
       ImageView[] img =d.getImagesFromDatabase("select library_logo from libraryDetalis",1);
       ImageView imge=img[0];
       if (img!=null){
       return imge;}
       else
           return null;

    }
    public String retriveTilte(){
        ArrayList<String> names = new ArrayList<>();
        names=d.retrieveNames("select library_title from libraryDetails","library_title");
        String n=names.get(0);
        return n;
    }
}
