package sudotrainer.gen1.GB;

import stringflow.rta.IGTState;
import stringflow.rta.InputDisplay;
import stringflow.rta.Strat;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.util.IGTTimeStamp;
import sudotrainer.gen1.Data.Configurations;
import sudotrainer.gen1.Data.Enums.Addresses;

import stringflow.rta.libgambatte.Gb;
import stringflow.rta.gen1.Gen1Game;

import java.util.ArrayList;

public class GBController {
    private static int defaultFlags = LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV;

    public static Gb loadGameboy(Gen1Game game, int renderScale, boolean withInputDisplay) {
        stringflow.rta.libgambatte.Gb gb = new Gb();
        gb.loadBios(Configurations.GBC_BIOS_PATH);
        String romName = GBHelper.getDefaultRomPath(game);
        gb.loadRom(GBHelper.getDefaultRomPath(game), game, defaultFlags);
        if (renderScale >0) {
            gb.createRenderContext(renderScale);
        }
        if (withInputDisplay) {
            gb.setOnDisplayUpdate(new InputDisplay());
        }
        Strat[] intro = GBHelper.GetDefaultIntroStrats(game);
        for (int i = 0; i < intro.length; i++) {
            intro[i].execute(gb);
        }
        gb.runUntil(Addresses.IGT_INJECT);
        gb.setInitialStates(getInitialStates(gb, game, 1));
        return gb;
    }

    public static ArrayList<IGTState> getInitialStates(Gb gb, Gen1Game game, int maxSecond) {
        byte igtState[] = gb.saveState();
        ArrayList<IGTState> initialStates = new ArrayList<>();
        for (int second = 0; second < maxSecond; second++) {
            for (int frame = 0; frame < 60; frame++) {
                gb.loadState(igtState);
                gb.write("wPlayTimeSeconds", second);
                gb.write("wPlayTimeFrames", frame);
                Strat[] continueStrats = GBHelper.GetDefaultContinueStrats(game);
                for (int i = 0; i < continueStrats.length; i++) {
                    continueStrats[i].execute(gb);
                }

                gb.runUntil(Addresses.OVERWOLRD);
                initialStates.add(new IGTState(new IGTTimeStamp(0, 0, second, frame), gb.saveState()));
            }
        }

        return initialStates;
    }
}
