package model;

import java.util.Objects;

public class EffetSecondaire {
	 private int id;
	    private String nom;
	    private String description;
	    
	 
	    public EffetSecondaire() {
	    }
	    
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		public String getNom() {
			return nom;
		}
		
		public void setNom(String nom) {
			this.nom = nom;
		}
		
		public String getDescription() {
			return description;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(description, id, nom);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EffetSecondaire other = (EffetSecondaire) obj;
			return Objects.equals(description, other.description) && id == other.id && Objects.equals(nom, other.nom);
		}
		
		@Override
		public String toString() {
			return "EffetSecondaire [id=" + id + ", nom=" + nom + ", description=" + description + "]";
		}
		
		public EffetSecondaire(int id, String nom, String description) {
			super();
			this.id = id;
			this.nom = nom;
			this.description = description;
		}
		
}
