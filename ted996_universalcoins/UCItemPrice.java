package ted996_universalcoins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

class UCItemPrice {
	public Item item;
	public int price;
	
	public UCItemPrice(ItemStack parItemStack, int parPrice) {
		item = parItemStack.getItem();
		price = parPrice;
	}
	
}
