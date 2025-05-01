package gestionRobots;

public interface Connectable {
    void connecter(String reseau) throws RobotException;
    void deconnecter();
    void envoyerDonnees(String donnees) throws RobotException;
}