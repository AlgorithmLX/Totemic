package pokefenn.totemic.network;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import pokefenn.totemic.api.TotemicAPI;

public final class NetworkHandler {
    private static final ResourceLocation CHANNEL_NAME = new ResourceLocation(TotemicAPI.MOD_ID, "main");
    private static final String PROTOCOL_VERSION = "2";

    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(CHANNEL_NAME, () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void init() {
        channel.registerMessage(0, ServerboundPacketMouseWheel.class, ServerboundPacketMouseWheel::encode, ServerboundPacketMouseWheel::decode, ServerboundPacketMouseWheel::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        channel.registerMessage(1, ClientboundPacketStartupMusic.class, ClientboundPacketStartupMusic::encode, ClientboundPacketStartupMusic::decode, ClientboundPacketStartupMusic::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
