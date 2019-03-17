package TEMP;

import stringflow.rta.*;
import stringflow.rta.astar.AStar;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.encounterigt.EncounterIGTResult;
import stringflow.rta.gen1.Gen1Game;
import stringflow.rta.gen1.PokeYellow;
import stringflow.rta.gen1.RbyIGTChecker;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.ow.OverworldAction;
import stringflow.rta.ow.OverworldEdge;
import stringflow.rta.ow.OverworldState;
import stringflow.rta.ow.OverworldTile;
import stringflow.rta.util.IGTTimeStamp;
import stringflow.rta.util.IO;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

public class OldClefairyBot {

    private static final String gameName;
    private static final Checkpoint checkpoints[];
    private static final ArrayList<Integer> ignoreFrames = new ArrayList<Integer>();
    private static final long flags;
    private static PrintWriter partialManips;
    private static PrintWriter foundManips;
    private static PrintWriter level9Manips;
    private static PrintWriter level11Manips;
    private static PrintWriter level13Manips;
    private static PrintWriter godFairyManips;
    private static Gb gb;
    private static HashSet<String> seenStates = new HashSet<>();

    private static ArrayList<IGTState> initialStates;
    private static OverworldTile savePos;

    private static int encounterCost = 34;
    private static int aPressCost = 2;
    private static int maxAPresses = 10;
    private static int maxStartFlashes = 3;
    private static int startFlashMaybeCost = 5;

