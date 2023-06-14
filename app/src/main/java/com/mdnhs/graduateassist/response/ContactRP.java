package com.mdnhs.graduateassist.response;

import com.mdnhs.graduateassist.item.ContactList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ContactRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;

    @SerializedName("contact_list")
    private List<ContactList> contactLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public List<ContactList> getContactLists() {
        return contactLists;
    }
}
