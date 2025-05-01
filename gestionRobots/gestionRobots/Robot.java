package gestionRobots;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class Robot {
    protected String id;
    protected int x;
    protected int y;
    protected int energie;
    protected int heuresUtilisation;
    protected boolean enMarche;
    protected List<String> historiqueActions;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public int getEnergie() {
        return energie;
    }
    public void setEnergie(int energie) {
        this.energie = energie;
    }

    public int getHeuresUtilisation() {
        return heuresUtilisation;
    }
    public void setHeuresUtilisation(int heuresUtilisation) {
        this.heuresUtilisation = heuresUtilisation;
    }

    public boolean getEnMarche() {
        return enMarche;
    }
    public void setEnMarche(boolean enMarche) {
        this.enMarche = enMarche;
    }


    protected void incrementerHeuresUtilisation(int heures) {
        this.heuresUtilisation += heures;
    }
    protected void decrementerHeuresUtilisation(int heures) {
        this.heuresUtilisation = Math.max(0, this.heuresUtilisation - heures);
    }

    protected void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }



    protected Robot(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.energie = 100;
        this.heuresUtilisation = 0;
        this.enMarche = false;
        this.historiqueActions = new ArrayList<>();
        ajouterHistorique("Robot créé");
    }

    protected void ajouterHistorique(String action) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
        historiqueActions.add(now.format(formatter) + " " + action);
    }

    protected void verifierEnergie(int energieRequise) throws EnergieInsuffisanteException {
        if (this.energie < energieRequise) {
            throw new EnergieInsuffisanteException("Énergie insuffisante pour effectuer cette action. Niveau actuel : " + this.energie + "%, requis : " + energieRequise + "%.");
        }
    }

    protected void verifierMaintenance() throws MaintenanceRequiseException {
        if (this.heuresUtilisation >= 100) {
            throw new MaintenanceRequiseException("Maintenance requise. Le robot a atteint " + this.heuresUtilisation + " heures d'utilisation.");
        }
    }

    public void demarrer() throws RobotException {
        if (this.energie < 10) {
            throw new RobotException("Impossible de démarrer le robot : niveau d'énergie insuffisant (moins de 10%).");
        }
        this.enMarche = true;
        ajouterHistorique("Démarrage du robot");
    }

    public void arreter() {
        this.enMarche = false;
        ajouterHistorique("Arrêt du robot");
    }

    protected void consommerEnergie(int quantite) {
        this.energie = Math.max(0, this.energie - quantite);
    }

    public void recharger(int quantite) {
        this.energie = Math.min(100, this.energie + quantite);
        ajouterHistorique("Recharge de " + quantite + "% d'énergie. Niveau actuel : " + this.energie + "%.");
    }

    public abstract void deplacer(int newX, int newY) throws RobotException;

    public abstract void effectuerTache() throws RobotException;

    public String getHistorique() {
        StringBuilder sb = new StringBuilder("Historique des actions du robot " + this.id + ":\n");
        for (String action : historiqueActions) {
            sb.append("- ").append(action).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ID : " + id + ", Position : (" + x + "," + y + "), Énergie : " + energie + "%, Heures : " + heuresUtilisation + "]";
    }
   
}