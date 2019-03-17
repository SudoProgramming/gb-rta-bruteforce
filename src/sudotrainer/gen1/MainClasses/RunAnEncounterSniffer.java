package sudotrainer.gen1.MainClasses;

import stringflow.rta.Checkpoint;
import stringflow.rta.Location;
import stringflow.rta.Map;
import stringflow.rta.MapDestination;
import stringflow.rta.astar.AStar;
import stringflow.rta.gen1.Gen1Game;
import stringflow.rta.gen1.PokeYellow;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.ow.OverworldState;
import stringflow.rta.ow.OverworldTile;
import sudotrainer.gen1.Data.Functions.DVs;
import sudotrainer.gen1.GB.GBController;
import sudotrainer.gen1.GB.GBHelper;

import java.util.HashMap;
import java.util.List;

public class RunAnEncounterSniffer {
    static Checkpoint[] checkpoints = new Checkpoint[] {new Checkpoint(60, 21, 17, 48, 3,57), };

    public static void main(String[] args) {
        HashMap<Integer, List<String>> possible = DVs.printPossibleDVCombos();
        List<String> zonesDVs = possible.get(10);
        int i = 0;
        while (i < zonesDVs.size()) {
            if (i % 10 == 0) {
                if (i > 0) {
                    System.out.println();
                }
                System.out.print(zonesDVs.get(i));
            }
            else {
                System.out.print(" | " + zonesDVs.get(i));
            }
            i++;
        }

//        Gen1Game game = new PokeYellow();
//
//        OverworldTile[][] owTiles1 = AStar.initTiles(Map.MT_MOON_3, 17, 3, true, new MapDestination(Map.MT_MOON_3, new Location(13, 12)));
//        // Add Edges and map conetions
//        Gb gb = GBController.loadGameboy(game, 0, false);
//        gb.loadState(gb.getInitialStates().get(0).getState());
//        gb.frameAdvance(2);
//        OverworldTile savePos = owTiles1[gb.read("wXCoord")][gb.read("wYCoord")];
//        OverworldState owState = new OverworldState(savePos.toString() + ":", savePos, gb.getInitialStates(), checkpoints[0], 1, 0, 0, true, 0, 0, gb.getRandomAdd(), gb.getRandomSub());
    }
}