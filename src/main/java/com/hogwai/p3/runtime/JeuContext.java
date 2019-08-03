package com.hogwai.p3.runtime;

import com.hogwai.p3.joueur.IAHandler;
import com.hogwai.p3.joueur.UtilisateurHandler;
import com.hogwai.p3.mode.Mode;
import com.hogwai.p3.mode.StrategyMode;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * JeuContext: gestion du jeu
 * <ul>
 *     <li>Tâches:</li>
 *     <li>
 *         <ul>
 *             <li>Récupération des propriétés du fichier config.properties</li>
 *             <li>Lancement du mode (strategy) choisi par l'utilisateur</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * @author Lilian
 */
public class JeuContext {
    private static final Logger LOGGER = LogManager.getLogger(JeuContext.class.getName());

    /**
     * Stratégie choisie par l'utilisateur
     * @see StrategyMode
     * @see JeuContext#setStrategyMode(StrategyMode)
     */
    private StrategyMode strategyMode;

    /**
     * Nombre de chiffres autorisés dans une combinaison
     * @see JeuContext#getNbCombinaison()
     */
    private String nbCombinaison;

    /**
     * Nombre d'essais dont dispose le joueur (IA/Utilisateur)
     */
    private String nbEssais;

    /**
     * Booléen indiquant l'activation ou non du mode développeur
     * @see JeuContext#setModeDev(boolean)
     * @see JeuContext#isModeDev()
     */
    private boolean modeDev;

    /**
     * Booléen indiquant la volonté de l'utilisateur de rejouer
     * @see JeuContext#setPlayAgain(boolean)
     * @see JeuContext#isPlayAgain()
     */
    private boolean playAgain;

    /**
     * Choix de l'utilisateur quand il rejoue
     * @see JeuContext#getPlayAgainChoice()
     */
    private String playAgainChoice;


    /**
     * Constructeur surchargé JeuContext
     * @param modeDev Mode développeur
     * @see JeuContext#playAgain
     * @see JeuContext#playAgainChoice
     * @see JeuContext#nbCombinaison
     * @see JeuContext#nbEssais
     * @see JeuContext#modeDev
     */
    public JeuContext(Boolean modeDev) {
        ResourceBundle properties = this.getProperties();
        this.playAgain = false;
        this.playAgainChoice = "2";
        this.nbCombinaison = properties.getString("nbCombinaison");
        this.nbEssais = properties.getString("nbEssais");
        this.modeDev = modeDev ? modeDev : Boolean.parseBoolean(properties.getString("modeDev"));
        LOGGER.info("Lancement d'une instance de jeu");
        if (this.modeDev) {
            LOGGER.debug(String.format("Nombre de chiffres dans une combinaison: %s", this.nbCombinaison));
            LOGGER.debug(String.format("Nombre d'essais: %s", this.nbEssais));
            LOGGER.debug(String.format("Mode développeur: %s", this.modeDev ? "activé" : "désactivé"));
        }

    }

    /**
     * Définit la stratégie
     * @param strategyMode Stratégie
     * @see StrategyMode
     */
    public void setStrategyMode(StrategyMode strategyMode) {
        this.strategyMode = strategyMode;
    }

    /**
     * Retourne les propriétés extraites du fichier config.properties
     * @return Bundle contenant les properties
     * @see ResourceBundle
     */
    public ResourceBundle getProperties() {
        try {
            ResourceBundle.getBundle("config");
        } catch (MissingResourceException ex) {
            LOGGER.error(String.format("Une erreur a été levée lors de la récupération des properties:%s", ex));
            return null;
        }
        return ResourceBundle.getBundle("config");
    }

    /**
     * Définit l'activation du mode développeur
     * @param modeDev Mode développeur
     */
    public void setModeDev(boolean modeDev) {
        this.modeDev = modeDev;
    }

    /**
     * Détermine si le mode développeur est activé
     * @return Mode développeur
     */
    public boolean isModeDev() {
        return modeDev;
    }

    /**
     * Définit la volonté de l'utilisateur de rejouer
     * @param playAgain Booléen
     */
    public void setPlayAgain(boolean playAgain) {
        this.playAgain = playAgain;
    }

