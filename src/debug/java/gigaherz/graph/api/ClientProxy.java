package gigaherz.graph.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector3d;

public class ClientProxy extends Proxy
{
    public void preInit()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void init()
    {

    }

    @SubscribeEvent
    public void renderWorldLast(RenderWorldLastEvent event)
    {
        RayTraceResult hit = Minecraft.getMinecraft().objectMouseOver;

        if (hit.typeOfHit != RayTraceResult.Type.BLOCK)
            return;

        BlockPos pos = hit.getBlockPos();
        TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(pos);

        if (!(te instanceof TileNetworkTest))
            return;

        TileNetworkTest network = (TileNetworkTest) te;

        GraphObject theObject = network.getNetworkHandler();

        Graph graph = theObject.getGraph();

        if (graph == null)
            return;

        //float partialTicks = event.getPartialTicks();

        GlStateManager.disableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();

        Vec3d look = Minecraft.getMinecraft().thePlayer.getPositionEyes(event.getPartialTicks());
        GlStateManager.pushMatrix();
        GlStateManager.translate(-look.xCoord, -look.yCoord + Minecraft.getMinecraft().thePlayer.eyeHeight, -look.zCoord);

        Tessellator tess = Tessellator.getInstance();
        VertexBuffer vb = tess.getBuffer();
        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        int color = graph.getGraphUid() * 8191;
        int r = (color) & 255;
        int g = (color >> 8) & 255;
        int b = (color >> 16) & 255;

        for (GraphObject objs : graph.getObjects())
        {
            Vector3d pos1 = ((DebugGraphObject) objs).getPosition();
            for (GraphObject other : graph.getNeighbours(objs))
            {
                if (!graph.contains(other))
                {
                    System.out.println("Verify error!");
                }

                Vector3d pos2 = ((DebugGraphObject) other).getPosition();
                vb.pos(pos1.x + 0.5f, pos1.y + 0.5f, pos1.z + 0.5f).color(r, g, b, 255).endVertex();
                vb.pos(pos2.x + 0.5f, pos2.y + 0.5f, pos2.z + 0.5f).color(r, g, b, 255).endVertex();
            }
        }

        tess.draw();

        GlStateManager.popMatrix();
    }
}
