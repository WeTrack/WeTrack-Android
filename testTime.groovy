@Grab(group='joda-time', module='joda-time', version='2.9.6')
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat

DateTimeFormatter RFC1123_DATE_TIME_FORMATTER =
        DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'")
                .withZoneUTC().withLocale(Locale.US);

println DateTime.now().toString(RFC1123_DATE_TIME_FORMATTER)