    /**
     * Retourne la volonté de l'utilisateur de rejouer
     * @return Booléen
     */
    public boolean isPlayAgain() {
        return playAgain;
    }

    /**
     * Retourne le choix du mode de l'utilisateur voulant rejouer
     * @return Choix de l'utilisateur
     */
    public String getPlayAgainChoice() {
        return playAgainChoice;
    }

    /**
     * Retourne le nombre de chiffres autorisés pour une combinaison
     * @return nombre de chiffres autorisés dans une combinaison
     */
    public String getNbCombinaison() {
        return nbCombinaison;
    }

    /**
     * Affiche le menu selon le mode choisi
     */
    public void afficherMenu() {
        LOGGER.info("Affichage du menu");
        this.strategyMode.afficherMenuMode();
    }

    /**
     * Demande à l'utilisateur de choisir un mode, récupère et retourne sa saisie
     * @return Nombre entier contenant la saisie
     */
    public int demanderChoixMode() {
        LOGGER.info("Choix du mode");
        Scanner sc;
        boolean checkSaisie;
        int choix = 0;
        do {
            System.out.println("Veuillez choisir le mode de jeu:");
            System.out.println("Tapez 1 pour Challenger");
            System.out.println("Tapez 2 pour Défenseur");
            System.out.println("Tapez 3 pour Duel");
            sc = new Scanner(System.in);
            if (sc.hasNextInt()) {
                choix = sc.nextInt();
                if (!(choix < 1 || choix > 3)) {
                    checkSaisie = true;
                } else {
                    checkSaisie = false;
                    System.out.println("Veuillez saisir un entier compris entre 1 et 3 selon le mode choisi.");
                    LOGGER.warn("Mauvaise saisie de l'utilisateur lors du choix du mode");
                }
            } else {
                checkSaisie = false;
                System.out.println("Veuillez saisir un entier compris entre 1 et 3 selon le mode choisi.");
                LOGGER.warn("Mauvaise saisie de l'utilisateur lors du choix du mode");
            }
        } while (!checkSaisie);
        return choix;
    }


    /**
     * Instancie deux joueurs (IA, utilisateur) et lance le jeu
     * @param mode Mode choisi par l'utilisateur
     * @see IAHandler
     * @see UtilisateurHandler
     * @see StrategyMode#jouer(UtilisateurHandler, IAHandler)
     */
    public void lancerPartie(Mode.ModeName mode) {
        LOGGER.info(String.format("Lancement du mode %s", mode));
        IAHandler ia = new IAHandler();
        UtilisateurHandler utilisateur = new UtilisateurHandler(mode);
        System.out.println("Le jeu va se lancer dans 3 secondes...");
        this.timer(3000);
        this.strategyMode.jouer(utilisateur, ia);
    }

    /**
     * Timer
     * @param time durée du timer
     */
    public void timer(Integer time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            LOGGER.warn(String.format("Une erreur est survenue lors du décompte avant le lancement de la boucle de jeu: %s", ex));
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Demande à l'utilisateur s'il veut rejouer et récupère sa saisie
     * @see JeuContext#playAgainChoice
     */
    public void rejouer(){
        Scanner sc;
        boolean checkSaisie;
        do {
            System.out.println("#============================");
            System.out.println("Que voulez-vous faire ?");
            System.out.println("1. Rejouer au même mode");
            System.out.println("2. Lancer un autre mode");
            System.out.println("3. Quitter");
            System.out.print("Votre choix: ");
            sc = new Scanner(System.in);
            if (sc.hasNext()) {
                this.playAgainChoice = sc.next();
                if (this.playAgainChoice.length() == 1
                        && this.playAgainChoice.matches("^[1-3]+$")) {
                    checkSaisie = true;
                } else {
                    checkSaisie = false;
                    System.out.println("Votre choix doit être compris entre 1 et 3.");
                    LOGGER.warn("Mauvaise saisie lors du choix de fin de jeu.");
                }
            } else {
                checkSaisie = false;
                System.out.println("Votre choix doit être compris entre 1 et 3.");
                LOGGER.warn("Mauvaise saisie lors du choix de fin de jeu.");
            }
            System.out.println();
        } while (!checkSaisie);
    }
}
