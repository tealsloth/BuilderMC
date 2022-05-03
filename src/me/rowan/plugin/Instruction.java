package me.rowan.plugin;

import org.bukkit.Axis;
import org.bukkit.Location;

public class Instruction {
    Location location;
    String material;
    Axis axis = null;

    /**
     * Constructor for the Instruction class when axis of block is irrelevant
     *
     *
     * @param location location of block to be placed
     * @param material material of block to be placed
     */
    public Instruction(Location location, String material){
        //Set location and material to values
        this.location = location;
        this.material = material;
    }

    /**
     * Constructor for the Instruction class when axis of block is important
     *
     *
     * @param location Location of block to be placed
     * @param material Material of block to be placed
     * @param axisDirection Orientation of block to be placed
     */
    public Instruction(Location location, String material, String axisDirection){
        this.location = location;
        this.material = material;


        //Check which orientation has been set - Converted to uppercase to eliminate confusion or error from lowercase
        //entries
        switch(axisDirection.toUpperCase()){
            //Block is facing the Y axis
            case "Y":
                this.axis = Axis.Y;
                break;
            //Block is facing the X axis
            case "X":
                this.axis = Axis.X;
                break;
            //Block is facing the Z axis
            case "Z":
                this.axis = Axis.Z;
                break;
        }
    }

    /**
     *
     * Getter for material
     *
     * @return value of material
     */
    public String getMaterial() {
        return material;
    }


    /**
     * Getter for location
     *
     * @return value of location
     */
    public Location getLocation() {
        return location;
    }


    @Override
    public String toString() {
        return "Instruction{" +
                "location=" + location +
                ", material='" + material + '\'' +
                '}';
    }


    /**
     *Getter for axis
     *
     * @return value of axis
     */
    public Axis getAxis(){return axis;}
}
