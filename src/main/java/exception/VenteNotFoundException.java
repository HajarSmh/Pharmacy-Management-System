package exception;

public class VenteNotFoundException extends Exception {
    
    public VenteNotFoundException(String message) {
        super(message);
    }
    
    public VenteNotFoundException(int idVente) {
        super("La vente avec l'ID " + idVente + " n'a pas été trouvée.");
    }
}