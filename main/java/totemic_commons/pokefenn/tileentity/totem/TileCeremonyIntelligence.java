package totemic_commons.pokefenn.tileentity.totem;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import totemic_commons.pokefenn.ModBlocks;
import totemic_commons.pokefenn.api.ceremony.ICeremonyEffect;
import totemic_commons.pokefenn.api.music.IMusicAcceptor;
import totemic_commons.pokefenn.api.music.MusicEnum;
import totemic_commons.pokefenn.api.plant.IPlantDrain;
import totemic_commons.pokefenn.recipe.registry.CeremonyRegistry;
import totemic_commons.pokefenn.lib.PlantIds;
import totemic_commons.pokefenn.tileentity.TileTotemic;
import totemic_commons.pokefenn.util.TotemUtil;
import totemic_commons.pokefenn.util.EntityUtil;

/**
 * Created by Pokefenn.
 * Licensed under MIT (If this is one of my Mods)
 */
public class TileCeremonyIntelligence extends TileTotemic implements IMusicAcceptor
{
    public boolean isDoingEffect;
    public int currentCeremony;
    public String player;
    public int currentTime;
    public int dancingEfficiency;
    public int[] music;
    public int plantEfficiency;
    public int[] musicSelector;
    public boolean isDoingStartup;
    public int armourEfficiency;

    public TileCeremonyIntelligence()
    {
        isDoingEffect = false;
        currentCeremony = 0;
        player = "";
        currentTime = 0;
        dancingEfficiency = 0;
        music = new int[MusicEnum.values().length];
        plantEfficiency = 0;
        musicSelector = new int[4];
        isDoingStartup = false;
        armourEfficiency = 0;
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if(!this.worldObj.isRemote)
        {
            if(!isDoingStartup)
                for(CeremonyRegistry ceremonyRegistry : CeremonyRegistry.ceremonyRegistry)
                {
                    if(ceremonyRegistry.getInstruments()[0].ordinal() == musicSelector[0] && ceremonyRegistry.getInstruments()[1].ordinal() == musicSelector[1] && ceremonyRegistry.getInstruments()[2].ordinal() == musicSelector[2] && ceremonyRegistry.getInstruments()[3].ordinal() == musicSelector[3])
                    {
                        isDoingStartup = true;
                    }
                }

            if(isDoingStartup)
            {
                startupMain();
            }

            if(currentCeremony <= CeremonyRegistry.ceremonyRegistry.size() && currentCeremony != 0)
            {
                if(isDoingEffect && !CeremonyRegistry.ceremonyRegistry.get(currentCeremony - 1).getIsInstant())
                    currentTime++;

                ICeremonyEffect effect = CeremonyRegistry.ceremonyRegistry.get(currentCeremony - 1).getCeremonyEffect();

                if(effect != null && !isDoingEffect)
                {
                    //TODO this is where i will do the actual effect
                }
            }
        }
    }

    public void startupMain()
    {
        if(worldObj.getWorldTime() % 40L == 0)
        {
            //TODO Check for plants, rarely drain, not always.
            drainPlantsForEfficieny();
        }

        if(worldObj.getWorldTime() % 60L == 0)
        {
            armourEfficiency = 0;
            getPlayersArmour();
        }

        if(worldObj.getWorldTime() % 10L == 0)
        {
            danceLikeAMonkey();
        }
    }

    public void getPlayersArmour()
    {

        if(EntityUtil.getEntitiesInRange(this.worldObj, xCoord, yCoord, zCoord, 8, 8) != null)
        {
            for(Entity entity : EntityUtil.getEntitiesInRange(getWorldObj(), xCoord, yCoord, zCoord, 8, 8))
            {
                if(entity instanceof EntityPlayer)
                {
                    int armourAndBaubles = TotemUtil.getArmourAmounts((EntityPlayer) entity) + TotemUtil.getTotemBaublesAmount((EntityPlayer) entity);

                    armourEfficiency += armourAndBaubles;
                }
            }
        }


    }

    public void danceLikeAMonkey()
    {

    }

    public void drainPlantsForEfficieny()
    {

    }

    public void resetEverything()
    {
        currentCeremony = 0;
        currentTime = 0;
        dancingEfficiency = 0;
        isDoingEffect = false;
    }

    public void tryCeremony(TileCeremonyIntelligence tileCeremonyIntelligence)
    {
        int x = tileCeremonyIntelligence.xCoord;
        int y = tileCeremonyIntelligence.yCoord;
        int z = tileCeremonyIntelligence.zCoord;

        World world = tileCeremonyIntelligence.getWorldObj();

        //else if(arePlantsValid(ceremonyRegistry))
        {
            //TODO
            //this.currentCeremony = ceremonyRegistry.getCeremonyID();
            //System.out.println(currentCeremony);
            //getPlayerArmour();
            //this.preformCeremony(ceremonyRegistry);
        }
    }

