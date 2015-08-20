package com.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by Duan on 5月20日.
 */
public class BaseConverter implements Converter {
    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        String response = "";
        try {
            while ((i = body.in().read()) != -1) {
                baos.write(i);
            }
            response = baos.toString();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public TypedOutput toBody(final Object object) {
        return new TypedOutput() {
            @Override
            public String fileName() {
                return null;
            }

            @Override
            public String mimeType() {
                return "String";
            }

            @Override
            public long length() {
                return object == null ? 0 : object.toString().length();
            }

            @Override
            public void writeTo(OutputStream out) throws IOException {
                out.write(object.toString().getBytes());
            }
        };
    }
}
