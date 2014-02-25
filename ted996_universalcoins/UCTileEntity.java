package ted996_universalcoins;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;

public class UCTileEntity extends TileEntity implements IInventory {
	private ItemStack[] inventory;
	private final int invSize = 5;
	public static final int revenueSlot = 2;
	public static final int tradedItemSlot = 3;
	public static final int boughtItemsSlot = 4;
	public static final int coinInputSlot = 0;
	public static final int coinOutputSlot = 1;
	private static final int[] multiplier = new int[] { 1, 9, 81, 729 };
	private static final Item[] coins = new Item[] { UniversalCoins.itemCoin,
			UniversalCoins.itemSmallCoinStack,
			UniversalCoins.itemLargeCoinStack, UniversalCoins.itemCoinHeap };
	public int coinSum = 0;
	public int itemPrice;
	private boolean needsPackageSending = true;
	
	public boolean buyButtonActive = false;
	public boolean sellButtonActive = false;
	public boolean coinButtonActive = false;
	public boolean sStackButtonActive = false;
	public boolean lStackButtonActive = false;
	public boolean heapButtonActive = false;

	public boolean bypassActive = false;


	public UCTileEntity() {
		super();
		inventory = new ItemStack[invSize];
		//Minecraft.getMinecraft().getLogAgent()
		//		.logInfo("In the tile entity constructor.");
	}
	
	@Override
	public int getSizeInventory() {
		return invSize;
	}
	
	@Override
	public ItemStack getStackInSlot(int i) {
		if (i >= invSize) {
			return null;
		}
		return inventory[i];
	}
	
	@Override
	public ItemStack decrStackSize(int i, int j) {
		/*
		 * Minecraft.getMinecraft().getLogAgent()
		 * .logInfo("Stack Size Decreased in slot " + i);
		 */
		ItemStack newStack;
		if (inventory[i] == null) {
			return null;
		}
		if (inventory[i].stackSize <= j) {
			newStack = inventory[i];
			inventory[i] = null;
			
			return newStack;
		}
		newStack = ItemStack.copyItemStack(inventory[i]);
		newStack.stackSize = j;
		inventory[i].stackSize -= j;
		return newStack;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return getStackInSlot(i);
	}
	
	@Override
	public void setInventorySlotContents(int i, ItemStack itemStack) {
		inventory[i] = itemStack;
		if (itemStack != null) {
			if (i == coinInputSlot) {
				int coinType = getCoinType(itemStack.getItem());
				if (coinType != -1) {
					coinSum += itemStack.stackSize * multiplier[coinType];
					inventory[i] = null;
					setNeedsPackageSendingFlag();
					//Minecraft
					//		.getMinecraft()
					//		.getLogAgent()
					//		.logInfo(
					//				"SetInvSlotContents.. Coin Sum: " + coinSum);
				}
			}
		}
	}
	
	private int getCoinType(Item item) {
		for (int i = 0; i < 4; i++) {
			if (item == coins[i]) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public String getInvName() {
		return "Universal Trade Station";
	}
	
	@Override
	public boolean isInvNameLocalized() {
		return false;
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this
				&& entityplayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5,
						zCoord + 0.5) < 64;
	}
	
	@Override
	public void openChest() {
		
	}
	
	@Override
	public void closeChest() {
		
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		Item stackItem = itemstack.getItem();
		if (i == coinInputSlot){
			return stackItem == UniversalCoins.itemCoin
				|| stackItem == UniversalCoins.itemSmallCoinStack
				|| stackItem == UniversalCoins.itemLargeCoinStack
				|| stackItem == UniversalCoins.itemCoinHeap;
		}
		else{ //noinspection RedundantIfStatement
			if (i == tradedItemSlot){
				return true;
			}
			else{
				return false;
			}
		}
		
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		
		NBTTagList tagList = tagCompound.getTagList("Inventory");
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inventory.length) {
				inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		try{
			coinSum = tagCompound.getShort("CoinsLeft");
		}
		catch(Throwable ex){
			try{
				coinSum = tagCompound.getInteger("CoinsLeft");
			}
			catch(Throwable ex2){
				coinSum = 0;
			}
		}
		try{
			bypassActive = tagCompound.getBoolean("Bypass");
		}
		catch (Throwable ex){
			bypassActive = false;
		}
		//Minecraft.getMinecraft().getLogAgent()
		//		.logInfo("UniversalCoins: In the NBT reader. Coin Sum: " + coinSum);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < inventory.length; i++) {
			ItemStack stack = inventory[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		tagCompound.removeTag("CoinsLeft");
		tagCompound.setTag("Inventory", itemList);
		tagCompound.setInteger("CoinsLeft", coinSum);
		tagCompound.setBoolean("Bypass", bypassActive);
		//Minecraft.getMinecraft().getLogAgent()
		//		.logInfo("In the NBT writer. Coin Sum: " + coinSum);
	}
	
	//@SuppressWarnings("RedundantIfStatement")
	@Override
	public void onInventoryChanged() {
		//Minecraft.getMinecraft().getLogAgent()
		//		.logInfo("Before the onInvChanged. Coin Sum: " + coinSum);
		if (needsPackageSending){
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ) {
				dispatchPackage();
			}
			else if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
				requestCoinSumPackage();
			}
			needsPackageSending = false;
		}
		super.onInventoryChanged();
		activateBuySellButtons();
		activateRetrieveButtons();
		//Minecraft.getMinecraft().getLogAgent()
		//		.logInfo("After the onInvChanged. Coin Sum: " + coinSum);
		
	}

