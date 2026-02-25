package model;

import java.util.Objects;

public class LigneVente {
	private int id;
	private int quantiteVendue;
	private double prixUnitaire;
	private int idVente;
	private int idMedicament;
	
	public LigneVente() {}
	
	public LigneVente(int id, int quantiteVendue, double prixUnitaire, int idVente, int idMedicament) {
		super();
		this.id = id;
		this.quantiteVendue = quantiteVendue;
		this.prixUnitaire = prixUnitaire;
		this.idVente = idVente;
		this.idMedicament = idMedicament;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getQuantiteVendue() {
		return quantiteVendue;
	}

	public void setQuantiteVendue(int quantiteVendue) {
		this.quantiteVendue = quantiteVendue;
	}

	public double getPrixUnitaire() {
		return prixUnitaire;
	}

	public void setPrixUnitaire(double prixUnitaire) {
		this.prixUnitaire = prixUnitaire;
	}

	public int getIdVente() {
		return idVente;
	}

	public void setIdVente(int idVente) {
		this.idVente = idVente;
	}

	public int getIdMedicament() {
		return idMedicament;
	}

	public void setIdMedicament(int idMedicament) {
		this.idMedicament = idMedicament;
	}

	@Override
	public String toString() {
		return "LigneVente [id=" + id + ", quantiteVendue=" + quantiteVendue + ", prixUnitaire=" + prixUnitaire
				+ ", idVente=" + idVente + ", idMedicament=" + idMedicament + "]";
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
		LigneVente other = (LigneVente) obj;
		return id == other.id;
	}
	
	public double calculerTotalLigne() {
	    return quantiteVendue * prixUnitaire;
	}
}
