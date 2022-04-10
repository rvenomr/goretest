package org.name.data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.LinkedHashMap;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String email;
    private String name;
    private String gender;
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User() {
    }

    public User(String email, String name, String gender, String status) {
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.status = status;
    }

    public User(LinkedHashMap<String,String> map) {
        this.name = map.get("name");
        this.gender = map.get("gender");
        this.email = map.get("email");
        this.status = map.get("status");
    }

}
