import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import java.awt.Graphics;
import org.dreambot.api.methods.skills.SkillTracker;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.methods.grandexchange.LivePrices;


@ScriptManifest(name = "Amethyst Miner", description = "Mines that sweet sweet purple crack rock", author = "Julian",
        version = 0.1, category = Category.MINING, image = "JCZEDTK.png")
//https://imgur.com/a/5l3lARv
//https://imgur.com/JCZEDTK
//https://i.imgur.com/JCZEDTK.png


public class AmethystMiner extends AbstractScript {
    //Script Variables
    //int[] pickaxes = {1275, 11920}; //rune, dragon
    //int[] inventoryItems = {21341, 1617, 1619, 1621, 1623, 21347}; //minerals, gems, amethyst
    Area amethystArea = new Area(3016, 9707, 3030, 9698);
    State state;

    //Paint Vars
    private Timer rt = new Timer();
    private int amethystPrice = LivePrices.get(21347);






    @Override
    public void onStart() {
        SkillTracker.start();
        SkillTracker.start(Skill.MINING);

    }
    @Override
    public int onLoop() {

        switch (getState()) {
            //case REQUIREMENTS_CHECK_FAIL -> LoggerSystem.exit(0);
            case FULL -> bank();
            case WALKING_TO_AMETHYST -> walkToAmethyst();
            case FINDING_AMETHYST -> finding_amethyst();
            case MINING_AMETHYST -> mine_amethyst();
        }

        return 1000;
    }
    private State getState() {
        if (!Inventory.isFull() && !amethystArea.contains(Players.getLocal().getTile())) {
            return State.WALKING_TO_AMETHYST;
        } else if (!Inventory.isFull() && !Players.getLocal().isAnimating() && amethystArea.contains(Players.getLocal().getTile())) {
            return State.FINDING_AMETHYST;
        } else if (!Inventory.isFull() && Players.getLocal().isAnimating() && amethystArea.contains(Players.getLocal().getTile())) {
            return State.MINING_AMETHYST;
        } else if (Inventory.isFull()) {
            return State.FULL;
        }


        return state;
    }
    @Override
    public void onPaint(Graphics g) {
        long xpTrack = SkillTracker.getGainedExperience(Skill.MINING);
        long amtMined = xpTrack/240;
        long estGold = amtMined * amethystPrice;

        // Example drawing operations
        g.drawString("Amethyst Miner v0.1", 5, 200);
        //g.drawString("Mining Level: " + SkillTracker.getStartLevel(Skill.MINING), 5, 220);
        g.drawString("Run Time: " + rt.formatTime(), 5, 240);
        g.drawString("Mining XP:" + xpTrack,5, 260);
        g.drawString("Ore: " + xpTrack/240, 5, 280);
        g.drawString("Gold Earned: " + estGold,5, 300);
        //g.drawString("State" + state, 5, 280);
        // Add more drawing operations as needed
    }

//SCRIPT METHODS
    public void walkToAmethyst() {
        if (!Players.getLocal().isMoving()) {
            Walking.walk(amethystArea.getRandomTile());
        }
    }
    public void finding_amethyst() {
        if (!Players.getLocal().isAnimating() && !Players.getLocal().isMoving()) {
            GameObject amethystRock = GameObjects.closest(11389, 11388);
            if (amethystRock != null && amethystRock.interact("Mine")) {
                Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 5000);
            }
        }
    }
    public void mine_amethyst() {
        //you manage to mine some amethyst
    }
    public boolean bank() {
        if (Bank.open(BankLocation.MINING_GUILD)) {
            Bank.depositAll(21341); //minerals
            Bank.depositAll(1617); // diamond
            Bank.depositAll(1623); // sapphire
            Bank.depositAll(1621); // emerald
            Bank.depositAll(1619); // ruby
            Bank.depositAll(21347); // amethyst
            Bank.close();
            return true;
            //21341, 1617, 1619, 1621, 1623, 21347
        }
        return false;
    }

}

