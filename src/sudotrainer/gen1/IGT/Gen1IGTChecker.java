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

    PrintStream printer;

    boolean shouldPrintNpcTimers = false;

    boolean shouldYoloballs = true;

    LinkedHashMap<Function<EncounterIGTResult, Boolean>, IPrintFunc> printMethods;

    public Gen1IGTChecker(Gen1Game game, String path) {

    }

    public Gen1IGTChecker(Gen1Game game, String path, String encounterName, long aditionalParameters, PrintStream outputStream) {
        this.game = game;
        walkingPath = path;
        encounterSpecies = encounterName;
        fullParameters = aditionalParameters | game.getSpecies(encounterSpecies.toUpperCase()).getIndexNumber();
        if (outputStream == null) {
            printer = System.out;
        }
        else {
            this.printer = outputStream;
        }
        printMethods = new LinkedHashMap<Function<EncounterIGTResult, Boolean>, IPrintFunc>();
    }

    public void addDefaultPrintMethods() {
        Function<EncounterIGTResult, Boolean> igtCheck = (EncounterIGTResult r) -> {return true;};
        IPrintFunc igtPrint = result -> String.format("[%d][%d] ", result.getIgt().getSeconds(), result.getIgt().getFrames());
        Function<EncounterIGTResult, Boolean> hitSpinnerCheck = (EncounterIGTResult r) -> {return r.getSpecies() == 0 && r.getHitSpinner();};
        IPrintFunc hitSpinnerPrint = result -> String.format("Hit spinner at [%d#%d,%d]; rng %s %s", result.getMap(), result.getX(), result.getY(), result.getRNG(), shouldPrintNpcTimers ? "npctimers " + result.getNpcTimers() : "");
        Function<EncounterIGTResult, Boolean> gotNoEncounterCheck = (EncounterIGTResult r) -> {return r.getSpecies() == 0 && !r.getHitSpinner();};
        IPrintFunc gotNoEncounterPrint = result -> String.format("No encounter at [%d#%d,%d]; rng %s %s", result.getMap(), result.getX(), result.getY(), result.getRNG(), shouldPrintNpcTimers ? "npctimers " + result.getNpcTimers() : "");
        Function<EncounterIGTResult, Boolean> gotEncounterCheck = (EncounterIGTResult r) -> {return r.getSpecies() != 0;};
        IPrintFunc gotEncounterPrint = result -> String.format("Encounter at [%d#%d,%d]: %s%s lv%d DVs %04X rng %s %s %s %s %s %s", result.getMap(), result.getX(), result.getY(), result.getGender() == Gender.GENDERLESS ? "" : result.getGender().getName() + " ", result.getSpeciesName(), result.getLevel(), result.getHexDVs(), result.getRNG(), shouldYoloballs ? String.valueOf(result.getYoloball()) : "", shouldYoloballs ? String.valueOf(result.getSelectYoloball()) : "", shouldYoloballs ? String.valueOf(result.getRedbarYoloball()) : "", shouldYoloballs ? String.valueOf(result.getRedbarYoloball()) : "", shouldPrintNpcTimers ? "npctimers " + result.getNpcTimers() : "");
        printMethods.put(igtCheck, igtPrint);
        printMethods.put(hitSpinnerCheck, hitSpinnerPrint);
        printMethods.put(gotNoEncounterCheck, gotNoEncounterPrint);
        printMethods.put(gotEncounterCheck, gotEncounterPrint);
    }

    public void addAPrintMethod(Function<EncounterIGTResult, Boolean> functionCall, IPrintFunc printFunc) {
        printMethods.put(functionCall, printFunc);
    }

    public EncounterIGTMap CheckIGT(int renderScale, boolean withInputDisplay) {
        Gb gb = GBController.loadGameboy(game, renderScale, withInputDisplay);
        ArrayList<IGTState> initialStates = gb.getInitialStates();

        RbyIGTChecker igtChecker = new RbyIGTChecker(gb);

        EncounterIGTMap map = igtChecker.checkIGT0(initialStates, walkingPath, fullParameters);
        gb.destroy();
        return map;
    }

    public void GenericPrintForMap(EncounterIGTMap map, Predicate<EncounterIGTResult> filters, boolean writeNpcTimers, boolean writeYoloballs) {
        if (filters != null) {
            EncounterIGTMap successMap = map.filter(filters);
        }

        addDefaultPrintMethods();
        map.print(printer, printMethods);
        //map.print(printer, writeNpcTimers, writeYoloballs);
        //map.print(printer, );
        //System.out.println(successMap.size() + "/60" + "- PATH: " + walkingPath);
    }

    public void setShouldPrintNpcTimers(boolean should) {
        shouldPrintNpcTimers = should;
    }

    public void setShouldPrintYoloballs(boolean should) {
        shouldYoloballs = should;
    }
}
