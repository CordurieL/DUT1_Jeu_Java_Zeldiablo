package Apps;

/**
 * classe abstraite qui represente une entite
 */
public abstract class Entite {
    /**
     * attributs d une entite ses coordonnees, les pv, et un boolean qui determine si l entite est morte ou vivante
     */
    private int posX, posY, pv;
    private boolean mort;

    /**
     * constructur d'une entite qui prend en parametre les coordonnees de l entite ainsi que sa vie
     * initialise le boolean mort à false
     * 
     * @param x
     * @param y
     * @param vie
     */
    public Entite(int x, int y, int vie) {
        this.posX = x;
        this.posY = y;
        this.pv = vie;
        this.mort = false;
    }

    /**
     * methode abstraite qui permet d attaquer une entite
     * 
     * @param victime
     */
    public abstract void attaquer(Entite victime);

    public abstract int getPortee();

    /**
     * methode abstraite qui permet de decrementer les pv d une entite
     * 
     * @param dgt nombre de pv perdu
     */
    public abstract void prendreDegats(int dgt);

    /**
     * deplacer l entite a des coordonnees x et y
     * 
     * @param x abscisse
     * @param y ordonnee
     */
    public void deplacer(int x, int y) {
        if (!mort) {
            this.posX += x;
            this.posY += y;
        }
    }

    /**
     * methode qui permet d'obtenir la coordonnee x du personnage
     * 
     * @return coordonnee x
     */
    public int getX() {
        return this.posX;
    }

    /**
     * methode qui permet d'obtenir la coordonnee y du personnage
     * 
     * @return coordonnee y
     */
    public int getY() {
        return this.posY;
    }

    /**
     * methode qui permet d'obtenir la vie actuelle du personnage
     * 
     * @return pv
     */
    public int getPv() {
        return this.pv;
    }

    /**
     * getDistance retourne la distance entre un monstre et le joueur
     * 
     * @param monstre
     * @return
     */
    public int getDistance(Entite entite) {
        int distanceX = Math.abs(this.getX() - entite.getX());
        int distanceY = Math.abs(this.getY() - entite.getY());
        return (distanceX + distanceY);
    }

    /**
     * methode qui permet de savoir si le personnage est mort si le boolean est true
     * et vivant si il est false
     * 
     * @return mort
     */
    public boolean etreMort() {
        return this.mort;
    }

    /**
     * methode qui permet d'incrementer les pv de l entite
     *
     * @param vie
     */
    public void setPv(int vie) {
        this.pv = vie;
    }

    /**
     * methode qui permet de changer l etat du boolean mort
     * 
     * @param etat
     */
    public void setMort(boolean etat) {
        this.mort = etat;
    }
}