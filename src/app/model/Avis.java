package app.model;

public class Avis {
    private int id_avis;
    private int id_session;
    private int id_apprenant;
    private String commentaire;
    private int note; // de 1 à 5

    public Avis() {
    }

    public Avis(int id_avis, int id_session, int id_apprenant, String commentaire, int note) {
        this.id_avis = id_avis;
        this.id_session = id_session;
        this.id_apprenant = id_apprenant;
        this.commentaire = commentaire;
        this.note = note;
    }

    // Getters et setters
    public int getId_avis() {
        return id_avis;
    }

    public void setId_avis(int id_avis) {
        this.id_avis = id_avis;
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

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }
}