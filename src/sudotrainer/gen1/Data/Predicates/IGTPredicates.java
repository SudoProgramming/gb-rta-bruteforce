package sudotrainer.gen1.Data.Predicates;

import stringflow.rta.encounterigt.EncounterIGTResult;

import java.util.function.Predicate;

public class IGTPredicates {
    public static Predicate<EncounterIGTResult> GetYoloballPredicate() {
        return result -> result.getYoloball();
    }
}
