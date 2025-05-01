package gestionRobots;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RobotLivraison livreur = new RobotLivraison("LVR-001", 0, 0);

        try {
            System.out.println("--- Simulation de Livraison ---");
            System.out.println("Robot créé : " + livreur);

            // Démarrage du robot
            livreur.demarrer();
            System.out.println("État du robot après démarrage : " + livreur);

            // Chargement du colis
            System.out.println("\n--- Chargement du colis ---");
            System.out.println("Veuillez entrer la destination du colis :");
            String destination = scanner.nextLine();
            livreur.chargerColis(destination);
            System.out.println("État du robot après chargement : " + livreur);

            // Effectuer la tâche (mène à la livraison si enLivraison est true)
            System.out.println("\n--- Préparation de la livraison ---");
            livreur.effectuerTache(); // Va demander les coordonnées de destination

            System.out.println("État du robot après la tentative de livraison : " + livreur);

            // Arrêt du robot
            livreur.arreter();
            System.out.println("État du robot après arrêt : " + livreur);

            // Affichage de l'historique
            System.out.println("\n--- Historique des actions ---");
            System.out.println(livreur.getHistorique());

        } catch (RobotException e) {
            System.err.println("Erreur : " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}