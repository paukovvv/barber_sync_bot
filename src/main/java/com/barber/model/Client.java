package com.barber.model;

import java.time.LocalDateTime;

public class Client {
    private String name;
    private String email;
    private String phone;
    private double price;
    private LocalDateTime createdAt;
    private LocalDateTime appointmentTime;

    public Client() {
        this.price = 35.0;
        this.createdAt = LocalDateTime.now();
    }

    public Client(String name, String email, String phone, LocalDateTime appointmentTime) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.price = 35.0;
        this.createdAt = LocalDateTime.now();
        this.appointmentTime = appointmentTime;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    public double getPrice(){
        return price;
    }
    public void setPrice(double price){
        this.price=price;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public LocalDateTime getAppointmentTime(){
        return appointmentTime;
    }
    public void setAppointmentTime(LocalDateTime appointmentTime){
        this.appointmentTime=appointmentTime;
    }
    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", price=" + price +
                ", createdAt=" + createdAt +
                ", appointmentTime=" + appointmentTime +
                '}';
    }


}

