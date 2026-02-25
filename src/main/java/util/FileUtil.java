package util;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileUtil {
    
    private static final Lock verrou = new ReentrantLock();
    private static final String DATA_DIR = "data/";
    

    public static List<String> lireFichier(String nomFichier) throws IOException {
        verrou.lock();
        BufferedReader br = null;
        
        try {
            br = new BufferedReader(new FileReader(DATA_DIR + nomFichier));
            List<String> lignes = new ArrayList<>();
            String ligne;
            
            while ((ligne = br.readLine()) != null) {
                lignes.add(ligne);
            }
            
            return lignes;
            
        } catch (FileNotFoundException e) {

            return new ArrayList<>();
            
        } finally {
            if (br != null) {
                br.close();
            }
            verrou.unlock();
        }
    }
    
    public static void ecrireFichier(String nomFichier, List<String> lignes) throws IOException {
        verrou.lock();
        BufferedWriter bw = null;
        
        try {

            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdir();
            }
            
            File fichier = new File(DATA_DIR + nomFichier);
            bw = new BufferedWriter(new FileWriter(fichier, false)); 
            
            for (String ligne : lignes) {
                bw.write(ligne);
                bw.newLine();
            }
            
        } finally {
            if (bw != null) {
                bw.close();
            }
            verrou.unlock();
        }
    }

    public static void ajouterLigne(String nomFichier, String ligne) throws IOException {
        verrou.lock();
        BufferedWriter bw = null;
        
        try {
       
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdir();
            }
            
            File fichier = new File(DATA_DIR + nomFichier);
            bw = new BufferedWriter(new FileWriter(fichier, true));
            bw.write(ligne);
            bw.newLine();
            
        } finally {
            if (bw != null) {
                bw.close();
            }
            verrou.unlock();
        }
    }
    public static boolean fichierExiste(String nomFichier) {
        File fichier = new File(DATA_DIR + nomFichier);
        return fichier.exists();
    }
}