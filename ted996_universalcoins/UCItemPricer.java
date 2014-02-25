package ted996_universalcoins;

import java.io.*;
import java.lang.management.GarbageCollectorMXBean;
import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

class UCItemPricer {
	
	private static List<UCItemPrice> prices = new ArrayList<UCItemPrice>(0);
	private static List<Item> blacklist = new ArrayList<Item>(0);

	private static Map<Integer, Integer> defPrices = new HashMap<Integer, Integer>(0);
	private static Map<Integer, Boolean> defBlacklist = new HashMap<Integer, Boolean>(0);

	public static void initPrices() {
	}
	
	public static int getItemPrice(ItemStack itemStack) {
		if (itemStack == null) {
			return -1;
		}
		return getItemPrice(itemStack.getItem());
		
	}
	
	private static int getItemPrice(Item item) {
		for (UCItemPrice price : prices) {
			if (price.item == item) {
				return price.price;
			}
		}
		return -1;
	}
	
	public static ItemStack getRevenueStack(ItemStack itemStack) {
		int itemPrice = getItemPrice(itemStack);
		if (itemPrice != -1){
			return getRevenueStack(itemPrice);
		}
		return null;
	}
	
	public static boolean isItemInList(ItemStack itemStack) {
		return itemStack != null && isItemInList(itemStack.getItem());
	}
	
	private static boolean isItemInList(Item item) {
		for (UCItemPrice price : prices) {
			if (price.item == item) {
				return true;
			}
		}
		return false;
	}
	
	public static ItemStack getRevenueStack(int itemPrice) {
		if (itemPrice <= 64) {
			return new ItemStack(UniversalCoins.itemCoin, itemPrice);
		}
		else if (itemPrice <= 9 * 64) {
			return new ItemStack(UniversalCoins.itemSmallCoinStack,
					itemPrice / 9);
		}
		else if (itemPrice <= 81 * 64) {
			return new ItemStack(UniversalCoins.itemLargeCoinStack,
					itemPrice / 81);
		}
		else {
			return new ItemStack(UniversalCoins.itemCoinHeap, Math.min(itemPrice / 729, 64));
		}
	}
	
	private static void addItemToList(Item item, int price) {
		prices.add(new UCItemPrice(new ItemStack(item), price));
	}
	
	public static void addItemToList(Block block, int price) {
		prices.add(new UCItemPrice(new ItemStack(block), price));
	}
	
	public static void addItemToList(ItemStack itemStack, int price) {
		prices.add(new UCItemPrice(itemStack, price));
	}
	
	public static void loadItemsFromConfig(Configuration config) {
		
		config.addCustomCategoryComment(
				"ITEM_PRICES",
				"Add here the item prices.\n"
						+ "-1 means no price is set.\n"
						+ "'item.' signifies an item, 'tile.' signifies a block.");
		Property configProperty;
		int price;
		for (int i = 0; i < Item.itemsList.length; i++) {
			if (Item.itemsList[i] == null || isBlacklisted(Item.itemsList[i]))
				continue;
			Integer defaultPrice = -1;
			if (defPrices != null){
				defaultPrice = defPrices.get(Item.itemsList[i].itemID);
				if (defaultPrice == null){
					defaultPrice = -1;
				}
			}
			configProperty = config.get("ITEM_PRICES",
					Item.itemsList[i].getUnlocalizedName(), defaultPrice);
			price = configProperty.getInt();
			if (price != -1) {
				addItemToList(Item.itemsList[i], price);
			}
		}
		
	}
	
	public static void buildBlacklist(Configuration config) {
		config.addCustomCategoryComment(
				"ITEM_BLACKLIST",
				"Add here the blacklisted items (set them to true).\n"
						+ "'item.' signifies an item, 'tile.' signifies a block.\n"
						+ "Everytime this is changed, you probably should delete the price file.\n"
						+ "(although this is not necessary.)");
		Property configProperty;
		for (int i = 0; i < Item.itemsList.length; i++) {
			if (Item.itemsList[i] == null)
				continue;
			Boolean isBlackListed = false;
			if (defBlacklist != null){
				isBlackListed = defBlacklist.get(Item.itemsList[i].itemID);
				if (isBlackListed == null){
					isBlackListed = false;
				}
			}
			configProperty = config.get("ITEM_BLACKLIST",
					Item.itemsList[i].getUnlocalizedName(), isBlackListed);
			if (configProperty.getBoolean(false)) {
				addItemToBlacklist(Item.itemsList[i]);
			}
		}
	}
	
	private static void addItemToBlacklist(Item item) {
		blacklist.add(item);
		
	}
	
	private static boolean isBlacklisted(Item item) {
		return blacklist.contains(item);
	}

	public static boolean generateDefaults(){
		InputStream priceResource = UCItemPricer.class.getResourceAsStream("defaultConfigs/defaultPriceFile.cfg");
		if (priceResource == null){
			return false;
		}
		String priceString = convertStreamToString(priceResource);
		buildDefaultPriceList(priceString);

		InputStream blackResource = UCItemPricer.class.getResourceAsStream("defaultConfigs/defaultBlackFile.cfg");
		if (blackResource == null){
			return false;
		}
		String blackString = convertStreamToString(blackResource);
		buildDefaultBlackList(blackString);
		return true;
	}

	private static void buildDefaultPriceList(String priceString) {
		StringTokenizer tokenizer = new StringTokenizer(priceString, "\n\r", false);
		while (tokenizer.hasMoreElements()){
			String token = tokenizer.nextToken();
			int equalSignPosition = token.indexOf('=');
			int colonSignPosition = token.indexOf(':');
			String ulName = token.substring(colonSignPosition + 1, equalSignPosition);
			for (int i = 0; i < 32000; i++){
				Item currItem = Item.itemsList[i];
				if (currItem != null && ulName.equals(currItem.getUnlocalizedName())){
					addItemToDefaultPrices(i, Integer.parseInt(token.substring(equalSignPosition + 1)));
					break;
				}
			}
		}
	}

	private static void addItemToDefaultPrices(int itemIndex, int price) {
		defPrices.put(itemIndex, price);
	}

	private static void buildDefaultBlackList(String priceString) {
		StringTokenizer tokenizer = new StringTokenizer(priceString, "\n\r", false);
		while (tokenizer.hasMoreElements()){
			String token = tokenizer.nextToken();
			int equalSignPosition = token.indexOf('=');
			int colonSignPosition = token.indexOf(':');
			String ulName = token.substring(colonSignPosition + 1, equalSignPosition);
			for (int i = 0; i < 32000; i++){
				Item currItem = Item.itemsList[i];
				if (currItem != null && ulName.equals(currItem.getUnlocalizedName())){
					addItemToDefaultBlacklist(i, Boolean.parseBoolean(token.substring(equalSignPosition + 1)));
					break;
				}
			}
		}
	}

	private static void addItemToDefaultBlacklist(int itemIndex, boolean isBlackListed) {
		defBlacklist.put(itemIndex, isBlackListed);
	}


	private static String convertStreamToString(java.io.InputStream is) {
		//Thanks to Pavel Repin on StackOverflow.
		java.util.Scanner scanner = new java.util.Scanner(is);
		java.util.Scanner s = scanner.useDelimiter("\\A");
		String result =  s.hasNext() ? s.next() : "";
		scanner.close();
		return result;
	}

	public static void nullifyMaps(){
		defBlacklist = null;
		defPrices = null;
		//Collect it, you garbageman!
	}
}
