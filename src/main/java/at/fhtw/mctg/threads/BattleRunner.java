package at.fhtw.mctg.threads;

import at.fhtw.mctg.controller.battle.BattleController;
import at.fhtw.mctg.dal.Repository.BattleRepository;
import at.fhtw.mctg.dal.Repository.CardRepository;
import at.fhtw.mctg.dal.Repository.UserRepository;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class represents the task for managing and executing battles between two users
 * in a card game. It implements the Runnable interface to allow battles to be processed
 * in a separate thread.
 * <p>
 * The `BattleRunner` handles the complete battle logic, including:
 * - Initializing the battle state and variables.
 * - Logging battle-related events and rounds.
 * - Executing battle rounds, where user cards are selected and combat is determined.
 * - Applying game-specific rules and mechanics for card interactions.
 * - Determining the winner of each round.
 * - Ensuring the battle runs for a maximum number of defined rounds or until one user's deck is empty.
 */
public class BattleRunner implements Runnable {

    /**
     * Instance of the BattleController class to manage battle-related operations.
     */
    BattleController battleController;

    /**
     * Represents the current battle.
     */
    Battle battle;

    /**
     * Represents the first user (User A) participating in a battle.
     */
    User userA;

    /**
     * Represents the second User participating in the battle.
     */
    User userB;

    /**
     * Represents the deck of cards belonging to User A in the context of a battle.
     */
    private ArrayList<Card> deckUserA;

    /**
     * Represents the deck of cards belonging to User B in the context of a battle.
     */
    private ArrayList<Card> deckUserB;

    /**
     * Defines the maximum number of battle rounds allowed in a game.
     */
    final int MAX_ROUNDS = 100;

    /**
     * An instance of the Random class used for generating random values during the execution of the BattleRunner class.
     */
    private final Random random;

    /**
     * Constructs a new BattleRunner instance to manage and run a battle.
     *
     * @param battle the Battle instance representing the battle to be managed,
     *               containing details such as participants, rounds, and unique identifiers.
     */
    public BattleRunner(Battle battle) {
        this.battle = battle;
        this.battleController = new BattleController();
        this.random = new Random();

        initVars();
    }

