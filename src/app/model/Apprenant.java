package app.model;

public class Apprenant {
    private int id_apprenant;
    private String pseudo;
    private String prenom;
    private String nom;
    private String email;
    private String telephone;
    private String mot_de_passe;
    private String role;

    public Apprenant() {
    }

    public int getId_apprenant() {
        return id_apprenant;
    }

    public void setId_apprenant(int id_apprenant) {
        this.id_apprenant = id_apprenant;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMot_de_passe() {
        return mot_de_passe;
    }

    public void setMot_de_passe(String mot_de_passe) {
        this.mot_de_passe = mot_de_passe;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDisplayPrenom() {
        if (prenom != null && !prenom.isBlank()) {
            return prenom;
        }
        if (pseudo != null && !pseudo.isBlank()) {
            return pseudo;
        }
        return "-";
    }

    public String getDisplayNom() {
        if (nom != null && !nom.isBlank()) {
            return nom;
        }
        return "-";
    }
}
