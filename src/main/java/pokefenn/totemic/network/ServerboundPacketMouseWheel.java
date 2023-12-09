package pokefenn.totemic.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import pokefenn.totemic.init.ModItems;
import pokefenn.totemic.item.TotemKnifeItem;

public record ServerboundPacketMouseWheel(boolean direction) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(direction);
    }

    public static ServerboundPacketMouseWheel decode(FriendlyByteBuf buf) {
        return new ServerboundPacketMouseWheel(buf.readBoolean());
    }

    public static void handle(ServerboundPacketMouseWheel packet, CustomPayloadEvent.Context context) {
        Player player = context.getSender();
        ItemStack stack = player.getMainHandItem();
        if(stack.getItem() == ModItems.totem_whittling_knife.get()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, TotemKnifeItem.changeIndex(stack, packet.direction));
        }
    }
}
