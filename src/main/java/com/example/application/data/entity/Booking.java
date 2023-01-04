package com.example.application.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.validation.constraints.Email;

@Entity
public class Booking extends AbstractEntity {

    private String namaPenyewa;
    private String noHp;
    @Email
    private String email;
    private LocalDate bookingUntuk;
    private String jam;
    private String paket;

    public String getNamaPenyewa() {
        return namaPenyewa;
    }
    public void setNamaPenyewa(String namaPenyewa) {
        this.namaPenyewa = namaPenyewa;
    }
    public String getNoHp() {
        return noHp;
    }
    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public LocalDate getBookingUntuk() {
        return bookingUntuk;
    }
    public void setBookingUntuk(LocalDate bookingUntuk) {
        this.bookingUntuk = bookingUntuk;
    }
    public String getJam() {
        return jam;
    }
    public void setJam(String jam) {
        this.jam = jam;
    }
    public String getPaket() {
        return paket;
    }
    public void setPaket(String paket) {
        this.paket = paket;
    }

}
