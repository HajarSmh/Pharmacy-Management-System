package exception;

public class PrescriptionRequiseException extends Exception {
    
    public PrescriptionRequiseException(String nomMedicament) {
        super("Le médicament " + nomMedicament + " nécessite une prescription médicale.");
    }
}