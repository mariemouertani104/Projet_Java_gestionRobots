package gestionRobots;

public abstract class RobotConnecte extends Robot implements Connectable {
    protected boolean connecte;
    protected String reseauConnecte;

    //suivi des données envoyées et reçues
    protected int donneesEnvoyees = 0;
    protected int donneesRecues = 0;

    protected static final int ENERGIE_PAR_ENVOI = 3;
    protected static final int ENERGIE_PAR_RECEPTION = 1;
    //recevoir des mises à jour logicielles écoénergétiques
    protected boolean miseAJourDisponible = false;
    protected String versionLogiciel = "1.0";

    public RobotConnecte(String id, int x, int y) {
        super(id, x, y);
        this.connecte = false;
        this.reseauConnecte = null;
    }

    public boolean isConnecte() {
        return connecte;
    }
    public void setConnecte(boolean connecte) {
        this.connecte = connecte;
    }

    public String getReseauConnecte() {
        return reseauConnecte;
    }
    public void setReseauConnecte(String reseauConnecte) {
        this.reseauConnecte = reseauConnecte;
    }

    public int getDonneesEnvoyees() {
        return donneesEnvoyees;
    }

    public int getDonneesRecues() {
        return donneesRecues;
    }

    public String getVersionLogiciel() {
        return versionLogiciel;
    }

    public boolean isMiseAJourDisponible() {
        return miseAJourDisponible;
    }

    @Override
    public void connecter (String reseau) throws RobotException {
        verifierEnergie(5);
        if (reseau == null || reseau.isEmpty()) {
            throw new RobotException("Impossible de se connecter à un réseau vide ou null.");
        }
        if (this.connecte) {
            throw new RobotException("Le robot est déjà connecté à un réseau.");
        }
        verifierMaintenance();
        if (this.enMarche) {
            throw new RobotException("Le robot doit être arrêté pour se connecter à un réseau.");
        }
        this.reseauConnecte = reseau;
        this.connecte = true;
        consommerEnergie(5);
        ajouterHistorique("Connecté au réseau : " + reseau +" en consommant 5% d'énergie.");
    }

    @Override
    public void deconnecter() {
        if (this.connecte) {
            ajouterHistorique("Déconnecté du réseau : " + this.reseauConnecte );
            this.reseauConnecte = null;
            this.connecte = false;
        } else {
            ajouterHistorique("Tentative de déconnexion alors que le robot n'était pas connecté à un réseau.");
        }
    }

    @Override
    public void envoyerDonnees(String donnees) throws RobotException {
        if (!this.connecte) {
            throw new RobotException("Impossible d'envoyer les données : le robot n'est pas connecté à un réseau.");
        }
        else if (donnees == null || donnees.isEmpty()) {
            throw new RobotException("Impossible d'envoyer des données vides ou nulles.");
        }
        verifierMaintenance();
        verifierEnergie(ENERGIE_PAR_ENVOI);
        consommerEnergie(ENERGIE_PAR_ENVOI);
        this.donneesEnvoyees++;
        ajouterHistorique("Données envoyées : \"" + donnees + "\" via le réseau : " + this.reseauConnecte + " (coût: " + ENERGIE_PAR_ENVOI + "%)");
    }



    public void verifierMiseAJour() {
        // Simuler une vérification ,dans le suivant c'est toujours vrai disponible)
        this.miseAJourDisponible = true;
        ajouterHistorique("Vérification des mises à jour logicielles. Mise à jour disponible.");
    }

    public void appliquerMiseAJour() throws RobotException {
        if (!connecte) {
            throw new RobotException("Le robot doit être connecté pour appliquer une mise à jour.");
        }
        if (miseAJourDisponible) {
            verifierEnergie(10); // Coût énergétique de la mise à jour
            consommerEnergie(10);
            this.versionLogiciel = "1.1-eco"; // Nouvelle version avec améliorations écoénergétiques , la version de MAJ
            this.miseAJourDisponible = false; // retourner à n'existe pas
            ajouterHistorique("Mise à jour logicielle vers la version " + this.versionLogiciel + " appliquée (coût: 10%).");
        } else {
            ajouterHistorique("Aucune mise à jour logicielle disponible.");
        }
    }

    //simuler la réception de données + des informations d'optimisation
    public void recevoirDonnees(String donnees) throws RobotException {
        if (!connecte) {
            throw new RobotException("Le robot doit être connecté pour recevoir des données.");
        }
        verifierEnergie(ENERGIE_PAR_RECEPTION);
        consommerEnergie(ENERGIE_PAR_RECEPTION);
        this.donneesRecues++;
        ajouterHistorique("Données reçues : \"" + donnees + "\" via le réseau : " + this.reseauConnecte + " (coût: " + ENERGIE_PAR_RECEPTION + "%)");

        // Ici, on pourrait imaginer un traitement de ces données pour optimiser le comportement du robot
        if (donnees.contains("optimisation_itineraire")) {
            ajouterHistorique("Données reçues suggérant une optimisation de l'itinéraire.");
        }
    }

    @Override
    public String toString() {
        String etatConnexion = connecte ? "connecté à " + reseauConnecte : "non connecté";
        return super.toString() + ", Connectivité : " + etatConnexion + ", Données envoyées : " + donneesEnvoyees + ", Données reçues : " + donneesRecues + ", Logiciel : " + versionLogiciel + (miseAJourDisponible ? " (mise à jour disponible)" : "");
    }
}