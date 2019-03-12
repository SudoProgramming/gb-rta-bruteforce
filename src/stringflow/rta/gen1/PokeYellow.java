package stringflow.rta.gen1;

import stringflow.rta.Address;
import stringflow.rta.Strat;
import stringflow.rta.libgambatte.IInjectCallback;

import sudotrainer.gen1.Data.Enums.PokemonGames;

import static stringflow.rta.Joypad.*;

public class PokeYellow extends Gen1Game {
	
	public PokeYellow(String romPath) {
		super(romPath, 0xFFF5, 0xFFD3, 0xFFD4);
		addressList.add(new Address("igtInject", 0x1C79D6));
		addressList.add(new Address("catchSuccess", 0x355B1));
		addressList.add(new Address("catchFailure", 0x35681));
		addressList.add(new Address("encounterTest", 0x4788E));
		
		stratList.add(new Strat("gfSkip", "_gfskip", 0, new Object[] { "joypad"}, new Integer[] { START }, new Integer[] { 1 }));
		stratList.add(new Strat("gfWait", "_gfwait", 253, new Object[] { 0x41A74}, new Integer[] { NO_INPUT }, new Integer[] { 0 }));
		stratList.add(new Strat("intro0", "_intro0", 0, new Object[] { "joypad"}, new Integer[] { A }, new Integer[] { 1 }));
		stratList.add(new Strat("intro1", "_intro1", 140, new Object[] { "YellowIntroScene2", "joypad"}, new Integer[] { NO_INPUT, A }, new Integer[] { 0, 1 }));
		stratList.add(new Strat("intro2", "_intro2", 275, new Object[] { "YellowIntroScene4", "joypad"}, new Integer[] { NO_INPUT, A }, new Integer[] { 0, 1 }));
		stratList.add(new Strat("intro3", "_intro3", 411, new Object[] { "YellowIntroScene6", "joypad"}, new Integer[] { NO_INPUT, A }, new Integer[] { 0, 1 }));
		stratList.add(new Strat("intro4", "_intro4", 594, new Object[] { "YellowIntroScene8", "joypad"}, new Integer[] { NO_INPUT, A }, new Integer[] { 0, 1 }));
		stratList.add(new Strat("intro5", "_intro5", 729, new Object[] { "YellowIntroScene10", "joypad"}, new Integer[] { NO_INPUT, A }, new Integer[] { 0, 1 }));
		stratList.add(new Strat("intro6", "_intro6", 864, new Object[] { "YellowIntroScene22", "joypad"}, new Integer[] { NO_INPUT, A }, new Integer[] { 0, 1 }));
		stratList.add(new Strat("introwait", "_introwait", 147 + 1199, new Object[] { "DisplayTitleScreen"}, new Integer[] { NO_INPUT }, new Integer[] { 0 }));
		stratList.add(new Strat("cont", "_cont", 0, new Object[] { "joypad"}, new Integer[] { A }, new Integer[] { 1 }));
		stratList.add(new Strat("backout", "_backout", 140, new Object[] { "joypad"}, new Integer[] { B }, new Integer[] { 1 }));
		stratList.add(new Strat("title", "_title", 0, new Object[] { "joypad"}, new Integer[] { START }, new Integer[] { 1 }));
		this.game = PokemonGames.Yellow;
	}

	public PokeYellow() {
		this("roms/pokeyellow.sym");
	}
	
	public void writeChecksum(byte sram[]) {
		int checksum = 0;
		for (int i = 0x2598; i < 0x3523; i++) {
			checksum += sram[i] & 0xFF;
		}
		sram[0x3523] = (byte) ((checksum & 0xFF) ^ 0xFF);
	}
}
