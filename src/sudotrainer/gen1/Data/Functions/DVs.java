package sudotrainer.gen1.Data.Functions;

import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.encounterigt.EncounterIGTResult;
import stringflow.rta.encounterigt.IPrintFunc;
import stringflow.rta.gen1.Gen1Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static int GetWeightedAverageAttackDvsFromMap(EncounterIGTMap map) {
        return GetAverageAttackDvsFromMap(map);
    }

    public static int GetWeightedAverageSpeedDvsFromMap(EncounterIGTMap map) {
        return GetAverageSpeedDvsFromMap(map);
    }

    public static int GetWeightedAverageSpecialDvsFromMap(EncounterIGTMap map) {
        int average = 0;
        for (int i = 0; i < map.size(); i++) {
            EncounterIGTResult result = map.get(i);
            int special = result.getDVs().getSpecial();
            if (special < 7) {
                special = 15 - special + 1;
            }
            average += special;
        }

        if (map.size() == 0) {
            return 0;
        }

        return average / map.size();
    }

    public static int GetWeightedAverageDefDvsFromMap(EncounterIGTMap map) {
        int average = 0;
        for (int i = 0; i < map.size(); i++) {
            EncounterIGTResult result = map.get(i);
            int def = result.getDVs().getDefense();
            if (def < 7) {
                def = 15 - def + 1;
            }
            average += def;
        }

        if (map.size() == 0) {
            return 0;
        }

        return average / map.size();
    }

    public static stringflow.rta.DVs GetWeightedAverageDVsFromMap(EncounterIGTMap map) {
        int atk = GetWeightedAverageAttackDvsFromMap(map);
        int def = GetWeightedAverageDefDvsFromMap(map);
        int speed = GetWeightedAverageSpeedDvsFromMap(map);
        int special = GetWeightedAverageSpecialDvsFromMap(map);
        return new stringflow.rta.DVs(atk, def, speed, special);
    }

    public static stringflow.rta.DVs GetAverageDVsFromMap(EncounterIGTMap map) {
        int atk = GetAverageAttackDvsFromMap(map);
        int def = GetAverageDefDvsFromMap(map);
        int speed = GetAverageSpeedDvsFromMap(map);
        int special = GetAverageSpecialDvsFromMap(map);
        return new stringflow.rta.DVs(atk, def, speed, special);
    }

    public static String ConvertDvs(int atk, int def, int speed, int special) {
        String[] key = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        return key[atk] + key[def] + key[speed] + key[special];
    }

    public static HashMap<Integer, List<String>>  printPossibleDVCombos() {
        HashMap<Integer, boolean[]> possibleDVCombos = new HashMap<>();
        int[] encounterRates = new int[]{3, 5, 8, 10, 15, 20, 25, 30};
        int rDiv2Max = 255;
        int[] dDiv3s;
        int[] dDiv4s = new int[]{1, 2, 3};
        int[] c2s;
        int c3 = 1;
        int c4 = 1;

        dDiv3s = new int[]{47, 48, 49};
        c2s = new int[]{0, 1};
        for (int encRate : encounterRates) {
            possibleDVCombos.put(encRate, new boolean[16 * 16 * 16 * 16]);
            for (int hRandomAdd1 = 0; hRandomAdd1 < encRate; hRandomAdd1++) {
                for (int rDiv2 = 0; rDiv2 <= rDiv2Max; rDiv2++) {
                    for (int dDiv3 : dDiv3s) {
                        for (int dDiv4 : dDiv4s) {
                            for (int c2 : c2s) {
                                int hRandomAdd2 = (hRandomAdd1 + rDiv2 + c2) % 256;
                                int rDiv3 = (rDiv2 + dDiv3) % 256;
                                int hRandomAdd3 = (hRandomAdd2 + rDiv3 + c3) % 256; // 16*spd + spc
                                int rDiv4 = (rDiv3 + dDiv4) % 256;
                                int hRandomAdd4 = (hRandomAdd3 + rDiv4 + c4) % 256; // 16*atk + def
                                possibleDVCombos.get(encRate)[(hRandomAdd4 << 8) + hRandomAdd3] = true;
                            }
                        }
                    }
                }
            }
        }
        HashMap<Integer, List<String>> possibleDVHex = new HashMap<>();
        for (int encRate : encounterRates) {
            boolean[] dvs = possibleDVCombos.get(encRate);
            List<String> dvList = new ArrayList<>();
            for (int i =0; i < dvs.length; i++) {
                if (dvs[i]) {
                    dvList.add(String.format("%04X", i));
                }
            }
            possibleDVHex.put(encRate, dvList);
        }

        return possibleDVHex;
    }

}
