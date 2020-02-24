package com.example.harish.findmyjobcapstone;


class User {
    private  String name,mobileno,email,username,password;

    public User(String name, String mobileno, String email, String username, String password) {
        this.name = name;
        this.mobileno = mobileno;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public String getMobileno() {
        return mobileno;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
