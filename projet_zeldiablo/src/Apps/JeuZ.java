package Apps;

import moteurJeu.*;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.InterruptedException;
import java.lang.Thread;
import java.util.ArrayList;

/**
 * Cette classe represente le jeu Zeldiablo, il possede personnage, monstres et
 * labyrinthe. En implementant l'interface Jeu, il gere les actions se deroulant
 * au sein du jeu.
 * 
 * les monstres sont places de maniere aleatoire dans la labyrinthe tandis que le 
 * personnage est place dans la premiere case libre en bas du labyrinthe
 */
public class JeuZ implements Jeu {

    /**
     * un jeu connait un aventurier, une liste de monstres, un labyrinthe et un
     * boolean actionEnCours pour limiter le nombre de deplacements par seconde
     */
    private Personnage aventurier;
    private Labyrinthe labyrinthe;
    private boolean actionEnCours;
    private ArrayList<Monstre> listDeMonstres;
    private final int coolDownTime = 250;

    /**
     * constructeur parametre qui permet de creer un jeu
     * le jeu se debrouille pour placer les monstres et le personnage
     * 
     * @param l = le labyrinthe dans lequel evolue les personnages et les monstres
     * il y a une erreur si le labyrinthe n est pas instancie
     */
    public JeuZ(Labyrinthe l){

        // on prepare la liste de monstres du labyrinthe
        this.listDeMonstres = new ArrayList<Monstre>();
        
        // on s assure que le labyrinthe existe
        // si ce n est pas le cas on fait planter toute l application
        // pour eviter des problemes
        if(l != null){
            this.labyrinthe = l;
        } 
        else{
            throw new Error("Un jeu DOIT connaitre un labyrinthe");
        }

        // on essaye de placer le personage
        this.placerPersonnage();

        if(this.aventurier == null){
            throw new Error("Le jeu n a pas pu placer son personnage");
        }

        // on place les monstres de maniere aleatoire dans le labyrinthe
        this.placerMonstres();

        // on precise qu il n y a pas encore eu d action en cour
        actionEnCours = false;
    }

    /**
     * constructeur parametre utile uniquement pour les tests
     * @param p = personnage a placer manuellement dans le labyrinthe
     * @param l = labyrinthe a tester
     */
    public JeuZ(Personnage p, Labyrinthe l){

        // on s assure qu il y a bien un perso
        if(p != null){
            this.aventurier = p;
        }
        else{
            throw new Error("Le jeu DOIT connaitre un peronnage");
        }

        // on s assure qu il y a bien un labyrinthe
        if(l != null){
            this.labyrinthe = l;
        } 
        else{
            throw new Error("Un jeu DOIT connaitre un labyrinthe");
        }

        // on precise qu il n y a pas encore d action en cour
        this.actionEnCours = false;

        // on ajoute les monstres a la liste de monstres du jeu
        listDeMonstres = new ArrayList<Monstre>();
        listDeMonstres.add(new Troll(18, 2));
        listDeMonstres.add(new Troll(1, 18));
        listDeMonstres.add(new Troll(1, 2));
        listDeMonstres.add(new Troll(12, 16));
    }

    /**
     * methodes utiles au constructeur uniquement
     */

    /**
     * methode qui permet de placer de maniere autonome le personnage dans le labyrinthe
    */
    private void placerPersonnage(){

        // on recupere les dimensions du labirynthe
        int tailleX = this.labyrinthe.getTailleX();
        int tailleY = this.labyrinthe.getTailleY();

        // on prepare un boolean et un int pour arreter la boucle des que l on trouve
        // une case libre
        boolean fin = false;
        int i = 0;

        // on parcours les abscisses disponibles pour trouver la premiere case libre 
        // en bas du labyrinthe
        while(!fin && i < tailleX){

            // des que l on trouve la premiere case libre en bas du labyrinthe
            if(this.labyrinthe.estAccessible(i, tailleY - 1)){
                fin = true;
                this.aventurier = new Personnage(i, tailleY - 1, 15);
            }

            i++;
        }
        
    }

