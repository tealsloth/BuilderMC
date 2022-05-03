package me.rowan.plugin.traits;

import me.rowan.plugin.Instruction;
import me.rowan.plugin.Main;
import me.rowan.plugin.handlers.FileHandler;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class BuilderTrait extends Trait {
    enum State{
        BUILDING,
        DONE,
        LEAVING
    }

    enum Facing{
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    ArrayList<Instruction> instructions;

    Location currentTarget;
    Location moveto;

    String currentBlock;
    int currentInstruction;
    Axis currentAxis;
    Facing facing;

    Main plugin;
    State state;
    double flatY;

    Player player;

    public BuilderTrait() {
        super("buildertrait");
        plugin = Main.getPlugin(Main.class);
        state = State.BUILDING;
    }


    @Override
    public void run(){
        switch (state){
            case BUILDING:
                break;
            case DONE:
                this.getNPC().getNavigator().setTarget(findLocationOutOfSight(player));
                state=State.LEAVING;
                break;
            case LEAVING:
                break;
        }
    }

    @Override
    public void onSpawn() {
        if(this.getNPC().isSpawned()){
            //Initialise instruction counter
            currentInstruction = 0;
            //initialise settings
            String buildingName;
            String direction;

            //buildingName = this.getNPC().data().get("template");
            //direction = this.getNPC().data().get("direction");

            /**
            //Get orientation of building and set facing to the building
            switch (direction.toLowerCase()) {
                case "north":
                    facing = Facing.NORTH;
                    break;
                case "south":
                    facing = Facing.SOUTH;
                    break;
                case "east":
                    facing = Facing.EAST;
                    break;
                case "west":
                    facing = Facing.WEST;
                    break;
            }
             */
            //Parse instructions using settings
            instructions = this.getNPC().data().get("instructions");
            player = this.getNPC().data().get("player");

            //Sets target, block, and axis to the next block
            currentTarget = instructions.get(currentInstruction).getLocation();
            currentBlock = instructions.get(currentInstruction).getMaterial();
            currentAxis = instructions.get(currentInstruction).getAxis();
            //Set default Y value
            flatY = currentTarget.getY();

            //set location to navigate to
            moveto = new Location(currentTarget.getWorld(),
                    currentTarget.getX(),flatY,currentTarget.getZ());
            //offset navigation location
            moveto.add(2,0,2);
            //Increment instruction
            currentInstruction++;
            //start moving to next location

            startNavigation();

        }

    }

    /**
     * Event for when NPC completes navigation
     *
     *
     * @param e
     * @throws InterruptedException
     */
    @EventHandler
    public void onArrive(NavigationCompleteEvent e) throws InterruptedException{

        //Check the correct NPC has arrived
        if(e.getNPC() == this.getNPC()){

            //Check the current state the builder is in
                switch (state) {

                    //NPC is in the Building state
                    case BUILDING -> {

                        if(Objects.nonNull(e.getNPC().getOrAddTrait(Equipment.class).get(Equipment.EquipmentSlot.HAND))) {

                            //If axis is not required or axis is Y -- Default axis is Y (Maybe don't need to specify in instructions)
                            if (currentAxis == null || currentAxis == Axis.Y) {

                                //Place held block at location
                                placeBlock(currentTarget,
                                        e.getNPC().getOrAddTrait(Equipment.class).get(Equipment.EquipmentSlot.HAND).getType());

                            } else {
                                //Place held block at location
                                Block b = placeBlock(currentTarget,
                                        e.getNPC().getOrAddTrait(Equipment.class).get(Equipment.EquipmentSlot.HAND).getType());
                                //reorient block to correct axis
                                orientBlock(b);
                            }
                        }
                        //Check if there are not any more instructions left
                        if (currentInstruction >= instructions.size()) {

                            //Announce building is completed
                            Bukkit.broadcastMessage("Building Completed!");

                            //Set state to done building
                            state = State.DONE;
                        }
                        //There are remaining instructions
                        else {
                            //get the next set of instructions
                            processInstruction();
                        }

                    }
                    //TO BE REPLACED!!!
                    case LEAVING -> {

                        //Place oak log and reorient it -- This was testing for orienting blocks
                        Block b = placeBlock(e.getNavigator().getTargetAsLocation(),
                                Material.OAK_LOG);

                        BlockData blockData = b.getBlockData();

                        ((Orientable) blockData).setAxis(Axis.Z);
                        b.setBlockData(blockData);

                        //Despawn npc
                        e.getNPC().despawn();
                        e.getNPC().destroy();
                    }
                }
        }
    }

    private void processInstruction(){
        //Process instructions
        nextBuilding();
        //Start navigating
        startNavigation();
    }

    private void startNavigation(){
        //start navigating to block coordinates
        this.getNPC().getNavigator().setTarget(moveto);


        Bukkit.broadcastMessage(currentBlock);

        //set npc's held item to the next material
        this.getNPC().getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND,
                new ItemStack(Material.matchMaterial(currentBlock), 1));
    }

    private Block placeBlock(Location loc, Material mat){
        //Get block at specified location
        Block block = loc.getBlock();

        //set block to specified material
        block.setType(mat);

        return block;
    }

    private void orientBlock(Block b){
        //Bukkit.broadcastMessage("Orienting block!");

        //Gathers blockdata from block passed to it
        BlockData blockData = b.getBlockData();

        //creates blockdata with orientation to the specified axis
        ((Orientable) blockData).setAxis(currentAxis);

        //set the blockdata
        b.setBlockData(blockData);
    }

    private Location findLocationOutOfSight(Player player){
        Location playerLocation = player.getLocation();
        BlockFace playerDirection = player.getFacing();

        Location targetLocation;
        switch (playerDirection){
            case NORTH -> targetLocation = playerLocation.add(0,0,1);
            case EAST -> targetLocation = playerLocation.subtract(1,0,0);
            case SOUTH -> targetLocation = playerLocation.subtract(0,0,1);
            case WEST -> targetLocation = playerLocation.add(1,0,0);
            default -> targetLocation = playerLocation;
        }
        return targetLocation;
    }

    /**
     * Handles preperation for loading the next building
     */
    private void nextBuilding(){

        //Sets target, block, and axis to the next block
        currentTarget = instructions.get(currentInstruction).getLocation();
        currentBlock = instructions.get(currentInstruction).getMaterial();
        currentAxis = instructions.get(currentInstruction).getAxis();

        //Prepares the coordinates for the npc to move to. Using the X and Z to be correct. But using the flatY
        //Coordinate to keep the npc on ground level/
        moveto = new Location(currentTarget.getWorld(),
                currentTarget.getX(),flatY,currentTarget.getZ());
        //Offset the location by 2 blocks to avoid getting trapped (Unsure why the pathfinding freaks out at this)

        //moveto.add(2,0,2);
        //Bukkit.broadcastMessage(moveto.toString());

        //Increment current instruction to target the next instruction
        currentInstruction++;
    }


}