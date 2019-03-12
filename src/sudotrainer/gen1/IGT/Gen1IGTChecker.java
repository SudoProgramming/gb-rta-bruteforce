package sudotrainer.gen1.IGT;

import stringflow.rta.Gender;
import stringflow.rta.IGTState;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.encounterigt.EncounterIGTResult;
import stringflow.rta.encounterigt.IPrintFunc;
import stringflow.rta.gen1.Gen1Game;
import stringflow.rta.gen1.RbyIGTChecker;
import stringflow.rta.libgambatte.Gb;
import sudotrainer.gen1.GB.GBController;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Gen1IGTChecker {
    Gen1Game game;

    String walkingPath;

    String encounterSpecies;

    long fullParameters;

    public Gen1IGTChecker(Gen1Game game, String path) {

    }

    public Gen1IGTChecker(Gen1Game game, String path, String encounterName, long aditionalParameters, PrintStream outputStream) {
        this.game = game;
        walkingPath = path;
        encounterSpecies = encounterName;
        fullParameters = aditionalParameters | game.getSpecies(encounterSpecies.toUpperCase()).getIndexNumber();
    }

    public EncounterIGTMap CheckIGT(int renderScale, boolean withInputDisplay) {
        Gb gb = GBController.loadGameboy(game, renderScale, withInputDisplay);
        ArrayList<IGTState> initialStates = gb.getInitialStates();

        RbyIGTChecker igtChecker = new RbyIGTChecker(gb);

        EncounterIGTMap map = igtChecker.checkIGT0(initialStates, walkingPath, fullParameters);
        gb.destroy();
        return map;
    }
}