    /**
     * methode qui permet au jeu de placer tout seul ses monstres de maniere aleatoire
     * dans le labyrinthe du jeu
     */
    private void placerMonstres(){

        // on recupere les dimensions du labyrinthe
        int tailleX = this.labyrinthe.getTailleX();
        int tailleY = this.labyrinthe.getTailleY();

        //int typeMonstre;

        // on essaye de placer 4 monstres dans le labyrinthe
        for(int i = 0; i < 4; i++){
            //on prepare des coordonnees aleatoires
            int x = (int)(Math.random() * tailleX);
            int y = (int)(Math.random() * tailleY);

            // tant qu on ne peut pas placer de monstre on reessaye de trouver
            // un nouvelle case pour placer le monstre
            while(!this.labyrinthe.estAccessible(x, y)){
                x = (int)(Math.random() * tailleX);
                y = (int)(Math.random() * tailleY);
            }

            /*
            // on met un type de monstre aleatoire
            typeMonstre = (int)(Math.random() * 4);
            */

            // on ajoute ce monstre
            this.listDeMonstres.add(new Troll(x, y));
        }

    }

    /**
     * methode qui permet de faire evoluer le personnage et les monstres
     * 
     * @param commande = direction du deplacement pour le personnage ou action
     *                 (attaque, ramassage...)
     */
    public void evoluer(Commande commande){

        /*
         * le code ne s'execute que si actionEnCours vaut faux, auquel cas,
         * actionEnCours vaut vrai durant un temps (coolDownTime)
         */
        if (!actionEnCours) {
            actionEnCours = true;
            
            // si le perso s est bien deplace on gere les pieges
            if(this.deplacerPerso(commande)){
                this.arriveSurUnPiege(this.aventurier);
            }
    
            // si on tente de prendre une arme, on essaye de prendre celle de la case 
            // sur la quelle on est
            if(commande.prendreArme){
                this.tentePrendreArme();
            }
    
            // si on tente d attaquer, on va essayer d attaquer les entites a portee
            if (commande.attaquer) {
                this.tenteAttaquePerso();
            }
    
            // Pour chaque monstre, s'il se trouve a 1 de distance du joueur, il l'attaque
            for(int i = 0; i < listDeMonstres.size(); i++){
    
                if(listDeMonstres.get(i).getDistance(this.aventurier) == 1){
                    listDeMonstres.get(i).attaquer(this.aventurier);
                }
    
                // Si le monstre est mort, il est retire de la liste
                if(listDeMonstres.get(i).etreMort()){
                    listDeMonstres.remove(i);
                }
            }
    
            // remet actionEnCours a false apres le temps programme (coolDownTime)
            TimerTask timerTask = new CooldownAction();
            Timer timer = new Timer(true);
            timer.schedule(timerTask, 0);
    
            // les monstres se deplacent
            this.deplacerMonstres();
            
        }
    }

