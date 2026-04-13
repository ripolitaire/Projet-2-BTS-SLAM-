package app.service;

import java.util.ArrayList;
import java.util.List;

import app.model.Formateur;

public class FormateurService {

    // Recuperer tous les formateurs (mock)
    public List<Formateur> getAllFormateurs() throws Exception {
        List<Formateur> formateurs = new ArrayList<>();
        Formateur f1 = new Formateur();
        f1.setId_formateur(1);
        f1.setPrenom("Pierre");
        f1.setNom("Durand");
        f1.setEmail("pierre@local");
        f1.setMot_de_passe("pass");
        f1.setRole("formateur");
        f1.setTelephone("0123456789");
        f1.setInfo_complementaires("Expert Java");
        formateurs.add(f1);

        Formateur f2 = new Formateur();
        f2.setId_formateur(2);
        f2.setPrenom("Sophie");
        f2.setNom("Leroy");
        f2.setEmail("sophie@local");
        f2.setMot_de_passe("pass");
        f2.setRole("formateur");
        f2.setTelephone("0987654321");
        f2.setInfo_complementaires("Specialiste Spring");
        formateurs.add(f2);

        return formateurs;
    }

    // Ajouter un formateur (mock)
    public Formateur ajouterFormateur(Formateur formateur) throws Exception {
        // Simulation
        formateur.setId_formateur(999);
        return formateur;
    }
}