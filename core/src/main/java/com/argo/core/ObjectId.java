/*
 * Copyright (c) 2008-2014 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.argo.core;

import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A globally unique identifier for objects.
 * <p>Consists of 12 bytes, divided as follows:
 * <blockquote><pre>
 * <table border="1">
 * <tr><td>0</td><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td>
 *     <td>7</td><td>8</td><td>9</td><td>10</td><td>11</td></tr>
 * <tr><td colspan="4">time</td><td colspan="3">machine</td>
 *     <td colspan="2">pid</td><td colspan="3">inc</td></tr>
 * </table>
 * </pre></blockquote>
 *
 * @dochub objectids
 */
public class ObjectId implements Comparable<ObjectId> , java.io.Serializable {

    private static final long serialVersionUID = -4415279469780082174L;

    static final Logger LOGGER = Logger.getLogger( "org.bson.ObjectId" );

    /** Gets a new object id.
     * @return the new id
     */
    public static ObjectId get(){
        return new ObjectId();
    }


    /** Checks if a string could be an <code>ObjectId</code>.
     * @return whether the string could be an object id
     */
    public static boolean isValid( String s ){
        if ( s == null )
            return false;

        final int len = s.length();
        if ( len != 24 )
            return false;

        for ( int i=0; i<len; i++ ){
            char c = s.charAt( i );
            if ( c >= '0' && c <= '9' )
                continue;
            if ( c >= 'a' && c <= 'f' )
                continue;
            if ( c >= 'A' && c <= 'F' )
                continue;

            return false;
        }

        return true;
    }

    /** Create a new object id.
     */
    public ObjectId(){
        _time = (int) (System.currentTimeMillis() / 1000);
        _machine = _genmachine;
        _inc = _nextInc.getAndIncrement();
        _new = true;
    }

    public int hashCode(){
        int x = _time;
        x += ( _machine * 111 );
        x += ( _inc * 17 );
        return x;
    }

    public boolean equals( Object o ){

        if ( this == o )
            return true;

        ObjectId other = (ObjectId)o;
        if ( other == null )
            return false;

        return
                _time == other._time &&
                        _machine == other._machine &&
                        _inc == other._inc;
    }

    /**
     * Converts this instance into a 24-byte hexadecimal string representation.
     *
     * @return a string representation of the ObjectId in hexadecimal format
     */
    public String toHexString() {
        final StringBuilder buf = new StringBuilder(24);

        for (final byte b : toByteArray()) {
            buf.append(String.format("%02x", b & 0xff));
        }

        return buf.toString();
    }

   public byte[] toByteArray(){
        byte b[] = new byte[12];
        ByteBuffer bb = ByteBuffer.wrap( b );
        // by default BB is big endian like we need
        bb.putInt( _time );
        bb.putInt( _machine );
        bb.putInt( _inc );
        return b;
    }

    public String toString(){
        return toHexString();
    }

    int _compareUnsigned( int i , int j ){
        long li = 0xFFFFFFFFL;
        li = i & li;
        long lj = 0xFFFFFFFFL;
        lj = j & lj;
        long diff = li - lj;
        if (diff < Integer.MIN_VALUE)
            return Integer.MIN_VALUE;
        if (diff > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        return (int) diff;
    }

    public int compareTo( ObjectId id ){
        if ( id == null )
            return -1;

        int x = _compareUnsigned( _time , id._time );
        if ( x != 0 )
            return x;

        x = _compareUnsigned( _machine , id._machine );
        if ( x != 0 )
            return x;

        return _compareUnsigned( _inc , id._inc );
    }

    /**
     * Gets the timestamp (number of seconds since the Unix epoch).
     *
     * @return the timestamp
     */
    public int getTimestamp() {
        return _time;
    }

    /**
     * Gets the timestamp as a {@code Date} instance.
     *
     * @return the Date
     */
    public Date getDate() {
        return new Date(_time * 1000L);
    }

    /**
     * Gets the time of this ID, in milliseconds
     *
     * @deprecated Please use {@link #getDate()} ()} instead.
     */
    @Deprecated
    public long getTime(){
        return _time * 1000L;
    }


    /**
     * Gets the counter.
     *
     * @return the counter
     * @deprecated Please use the {@link #toByteArray()} instead.
     */
    @Deprecated
    public int getInc() {
        return _inc;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     * @deprecated Please use {@link #getTimestamp()} ()} instead.
     */
    @Deprecated
    public int _time(){
        return _time;
    }

    /**
     * Gets the machine identifier.
     *
     * @return the machine identifier
     * @deprecated Please use {@code #toByteArray()} instead.
     */
    @Deprecated
    public int getMachine() {
        return _machine;
    }

    /**
     * Gets the machine identifier.
     *
     * @return the machine identifier
     */
    public static int getGenMachineId() {
        return _genmachine;
    }

    /**
     * Gets the current value of the auto-incrementing counter.
     */
    public static int getCurrentCounter() {
        return _nextInc.get();
    }


    final int _time;
    final int _machine;
    final int _inc;

    boolean _new;

    private static AtomicInteger _nextInc = new AtomicInteger( (new java.util.Random()).nextInt() );

    private static final int _genmachine;
    static {

        try {
            // build a 2-byte machine piece based on NICs info
            int machinePiece;
            {
                try {
                    StringBuilder sb = new StringBuilder();
                    Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
                    while ( e.hasMoreElements() ){
                        NetworkInterface ni = e.nextElement();
                        sb.append( ni.toString() );
                    }
                    machinePiece = sb.toString().hashCode() << 16;
                } catch (Throwable e) {
                    // exception sometimes happens with IBM JVM, use random
                    LOGGER.log(Level.WARNING, e.getMessage(), e);
                    machinePiece = (new Random().nextInt()) << 16;
                }
                LOGGER.fine( "machine piece post: " + Integer.toHexString( machinePiece ) );
            }

            // add a 2 byte process piece. It must represent not only the JVM but the class loader.
            // Since static var belong to class loader there could be collisions otherwise
            final int processPiece;
            {
                int processId = new java.util.Random().nextInt();
                try {
                    processId = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode();
                }
                catch ( Throwable t ){
                }

                ClassLoader loader = ObjectId.class.getClassLoader();
                int loaderId = loader != null ? System.identityHashCode(loader) : 0;

                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toHexString(processId));
                sb.append(Integer.toHexString(loaderId));
                processPiece = sb.toString().hashCode() & 0xFFFF;
                LOGGER.fine( "process piece: " + Integer.toHexString( processPiece ) );
            }

            _genmachine = machinePiece | processPiece;
            LOGGER.fine( "machine : " + Integer.toHexString( _genmachine ) );
        }
        catch ( Exception e ){
            throw new RuntimeException( e );
        }

    }
}
