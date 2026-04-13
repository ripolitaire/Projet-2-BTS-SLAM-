package app.model;

public class Presence {
    private int id_presence;
    private int id_session;
    private int id_apprenant;
    private boolean present; // true si present, false sinon

    public Presence() {
    }

    public Presence(int id_presence, int id_session, int id_apprenant, boolean present) {
        this.id_presence = id_presence;
        this.id_session = id_session;
        this.id_apprenant = id_apprenant;
        this.present = present;
    }

    // Getters et setters
    public int getId_presence() {
        return id_presence;
    }

    public void setId_presence(int id_presence) {
        this.id_presence = id_presence;
    }

    public int getId_session() {
        return id_session;
    }

    public void setId_session(int id_session) {
        this.id_session = id_session;
    }

    public int getId_apprenant() {
        return id_apprenant;
    }

    public void setId_apprenant(int id_apprenant) {
        this.id_apprenant = id_apprenant;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}