    /*
     * Methode faisant se deplacer tout les monstres encore vivants de maniere
     * aleatoire
     */
    public void deplacerMonstres(){

        for(int i = 0; i < listDeMonstres.size(); i++){

            int x = (int) Math.ceil(Math.random() * 4);

            // on recupere les coordonnees de l aventurier
            int xMonstre = listDeMonstres.get(i).getX();
            int yMonstre = listDeMonstres.get(i).getY();

            int xPerso = this.aventurier.getX();
            int yPerso = this.aventurier.getY();

            // on verifie que le monstre puisse bien aller a la case souhaitee
            if(x == 1){

                if(xMonstre > 0 && this.labyrinthe.estAccessible(xMonstre - 1, yMonstre)
                        && ((xMonstre - 1 != xPerso || yMonstre != yPerso))){
                    listDeMonstres.get(i).deplacer(-1, 0);
                }
            }
            else if(x == 2){

                if(xMonstre < this.labyrinthe.getTailleX() - 1 && this.labyrinthe.estAccessible(xMonstre + 1, yMonstre)
                        && ((xMonstre + 1 != xPerso || yMonstre != yPerso))) {
                    listDeMonstres.get(i).deplacer(1, 0);
                }
            } 
            else if(x == 3){

                if(yMonstre > 0 && this.labyrinthe.estAccessible(xMonstre, yMonstre - 1)
                        && ((xMonstre != xPerso || yMonstre - 1 != yPerso))) {
                    listDeMonstres.get(i).deplacer(0, -1);
                }
            } 
            else if(x == 4){

                if(yMonstre < this.labyrinthe.getTailleY() - 1 && this.labyrinthe.estAccessible(xMonstre, yMonstre + 1)
                        && ((xMonstre != xPerso || yMonstre + 1 != yPerso))){
                    listDeMonstres.get(i).deplacer(0, 1);
                }
            }
        }
    }

    /**
     * methode qui permet de deplacer le personnage
     * @param commande = commande utilisateur
     * @return vrai que si le deplacement a eu lieu
     */
    public boolean deplacerPerso(Commande commande) {
        boolean res = false;

        // on recupere les coordonnees de l aventurier
        int xPerso = this.aventurier.getX();
        int yPerso = this.aventurier.getY();

        // on verifie que l aventurier puisse bien aller a la case souhaitee
        boolean ok = true;

        if(commande.gauche){

            if(xPerso > 0 && this.labyrinthe.estAccessible(xPerso - 1, yPerso)){
                
                for(int i = 0; i < listDeMonstres.size(); i++){

                    if((xPerso - 1 == listDeMonstres.get(i).getX() && yPerso == listDeMonstres.get(i).getY())){
                        ok = false;
                    }
                }

                if(ok){
                    this.aventurier.deplacer(-1, 0);
                }
            }
        } 
        else if(commande.droite){

            if(xPerso < this.labyrinthe.getTailleX() - 1 && this.labyrinthe.estAccessible(xPerso + 1, yPerso)){
                
                for(int i = 0; i < listDeMonstres.size(); i++){

                    if((xPerso + 1 == listDeMonstres.get(i).getX() && yPerso == listDeMonstres.get(i).getY())){
                        ok = false;
                    }
                }

                if(ok){
                    this.aventurier.deplacer(1, 0);
                }
            }
        } 
        else if(commande.haut){

            if(yPerso > 0 && this.labyrinthe.estAccessible(xPerso, yPerso - 1)){
                for(int i = 0; i < listDeMonstres.size(); i++){

                    if ((xPerso == listDeMonstres.get(i).getX() && yPerso - 1 == listDeMonstres.get(i).getY())){
                        ok = false;
                    }
                }

                if(ok){
                    this.aventurier.deplacer(0, -1);
                }
            }
        } 
        else if(commande.bas){

            if(yPerso < this.labyrinthe.getTailleY() - 1 && this.labyrinthe.estAccessible(xPerso, yPerso + 1)){

                for(int i = 0; i < listDeMonstres.size(); i++){

                    if ((xPerso == listDeMonstres.get(i).getX() && yPerso + 1 == listDeMonstres.get(i).getY())){
                        ok = false;
                    }
                }

                if(ok){
                    this.aventurier.deplacer(0, 1);
                }
            }
        }

        // on verifie que le deplacement a bien eu lieu
        if(xPerso != this.aventurier.getX() || yPerso != this.aventurier.getY()){
            res = true;
        }

        return(res);
    }

