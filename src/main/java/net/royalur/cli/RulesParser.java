package net.royalur.cli;

import net.royalur.model.GameSettings;

/**
 * Parser for game rules specifications from CLI arguments.
 */
public class RulesParser {

    /**
     * Private constructor to prevent instantiation.
     */
    private RulesParser() {}

    /**
     * Parses a rules specification string into GameSettings.
     *
     * @param spec The rules specification (e.g., "finkel", "blitz", "masters")
     * @return The parsed GameSettings
     * @throws CLIException if the specification is invalid
     */
    public static GameSettings parseRules(String spec) throws CLIException {
        if (spec == null || spec.isEmpty()) {
            throw new CLIArgumentException("Rules specification cannot be empty");
        }

        RuleType ruleType = parseRuleType(spec);
        return getGameSettings(ruleType);
    }

    /**
     * Parses a rule type from a string.
     *
     * @param typeName The rule type name (case-insensitive)
     * @return The parsed RuleType
     * @throws CLIException if the type name is invalid
     */
    public static RuleType parseRuleType(String typeName) throws CLIException {
        switch (typeName.toLowerCase()) {
            case "finkel":
                return RuleType.FINKEL;
            case "blitz":
                return RuleType.BLITZ;
            case "masters":
                return RuleType.MASTERS;
            default:
                throw new CLIArgumentException(
                    "Unknown rule type: " + typeName + ". "
                            + "Available types: finkel, blitz, masters"
                );
        }
    }

    /**
     * Gets the GameSettings for a given rule type.
     *
     * @param type The rule type
     * @return The corresponding GameSettings
     */
    public static GameSettings getGameSettings(RuleType type) {
        switch (type) {
            case FINKEL:
                return GameSettings.FINKEL;
            case BLITZ:
                return GameSettings.BLITZ;
            case MASTERS:
                return GameSettings.MASTERS;
            default:
                throw new IllegalArgumentException("Unsupported rule type: " + type);
        }
    }
}
