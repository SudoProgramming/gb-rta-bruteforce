package sudotrainer.gen1.Data.Functions;

import stringflow.rta.Gender;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.encounterigt.EncounterIGTResult;
import stringflow.rta.encounterigt.IPrintFunc;
import sudotrainer.gen1.Data.Predicates.IGTPredicates;

import java.util.function.Function;

import static sudotrainer.gen1.Data.Functions.DVs.*;

public class PrintFunctions {
    public static IPrintFunc<EncounterIGTResult> GetIGTPrintFunction() {
        return result -> String.format("[%d][%d] ", result.getIgt().getSeconds(), result.getIgt().getFrames());
    }

    public static IPrintFunc<EncounterIGTResult> GetHitSpinnerPrintFunction(boolean shouldPrintNpcTimers) {
        return result -> String.format("Hit spinner at [%d#%d,%d]; rng %s %s", result.getMap(), result.getX(), result.getY(), result.getRNG(), shouldPrintNpcTimers ? "npctimers " + result.getNpcTimers() : "");
    }

    public static IPrintFunc<EncounterIGTResult> GetNoEncounterPrintFunction(boolean shouldPrintNpcTimers) {
        return result -> String.format("No encounter at [%d#%d,%d]; rng %s %s", result.getMap(), result.getX(), result.getY(), result.getRNG(), shouldPrintNpcTimers ? "npctimers " + result.getNpcTimers() : "");
    }

    public static IPrintFunc<EncounterIGTResult> GetEncounterPrintFunction(boolean shouldPrintNpcTimers, boolean shouldYoloballs) {
        return result -> String.format("Encounter at [%d#%d,%d]: %s%s lv%d DVs %04X rng %s %s %s %s %s %s", result.getMap(), result.getX(), result.getY(), result.getGender() == Gender.GENDERLESS ? "" : result.getGender().getName() + " ", result.getSpeciesName(), result.getLevel(), result.getHexDVs(), result.getRNG(), shouldYoloballs ? String.valueOf(result.getYoloball()) : "", shouldYoloballs ? String.valueOf(result.getSelectYoloball()) : "", shouldYoloballs ? String.valueOf(result.getRedbarYoloball()) : "", shouldYoloballs ? String.valueOf(result.getRedbarYoloball()) : "", shouldPrintNpcTimers ? "npctimers " + result.getNpcTimers() : "");
    }
}
