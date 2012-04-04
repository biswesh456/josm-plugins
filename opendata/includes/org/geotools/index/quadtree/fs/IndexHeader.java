/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
 * 
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.index.quadtree.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Logger;

import org.geotools.index.quadtree.StoreException;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 *
 * @source $URL: http://svn.osgeo.org/geotools/branches/2.7.x/modules/plugin/shapefile/src/main/java/org/geotools/index/quadtree/fs/IndexHeader.java $
 */
public class IndexHeader {
    public static final byte LSB_ORDER = -1;
    public static final byte MSB_ORDER = -2;
    public static final byte NATIVE_ORDER = 0;
    public static final byte NEW_LSB_ORDER = 1;
    public static final byte NEW_MSB_ORDER = 2;
    private static final String SIGNATURE = "SQT";
    private static final Logger LOGGER = org.geotools.util.logging.Logging
            .getLogger("org.geotools.index.quadtree");
    private byte byteOrder;

    /**
     * DOCUMENT ME!
     * 
     * @param channel
     * 
     * @throws IOException
     * @throws StoreException
     */
    public IndexHeader(ReadableByteChannel channel) throws IOException,
            StoreException {
        ByteBuffer buf = ByteBuffer.allocate(8);

        channel.read(buf);
        buf.flip();

        byte[] tmp = new byte[3];
        buf.get(tmp);

        String s = new String(tmp, "US-ASCII");

        if (!s.equals(SIGNATURE)) {
            // Old file format
            LOGGER.warning("Old qix file format; this file format "
                    + "is deprecated; It is strongly recommended "
                    + "to regenerate it in new format.");

            buf.position(0);
            tmp = buf.array();

            boolean lsb;

            if ((tmp[4] == 0) && (tmp[5] == 0) && (tmp[6] == 0)
                    && (tmp[7] == 0)) {
                lsb = !((tmp[0] == 0) && (tmp[1] == 0));
            } else {
                lsb = !((tmp[4] == 0) && (tmp[5] == 0));
            }

            this.byteOrder = lsb ? LSB_ORDER : MSB_ORDER;
        } else {
            this.byteOrder = buf.get();
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @return Returns the byteOrder.
     */
    public byte getByteOrder() {
        return this.byteOrder;
    }
}