import java.util.Arrays;
import java.util.List;

public class Type {

    // As per the PDF, this class will hold data and static methods.
    // I will use placeholder data for now.

    public static final int NORMAL = 0;
    public static final int FEU = 1;
    public static final int EAU = 2;
    public static final int PLANTE = 3;
    public static final int SPECTRE = 4;
    public static final int POISON = 5;
    public static final int COMBAT = 6;


    private static final List<String> NOMS_POKEMON = Arrays.asList(
        "Bulbizarre", "Herbizarre", "Florizarre", "Salamèche", "Reptincel", "Dracaufeu",
        // ... and so on for all 151 pokemon
        "Mewtwo", "Mew"
    );

    private static final List<String> NOMS_TYPE = Arrays.asList(
        "Normal", "Feu", "Eau", "Plante", "Spectre", "Poison", "Combat"
    );

    // Simplified effectiveness chart: Attacker -> Defender -> Multiplier
    private static final double[][] TABLE_EFFICACITE = {
        //          NOR  FEU  EAU  PLA  SPE  POI  COM
        /*NOR*/   { 1.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0 },
        /*FEU*/   { 1.0, 0.5, 0.5, 2.0, 1.0, 1.0, 1.0 },
        /*EAU*/   { 1.0, 2.0, 0.5, 0.5, 1.0, 1.0, 1.0 },
        /*PLANTE*/{ 1.0, 0.5, 2.0, 0.5, 1.0, 0.5, 1.0 },
        /*SPECTRE*/ { 0.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0 },
        /*POISON*/{ 1.0, 1.0, 1.0, 2.0, 0.5, 0.5, 1.0 },
        /*COMBAT*/{ 2.0, 1.0, 1.0, 0.5, 0.0, 0.5, 1.0 }
    };

    public static String getNomPokemon(int numero) {
        // Adjusting for 1-based pokedex number
        if (numero > 0 && numero <= NOMS_POKEMON.size()) {
            return NOMS_POKEMON.get(numero - 1);
        }
        return "Inconnu";
    }

    public static String getNomType(int type) {
        if (type >= 0 && type < NOMS_TYPE.size()) {
            return NOMS_TYPE.get(type);
        }
        return "Inconnu";
    }

    public static double getEfficacite(int typeAtt, int typeDef) {
        if (typeAtt >= 0 && typeAtt < TABLE_EFFICACITE.length &&
            typeDef >= 0 && typeDef < TABLE_EFFICACITE[typeAtt].length) {
            return TABLE_EFFICACITE[typeAtt][typeDef];
        }
        return 1.0;
    }
}