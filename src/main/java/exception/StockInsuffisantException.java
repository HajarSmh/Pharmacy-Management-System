package exception;

public class StockInsuffisantException extends Exception {
    
    public StockInsuffisantException(String message) {
        super(message);
    }
    
    public StockInsuffisantException(String nomMedicament, int quantiteDemandee, int quantiteDisponible) {
        super("Stock insuffisant pour " + nomMedicament + ". Demandé : " 
              + quantiteDemandee + ", Disponible : " + quantiteDisponible);
    }
}