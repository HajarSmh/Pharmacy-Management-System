package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vente {
	private int id;
	private LocalDateTime dateVente;
	private double montantTotal;
	private String statut;
	private int idClient;
	private int idPharmacien;
	private List<LigneVente> lignesVente;
	
	public Vente() {
	    this.lignesVente = new ArrayList<>();
	    this.statut = "EN_ATTENTE";
	    this.dateVente = LocalDateTime.now();
	}
	public Vente(int id, LocalDateTime dateVente, double montantTotal, 
            String statut, int idClient, int idPharmacien) {
   this.id = id;
   this.dateVente = dateVente;
   this.montantTotal = montantTotal;
   this.statut = statut;
   this.idClient = idClient;
   this.idPharmacien = idPharmacien;
   this.lignesVente = new ArrayList<>();
}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public LocalDateTime getDateVente() {
		return dateVente;
	}
	public void setDateVente(LocalDateTime dateVente) {
		this.dateVente = dateVente;
	}
	public double getMontantTotal() {
		return montantTotal;
	}
	public void setMontantTotal(double montantTotal) {
		this.montantTotal = montantTotal;
	}
	public String getStatut() {
		return statut;
	}
	public void setStatut(String statut) {
		this.statut = statut;
	}
	public int getIdClient() {
		return idClient;
	}
	public void setIdClient(int idClient) {
		this.idClient = idClient;
	}
	public int getIdPharmacien() {
		return idPharmacien;
	}
	public void setIdPharmacien(int idPharmacien) {
		this.idPharmacien = idPharmacien;
	}
	public List<LigneVente> getLignesVente() {
		return lignesVente;
	}
	public void setLignesVente(List<LigneVente> lignesVente) {
		this.lignesVente = lignesVente;
	}
	@Override
	public String toString() {
		return "Vente [id=" + id + ", dateVente=" + dateVente + ", montantTotal=" + montantTotal + ", statut=" + statut
				+ ", idClient=" + idClient + ", idPharmacien=" + idPharmacien + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vente other = (Vente) obj;
		return id == other.id;
	}
	
	public double calculerTotal() {
	    montantTotal = lignesVente.stream()
	            .map(ligne -> ligne.calculerTotalLigne())
	            .reduce(0.0, (a, b) -> a + b);
	    return montantTotal;
	}
	
	public void ajouterLigneVente(LigneVente ligne) {
	    lignesVente.add(ligne);
	    calculerTotal();
	}
	
}