    static {
        gameName = "yellow";
        flags = RbyIGTChecker.MONITOR_NPC_TIMERS | RbyIGTChecker.CREATE_SAVE_STATES  | RbyIGTChecker.YOLOBALL | 4;
        checkpoints = new Checkpoint[] {new Checkpoint(60, 21, 18, encounterCost + aPressCost * maxAPresses + maxStartFlashes * startFlashMaybeCost, maxStartFlashes, 57), };
//		ignoreFrames.add(33);
//		ignoreFrames.add(34);
////		ignoreFrames.add(37);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Gen1Game game = new PokeYellow();
        foundManips = new PrintWriter(new File(gameName + "_foundManips.txt"));
        partialManips = new PrintWriter(new File(gameName + "_partial_moon_paths.txt"));
        level9Manips = new PrintWriter(new File(gameName + "_clefairy_9.txt"));
        level11Manips = new PrintWriter(new File(gameName + "_clefairy_11.txt"));
        level13Manips = new PrintWriter(new File(gameName + "_clefairy_13.txt"));
        godFairyManips = new PrintWriter(new File(gameName + "_godfairy_13.txt"));

        long startTime = System.currentTimeMillis();
        OverworldTile[][] owTiles1 = AStar.initTiles(Map.MT_MOON_3, 17, 3, true, new MapDestination(Map.MT_MOON_3, new Location(13, 9))); // MapDestination(Map.MT_MOON_3, new Location(2, 9)


        owTiles1[13][9].removeEdge(OverworldAction.UP);
        owTiles1[12][10].removeEdge(OverworldAction.UP);
        owTiles1[11][18].removeEdge(OverworldAction.UP);
        owTiles1[11][18].removeEdge(OverworldAction.LEFT);
        owTiles1[11][19].removeEdge(OverworldAction.LEFT);
        owTiles1[11][20].removeEdge(OverworldAction.LEFT);
        owTiles1[11][21].removeEdge(OverworldAction.LEFT);
        owTiles1[11][22].removeEdge(OverworldAction.LEFT);
        owTiles1[11][23].removeEdge(OverworldAction.LEFT);
        owTiles1[11][24].removeEdge(OverworldAction.LEFT);
        owTiles1[11][25].removeEdge(OverworldAction.LEFT);
        owTiles1[11][26].removeEdge(OverworldAction.LEFT);
        owTiles1[11][27].removeEdge(OverworldAction.LEFT);
        owTiles1[11][28].removeEdge(OverworldAction.LEFT);
        owTiles1[11][29].removeEdge(OverworldAction.LEFT);
        owTiles1[11][30].removeEdge(OverworldAction.LEFT);
        owTiles1[11][31].removeEdge(OverworldAction.LEFT);
        owTiles1[11][22].removeEdge(OverworldAction.DOWN);

        owTiles1[12][17].removeEdge(OverworldAction.LEFT);
        owTiles1[13][16].removeEdge(OverworldAction.RIGHT);
        owTiles1[16][12].removeEdge(OverworldAction.RIGHT);
        owTiles1[13][17].removeEdge(OverworldAction.RIGHT);
        owTiles1[13][18].removeEdge(OverworldAction.RIGHT);
        owTiles1[15][31].removeEdge(OverworldAction.RIGHT);
        owTiles1[15][31].removeEdge(OverworldAction.DOWN);
        owTiles1[14][31].removeEdge(OverworldAction.DOWN);
        owTiles1[13][31].removeEdge(OverworldAction.DOWN);
        owTiles1[12][31].removeEdge(OverworldAction.DOWN);
        owTiles1[11][31].removeEdge(OverworldAction.DOWN);

        long endTime = System.currentTimeMillis();
        System.out.println("Generic edge generation time: " + (endTime - startTime) + " ms");

        gb = new Gb();
        gb.loadBios("roms/gbc_bios.bin");
        gb.loadRom("roms/pokeyellow.gbc", new PokeYellow(), LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
        gb.createRenderContext(5);
        gb.setOnDisplayUpdate(new InputDisplay());
        game.getStrat("gfSkip").execute(gb);
        game.getStrat("intro0").execute(gb);
        game.getStrat("title").execute(gb);
        gb.runUntil("igtInject");
        byte igtState[] = gb.saveState();
        ArrayList<IGTState> initialStates = new ArrayList<>();
        for (int second = 0; second < 1; second++) {
            for (int frame = 0; frame < 60; frame++) {
                gb.loadState(igtState);
                gb.write("wPlayTimeSeconds", second);
                gb.write("wPlayTimeFrames", frame);
                game.getStrat("cont").execute(gb);
                game.getStrat("cont").execute(gb);
                gb.runUntil("joypadOverworld");
                initialStates.add(new IGTState(new IGTTimeStamp(0, 0, second, frame), gb.saveState()));
            }
        }
        gb.loadState(initialStates.get(0).getState());
        gb.frameAdvance(2);
        savePos = owTiles1[gb.read("wXCoord")][gb.read("wYCoord")];
        OverworldState owState = new OverworldState(savePos.toString() + ":", savePos, initialStates, checkpoints[0], 1, 0, 0, true, 0, 0, gb.getRandomAdd(), gb.getRandomSub());
        overworldSearch(owState);
    }

    private static int indexOf(Checkpoint checkpoint) {
        for(int i = 0; i < checkpoints.length; i++) {
            if(checkpoints[i] == checkpoint) {
                return i;
            }
        }
        return -1;
    }

    private static int calcMaxCost(Checkpoint checkpoint) {
        int index = indexOf(checkpoint);
        int sum = checkpoint.getMaxCost();
        for(int i = 0; i < index; i++) {
            sum += checkpoints[i].getMaxCost();
        }
        return sum;
    }

    private static int calcMaxStartFlashes(Checkpoint checkpoint) {
        int index = indexOf(checkpoint);
        int sum = checkpoint.getMaxStartFlashes();
        for(int i = 0; i < index; i++) {
            sum += checkpoints[i].getMaxStartFlashes();
        }
        return sum;
    }

    private static void overworldSearch(OverworldState ow) {
        if(!seenStates.add(ow.getUniqId())) {
            return;
        }
        int maxCost = calcMaxCost(ow.getCurrentTarget());
        int maxStartFlashes = calcMaxStartFlashes(ow.getCurrentTarget());
        int currentCheckpointIndex = indexOf(ow.getCurrentTarget());
        Checkpoint lastCheckpoint = checkpoints[checkpoints.length - 1];
        if(ow.getWastedFrames() > maxCost) {
            return;
        }
        for(OverworldEdge edge : ow.getPos().getEdgeList()) {
            OverworldAction edgeAction = edge.getAction();
            if(ow.aPressCounter() > 0 && (edgeAction == OverworldAction.A || edgeAction == OverworldAction.START_B || edgeAction == OverworldAction.S_A_B_S || edgeAction == OverworldAction.S_A_B_A_B_S)) {
                continue;
            }
            if(!ow.canPressStart() && (edgeAction == OverworldAction.START_B || edgeAction == OverworldAction.S_A_B_S || edgeAction == OverworldAction.S_A_B_A_B_S)) {
                continue;
            }
            if((edgeAction == OverworldAction.START_B || edgeAction == OverworldAction.S_A_B_S || edgeAction == OverworldAction.S_A_B_A_B_S) && ow.getNumStartPresses() >= maxStartFlashes) {
                continue;
            }
            int edgeCost = edge.getCost();
            if(ow.getWastedFrames() + edgeCost > maxCost) {
                continue;
            }
            OverworldState newState;
            int owFrames = ow.getOverworldFrames() + edge.getFrames();
            gb.hold(0);
            RbyIGTChecker igtChecker = new RbyIGTChecker(gb);
            EncounterIGTMap igtMap = igtChecker.checkIGT0(ow.getStates(), edgeAction.logStr(), flags);
            HashSet<String> npcs = new HashSet<>();
            ArrayList<IGTState> newStates = new ArrayList<>();
            for(int i = 0; i < igtMap.size(); i++) {
                EncounterIGTResult result = igtMap.get(i);
                newStates.add(new IGTState(result.getIgt(), result.getSave()));
                if(!ignoreFrames.contains(result.getIgt().getFrames())) {
                    npcs.add(igtMap.get(i).getNpcTimers());
                }
            }
            int encounterIgt0 = igtMap.filter(igt -> igt.getSpecies() == 0).size();
            int clefairy = igtMap.filter(result -> result.getSpecies() == 4).size();
            int level = -1;
            if(clefairy >= 1) {
                EncounterIGTMap clefairyMap = igtMap.filter(result -> result.getSpecies() == 4);
                EncounterIGTMap nine = clefairyMap.filter(result -> result.getLevel() == 9);
                EncounterIGTMap eleven = clefairyMap.filter(result -> result.getLevel() == 11);
                EncounterIGTMap thirteen = clefairyMap.filter(result -> result.getLevel() == 13);
                foundManips.println(ow.toString() + " " + edgeAction.logStr() + ", cost: " + (ow.getWastedFrames() + edgeCost) + ", owFrames: " + (owFrames) + " - " + clefairy + "/60 hra=" + gb.getRandomAdd());
                foundManips.flush();
                System.out.println("IGT SUCCESS (" + nine.size() + "/60), (" + eleven.size() + "/60), (" + thirteen.size() + "/60) [YOLOBALL SUCCESS (" +  nine.filter(result -> result.getYoloball()).size() + "/60), ("  +  eleven.filter(result -> result.getYoloball()).size() + "/60), ("  +  thirteen.filter(result -> result.getYoloball()).size() + "/60)] AVERAGE DVS [" + sudotrainer.gen1.Data.Functions.DVs.GetAverageDVsFromMap(nine).toHexString() + "," + sudotrainer.gen1.Data.Functions.DVs.GetAverageDVsFromMap(eleven).toHexString() + "," + sudotrainer.gen1.Data.Functions.DVs.GetAverageDVsFromMap(thirteen).toHexString() + "] WEIGHTED AVERAGE DVS [" + sudotrainer.gen1.Data.Functions.DVs.GetWeightedAverageDVsFromMap(nine).toHexString() + "," + sudotrainer.gen1.Data.Functions.DVs.GetWeightedAverageDVsFromMap(eleven).toHexString() + "," + sudotrainer.gen1.Data.Functions.DVs.GetWeightedAverageDVsFromMap(thirteen).toHexString() + "] - PATH: "+ ow.toString() + " " + edgeAction.logStr());
                for (int i = 0; i < igtMap.size(); i++) {
                    EncounterIGTResult result = igtMap.get(i);
                    System.out.println("[" + i + "], " + result.getSpeciesName() + " " + result.getLevel() + " " + result.getDVs().toHexString() + " YOLOBALL: " + result.getYoloball());
                }
                if (nine.size() > 30) {
                    level9Manips.println("IGT SUCCESS " + nine.size() + "/60  [YOLOBALL SUCCESS (" +  nine.filter(result -> result.getYoloball()).size() + "/60)] AVERAGE DVS [" + sudotrainer.gen1.Data.Functions.DVs.GetAverageDVsFromMap(nine).toHexString() + "] WEIGHTED AVERAGE DVS [" + sudotrainer.gen1.Data.Functions.DVs.GetWeightedAverageDVsFromMap(nine).toHexString() + "] - PATH: "+ ow.toString() + " " + edgeAction.logStr());
                    for (int i = 0; i < nine.size(); i++) {
                        EncounterIGTResult result = nine.get(i);
                        level9Manips.println("[" + i + "], " + result.getSpeciesName() + " " + result.getLevel() + " " + result.getDVs().toHexString() + " YOLOBALL: " + result.getYoloball());
                    }
                    level9Manips.flush();
                }
                if (eleven.size() > 30) {
                    level11Manips.println("IGT SUCCESS " + eleven.size() + "/60  [YOLOBALL SUCCESS (" +  eleven.filter(result -> result.getYoloball()).size() + "/60)] AVERAGE DVS [" + sudotrainer.gen1.Data.Functions.DVs.GetAverageDVsFromMap(eleven).toHexString() + "] WEIGHTED AVERAGE DVS [" + sudotrainer.gen1.Data.Functions.DVs.GetWeightedAverageDVsFromMap(eleven).toHexString() + "] - PATH: "+ ow.toString() + " " + edgeAction.logStr());
                    for (int i = 0; i < eleven.size(); i++) {
                        EncounterIGTResult result = eleven.get(i);
                        level11Manips.println("[" + i + "], " + result.getSpeciesName() + " " + result.getLevel() + " " + result.getDVs().toHexString() + " YOLOBALL: " + result.getYoloball());
                    }
                    level11Manips.flush();
                }
                if (thirteen.size() > 20) {
                    DVs averageDVs = sudotrainer.gen1.Data.Functions.DVs.GetAverageDVsFromMap(thirteen);
                    DVs weightedAverageDVs = sudotrainer.gen1.Data.Functions.DVs.GetWeightedAverageDVsFromMap(thirteen);
                    if (averageDVs.getSpeed() >= 14 && averageDVs.getSpecial() >= 12 && averageDVs.getAttack() >= 12) {
                        godFairyManips.println("IGT SUCCESS " + thirteen.size() + "/60  [YOLOBALL SUCCESS (" +  thirteen.filter(result -> result.getYoloball()).size() + "/60)] AVERAGE DVS [" + averageDVs.toHexString() + "] WEIGHTED AVERAGE DVS [" + weightedAverageDVs.toHexString() + "] - PATH: "+ ow.toString() + " " + edgeAction.logStr());
                        for (int i = 0; i < thirteen.size(); i++) {
                            EncounterIGTResult result = thirteen.get(i);
                            godFairyManips.println("[" + i + "], " + result.getSpeciesName() + " " + result.getLevel() + " " + result.getDVs().toHexString() + " YOLOBALL: " + result.getYoloball());
                        }
                        godFairyManips.flush();
                    }

                    level13Manips.println("IGT SUCCESS " + thirteen.size() + "/60  [YOLOBALL SUCCESS (" +  thirteen.filter(result -> result.getYoloball()).size() + "/60)] AVERAGE DVS [" + averageDVs.toHexString() + "] WEIGHTED AVERAGE DVS [" + weightedAverageDVs.toHexString() + "] - PATH: "+ ow.toString() + " " + edgeAction.logStr());
                    for (int i = 0; i < thirteen.size(); i++) {
                        EncounterIGTResult result = thirteen.get(i);
                        level13Manips.println("[" + i + "], " + result.getSpeciesName() + " " + result.getLevel() + " " + result.getDVs().toHexString() + " YOLOBALL: " + result.getYoloball());
                    }
                    level13Manips.flush();
                }
            }
            partialManips.println(ow.toString() + " " + edgeAction.logStr() + ", cost: " + (ow.getWastedFrames() + edgeCost) + ", owFrames: " + (owFrames) + " - " + encounterIgt0 + "/60 " + npcs.size() + " differences");
            partialManips.flush();
//			if (encounterIgt0 >= 57 && igtMap.filter(igt -> igt.getX() == 12 && igt.getY() == 9).size() >= 57) {
//				foundManips.println(ow.toString() + " " + edgeAction.logStr() + ", cost: " + (ow.getWastedFrames() + edgeCost) + ", owFrames: " + (owFrames) + " - " + encounterIgt0 + "/60 hra=" + gb.getRandomAdd());
//				foundManips.flush();
//			}
            if(npcs.size() > 1) {
                continue;
            }
            if(encounterIgt0 < ow.getCurrentTarget().getMinConsistency()) {
                continue;
            }
            switch(edgeAction) {
                case LEFT:
                case UP:
                case RIGHT:
                case DOWN:
                    Checkpoint newCheckpoint = ow.getCurrentTarget();
                    if(edge.getNextPos().getX() == newCheckpoint.getX() && edge.getNextPos().getY() == newCheckpoint.getY()) {
                        if(ow.getCurrentTarget() != lastCheckpoint) {
                            newCheckpoint = checkpoints[currentCheckpointIndex + 1];
                        }
                    }
                    if(edge.getNextPos().getX() == lastCheckpoint.getX() && edge.getNextPos().getY() == lastCheckpoint.getY() && edge.getNextPos().getMap() == lastCheckpoint.getMap() && encounterIgt0 >= lastCheckpoint.getMinConsistency()) {
                        foundManips.println(ow.toString() + " " + edgeAction.logStr() + ", cost: " + (ow.getWastedFrames() + edgeCost) + ", owFrames: " + (owFrames) + " - " + encounterIgt0 + "/60 hra=" + gb.getRandomAdd());
                        foundManips.flush();
                        break;
                    }
                    newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newStates, newCheckpoint, Math.max(0, ow.aPressCounter() - 1), ow.getNumStartPresses(), ow.getNumAPresses(), true, ow.getWastedFrames() + edgeCost, ow.getOverworldFrames() + edge.getFrames(), gb.getRandomAdd(), gb.getRandomSub());
                    overworldSearch(newState);
                    break;
                case A:
                    newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newStates, ow.getCurrentTarget(), 2, ow.getNumStartPresses(), ow.getNumAPresses() + 1, true, ow.getWastedFrames() + 2, ow.getOverworldFrames() + 2, gb.getRandomAdd(), gb.getRandomSub());
                    overworldSearch(newState);
                    break;
                case START_B:
                    newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newStates, ow.getCurrentTarget(), 1, ow.getNumStartPresses() + 1, ow.getNumAPresses(), true, ow.getWastedFrames() + edgeCost, ow.getOverworldFrames() + edgeCost, gb.getRandomAdd(), gb.getRandomSub());
                    overworldSearch(newState);
                    break;
                default:
                    break;
            }
        }
    }
}
