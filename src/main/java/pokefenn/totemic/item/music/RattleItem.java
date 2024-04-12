package pokefenn.totemic.item.music;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import pokefenn.totemic.api.TotemicAPI;
import pokefenn.totemic.init.ModContent;

public class RattleItem extends Item {
    public RattleItem(Properties props) {
        super(props);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if(entity instanceof Player player && !player.getCooldowns().isOnCooldown(this)) {
            if(entity.isShiftKeyDown())
                TotemicAPI.get().music().playSelector(entity, ModContent.rattle.get());
            else
                TotemicAPI.get().music().playMusic(entity, ModContent.rattle.get());

            player.getCooldowns().addCooldown(this, 16);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(!player.swinging)
            player.swing(hand, true);
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("totemic.tooltip.selectorMode"));
    }
}
