package net.royalur.cli;

import net.royalur.agent.*;
import net.royalur.lut.Lut;
import net.royalur.engine.Engine;

import java.io.File;
import java.io.IOException;

/**
 * Parser for agent specifications from CLI arguments.
 */
public class AgentParser {

    /**
     * Private constructor to prevent instantiation.
     */
    private AgentParser() {}

    /**
     * Parses an agent specification string into an Agent instance.
     *
     * @param spec The agent specification (e.g., "random", "greedy", "lut:model.rgu")
     * @param rules The rules to use for the agent (can be null for non-LUT agents)
     * @return The parsed Agent instance
     * @throws CLIException if the specification is invalid
     * @throws IOException if there's an error reading a LUT file
     */
    public static Agent parseAgent(String spec, Engine rules) throws CLIException, IOException {
        if (spec == null || spec.isEmpty()) {
            throw new CLIArgumentException("Agent specification cannot be empty");
        }

        // Check if it's a LUT specification
        if (spec.startsWith("lut:")) {
            String lutPath = spec.substring(4);
            if (lutPath.isEmpty()) {
                throw new CLIArgumentException("LUT agent requires a model file path after 'lut:'");
            }

            File lutFile = new File(lutPath);
            if (!lutFile.exists()) {
                throw new CLIArgumentException("LUT file not found: " + lutPath);
            }

            try {
                Lut lut = Lut.read(lutFile);
                return new LutAgent(lut);
            } catch (IOException e) {
                throw new IOException("Failed to read LUT file: " + lutPath, e);
            }
        }

        // Otherwise, parse as agent type
        AgentType agentType = parseAgentType(spec);
        return createAgent(agentType, rules);
    }

    /**
     * Parses an agent type from a string.
     *
     * @param typeName The agent type name (case-insensitive)
     * @return The parsed AgentType
     * @throws CLIException if the type name is invalid
     */
    public static AgentType parseAgentType(String typeName) throws CLIException {
        switch (typeName.toLowerCase()) {
            case "random":
                return AgentType.RANDOM;
            case "greedy":
                return AgentType.GREEDY;
            case "least-advanced":
            case "leastadvanced":
            case "least_advanced":
                return AgentType.LEAST_ADVANCED;
            case "lut":
                throw new CLIArgumentException(
                    "LUT agent requires a model file path (e.g., 'lut:models/finkel.rgu')"
                );
            default:
                throw new CLIArgumentException(
                    "Unknown agent type: " + typeName + ". "
                            + "Available types: random, greedy, least-advanced, lut:<model-file>"
                );
        }
    }

    /**
     * Creates an agent instance from an agent type.
     *
     * @param type The agent type
     * @param rules The rules to use (can be null for some agents)
     * @return The created Agent instance
     */
    public static Agent createAgent(AgentType type, Engine rules) {
        switch (type) {
            case RANDOM:
                return new RandomAgent();
            case GREEDY:
                return new GreedyAgent();
            case LEAST_ADVANCED:
                return new LeastAdvancedGreedyAgent();
            case LUT:
                throw new IllegalArgumentException("LUT agents must be created with a Lut instance");
            default:
                throw new IllegalArgumentException("Unsupported agent type: " + type);
        }
    }
}
