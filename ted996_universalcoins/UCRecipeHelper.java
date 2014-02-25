package ted996_universalcoins;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

class UCRecipeHelper {
	
	private static ItemStack oneSeller = new ItemStack(UniversalCoins.itemSeller);
	private static ItemStack oneCoin = new ItemStack(UniversalCoins.itemCoin);
	private static ItemStack oneSStack = new ItemStack(UniversalCoins.itemSmallCoinStack);
	private static ItemStack oneLStack = new ItemStack(UniversalCoins.itemLargeCoinStack);
	private static ItemStack oneHeap = new ItemStack(UniversalCoins.itemCoinHeap);
	
	
	public static void addCoinRecipes(){
		
		
		GameRegistry.addShapelessRecipe(new ItemStack(UniversalCoins.itemCoin, 9), new Object[]{
			oneSStack
		});
		GameRegistry.addShapelessRecipe(new ItemStack(UniversalCoins.itemSmallCoinStack, 9), new Object[]{
			oneLStack
		});
		GameRegistry.addShapelessRecipe(new ItemStack(UniversalCoins.itemLargeCoinStack, 9), new Object[]{
			oneHeap
		});
		
		GameRegistry.addShapelessRecipe(oneSStack, new Object[]{
				oneCoin, oneCoin, oneCoin, oneCoin, oneCoin, oneCoin, oneCoin, oneCoin, oneCoin
		});
		GameRegistry.addShapelessRecipe(oneLStack, new Object[]{
				oneSStack, oneSStack, oneSStack, oneSStack, oneSStack, oneSStack,oneSStack, oneSStack, oneSStack
		});
		GameRegistry.addShapelessRecipe(oneHeap, new Object[]{
				oneLStack, oneLStack, oneLStack, oneLStack, oneLStack, oneLStack,oneLStack, oneLStack, oneLStack
		});
		
	}

	public static void addTradeStationRecipe() {
		GameRegistry.addShapedRecipe(oneSeller, new Object[]{
			"LGE",
			"PPP",
			'L', Item.leather, 'G', Item.ingotGold, 'E', Item.enderPearl, 'P', Item.paper
		});
		GameRegistry.addShapedRecipe(new ItemStack(UniversalCoins.blockTradeStation), new Object[]{
			"IGI",
			"ICI",
			"III",
			'I', Item.ingotIron, 'G', Item.ingotGold, 'C', UniversalCoins.itemSeller
		});
	}

	

}