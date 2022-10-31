package ee.mcdimus.matewp.serialization

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.format.DateTimeFormatter

object BingLocalDateTimeSerializer : KSerializer<LocalDateTime> {

  private val formatter = DateTimeFormatter.ofPattern("uuuuMMddHHmm")

  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder) = java.time.LocalDateTime.parse(decoder.decodeString(), formatter).toKotlinLocalDateTime()

  override fun serialize(encoder: Encoder, value: LocalDateTime) = encoder.encodeString(formatter.format(value.toJavaLocalDateTime()))

}