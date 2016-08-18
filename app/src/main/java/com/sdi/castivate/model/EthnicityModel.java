package com.sdi.castivate.model;

/**
 * Created by Balachandar on 22-Apr-15.
 */


/** Holds Ethnicity data. */
public class EthnicityModel {
    public String name = "" ;
    public boolean checked = false ;

    public EthnicityModel( String name, boolean checked ) {
        this.name = name ;
        this.checked = checked ;
    }
    public String getName() {
        return name;
    }
    public String setName(String name) {
        return this.name = name;
    }
    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public String toString() {
        return name ;
    }
    public void toggleChecked() {
        checked = !checked ;
    }
}