	private void activateBuySellButtons() {
		if (inventory[tradedItemSlot] == null) {
			itemPrice = 0;
			buyButtonActive = false;
			sellButtonActive = false;
		}
		else {
			itemPrice = UCItemPricer.getItemPrice(inventory[tradedItemSlot]);
			if (itemPrice == -1){
				itemPrice = 0;
				buyButtonActive = false;
				sellButtonActive = false;
			}
			else{
				ItemStack revenueStack = UCItemPricer.getRevenueStack(itemPrice);
				sellButtonActive = bypassActive || inventory[revenueSlot] == null ||
						(inventory[revenueSlot].getItem() == revenueStack.getItem() &&
						inventory[revenueSlot].stackSize + revenueStack.stackSize <= 64);
				buyButtonActive = (inventory[boughtItemsSlot] == null || (inventory[boughtItemsSlot])
						.getItem() == inventory[tradedItemSlot].getItem()
						&& inventory[boughtItemsSlot].stackSize < inventory[tradedItemSlot].getItem().getItemStackLimit())
						&& coinSum >= itemPrice;
			}
		}
	}


	private void activateRetrieveButtons() {
		coinButtonActive = false;
		sStackButtonActive = false;
		lStackButtonActive = false;
		heapButtonActive = false;
		if (coinSum > 0){
			coinButtonActive = inventory[coinOutputSlot] == null || (inventory[coinOutputSlot].getItem() == UniversalCoins.itemCoin &&
					inventory[coinOutputSlot].stackSize != 64);
		}
		if (coinSum >= 9){
			sStackButtonActive = inventory[coinOutputSlot] == null ||
					(inventory[coinOutputSlot].getItem() == UniversalCoins.itemSmallCoinStack &&
							inventory[coinOutputSlot].stackSize != 64);
		}
		if (coinSum >= 81) {
			lStackButtonActive = inventory[coinOutputSlot] == null ||
					(inventory[coinOutputSlot].getItem() == UniversalCoins.itemLargeCoinStack &&
							inventory[coinOutputSlot].stackSize != 64);
		}
		if (coinSum >= 729){
			heapButtonActive = inventory[coinOutputSlot] == null ||
					(inventory[coinOutputSlot].getItem() == UniversalCoins.itemCoinHeap &&
							inventory[coinOutputSlot].stackSize != 64);
		}
	}

	public void setBypass(boolean newValue){
		bypassActive = newValue;
		activateBuySellButtons();
	}

