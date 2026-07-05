package net.royalur.cli;

/**
 * The different types of agents that can play the Royal Game of Ur.
 */
public enum AgentType {
    /**
     * An agent that uses a lookup table to determine moves.
     */
    LUT,

    /**
     * An agent that uses a greedy strategy.
     */
    GREEDY,

    /**
     * An agent that prioritizes moving the least advanced pieces.
     */
    LEAST_ADVANCED,

    /**
     * An agent that makes random moves.
     */
    RANDOM
}
