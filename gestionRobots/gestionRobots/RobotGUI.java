package gestionRobots;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class RobotGUI extends JFrame implements ActionListener {

    private List<Robot> robots = new ArrayList<>();
    private JComboBox<String> robotList;
    private JTextArea actionHistory;
    private JButton startButton, stopButton, moveButton, connectButton, disconnectButton, sendDataButton, loadButton, deliverButton,
            rechargeButton, rechargeEcoButton, optimiseRouteButton, fastDeliveryButton, checkUpdateButton, applyUpdateButton, forceVeilleButton;
    private JTextField moveXField, moveYField, connectNetworkField, sendDataField, loadDestinationField, preferredZoneField;
    private JPanel robotControlPanel;
    private JPanel robotDrawingPanel;
    private JPanel actionPanel;
    private JPanel statusPanel;
    private JLabel statusLabel;
    private Robot currentRobot = null;
    private boolean colisChargeValide = false;

    public RobotGUI() {
        setTitle("Gestionnaire de Robots Écologiques");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de sélection robot
        JPanel selectionPanel = new JPanel();
        JLabel robotLabel = new JLabel("Sélectionner un robot :");
        robotList = new JComboBox<>();
        robotList.addActionListener(e -> updateSelectedRobot());
        selectionPanel.add(robotLabel);
        selectionPanel.add(robotList);
        add(selectionPanel, BorderLayout.NORTH);

        // Panel pour le dessin du robot
        robotDrawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRobot(g);
            }
        };
        robotDrawingPanel.setBackground(Color.WHITE);
        add(robotDrawingPanel, BorderLayout.CENTER);

        // Panel pour les boutons de démarrage et d'arrêt
        robotControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startButton = new JButton("Démarrer");
        startButton.setBackground(new Color(0, 150, 0));
        startButton.setForeground(Color.WHITE);
        stopButton = new JButton("Arrêter");
        stopButton.setBackground(new Color(150, 0, 0));
        stopButton.setForeground(Color.WHITE);

        startButton.addActionListener(this);
        stopButton.addActionListener(this);

        robotControlPanel.add(startButton);
        robotControlPanel.add(stopButton);


        // Panel pour les autres boutons d'action et les champs de saisie
        actionPanel = new JPanel(new GridLayout(0, 12, 5, 5));
        moveButton = new JButton("Déplacer vers:");
        moveXField = new JTextField(5);
        moveYField = new JTextField(5);
        connectButton = new JButton("Connecter à :");
        connectNetworkField = new JTextField(5);
        disconnectButton = new JButton("Déconnecter");
        sendDataButton = new JButton("Envoyer données :");
        sendDataField = new JTextField(20);
        loadButton = new JButton("Charger colis pour :");
        loadDestinationField = new JTextField(15);
        deliverButton = new JButton("Livrer Colis");
        rechargeButton = new JButton("Recharger");
        rechargeButton.setBackground(new Color(0, 100, 200));
        rechargeButton.setForeground(Color.WHITE);
        rechargeEcoButton = new JButton("Recharger Éco");
        rechargeEcoButton.setBackground(new Color(0, 180, 0));
        rechargeEcoButton.setForeground(Color.WHITE);
        optimiseRouteButton = new JButton("Optimiser Route");
        fastDeliveryButton = new JButton("Livraison Rapide");
        checkUpdateButton = new JButton("Vérifier MàJ");
        applyUpdateButton = new JButton("Appliquer MàJ");
        JLabel preferredZoneLabel = new JLabel("Zone Préférée :");
        preferredZoneField = new JTextField(10);
        forceVeilleButton = new JButton("Forcer Veille");

        
        

        moveButton.addActionListener(this);
        connectButton.addActionListener(this);
        disconnectButton.addActionListener(this);
        sendDataButton.addActionListener(this);
        loadButton.addActionListener(this);
        deliverButton.addActionListener(this);
        rechargeButton.addActionListener(this);
        rechargeEcoButton.addActionListener(this);
        optimiseRouteButton.addActionListener(this);
        fastDeliveryButton.addActionListener(this);
        checkUpdateButton.addActionListener(this);
        applyUpdateButton.addActionListener(this);
        preferredZoneField.addActionListener(this);
        forceVeilleButton.addActionListener(this);

        actionPanel.add(moveButton);
        actionPanel.add(moveXField);
        actionPanel.add(moveYField);
        actionPanel.add(connectButton);
        actionPanel.add(connectNetworkField);
        actionPanel.add(disconnectButton);
        actionPanel.add(sendDataButton);
        actionPanel.add(sendDataField);
        actionPanel.add(loadButton);
        actionPanel.add(loadDestinationField);
        actionPanel.add(deliverButton);
        actionPanel.add(rechargeButton);
        actionPanel.add(rechargeEcoButton);
        actionPanel.add(optimiseRouteButton);
        actionPanel.add(fastDeliveryButton);
        actionPanel.add(checkUpdateButton);
        actionPanel.add(applyUpdateButton);
        actionPanel.add(preferredZoneLabel);
        actionPanel.add(preferredZoneField);
        actionPanel.add(forceVeilleButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(actionPanel, BorderLayout.NORTH);
        centerPanel.add(robotDrawingPanel, BorderLayout.CENTER);
        centerPanel.add(robotControlPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);


        // Panel pour l'état actuel du robot
        statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusTitleLabel = new JLabel("État du robot : ");
        statusTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusTitleLabel.setForeground(Color.GREEN);
        statusLabel = new JLabel("Aucun robot sélectionné");
        statusPanel.add(statusTitleLabel);
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        // Panel pour l'historique des actions
        actionHistory = new JTextArea(15, 60);
        JScrollPane historyScrollPane = new JScrollPane(actionHistory);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusPanel, BorderLayout.NORTH);
        bottomPanel.add(historyScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Création de robots pour l'interface
        RobotLivraison livreurGUI = new RobotLivraison("LVR-GUI-001", 30, 50);
        robots.add(livreurGUI);
        updateRobotList();
        updateSelectedRobot();
        enableOrDisableButtons();
        setVisible(true);

        // Démarrer le timer pour la gestion de l'énergie passive et l'optimisation de la batterie
        Timer timer = new Timer(1000, e -> {
            for (Robot robot : robots) {
                robot.gestionEnergiePassive();
                robot.optimiserBatterie();
            }
            updateStatusPanel();
            updateRobotDrawing();
        });
        timer.start();
    }

    private void updateSelectedRobot() {
        currentRobot = getSelectedRobot();
        enableOrDisableButtons();
        updateRobotDrawing();
        updateActionHistory();
        updateStatusPanel();
    }

    private void updateRobotList() {
        robotList.removeAllItems();
        for (Robot robot : robots) {
            robotList.addItem(robot.getId());
        }
    }

    private void updateRobotDrawing() {
        robotDrawingPanel.repaint();
    }

    private void updateActionHistory() {
        if (currentRobot != null) {
            actionHistory.setText(currentRobot.getHistorique());
        } else {
            actionHistory.setText("");
        }
    }

    private void updateStatusPanel() {
        if (currentRobot != null) {
            statusLabel.setText(currentRobot.toString());
        } else {
            statusLabel.setText("Aucun robot sélectionné");
        }
    }

    private Robot getSelectedRobot() {
        String selectedId = (String) robotList.getSelectedItem();
        if (selectedId != null) {
            for (Robot robot : robots) {
                if (robot.getId().equals(selectedId)) {
                    return robot;
                }
            }
        }
        return null;
    }

    private void drawRobot(Graphics g) {
        if (currentRobot != null) {
            int x = currentRobot.getX();
            int y = currentRobot.getY();
            int size = 80;

            g.setColor(new Color(100, 249, 137));
            g.fillRect(x, y, size, size);
            g.setColor(new Color(169, 169, 169));
            g.fillOval(x + size / 4, y - size / 3, size / 2, size / 3);
            g.setColor(Color.YELLOW);
            g.fillOval(x + size / 3, y - size / 6, size / 6, size / 6);
            g.fillOval(x + size / 2, y - size / 6, size / 6, size / 6);

            if (currentRobot.getEnMarche()) {
                g.setColor(Color.GREEN);
                g.fillOval(x - 15, y - 15, 15, 15);
            } else {
                g.setColor(Color.RED);
                g.fillOval(x - 15, y - 15, 15, 15);
            }

            g.setColor(Color.BLACK);

            if (currentRobot instanceof RobotConnecte) {
                RobotConnecte connecte = (RobotConnecte) currentRobot;
                if (connecte.isConnecte()) {
                    g.drawString("Connecté", x, y - 30);
                }
                if (connecte.isMiseAJourDisponible()) {
                    g.drawString("MàJ!", x + size - 20, y - 30);
                }
            }
            g.drawString("X: " + currentRobot.getX() + ", Y: " + currentRobot.getY(), x, y + size + 20);
            g.drawString("Energie: " + currentRobot.getEnergie() + "%", x, y + size + 40);
            if (currentRobot instanceof Robot) {
                g.drawString("Usure: " + ((Robot) currentRobot).niveauUsure + "/" + Robot.SEUIL_USURE_MAINTENANCE, x, y + size + 60);
                g.drawString("Recharge: " + ((Robot) currentRobot).getSourceRecharge(), x, y + size + 80);
            }
            
        }
    }

    private void enableOrDisableButtons() {
        boolean started = (currentRobot != null && currentRobot.getEnMarche());
        moveButton.setEnabled(started);
        connectButton.setEnabled(currentRobot instanceof RobotConnecte);
        disconnectButton.setEnabled(currentRobot instanceof RobotConnecte && ((RobotConnecte) currentRobot).isConnecte());
        sendDataButton.setEnabled(started && currentRobot instanceof RobotConnecte && ((RobotConnecte) currentRobot).isConnecte());
        loadButton.setEnabled(started && currentRobot instanceof RobotLivraison);
        deliverButton.setEnabled(started && currentRobot instanceof RobotLivraison && ((RobotLivraison) currentRobot).getColisActuel() != null && colisChargeValide);
        rechargeButton.setEnabled(true);
        rechargeEcoButton.setEnabled(true);
        optimiseRouteButton.setEnabled(started && currentRobot instanceof RobotLivraison && colisChargeValide);
        fastDeliveryButton.setEnabled(started && currentRobot instanceof RobotLivraison && colisChargeValide);
        checkUpdateButton.setEnabled(started && currentRobot instanceof RobotConnecte);
        applyUpdateButton.setEnabled(started && currentRobot instanceof RobotConnecte);
        preferredZoneField.setEnabled(started && currentRobot instanceof RobotLivraison);
        forceVeilleButton.setEnabled(currentRobot != null && currentRobot.getEnMarche() && !currentRobot.isEnVeille());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentRobot == null) return;

        try {
            if (e.getSource() == startButton) {
                currentRobot.demarrer();
            } else if (e.getSource() == stopButton) {
                currentRobot.arreter();
            } else if (e.getSource() == moveButton) {
                try {
                    int x = Integer.parseInt(moveXField.getText());
                    int y = Integer.parseInt(moveYField.getText());
                    currentRobot.deplacer(x, y);
                    robotDrawingPanel.repaint();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Coordonnées de déplacement invalides.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else if (e.getSource() == connectButton && currentRobot instanceof RobotConnecte) {
                String network = connectNetworkField.getText();
                ((RobotConnecte) currentRobot).connecter(network);
            } else if (e.getSource() == disconnectButton && currentRobot instanceof RobotConnecte) {
                ((RobotConnecte) currentRobot).deconnecter();
            } else if (e.getSource() == sendDataButton && currentRobot instanceof RobotConnecte) {
                String data = sendDataField.getText();
                ((RobotConnecte) currentRobot).envoyerDonnees(data);
            } else if (e.getSource() == loadButton && currentRobot instanceof RobotLivraison) {
                String destination = loadDestinationField.getText();
                if (destination != null && !destination.trim().isEmpty()) {
                    ((RobotLivraison) currentRobot).chargerColis(destination);
                    colisChargeValide = true;
                } else {
                    JOptionPane.showMessageDialog(this, "La destination du colis ne peut pas être vide.", "Erreur de Chargement", JOptionPane.ERROR_MESSAGE);
                    colisChargeValide = false;
                }
            } else if (e.getSource() == deliverButton && currentRobot instanceof RobotLivraison && ((RobotLivraison) currentRobot).getColisActuel() != null && colisChargeValide) {
                String xStr = JOptionPane.showInputDialog(this, "Entrez la coordonnée X de la destination :", "Destination de Livraison", JOptionPane.QUESTION_MESSAGE);
                String yStr = JOptionPane.showInputDialog(this, "Entrez la coordonnée Y de la destination :", "Destination de Livraison", JOptionPane.QUESTION_MESSAGE);
                try {
                    int destinationX = Integer.parseInt(xStr);
                    int destinationY = Integer.parseInt(yStr);
                    ((RobotLivraison) currentRobot).faireLivraison(destinationX, destinationY);
                    robotDrawingPanel.repaint();
                    colisChargeValide = false;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Coordonnées de livraison invalides.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }

            } else if (e.getSource() == rechargeButton) {
                currentRobot.recharger(100-currentRobot.getEnergie());
                currentRobot.setSourceRecharge("Standard");
            }
            else if (e.getSource() == rechargeEcoButton) {
                currentRobot.recharger(100-currentRobot.getEnergie());
                currentRobot.setSourceRecharge("Écologique");
            }
            else if (e.getSource() == optimiseRouteButton && currentRobot instanceof RobotLivraison && colisChargeValide) {
                ((RobotLivraison) currentRobot).setOptimiserItineraire(!((RobotLivraison) currentRobot).isOptimiserItineraire());
                optimiseRouteButton.setText(((RobotLivraison) currentRobot).isOptimiserItineraire() ? "Optimisation ON" : "Optimiser Route");
            }
            else if (e.getSource() == fastDeliveryButton && currentRobot instanceof RobotLivraison && colisChargeValide) {
                ((RobotLivraison) currentRobot).setLivraisonRapide(!((RobotLivraison) currentRobot).isLivraisonRapide());
                fastDeliveryButton.setText(((RobotLivraison) currentRobot).isLivraisonRapide() ? "Rapide ON" : "Livraison Rapide");
            }
            else if (e.getSource() == checkUpdateButton && currentRobot instanceof RobotConnecte) {
                ((RobotConnecte) currentRobot).verifierMiseAJour();
            } else if (e.getSource() == applyUpdateButton && currentRobot instanceof RobotConnecte) {
                ((RobotConnecte) currentRobot).appliquerMiseAJour();
            }
            else if (e.getSource() == preferredZoneField && currentRobot instanceof RobotLivraison) {
                ((RobotLivraison) currentRobot).setZonePreferee(preferredZoneField.getText());
            } else if (e.getSource() == forceVeilleButton) {
                if (currentRobot != null && currentRobot.getEnMarche() && !currentRobot.isEnVeille()) {
                    currentRobot.setEnVeille(true);
                }
            }
        } catch (RobotException ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            updateActionHistory();
            enableOrDisableButtons();
            updateRobotDrawing();
            updateStatusPanel();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RobotGUI());
    }
}