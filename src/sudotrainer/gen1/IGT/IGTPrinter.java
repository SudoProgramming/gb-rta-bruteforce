package sudotrainer.gen1.IGT;

import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.encounterigt.EncounterIGTResult;
import stringflow.rta.encounterigt.IPrintFunc;
import sudotrainer.gen1.Data.Functions.PrintFunctions;
import sudotrainer.gen1.Data.Predicates.IGTPredicates;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import static sudotrainer.gen1.Data.Functions.DVs.*;
import static sudotrainer.gen1.Data.Functions.DVs.GetAverageSpecialDvsFromMap;

public class IGTPrinter {

    public static void PrintIGTForMap(EncounterIGTMap map, PrintStream outputStream) {
        PrintIGTForMap(map, outputStream, GetDefaultPrintMethods(false, true));
    }

    public static void PrintIGTForMap(EncounterIGTMap map, PrintStream outputStream, boolean shouldPrintNpcTimers, boolean shouldPrintYoloballs) {
        PrintIGTForMap(map, outputStream, GetDefaultPrintMethods(shouldPrintNpcTimers, shouldPrintYoloballs));
    }

    public static void PrintIGTForMap(EncounterIGTMap map) {
        PrintIGTForMap(map, System.out, GetDefaultPrintMethods(false, true));
    }

    public static void PrintIGTForMap(EncounterIGTMap map, boolean shouldPrintNpcTimers, boolean shouldPrintYoloballs) {
        PrintIGTForMap(map, System.out, GetDefaultPrintMethods(shouldPrintNpcTimers, shouldPrintYoloballs));
    }

    public static void PrintIGTForMap(EncounterIGTMap map, PrintStream outputStream, LinkedHashMap<Function<EncounterIGTResult, Boolean>, IPrintFunc> printFunctions) {
        map.forEach(result -> {
                    boolean hasPrinted = false;
                    for (Map.Entry<Function<EncounterIGTResult, Boolean>, IPrintFunc> entry : printFunctions.entrySet()) {
                        Function<EncounterIGTResult, Boolean> conditionalFunction = entry.getKey();
                        IPrintFunc printFunc = entry.getValue();
                        if (conditionalFunction.apply(result)) {
                            if (hasPrinted) {
                                outputStream.print(" | ");
                            }
                            outputStream.print(printFunc.get(result));
                            hasPrinted = true;
                        }
                    }
                    outputStream.println();
                }
        );
    }

    public static void PrintIGTForMap(EncounterIGTMap map, LinkedHashMap<Function<EncounterIGTResult, Boolean>, IPrintFunc> printFunctions) {
        PrintIGTForMap(map, System.out, printFunctions);
    }

    public static LinkedHashMap<Function<EncounterIGTResult, Boolean>, IPrintFunc>  GetDefaultPrintMethods(boolean shouldPrintNpcTimers, boolean shouldYoloballs) {
        LinkedHashMap<Function<EncounterIGTResult, Boolean>, IPrintFunc> printFunctions = new LinkedHashMap<Function<EncounterIGTResult, Boolean>, IPrintFunc>();

        Function<EncounterIGTResult, Boolean> igtCheck = (EncounterIGTResult r) -> {return true;};
        IPrintFunc igtPrint = PrintFunctions.GetIGTPrintFunction();

        Function<EncounterIGTResult, Boolean> hitSpinnerCheck = (EncounterIGTResult r) -> {return r.getSpecies() == 0 && r.getHitSpinner();};
        IPrintFunc hitSpinnerPrint = PrintFunctions.GetHitSpinnerPrintFunction(shouldPrintNpcTimers);

        Function<EncounterIGTResult, Boolean> gotNoEncounterCheck = (EncounterIGTResult r) -> {return r.getSpecies() == 0 && !r.getHitSpinner();};
        IPrintFunc gotNoEncounterPrint = PrintFunctions.GetNoEncounterPrintFunction(shouldPrintNpcTimers);

        Function<EncounterIGTResult, Boolean> gotEncounterCheck = (EncounterIGTResult r) -> {return r.getSpecies() != 0;};
        IPrintFunc gotEncounterPrint = PrintFunctions.GetEncounterPrintFunction(shouldPrintNpcTimers, shouldYoloballs);

        printFunctions.put(igtCheck, igtPrint);
        printFunctions.put(hitSpinnerCheck, hitSpinnerPrint);
        printFunctions.put(gotNoEncounterCheck, gotNoEncounterPrint);
        printFunctions.put(gotEncounterCheck, gotEncounterPrint);

        return printFunctions;
    }

    public static void PrintIGTSummary(EncounterIGTMap map, PrintStream outputStream, String species, String path) {
        PrintIGTSummary(map, outputStream, species, -1, path);
    }

    public static void PrintIGTSummary(EncounterIGTMap map, PrintStream outputStream, String species, int level, String path) {
        EncounterIGTMap filteredEncounterResults;
        EncounterIGTMap filteredYoloballResults;
        if (level >= 0) {
            filteredEncounterResults = map.filter(IGTPredicates.GetSpeciesAndLevelPredicate(species, level));
            filteredYoloballResults = map.filter(IGTPredicates.GetSpeciesLevelAndYoloballPredicate(species, level));
        }
        else {
            filteredEncounterResults = map.filter(IGTPredicates.GetSpeciesPredicate(species));
            filteredYoloballResults = map.filter(IGTPredicates.GetSpeciesAndYoloballPredicate(species));
        }
        outputStream.println(String.format("IGT SUCCESS: %d/60 [YOLOBALL SUCCESS (%d/60)] Average DVs [%s] - PATH: %s", filteredEncounterResults.size(), filteredYoloballResults.size(), ConvertDvs(GetAverageAttackDvsFromMap(filteredEncounterResults), GetAverageDefDvsFromMap(filteredEncounterResults), GetAverageSpeedDvsFromMap(filteredEncounterResults), GetAverageSpecialDvsFromMap(filteredEncounterResults)), path));
    }

    public static void PrintIGTSummary(EncounterIGTMap map, String species, int level, String path) {
        PrintIGTSummary(map, System.out, species, level, path);
    }

    public static void PrintIGTSummary(EncounterIGTMap map, String species, String path) {
        PrintIGTSummary(map, System.out, species, path);
    }
}
