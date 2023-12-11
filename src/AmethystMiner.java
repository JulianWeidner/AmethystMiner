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

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.widgets.message.Message;





@ScriptManifest(name = "Amethyst Miner", description = "Mines that sweet sweet purple crack rock", author = "Julian",
        version = 0.1, category = Category.MINING, image = "https://imgur.com/a/5l3lARv")
//https://imgur.com/a/5l3lARv


public class AmethystMiner extends AbstractScript {
    //Class Variables
    int[] pickaxes = {1275, 11920}; //rune, dragon
    int[] inventoryItems = {21341, 1617, 1619, 1621, 1623, 21347}; //minerals, gems, amethyst

    Area amethystArea = new Area(3016, 9707, 3030, 9698);
    State state;

    int initMiningLevel = Skill.MINING.getLevel();
    int initMiningExp = Skill.MINING.getExperience();

    @Override
    public void onStart(){
        Logger.log("On start override");
    }

    @Override
    public int onLoop() {


        switch (getState()){
            //case REQUIREMENTS_CHECK_FAIL -> LoggerSystem.exit(0);
            case FULL -> bank();
            case WALKING_TO_AMETHYST ->  walkToAmethyst();
            case FINDING_AMETHYST -> finding_amethyst();
            case MINING_AMETHYST -> mine_amethyst();
        }

        return 1000;
    }



    private State getState(){
        if (!Inventory.isFull() && !amethystArea.contains(Players.getLocal().getTile())){
            return State.WALKING_TO_AMETHYST;
        }
        else if (!Inventory.isFull() && !Players.getLocal().isAnimating() && amethystArea.contains(Players.getLocal().getTile())){
            return State.FINDING_AMETHYST;
        }
        else if (!Inventory.isFull() && Players.getLocal().isAnimating() && amethystArea.contains(Players.getLocal().getTile())){
            return State.MINING_AMETHYST;
        }
        else if (Inventory.isFull()) {
            return State.FULL;
        }


        return state;
    }



    public void walkToAmethyst(){
        if(!Players.getLocal().isMoving()) {
            Walking.walk(amethystArea.getRandomTile());
        }
    }

    public void finding_amethyst(){
        if (!Players.getLocal().isAnimating() && !Players.getLocal().isMoving()){
            GameObject amethystRock = GameObjects.closest(11389,11388);
            if (amethystRock != null && amethystRock.interact("Mine")){
                Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 5000);
            }
        }
    }

    public void mine_amethyst(){

    }
    public boolean bank(){
        if (Bank.open(BankLocation.MINING_GUILD)){
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

    public boolean requirementsCheck(){
        //mining level check
        if (mining_level_check(92)){
            Logger.log("Mining Level Pass");
        } else {
            Logger.log("Mining Level Fail");
            return false;
        }
        //pickaxe check pickaxes equipped, inventory
        if (!equipped_pickaxe_check(pickaxes) || !inventory_pickaxe_check(pickaxes)){
            Logger.log("Pickaxe Not Found");
            return false;

        }

        return true;
    }
    public boolean mining_level_check(int requirement){
        //get player skill level, return true or false on conditional based on the requirement param
        return  Skills.getRealLevel(Skill.MINING) >= requirement;
    }
    public boolean equipped_pickaxe_check(int[] p_pickaxes){
        return Equipment.contains(p_pickaxes);
    }
    public boolean inventory_pickaxe_check(int[] p_pickaxes){
        return Inventory.contains(p_pickaxes);
    }


    @Override
    public void onPaint(Graphics g) {
        // Example drawing operations
        g.drawString("SCRIPT", 5, 321);
        g.drawString("Mining Level: " + initMiningLevel, 5, 331);
        g.drawString("Run Time: " + rt.formatTime(), 5, 340);
        // Add more drawing operations as needed
    }

    private Timer rt = new Timer();
    private int mined = 0;



    public boolean bonusEquipmentCheck(){
        //Varrock armour
        //prospector
        //expert mining gloves
        //signet
        //charged glory
        //mining cape
        return false;
    }

}

