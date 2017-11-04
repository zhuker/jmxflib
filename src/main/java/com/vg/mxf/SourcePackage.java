package com.vg.mxf;

import static com.vg.mxf.Key.key;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SourcePackage extends GenericPackage {
    TagUUID Descriptor;

    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.37.00");

    private final static int localTags[] = new int[] { 0x4701 };

    @Override
    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x4701:
                    Descriptor = inner(new TagUUID(0x4701));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public List<UUID> getReferencedUIDs() {
        List<UUID> list = new ArrayList<UUID>();
        if (Descriptor != null) {
            list.addAll(Arrays.asList(Descriptor.uuid));
        }
        return Collections.unmodifiableList(list);
    }
}
