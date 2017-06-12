/*
 * Copyright 2016 inventivetalent. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */

package io.github.dre2n.dungeonsxl.util.resourcepackapi.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.NetworkManager;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayInResourcePackStatus;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.inventivetalent.rpapi.IPacketPlayResourcePackStatus;
import org.inventivetalent.rpapi.RPApiPlugin;
import org.inventivetalent.rpapi.Status;

public class PacketPlayResourcePackStatus_v1_12_R1 implements IPacketPlayResourcePackStatus
{
    private Status status;
    private Player p;
    private static Field channelField;
    
    @Override
    public Status getStatus() {
        /*SL:55*/return this.status;
    }
    
    @Override
    public String getHash() {
        /*SL:60*/return null;
    }
    
    @Override
    public void onPacketReceive(final Object v2, final Player v3) {
        /*SL:65*/if (!(v2 instanceof Packet)) {
            return;
        }
        /*SL:66*/this.p = v3;
        try {
            final Field a1 = /*EL:69*/PacketPlayInResourcePackStatus.class.getDeclaredField("status");
            /*SL:70*/a1.setAccessible(true);
            /*SL:72*/this.status = Status.byID(((PacketPlayInResourcePackStatus.EnumResourcePackStatus)a1.get(v2)).ordinal());
        }
        catch (Exception a2) {
            /*SL:74*/a2.printStackTrace();
        }
        /*SL:77*/if (this.getStatus() != null && v3 != null) {
            /*SL:78*/RPApiPlugin.onResourcePackResult(this.getStatus(), v3, this.getHash());
        }
    }
    
    @Override
    public void inject() throws NoSuchFieldException, IllegalAccessException {
    }
    
    @Override
    public void addChannelForPlayer(final Player v0) {
        /*SL:90*/if (PacketPlayResourcePackStatus_v1_12_R1.channelField == null) {
            try {
                PacketPlayResourcePackStatus_v1_12_R1.channelField = /*EL:92*/NetworkManager.class.getDeclaredField("channel");
            }
            catch (NoSuchFieldException | SecurityException a1) {
                /*SL:94*/a1.printStackTrace();
            }
            PacketPlayResourcePackStatus_v1_12_R1.channelField.setAccessible(/*EL:96*/true);
        }
        try {
            final EntityPlayer v = /*EL:99*/((CraftPlayer)v0).getHandle();
            final Channel v2 = (Channel)PacketPlayResourcePackStatus_v1_12_R1.channelField.get(/*EL:100*/v.playerConnection.networkManager);
            /*SL:101*/new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        /*SL:106*//*EL:109*/v2.pipeline().addBefore("packet_handler", "RPApi", (io.netty.channel.ChannelHandler)new ChannelHandler(v0));
                    }
                    catch (Exception ex) {}
                }
            }, "RPApi channel adder").start();
        }
        catch (Exception v3) {
            /*SL:112*/v3.printStackTrace();
        }
    }
    
    @Override
    public void removeChannelForPlayer(final Player v0) {
        /*SL:118*/if (PacketPlayResourcePackStatus_v1_12_R1.channelField == null) {
            try {
                PacketPlayResourcePackStatus_v1_12_R1.channelField = /*EL:120*/NetworkManager.class.getDeclaredField("channel");
            }
            catch (NoSuchFieldException | SecurityException a1) {
                /*SL:122*/a1.printStackTrace();
            }
            PacketPlayResourcePackStatus_v1_12_R1.channelField.setAccessible(/*EL:124*/true);
        }
        try {
            final EntityPlayer v = /*EL:127*/((CraftPlayer)v0).getHandle();
            final Channel v2 = (Channel)PacketPlayResourcePackStatus_v1_12_R1.channelField.get(/*EL:128*/v.playerConnection.networkManager);
            /*SL:129*/new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        /*SL:134*//*EL:137*/v2.pipeline().remove("RPApi");
                    }
                    catch (Exception ex) {}
                }
            }, "RPApi channel remover").start();
        }
        catch (Exception v3) {
            /*SL:140*/v3.printStackTrace();
        }
    }
    
    public class ChannelHandler extends ByteToMessageDecoder
    {
        private Player p;
        
        public ChannelHandler(final Player a2) {
            this.p = a2;
        }
        
        public void channelRead(final ChannelHandlerContext a1, final Object a2) throws Exception {
            /*SL:154*/if (PacketPlayInResourcePackStatus.class.isAssignableFrom(a2.getClass())) {
                /*SL:155*/PacketPlayResourcePackStatus_v1_12_R1.this.onPacketReceive(a2, this.p);
            }
            /*SL:157*/super.channelRead(a1, a2);
        }
        
        protected void decode(final ChannelHandlerContext a1, final ByteBuf a2, final List<Object> a3) throws Exception {
        }
    }
}
