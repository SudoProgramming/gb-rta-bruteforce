package sudotrainer.gen1.MainClasses;

import stringflow.rta.gen1.PokeYellow;
import stringflow.rta.gen1.Gen1Game;
import stringflow.rta.gen1.RbyIGTChecker;
import stringflow.rta.encounterigt.EncounterIGTMap;
import sudotrainer.gen1.IGT.Gen1IGTChecker;
import sudotrainer.gen1.Data.Predicates.IGTPredicates;

public class RunAnIGTCheck {

    public static void main(String[] args) {

        Gen1Game game = new PokeYellow();

        String path = "L L L L L L A L L L D D D D D D L A L L A L L L U U U U R U A U U U R R S_B R R D S_B L L S_B L L D D D D D D D";

        String encounterSpecies = "Mankey";

        long aditionalParameters = RbyIGTChecker.YOLOBALL;

        Gen1IGTChecker igtChecker = new Gen1IGTChecker(game, path, encounterSpecies, aditionalParameters, null);

        EncounterIGTMap map = igtChecker.CheckIGT(0, false);

        igtChecker.GenericPrintForMap(map, IGTPredicates.GetYoloballPredicate(), false, true);

    }
}