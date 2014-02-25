package ted996_universalcoins;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;

class BlockTradeStation extends BlockContainer {
	
	private Icon[] icons;

	public BlockTradeStation(int id) {
		super(id, new Material(MapColor.stoneColor));
		setHardness(1.0f);

		setCreativeTab(CreativeTabs.tabMisc);

		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 2);
	}

	public int idDropped(int par1, Random par2Random, int par3)
	{
		return blockID;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister){
		icons = new Icon[2];
		
		for (int i = 0; i < icons.length; i++){
			icons[i] = par1IconRegister.registerIcon(UniversalCoins.modid + ":" +
													  		this.getUnlocalizedName().substring(5) + i);
		}
	}
	
	public Icon getIcon(int par1, int par2){
		if (par1 == 0 || par1 == 1){
			return icons[1];
		}
		return icons[0];
	}
	
	@Override
    public TileEntity createNewTileEntity(World world) {
		//Minecraft.getMinecraft().getLogAgent().logInfo("Creating a new TileEntity.");
		return new UCTileEntity();
    }
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z,
    								EntityPlayer player, int par6, float par7, float par8, float par9) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking()) {
				return false;
		}
		//code to open gui explained later
		player.openGui(UniversalCoins.instance, 0, world, x, y, z);
		return true;
    }
	
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		dropItems(world, x, y, z);
		throwCoins(world, x, y, z);
        super.breakBlock(world, x, y, z, par5, par6);
	}

	private void throwCoins(World world, int x, int y, int z) {
		Random rand = new Random();

		UCTileEntity tileEntity = (UCTileEntity) world.getBlockTileEntity(x, y, z);
		if (tileEntity == null) {
			return;
		}
		int sumLeft = tileEntity.coinSum;
		while (sumLeft > 0){
			if (sumLeft <= 729 * 64){
				dropStack(world, x, y, z, UCItemPricer.getRevenueStack(sumLeft), rand);
				sumLeft = 0;
			}
			else{
				dropStack(world, x, y, z, new ItemStack(UniversalCoins.itemCoinHeap, 64), rand);
				sumLeft -= 729 * 64;
			}
		}


	}

	private void dropStack(World world, int x, int y, int z, ItemStack item, Random rand) {
		float rx = rand.nextFloat() * 0.8F + 0.1F;
		float ry = rand.nextFloat() * 0.8F + 0.1F;
		float rz = rand.nextFloat() * 0.8F + 0.1F;

		EntityItem entityItem = new EntityItem(world,
				x + rx, y + ry, z + rz,
				new ItemStack(item.itemID, item.stackSize, item.getItemDamage()));

		if (item.hasTagCompound()) {
			entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
		}


		float factor = 0.05F;
		entityItem.motionX = rand.nextGaussian() * factor;
		entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
		entityItem.motionZ = rand.nextGaussian() * factor;
		world.spawnEntityInWorld(entityItem);
		item.stackSize = 0;
	}

	private void dropItems(World world, int x, int y, int z){
		Random rand = new Random();

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof IInventory)) {
			return;
		}
		IInventory inventory = (IInventory) tileEntity;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack item = inventory.getStackInSlot(i);

			if (item != null && item.stackSize > 0) {
				dropStack(world, x, y, z, item, rand);
			}
		}
	}

	/*@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
		super.onNeighborBlockChange(par1World, par2, par3, par4, par5);    //To change body of overridden methods use File | Settings | File Templates.
		if (par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)){

		}
	}*/
}
