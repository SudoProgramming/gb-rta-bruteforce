package sudotrainer.gen1.Data.Functions;

import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.encounterigt.EncounterIGTResult;
import stringflow.rta.encounterigt.IPrintFunc;

public class DVs {
    public static int GetAverageAttackDvsFromMap(EncounterIGTMap map) {
        int average = 0;
        for (int i = 0; i < map.size(); i++) {
            EncounterIGTResult result = map.get(i);
            average += result.getDVs().getAttack();
        }

        if (map.size() == 0) {
            return 0;
        }

        return average / map.size();
    }

    public static int GetAverageSpeedDvsFromMap(EncounterIGTMap map) {
        int average = 0;
        for (int i = 0; i < map.size(); i++) {
            EncounterIGTResult result = map.get(i);
            average += result.getDVs().getSpeed();
        }

        if (map.size() == 0) {
            return 0;
        }

        return average / map.size();
    }

    public static int GetAverageSpecialDvsFromMap(EncounterIGTMap map) {
        int average = 0;
        for (int i = 0; i < map.size(); i++) {
            EncounterIGTResult result = map.get(i);
            average += result.getDVs().getSpecial();
        }

        if (map.size() == 0) {
            return 0;
        }

        return average / map.size();
    }

    public static int GetAverageDefDvsFromMap(EncounterIGTMap map) {
        int average = 0;
        for (int i = 0; i < map.size(); i++) {
            EncounterIGTResult result = map.get(i);
            average += result.getDVs().getDefense();
        }

        if (map.size() == 0) {
            return 0;
        }

        return average / map.size();
    }

    public static String ConvertDvs(int atk, int def, int speed, int special) {
        String[] key = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        return key[atk] + key[def] + key[speed] + key[special];
    }
}
