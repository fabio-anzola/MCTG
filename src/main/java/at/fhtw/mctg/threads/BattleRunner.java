package at.fhtw.mctg.threads;

import at.fhtw.mctg.controller.battle.BattleController;
import at.fhtw.mctg.dal.Repository.BattleRepository;
import at.fhtw.mctg.dal.Repository.CardRepository;
import at.fhtw.mctg.dal.Repository.UserRepository;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class BattleRunner implements Runnable {

    BattleController battleController;

    Battle battle;

    User userA;

    User userB;

    private ArrayList<Card> deckUserA;

    private ArrayList<Card> deckUserB;

    final int MAX_ROUNDS = 100;

    private Random random;

    public BattleRunner(Battle battle) {
        super();
        this.battle = battle;
        this.battleController = new BattleController();
        this.random = new Random();

        initVars();
    }

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
            if ((userA_card.getName() == "Goblin" && userB_card.getName() == "Dragon")) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Goblin is too afraid to attack!");
                damageA = 0;
            } else if ((userB_card.getName() == "Goblin" && userA_card.getName() == "Dragon")) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Goblin is too afraid to attack!");
                damageB = 0;
            }

            if ((userA_card.getName() == "Wizard" && userB_card.getName() == "Ork")) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Wizard controls Ork. No damage!");
                damageB = 0;
            } else if ((userB_card.getName() == "Ork" && userA_card.getName() == "Wizard")) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Wizard controls Ork. No damage!");
                damageA = 0;
            }

            if ((userA_card.getName() == "Knight" && (userB_card.getType() == CardType.SPELL && userB_card.getElement() == Elements.WATER))) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Knight drowns due to WaterSpell!");
                damageA = 0;
            } else if ((userB_card.getName() == "Knight" && (userA_card.getType() == CardType.SPELL && userA_card.getElement() == Elements.WATER))) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Knight drowns due to WaterSpell!");
                damageB = 0;
            }

            if ((userA_card.getName() == "Kraken" && userB_card.getType() == CardType.SPELL)) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Kraken is immune to spells!");
                damageB = 0;
            } else if ((userB_card.getName() == "Kraken" && userA_card.getType() == CardType.SPELL)) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Kraken is immune to spells!");
                damageA = 0;
            }

            if ((userA_card.getName() == "FireElf" && userB_card.getName() == "Dragon")) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> FireElf evades Dragon's attack!");
                damageB = 0;
            } else if ((userB_card.getName() == "FireElf" && userA_card.getName() == "Dragon")) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> FireElf evades Dragon's attack!");
                damageA = 0;
            }

            // Spell affects damage
            if (userA_card.getType() == CardType.SPELL || userB_card.getType() == CardType.SPELL) {
                new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "Spell affects the damage calculation!");

                // water -> fire
                if (userA_card.getElement() == Elements.WATER && userB_card.getElement() == Elements.FIRE) {
                    new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Fire > Water");
                    damageA = 2*damageA;
                } else if (userB_card.getElement() == Elements.WATER && userA_card.getElement() == Elements.FIRE) {
                    new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Fire > Water");
                    damageB = 2*damageB;
                }

                // fire -> normal
                if (userA_card.getElement() == Elements.FIRE && userB_card.getElement() == Elements.NORMAL) {
                    new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Fire > Normal");
                    damageA = 2*damageA;
                } else if (userB_card.getElement() == Elements.FIRE && userA_card.getElement() == Elements.NORMAL) {
                    new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Fire > Normal");
                    damageB = 2*damageB;
                }

                // normal -> water
                if (userA_card.getElement() == Elements.NORMAL && userB_card.getElement() == Elements.WATER) {
                    new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "->  Normal > Water");
                    damageA = 2*damageA;
                } else if (userB_card.getElement() == Elements.NORMAL && userA_card.getElement() == Elements.WATER) {
                    new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "-> Normal > Water");
                    damageB = 2*damageB;
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
        unitOfWork = new UnitOfWork();

        new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "### Rounds complete ###");
        if (deckUserA.isEmpty()) {
            new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "User B (" + (userB.getUsername()) + ") wins!");

            userA.setElo(userA.getElo() - 5);
            userB.setElo(userB.getElo() + 3);
            new BattleRepository(unitOfWork).finalizeUserBattle(battle.getBattleId(), userA.getUserId(), BattleStatus.LOSS);
            new BattleRepository(unitOfWork).finalizeUserBattle(battle.getBattleId(), userB.getUserId(), BattleStatus.WIN);
        } else if (deckUserB.isEmpty()) {
            new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "User A (" + (userA.getUsername()) + ") wins!");

            userA.setElo(userA.getElo() + 3);
            userB.setElo(userB.getElo() - 5);
            new BattleRepository(unitOfWork).finalizeUserBattle(battle.getBattleId(), userA.getUserId(), BattleStatus.LOSS);
            new BattleRepository(unitOfWork).finalizeUserBattle(battle.getBattleId(), userB.getUserId(), BattleStatus.WIN);
        } else {
            new BattleRepository(unitOfWork).addLogLine(this.battle.getBattleId(), "Battle is a draw!");

            new BattleRepository(unitOfWork).finalizeUserBattle(battle.getBattleId(), userA.getUserId(), BattleStatus.TIE);
            new BattleRepository(unitOfWork).finalizeUserBattle(battle.getBattleId(), userB.getUserId(), BattleStatus.TIE);
        }

        // set battle infos
        new BattleRepository(unitOfWork).finalizeBattle(battle.getBattleId(), new Timestamp(System.currentTimeMillis()), round);

        // set user stats
        // ELO
        // +3 win
        // -5 loss
        new UserRepository(unitOfWork).updateUserByName(userA.getUsername(), userA);
        new UserRepository(unitOfWork).updateUserByName(userA.getUsername(), userB);

        unitOfWork.commitTransaction();
    }

    private void initVars() {
        UnitOfWork unitOfWork = new UnitOfWork();
        ArrayList<UserBattle> users = (ArrayList<UserBattle>) new BattleRepository(unitOfWork).getBattleUsers(this.battle.getBattleId());
        System.out.println(users.size());

        // Init users
        this.userA = new UserRepository(unitOfWork).getUserById(users.get(0).getUserId());
        this.userB = new UserRepository(unitOfWork).getUserById(users.get(1).getUserId());

        // Init decks
        this.deckUserA = (ArrayList<Card>) new CardRepository(unitOfWork).getCardsByUserId(this.userA.getUserId());
        this.deckUserB = (ArrayList<Card>) new CardRepository(unitOfWork).getCardsByUserId(this.userB.getUserId());

        unitOfWork.commitTransaction();
    }
}
