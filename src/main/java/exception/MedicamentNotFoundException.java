package exception;


public class MedicamentNotFoundException extends Exception {
    
    public MedicamentNotFoundException(String message) {
        super(message);
    }
    
    public MedicamentNotFoundException(int idMedicament) {
        super("Le médicament avec l'ID " + idMedicament + " n'a pas été trouvé.");
    }
}