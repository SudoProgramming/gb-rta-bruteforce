package sudotrainer.gen1.Data.Predicates;

import stringflow.rta.encounterigt.EncounterIGTResult;

import java.util.function.Predicate;

public class IGTPredicates {
    public static Predicate<EncounterIGTResult> GetYoloballPredicate() {
        return result -> result.getYoloball();
    }

    public static Predicate<EncounterIGTResult> GetSpeciesPredicate(String species) {
        return result -> result.getSpecies() != 0 && result.getSpeciesName().toUpperCase().equals(species.toUpperCase());
    }

    public static Predicate<EncounterIGTResult> GetSpeciesAndYoloballPredicate(String species) {
        return result -> result.getSpecies() != 0 && result.getYoloball() && result.getSpeciesName().toUpperCase().equals(species.toUpperCase());
    }
}
