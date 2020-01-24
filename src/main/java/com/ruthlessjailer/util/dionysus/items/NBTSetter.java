package com.ruthlessjailer.util.theseus.items;

import com.sun.org.apache.xpath.internal.objects.XNull;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NBTSetter {

    public static ItemStack addNBT(ItemStack item, String tag, String value){
        net.minecraft.server.v1_15_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound t = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        t.setString(tag, value);
        nmsItem.setTag(t);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public static ItemStack addNBT(ItemStack item, String tag, float value){
        net.minecraft.server.v1_15_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound t = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        t.setFloat(tag, value);
        nmsItem.setTag(t);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public static String getNBT(ItemStack item, String tag){
        net.minecraft.server.v1_15_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if(!nmsItem.hasTag()) {
            return null;
        }
        NBTTagCompound t = nmsItem.getTag();
        return t.getString(tag);
    }

    public static float getNBT(ItemStack item, String tag, String ignore){
        net.minecraft.server.v1_15_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if(!nmsItem.hasTag()) {
            return 0F;
        }
        NBTTagCompound t = nmsItem.getTag();
        return t.getFloat(tag);
    }

}
