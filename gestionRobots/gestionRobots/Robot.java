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

    // Ajout écologique: seuil avant de considérer le robot en mode veille
    protected static final int SEUIL_INACTIVITE_VEILLE = 30; // secondes
    protected LocalDateTime dernierActionTime;
    protected boolean enVeille = false;

    // Ajout écologique: Simuler l'usure des composants
    protected int niveauUsure = 0;
    protected static final int SEUIL_USURE_MAINTENANCE = 100;
    protected static final int CONSOMMATION_EN_VEILLE = 1 ; // % d'énergie par minute en veille

    protected String sourceRecharge = "Standard";

    protected static final int SEUIL_BATTERIE_FAIBLE = 20; // Seuil de batterie faible

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

    public boolean isEnVeille() {
        return enVeille;
    }

    protected void setEnVeille(boolean enVeille) {
        this.enVeille = enVeille;
        if (enVeille) {
            ajouterHistorique("Passage en mode veille.");
        } else {
            ajouterHistorique("Sortie du mode veille.");
        }
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
        this.dernierActionTime = LocalDateTime.now();
    }
    public String getSourceRecharge() {
        return sourceRecharge;
    }

    public void setSourceRecharge(String sourceRecharge) {
        this.sourceRecharge = sourceRecharge;
    }

    public boolean isBatterieFaible() {
        return energie < SEUIL_BATTERIE_FAIBLE;
    }
    protected Robot(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.energie = 100;
        this.heuresUtilisation = 0;
        this.enMarche = false;
        this.historiqueActions = new ArrayList<>();
        this.dernierActionTime = LocalDateTime.now();
        ajouterHistorique("Robot créé");
    }

    protected void ajouterHistorique(String action) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm:ss");
        this.dernierActionTime = now;
        historiqueActions.add(now.format(formatter) + " " + action);
    }

    protected void verifierEnergie(int energieRequise) throws EnergieInsuffisanteException {
        if (this.energie < energieRequise) {
            throw new EnergieInsuffisanteException("Énergie insuffisante pour effectuer cette action. Niveau actuel : " + this.energie + "%, requis : " + energieRequise + "%.");
        }
    }

    protected void verifierMaintenance() throws MaintenanceRequiseException {
        if (this.heuresUtilisation >= SEUIL_USURE_MAINTENANCE) {
            throw new MaintenanceRequiseException("Maintenance requise. Le robot a atteint " + this.heuresUtilisation + " heures d'utilisation.");
        }
    }

    public void demarrer() throws RobotException {
        if (this.energie < 10) {
            throw new RobotException("Impossible de démarrer le robot : niveau d'énergie insuffisant (moins de 10%).");
        }
        this.enMarche = true;
        this.enVeille = false;
        ajouterHistorique("Démarrage du robot");
    }

    public void arreter() {
        this.enMarche = false;
        this.enVeille = false;
        ajouterHistorique("Arrêt du robot");
    }
    
    protected void consommerEnergie(int quantite) {
        this.energie = Math.max(0, this.energie - quantite);
    }

    public void recharger(int quantite) {
        if (this.energie + quantite > 100) {
            this.energie = 100;
        } else {
            this.energie += quantite;
        }
        ajouterHistorique("Recharge de " + quantite + "% d'énergie. Niveau actuel : " + this.energie + "%.");
        if (this.energie < 20) {
            ajouterHistorique("Avertissement : Niveau d'énergie faible.");
        }
    }

    public abstract void deplacer(int newX, int newY) throws RobotException;
    public abstract void effectuerTache() throws RobotException;

    public String getHistorique() {
        StringBuilder sb = new StringBuilder("Historique des actions du robot " + this.id + ":\n");
        for (String action : historiqueActions) {
            sb.append(" - ").append(action).append("\n");
        }
        return sb.toString();
    }

    //Simuler la dégradation de l'énergie passivement
    public void gestionEnergiePassive() {
        if (enMarche && !enVeille) {
            consommerEnergie(1); // méme en marche ca consomme de l'énergie
            incrementerHeuresUtilisation(1); // L'utilisation continue quand il est en marche
            niveauUsure++;
            if (niveauUsure >= SEUIL_USURE_MAINTENANCE ) { // Usure aléatoire autour du seuil
                ajouterHistorique("Avertissement : Niveau d'usure élevé.");
            }
        } else if (enMarche && enVeille) {
            consommerEnergie(CONSOMMATION_EN_VEILLE);
        }
        // passage en mode veille
        if (enMarche && !enVeille && LocalDateTime.now().isAfter(dernierActionTime.plusSeconds(SEUIL_INACTIVITE_VEILLE))) {
            setEnVeille(true);
        }
    }

    @Override
    public String toString() {
        String etatMarche = enMarche ? "En marche" : "Arrêté";
        String etatVeille = enVeille ? ", En veille" : "";
        String etatBatterie = isBatterieFaible() ? ", Batterie faible" : "";
        return getClass().getSimpleName() + " [ID : " + id + ", Position : (" + x + "," + y + "), Énergie : " + energie + "%, Heures : " + heuresUtilisation + ", État : " + etatMarche + etatVeille + etatBatterie + ", Usure : " + niveauUsure + "/" + SEUIL_USURE_MAINTENANCE + ", Recharge : " + sourceRecharge + "]";
    }

    // Méthode de gestion de batterie
    public void optimiserBatterie() {
        if (energie < 20 && enMarche && !enVeille) {
            setEnVeille(true);
            ajouterHistorique("Batterie faible (< 20%), passage automatique en mode veille pour économiser l'énergie.");
            System.err.println("Impossible d'effectuer quelque tache comme la livraison eloignée");
        } else if (energie > 80 && enVeille) {
            setEnVeille(false);
            ajouterHistorique("Batterie suffisamment rechargée (> 80%), sortie du mode veille.");
        }
    }
}