package ted996_universalcoins;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class UCPacketHandler implements IPacketHandler {
	
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if (packet.channel.equals("UCTS_TileEntity")) {
			handleTEPacket(manager, packet, player);
		}
		else if (packet.channel.equals("UCTS_Buttons")) {
			handleButtonsPacket(manager, packet, player);
		}
		else if (packet.channel.equals("UCTS_TE_Request")){
			handleTERequest(manager, packet, player);
		}
	}


	private void handleTEPacket(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		// Packet always server-to-player.
		EntityPlayer entityPlayer = (EntityPlayer) player;
		World world = entityPlayer.worldObj;
		if (!world.isRemote) {
			Minecraft.getMinecraft().getLogAgent()
					.logWarning("UniversalCoins: TileEntity packet received by server.");
		}
		int coinSum, x = 0, y = 0, z = 0, dimension;
		boolean bypass;
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(
				packet.data));
		try {
			coinSum = stream.readInt();
			x = stream.readInt();
			y = stream.readInt();
			z = stream.readInt();
			dimension = stream.readInt();
			bypass = stream.readBoolean();
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		if (world.getWorldInfo().getVanillaDimension() != dimension) {
			return;
		}
		TileEntity tEntity = world.getBlockTileEntity(x, y, z);
		if (tEntity != null && tEntity instanceof UCTileEntity) {
			UCTileEntity ucTileEntity = (UCTileEntity) tEntity;
			ucTileEntity.coinSum = coinSum;
			ucTileEntity.bypassActive = bypass;
			ucTileEntity.onInventoryChanged();
		}
	}
	
	private void handleButtonsPacket(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		
		// Packet always player-to-server
		EntityPlayer entityPlayer = (EntityPlayer) player;
		World world = entityPlayer.worldObj;
		if (world.isRemote) {
			Minecraft.getMinecraft().getLogAgent()
					.logWarning("UniversalCoins: Buttons packet received by client.");
		}
		int buttonClicked, x = 0, y = 0, z = 0, dimension;
		boolean bypass;
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(
				packet.data));
		try {
			buttonClicked = stream.readInt();
			x = stream.readInt();
			y = stream.readInt();
			z = stream.readInt();
			dimension = stream.readInt();
			bypass = stream.readBoolean();
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		if (world.getWorldInfo().getVanillaDimension() != dimension) {
			return;
		}
		TileEntity tEntity = world.getBlockTileEntity(x, y, z);
		if (tEntity != null && tEntity instanceof UCTileEntity) {
			UCTileEntity ucTileEntity = (UCTileEntity) tEntity;
			if (buttonClicked == UCTradeStationGUI.idBypassButton){
				ucTileEntity.setBypass(bypass);
			}
			if (buttonClicked == UCTradeStationGUI.idBuyButton) {
				ucTileEntity.onBuyPressed();
			}
			else if (buttonClicked == UCTradeStationGUI.idSellButton) {
				ucTileEntity.onSellPressed();
			}
			else if (buttonClicked == UCTradeStationGUI.idSellMaxButton){
				ucTileEntity.onSellMaxPressed();
			}
			else if (buttonClicked == UCTradeStationGUI.idBuyMaxButton){
				ucTileEntity.onBuyMaxPressed();
			}
			else if (buttonClicked <= UCTradeStationGUI.idHeapButton){
				ucTileEntity.onRetrieveButtonsPressed(buttonClicked);
			}
			ucTileEntity.onInventoryChanged();
		}
	}

	private void handleTERequest(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		// Packet always player-to-server
		EntityPlayer entityPlayer = (EntityPlayer) player;
		World world = entityPlayer.worldObj;
		if (world.isRemote) {
			Minecraft.getMinecraft().getLogAgent()
					.logWarning("UniversalCoins: TE Request packet received by client.");
		}
		int x = 0, y = 0, z = 0, dimension;
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(
				packet.data));
		try {
			x = stream.readInt();
			y = stream.readInt();
			z = stream.readInt();
			dimension = stream.readInt();
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		if (world.getWorldInfo().getVanillaDimension() != dimension) {
			return;
		}
		TileEntity tEntity = world.getBlockTileEntity(x, y, z);
		if (tEntity != null && tEntity instanceof UCTileEntity) {
			UCTileEntity ucTileEntity = (UCTileEntity) tEntity;
			ucTileEntity.dispatchPackage();
		}

	}
}