    /**
     * Executes the battle logic for a defined number of rounds or until one player's deck is empty.
     * The method handles the initialization, execution of battle rounds, applying custom rules,
     * specifying battle mechanics, and determining the winner based on card damages.
     * <p>
     * Key actions performed by this method:
     * - Logs the starting details of the battle, including usernames of participants.
     * - Sets the battle start time and ensures proper transactional handling.
     * - Executes individual rounds with rules determining interactions between cards (e.g., special abilities and elemental type advantages).
     * - Applies predefined custom rules based on specific conditions within the game.
     * - Logs detailed battle progress, including card selections, applied rules, and round outcomes.
     * - Processes card exchanges between players' decks based on win/loss outcomes of rounds.
     * - Handles the termination of the battle either by reaching the maximum number of rounds or by deck depletion.
     * - Records comprehensive logs of every significant battle event and commits changes within transactions.
     * <p>
     * Throws runtime exceptions for any issues during the transaction close process.
     */
    @Override
    public void run() {
        UnitOfWork unitOfWork = new UnitOfWork();
        new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "User A is : " + userA.getUsername());
        new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "User B is : " + userA.getUsername());

        // Set the start time for the battle
        new BattleRepository(unitOfWork).setBattleStart(battle.getBattleId(), new Timestamp(System.currentTimeMillis()));

        unitOfWork.commitTransaction();
        // Battle logic
        int round = 0;

        while (round < MAX_ROUNDS && !this.deckUserA.isEmpty() && !this.deckUserB.isEmpty()) {
            System.out.println("round = " + round);
            System.out.println("this.deckUserA.size() = " + this.deckUserA.size());
            System.out.println("this.deckUserB.size() = " + this.deckUserB.size());
            round++;

            unitOfWork = new UnitOfWork();

            new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "Starting round " + round);

            // Select random card
            Card userA_card = this.deckUserA.get(this.random.nextInt(deckUserA.size()));
            Card userB_card = this.deckUserB.get(this.random.nextInt(deckUserB.size()));

            // get damage values to edit
            int damageA = userA_card.getDamage();
            int damageB = userB_card.getDamage();

            // Log cards
            new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "Card of User A: " + userA_card.getName() + " with damage " + userA_card.getDamage());
            new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "Card of User B: " + userB_card.getName() + " with damage " + userB_card.getDamage());

            // Custom Rule!
            if ((deckUserA.size() == 1 || deckUserB.size() == 1) && (round % 2 == 0)) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "Custom Rule active!");
                if (userA.getElo() > userB.getElo()) {
                    userB_card.setDamage(userB_card.getDamage() * 2);
                } else if (userA.getElo() < userB.getElo()) {
                    userA_card.setDamage(userA_card.getDamage() * 2);
                }
            }

            // Special rules
            if (("Goblin".equals(userA_card.getName()) && "Dragon".equals(userB_card.getName()))) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Goblin is too afraid to attack!");
                damageA = 0;
            } else if (("Goblin".equals(userB_card.getName()) && "Dragon".equals(userA_card.getName()))) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Goblin is too afraid to attack!");
                damageB = 0;
            }

            if (("Wizard".equals(userA_card.getName()) && "Ork".equals(userB_card.getName()))) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Wizard controls Ork. No damage!");
                damageB = 0;
            } else if (("Ork".equals(userB_card.getName()) && "Wizard".equals(userA_card.getName()))) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Wizard controls Ork. No damage!");
                damageA = 0;
            }

            if (("Knight".equals(userA_card.getName()) && (userB_card.getType() == CardType.SPELL && userB_card.getElement() == Elements.WATER))) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Knight drowns due to WaterSpell!");
                damageA = 0;
            } else if (("Knight".equals(userB_card.getName()) && (userA_card.getType() == CardType.SPELL && userA_card.getElement() == Elements.WATER))) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Knight drowns due to WaterSpell!");
                damageB = 0;
            }

            if (("Kraken".equals(userA_card.getName()) && userB_card.getType() == CardType.SPELL)) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Kraken is immune to spells!");
                damageB = 0;
            } else if (("Kraken".equals(userB_card.getName()) && userA_card.getType() == CardType.SPELL)) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Kraken is immune to spells!");
                damageA = 0;
            }

            if (("FireElf".equals(userA_card.getName()) && "Dragon".equals(userB_card.getName()))) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> FireElf evades Dragon's attack!");
                damageB = 0;
            } else if (("FireElf".equals(userB_card.getName()) && "Dragon".equals(userA_card.getName()))) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> FireElf evades Dragon's attack!");
                damageA = 0;
            }

            // Spell affects damage
            if (userA_card.getType() == CardType.SPELL || userB_card.getType() == CardType.SPELL) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "Spell affects the damage calculation!");

                // water -> fire
                if (userA_card.getElement() == Elements.WATER && userB_card.getElement() == Elements.FIRE) {
                    new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Fire > Water");
                    damageA = 2 * damageA;
                } else if (userB_card.getElement() == Elements.WATER && userA_card.getElement() == Elements.FIRE) {
                    new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Fire > Water");
                    damageB = 2 * damageB;
                }

                // fire -> normal
                if (userA_card.getElement() == Elements.FIRE && userB_card.getElement() == Elements.NORMAL) {
                    new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Fire > Normal");
                    damageA = 2 * damageA;
                } else if (userB_card.getElement() == Elements.FIRE && userA_card.getElement() == Elements.NORMAL) {
                    new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Fire > Normal");
                    damageB = 2 * damageB;
                }

                // normal -> water
                if (userA_card.getElement() == Elements.NORMAL && userB_card.getElement() == Elements.WATER) {
                    new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "->  Normal > Water");
                    damageA = 2 * damageA;
                } else if (userB_card.getElement() == Elements.NORMAL && userA_card.getElement() == Elements.WATER) {
                    new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Normal > Water");
                    damageB = 2 * damageB;
                }
            }

            // determine winner based on damage
            new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "Damage is " + damageA + " (A) vs " + damageB + "(B)");
            if (damageA > damageB) {
                // user A wins
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "User A wins round " + round);
                deckUserA.add(userB_card);
                deckUserB.remove(userB_card);
            } else if (damageA < damageB) {
                // user B wins
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "User B wins round " + round);
                deckUserB.add(userA_card);
                deckUserA.remove(userA_card);
            } else {
                // tie
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "Round " + round + " is a tie");
            }


            unitOfWork.commitTransaction();
            unitOfWork.finishWork();
            try {
                unitOfWork.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // Overall winner
        UnitOfWork unitOfWorkOverall = new UnitOfWork();
        try (unitOfWorkOverall) {

            new BattleRepository(unitOfWorkOverall).addLogLine(this.battle.getBattleId(), "### Rounds complete ###");
            if (deckUserA.isEmpty()) {
                new BattleRepository(unitOfWorkOverall).addLogLine(this.battle.getBattleId(), "User B (" + (userB.getUsername()) + ") wins!");

                userA.setElo(userA.getElo() - 5);
                userB.setElo(userB.getElo() + 3);
                new BattleRepository(unitOfWorkOverall).finalizeUserBattle(battle.getBattleId(), userA.getUserId(), BattleStatus.LOSS);
                new BattleRepository(unitOfWorkOverall).finalizeUserBattle(battle.getBattleId(), userB.getUserId(), BattleStatus.WIN);
            } else if (deckUserB.isEmpty()) {
                new BattleRepository(unitOfWorkOverall).addLogLine(this.battle.getBattleId(), "User A (" + (userA.getUsername()) + ") wins!");

                userA.setElo(userA.getElo() + 3);
                userB.setElo(userB.getElo() - 5);
                new BattleRepository(unitOfWorkOverall).finalizeUserBattle(battle.getBattleId(), userA.getUserId(), BattleStatus.LOSS);
                new BattleRepository(unitOfWorkOverall).finalizeUserBattle(battle.getBattleId(), userB.getUserId(), BattleStatus.WIN);
            } else {
                new BattleRepository(unitOfWorkOverall).addLogLine(this.battle.getBattleId(), "Battle is a draw!");

                new BattleRepository(unitOfWorkOverall).finalizeUserBattle(battle.getBattleId(), userA.getUserId(), BattleStatus.TIE);
                new BattleRepository(unitOfWorkOverall).finalizeUserBattle(battle.getBattleId(), userB.getUserId(), BattleStatus.TIE);
            }

            // set battle infos
            new BattleRepository(unitOfWorkOverall).finalizeBattle(battle.getBattleId(), new Timestamp(System.currentTimeMillis()), round);

            // set user stats
            // ELO
            // +3 win
            // -5 loss
            new UserRepository(unitOfWorkOverall).updateUserByName(userA.getUsername(), userA);
            new UserRepository(unitOfWorkOverall).updateUserByName(userA.getUsername(), userB);

            unitOfWorkOverall.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();

            unitOfWorkOverall.rollbackTransaction();
        }
    }

    /**
     * Initializes battle-related variables required for executing the battle logic.
     *
     * This method performs the following operations:
     * 1. Instantiates a new UnitOfWork object to manage database transactions.
     * 2. Retrieves a list of UserBattle objects representing the participants of the battle.
     * 3. Initializes the two users involved in the battle using their user IDs.
     * 4. Retrieves and initializes the card decks for both users.
     * 5. Commits the current transaction to persist changes made during initialization.
     */
    private void initVars() {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (unitOfWork) {

        ArrayList<UserBattle> users = (ArrayList<UserBattle>) new BattleRepository(unitOfWork).getBattleUsers(this.battle.getBattleId());

        // Init users
        this.userA = new UserRepository(unitOfWork).getUserById(users.get(0).getUserId());
        this.userB = new UserRepository(unitOfWork).getUserById(users.get(1).getUserId());

        // Init decks
        this.deckUserA = (ArrayList<Card>) new CardRepository(unitOfWork).getCardsByUserId(this.userA.getUserId());
        this.deckUserB = (ArrayList<Card>) new CardRepository(unitOfWork).getCardsByUserId(this.userB.getUserId());

        unitOfWork.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();

            unitOfWork.rollbackTransaction();
        }
    }
}
