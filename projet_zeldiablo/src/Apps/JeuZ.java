package Apps;

import moteurJeu.*;

public class JeuZ implements Jeu{

    /**
     * un jeu connait un aventurier
     */
    private Personnage aventurier;
    private Labyrinthe labyrinthe;

    /**
     * constructeur parametre qui permet de creer un jeu 
     * @param p = personnage qui sera l aventurier du jeu
     * ce constructeur lance une erreur si le personnage donne
     * n est pas instancie
     */
    public JeuZ(Personnage p, Labyrinthe l){
        if(p != null){
            this.aventurier = p;
        }
        else{
            throw new Error("Un jeu DOIT connaitre un personnage");
        }
        if(l != null){
            this.labyrinthe = l;
        }
        else{
            throw new Error("Un jeu DOIT connaitre un labyrinthe");
        }
    }

    /**
     * methode qui permet de faire evoluer le personnage
     * @param commande = direction du deplacement
     */
    public void evoluer(Commande commande){

        int xPersoAncien = this.aventurier.getX();
        int yPersoAncien = this.aventurier.getY();

        // si le perso c est bien deplace on gere les pieges
        if(this.deplacerPerso(commande)){

            // on verifie si le personnage etait sur un piege avant de bouger
            if(this.labyrinthe.estUnPiege(xPersoAncien, yPersoAncien)){

                // si c etait le cas on reactive le piege
                this.labyrinthe.activerPiege(xPersoAncien, yPersoAncien);
            }

            // on recupere les coordonnees actuelles du personnage
            int xPersoActuel = this.aventurier.getX();
            int yPersoActuel = this.aventurier.getY();

            // on regarde si le personnage est arrive sur un piege
            if(this.labyrinthe.estUnPiege(xPersoActuel, yPersoActuel)){

                // si on est bien sur un piege on s assure qu il est actif
                if(this.labyrinthe.getEtatPiege(xPersoActuel, yPersoActuel)){

                    // on fait subir des degats au personnage
                    this.aventurier.prendreDegats(1);

                    // puis on pense a desactiver le piege
                    this.labyrinthe.desactiverPiege(xPersoActuel, yPersoActuel);
                }
            }
        }

    }
    
    public boolean deplacerPerso(Commande commande){

        boolean res = false;

        // on recupere les coordonnees de l aventurier
        int xPerso = this.aventurier.getX();
        int yPerso = this.aventurier.getY();
    
        // on verifie que l aventurier puisse bien aller a la case souhaitee
        if (commande.gauche) {
            if (xPerso > 0 && this.labyrinthe.estAccessible(xPerso - 1, yPerso)) {
                this.aventurier.deplacer(-1, 0);
            }
        }
        if (commande.droite) {
            if (xPerso < this.labyrinthe.getTailleX() - 1 && this.labyrinthe.estAccessible(xPerso + 1, yPerso)) {
                this.aventurier.deplacer(1, 0);
            }	
        }
        if (commande.haut) {
            if (yPerso > 0 && this.labyrinthe.estAccessible(xPerso, yPerso - 1)) {
                this.aventurier.deplacer(0, -1);
            }
        }
        if (commande.bas) {
            if (yPerso < this.labyrinthe.getTailleY() - 1 && this.labyrinthe.estAccessible(xPerso, yPerso + 1)) {
                this.aventurier.deplacer(0, 1);
            }
        }

        // on verifie que le deplacement a bien eu lieu
        if(xPerso != this.aventurier.getX() || yPerso != this.aventurier.getY()){
            res = true;
        }
        
        return(res);
    }

    /**
     * methode qui permet de savoir si le jeu est fini
     * pour le moment il ne s arrete pas
     * @return vrai que si le jeu est fini
     */
    public boolean etreFini(){
        return(false);
    }

    /**
     * getter aventurier du jeu
     * @return aventurier du jeu
     */
    public Personnage getAventurier(){
        return(this.aventurier);
    }

    /**
     * getter labyrinthe
     * @return
     */
    public Labyrinthe getLabyrinthe(){
        return(this.labyrinthe);
    }

}