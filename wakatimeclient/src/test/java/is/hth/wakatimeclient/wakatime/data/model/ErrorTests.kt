package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor
import `is`.hth.wakatimeclient.core.data.net.WakatimeJsonFactory
import `is`.hth.wakatimeclient.wakatime.data.api.FieldError
import `is`.hth.wakatimeclient.wakatime.data.api.ServiceError
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeErrorProcessor
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.json.*

public class ErrorTests : DescribeSpec({

    val json = WakatimeJsonFactory.makeJson()

    describe("deserialization") {
        describe("of ServiceError") {
            describe("as received from server") {

                it("with only error message") {
                    json.decodeFromJsonElement<ServiceError>(buildJsonObject {
                        put("error", "Start date can't come after end date.")
                    }) shouldBe ServiceError(
                        message = "Start date can't come after end date."
                    )
                }

                it("with only one field error") {
                    json.decodeFromJsonElement<ServiceError>(buildJsonObject {
                        putJsonObject("errors") {
                            putJsonArray("entity") {
                                add(JsonPrimitive("This field is required."))
                            }
                        }
                    }) shouldBe ServiceError(
                        message = "",
                        fieldErrors = listOf(
                            FieldError(
                                name = "entity",
                                description = "This field is required."
                            )
                        )
                    )
                }

                it("with only two field errors") {
                    json.decodeFromJsonElement<ServiceError>(buildJsonObject {
                        putJsonObject("errors") {
                            putJsonArray("entity") {
                                add(JsonPrimitive("This field is required."))
                            }
                            putJsonArray("time") {
                                add(JsonPrimitive("Number must be between 1388534400 and 99999999999."))
                            }
                        }
                    }) shouldBe ServiceError(
                        message = "",
                        fieldErrors = listOf(
                            FieldError(
                                name = "entity",
                                description = "This field is required."
                            ),
                            FieldError(
                                name = "time",
                                description = "Number must be between 1388534400 and 99999999999."
                            )
                        )
                    )
                }

                it("with error message and two field errors") {
                    json.decodeFromJsonElement<ServiceError>(buildJsonObject {
                        put("error", "a error message")
                        putJsonObject("errors") {
                            putJsonArray("entity") {
                                add(JsonPrimitive("This field is required."))
                            }
                            putJsonArray("time") {
                                add(JsonPrimitive("Number must be between 1388534400 and 99999999999."))
                            }
                        }
                    }) shouldBe ServiceError(
                        message = "a error message",
                        fieldErrors = listOf(
                            FieldError(
                                name = "entity",
                                description = "This field is required."
                            ),
                            FieldError(
                                name = "time",
                                description = "Number must be between 1388534400 and 99999999999."
                            )
                        )
                    )
                }
            }
        }
    }

    describe("of NetworkErrorProcessor") {
        val processor = NetworkErrorProcessor()

        describe("status code conversion") {

            describe("of standard network codes") {
                it("of 'Http Bad Request' (400)") {
                    processor.onError(400, "")
                        .shouldBeInstanceOf<Error.Network.BadRequest>()
                }
                it("of 'Http Unauthorized' (401)") {
                    processor.onError(401, "")
                        .shouldBeInstanceOf<Error.Network.Unauthorized>()
                }
                it("of 'Http Forbidden' (403)") {
                    processor.onError(403, "")
                        .shouldBeInstanceOf<Error.Network.Forbidden>()
                }
                it("of 'Http Not Found' (404)") {
                    processor.onError(404, "")
                        .shouldBeInstanceOf<Error.Network.NotFound>()
                }
                it("of 'Http Timeout' (408)") {
                    processor.onError(408, "")
                        .shouldBeInstanceOf<Error.Network.Timeout>()
                }
                it("of 'Http TooManyRequests' (429)") {
                    processor.onError(429, "")
                        .shouldBeInstanceOf<Error.Network.TooManyRequests>()
                }
                it("of 'Http Unavailable' (500)") {
                    processor.onError(500, "")
                        .shouldBeInstanceOf<Error.Network.InternalServer>()
                }
                it("of 'Http Unavailable' (503)") {
                    processor.onError(503, "")
                        .shouldBeInstanceOf<Error.Network.Unavailable>()
                }
            }

            describe("of internal network codes") {
                it("of 'No Network Access' (1001)") {
                    processor.onError(Error.Network.Internal.NoNetwork.CODE, "")
                        .shouldBeInstanceOf<Error.Network.Internal.NoNetwork>()
                }
                it("of 'Unknown Host' (1002)") {
                    processor.onError(Error.Network.Internal.UnknownHost.CODE, "")
                        .shouldBeInstanceOf<Error.Network.Internal.UnknownHost>()
                }
                it("of 'Serialization' (1003)") {
                    processor.onError(Error.Network.Internal.Serialization.CODE, "")
                        .shouldBeInstanceOf<Error.Network.Internal.Serialization>()
                }
                it("of 'SocketTimeout' (1004)") {
                    processor.onError(Error.Network.Internal.SocketTimeout.CODE, "")
                        .shouldBeInstanceOf<Error.Network.Internal.SocketTimeout>()
                }
                it("of 'Protocol' (1005)") {
                    processor.onError(Error.Network.Internal.Protocol.CODE, "")
                        .shouldBeInstanceOf<Error.Network.Internal.Protocol>()
                }
                it("of 'Unknown' (-1)") {
                    processor.onError(-1, "").shouldBeInstanceOf<Error.Network.Unknown>()
                }
            }
        }
    }

    describe("of WakatimeProcessor") {
        val processor = WakatimeErrorProcessor(json)

        describe("conversion of ServiceError to Error") {

            it("with error message and full error body") {
                val processed = processor.onNetworkError(
                    code = Error.Network.Unauthorized.CODE,
                    error = buildJsonObject {
                        put("error", "a error message")
                        putJsonObject("errors") {
                            putJsonArray("entity") {
                                add(JsonPrimitive("This field is required."))
                            }
                            putJsonArray("time") {
                                add(JsonPrimitive("Number must be between 1388534400 and 99999999999."))
                            }
                        }
                    }.toString()
                )

                processed.shouldBeInstanceOf<Error.Network.Unauthorized>()
                processed.message shouldBe "a error message"
                processed.extra.size shouldBe 2
            }

            it("with non json error message") {
                val processed = processor.onNetworkError(
                    code = Error.Network.Unauthorized.CODE,
                    error = "a error message"
                )

                processed.shouldBeInstanceOf<Error.Network.Unauthorized>()
                processed.message shouldBe "a error message"
                processed.extra.size shouldBe 0
            }
        }

    }
})