package util;

import java.io.IOException;
import java.util.List;

public class IdGenerator {

    public static int genererProchainId(String nomFichier) throws IOException {
        List<String> lignes = FileUtil.lireFichier(nomFichier);
        
        if (lignes.isEmpty()) {
            return 1;
        }
        
        int maxId = 0;
        for (String ligne : lignes) {
            if (ligne.trim().isEmpty()) {
                continue;
            }
            
            String[] tab = ligne.split(",");
            if (tab.length > 0) {
                try {
                    int id = Integer.parseInt(tab[0]);
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                   
                }
            }
        }
        
        return maxId + 1;
    }
}