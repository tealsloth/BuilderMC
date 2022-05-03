package me.rowan.plugin.traits;

import me.rowan.plugin.Instruction;
import me.rowan.plugin.Main;
import me.rowan.plugin.handlers.FileHandler;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommunicatorTrait extends Trait {

    Main plugin;
    int searchRadius = 15;
    int width, height, length;
    Inventory menuInventory;
    public CommunicatorTrait() {
        super("communicatortrait");
        plugin = Main.getPlugin(Main.class);
        menuInventory = Bukkit.createInventory(null, 45, "Builder Menu");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent e) throws IOException {
        if (e.getNPC() == this.getNPC()){

            File schematicsDirectory = new File("./plugins/WorldEdit/schematics/");

            File schematics[] = schematicsDirectory.listFiles();
            int items = 0;

            for (File schematic: schematics) {
                String schemName, extenstion;
                String splitString[] = schematic.getName().split("\\.");
                schemName = splitString[0];
                extenstion = splitString[1];
                if(extenstion.equals("schem")){
                    menuInventory.setItem(10 + (2 * items), createMenuItem(Material.OAK_LOG, schemName));
                    items++;
                }
            }

            e.getClicker().openInventory(menuInventory);

            Bukkit.broadcastMessage(this.getNPC().getName() + " was clicked!");
        }
    }


    private ItemStack createMenuItem(Material material, String name){
        ItemStack itemStack = new ItemStack(material,1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            itemMeta.setDisplayName(name);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) throws IOException {
        Inventory clickedInventory = e.getInventory();
        if (clickedInventory.equals(menuInventory)){
            Player player = (Player) e.getWhoClicked();
            ItemStack item = e.getCurrentItem();
            
            Bukkit.broadcastMessage("This is working!");

            assert item != null;
            Bukkit.broadcastMessage(item.toString());
            Bukkit.broadcastMessage(clickedInventory.toString());
        
            Bukkit.broadcastMessage("Inv Correct.");
            ArrayList<Instruction> instructions = null;
            int height = 0,width = 0,length = 0;
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "builder");

            CompoundTag compoundTag = readNBTFile("./plugins/WorldEdit/schematics/" + item.getItemMeta().getDisplayName() + ".schem");
            instructions = parseInstructions(compoundTag, findFlatLand(this.getNPC().getEntity().getLocation()), BuilderTrait.Facing.NORTH);

            npc.data().set("instructions",instructions);
            npc.data().set("searchradius",searchRadius);
            npc.data().set("player", player);

            npc.spawn(this.getNPC().getEntity().getLocation());
            npc.addTrait(BuilderTrait.class);

            player.closeInventory();
            e.setCancelled(true);

        }

    }

    private CompoundTag readNBTFile(String file) throws IOException {
        ArrayList<Instruction> parsedInstructions = new ArrayList<>();

        NamedTag namedTag = NBTUtil.read(file);

        CompoundTag compoundTag = (CompoundTag) namedTag.getTag();

        height = Integer.parseInt( compoundTag.getShortTag("Height").valueToString());
        length = Integer.parseInt(compoundTag.getShortTag("Length").valueToString());
        width = Integer.parseInt(compoundTag.getShortTag("Width").valueToString());
        System.out.println(height + " " + length + " " + width);

        return compoundTag;
    }


    private ArrayList<Instruction> parseInstructions(CompoundTag compoundTag, Location loc, BuilderTrait.Facing facing) throws IOException {
        ArrayList<Instruction> parsedInstructions = new ArrayList<>();
        ByteArrayTag byteArrayTag = compoundTag.getByteArrayTag("BlockData");

        HashMap<Integer, String> map = new HashMap<Integer, String>();

        CompoundTag blocks = (CompoundTag) compoundTag.get("Palette");

        for (Iterator<Map.Entry<String, Tag<?>>> it = blocks.iterator(); it.hasNext(); ) {
            String block;
            int blockValue;
            Map.Entry<String, Tag<?>> f = it.next();
            block = f.getKey();
            blockValue = Integer.parseInt(f.getValue().valueToString());

            map.put(blockValue, block);

        }

        String s = byteArrayTag.valueToString();
        s = s.replace("[", "");
        s = s.replace("]", "");

        for(String ss: s.split(",")){
            System.out.println(ss);
        }

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < width; z++) {
                for (int x = 0; x < length; x++) {
                    int key = x + length*(z + width*(y));
                    Location newloc = new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ());

                    newloc.add(x - 1,y,z - 1);

                    if (!map.get(Integer.parseInt(s.split(",")[key])).equals("minecraft:air")) {

                        Instruction instruction = new Instruction(newloc, map.get(Integer.parseInt(s.split(",")[key])).split("\\[")[0]);

                        parsedInstructions.add(instruction);
                    }

                }
            }

        }
        return parsedInstructions;
    }


        //Load filehandler class and load unparsed instructions to array list
        //FileHandler fileHandler = new FileHandler();
        //ArrayList<String> instructions = fileHandler.load(file);



