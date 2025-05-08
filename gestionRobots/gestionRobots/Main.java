package gestionRobots;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RobotLivraison livreur = new RobotLivraison("LVR-001", 0, 0);
        boolean continuerOperations = true;

        try {
            System.out.println("--- Simulation de Livraison Écologique ---");
            System.out.println("Robot créé : " + livreur);

            while (continuerOperations) {
                
                boolean estConnecte = false;
                if (livreur instanceof RobotConnecte) {
                    System.out.println("\n--- Connexion du robot ---");
                    System.out.println("Souhaitez-vous connecter le robot à un réseau ? (oui/non)");
                    String reponseConnexion = scanner.nextLine().trim().toLowerCase();
                    if (reponseConnexion.equals("oui") || reponseConnexion.equals("o")) {
                        livreur.arreter();
                        System.out.println("État du robot après arrêt pour connexion : " + livreur);
                        System.out.println("Entrez le nom du réseau :");
                        String reseau = scanner.nextLine();
                        try {
                            ((RobotConnecte) livreur).connecter(reseau);
                            System.out.println("État du robot après tentative de connexion : " + livreur);
                            ((RobotConnecte) livreur).verifierMiseAJour();
                            if (((RobotConnecte) livreur).isMiseAJourDisponible()) {
                                ((RobotConnecte) livreur).appliquerMiseAJour();
                                System.out.println("État du robot après mise à jour : " + livreur);
                            }
                            estConnecte = ((RobotConnecte) livreur).isConnecte();
                        } catch (RobotException e) {
                            System.err.println("Erreur lors de la connexion : " + e.getMessage());
                        } finally {
                            livreur.demarrer();
                            System.out.println("État du robot après la phase de connexion : " + livreur);
                        }
                    } else if (reponseConnexion.equals("non") || reponseConnexion.equals("n") || reponseConnexion.equals("no") ) {
                        livreur.demarrer(); // Démarrer même si pas de connexion
                        System.out.println("État du robot après la phase de connexion (aucune tentative) : " + livreur);
                    } else {
                        System.out.println("Réponse invalide. Veuillez répondre par 'oui' ou 'non'.");
                    }
                } else {
                    livreur.demarrer(); // Démarrer si ce n'est pas un RobotConnecte
                    System.out.println("État du robot après démarrage (non RobotConnecte) : " + livreur);
                }

                // Question sur l'envoi de données (posée à chaque opération si connecté)
                if (livreur instanceof RobotConnecte && ((RobotConnecte) livreur).isConnecte()) {
                    System.out.println("\n--- Envoi de données ---");
                    System.out.println("Souhaitez-vous envoyer des données ? (oui/non)");
                    String reponseEnvoyerDonnees = scanner.nextLine().trim().toLowerCase();
                    if (reponseEnvoyerDonnees.equals("oui") || reponseEnvoyerDonnees.equals("o")) {
                        System.out.println("Veuillez entrer les données à envoyer :");
                        String donnees = scanner.nextLine().trim();
                        ((RobotConnecte) livreur).envoyerDonnees(donnees);
                        System.out.println("Données envoyées.");
                    } else if (reponseEnvoyerDonnees.equals("non") || reponseEnvoyerDonnees.equals("n") || reponseEnvoyerDonnees.equals("no")) {
                        System.out.println("L'envoi de données a été annulé.");
                    } else {
                        System.out.println("Réponse invalide. Veuillez répondre par 'oui' ou 'non'.");
                    }
                } else if (livreur instanceof RobotConnecte && !((RobotConnecte) livreur).isConnecte()) {
                    System.out.println("Le robot n'est pas connecté, impossible d'envoyer des données.");
                }

                // Demande de chargement de colis
                System.out.println("\n--- Chargement du colis ---");
                System.out.println("Souhaitez-vous charger un colis ? (oui/non)");
                String reponseCharger = scanner.nextLine().trim().toLowerCase();
                if (reponseCharger.equals("oui") || reponseCharger.equals("o")) {
                    System.out.println("Veuillez entrer la destination du colis :");
                    String destination = scanner.nextLine().trim();
                    try {
                        livreur.chargerColis(destination);
                        System.out.println("État du robot après chargement : " + livreur);

                        // Demande d'effectuer une livraison
                        System.out.println("\n--- Livraison ---");
                        System.out.println("Souhaitez-vous effectuer une livraison ? (oui/non)");
                        String reponseLivrer = scanner.nextLine().trim().toLowerCase();
                        if (reponseLivrer.equals("oui") || reponseLivrer.equals("o")) {
                            System.out.println("\n--- Préparation de la livraison ---");
                            livreur.effectuerTache(); //demander les coordonnées de destination
                            System.out.println("État du robot après la tentative de livraison : " + livreur);

                            // Choix du mode de livraison
                            System.out.println("Souhaitez-vous une livraison rapide (oui/non) ?");
                            String reponseRapide = scanner.nextLine().trim().toLowerCase();
                            livreur.setLivraisonRapide(reponseRapide.equals("oui") || reponseRapide.equals("o"));
                            System.out.println("Mode de livraison rapide : " + livreur.isLivraisonRapide());
                        } else if (reponseLivrer.equals("non") || reponseLivrer.equals("n")|| reponseLivrer.equals("no")) {
                            System.out.println("La livraison a été annulée.");
                            livreur.setEnLivraison(false); // Réinitialiser l'état en cas d'annulation de la livraison
                        } else {
                            System.out.println("Réponse invalide. Veuillez répondre par 'oui' ou 'non'.");
                        }
                    } catch (RobotException e) {
                        System.err.println("Erreur lors du chargement : " + e.getMessage());
                        livreur.setEnLivraison(false); // s'assurer que l'état de livraison est réinitialisé en cas d'erreur de chargement
                    }
                } else if (reponseCharger.equals("non") || reponseCharger.equals("n")|| reponseCharger.equals("no")) {
                    System.out.println("Le chargement du colis a été annulé.");
                    livreur.setEnLivraison(false); // s'assurer que l'état n'est pas bloqué à 'enLivraison' si on ne charge pas
                } else {
                    System.out.println("Réponse invalide. Veuillez répondre par 'oui' ou 'non'.");
                }

                System.out.println("\n--- Fin de l'opération actuelle ---");
                System.out.println("Souhaitez-vous effectuer d'autres opérations ? (oui/non)");
                String reponseContinuer = scanner.nextLine().trim().toLowerCase();
                if (reponseContinuer.equals("oui") || reponseContinuer.equals("o")) {
                    continuerOperations = true;
                } else if (reponseContinuer.equals("non") || reponseContinuer.equals("n")|| reponseContinuer.equals("no")) {
                    continuerOperations = false;
                    System.out.println("Fin de la simulation.");
                } else {
                    System.out.println("Réponse invalide. Veuillez répondre par 'oui' ou 'non'.");
                }

                livreur.arreter();
                System.out.println("État du robot après arrêt : " + livreur);
            }

            System.out.println("\n--- Historique final des actions ---");
            System.out.println(livreur.getHistorique());

        } catch (RobotException e) {
            System.err.println("Erreur : " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}