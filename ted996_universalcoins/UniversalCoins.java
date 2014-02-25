package ted996_universalcoins;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = UniversalCoins.modid, name = "Universal Coins", version = "0.1.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = {
		"UCTS_TileEntity", "UCTS_Buttons", "UCTS_TE_Request" }, packetHandler = UCPacketHandler.class)
public class UniversalCoins {
	@Instance("ted_996_universalcoins")
	public static UniversalCoins instance;
	public static final String modid = "ted_996_universalcoins";
	
	public static Item itemCoin;
	public static Item itemSmallCoinStack;
	public static Item itemLargeCoinStack;
	public static Item itemCoinHeap;
	public static Item itemSeller;
	
	public static Block blockTradeStation;
	
	private static int itemCoinID = 5221;
	private static int itemSSTackID = 5222;
	private static int itemLStackID = 5223;
	private static int itemHeapID = 5224;
	private static int itemSellerID = 5225;
	private static int blockTradeStationID = 522;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		Configuration config = new Configuration(
				event.getSuggestedConfigurationFile());
		
		config.load();
		itemCoinID = config.getItem("itemCoin", itemCoinID).getInt();
		itemSSTackID = config.getItem("itemSmallCoinStack", itemSSTackID).getInt();
		itemLStackID = config.getItem("itemLargeCoinStack", itemLStackID).getInt();
		itemHeapID = config.getItem("itemCoinHeap", itemHeapID).getInt();
		itemSellerID = config.getItem("itemSeller", itemSellerID).getInt();
		
		blockTradeStationID = config.getBlock("blockTradeStation", blockTradeStationID).getInt();
		config.save();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		itemCoin = new ItemCoin(itemCoinID).setUnlocalizedName("itemCoin");
		itemSmallCoinStack = new ItemSmallCoinStack(itemSSTackID)
				.setUnlocalizedName("itemSmallCoinStack");
		itemLargeCoinStack = new ItemLargeCoinStack(itemLStackID)
				.setUnlocalizedName("itemLargeCoinStack");
		itemCoinHeap = new ItemCoinHeap(itemHeapID)
				.setUnlocalizedName("itemCoinHeap");
		itemSeller = new ItemSeller(itemSellerID)
				.setUnlocalizedName("itemSeller");
		blockTradeStation = new BlockTradeStation(blockTradeStationID)
				.setUnlocalizedName("blockTradeStation");
		
		GameRegistry.registerBlock(blockTradeStation,
				modid + (blockTradeStation.getUnlocalizedName().substring(5)));
		LanguageRegistry.addName(blockTradeStation, "Universal Trade Station");
		LanguageRegistry.addName(itemCoin, "Coin");
		LanguageRegistry.addName(itemSmallCoinStack, "Coin Stack");
		LanguageRegistry.addName(itemLargeCoinStack, "Large Coin Stack");
		LanguageRegistry.addName(itemCoinHeap, "Coin Pile");
		
		LanguageRegistry.addName(itemSeller, "Selling Catalogue");
		
		UCRecipeHelper.addCoinRecipes();
		UCRecipeHelper.addTradeStationRecipe();
		UCItemPricer.initPrices();
		
		GameRegistry.registerTileEntity(UCTileEntity.class, "UCTSTileEntity");
		NetworkRegistry.instance().registerGuiHandler(this, new UCGuiHandler());
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (!UCItemPricer.generateDefaults()){
			Minecraft.getMinecraft().getLogAgent().logWarning("UniversalCoins: Could not read the config defaults.");
		}
		File file = new File("config/ted_996_universalcoins_ItemBlacklist.cfg");
		Configuration config = new Configuration(file);
		config.load();
		UCItemPricer.buildBlacklist(config);
		
		config.save();
		
		file = new File("config/ted_996_universalcoins_ItemPrices.cfg");
		config = new Configuration(file);
		
		config.load();
		UCItemPricer.loadItemsFromConfig(config);
		config.save();
		UCItemPricer.nullifyMaps();
	}
	

	
}
