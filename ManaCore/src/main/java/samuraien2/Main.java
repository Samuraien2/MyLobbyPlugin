package samuraien2;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.*;

public final class Main extends JavaPlugin implements Listener {
    private static TextComponent prefix(NamedTextColor color, char letter) {
        return Component.text().color(NamedTextColor.DARK_GRAY).append(Component.text("[")).append(Component.text(letter).color(color)).append(Component.text("] ")).build();
    }

    private static final TextColor maincolor = TextColor.color(0x0E9EF2);
    private static Location spawn;
    private static final Component discordMSG = prefix(NamedTextColor.BLUE,'☽')
            .append(Component.text("Discord: ", NamedTextColor.GRAY))
            .append(Component.text("discord.gg/FJwAW3dtVY", NamedTextColor.BLUE, TextDecoration.UNDERLINED)
                    .clickEvent(ClickEvent.openUrl("https://discord.gg/FJwAW3dtVY")));
    private static final TextComponent prefixServer = prefix(NamedTextColor.GREEN, '☽');
    private static final Component line = Component.text().content(" ").append(MiniMessage.miniMessage().deserialize("<gradient:dark_gray:#636363:dark_gray>━━━━━━━━━━━━━━━━━━━</gradient>").decorate(TextDecoration.STRIKETHROUGH)).append(Component.text(" ")).build();
    private static final Component tablistHeader = MiniMessage.miniMessage().deserialize("<gradient:#4CBDFF:#0071CE>ᴍᴀɴᴀʀᴇᴀʟᴍs</gradient>")
            .append(Component.text(" ₁․₂₀․₆").color(NamedTextColor.GRAY)).appendNewline().append(line);

    private static final String[] entityNames = {
            "Vanilla Survival", // vanilla
            "Arcade", // arcade
            "Void Pirates", // void pirates
            "Realms", // realms
            "Pigwars", // pig wars
            "Music Lab", // music lab
            "Tetris" // tetris
    };

    

    private static final String[] servers = {
            "vanilla",
            "arcade",
            "voidpirates",
            "realms",
            "pigwars",
            "musiclab",
            "tetris",
            "zombierush"
    };

    private static final String[] versions = {
            "1.20.4",
            "1.20.4",
            "1.20.4+",
            "1.20.4",
            "1.20.4",
            "1.20.4",
            "1.20.4",
            "1.20.4",
    };

    ItemStack selectorItem = new ItemStack(Material.NETHER_STAR);
    ItemStack resetItem = new ItemStack(Material.CLOCK);
    ItemStack leaveItem = new ItemStack(Material.RED_DYE);

    private TextComponent footerTemplate;

    Inventory selector = Bukkit.createInventory(null, 9, Component.text("Server Selector"));
    private boolean notBypass = true;
    private int currentTick = 0;



    private void updateFooter(Player p) {
        p.sendPlayerListFooter(footerTemplate.toBuilder()
                .append(Component.text(p.getPing()).color(maincolor)).build());
    }



