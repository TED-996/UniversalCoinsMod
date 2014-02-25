package ted996_universalcoins;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

class UCTradeStationGUI extends GuiContainer {
	
	private UCTileEntity tileEntity;
	private GuiButton buyButton, sellButton;
	private GuiButton retrCoinButton, retrSStackButton, retrLStackButton, retrHeapButton, bypassButton,
			sellMaxButton, buyMaxButton;
	public static final int idBuyButton = 0;
	public static final int idSellButton = 1;
	public static final int idCoinButton = 2;
	private static final int idSStackButton = 3;
	private static final int idLStackButton = 4;
	public static final int idHeapButton = 5;
	public static final int idBypassButton = 6;
	public static final int idSellMaxButton = 7;
	public static final int idBuyMaxButton = 8;

	boolean bypass = false;
	
	public UCTradeStationGUI(InventoryPlayer inventoryPlayer,
			UCTileEntity parTileEntity) {
		super(new UCContainer(inventoryPlayer, parTileEntity));
		tileEntity = parTileEntity;
		xSize = 176;
		ySize = 200;

		bypass = parTileEntity.bypassActive;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		buyButton = new GuiButton(idBuyButton, 60 + (width - xSize) / 2, 20 + (height - ySize) / 2, 25, 11, "Buy");
		sellButton = new GuiButton(idSellButton, 60 + (width - xSize) / 2, 38 + (height - ySize) / 2, 25, 11, "Sell");
		bypassButton = new GuiButton(idBypassButton, 133 + (width - xSize) / 2, 41 + (height - ySize) / 2, 38, 11, "Auto");
		buyMaxButton = new GuiButton(idBuyMaxButton, 31 + (width - xSize) / 2, 52 + (height - ySize) / 2, 47, 11, "Buy Max");
		sellMaxButton = new GuiButton(idSellMaxButton, 31 + (width - xSize) / 2, 67 + (height - ySize) / 2, 47, 11, "Sell Max");
		retrCoinButton = new GuiButton(idCoinButton, 69 + (width - xSize) / 2, 99 + (height - ySize) / 2, 18, 18, "");
		retrSStackButton = new GuiButton(idSStackButton, 88 + (width - xSize) / 2, 99 + (height - ySize) / 2, 18, 18, "");
		retrLStackButton = new GuiButton(idLStackButton, 107 + (width - xSize) / 2, 99 + (height - ySize) / 2, 18, 18, "");
		retrHeapButton = new GuiButton(idHeapButton, 126 + (width - xSize) / 2, 99 + (height - ySize) / 2, 18, 18, "");
		buttonList.clear();
		buttonList.add(buyButton);
		buttonList.add(sellButton);
		buttonList.add(retrCoinButton);
		buttonList.add(retrSStackButton);
		buttonList.add(retrLStackButton);
		buttonList.add(retrHeapButton);
		buttonList.add(bypassButton);
		buttonList.add(buyMaxButton);
		buttonList.add(sellMaxButton);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		fontRenderer.drawString("Universal Coins Trade Station", 6, 5, 4210752);
		// draws "Inventory" or your regional equivalent
		fontRenderer.drawString(
				StatCollector.translateToLocal("container.inventory"), 6,
				ySize - 96 + 2, 4210752);
		fontRenderer.drawString(String.valueOf(tileEntity.coinSum), 57, 85,
				4210752);
		String priceInLocal = "Price:";
		int stringWidth = fontRenderer.getStringWidth(priceInLocal);
		fontRenderer.drawString(priceInLocal, 118 - stringWidth, 65, 4210752);
		if (tileEntity.itemPrice != 0){
			fontRenderer.drawString(String.valueOf(tileEntity.itemPrice), 124, 65,
					4210752);
		}
		else{
			fontRenderer.drawString("No item.", 124, 65,
					4210752);
		}

		drawOverlay();
		// fontRenderer.drawString(String.valueOf(tileEntity.itemPrice), 60,
		// 100, 4210752);
	}

	private void drawOverlay() {
		int x, y, u = 176, v = 0;
		//int x_offset = -125, y_offset = -20;
		int x_offset = -guiLeft;
		int y_offset = -guiTop;
		this.mc.renderEngine
				.func_110577_a(new ResourceLocation(UniversalCoins.modid, "textures/gui/tradeStation.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 70 + x_offset;
		y = (height - ySize) / 2 + 100 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);

		v += 16;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 89 + x_offset;
		y = (height - ySize) / 2 + 100 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);

		v += 16;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 108 + x_offset;
		y = (height - ySize) / 2 + 100 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);

		v += 16;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 127 + x_offset;
		y = (height - ySize) / 2 + 100 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);

		if (!bypass){
			v += 16;
		}
		else{
			v += 22;
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 95 + x_offset;
		y = (height - ySize) / 2 + 43 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 5, 6);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		bypass = tileEntity.bypassActive;

		buyButton.enabled = tileEntity.buyButtonActive;
		sellButton.enabled = tileEntity.sellButtonActive;
		sellMaxButton.enabled = sellButton.enabled;
		buyMaxButton.enabled = buyButton.enabled;
		retrCoinButton.enabled = tileEntity.coinButtonActive;
		retrSStackButton.enabled = tileEntity.sStackButtonActive;
		retrLStackButton.enabled = tileEntity.lStackButtonActive;
		retrHeapButton.enabled = tileEntity.heapButtonActive;


		this.mc.renderEngine
				.func_110577_a(new ResourceLocation(UniversalCoins.modid, "textures/gui/tradeStation.png"));
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		//drawOverlay();
		
	}
	
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.id == idBypassButton){
			bypass = !bypass;
			tileEntity.setBypass(bypass);
		}
		if (par1GuiButton.id == idBuyButton){
			tileEntity.onBuyPressed();
		}
		else if (par1GuiButton.id == idSellButton){
			tileEntity.onSellPressed();
		}
		else if (par1GuiButton.id == idSellMaxButton){
			tileEntity.onSellMaxPressed();
		}
		else if (par1GuiButton.id == idBuyMaxButton){
			tileEntity.onBuyMaxPressed();
		}
		else if (par1GuiButton.id <= idHeapButton) {
			tileEntity.onRetrieveButtonsPressed(par1GuiButton.id);
		}
		if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
			//Minecraft.getMinecraft().getLogAgent()
			//		.logInfo("The server is also getting buttons.");
			return;
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		ByteArrayOutputStream stream = new ByteArrayOutputStream(21);
		DataOutputStream outputStream = new DataOutputStream(stream);
		try {
			outputStream.writeInt(par1GuiButton.id);
			outputStream.writeInt(tileEntity.xCoord);
			outputStream.writeInt(tileEntity.yCoord);
			outputStream.writeInt(tileEntity.zCoord);
			outputStream.writeInt(tileEntity.worldObj.getWorldInfo()
					.getVanillaDimension());
			outputStream.writeBoolean(bypass);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		packet.channel = "UCTS_Buttons";
		packet.data = stream.toByteArray();
		packet.length = stream.size();
		PacketDispatcher.sendPacketToServer(packet);
	}
	
}
