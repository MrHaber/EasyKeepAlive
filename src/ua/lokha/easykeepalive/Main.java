package ua.lokha.easykeepalive;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Main extends JavaPlugin {

    public final String VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3]; //версия пакетов

    @Override
    public void onEnable() {
        try {
            Method PlayerConnection_setPendingPing = Class.forName("net.minecraft.server." + VERSION + ".PlayerConnection").getDeclaredMethod("setPendingPing", boolean.class);
            PlayerConnection_setPendingPing.setAccessible(true);

            Field CraftPlayer_entity = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftEntity").getDeclaredField("entity");
            CraftPlayer_entity.setAccessible(true);

            Field EntityPlayer_playerConnection = Class.forName("net.minecraft.server." + VERSION + ".EntityPlayer").getDeclaredField("playerConnection");
            EntityPlayer_playerConnection.setAccessible(true);

            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Client.KEEP_ALIVE) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    try {
                        Object entity = CraftPlayer_entity.get(event.getPlayer());
                        Object playerConnection = EntityPlayer_playerConnection.get(entity);
                        PlayerConnection_setPendingPing.invoke(playerConnection, true);
                    } catch (Exception e) {
                        getLogger().info("Ошибка обработки " + event);
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
