package com.yile.learning.cassandra.hector;

import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.DoubleSerializer;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;

public interface Serializers {

    public static final StringSerializer se = StringSerializer.get();
    public static final ByteBufferSerializer be = ByteBufferSerializer.get();
    public static final UUIDSerializer ue = UUIDSerializer.get();
    public static final BytesArraySerializer bae = BytesArraySerializer.get();
    public static final DynamicCompositeSerializer dce = DynamicCompositeSerializer.get();
    public static final LongSerializer le = LongSerializer.get();
    public static final DoubleSerializer de = DoubleSerializer.get();
}
