package me.rowan.plugin;


import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.NBTSchematicReader;
import me.rowan.plugin.traits.BuilderTrait;
import me.rowan.plugin.traits.CommunicatorTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.querz.nbt.io.NBTInput;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;


import java.io.*;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Command to spawn the builder npc
 *
 *
 */
class build implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cmdsender, Command cmd, String s, String[] args){
/*
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "builder");
        Player player = (Player) cmdsender;

        npc.data().set("template",args[0]);
        npc.data().set("direction",args[1]);

        npc.data().set("settings", args[0] + " " + args[1]);
        npc.addTrait(BuilderTrait.class);
        npc.spawn(player.getLocation());
        return true;
        *
 */

        return true;
    }
}

/**
 *
 * Command to spawn the communicator
 *
 */

class foreman implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cmdsender, Command cmd, String s, String[] args){


        CitizensAPI.getNPCRegistry().deregisterAll();
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "foreman");
        Player player = (Player) cmdsender;

        npc.spawn(player.getLocation());
        npc.addTrait(CommunicatorTrait.class);


        return true;
    }





}
