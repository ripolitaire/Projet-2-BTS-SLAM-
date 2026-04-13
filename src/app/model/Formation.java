package app.model;

public class Formation {
    private int id_formation;
    private String nom;
    private String description;
    private String duree; // en heures ou jours
    private String niveau; // debutant, intermediaire, avance

    public Formation() {
    }

    public Formation(int id_formation, String nom, String description, String duree, String niveau) {
        this.id_formation = id_formation;
        this.nom = nom;
        this.description = description;
        this.duree = duree;
        this.niveau = niveau;
    }

    // Getters et setters
    public int getId_formation() {
        return id_formation;
    }

    public void setId_formation(int id_formation) {
        this.id_formation = id_formation;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    @Override
    public String toString() {
        return nom; // pour afficher dans les listes
    }
}