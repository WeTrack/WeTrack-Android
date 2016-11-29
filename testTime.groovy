@Grab(group='joda-time', module='joda-time', version='2.9.6')
import org.joda.time.LocalDateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

import java.time.ZoneId

// LocalDateTime localDateTime = LocalDateTime.now()
// String result = localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime().toString()
String result = '2016-11-29T22:17:00.871+08:00'
println result
DateTimeFormatter offsetDateTimeFormatter = ISODateTimeFormat.dateTime()
println offsetDateTimeFormatter.parseDateTime(result).withZone(DateTimeZone.UTC).toLocalDateTime();
println LocalDateTime.now().toDateTime(DateTimeZone.getDefault()).toString(offsetDateTimeFormatter)