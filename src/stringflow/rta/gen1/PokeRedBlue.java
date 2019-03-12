package stringflow.rta.gen1;

import stringflow.rta.Address;
import stringflow.rta.Strat;
import stringflow.rta.libgambatte.IInjectCallback;
import sudotrainer.gen1.Data.Enums.PokemonGames;

import static stringflow.rta.Joypad.*;

public class PokeRedBlue extends Gen1Game {
	
	public PokeRedBlue() {
		super("roms/pokered.sym",0xFFF8, 0xFFD3, 0xFFD4);
		addressList.add(new Address("igtInject", 0x1C766A));
		addressList.add(new Address("biosReadKeypad", 0x021D));
		addressList.add(new Address("catchSuccess", 0x35868));
		addressList.add(new Address("catchFailure", 0x35922));
		addressList.add(new Address("encounterTest", 0x0478C4));
		
		stratList.add(new Strat("pal", "_pal", 0, new Object[] { "biosReadKeypad" }, new Integer[] { UP }, new Integer[] { 1 }));
		stratList.add(new Strat("nopal", "_nopal", 0, new Object[] { "biosReadKeypad" }, new Integer[] { NO_INPUT }, new Integer[] { 1 }));
		stratList.add(new Strat("abss", "_nopal(ab)", 0, new Object[] { "biosReadKeypad", "Init" }, new Integer[] { A, A }, new Integer[] { 0, 0 }));
		stratList.add(new Strat("holdpal", "_pal(hold)", 0, new Object[] { "biosReadKeypad", "Init" }, new Integer[] { A | LEFT, A | LEFT }, new Integer[] { 0, 0 }));
		stratList.add(new Strat("cheatpal", "_pal(ab)", 0, new Object[] { "biosReadKeypad", "biosReadKeypad", "Init" }, new Integer[] { UP, UP | A, UP | A }, new Integer[] { 70, 0, 0 }));
		stratList.add(new Strat("gfSkip", "", 0, new Object[] { "joypad" }, new Integer[] { UP | SELECT | B }, new Integer[] { 1 }));
		stratList.add(new Strat("intro0", "_hop0", 0, new Object[] { "joypad" }, new Integer[] { UP | SELECT | B }, new Integer[] { 1 }));
		stratList.add(new Strat("title", "", 0, new Object[] { "joypad" }, new Integer[] { START }, new Integer[] { 1 }));
		stratList.add(new Strat("cont", "", 0, new Object[] { "joypad" }, new Integer[] { A }, new Integer[] { 1 }));
		this.game = PokemonGames.Red;
	}
	
	public void writeChecksum(byte sram[]) {
		int checksum = 0;
		for (int i = 0x2598; i < 0x3523; i++) {
			checksum += sram[i] & 0xFF;
		}
		sram[0x3523] = (byte) ((checksum & 0xFF) ^ 0xFF);
	}
}