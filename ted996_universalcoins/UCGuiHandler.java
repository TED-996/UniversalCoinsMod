package ted996_universalcoins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

class UCGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if(tileEntity instanceof UCTileEntity){
                return new UCContainer(player.inventory, (UCTileEntity) tileEntity);
        }
        return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if(tileEntity instanceof UCTileEntity){
                return new UCTradeStationGUI(player.inventory, (UCTileEntity) tileEntity);
        }
        return null;
	}
	
}
