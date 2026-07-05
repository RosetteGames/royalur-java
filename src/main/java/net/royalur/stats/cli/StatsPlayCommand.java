package net.royalur.stats.cli;

import net.royalur.Game;
import net.royalur.agent.Agent;
import net.royalur.cli.*;
import net.royalur.model.GameSettings;
import net.royalur.model.PlayerType;

import javax.annotation.Nullable;
import java.io.IOException;

public class StatsPlayCommand extends CLICommand {

    public static final String NAME = "play";
    public static final String DESC = "Play games between two agents and report win statistics";

    public StatsPlayCommand(CLICommand parent) {
        super(parent, NAME, DESC);
        addRequiredArg(
                "<agent1>", CLIArgumentType.TEXT,
                "First agent specification",
                "(e.g., random, greedy, lut:model.rgu)"
        );
        addOptionalArg(
                "[agent2]", CLIArgumentType.TEXT,
                "Second agent specification",
                "(defaults to same as agent1)"
        );
        addOptionalArg(
                "--rules", CLIArgumentType.TEXT,
                "Game rules to use (default: finkel)",
                "Options: finkel, blitz, masters"
        );
        addOptionalArg(
                "--game-count", CLIArgumentType.INTEGER,
                "Number of games to play (default: 100)"
        );
    }

    @Override
    public @Nullable CLIHandler handle(CLI cli) throws CLIException, IOException {
        // Required positional argument
        String agent1Spec = cli.next();

        // Optional positional argument
        String agent2Spec = cli.hasNext() ? cli.next() : agent1Spec;

        // Parse named arguments
        String rulesSpec = cli.readKeywordOrNull("--rules");
        GameSettings settings = (rulesSpec != null)
                ? RulesParser.parseRules(rulesSpec)
                : GameSettings.FINKEL;

        String gameCountStr = cli.readKeywordOrNull("--game-count");
        final int numGames;
        if (gameCountStr != null) {
            try {
                int count = Integer.parseInt(gameCountStr);
                if (count <= 0) {
                    throw new CLIArgumentException("Number of games must be positive");
                }
                numGames = count;
            } catch (NumberFormatException e) {
                throw new CLIArgumentException("Invalid game count: " + gameCountStr);
            }
        } else {
            numGames = 100; // default
        }

        // Ensure no remaining arguments
        cli.expectEmpty();

        return () -> playGames(agent1Spec, agent2Spec, numGames, settings);
    }

    private static void playGames(
            String agent1Spec,
            String agent2Spec,
            int numGames,
            GameSettings settings
    ) throws IOException, CLIException {
        System.out.println("Playing " + numGames + " games between agents...");
        System.out.println("Agent 1: " + agent1Spec);
        System.out.println("Agent 2: " + agent2Spec);
        System.out.println("Settings: " + settings.getName());
        System.out.println();

        // Parse the agents
        Agent agent1 = AgentParser.parseAgent(agent1Spec, null);
        Agent agent2 = AgentParser.parseAgent(agent2Spec, null);

        // Track statistics
        int agent1Wins = 0;
        int agent2Wins = 0;
        int lightWins = 0;
        int darkWins = 0;

        long startTime = System.nanoTime();

        // Run the games
        for (int i = 0; i < numGames; i++) {
            // Alternate which agent plays as light/dark for fairness
            boolean swap = (i % 2 == 1);

            Game game = Game.create(settings);
            Agent lightAgent = swap ? agent2 : agent1;
            Agent darkAgent = swap ? agent1 : agent2;

            Agent.playAutonomously(game, lightAgent, darkAgent);

            // Record who won
            PlayerType winner = game.getWinner();
            if (winner == PlayerType.LIGHT) {
                lightWins++;
                if (!swap) {
                    agent1Wins++;
                } else {
                    agent2Wins++;
                }
            } else {
                darkWins++;
                if (swap) {
                    agent1Wins++;
                } else {
                    agent2Wins++;
                }
            }

            // Progress indicator
            if ((i + 1) % 1000 == 0) {
                System.out.println("Completed " + (i + 1) + "/" + numGames + " games...");
            }
        }

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        double msPerGame = durationMs / numGames;

        // Report results
        System.out.println();
        System.out.println("=== Results ===");
        System.out.printf("Agent 1 wins: %d (%.2f%%)%n", agent1Wins, 100.0 * agent1Wins / numGames);
        System.out.printf("Agent 2 wins: %d (%.2f%%)%n", agent2Wins, 100.0 * agent2Wins / numGames);
        System.out.println();
        System.out.printf("Light wins: %d (%.2f%%)%n", lightWins, 100.0 * lightWins / numGames);
        System.out.printf("Dark wins: %d (%.2f%%)%n", darkWins, 100.0 * darkWins / numGames);
        System.out.println();
        System.out.printf("Total time: %.2f ms (%.3f ms/game)%n", durationMs, msPerGame);
    }
}
