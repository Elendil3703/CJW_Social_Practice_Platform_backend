package org.example.practice_platform_backend.entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private int user_id;
    private String user_name;
    private String password;
    private String phone_number;
    private String name;
    private String user_category;
    private String avatar_path;
    private String gender;

    public User(){
    }

}
