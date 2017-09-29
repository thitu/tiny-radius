/**
 * $Id: AccountingRequest.java,v 1.2 2006/02/17 18:14:54 wuttke Exp $
 * Created on 09.04.2005
 *
 * @author Matthias Wuttke
 * @version $Revision: 1.2 $
 */
package org.tinyradius.packet;

import org.tinyradius.attribute.IntegerAttribute;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.attribute.StringAttribute;
import org.tinyradius.util.RadiusException;

import java.util.List;

/**
 * This class represents a Radius packet of the type
 * "Accounting-Request".
 */
public class AccountingRequest extends AbstractRadiusPacket {

    /**
     * Acct-Status-Type: Start
     */
    public static final int ACCT_STATUS_TYPE_START = 1;

    /**
     * Acct-Status-Type: Stop
     */
    public static final int ACCT_STATUS_TYPE_STOP = 2;

    /**
     * Acct-Status-Type: Interim Update/Alive
     */
    public static final int ACCT_STATUS_TYPE_INTERIM_UPDATE = 3;

    /**
     * Acct-Status-Type: Accounting-On
     */
    public static final int ACCT_STATUS_TYPE_ACCOUNTING_ON = 7;

    /**
     * Acct-Status-Type: Accounting-Off
     */
    public static final int ACCT_STATUS_TYPE_ACCOUNTING_OFF = 8;
    /**
     * Radius User-Name attribute type
     */
    private static final int USER_NAME = 1;
    /**
     * Radius Acct-Status-Type attribute type
     */
    private static final int ACCT_STATUS_TYPE = 40;

    /**
     * Constructs an Accounting-Request packet to be sent to a Radius server.
     *
     * @param userName       user name
     * @param acctStatusType ACCT_STATUS_TYPE_*
     */
    public AccountingRequest(String userName, int acctStatusType) {
        super(ACCOUNTING_REQUEST, getNextPacketIdentifier());
        setUserName(userName);
        setAcctStatusType(acctStatusType);
    }

    /**
     * Constructs an empty Accounting-Request to be received by a
     * Radius client.
     */
    public AccountingRequest() {
        super();
    }

    /**
     * Retrieves the user name from the User-Name attribute.
     *
     * @return user name
     */
    public String getUserName() throws RadiusException {
        List<RadiusAttribute> attrs = getAttributes(USER_NAME);

        if (attrs.size() < 1 || attrs.size() > 1) {
            throw new RuntimeException("exactly one User-Name attribute required");
        }

        RadiusAttribute ra = attrs.get(0);
        return ra.getAttributeValue();
    }

    /**
     * Sets the User-Name attribute of this Accountnig-Request.
     *
     * @param userName user name to set
     */
    public void setUserName(String userName) {
        if (userName == null) {
            throw new NullPointerException("user name not set");
        }
        if (userName.length() == 0) {
            throw new IllegalArgumentException("empty user name not allowed");
        }

        removeAttributes(USER_NAME);
        addAttribute(new StringAttribute(USER_NAME, userName));
    }

    /**
     * Retrieves the user name from the User-Name attribute.
     *
     * @return user name
     */
    public int getAcctStatusType()
        throws RadiusException {
        RadiusAttribute ra = getAttribute(ACCT_STATUS_TYPE);
        if (ra == null) {
            return -1;
        }
        else {
            return ((IntegerAttribute) ra).getAttributeValueInt();
        }
    }

    /**
     * Sets the Acct-Status-Type attribute of this Accountnig-Request.
     *
     * @param acctStatusType ACCT_STATUS_TYPE_* to set
     */
    public void setAcctStatusType(int acctStatusType) {
        if (acctStatusType < 1 || acctStatusType > 15) {
            throw new IllegalArgumentException("bad Acct-Status-Type");
        }
        removeAttributes(ACCT_STATUS_TYPE);
        addAttribute(new IntegerAttribute(ACCT_STATUS_TYPE, acctStatusType));
    }

    /**
     * Checks the received request authenticator as specified by RFC 2866.
     */
    @Override
    protected void checkRequestAuthenticator(String sharedSecret, int packetLength, byte[] attributes) throws RadiusException {
        byte[] expectedAuthenticator = updateRequestAuthenticator(sharedSecret, packetLength, attributes);
        byte[] receivedAuth = getAuthenticator();
        for (int i = 0; i < 16; i++) {
            if (expectedAuthenticator[i] != receivedAuth[i]) {
                throw new RadiusException("request authenticator invalid");
            }
        }
    }
}