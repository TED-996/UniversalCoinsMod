package ted996_universalcoins;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;


class UCSlotCoinInput extends Slot {


	public UCSlotCoinInput(IInventory par1IInventory, int par2, int par3, int par4) {
		super(par1IInventory, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack){
		if (par1ItemStack == null){
			return true;
		}
		Item itemInStack = par1ItemStack.getItem();
		return (itemInStack == UniversalCoins.itemCoin || itemInStack == UniversalCoins.itemSmallCoinStack ||
				itemInStack == UniversalCoins.itemLargeCoinStack || itemInStack == UniversalCoins.itemCoinHeap);
	}
}