    private void setName(ItemStack item, Component name) {
        ItemMeta meta = item.getItemMeta();
        meta.itemName(name);
        item.setItemMeta(meta);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        String[] descriptions = {
                "No plugins or resets only vanilla",
                "Fun minigames",
                "Skyblock but competetive",
                "Better survival",
                "Bedwars but pigs",
                "Fl-studio in mc",
                "Tetris Minecraft Edition",
                "Zombies and guns :)"
        };
        Material[] materials = {
                Material.GRASS_BLOCK,
                Material.TOTEM_OF_UNDYING,
                Material.END_PORTAL_FRAME,
                Material.LAVA_BUCKET,
                Material.RED_BED,
                Material.NOTE_BLOCK,
                Material.COMMAND_BLOCK_MINECART,
                Material.ZOMBIE_HEAD
        };
        Component[] names = {
                Component.text("ᴠᴀɴɪʟʟᴀ").color(TextColor.color(0x50A849)),
                MiniMessage.miniMessage().deserialize("<gradient:#CB42E6:#46A5F9>ᴀʀᴄᴀᴅᴇ</gradient>"),
                Component.text("ᴠᴏɪᴅ ᴘɪʀᴀᴛᴇs").color(TextColor.color(0xFFD53D)),
                Component.text("ʀᴇᴀʟᴍs").color(TextColor.color(0xCB42E6)),
                Component.text("ᴘɪɢ").color(NamedTextColor.RED).append(Component.text("ᴡᴀʀs").color(NamedTextColor.WHITE)),
                Component.text("ᴍᴜsɪᴄ ʟᴀʙ").color(TextColor.color(0x00A0D6)),
                Component.text("ᴛᴇᴛʀɪs").color(TextColor.color(0xD82FCD)),
                Component.text("ᴢᴏᴍʙɪᴇ ").color(TextColor.color(0x5e9e70)).append(Component.text("ʀᴜsʜ").color(TextColor.color(0xA93A3A)))
        };
        for (int i = 0; i < 8; i++) {
            List<Component> lore = new ArrayList<>(4);
            ItemStack item = new ItemStack(materials[i], 1);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(names[i].decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("[NOT RELEASED]").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text(descriptions[i]).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("Version: ").color(NamedTextColor.GRAY).append(Component.text(versions[i]).color(NamedTextColor.AQUA)).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("Players: ").color(NamedTextColor.GRAY).append(Component.text(0).color(NamedTextColor.AQUA)).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
            item.setItemMeta(meta);
            selector.addItem(item);
        }

        setName(selectorItem, Component.text("Server Selector"));
        setName(resetItem, Component.text("Reset", NamedTextColor.YELLOW));
        setName(leaveItem, Component.text("Leave Parkour", NamedTextColor.RED));

        spawn = new Location(Bukkit.getWorld("world"), 0.5, 165, 0.5);

        new BukkitRunnable() {
            @Override
            public void run() {
                footerTemplate = Component.text()
                        .append(line).appendNewline()
                        .append(Component.text(" TPS: ").color(NamedTextColor.GRAY))
                        .append(Component.text(Math.round(Bukkit.getTPS()[0] * 10) / 10.0)).color(TextColor.color(0x4CBDFF))
                        .append(Component.text(", Ping: ").color(NamedTextColor.GRAY)).build();
                for (Player lp : Bukkit.getOnlinePlayers()) {
                    updateFooter(lp);
                }
            }
        }.runTaskTimer(this, 0L, 300L);

        new BukkitRunnable() {
            @Override
            public void run() {
                currentTick += 5;
                for (Map.Entry<UUID, Integer> entry : timer.entrySet()) {
                    Player p = Bukkit.getPlayer(entry.getKey());
                    if (p != null) {
                        p.sendActionBar(formatTime((currentTick - entry.getValue()), NamedTextColor.GOLD));
                    }
                }
            }
        }.runTaskTimer(this, 0L, 1L);

        getLogger().info("Finished loading");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final Player p = (Player) sender;
        switch (cmd.getName()) {
            case "discord":
                p.sendMessage(discordMSG);
                break;

            case "bypass":
                notBypass = !notBypass;
                p.sendMessage(Component.text("Bypass " + (notBypass ? "disabled (default)" : "enabled")));
                break;

            case "ping":
                if (args.length == 0) {
                    p.sendMessage(Component.text().append(prefixServer)
                            .append(Component.text("Your ping is: ").color(NamedTextColor.GRAY))
                            .append(Component.text(p.getPing()).color(NamedTextColor.GREEN)).build());
                } else {
                    final Player target = Bukkit.getServer().getPlayerExact(args[0]);
                    TextComponent pingPrefix = Component.text().append(prefixServer).append(Component.text(args[0]).color(NamedTextColor.GREEN)).build();

                    if (target == null) {
                        p.sendMessage(Component.text()
                                .append(pingPrefix).append(Component.text(" isn't online").color(NamedTextColor.GRAY)).build());
                    } else {
                        p.sendMessage(Component.text()
                                .append(pingPrefix).append(Component.text("'s ping is: ").color(NamedTextColor.GRAY))
                                .append(Component.text(target.getPing()).color(NamedTextColor.GREEN)).build());
                    }
                }
                break;

            case "hub":
                p.teleport(spawn);
                break;
        }
        return true;
    }


    @EventHandler
    public void playerSwapOffhand(PlayerSwapHandItemsEvent e) {
        if (notBypass) e.setCancelled(true);
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        if (notBypass) {
            e.setCancelled(true);
            if (e.getInventory().getHolder() == null && e.getCurrentItem() != null) {
                final int slot = e.getRawSlot();
                if (slot < 8) {
                    connect((Player)e.getWhoClicked(), slot);
                }
            }

        }
    }

    @EventHandler
    public void dropItem(PlayerDropItemEvent e) {
        if (notBypass) e.setCancelled(true);
    }

    @EventHandler
    public void damageEvent(EntityDamageEvent e) {
        if (notBypass) e.setCancelled(true);
    }


    private void createNewScoreboard(Player p) {
        //p.getScoreboard().getObjective("main").getScore("mana");
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = scoreboard.registerNewObjective("main", Criteria.DUMMY, Component.text("ʟᴏʙʙʏ").color(maincolor));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.numberFormat(NumberFormat.blank());

        obj.getScore("                        ").setScore(12);
        obj.getScore("mana").setScore(11);
        obj.getScore("mana").customName(Component.text().content("| ")
                .append(Component.text("ᴍᴀɴᴀ: ").color(NamedTextColor.GRAY))
                .append(Component.text(0).color(maincolor)).build());
        obj.getScore("players").setScore(10);
        obj.getScore("players").customName(Component.text().content("| ")
                .append(Component.text("ᴘʟᴀʏᴇʀs: ").color(NamedTextColor.GRAY))
                .append(Component.text(0).color(maincolor)).build());
        obj.getScore(" ").setScore(9);
        obj.getScore("servers").setScore(8);
        obj.getScore("servers").customName(Component.text("sᴇʀᴠᴇʀs").decorate(TextDecoration.BOLD));
        obj.getScore("a").setScore(7);
        obj.getScore("b").setScore(6);
        obj.getScore("c").setScore(5);
        obj.getScore("d").setScore(4);
        obj.getScore("e").setScore(3);
        obj.getScore("f").setScore(2);
        obj.getScore("g").setScore(1);
        obj.getScore("h").setScore(0);
        obj.getScore("h").customName(Component.text("comp").color(NamedTextColor.RED));

        p.setScoreboard(scoreboard);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void joinEvent(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        if (p.getName().equals("Samuraien2")) {
            p.addAttachment(this, "minecraft.command.op", true);
        }
        p.sendPlayerListHeader(tablistHeader);
        updateFooter(p);
        p.getInventory().setItem(4, selectorItem);
        createNewScoreboard(p);
    }

    @EventHandler
    public void quitEvent(PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        removePlayerData(p);
    }

    @EventHandler
    public void playerChatMessage(AsyncChatEvent e) {
        e.setCancelled(true);
        Bukkit.broadcast(Component.text(e.getPlayer().getName() + ": ").append(e.message()));
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (e.getAction() == Action.PHYSICAL) {
            if (e.getClickedBlock() != null) {
                if (e.getClickedBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                    int time = (currentTick - timer.get(p.getUniqueId()));
                    p.sendMessage(Component.text().content("You won in ").append(formatTime(time, NamedTextColor.GREEN)).build());
                    p.sendActionBar(formatTime(time, NamedTextColor.GREEN));
                    parkourLeave(p);

                } else {
                    parkourStart(p);
                }
            }

        } else {
            switch (e.getMaterial()){
                case NETHER_STAR:
                    p.openInventory(selector);
                    break;
                case RED_DYE:
                    parkourLeave(p);
                    break;
                case CLOCK:
                    p.teleport(start.get(p.getUniqueId()));
                    break;
            }
            if (notBypass) e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerEntityInteract(PlayerInteractAtEntityEvent e) {
        if (notBypass) {
            e.setCancelled(true);
            final String name = e.getRightClicked().getName();
            for (int i = 0; i < entityNames.length; i++){
                if(name.equals(entityNames[i])){
                    connect(e.getPlayer(), i);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        event.getCommands().removeIf(command -> command.contains(":"));
    }

    private void connect(Player p, int server){
        p.sendMessage("/server " + servers[server]);
    }

    private final HashMap<UUID, Integer> timer = new HashMap<>();
    private final HashMap<UUID, Location> start = new HashMap<>();

    private void parkourStart(Player p) {
        resetTimer(p);
        start.put(p.getUniqueId(), p.getLocation());
        p.getInventory().setItem(3, resetItem);
        p.getInventory().setItem(5, leaveItem);
    }

    private void parkourLeave(Player p) {
        removePlayerData(p);
        p.getInventory().clear(3);
        p.getInventory().clear(5);
        p.teleport(spawn);
    }

    private void resetTimer(Player p) {
        timer.put(p.getUniqueId(), currentTick);
    }

    private void removePlayerData(Player p) {
        timer.remove(p.getUniqueId());
        //start.remove(p.getUniqueId());
    }

    private static Component formatTime(int milliseconds, NamedTextColor color) {
        int seconds = milliseconds / 100;

        TextComponent.Builder builder = Component.text();

        if (seconds > 60) {
            builder.append(Component.text(seconds / 60 + "m", color))
                    .append(Component.text(", ", NamedTextColor.GRAY));
            seconds = seconds % 60;
        }

        return builder.append(Component.text(String.format("%d.%02ds", seconds, milliseconds % 100), color)).build();
    }
}
