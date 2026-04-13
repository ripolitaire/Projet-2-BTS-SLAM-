package app.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Session {
    private int id;
    private String formation;
    private String formateur;
    private LocalDate date;
    private String salle;

    public Session() {
    }

    public Session(int id, String formation, String formateur, LocalDate date, String salle) {
        this.id = id;
        this.formation = formation;
        this.formateur = formateur;
        this.date = date;
        this.salle = salle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFormation() {
        return formation;
    }

    public void setFormation(String formation) {
        this.formation = formation;
    }

    public String getFormateur() {
        return formateur;
    }

    public void setFormateur(String formateur) {
        this.formateur = formateur;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSalle() {
        return salle;
    }

    public void setSalle(String salle) {
        this.salle = salle;
    }

    private boolean present;

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public boolean estPasse() {
        return date.isBefore(LocalDate.now());
    }

    public String getDateFormattee() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
}
