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

    public static Predicate<EncounterIGTResult> GetLevelPredicate(int level) {
        return result -> result.getSpecies() != 0 && result.getLevel() == level;
    }

    public static Predicate<EncounterIGTResult> GetSpeciesAndLevelPredicate(String species, int level) {
        return result -> result.getSpecies() != 0 && result.getSpeciesName().toUpperCase().equals(species.toUpperCase()) && result.getLevel() == level;
    }

    public static Predicate<EncounterIGTResult> GetSpeciesLevelAndYoloballPredicate(String species, int level) {
        return result -> result.getSpecies() != 0 && result.getYoloball() && result.getSpeciesName().toUpperCase().equals(species.toUpperCase()) && result.getLevel() == level;
    }
}
