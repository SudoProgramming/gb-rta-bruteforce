package sudotrainer.gen1.MainClasses;

import stringflow.rta.encounterigt.IPrintFunc;
import stringflow.rta.gen1.PokeYellow;
import stringflow.rta.gen1.Gen1Game;
import stringflow.rta.gen1.RbyIGTChecker;
import stringflow.rta.encounterigt.EncounterIGTMap;
import sudotrainer.gen1.Data.Functions.PrintFunctions;
import sudotrainer.gen1.IGT.Gen1IGTChecker;
import sudotrainer.gen1.Data.Predicates.IGTPredicates;
import sudotrainer.gen1.IGT.IGTPrinter;

public class RunAnIGTCheck {

    public static void main(String[] args) {

        Gen1Game game = new PokeYellow();

        String path = "U S_B U S_B U";

        String encounterSpecies = "Clefairy";

        long aditionalParameters = RbyIGTChecker.YOLOBALL;

        Gen1IGTChecker igtChecker = new Gen1IGTChecker(game, path, encounterSpecies, aditionalParameters, null);

        EncounterIGTMap map = igtChecker.CheckIGT(5, false);

        IGTPrinter.PrintIGTSummary(map, encounterSpecies, path);
        IGTPrinter.PrintIGTForMap(map);
    }
}