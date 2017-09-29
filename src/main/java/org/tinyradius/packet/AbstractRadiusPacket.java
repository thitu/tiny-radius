package org.tinyradius.packet;

import org.tinyradius.util.RadiusUtil;

import java.security.MessageDigest;

/**
 * @author Nikolas Thitu
 *
 */
public abstract class AbstractRadiusPacket extends RadiusPacket {
    AbstractRadiusPacket(int accountingRequest, int nextPacketIdentifier) {
        super(accountingRequest, nextPacketIdentifier);
    }

    AbstractRadiusPacket() {
        super();
    }

    /**
     * Calculates the request authenticator as specified by RFC 2866.
     *
     * @see org.tinyradius.packet.RadiusPacket#updateRequestAuthenticator(java.lang.String, int, byte[])
     */
    @Override
    protected byte[] updateRequestAuthenticator(String sharedSecret, int packetLength, byte[] attributes) {
        byte[] authenticator = new byte[16];
        for (int i = 0; i < 16; i++) {
            authenticator[i] = 0;
        }

        MessageDigest md5 = getMd5Digest();
        md5.reset();
        md5.update((byte) getPacketType());
        md5.update((byte) getPacketIdentifier());
        md5.update((byte) (packetLength >> 8));
        md5.update((byte) (packetLength & 0xff));
        md5.update(authenticator, 0, authenticator.length);
        md5.update(attributes, 0, attributes.length);
        md5.update(RadiusUtil.getUtf8Bytes(sharedSecret));
        return md5.digest();
    }
}