//        /*//initialise arraylist for parsed instructions
//        ArrayList<Instruction> parsedInstructions = new ArrayList<>();
//
//
//        //For every instruction in unprocessed instructions
//        for(String i:instructions){
//
//            //initialise location for the block
//            Location newLoc = new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ());
//
//            //X, Y, and Z offsets
//            int x,y,z;
//
//            String ax;
//
//
//            //Split raw instruction
//            String [] split = i.split(" ");
//            //positions 1-3 are XYZ offset
//            x = Integer.parseInt(split[0]);
//            y = Integer.parseInt(split[1]);
//            z = Integer.parseInt(split[2]);
//
//
//            ax = split[4];
//            //check direction building is to be constructed in and add offset accordingly
//            switch (facing) {
//                case WEST -> { //Works but gets trapped.
//                    newLoc.add(x, y, z);
//                    if (ax.equals("Z")) {
//                        ax = "X";
//                    } else if (ax.equals("X")) {
//                        ax = "Z";
//                    }
//                }
//                case NORTH ->//Works but can't navigate
//                        newLoc.add(z, y, x);
//                case SOUTH -> { //Works
//                    newLoc.subtract(z, 0, x);
//                    newLoc.add(0, y, 0);
//                }
//                case EAST -> { //Doesn't work
//                    newLoc.subtract(x, 0, z);
//                    newLoc.add(0, y, 0);
//                    if (ax.equals("Z")) {
//                        ax = "X";
//                    } else if (ax.equals("X")) {
//                        ax = "Z";
//                    }
//                }
//                default -> throw new IllegalStateException("Unexpected value: " + facing);
//            }
//
//            //Create instruction class with the data
//            Instruction instruction = new Instruction(newLoc,split[3],ax);
//            //Add instruction to the list of parsed instructions
//            parsedInstructions.add(instruction);
//        }
//
//        //return the parsed instructions
//        return parsedInstructio
//    */



    private Location findFlatLand(Location originPoint){
        /**
         * Finish this
         * Builder Foreman
         * Out of sight of player
         */
        int lastY = 0;
        int currentY;
        int counter = 0;
        int startX = 0, startZ = 0;

        for(int x = 0;x < searchRadius;x++){
            for(int z=0; z<searchRadius;z++){
                currentY = originPoint.getWorld().getHighestBlockYAt(originPoint.getBlockX() + x, originPoint.getBlockZ() + z);
                for (int xAxis = 0; xAxis < width; xAxis++) {
                    for (int zAxis = 0; zAxis < length; zAxis++) {
                        startX = originPoint.getBlockX() + x;
                        startZ = originPoint.getBlockZ() + z;
                        Bukkit.broadcastMessage(String.valueOf(currentY));
                        currentY = originPoint.getWorld().getHighestBlockYAt(startX + xAxis, startZ + zAxis);
                        if (lastY == 0){
                            lastY = originPoint.getWorld().getHighestBlockYAt(startX + xAxis, startZ + zAxis);
                            counter++;
                            startX = originPoint.getBlockX() + x;
                            startZ = originPoint.getBlockZ() + z;
                        }
                        if (currentY == lastY){
                            lastY = originPoint.getWorld().getHighestBlockYAt(startX + xAxis, startZ + zAxis);
                            counter++;
                        }else{
                            counter = 0;
                            lastY = 0;
                        }
                        if(counter == length * width){
                            return new Location(originPoint.getWorld(), startX, lastY, startZ);
                        }
                    }
                }

            }
        }
        return originPoint;
    }
}

