package com.vg.mxf;

import static com.vg.mxf.Preconditions.checkState;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vg.mxf.Registry.ULDesc;

public class InterchangeObject extends MxfValue {
    private final static Logger log = Logger.getLogger(InterchangeObject.class.getName());

    TagUUID InstanceUID = null;
    TagUUID GenerationUID = null;
    TagUUID ObjectClass = null;
    List<TagValue> unhandled;

    @Override
    public void parse() {
        ByteBuffer buf = getContent();
        buf.position(size());
        while (buf.hasRemaining()) {
            int localTag = buf.getShort() & 0xffff;
            // System.out.println("localTag: " + Integer.toHexString(localTag));
            int sz = buf.getShort() & 0xffff;
            int pos = buf.position();
            if (!handleTag(localTag, sz, buf)) {
                String msg = "unhandled tag " + Integer.toHexString(localTag);
                if (ltks != null) {
                    LocalTagKey ltk = ltks.getLtk(localTag);
                    if (ltk != null) {
                        ULDesc desc = Registry.getInstance().get(ltk.key);
                        String n = "";
                        String d = "";
                        if (desc != null) {
                            n = desc.refName;
                            d = desc.desc;
                        }
                        msg = String.format(this.getClass().getName() + " unhandled tag %04X: %s %s %s", localTag, ltk.key.toString(), n, d);

                    }
                    if (unhandled == null) {
                        unhandled = new ArrayList<TagValue>();
                    }
                    unhandled.add(inner(new TagValue(localTag, sz)));
                }

                log.log(Level.FINE, msg);
            }
            buf.position(pos + sz);
        }

    }

    private final static int localTags[] = new int[] { 0x0101, 0x0102, 0x3c0a, };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (Arrays.binarySearch(localTags, localTag) >= 0) {
            switch (localTag) {
            case 0x3c0a:
                checkState(16 == sz);
                InstanceUID = inner(new TagUUID(0x3c0a));
                break;
            case 0x0102:
                checkState(16 == sz);
                GenerationUID = inner(new TagUUID(0x0102));
                break;
            case 0x0101:
                checkState(16 == sz);
                ObjectClass = inner(new TagUUID(0x0101));
                break;
            }
            return true;
        }
        return false;
    }

    @Override
    public UUID getInstanceUID() {
        return InstanceUID != null ? InstanceUID.uuid : UUID.ZERO;
    }
}
