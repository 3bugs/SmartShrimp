package th.ac.dusit.dbizcom.smartshrimp.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    public final int id;
    @SerializedName("username")
    public final String username;
    @SerializedName("email")
    public final String email;
    @SerializedName("first_name")
    public final String firstName;
    @SerializedName("last_name")
    public final String lastName;
    @SerializedName("address")
    public final String address;
    @SerializedName("created_at")
    public final String createdAt;

    public User(int id, String username, String email, String firstName, String lastName, String address, String createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return this.username;
    }
}
