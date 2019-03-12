package stringflow.rta.gen1;

import stringflow.rta.*;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.ow.OverworldAction;
import stringflow.rta.util.IGTTimeStamp;
import stringflow.rta.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static stringflow.rta.Joypad.*;

public class RbyIGTChecker {
	
	public static final int NONE = 0;
	public static final int PICKUP_RARE_CANDY = 1 << 8;
	public static final int PICKUP_ESCAPE_ROPE = 1 << 9;
	public static final int PICKUP_MEGA_PUNCH = 1 << 10;
	public static final int PICKUP_MOON_STONE = 1 << 11;
	public static final int PICKUP_WATER_GUN = 1 << 12;
	public static final int YOLOBALL = 1 << 13;
	public static final int SELECT_YOLOBALL = 1 << 14;
	public static final int REDBAR_YOLOBALL = 1 << 15;
	public static final int REDBAR_SELECT_YOLOBALL = 1 << 16;
	public static final int MONITOR_NPC_TIMERS = 1 << 17;
	public static final int CREATE_SAVE_STATES = 1 << 18;
	public static final int ENCOUNTER_RATE_HACK = 1 << 19;
	
	private static final int NUM_NPCS = 15;
	private static final Itemball WATER_GUN = new Itemball(0xD, new Location(59, 0x5, 0x1F), new Location(59, 0x6, 0x20));
	private static final Itemball RARE_CANDY = new Itemball(0xA, new Location(59, 0x23, 0x20), new Location(59, 0x22, 0x1F));
	private static final Itemball ESCAPE_ROPE = new Itemball(0xB, new Location(59, 0x24, 0x18), new Location(59, 0x23, 0x17));
	private static final Itemball MOON_STONE = new Itemball(0x9, new Location(59, 0x3, 0x2), new Location(59, 0x2, 0x3));
	private static final Itemball MEGA_PUNCH = new Itemball(0x9, new Location(61, 0x1C, 0x5));
	
	private Gb gb;
	private long params;
	private boolean yoloballs[];
	private ArrayList<Integer> enterMapCalls;
	private ArrayList<Integer> itemPickups;
	
	public RbyIGTChecker(Gb gb) {
		this.gb = gb;
	}
	
	public EncounterIGTMap checkIGT0(Collection<IGTState> initalStates, String path, long params) {
		this.params = params;
		ArrayList<Itemball> itemballs = new ArrayList<>();
		if((params & PICKUP_RARE_CANDY) != 0) {
			itemballs.add(RARE_CANDY);
		}
		if((params & PICKUP_ESCAPE_ROPE) != 0) {
			itemballs.add(ESCAPE_ROPE);
		}
		if((params & PICKUP_MEGA_PUNCH) != 0) {
			itemballs.add(MEGA_PUNCH);
		}
		if((params & PICKUP_MOON_STONE) != 0) {
			itemballs.add(MOON_STONE);
		}
		if((params & PICKUP_WATER_GUN) != 0) {
			itemballs.add(WATER_GUN);
		}
		ArrayList<Integer>[] npcTimers = new ArrayList[NUM_NPCS];
		EncounterIGTMap igtmap = new EncounterIGTMap();
		String actions[] = path.split(" ");
		for(IGTState state : initalStates) {
			IGTTimeStamp igt = state.getIgt();
			byte data[] = state.getState();
			gb.loadState(data);
			gb.runUntil("joypadOverworld");
			for(int i = 0; i < NUM_NPCS; i++) {
				npcTimers[i] = new ArrayList<>();
			}
			yoloballs = new boolean[4];
			enterMapCalls = new ArrayList<>();
			itemPickups = new ArrayList<>();
			if(state.getEnterMapIGT() != -1) {
				enterMapCalls.add(state.getEnterMapIGT());
			}
			if((params & MONITOR_NPC_TIMERS) != 0) {
				updateNPCTimers(gb, npcTimers);
			}
			boolean hitTextbox = false;
			for(String action : actions) {
				if(action.trim().isEmpty()) {
					continue;
				}
				Failure failure = execute(OverworldAction.fromString(action), itemballs);
				if(failure == Failure.TEXTBOX) {
					hitTextbox = true;
				}
				if(failure != Failure.NO_FAILURE) {
					break;
				}
				if((params & MONITOR_NPC_TIMERS) != 0) {
					updateNPCTimers(gb, npcTimers);
				}
			}
			igtmap.addResult(gb, igt, npcTimers, (params & CREATE_SAVE_STATES) != 0 && gb.read("wEnemyMonSpecies") == 0 ? gb.saveState() : null, enterMapCalls, itemPickups, yoloballs, hitTextbox);
		}
		return igtmap;
	}
	
