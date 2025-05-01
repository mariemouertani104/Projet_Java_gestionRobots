package gestionRobots;

import java.util.Scanner;


public class RobotLivraison extends RobotConnecte {
    protected String colisActuel;
    protected String destination;
    protected boolean enLivraison;
    protected static final int ENERGIE_LIVRAISON = 15;
    protected static final int ENERGIE_CHARGEMENT = 5;

    public RobotLivraison(String id, int x, int y) {
        super(id, x, y);
        this.colisActuel = null;
        this.destination = null;
        this.enLivraison = false;
    }

    public String getColisActuel() {
        return colisActuel;
    }
    public void setColisActuel(String colisActuel) {
        this.colisActuel = colisActuel;
    }

    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isEnLivraison() {
        return enLivraison;
    }
    public void setEnLivraison(boolean enLivraison) {
        this.enLivraison = enLivraison;
    }
    public static int getEnergieLivraison() {
        return ENERGIE_LIVRAISON;
    }
    public static int getEnergieChargement() {
        return ENERGIE_CHARGEMENT;
    }

    @Override
    public void effectuerTache() throws RobotException {
        if (!getEnMarche()) {
            throw new RobotException("Le robot doit être démarré pour effectuer une tâche.");
        }
    
        Scanner scanner = new Scanner(System.in);
    
        if (enLivraison) {
            System.out.println("Veuillez entrer les coordonnées X de la destination :");
            int destX = scanner.nextInt();
            System.out.println("Veuillez entrer les coordonnées Y de la destination :");
            int destY = scanner.nextInt();
            faireLivraison(destX, destY);
            System.out.println("Colis livré avec succès à destination de : " + destination);
            ajouterHistorique("Colis livré avec succès à destination de : " + destination);
            this.colisActuel = null;
            this.destination = null;
            this.enLivraison = false;
        } else {
            System.out.println("Voulez-vous charger un nouveau colis ? (oui/non)");
            String reponse = scanner.next().toLowerCase();
            if (reponse.equals("oui") || reponse.equals("o")) {
                ajouterHistorique("Demande de chargement d'un nouveau colis.");
                try {
                    verifierEnergie(ENERGIE_CHARGEMENT);
                    System.out.println("Veuillez entrer la destination du colis :");
                    String nouvelleDestination = scanner.next();
                    chargerColis(nouvelleDestination);
                    System.out.println("Colis chargé avec succès à destination de : " + nouvelleDestination);
                    ajouterHistorique("Colis chargé avec succès à destination de : " + nouvelleDestination);
                } catch (EnergieInsuffisanteException e) {
                    System.out.println("Erreur : " + e.getMessage());
                    ajouterHistorique("Tentative de chargement de colis échouée car l'énergie est insuffisante.");
                }
            } else {
                ajouterHistorique("En attente de colis.");
            }
        }
    }
    
    public void faireLivraison(int destX, int destY) throws RobotException {
        
        verifierEnergie(ENERGIE_LIVRAISON);
        deplacer(destX, destY);
        this.colisActuel = null;
        this.enLivraison = false;
        ajouterHistorique("Livraison terminée à " + destination);
    }


    @Override
    public void deplacer(int destX, int destY) throws RobotException {
        double distance = Math.sqrt(Math.pow(destX - getX(), 2) + Math.pow(destY - getY(), 2));
    
        if (distance > 100) {
            throw new RobotException("La distance de déplacement (" + String.format("%.2f", distance) + ") est supérieure à la limite de 100 unités.");
        }
    
        verifierMaintenance();
        verifierEnergie((int) (distance * 0.3));
    
        int heuresDeplacement = (int) (distance / 10);
        incrementerHeuresUtilisation(heuresDeplacement);
        consommerEnergie((int) (distance * 0.3));
        setPosition(destX, destY);
        ajouterHistorique("Déplacement vers (" + destX + "," + destY + "), distance parcourue : " + String.format("%.2f", distance) + " unités, temps estimé : " + heuresDeplacement + " heures.");
    }

    
    public void chargerColis(String destination) throws RobotException {
        if (this.enLivraison) {
            throw new RobotException("Impossible de charger un colis : le robot est déjà en livraison.");
        }
        if (this.colisActuel != null) {
            throw new RobotException("Impossible de charger un colis : le robot transporte déjà un colis.");
        }
        verifierEnergie(ENERGIE_CHARGEMENT);
        this.colisActuel = "1";
        this.destination = destination;
        consommerEnergie(ENERGIE_CHARGEMENT);
        this.enLivraison = true;
        ajouterHistorique("Colis chargé à destination de : " + destination + " (consommation: " + ENERGIE_CHARGEMENT + "%)");
    }

    @Override
    public String toString() {
        String etatConnexion = isConnecte() ? "Oui" : "Non";
        String colisInfo = (colisActuel != null) ? "1" : "0";
        String destinationInfo = (destination != null) ? destination : "N/A";
        return getClass().getSimpleName() + " [ID : " + getId() + ", Position : (" + getX() + "," + getY() + "), Énergie : " + getEnergie() + "%, Heures : " + getHeuresUtilisation() + ", Colis : " + colisInfo + ", Destination : " + destinationInfo + ", Connecté : " + etatConnexion + "]";
    }
}