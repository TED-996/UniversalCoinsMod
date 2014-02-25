package ted996_universalcoins;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

class UCSlotCoinOutput extends Slot {
	public UCSlotCoinOutput(IInventory parInventory, int parSlotIndex, int parX, int parY) {
		super(parInventory, parSlotIndex, parX, parY);
	}

	public boolean isItemValid(ItemStack par1ItemStack){
		return false;
	}
}
