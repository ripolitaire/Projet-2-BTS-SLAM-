package app.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InscriptionService {
    private static final Map<String, Set<Integer>> inscriptionsParEmail = new HashMap<>();

    static {
        // jeu d'exemple : l'apprenants@example.com est inscrit aux sessions 1 et 2.
        inscriptionsParEmail.put("apprenants@example.com", Set.of(1, 2));
        inscriptionsParEmail.put("demo@example.com", Set.of(2, 3));
    }

    public Set<Integer> getSessionIdsPourApprenant(String email) {
        if (email == null || email.isBlank()) {
            return Set.of();
        }

        return inscriptionsParEmail.getOrDefault(email.trim().toLowerCase(), Set.of());
    }
}