	private Failure execute(OverworldAction owAction, ArrayList<Itemball> itemballs) {
		if((params & ENCOUNTER_RATE_HACK) != 0) {
			gb.write("wgrassrate", 0);
		}
		Address res;
		switch(owAction) {
			case LEFT:
			case UP:
			case RIGHT:
			case DOWN:
				int joypadOverworld2 = gb.getGame().getAddress("joypadOverworld").getAddress() + 1;
				int input = 16 * (int)(Math.pow(2.0, (owAction.ordinal())));
				Location dest = getDestination(gb, input);
				gb.hold(input);
				gb.runUntil(joypadOverworld2);
				Address result = gb.runUntil("joypadOverworld", "newBattle", "manualTextScroll");
				if(result.equals("manualTextScroll")) {
					return Failure.TEXTBOX;
				}
				Tile destTile = Map.getMapByID(dest.map).getTile(dest.x, dest.y);
				if(destTile != null) {
					if(destTile.isWarp()) {
						gb.runUntil("EnterMap");
						enterMapCalls.add(gb.getIGT().getTotalFrames());
					}
				}
				while(gb.read("wXCoord") != dest.x || gb.read("wYCoord") != dest.y) {
					if(result.equals("newBattle")) {
						Address result2 = gb.runUntil("encounterTest", "joypadOverworld");
						if(result2.equals("encounterTest")) {
							int hra = gb.getRandomAdd();
							if(hra < gb.read("wGrassRate")) {
								gb.frameAdvance(3);
								if((params & 0xFF) == gb.read("wEnemyMonSpecies")) {
									simulateYoloball();
								}
								return Failure.ENCOUNTER;
							}
						}
					}
					gb.hold(0);
					gb.runUntil("joypadOverworld");
					gb.hold(input);
					gb.runUntil(joypadOverworld2);
					result = gb.runUntil("newBattle", "joypadOverworld");
				}
				gb.hold(0);
				Address result2 = gb.runUntil("encounterTest", "joypadOverworld", "manualTextScroll");
				if(result2.equals("manualTextScroll")) {
					return Failure.TEXTBOX;
				}
				if(result2.equals("encounterTest")) {
					int hra = gb.getRandomAdd();
					if(hra < gb.read("wGrassRate")) {
						gb.frameAdvance(3);
						if((params & 0xFF) == gb.read("wEnemyMonSpecies")) {
							simulateYoloball();
						}
						return Failure.ENCOUNTER;
					}
					gb.hold(0);
					gb.runUntil("joypadOverworld");
					if(timeToPickUpItem(gb, itemballs)) {
						gb.press(A);
						gb.hold(A);
						gb.runUntil("TextCommand0B");
						itemPickups.add(gb.getIGT().getTotalFrames());
						gb.hold(0);
						gb.runUntil("joypadOverworld");
					}
				} else {
    					if(destTile != null) {
        					if(destTile.isSolid()) {
            						gb.hold(input);
            						gb.frameAdvance();
            						gb.runUntil("joypadOverworld");
        					}
    					}
				}
				return Failure.NO_FAILURE;
			case A:
				gb.hold(A);
				gb.frameAdvance();
				res = gb.runUntil("joypadOverworld", "manualTextScroll");
				if(res.equals("joypadOverworld")) {
					return Failure.NO_FAILURE;
				} else {
					return Failure.TEXTBOX;
				}
			case START_B:
				gb.hold(START);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(B);
				gb.frameAdvance();
				gb.runUntil("joypadOverworld");
				return Failure.NO_FAILURE;
			case S_A_B_S:
				gb.hold(START);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(A);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(B);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(START);
				gb.frameAdvance();
				gb.runUntil("joypadOverworld");
				return Failure.NO_FAILURE;
			case S_A_B_A_B_S:
				gb.hold(START);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(A);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(B);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(A);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(B);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(START);
				gb.frameAdvance();
				gb.runUntil("joypadOverworld");
				return Failure.NO_FAILURE;
			default:
				return Failure.WRONG_ACTION;
		}
	}
	
	
	private void simulateYoloball() {
		gb.runUntil("manualTextScroll");
		byte hpSave[] = gb.saveState();
		int currentHPAddress = gb.getGame().getAddress("wPartyMon1HP").getAddress() + 1;
		if((params & YOLOBALL) != 0 || (params & SELECT_YOLOBALL) != 0) {
			gb.write(currentHPAddress, gb.read(gb.getGame().getAddress("wPartyMon1Stats").getAddress() + 1));
			executeYoloball((params & YOLOBALL) != 0, (params & SELECT_YOLOBALL) != 0, 0);
		}
		if((params & REDBAR_YOLOBALL) != 0 || (params & REDBAR_SELECT_YOLOBALL) != 0) {
			gb.loadState(hpSave);
			gb.write(currentHPAddress, 1);
			executeYoloball((params & REDBAR_YOLOBALL) != 0, (params & REDBAR_SELECT_YOLOBALL) != 0, 2);
		}
	}
	
