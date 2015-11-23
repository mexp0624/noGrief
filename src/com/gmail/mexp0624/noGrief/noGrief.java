package com.gmail.mexp0624.noGrief;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class noGrief extends JavaPlugin implements Listener
{

	public static noGrief p;
	FileConfiguration config;
	public boolean
		blockCreeperBlockDamage,
		blockWitherBlockDamage,
		blockWitherSkullBlockDamage,
		blockEnderDragonBlockDamage,
		blockFireballBlockDamage,
		disableEndermanGriefing,
		blockZombieDoorDestruction;
	
	public boolean blockTNTBlockDamage;
	public boolean blockEnderDragonPortalCreation;
	private HashMap<String, Boolean> blockMap = new HashMap<String, Boolean>();
	private static List<String> onTab =
			Arrays.asList(
					"block-creeper-block-damage",
					"block-wither-block-damage",
					"block-wither-skull-block-damage",
					"block-enderdragon-block-damage",
					"block-fireball-block-damage",
					"disable-enderman-griefing",
					"block-zombie-door-destruction",
					"block-tnt-block-damage",
					"block-enderdragon-portal-creation"
					);

	@Override
	public void onEnable()
	{
		p = this;
		Bukkit.getPluginManager().registerEvents(this, this);

		loadConfig();
	}

	@Override
	public void onDisable() {
		// save Data
		// saveConfig();
		// Disable listeners
		HandlerList.unregisterAll((Listener )p);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if(!player.isPermissionSet("nogrief.command.admin") && !player.isOp()){
				 sender.sendMessage("\u00a74You do not have permission to use this command");
				return true;
			}
		}
		if(args.length == 1){
			if(args[0].equalsIgnoreCase("reload") ){
				p.reloadConfig();
				loadConfig();
				sender.sendMessage(ChatColor.GOLD + "[noGrief]" + ChatColor.WHITE +" reloaded !");
				return true;
			} else if(blockMap.containsKey(args[0]) ){
				// show the one
				sender.sendMessage(ChatColor.GOLD + "[noGrief] " + ChatColor.WHITE + args[0] + " is " + blockMap.get(args[0]));
				return true;
			} else {
				// wrong key
				sender.sendMessage("There is no " + ChatColor.GOLD + args[0] + " !");
				return true;
			}
		}
		if(args.length == 2){
			if(blockMap.containsKey(args[0])){
				if(args[1].equalsIgnoreCase("on")){
					blockMap.replace(args[0], true);
					config.set(args[0], true);
				}else if(args[1].equalsIgnoreCase("off")){
					blockMap.replace(args[0], false);
					config.set(args[0], false);
				}else{
					return false;
				}
				saveConfig();
				updateConfig();
				// set the one
				sender.sendMessage(ChatColor.GOLD + "[noGrief] " + ChatColor.WHITE + args[0] + " is now " + blockMap.get(args[0]));
				return true;
			} else {
				// wrong key
				sender.sendMessage("There is no " + ChatColor.GOLD + args[0] + " !");
				return true;
			}
		}
		if(args.length == 0){
			// show all status
			sender.sendMessage(ChatColor.GOLD + "[noGrief] " + ChatColor.WHITE +"====================" );
			for(String anti : onTab){
				sender.sendMessage(anti + " is now " + blockMap.get(anti));
			}
			sender.sendMessage(ChatColor.GOLD + "[noGrief] " + ChatColor.WHITE +"====================" );
			return true;
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) 
	{
		List<String> toreturn = new ArrayList<String>();
		toreturn.clear();
		if (command.getName().equalsIgnoreCase("ng") | command.getName().equalsIgnoreCase("nogrief"))
		{
			if (args.length == 2){
				toreturn.add("on");
				toreturn.add("off");
			}
			if (args.length == 1){
				toreturn = new ArrayList<String>(onTab);
			}
		}
		return toreturn;
	}
	
	public void loadConfig() {
		// Read Config
		config = getConfig();
		for(String anti : onTab){
			config.addDefault(anti, true);
			if(blockMap.containsKey(anti)){
				blockMap.replace(anti,  config.getBoolean(anti, true));
			}else{
				blockMap.put(anti,  config.getBoolean(anti, true));
			}
		}
		config.addDefault("block-tnt-block-damage", false);
		blockMap.put("block-tnt-block-damage",  config.getBoolean("block-tnt-block-damage", false));
		
		config.options().copyDefaults(true);
		saveConfig();
		
		updateConfig();
	}
	
	public void updateConfig() {
		blockCreeperBlockDamage = config.getBoolean("block-creeper-block-damage", true);
		blockWitherBlockDamage = config.getBoolean("block-wither-block-damage", true);
		blockWitherSkullBlockDamage = config.getBoolean("block-wither-skull-block-damage", true);
		blockEnderDragonBlockDamage = config.getBoolean("block-enderdragon-block-damage", true);
		blockFireballBlockDamage = config.getBoolean("block-fireball-block-damage", true);
		disableEndermanGriefing = config.getBoolean("disable-enderman-griefing", true);
		blockZombieDoorDestruction = config.getBoolean("block-zombie-door-destruction", true);
		
		blockTNTBlockDamage = config.getBoolean("block-tnt-block-damage", false);
	        
	        blockEnderDragonPortalCreation = config.getBoolean("block-enderdragon-portal-creation", true);
	        
	        //blockEntityPaintingDestroy = config.getBoolean("block-painting-destroy", false);
	        //blockEntityItemFrameDestroy = config.getBoolean("block-item-frame-destroy", false);
	}
	
	 /**
	 * Called when an entity changes a block somehow
	 */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		Entity ent = event.getEntity();
		
		if (ent instanceof Enderman) {
			if (disableEndermanGriefing) {
				event.setCancelled(true);
				return;
			}
		} else if (ent.getType() == EntityType.WITHER) {
			if (blockWitherBlockDamage) {
				event.setCancelled(true);
				return;
			}
		} else if (/*ent instanceof Zombie && */event instanceof EntityBreakDoorEvent) {
			if (blockZombieDoorDestruction) {
				event.setCancelled(true);
				return;
			}
		}
	}

	/**
	 * Called on entity explode.
	 */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
