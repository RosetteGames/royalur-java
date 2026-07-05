package net.royalur.engine.simple;

import net.royalur.model.GameMetadata;
import net.royalur.model.GameSettings;
import net.royalur.engine.*;


/**
 * A provider that creates simple rule sets.
 */
public class SimpleEngineProvider implements EngineProvider {

    @Override
    public SimpleEngine create(
            GameSettings settings,
            GameMetadata metadata
    ) {
        SimplePieceProvider pieceProvider = new SimplePieceProvider();
        SimplePlayerStateProvider stateProvider = new SimplePlayerStateProvider(
                settings.getStartingPieceCount()
        );
        return new SimpleEngine(
                settings.getBoardShape(),
                settings.getPaths(),
                settings.getDice(),
                pieceProvider,
                stateProvider,
                settings.areRosettesSafe(),
                settings.doRosettesGrantExtraRolls(),
                settings.doCapturesGrantExtraRolls()
        );
    }
}
