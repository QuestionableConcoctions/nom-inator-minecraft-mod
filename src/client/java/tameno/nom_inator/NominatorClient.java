package tameno.nom_inator;

import com.tameno.snatched.Snatched;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class NominatorClient implements ClientModInitializer {

	private boolean wasUsing = false;

	private boolean wasDropping = false;

	@Override
	public void onInitializeClient() {

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;

			// Use air
			boolean isUsing = client.options.useKey.isPressed();
			boolean hasNoTarget = client.crosshairTarget == null || client.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.MISS;
			if (isUsing) {
				if (wasUsing) return;
				wasUsing = true;

				if (hasNoTarget) {

					PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
					ClientPlayNetworking.send(Nominator.USE_AIR_PACKET_ID, buf);

				}

			} else {
				wasUsing = false;
			}

		});

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (client.player == null) return;

			// Drop nothing, this has to happen at the start of the tick because otherwise, we will drop the item before
			// we can see whet the player was holding
			boolean isDropping = client.options.dropKey.isPressed();
			boolean hasNoItem = client.player.getMainHandStack().isEmpty();
			if (isDropping) {
				if (wasDropping) return;
				wasDropping = true;

				if (hasNoItem) {

					PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
					ClientPlayNetworking.send(Nominator.DROP_NOTHING_PACKET_ID, buf);

				}

			} else {
				wasDropping = false;
			}
		});
	}
}