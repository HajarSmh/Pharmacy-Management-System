package exception;

import java.time.LocalDate;

public class MedicamentExpireException extends Exception {
    
    public MedicamentExpireException(String message) {
        super(message);
    }
    
    public MedicamentExpireException(String nomMedicament, LocalDate dateExpiration) {
        super("Le médicament " + nomMedicament + " a expiré le " + dateExpiration);
    }
}