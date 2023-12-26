package me.jellysquid.mods.sodium.client.render.immediate.model;

import net.minecraft.util.math.Direction;
import org.joml.*;

import java.util.Set;

public class ModelCuboid {

    public final float x1, y1, z1;
    public final float x2, y2, z2;

    public final float u0, u1, u2, u3, u4, u5;
    public final float v0, v1, v2;

    private final int faces;

    public final boolean mirror;

    public ModelCuboid(int u, int v,
                       float x1, float y1, float z1,
                       float sizeX, float sizeY, float sizeZ,
                       float extraX, float extraY, float extraZ,
                       boolean mirror,
                       float textureWidth, float textureHeight,
                       Set<Direction> renderDirections) {

        this.x1 = x1 / 16.0f;
        this.y1 = y1 / 16.0f;
        this.z1 = z1 / 16.0f;

        this.x2 = x1 + sizeX;
        this.y2 = y1 + sizeY;
        this.z2 = z1 + sizeZ;

        x1 -= extraX;
        y1 -= extraY;
        z1 -= extraZ;

        x2 += extraX;
        y2 += extraY;
        z2 += extraZ;

        if (mirror) {
            float tmp = x2;
            x2 = x1;
            x1 = tmp;
        }

        final float scaleU = 1.0f / textureWidth;
        final float scaleV = 1.0f / textureHeight;

        this.u0 = scaleU * u;
        this.u1 = scaleU * (u + sizeZ);
        this.u2 = scaleU * (u + sizeZ + sizeX);
        this.u3 = scaleU * (u + sizeZ + sizeX + sizeX);
        this.u4 = scaleU * (u + sizeZ + sizeX + sizeZ);
        this.u5 = scaleU * (u + sizeZ + sizeX + sizeZ + sizeX);

        this.v0 = scaleV * v;
        this.v1 = scaleV * (v + sizeZ);
        this.v2 = scaleV * (v + sizeZ + sizeY);

        this.mirror = mirror;

        this.faces = 0;

        for (Direction dir : renderDirections) {
             faces |= 1 << dir.ordinal();
        }
    }

    public boolean shouldDrawFace(int quadIndex) {
        return (this.faces & (1 << quadIndex)) != 0;
    }
}
