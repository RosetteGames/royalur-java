package net.royalur.notation;

import net.royalur.model.AbandonReason;
import net.royalur.model.Move;
import net.royalur.model.PlayerType;
import net.royalur.model.dice.Roll;
import net.royalur.engine.Engine;
import net.royalur.engine.state.*;

import javax.annotation.Nullable;

/**
 * Produces game states from serialised information.
 */
public abstract class StateSource {

    public abstract RolledGameState createRolledState(
            Engine rules,
            long timeSinceGameStartMs,
            PlayerType turn,
            Roll roll
    );

    public abstract MovedGameState createMovedState(
            Engine rules,
            long timeSinceGameStartMs,
            PlayerType turn,
            Roll roll,
            Move move
    );

    public abstract WaitingForRollGameState createWaitingForRollState(
            Engine rules,
            long timeSinceGameStartMs,
            PlayerType turn
    );

    public abstract WaitingForMoveGameState createWaitingForMoveState(
            Engine rules,
            long timeSinceGameStartMs,
            PlayerType turn,
            Roll roll
    );

    public abstract ResignedGameState createResignedState(
            Engine rules,
            long timeSinceGameStartMs,
            PlayerType player
    );

    public abstract AbandonedGameState createAbandonedState(
            Engine rules,
            long timeSinceGameStartMs,
            AbandonReason abandonReason,
            @Nullable PlayerType player
    );

    public abstract EndGameState createEndState(
            Engine rules,
            long timeSinceGameStartMs,
            @Nullable PlayerType winner
    );
}
