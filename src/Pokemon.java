public class Pokemon {
    private int numero;
    private String nom;
    private int type1;
    private int type2;
    private int pv;
    private int att;
    private int def;
    private int vit;
    private int maxHp; // Keep this for the GUI health bar
    private boolean player1; // Keep this for team identification

    public Pokemon(int numero, String nom, int type1, int type2, int pv, int att, int def, int vit, boolean player1) {
        this.numero = numero;
        this.nom = nom;
        this.type1 = type1;
        this.type2 = type2;
        this.pv = pv;
        this.maxHp = pv;
        this.att = att;
        this.def = def;
        this.vit = vit;
        this.player1 = player1;
    }
    

    public void takeDamage(int damage) {
        pv = Math.max(0, pv - damage);
    }

    public void attaque(Pokemon adversaire) {
        // Simplified damage formula from the PDF
        double modificateur = Type.getEfficacite(this.type1, adversaire.type1);
        if (adversaire.type2 != -1) {
            modificateur *= Type.getEfficacite(this.type1, adversaire.type2);
        }
        int degats = (int) (((((((2 * 50) / 5 + 2) * this.att * 50) / adversaire.def) / 50) + 2) * modificateur);
        adversaire.takeDamage(degats);
    }

    @Override
    public String toString() {
        return nom + " (PV: " + pv + "/" + maxHp + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pokemon pokemon = (Pokemon) obj;
        return numero == pokemon.numero && nom.equals(pokemon.nom);
    }

    // Getters
    public String getName() { return nom; }
    public int getHp() { return pv; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return att; }
    public boolean isPlayer1() { return player1; }
    public int getNumero() { return numero; }
    public int getVit() { return vit; }
}
