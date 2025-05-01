package gestionRobots;

public abstract class RobotConnecte extends Robot implements Connectable {
    protected boolean connecte;
    protected String reseauConnecte;

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

    @Override
    public void connecter(String reseau) throws RobotException {
        verifierEnergie(5);
        if (reseau == null || reseau.isEmpty()) {
            throw new RobotException("Impossible de se connecter à un réseau vide ou null.");
        }
        if (this.connecte) {
            throw new RobotException("Le robot est déjà connecté à un réseau.");
        }
        if (this.heuresUtilisation >= 100) {
            throw new MaintenanceRequiseException("Maintenance requise. Le robot a atteint " + this.heuresUtilisation + " heures d'utilisation.");
        }
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
        verifierEnergie(3);
        consommerEnergie(3);
        ajouterHistorique("Données envoyées : \"" + donnees + "\" via le réseau : " + this.reseauConnecte );
    }

    @Override
    public String toString() {
        String etatConnexion = connecte ? "connecté à " + reseauConnecte : "non connecté";
        return super.toString() + ", Connectivité : " + etatConnexion;
    }
}