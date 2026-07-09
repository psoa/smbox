package br.com.psoa.smbox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component("localizedDateFormatter")
public class LocalizedDateFormatter {

    public String formatIsoDate(String isoDate, Locale locale) {
        if (isoDate == null || isoDate.isBlank()) {
            return "";
        }

        Locale effectiveLocale = locale != null ? locale : Locale.ENGLISH;
        LocalDate parsedDate = LocalDate.parse(isoDate);
        return parsedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(effectiveLocale));
    }
}

