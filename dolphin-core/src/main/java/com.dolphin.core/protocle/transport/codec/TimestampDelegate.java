package com.dolphin.core.protocle.transport.codec;

import java.io.IOException;
import java.sql.Timestamp;

import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.Pipe;
import io.protostuff.WireFormat.FieldType;
import io.protostuff.runtime.Delegate;

/**
 * protostuff timestamp 委托类
 * @author jiujie
 * @version $Id: TimestampDelegate.java, v 0.1 2016年7月20日 下午2:08:11 jiujie Exp $
 */
public class TimestampDelegate implements Delegate<Timestamp> {
    
    public FieldType getFieldType() {
        return FieldType.FIXED64;
    }

    public Class<?> typeClass() {
        return Timestamp.class;
    }

    public Timestamp readFrom(Input input) throws IOException {
        return new Timestamp(input.readFixed64());
    }

    public void writeTo(Output output, int number, Timestamp value,
                        boolean repeated) throws IOException {
        output.writeFixed64(number, value.getTime(), repeated);
    }

    public void transfer(Pipe pipe, Input input, Output output, int number,
                         boolean repeated) throws IOException {
        output.writeFixed64(number, input.readFixed64(), repeated);
    }

}
