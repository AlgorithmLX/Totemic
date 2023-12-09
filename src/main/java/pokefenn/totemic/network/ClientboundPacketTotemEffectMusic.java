package pokefenn.totemic.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import pokefenn.totemic.block.totem.entity.StateTotemEffect;
import pokefenn.totemic.init.ModBlockEntities;

public record ClientboundPacketTotemEffectMusic(BlockPos pos, int amount) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeShort(amount); //MAX_TOTEM_EFFECT_MUSIC is less than 2^16
    }

    public static ClientboundPacketTotemEffectMusic decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int amount = buf.readShort();
        return new ClientboundPacketTotemEffectMusic(pos, amount);
    }

    @SuppressWarnings("resource")
    public static void handle(ClientboundPacketTotemEffectMusic packet, CustomPayloadEvent.Context context) {
        Minecraft.getInstance().level.getBlockEntity(packet.pos, ModBlockEntities.totem_base.get())
        .ifPresent(tile -> {
            if(tile.getTotemState() instanceof StateTotemEffect state) {
                state.setTotemEffectMusic(packet.amount);
            }
        });
    }
}
