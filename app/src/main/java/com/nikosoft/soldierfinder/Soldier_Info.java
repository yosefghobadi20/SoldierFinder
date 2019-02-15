package com.nikosoft.soldierfinder;

/**
 * Created by Yosef on 24/03/2017.
 */
public class Soldier_Info {
    public int      ID;
    public String Name;
    public String Family;
    public String CurrentState;
    public String CurrentCity;
    public String CurrentPadegan;
    public String RequestState;
    public String RequestCity;
    public String RequestPadegan;
    public String RequestDate;
    public String Send_OutDate;
    public String Phone;
    public boolean CurrentStatus;
    public int Sub_Organ;
    public boolean MovmentStatus;
    public boolean Favorite;
    public int Rate;
    public int Organ;
    //public String Description;

    public Soldier_Info() {
    }

    public Soldier_Info(
            int iD,
            String name,
            String family,
            String currentState,
            String currentCity,
            String currentPadegan,
            String requestState,
            String requestCity,
            String requestPadegan,
            String requestDate,
            String send_OutDate,
            String phone,
            boolean currentStatus,
            int sub_organ,
            boolean movmentStatus,
            boolean favorite,
            int rate,
            int organ)
            //String description)
    {
        this.ID = iD;
        this.Name = name;
        this.Family = family;
        this.CurrentState = currentState;
        this.RequestState = requestState;
        this.CurrentCity = currentCity;
        this.CurrentPadegan = currentPadegan;
        this.RequestCity = requestCity;
        this.RequestPadegan = requestPadegan;
        this.RequestDate = requestDate;
        this.Send_OutDate = send_OutDate;
        this.Phone = phone;
        this.CurrentStatus = currentStatus;
        this.Sub_Organ = sub_organ;
        this.MovmentStatus = movmentStatus;
        this.Favorite = favorite;
        this.Rate = rate;
        this.Organ = organ;
//        this.Description = description;
    }
}



