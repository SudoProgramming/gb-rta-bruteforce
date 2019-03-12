package sudotrainer.gen1.GB;

import stringflow.rta.Strat;
import stringflow.rta.gen1.Gen1Game;
import sudotrainer.gen1.Data.Configurations;
import sudotrainer.gen1.Data.Enums.PokemonGames;
import sudotrainer.gen1.Data.Enums.RomExtensions;

import java.util.HashMap;

public class GBHelper {
    public static HashMap<PokemonGames, RomExtensions> gameExtensions = new HashMap<PokemonGames, RomExtensions>() {{
       put(PokemonGames.Red, RomExtensions.gb);
       put(PokemonGames.Blue, RomExtensions.gb);
       put(PokemonGames.Yellow, RomExtensions.gbc);
       put(PokemonGames.Gold, RomExtensions.gbc);
       put(PokemonGames.Silver, RomExtensions.gbc);
       put(PokemonGames.Crystal, RomExtensions.gbc);
    }};

    public static String getDefaultRomPath(Gen1Game game) {
        return String.format(Configurations.DEFAULT_ROM_PATH_UNFORMATED, game.getGame().toString().toLowerCase(), gameExtensions.get(game.getGame()).toString().toLowerCase());
    }

    public static Strat[] GetDefaultIntroStrats(Gen1Game game) {
        switch (game.getGame()) {
            case Yellow:
                return new  Strat[] { game.getStrat("gfSkip"),game.getStrat("intro0"),game.getStrat("title") };
            default:
                return new Strat[] {};
        }
    }

    public static Strat[] GetDefaultContinueStrats(Gen1Game game) {
        switch (game.getGame()) {
            case Yellow:
                return new  Strat[] { game.getStrat("cont"), game.getStrat("cont") };
            default:
                return new Strat[] {};
        }
    }
}