    /**
     * methode qui permet au joueur de tenter de prendre une arme
     */
    public void tentePrendreArme() {

        int x = this.aventurier.getX();
        int y = this.aventurier.getY();

        // si le personnage n a pas d arme on lui donne directement l arme qu il y a sur
        // la case
        if(this.aventurier.getArme() == null){

            Arme a = this.labyrinthe.getArmeCase(x, y);

            // si il y avait bel et bien une arme on la donne au perso et on l enleve de la
            // case
            if(a != null){
                this.aventurier.prendreArme(a);
                this.labyrinthe.retirerArmeCase(x, y);
                a.setPerso(this.aventurier);
                System.out.println("Arme prise avec succes");
            }
        } 
        else{

            // si le personnage a deja une arme
            // soit il la pose par terre soit il switch avec celle de la case sur la quelle
            // il est

            Arme aLab = this.labyrinthe.getArmeCase(x, y);

            // si il y a bel et bien une arme sur la case on swicth l arme du perso et de la
            // case
            if (aLab != null) {
                // depose de l arme du perso
                Arme aPerso = this.aventurier.poserArme();
                aPerso.setPerso(null);

                // recuperation de la nouvelle arme par le perso
                this.aventurier.prendreArme(aLab);
                aLab.setPerso(this.aventurier);
                
                // depose de l ancienne arme du perso sur la case du labyrinthe
                this.labyrinthe.retirerArmeCase(x, y);
                this.labyrinthe.ajouterArmeCase(aPerso, x, y);
                System.out.println("Arme switchee avec succes");
            } 
            else{
                // si il n y a pas d arme le personnage repose son arme
                Arme aPerso = this.aventurier.poserArme();
                aPerso.setPerso(null);
                this.labyrinthe.ajouterArmeCase(aPerso, x, y);
                System.out.println("Arme depose avec succes");
            }
        }
    }

    /**
     * methode qui permet d attaquer les entite proches du personnage
     */
    public void tenteAttaquePerso() {
        // on essaye d attaquer les entites proches
        for(int i = 0; i < this.listDeMonstres.size(); i++){

            if(this.aventurier.getDistance(this.listDeMonstres.get(i)) <= this.aventurier.getPortee()){
                this.aventurier.attaquer(this.listDeMonstres.get(i));
            }
        }
    }

    /**
     * methode qui permet de savoir si une entite arrive sur un piege
     * 
     * @param e = entite a verifier
     */
    public void arriveSurUnPiege(Entite e) {

        // on recupere les coordonnees de l entite
        int xEntite = e.getX();
        int yEntite = e.getY();

        // on regarde si la case est un piege
        if (this.labyrinthe.estUnPiege(xEntite, yEntite)) {
            // si c est le cas l entite prend des degats
            e.prendreDegats(3);
        }

    }

    /**
     * methode qui permet de savoir si le jeu est fini le jeu s arrete si le
     * personnage meurt
     * 
     * ou si il n y a plus de monstre et que l on atteint un bord
     * @return vrai que si le jeu est fini
     */
    public boolean etreFini() {
        boolean res = false;

        if(this.aventurier.etreMort()){
            res = true;
        }

        // on gagne si on atteint une extremite du labyrinthe
        if(this.listDeMonstres.size() == 0 && this.aventurier.getY() == this.labyrinthe.getTailleY() - 1| this.aventurier.getY() == 0){
            res = true;
            System.out.println("Victoire");
        }

        return(res);
    }

    /**
     * getter aventurier du jeu
     * @return aventurier du jeu
     */
    public Personnage getAventurier() {
        return (this.aventurier);
    }

    /**
     * getter labyrinthe
     * @return labyrinthe du jeu
     */
    public Labyrinthe getLabyrinthe() {
        return (this.labyrinthe);
    }

    /**
     * getter de la liste des monstres presents dans le labyrinthe. Seuls les
     * monstres vivants sont dans la liste
     * @return la liste des monstres
     */
    public ArrayList<Monstre> getListeMonstre() {
        return listDeMonstres;
    }

    /** Petite classe permettant de gerer le cooldown entre chaque action */
    class CooldownAction extends TimerTask {
        @Override
        public void run() {
            try {
                Thread.sleep(coolDownTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            actionEnCours = false;
        }
    }
}