package me.shakeforprotein.treebotreasures.Guis;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DailyGui {

    private TreeboTreasures pl;

    public DailyGui(TreeboTreasures main) {
        this.pl = main;
    }

    public void dailyGui(Player p) {

        File dailyGuiFile = new File(pl.getDataFolder(), "dailyRewards.yml");
        YamlConfiguration dailyYml = YamlConfiguration.loadConfiguration(dailyGuiFile);
        File playerFile = new File(pl.getDataFolder() + File.separator + "playerFiles", p.getUniqueId() + ".yml");
        YamlConfiguration playerYml = YamlConfiguration.loadConfiguration(playerFile);
        boolean allClaimed = true;

        int rows = dailyYml.getInt("gui.rows");
        String title = ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.title") + "");

        Inventory thisInv = Bukkit.createInventory(null, rows * 9, title);
        for (int slot = 0; slot < thisInv.getSize(); slot++) {
            ItemStack fillerItem = new ItemStack(Material.valueOf(dailyYml.getString("gui.filler")));
            ItemMeta fillMeta = fillerItem.getItemMeta();
            fillMeta.setDisplayName(" ");
            fillerItem.setItemMeta(fillMeta);
            thisInv.setItem(slot, fillerItem);
        }
        for (String menuItem : dailyYml.getConfigurationSection("gui.items").getKeys(false)) {
            int position = dailyYml.getInt("gui.items." + menuItem + ".Slot");
            ItemStack newItem;

            if(playerYml.get("claimed." + menuItem) == null){
                playerYml.set("claimed." + menuItem, 0);
                try{playerYml.save(playerFile);}
                catch (IOException err){
                    err.printStackTrace();
                }
            }
            if(playerYml.getInt("streak") < dailyYml.getInt("gui.items." + menuItem + ".DaysUntilPlayerCanClaim")){
                playerYml.set("claimed." + menuItem, 0);
            }
            if(playerYml.getInt("claimed." + menuItem) == 0){
                allClaimed = false;
            }


                if(playerYml.getInt("claimed." + menuItem) == 0 && playerYml.getInt("streak") >= dailyYml.getInt("gui.items." + menuItem + ".DaysUntilPlayerCanClaim")){
                    newItem = new ItemStack(Material.valueOf(dailyYml.getString("gui.items." + menuItem + ".ActiveItem")), dailyYml.getInt("gui.items." + menuItem + ".ActiveItemAmount"));
                    ItemMeta newMeta = newItem.getItemMeta();
                    List<String> newLore = new ArrayList<>();
                    newMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.items." + menuItem + ".Heading")));
                    newLore.add(ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.items." + menuItem + ".ActiveLore")));
                    newMeta.setLore(newLore);
                    newItem.setItemMeta(newMeta);
                    pl.addGlow(newItem);
                }
                else{
                    newItem = new ItemStack(Material.valueOf(dailyYml.getString("gui.items." + menuItem + ".InactiveItem")), dailyYml.getInt("gui.items." + menuItem + ".InactiveItemAmount"));
                    ItemMeta newMeta = newItem.getItemMeta();
                    List<String> newLore = new ArrayList<>();
                    newMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.items." + menuItem + ".Heading")));
                    newLore.add(ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.items." + menuItem + ".InactiveLore")));
                    newMeta.setLore(newLore);
                    newItem.setItemMeta(newMeta);

                }
                thisInv.setItem(position, newItem);


        }
        if(allClaimed){
            playerYml.set("claimed", null);
            playerYml.set("streak", 0);
            try{playerYml.save(playerFile);}
            catch (IOException err){
                err.printStackTrace();
            }
        }
        for (int slot = 0; slot < thisInv.getSize(); slot++) {
            if (thisInv.getItem(slot).getType() == Material.AIR) {
                ItemStack airItem = new ItemStack(Material.AIR);
                thisInv.setItem(slot, airItem);
            }
        }

        p.openInventory(thisInv);
    }
}