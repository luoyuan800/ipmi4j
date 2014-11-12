/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.ipmi.protocol.packet.asf;

import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.anarres.ipmi.protocol.packet.common.Bits;

/**
 * CapabilitiesResponse.
 * 
 * http://www.dmtf.org/sites/default/files/standards/documents/DSP0136.pdf
 * http://www.dmtf.org/standards/asf
 * Section 3.2.4.4 page 36.
 *
 * @author shevek
 */
public class AsfCapabilitiesResponseData extends AbstractAsfData {

    public enum SpecialCommands implements Bits.Wrapper {

        SUPPORTS_FORCE_CD_BOOT(1, 4),
        SUPPORTS_FORCE_DIAGNOSTIC_BOOT(1, 3),
        SUPPORTS_FORCE_HARD_DRIVE_SAFE_MODE_BOOT(1, 2),
        SUPPORTS_FORCE_HARD_DRIVE_BOOT(1, 1),
        SUPPORTS_FORCE_PXE_BOOT(1, 0);
        private final Bits bits;

        private SpecialCommands(@Nonnegative int byteIndex, @Nonnegative int bitIndex) {
            this.bits = Bits.forBitIndex(byteIndex, bitIndex);
        }

        @Nonnull
        public Bits getBits() {
            return bits;
        }
    }

    public enum SystemCapabilities implements Bits.Wrapper {

        SUPPORTS_RESET_BOTH_PORTS(0, 7),
        SUPPORTS_POWER_UP_BOTH_PORTS(0, 6),
        SUPPORTS_POWER_DOWN_BOTH_PORTS(0, 5),
        SUPPORTS_POWER_CYCLE_RESET_BOTH_PORTS(0, 4),
        SUPPORTS_RESET_SECURE_PORT_ONLY(0, 3),
        SUPPORTS_POWER_UP_SECURE_PORT_ONLY(0, 2),
        SUPPORTS_POWER_DOWN_SECURE_PORT_ONLY(0, 1),
        SUPPORTS_POWER_CYCLE_RESET_SECURE_PORT_ONLY(0, 0);
        private final Bits bits;

        private SystemCapabilities(@Nonnegative int byteIndex, @Nonnegative int bitIndex) {
            this.bits = Bits.forBitIndex(byteIndex, bitIndex);
        }

        @Nonnull
        public Bits getBits() {
            return bits;
        }
    }

    public enum SystemFirmwareCapabilities implements Bits.Wrapper {

        SUPPORTS_LOCK_SLEEP_BUTTON(0, 6),
        SUPPORTS_LOCK_KEYBOARD(0, 5),
        SUPPORTS_LOCK_RESET_BUTTON(0, 2),
        SUPPORTS_LOCK_POWER_BUTTON(0, 1),
        SUPPORTS_SCREEN_BLANK(0, 0);
        // TODO: More here, page 38.
        private final Bits bits;

        private SystemFirmwareCapabilities(@Nonnegative int byteIndex, @Nonnegative int bitIndex) {
            this.bits = Bits.forBitIndex(byteIndex, bitIndex);
        }

        @Nonnull
        public Bits getBits() {
            return bits;
        }
    }
    private final Set<SpecialCommands> specialCommands = EnumSet.noneOf(SpecialCommands.class);
    private final Set<SystemCapabilities> systemCapabilities = EnumSet.noneOf(SystemCapabilities.class);
    private final Set<SystemFirmwareCapabilities> systemFirmwareCapabilities = EnumSet.noneOf(SystemFirmwareCapabilities.class);

    @Override
    public AsfRcmpMessageType getMessageType() {
        return AsfRcmpMessageType.CapabilitiesResponse;
    }

    @Nonnull
    public Set<? extends SpecialCommands> getSpecialCommands() {
        return specialCommands;
    }

    @Nonnull
    public Set<? extends SystemCapabilities> getSystemCapabilities() {
        return systemCapabilities;
    }

    @Nonnull
    public Set<? extends SystemFirmwareCapabilities> getSystemFirmwareCapabilities() {
        return systemFirmwareCapabilities;
    }

    @Override
    protected int getDataWireLength() {
        return 16;
    }

    @Override
    protected void toWireData(ByteBuffer buffer) {
        buffer.putInt(IANA_ENTERPRISE_NUMBER);
        buffer.putInt(0);   // OEM defined
        buffer.put(Bits.toBytes(2, getSpecialCommands()));
        buffer.put(Bits.toByte(getSystemCapabilities()));
        buffer.put(Bits.toBytes(4, getSystemFirmwareCapabilities()));
        buffer.put((byte) 0);   // Reserved
    }
}
