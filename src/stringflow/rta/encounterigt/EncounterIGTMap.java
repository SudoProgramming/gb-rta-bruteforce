package stringflow.rta.encounterigt;

import java.util.function.Function;
import stringflow.rta.Gender;
import stringflow.rta.GenderRatio;
import stringflow.rta.Species;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.util.ArrayUtils;
import stringflow.rta.util.IGTTimeStamp;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EncounterIGTMap extends ArrayList<EncounterIGTResult> {
	
	public EncounterIGTMap() {
		super();
	}
	
	public EncounterIGTMap(Collection<EncounterIGTResult> collection) {
		super(collection);
	}
	
	// gen 1 constructor
	public void addResult(Gb gb, IGTTimeStamp timeStamp, ArrayList<Integer>[] npcTimers, byte save[], ArrayList<Integer> mapTransitions, ArrayList<Integer> itemPickups, boolean yoloballs[], boolean hitTextbox) {
		String npcTimersString = "";
		for(int i = 1; i < npcTimers.length; i++) {
			long timerId = 0x00000000;
			int expireCounter = 0;
			for(int j = 1; j < npcTimers[i].size(); j++) {
				int previousTimer = npcTimers[i].get(j - 1);
				int currentTimer = npcTimers[i].get(j);
				if(currentTimer > previousTimer) {
					timerId |= j << expireCounter++ * 8;
				}
			}
			npcTimersString += String.format("%08X", timerId);
			if(i != npcTimers.length - 1) {
				npcTimersString += "_";
			}
		}
		add(new EncounterIGTResult(timeStamp, gb.read("wCurMap"), gb.read("wXCoord"), gb.read("wYCoord"), gb.getRandomAdd(), gb.getRandomSub(), gb.getRdiv(), npcTimersString, save, mapTransitions, itemPickups, gb.getGame().getSpecies(gb.read("wEnemyMonSpecies")), Gender.GENDERLESS, gb.read("wEnemyMonLevel"), gb.read("wEnemyMonDVs", 2), yoloballs[0], yoloballs[1], yoloballs[2], yoloballs[3], hitTextbox));
	}
	
	// gen 2 constructor
	public void addResult(IGTTimeStamp igt, int map, int x, int y, int hra, int hrs, int rdiv, byte save[], Species species, int level, int dvUpper, int dvLower, boolean hitSpinner) {
		int genderDVs = (dvUpper & 0xF0) | (dvLower >> 4);
		Gender gender;
		if(species.getGenderRatio() == GenderRatio.GENDER_UNKNOWN)
			gender = Gender.GENDERLESS;
		else if(species.getGenderRatio() == GenderRatio.GENDER_F0)
			gender = Gender.MALE;
		else if(species.getGenderRatio() == GenderRatio.GENDER_F100)
			gender = Gender.FEMALE;
		else if(species.getGenderRatio().getCompareVal() < genderDVs)
			gender = Gender.MALE;
		else
			gender = Gender.FEMALE;
		add(new EncounterIGTResult(igt, map, x, y, hra, hrs, rdiv, "", save, new ArrayList<>(), new ArrayList<>(), species, gender, level, (dvUpper << 8) | dvLower, false, false, false, false, hitSpinner));
	}
	
	public void print(PrintStream target, boolean writeNpcTimers, boolean writeYoloballs) {
		print(target, result -> String.format("Hit spinner at [%d#%d,%d]; rng %s %s", result.getMap(), result.getX(), result.getY(), result.getRNG(), writeNpcTimers ? "npctimers " + result.getNpcTimers() : ""),
				      result -> String.format("No encounter at [%d#%d,%d]; rng %s %s", result.getMap(), result.getX(), result.getY(), result.getRNG(), writeNpcTimers ? "npctimers " + result.getNpcTimers() : ""),
				      result -> String.format("Encounter at [%d#%d,%d]: %s%s lv%d DVs %04X rng %s %s %s %s %s %s", result.getMap(), result.getX(), result.getY(), result.getGender() == Gender.GENDERLESS ? "" : result.getGender().getName() + " ", result.getSpeciesName(), result.getLevel(), result.getHexDVs(), result.getRNG(), writeYoloballs ? String.valueOf(result.getYoloball()) : "", writeYoloballs ? String.valueOf(result.getSelectYoloball()) : "", writeYoloballs ? String.valueOf(result.getRedbarYoloball()) : "", writeYoloballs ? String.valueOf(result.getRedbarYoloball()) : "", writeNpcTimers ? "npctimers " + result.getNpcTimers() : ""));
	}
	
	public void print(PrintStream target, IPrintFunc<EncounterIGTResult> noEncounterFunc, IPrintFunc<EncounterIGTResult> encounterFunc) {
		print(target, null, noEncounterFunc, encounterFunc);
	}
	
	public void print(PrintStream target, IPrintFunc<EncounterIGTResult> hitSpinnerFunc, IPrintFunc<EncounterIGTResult> noEncounterFunc, IPrintFunc<EncounterIGTResult> encounterFunc) {
		forEach(result -> {
			String igt = String.format("[%d][%d] ", result.getIgt().getSeconds(), result.getIgt().getFrames());
			if(result.getSpecies() == 0) {
				if(result.getHitSpinner()) {
					target.println(igt + hitSpinnerFunc.get(result));
				} else {
					target.println(igt + noEncounterFunc.get(result));
				}
			} else {
				target.println(igt + encounterFunc.get(result));
			}
			target.flush();
		});
	}

	public void print(PrintStream target, LinkedHashMap<Function<EncounterIGTResult, Boolean>, IPrintFunc<EncounterIGTResult>> printMethods) {
		forEach(result -> {
			boolean hasPrinted = false;
				//String igt = String.format("[%d][%d] ", result.getIgt().getSeconds(), result.getIgt().getFrames());
				for (Map.Entry<Function<EncounterIGTResult, Boolean>, IPrintFunc<EncounterIGTResult>> entry : printMethods.entrySet()) {
					Function<EncounterIGTResult, Boolean> conditionalFunction = entry.getKey();
					IPrintFunc<EncounterIGTResult> printFunc = entry.getValue();
					if (conditionalFunction.apply(result)) {
						if (hasPrinted) {
							target.print(" | ");
						}
						target.print(printFunc.get(result));
						hasPrinted = true;
					}
				}
				target.println();
			}
		);
	}

	public void printSummary(PrintStream target) {

	}

	public void printSummary(PrintStream target, String printSummaryUnFormatted) {
		target.println();
	}
	
	public EncounterIGTMap filter(Predicate<EncounterIGTResult> visitor) {
		ArrayList<EncounterIGTResult> copy = new ArrayList<>(this);
		return new EncounterIGTMap(copy.stream().filter(visitor).collect(Collectors.toList()));
	}
	
	public int getNumDifferentNPCs(IGTTimeStamp... ignoreFramesIn) {
		return getNumDifferentNPCs(Stream.of(ignoreFramesIn).mapToInt(IGTTimeStamp::getTotalFrames).toArray());
	}
	
	public int getNumDifferentNPCs(int... ignoreFrames) {
		HashSet<String> npcs = new HashSet<>();
		forEach(result -> {
			if(!ArrayUtils.arrayContains(ignoreFrames, result.getIgt().getTotalFrames())) {
				npcs.add(result.getNpcTimers());
			}
		});
		return npcs.size();
	}
}