    /*
    public static PlantEnum getEnumFromPlant(Block block)
    {
        if(block != null)
        {
            if(block == Blocks.wheat)
                return PlantEnum.WHEAT;
            if(block == Blocks.potatoes)
                return PlantEnum.POTATO;
            if(block == Blocks.carrots)
                return PlantEnum.POTATO;
            if(block == Blocks.melon_stem)
                return PlantEnum.MELON;
            if(block == Blocks.pumpkin_stem)
                return PlantEnum.PUMPKIN;
            if(block == ModBlocks.moonglow)
                return PlantEnum.MOONGLOW;
            if(block == ModBlocks.lotusBlock)
                return PlantEnum.LOTUS;
        }

        return null;
    }

    public static Block getPlantFromEnum(PlantEnum plantEnum)
    {
        if(plantEnum == PlantEnum.WHEAT)
            return Blocks.wheat;
        if(plantEnum == PlantEnum.POTATO)
            return Blocks.potatoes;
        if(plantEnum == PlantEnum.CARROT)
            return Blocks.carrots;
        if(plantEnum == PlantEnum.MELON)
            return Blocks.melon_stem;
        if(plantEnum == PlantEnum.BLOODWART)
            return ModBlocks.bloodwart;
        if(plantEnum == PlantEnum.PUMPKIN)
            return Blocks.pumpkin_stem;
        if(plantEnum == PlantEnum.MOONGLOW)
            return ModBlocks.moonglow;
        if(plantEnum == PlantEnum.LOTUS)
            return ModBlocks.lotusBlock;

        return null;
    }


    public boolean arePlantsValid(CeremonyRegistry ceremonyRegistry)
    {
        World world = this.worldObj;

        int x = this.xCoord;
        int y = this.yCoord;
        int z = this.zCoord;

        Block plant1 = world.getBlock(x + 3, y, z);
        Block plant2 = world.getBlock(x - 3, y, z);
        Block plant3 = world.getBlock(x, y, z + 3);
        Block plant4 = world.getBlock(x, y, z - 3);

        PlantEnum plantID1 = ceremonyRegistry.getPlant1();
        PlantEnum plantID2 = ceremonyRegistry.getPlant2();
        PlantEnum plantID3 = ceremonyRegistry.getPlant3();
        PlantEnum plantID4 = ceremonyRegistry.getPlant4();

        boolean possibility1 = plantID1 == getEnumFromPlant(plant1) && plantID2 == getEnumFromPlant(plant2) && plantID3 == getEnumFromPlant(plant3) && plantID4 == getEnumFromPlant(plant4);

        if(plant1 != null && plant2 != null && plant3 != null && plant4 != null && getEnumFromPlant(world.getBlock(x + 3, y, z)) != null && getEnumFromPlant(world.getBlock(x - 3, y, z)) != null && getEnumFromPlant(world.getBlock(x, y, z + 3)) != null && getEnumFromPlant(world.getBlock(x, y, z - 3)) != null)
        {
            if(possibility1)
            {
                return true;
            }
        }

        return false;
    }

    */

    public int getEffiencyFromBlock(Block block)
    {
        //if(efficiency < 50)
        {
            if(block == ModBlocks.totemTorch)
                return 4;
            if(block == ModBlocks.totemSocket)
                return 3;
            if(block == Blocks.fire)
                return 2;
            if(block == ModBlocks.flameParticle)
                return 4;
        }

        return 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);

        nbtTagCompound.setBoolean("isDoingEffect", isDoingEffect);
        nbtTagCompound.setInteger("currentCeremony", currentCeremony);
        nbtTagCompound.setString("player", player);
        nbtTagCompound.setInteger("currentTime", currentTime);
        nbtTagCompound.setInteger("dancingEfficiency", dancingEfficiency);
        nbtTagCompound.setIntArray("music", music);
        nbtTagCompound.setInteger("plantEfficiency", plantEfficiency);
        nbtTagCompound.setIntArray("musicSelector", musicSelector);
        nbtTagCompound.setBoolean("isDoingStartup", isDoingStartup);
        nbtTagCompound.setInteger("armourEfficiency", armourEfficiency);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);

        isDoingEffect = nbtTagCompound.getBoolean("isDoingEffect");
        currentCeremony = nbtTagCompound.getInteger("currentCeremony");
        player = nbtTagCompound.getString("player");
        dancingEfficiency = nbtTagCompound.getInteger("dancingEfficiency");
        currentTime = nbtTagCompound.getInteger("currentTime");
        music = nbtTagCompound.getIntArray("music");
        plantEfficiency = nbtTagCompound.getInteger("plantEfficiency");
        musicSelector = nbtTagCompound.getIntArray("musicSelector");
        isDoingStartup = nbtTagCompound.getBoolean("isDoingStartup");
        armourEfficiency = nbtTagCompound.getInteger("armourEfficiency");
    }


    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbttagcompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public int[] getMusicArray()
    {
        return this.music;
    }

    @Override
    public int[] getMusicSelector()
    {
        return musicSelector;
    }

    @Override
    public boolean doesMusicSelect()
    {
        return true;
    }
}
