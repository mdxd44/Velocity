package com.velocitypowered.proxy.network.packet.clientbound;

import static com.velocitypowered.proxy.connection.VelocityConstants.EMPTY_BYTE_ARRAY;

import com.google.common.base.MoreObjects;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.network.ProtocolUtils;
import com.velocitypowered.proxy.network.packet.Packet;
import com.velocitypowered.proxy.network.packet.PacketDirection;
import com.velocitypowered.proxy.network.packet.PacketHandler;
import com.velocitypowered.proxy.network.packet.PacketReader;
import io.netty.buffer.ByteBuf;

public class ClientboundEncryptionRequestPacket implements Packet {
  public static final PacketReader<ClientboundEncryptionRequestPacket> DECODER = PacketReader.method(ClientboundEncryptionRequestPacket::new);

  private String serverId = "";
  private byte[] publicKey = EMPTY_BYTE_ARRAY;
  private byte[] verifyToken = EMPTY_BYTE_ARRAY;

  public byte[] getPublicKey() {
    return publicKey.clone();
  }

  public void setPublicKey(byte[] publicKey) {
    this.publicKey = publicKey.clone();
  }

  public byte[] getVerifyToken() {
    return verifyToken.clone();
  }

  public void setVerifyToken(byte[] verifyToken) {
    this.verifyToken = verifyToken.clone();
  }

  @Override
  public void decode(ByteBuf buf, PacketDirection direction, ProtocolVersion version) {
    this.serverId = ProtocolUtils.readString(buf, 20);

    if (version.gte(ProtocolVersion.MINECRAFT_1_8)) {
      publicKey = ProtocolUtils.readByteArray(buf, 256);
      verifyToken = ProtocolUtils.readByteArray(buf, 16);
    } else {
      publicKey = ProtocolUtils.readByteArray17(buf);
      verifyToken = ProtocolUtils.readByteArray17(buf);
    }
  }

  @Override
  public void encode(ByteBuf buf, PacketDirection direction, ProtocolVersion version) {
    ProtocolUtils.writeString(buf, this.serverId);

    if (version.gte(ProtocolVersion.MINECRAFT_1_8)) {
      ProtocolUtils.writeByteArray(buf, publicKey);
      ProtocolUtils.writeByteArray(buf, verifyToken);
    } else {
      ProtocolUtils.writeByteArray17(publicKey, buf, false);
      ProtocolUtils.writeByteArray17(verifyToken, buf, false);
    }
  }

  @Override
  public boolean handle(PacketHandler handler) {
    return handler.handle(this);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("serverId", this.serverId)
      .add("publicKey", this.publicKey)
      .add("verifyToken", this.verifyToken)
      .toString();
  }
}
