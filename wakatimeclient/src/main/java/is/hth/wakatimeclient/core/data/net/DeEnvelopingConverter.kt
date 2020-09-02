package `is`.hth.wakatimeclient.core.data.net

import androidx.annotation.Nullable
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * A {@link retrofit2.Converter.Factory} which removes unwanted wrapping envelopes from API
 * responses.
 *
 * Based on Google's DeEnvelopingConverter.java from the Plaid sample application
 */
class DeEnvelopingConverter(private val gson: Gson) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        // This converter requires an annotation providing the name of the payload in the envelope;
        // if one is not supplied then return null to continue down the converter chain.
        val payloadName = getPayloadName(annotations) ?: return null
        val adapter: TypeAdapter<*> = gson.getAdapter(TypeToken.get(type))
        return Converter<ResponseBody, Any?> { body: ResponseBody ->
            body.use {
                gson.newJsonReader(it.charStream()).use { jsonReader ->
                    jsonReader.beginObject()
                    while (jsonReader.hasNext()) {
                        if (payloadName == jsonReader.nextName()) {
                            return@Converter adapter.read(jsonReader)
                        } else {
                            jsonReader.skipValue()
                        }
                    }
                    return@Converter null
                }
            }
        }
    }

    @Nullable
    private fun getPayloadName(annotations: Array<Annotation>?): String? {
        if (annotations == null) return null
        for (annotation in annotations) {
            if (annotation is EnvelopePayload) {
                return annotation.value
            }
        }
        return null
    }
}