//		Location l = event.getLocation();
//		World world = l.getWorld();
		Entity ent = event.getEntity();
		
		if (ent instanceof Creeper) {
			if (blockCreeperBlockDamage) {
				event.blockList().clear();
				return;
			}
		} else if (ent instanceof EnderDragon) {
			if (blockEnderDragonBlockDamage) {
				event.blockList().clear();
				return;
			}
		} else if (ent instanceof TNTPrimed || ent instanceof ExplosiveMinecart) {
			if (blockTNTBlockDamage) {
				event.blockList().clear();
				return;
			}
		} else if (ent instanceof Fireball) {
			if (ent instanceof WitherSkull) {
				if (blockWitherSkullBlockDamage) {
					event.blockList().clear();
					return;
				}
			} else {
				if (blockFireballBlockDamage) {
					event.blockList().clear();
					return;
				}
			}
		} else if (ent instanceof Wither) {
			if (blockWitherBlockDamage) {
				event.blockList().clear();
				return;
			}
		} else {
			// unhandled entity
			//event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCreatePortal(EntityCreatePortalEvent event) {
		switch (event.getEntityType()) {
			case ENDER_DRAGON:
				if (blockEnderDragonPortalCreation) event.setCancelled(true);
				break;
			default:
				break;
		}
	}


	   /* @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	    public void onHangingBreak(HangingBreakEvent event) {
	        Hanging hanging = event.getEntity();
	        World world = hanging.getWorld();

	        if (event instanceof HangingBreakByEntityEvent) {
	            HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent) event;
	            Entity removerEntity = entityEvent.getRemover();
	            if (removerEntity instanceof Projectile) {
	                Projectile projectile = (Projectile) removerEntity;
	                ProjectileSource remover = projectile.getShooter(); 
	                removerEntity = (remover instanceof LivingEntity ? (LivingEntity) remover : null);
	            }

	            if (!(removerEntity instanceof Player)) {
	                if (removerEntity instanceof Creeper) {
	                    if (wcfg.blockCreeperBlockDamage || wcfg.blockCreeperExplosions) {
	                        event.setCancelled(true);
	                        return;
	                    }
	                    if (wcfg.useRegions && !plugin.getGlobalRegionManager().allows(DefaultFlag.CREEPER_EXPLOSION, hanging.getLocation())) {
	                        event.setCancelled(true);
	                        return;
	                    }
	                }

	                // this now covers dispensers as well, if removerEntity is null above,
	                // due to a non-LivingEntity ProjectileSource
	                if (hanging instanceof Painting
	                        && (wcfg.blockEntityPaintingDestroy
	                        || (wcfg.useRegions
	                        && !plugin.getGlobalRegionManager().allows(DefaultFlag.ENTITY_PAINTING_DESTROY, hanging.getLocation())))) {
	                    event.setCancelled(true);
	                } else if (hanging instanceof ItemFrame
	                        && (wcfg.blockEntityItemFrameDestroy
	                        || (wcfg.useRegions
	                        && !plugin.getGlobalRegionManager().allows(DefaultFlag.ENTITY_ITEM_FRAME_DESTROY, hanging.getLocation())))) {
	                    event.setCancelled(true);
	                }
	            }
	        } else {
	            // Explosions from mobs are not covered by HangingBreakByEntity
	            if (hanging instanceof Painting && wcfg.blockEntityPaintingDestroy
	                    && event.getCause() == RemoveCause.EXPLOSION) {
	                event.setCancelled(true);
	            } else if (hanging instanceof ItemFrame && wcfg.blockEntityItemFrameDestroy
	                    && event.getCause() == RemoveCause.EXPLOSION) {
	                event.setCancelled(true);
	            }
	        }
	    }*/
	    
}
