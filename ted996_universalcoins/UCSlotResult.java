package ted996_universalcoins;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

class UCSlotResult extends Slot {
	
	public UCSlotResult(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}
	
	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return false;
	}
	
	public ItemStack decrStackSize(int par1) {
		if (getStack() != null && getStack().stackSize != par1) {
			return new ItemStack(getStack().getItem(), -1);
		}
		return inventory.decrStackSize(getSlotIndex(), par1);
	}
	
}