	private void executeYoloball(boolean regular, boolean select, int indexOffset) {
		boolean isYellow = gb.getGame() instanceof PokeYellow;
		gb.press(A);
		gb.runUntil(isYellow && gb.read("wPartySpecies") == gb.getGame().getSpecies("PIKACHU").getIndexNumber() ? "PlayPikachuSoundClip" : "playCry");
		byte ballToss[] = gb.saveState();
		if(regular) {
			if(isYellow) {
				gb.runUntil("joypad");
				gb.press(DOWN);
				gb.runUntil("joypad");
				gb.press(A);
				gb.runUntil("joypad");
				gb.hold(A | B);
				yoloballs[indexOffset] = gb.runUntil("catchSuccess", "catchFailure").equals("catchSuccess");
			} else {
				gb.hold(DOWN | A);
				gb.runUntil("displayListMenuId");
				gb.hold(A | RIGHT);
				yoloballs[indexOffset] = gb.runUntil("catchSuccess", "catchFailure").equals("catchSuccess");
			}
		}
		if(select) {
			gb.loadState(ballToss);
			if(isYellow) {
				gb.runUntil("joypad");
				gb.press(DOWN);
				gb.runUntil("joypad");
				gb.press(A);
				gb.runUntil("joypad");
				gb.press(SELECT);
				gb.hold(A);
				yoloballs[indexOffset + 1] = gb.runUntil("catchSuccess", "catchFailure").equals("catchSuccess");
			} else {
				gb.hold(DOWN | A);
				gb.runUntil("displayListMenuId");
				gb.hold(0);
				gb.runUntil("joypad");
				gb.press(SELECT);
				gb.hold(A);
				yoloballs[indexOffset + 1] = gb.runUntil("catchSuccess", "catchFailure").equals("catchSuccess");
			}
		}
	}
	
	private void updateNPCTimers(Gb gb, ArrayList<Integer> timers[]) {
		for(int index = 1; index < NUM_NPCS; index++) {
			String addressPrefix = "wSprite" + StringUtils.getSpriteAddressIndexString(index);
			if(gb.read(addressPrefix + "SpriteImageIdx") != 0xFF) {
				timers[index].add(gb.read(addressPrefix + "MovementDelay"));
			}
		}
	}
	
	private boolean timeToPickUpItem(Gb gb, ArrayList<Itemball> itemballs) {
		for(Itemball itemball : itemballs) {
			if(itemball.canBePickedUp(gb) && !itemball.isPickedUp(gb)) {
				return true;
			}
		}
		return false;
	}
	
	private Location getDestination(Gb gb, int input) {
		int map = gb.read("wCurMap");
		int x = gb.read("wXCoord");
		int y = gb.read("wYCoord");
		if(input == LEFT) {
			return new Location(map, x == 0 ? 255 : x - 1, y);
		} else if(input == RIGHT) {
			return new Location(map, x + 1, y);
		} else if(input == UP) {
			return new Location(map, x, y == 0 ? 255 : y - 1);
		} else if(input == DOWN) {
			return new Location(map, x, y + 1);
		} else {
			return new Location(map, x, y);
		}
	}
}