	public void dispatchPackage() { //packet server-to-player
		Packet250CustomPayload packet = new Packet250CustomPayload();
		ByteArrayOutputStream stream = new ByteArrayOutputStream(21);
		DataOutputStream outputStream = new DataOutputStream(stream);
		try {
			outputStream.writeInt(coinSum);
			outputStream.writeInt(xCoord);
			outputStream.writeInt(yCoord);
			outputStream.writeInt(zCoord);
			outputStream.writeInt(this.worldObj.getWorldInfo().getVanillaDimension());
			outputStream.writeBoolean(bypassActive);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		packet.channel = "UCTS_TileEntity";
		packet.data = stream.toByteArray();
		packet.length = stream.size();
		PacketDispatcher.sendPacketToAllPlayers(packet);
	}

	void requestCoinSumPackage(){
		Packet250CustomPayload packet = new Packet250CustomPayload();
		ByteArrayOutputStream stream = new ByteArrayOutputStream(16);
		DataOutputStream outputStream = new DataOutputStream(stream);
		try {
			outputStream.writeInt(xCoord);
			outputStream.writeInt(yCoord);
			outputStream.writeInt(zCoord);
			outputStream.writeInt(this.worldObj.getWorldInfo().getVanillaDimension());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		packet.channel = "UCTS_TE_Request";
		packet.data = stream.toByteArray();
		packet.length = stream.size();
		PacketDispatcher.sendPacketToServer(packet);
	}

	public void onSellPressed(){
		onSellPressed(1);
	}
	
	public void onSellPressed(int amount) {
		/*if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
			Minecraft.getMinecraft().getLogAgent().logInfo("Sell Pressed on client side.");
		}
		else{
			Minecraft.getMinecraft().getLogAgent().logInfo("Sell Pressed on server side.");
		}*/
		if (inventory[tradedItemSlot] == null){
			sellButtonActive = false;
			return;
		}
		if (amount > inventory[tradedItemSlot].stackSize){
			return;
		}
		itemPrice = UCItemPricer.getItemPrice(inventory[tradedItemSlot]);
		if (itemPrice == -1){
			sellButtonActive = false;
			return;
		}
		ItemStack revenueStack = UCItemPricer.getRevenueStack(itemPrice * amount);
		if (!bypassActive){
			if (inventory[revenueSlot] == null) {
				inventory[revenueSlot] = revenueStack;
				inventory[tradedItemSlot].stackSize -= amount;
				if (inventory[tradedItemSlot].stackSize <= 0){
					inventory[tradedItemSlot] = null;
				}
			}
			else if (inventory[revenueSlot].getItem() == revenueStack.getItem() &&
					 inventory[revenueSlot].stackSize + revenueStack.stackSize <= revenueStack.getMaxStackSize()){
				inventory[revenueSlot].stackSize += revenueStack.stackSize;

				inventory[tradedItemSlot].stackSize -= amount;
				if (inventory[tradedItemSlot].stackSize <= 0){
					inventory[tradedItemSlot] = null;
				}
			}
			else{
				sellButtonActive = false;
			}
		}
		else{
			inventory[tradedItemSlot].stackSize -= amount;
			if (inventory[tradedItemSlot].stackSize <= 0){
				inventory[tradedItemSlot] = null;
			}
			coinSum += itemPrice * amount;
		}
		
	}

	public void onSellMaxPressed() {
		int amount = 0;
		if (inventory[tradedItemSlot] == null){
			if (inventory[tradedItemSlot] == null){
				sellButtonActive = false;
				return;
			}
		}
		itemPrice = UCItemPricer.getItemPrice(inventory[tradedItemSlot]);
		if (itemPrice == -1){
			sellButtonActive = false;
			return;
		}

		ItemStack revenueStack;
		if (!bypassActive){
			if (inventory[revenueSlot] == null) {
				revenueStack = UCItemPricer.getRevenueStack(inventory[tradedItemSlot].stackSize * itemPrice);
				if (revenueStack.stackSize <= 64){
					amount = inventory[tradedItemSlot].stackSize;
				}
				else{
					amount = (64 * 729) / itemPrice;
				}
			}
			else if (inventory[revenueSlot].stackSize < 64) {
				int lastOK = 0;
				for (int i = 1; i <= inventory[tradedItemSlot].stackSize; i++){
					revenueStack = UCItemPricer.getRevenueStack(i * itemPrice);
					if (revenueStack.getItem() == inventory[revenueSlot].getItem() &&
						inventory[revenueSlot].stackSize + revenueStack.stackSize <= 64){
						lastOK = i;
					}
				}
				amount = lastOK;
			}
		}
		else{
			amount = inventory[tradedItemSlot].stackSize;
		}
		if (amount != 0){
			onSellPressed(amount);
		}
	}

	public void onBuyPressed(){
		onBuyPressed(1);
	}
	
	public void onBuyPressed(int amount) {
		/*if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
			Minecraft.getMinecraft().getLogAgent().logInfo("Buy Pressed on client side.");
		}
		else{
			Minecraft.getMinecraft().getLogAgent().logInfo("Buy Pressed on server side.");
		}*/
		if (inventory[tradedItemSlot] == null){
			buyButtonActive = false;
			return;
		}
		itemPrice = UCItemPricer.getItemPrice(inventory[tradedItemSlot]);
		if (itemPrice == -1 || coinSum < itemPrice * amount){
			buyButtonActive = false;
			return;
		}
		if (inventory[boughtItemsSlot] == null && inventory[tradedItemSlot].getMaxStackSize() >= amount){
			coinSum -= itemPrice * amount;
			inventory[boughtItemsSlot] = ItemStack.copyItemStack(inventory[tradedItemSlot]);
			inventory[boughtItemsSlot].stackSize = amount;
		}
		else if (inventory[boughtItemsSlot].getItem() == inventory[tradedItemSlot].getItem() &&
				inventory[boughtItemsSlot].getItemDamage() == inventory[tradedItemSlot].getItemDamage() &&
				inventory[boughtItemsSlot].stackSize + amount <= inventory[tradedItemSlot].getMaxStackSize()) {
			coinSum -= itemPrice * amount;
			inventory[boughtItemsSlot].stackSize += amount;
		}
		else {
			buyButtonActive = false;
		}
		setNeedsPackageSendingFlag();
	}

	public void onBuyMaxPressed() {
		int amount = 0;
		if (inventory[tradedItemSlot] == null){
			buyButtonActive = false;
			return;
		}
		itemPrice = UCItemPricer.getItemPrice(inventory[tradedItemSlot]);
		if (itemPrice == -1 || coinSum < itemPrice){ //can't buy even one
			buyButtonActive = false;
			return;
		}

		if (inventory[boughtItemsSlot] == null){ //empty stack
			if (inventory[tradedItemSlot].getMaxStackSize() * itemPrice <= coinSum){
				amount = inventory[tradedItemSlot].getMaxStackSize(); //buy one stack
			}
			else{
				amount = coinSum / itemPrice; //buy as many as i can.
			}
		}
		else if (inventory[boughtItemsSlot].getItem() == inventory[tradedItemSlot].getItem() &&
				inventory[boughtItemsSlot].getItemDamage() == inventory[tradedItemSlot].getItemDamage() &&
				inventory[boughtItemsSlot].stackSize < inventory[tradedItemSlot].getItem().getItemStackLimit()) {

			if ((inventory[boughtItemsSlot].getMaxStackSize() - inventory[boughtItemsSlot].stackSize)
					* itemPrice <= coinSum){
				amount = inventory[boughtItemsSlot].getMaxStackSize() - inventory[boughtItemsSlot].stackSize;
				//buy as much as i can fit in a stack
			}
			else{
				amount = coinSum / itemPrice; //buy as many as i can.
			}
		}
		else {
			buyButtonActive = false;
		}
		onBuyPressed(amount);
	}

	void setNeedsPackageSendingFlag(){
		needsPackageSending = true;
	}

	public void onRetrieveButtonsPressed(int buttonClickedID) {
		int absoluteButton = buttonClickedID - UCTradeStationGUI.idCoinButton;
		int multiplier = 1;
		for (int i = 0; i < absoluteButton; i++){
			multiplier *= 9;
		}
		Item itemOnButton = coins[absoluteButton];
		if (coinSum < multiplier ||
				(inventory[coinOutputSlot] != null && inventory[coinOutputSlot].getItem() != itemOnButton) ||
				(inventory[coinOutputSlot] != null && inventory[coinOutputSlot].stackSize == 64)){
			return;
		}
		coinSum -= multiplier;
		if (inventory[coinOutputSlot] == null){
			inventory[coinOutputSlot] = new ItemStack(itemOnButton);
		}
		else{
			inventory[coinOutputSlot].stackSize++;
		}

	}
}
