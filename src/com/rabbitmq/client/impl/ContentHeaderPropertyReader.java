//  The contents of this file are subject to the Mozilla Public License
//  Version 1.1 (the "License"); you may not use this file except in
//  compliance with the License. You may obtain a copy of the License
//  at http://www.mozilla.org/MPL/
//
//  Software distributed under the License is distributed on an "AS IS"
//  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
//  the License for the specific language governing rights and
//  limitations under the License.
//
//  The Original Code is RabbitMQ.
//
//  The Initial Developer of the Original Code is VMware, Inc.
//  Copyright (c) 2007-2012 VMware, Inc.  All rights reserved.
//


package com.rabbitmq.client.impl;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import com.rabbitmq.client.ContentHeader;
import com.rabbitmq.client.LongString;

/**
 * Parses an AMQP wire-protocol {@link ContentHeader} from a
 * DataInputStream. Methods on this object are usually called from
 * auto-generated code.
 */
public class ContentHeaderPropertyReader {
    /** Stream we are reading from */
    private final ValueReader in;

    /** Current field flag word */
    public int flagWord;

    /** Current flag position counter */
    public int bitCount;

    /**
     * Protected API - Constructs a reader from the given input stream
     * @param in input stream to read from
     * @throws IOException for input stream errors
     */
    public ContentHeaderPropertyReader(DataInputStream in) throws IOException {
        this.in = new ValueReader(in);
        this.flagWord = 1; // just the continuation bit
        this.bitCount = 15; // forces a flagWord read
    }

    private boolean isContinuationBitSet() {
        return (flagWord & 1) != 0;
    }

    /**
     * Get (next) flag word
     * @throws IOException if no flag word to read!
     */
    public void readFlagWord() throws IOException {
        if (!isContinuationBitSet()) {
            // FIXME: Proper exception class!
            throw new IOException("Attempted to read flag word when none advertised");
        }
        flagWord = in.readShort();
        bitCount = 0;
    }

    /**
     * @return true if next flag is set
     * @throws IOException from input stream
     */
    public boolean readPresence() throws IOException {
        if (bitCount == 15) {
            readFlagWord();
        }

        int bit = 15 - bitCount;
        bitCount++;
        return (flagWord & (1 << bit)) != 0;
    }

    /**
     * Signal end of boolean flags for optional fields
     * @throws IOException if not end
     */
    public void finishPresence() throws IOException {
        if (isContinuationBitSet()) {
            // FIXME: Proper exception class!
            throw new IOException("Unexpected continuation flag word");
        }
    }

    /**
     * Reads and returns an AMQP short string content header field.
     * @return <code>String</code> version of short string
     * @throws IOException from input stream
     */
    public String readShortstr() throws IOException {
        return in.readShortstr();
    }

    /**
     * Reads and returns an AMQP "long string" (binary) content header field.
     * @return <code>LongString</code> read
     * @throws IOException from input stream
     */
    public LongString readLongstr() throws IOException {
        return in.readLongstr();
    }

    /**
     * Reads and returns an AMQP short integer content header field.
     * @return <code>Integer</code> of short read
     * @throws IOException from input stream
     */
    public Integer readShort() throws IOException {
        return in.readShort();
    }

    /** Reads and returns an AMQP integer content header field.
     * @return value read
     * @throws IOException input stream exception on read
     */
    public Integer readLong() throws IOException {
        return in.readLong();
    }

    /** Reads and returns an AMQP long integer content header field.
     * @return value read
     * @throws IOException input stream exception on read
     */
    public Long readLonglong() throws IOException {
        return in.readLonglong();
    }

    /** Reads and returns an AMQP table content header field.
     * @return table read as map
     * @throws IOException input stream exception on read
     */
    public Map<String, Object> readTable() throws IOException {
        return in.readTable();
    }

    /** Reads and returns an AMQP octet content header field.
     * @return value read
     * @throws IOException input stream exception on read
     */
    public int readOctet() throws IOException {
        return in.readOctet();
    }

    /** Reads and returns an AMQP timestamp content header field.
     * @return date value read
     * @throws IOException input stream exception on read
     */
    public Date readTimestamp() throws IOException {
        return in.readTimestamp();
    }
}
