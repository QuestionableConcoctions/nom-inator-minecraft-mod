package tameno.nom_inator.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class InsidesCollisionEntity extends Entity {
    private float width = 1.0f;
    private float height = 1.0f;

    public InsidesCollisionEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public void setDimensions(float newWidth, float newHeight) {
        width = newWidth;
        height = newHeight;
        calculateDimensions();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.changing(width, height);
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean collidesWith(Entity other) {
        return false;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("width")) this.width = nbt.getFloat("width");
        if (nbt.contains("height")) this.height = nbt.getFloat("height");
        calculateDimensions();
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("width", this.width);
        nbt.putFloat("height", this.height);
    }
}
