package eu.maksimov.matewp.core.serialization

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.format.DateTimeFormatter

object BingLocalDateSerializer : KSerializer<LocalDate> {

  private val formatter = DateTimeFormatter.ofPattern("uuuuMMdd")

  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder) = java.time.LocalDate.parse(decoder.decodeString(), formatter).toKotlinLocalDate()

  override fun serialize(encoder: Encoder, value: LocalDate) = encoder.encodeString(formatter.format(value.toJavaLocalDate()))

}
