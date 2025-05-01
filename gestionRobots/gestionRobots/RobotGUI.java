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
    private JTextArea robotInfo; // Ne sera plus affiché séparément
    private JTextArea actionHistory;
    private JButton startButton, stopButton, moveButton, connectButton, disconnectButton, sendDataButton, loadButton, deliverButton; // taskButton supprimé
    private JTextField moveXField, moveYField, connectNetworkField, sendDataField, loadDestinationField;
    private JPanel robotControlPanel; // Panel pour les boutons de démarrage et d'arrêt
    private JPanel robotDrawingPanel; // Panel pour dessiner le robot
    private JPanel actionPanel; // Panel pour les autres boutons d'action

    public RobotGUI() {
        setTitle("Gestionnaire de Robots Écologiques");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel pour la sélection du robot (en haut)
        JPanel selectionPanel = new JPanel();
        JLabel robotLabel = new JLabel("Sélectionner un robot :");
        robotList = new JComboBox<>();
        selectionPanel.add(robotLabel);
        selectionPanel.add(robotList);
        add(selectionPanel, BorderLayout.NORTH);

        // Panel pour le dessin du robot (au centre)
        robotDrawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRobot(g);
            }
        };
        robotDrawingPanel.setBackground(Color.WHITE);
        add(robotDrawingPanel, BorderLayout.CENTER);

        // Panel pour les boutons de démarrage et d'arrêt (au sud du centre, au-dessus de l'historique)
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

        // Panel pour les autres boutons d'action et les champs de saisie (au nord du centre)
        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        moveButton = new JButton("Déplacer vers (x, y) :");
        moveXField = new JTextField(5);
        moveYField = new JTextField(5);
        connectButton = new JButton("Connecter à :");
        connectNetworkField = new JTextField(10);
        disconnectButton = new JButton("Déconnecter");
        sendDataButton = new JButton("Envoyer données :");
        sendDataField = new JTextField(20);
        loadButton = new JButton("Charger colis pour :");
        loadDestinationField = new JTextField(15);
        deliverButton = new JButton("Livrer Colis"); // Bouton Livrer

        moveButton.addActionListener(this);
        connectButton.addActionListener(this);
        disconnectButton.addActionListener(this);
        sendDataButton.addActionListener(this);
        loadButton.addActionListener(this);
        deliverButton.addActionListener(this);

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
        actionPanel.add(deliverButton); // Ajout du bouton Livrer

        // Panel pour l'historique des actions (en bas)
        actionHistory = new JTextArea(15, 60); // Augmenter la taille pour potentiellement afficher plus d'informations
        JScrollPane historyScrollPane = new JScrollPane(actionHistory);

        // Réorganisation pour placer le dessin au centre, les boutons start/stop au-dessus de l'historique
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(actionPanel, BorderLayout.NORTH);
        centerPanel.add(robotDrawingPanel, BorderLayout.CENTER);
        centerPanel.add(robotControlPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
        add(historyScrollPane, BorderLayout.SOUTH); // Historique en bas

        // Création de robots pour l'interface
        RobotLivraison livreurGUI = new RobotLivraison("LVR-GUI-001", 20, 30); // Position initiale (20, 30)
        robots.add(livreurGUI);
        updateRobotList();
        updateRobotInfo(); // L'état sera affiché dans l'historique et sur le dessin
        updateActionHistory();
        robotDrawingPanel.repaint();

        setVisible(true);
    }

    private void updateRobotList() {
        robotList.removeAllItems();
        for (Robot robot : robots) {
            robotList.addItem(robot.getId());
        }
    }

    private void updateRobotInfo() {
        Robot selectedRobot = getSelectedRobot();
        if (selectedRobot != null) {
            // L'état est maintenant potentiellement affiché dans l'historique et sur le dessin
            updateDeliverButtonState();
        }
    }

    private void updateActionHistory() {
        Robot selectedRobot = getSelectedRobot();
        if (selectedRobot != null) {
            actionHistory.setText(selectedRobot.getHistorique());
        } else {
            actionHistory.setText("Historique non disponible.");
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
        Robot selectedRobot = getSelectedRobot();
        if (selectedRobot != null) {
            int x = selectedRobot.getX(); // Récupérer la position X du robot
            int y = selectedRobot.getY(); // Récupérer la position Y du robot
            int size = 80;

            g.setColor(new Color(100, 149, 237));
            g.fillRect(x, y, size, size);

            g.setColor(new Color(169, 169, 169));
            g.fillOval(x + size / 4, y - size / 3, size / 2, size / 3);

            g.setColor(Color.YELLOW);
            g.fillOval(x + size / 3, y - size / 6, size / 6, size / 6);
            g.fillOval(x + size / 2, y - size / 6, size / 6, size / 6);

            if (selectedRobot.getEnMarche()) {
                g.setColor(Color.GREEN);
                g.fillOval(x - 15, y - 15, 15, 15);
            } else {
                g.setColor(Color.RED);
                g.fillOval(x - 15, y - 15, 15, 15);
            }

            g.setColor(Color.BLACK);
            if (selectedRobot instanceof RobotLivraison) {
                if (((RobotLivraison) selectedRobot).isEnLivraison()) {
                    g.drawString("Livraison...", x, y + size + 20);
                } else if (((RobotLivraison) selectedRobot).getColisActuel() != null) {
                    g.drawString("Chargé", x, y + size + 20);
                }
            }
            if (selectedRobot instanceof RobotConnecte && ((RobotConnecte) selectedRobot).isConnecte()) {
                g.drawString("Connecté", x, y - 30);
            }
            g.drawString("X: " + selectedRobot.getX() + ", Y: " + selectedRobot.getY(), x, y + size + 40);
            g.drawString("Energie: " + selectedRobot.getEnergie() + "%", x, y + size + 60);

        } else {
            g.setColor(Color.GRAY);
            g.drawString("Aucun robot sélectionné", robotDrawingPanel.getWidth() / 2 - 80, robotDrawingPanel.getHeight() / 2);
        }
    }

    private void updateDeliverButtonState() {
        Robot selectedRobot = getSelectedRobot();
        if (selectedRobot instanceof RobotLivraison && ((RobotLivraison) selectedRobot).getColisActuel() != null && selectedRobot.getEnMarche()) {
            deliverButton.setEnabled(true);
        } else {
            deliverButton.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Robot selectedRobot = getSelectedRobot();
        if (selectedRobot == null) return;

        try {
            if (e.getSource() == startButton) {
                selectedRobot.demarrer();
            } else if (e.getSource() == stopButton) {
                selectedRobot.arreter();
            } else if (e.getSource() == moveButton) {
                try {
                    int x = Integer.parseInt(moveXField.getText());
                    int y = Integer.parseInt(moveYField.getText());
                    selectedRobot.deplacer(x, y); // Mettre à jour la position logique du robot
                    robotDrawingPanel.repaint(); // Redessiner le robot à sa nouvelle position
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Coordonnées de déplacement invalides.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else if (e.getSource() == connectButton && selectedRobot instanceof RobotConnecte) {
                String network = connectNetworkField.getText();
                ((RobotConnecte) selectedRobot).connecter(network);
            } else if (e.getSource() == disconnectButton && selectedRobot instanceof RobotConnecte) {
                ((RobotConnecte) selectedRobot).deconnecter();
            } else if (e.getSource() == sendDataButton && selectedRobot instanceof RobotConnecte) {
                String data = sendDataField.getText();
                ((RobotConnecte) selectedRobot).envoyerDonnees(data);
            } else if (e.getSource() == loadButton && selectedRobot instanceof RobotLivraison) {
                String destination = loadDestinationField.getText();
                ((RobotLivraison) selectedRobot).chargerColis(destination);
                updateDeliverButtonState();
            } else if (e.getSource() == deliverButton && selectedRobot instanceof RobotLivraison) {
                String xStr = JOptionPane.showInputDialog(this, "Entrez la coordonnée X de la destination :", "Destination de Livraison", JOptionPane.QUESTION_MESSAGE);
                String yStr = JOptionPane.showInputDialog(this, "Entrez la coordonnée Y de la destination :", "Destination de Livraison", JOptionPane.QUESTION_MESSAGE);
                try {
                    int destinationX = Integer.parseInt(xStr);
                    int destinationY = Integer.parseInt(yStr);
                    ((RobotLivraison) selectedRobot).faireLivraison(destinationX, destinationY); // Mettre à jour la position logique
                    robotDrawingPanel.repaint(); // Redessiner le robot à sa nouvelle position
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Coordonnées de livraison invalides.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (RobotException ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            updateRobotInfo();
            updateActionHistory();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RobotGUI());
    }
}