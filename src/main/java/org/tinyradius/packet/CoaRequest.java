package org.tinyradius.packet;

/**
 * CoA packet. Thanks to Michael Krastev.
 *
 * @author Michael Krastev <mkrastev@gmail.com>
 */
public class CoaRequest extends AbstractRadiusPacket {

    public CoaRequest() {
        super(COA_REQUEST, getNextPacketIdentifier());
    }

}