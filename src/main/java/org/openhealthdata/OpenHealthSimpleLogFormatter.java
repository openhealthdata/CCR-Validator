/*
 * Copyright 2010 OpenHealthData, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openhealthdata;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;

/**
 *
 * @author swaldren
 */
public class OpenHealthSimpleLogFormatter extends java.util.logging.Formatter {

    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final String lineSep = System.getProperty("line.separator");

    public OpenHealthSimpleLogFormatter() {
        super();
    }

    @Override
    public String format(LogRecord record) {
        String loggerName = record.getLoggerName();
		if(loggerName == null) {
			loggerName = "root";
		}
		StringBuilder output = new StringBuilder()
			.append(loggerName)
                        .append("-")
                        .append(record.getSourceMethodName())
			.append("[")
			.append(record.getLevel()).append('|')
			.append(Thread.currentThread().getName()).append('|')
			.append(format.format(new Date(record.getMillis())))
			.append("]: ")
			.append(record.getMessage()).append(' ')
			.append(lineSep);
                if (record.getThrown() != null) {
                    try {
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            record.getThrown().printStackTrace(pw);
                            pw.close();
                            output.append(sw.toString());
                    } catch (Exception ex) {
                    }
        }

		return output.toString();
    }
}
