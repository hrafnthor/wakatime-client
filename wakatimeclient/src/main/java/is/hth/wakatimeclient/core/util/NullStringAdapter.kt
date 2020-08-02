package `is`.hth.wakatimeclient.core.util

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class NullStringAdapter : TypeAdapter<String>() {

    override fun write(writer: JsonWriter?, value: String?) {
        writer?.let {
            if (value == null) {
                it.nullValue()
            } else {
                it.value(value)
            }
        }
    }

    override fun read(reader: JsonReader?): String {
        return reader?.let {
            if (it.peek() != JsonToken.NULL) {
                it.nextString()
            } else {
                it.nextNull()
                ""
            }
        } ?: ""
    }
}