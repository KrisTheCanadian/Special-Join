package tech.alphak.SpecialJoin;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;

public class SpecialJoin extends JavaPlugin implements Listener {
    private FileConfiguration _config;

    private String _welcomeMessage;
    private String _titleMain;
    private String _titleSub;

    private int _fadeIn;
    private int _stay;
    private int _fadeOut;

    private int _numFireworks;
    private int _numDelay;

    @Override
    public void onEnable(){
        this.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info(ChatColor.GREEN + "Special Join is enabled.");
        this.saveDefaultConfig();
        _config = getConfig();
        if(!confIsValid()){
            getLogger().warning(ChatColor.DARK_RED + "Something is wrong with the config.");
        }
    }
    @Override
    public void onDisable(){
        getLogger().info( ChatColor.GREEN + "Special Join is disabled.");
    }

    private boolean confIsValid(){
        _welcomeMessage = _config.getString("welcome_message");
        _titleMain = _config.getString("title_main_welcome_message");
        _titleSub = _config.getString("title_sub_welcome_message");
        _fadeIn = _config.getInt("fade-in");
        _stay = _config.getInt("stay");
        _fadeOut = _config.getInt("fade-out");
        _numFireworks = _config.getInt("fireworks_amount");
        _numDelay = _config.getInt("fireworks_delay");
        boolean numIsNeg = _fadeIn < 0 && _stay < 0 && _fadeOut < 0 && _numFireworks < 0 && _numDelay < 0;
        return _welcomeMessage != null && _titleMain != null && _titleSub != null && !numIsNeg;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(label.equalsIgnoreCase("specialjoin")){
            if(!sender.hasPermission("specialjoin.reload")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission.");
                return true;
            }
            if(args.length == 0){
                sender.sendMessage(ChatColor.RED + "Do you mean: /specialjoin help");
                return true;
            }
            if(args.length > 0){
                if(args[0].equalsIgnoreCase("reload")){
                    sender.sendMessage(ChatColor.BLUE + "Reloaded.");
                    reloadConfig();
                    _config = getConfig();
                    if(!confIsValid()){
                        getLogger().warning(ChatColor.DARK_RED + "Something is wrong with the config.");
                    }
                    return true;
                }
            }
        }

        return false;
    }

    private String translate(String s){
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(s));
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            Player player = event.getPlayer();

            _welcomeMessage = _config.getString("welcome_message");
            assert _welcomeMessage != null;

            _welcomeMessage = _welcomeMessage.replace("%player%", player.getDisplayName());
            _titleSub = _titleSub.replace("%player%", player.getDisplayName());
            _titleMain = _titleMain.replace("%player%", player.getDisplayName());

            player.sendMessage(translate(_welcomeMessage));
            player.sendTitle(translate(_titleMain), translate(_titleSub), _fadeIn, _stay, _fadeOut);

            boolean isFireworks = _config.isBoolean("fireworks");
            if( isFireworks && player.hasPermission("special.fireworks")){
                spawnFireworks(player.getLocation(), _numFireworks);
            }
        }, _numDelay);
    }

    public void spawnFireworks(Location location, int amount){


        for(int i = 0; i < amount; ++i){
            Random r = new Random();
            Firework fw;
            int offsetx = r.nextInt(5);
            int offsetz = r.nextInt(5);
            Location loc = location.add(offsetx, 10, offsetz);

            try{
                fw = (Firework) Objects.requireNonNull(loc.getWorld()).spawnEntity(loc, EntityType.FIREWORK);
            } catch (Exception e){
                getLogger().warning("[ERROR] null exception occured with fireworks spawning.");
                return;
            }
            FireworkMeta fwm = fw.getFireworkMeta();


            //Get the type
            int rt = r.nextInt(4) + 1;
            FireworkEffect.Type type = FireworkEffect.Type.BALL;
            if (rt == 1) type = FireworkEffect.Type.BALL;
            else if (rt == 2) type = FireworkEffect.Type.BALL_LARGE;
            else if (rt == 3) type = FireworkEffect.Type.BURST;
            else if (rt == 4) type = FireworkEffect.Type.CREEPER;
            else if (rt == 5) type = FireworkEffect.Type.STAR;

            //Get our random colours
            int r1i = r.nextInt(17) + 1;
            int r2i = r.nextInt(17) + 1;
            Color c1 = getColor(r1i);
            Color c2 = getColor(r2i);

            //Create our effect with this
            FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();

            //Then apply the effect to the meta
            fwm.addEffect(effect);

            //Generate some random power and set it
            int rp = r.nextInt(2) + 1;
            fwm.setPower(rp);

            //Then apply this to our rocket
            fw.setFireworkMeta(fwm);
        }
    }

    private Color getColor(int r1i) {
        switch(r1i){
            case 1:
                return Color.NAVY;
            case 2:
                return Color.AQUA;
            case 3:
                return Color.BLUE;
            case 4:
                return Color.BLACK;
            case 5:
                return Color.FUCHSIA;
            case 6:
                return Color.GRAY;
            case 7:
                return Color.GREEN;
            case 8:
                return Color.LIME;
            case 9:
                return Color.MAROON;
            case 10:
                return Color.OLIVE;
            case 11:
                return Color.ORANGE;
            case 12:
                return Color.PURPLE;
            case 13:
                return Color.RED;
            case 14:
                return Color.SILVER;
            case 15:
                return Color.WHITE;
            case 16:
                return Color.YELLOW;
            case 17:
                return Color.TEAL;
            default:
                return Color.BLUE;
        }

    }
}
