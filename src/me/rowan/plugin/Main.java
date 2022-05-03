package me.rowan.plugin;

import me.rowan.plugin.traits.BuilderTrait;
import me.rowan.plugin.traits.CommunicatorTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;
import org.bukkit.*;

import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Main extends JavaPlugin {

    @Override
    public void onEnable(){
        Bukkit.getLogger().info(ChatColor.GREEN + "Enabled " + this.getName());

        //Register commands with Bukkit
        this.getCommand("build").setExecutor(new build());
        this.getCommand("foreman").setExecutor(new foreman());

        //Register traits with Citizens
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(BuilderTrait.class).withName("buildertrait"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(CommunicatorTrait.class).withName("communicatortrait"));




    }

    public static void main(String[] args) throws IOException {


        Location loc = new Location(null, 100, 60, 100);

        ArrayList<Instruction> parsedInstructions = new ArrayList<>();

        NamedTag namedTag = NBTUtil.read("Office.schem");

        CompoundTag compoundTag = (CompoundTag) namedTag.getTag();

        int height, length, width;
        height = Integer.parseInt( compoundTag.getShortTag("Height").valueToString());
        length = Integer.parseInt(compoundTag.getShortTag("Length").valueToString());
        width = Integer.parseInt(compoundTag.getShortTag("Width").valueToString());
        System.out.println(height + " " + length + " " + width);
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
            //System.out.println(ss);
        }

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < width; z++) {
                for (int x = 0; x < length; x++) {
                    int key = (x + length*(z + width*(y)));
                    System.out.println(key);
                    Location newloc = new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ());

                    newloc.add(x - 1,y - 1,z - 1);

                    if (!map.get(Integer.parseInt(s.split(",")[key])).equals("minecraft:air")) {

                        Instruction instruction = new Instruction(newloc, map.get(Integer.parseInt(s.split(",")[key])).split("\\[")[0]);

                        parsedInstructions.add(instruction);
                    }

                }
            }

        }
        //System.out.println(parsedInstructions);



    }
}
