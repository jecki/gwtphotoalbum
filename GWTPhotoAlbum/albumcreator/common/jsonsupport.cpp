/*
 * Copyright 2011 Eckhart Arnold (eckhart_arnold@hotmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


#include "jsonsupport.h"
#include "toolbox.h"

#include <QChar>
#include <QString>
#include <QList>
#include <QStringList>
#include <QMap>
#include <QVariant>
#include <QTextStream>
#include <QSize>


/*!
 * Skips part of a JSON text until a stop character is reached.
 * Sets the ok flag to false if no stop character is reached within the
 * text chunk.
 * @param[in]  stoppers a string of stop characters.
 * @param[in]  text   ascii text containing the JSON chunk to be parsed
 * @param[out] index  index for parsing (which is being advanced by parseSkipGap)
 * @param[in]  stop   the last index +1 for parsing
 * @param[out] ok     set to false if a parsing error occurred, otherwise true
 * @return  returns the ok flag (just for convenience, the flag is of course set already)
 */

inline bool parseSkip(QString stoppers, const QString text, int &index, int stop, bool &ok) {
	if (ok) {
		while (index < stop && stoppers.indexOf(text[index]) == -1) {
			if (text[index] == '\\') index++;
			index++;
		}
		if (index >= stop) ok = false;
	}
	return (ok);
}


/*!
 * Parses a multiline string (enclosed by """).
 * @param[in]  text   ascii text containing the JSON chunk to be parsed
 * @param[out] index  index for parsing
 * @param[in]  stop   the last index +1 for parsing
 * @param[out] ok     set to false if a parsing error occurred, otherwise true
 * @return  the parsed string
 */
QString parseMultiLineString(const QString text, int &index, int stop, bool &ok) {
	if (text.mid(index, 3) == "\"\"\"") {
		index += 3;
		int start = index;
		while (index < stop && text.mid(index, 3) != "\"\"\"") {
			index++;
		}
		if (index >= stop) {
			ok = false;
		} else {
			int n = index-start;
			index += 3;
			return (text.mid(start, n));
		}
	}
	return (QString(""));
}


/*!
 * Parses a JSON string. A string is a sequences of characters, delimited by
 * either "..." or '...'.
 * @param[in]  text   ascii text containing the JSON chunk to be parsed
 * @param[out] index  index for parsing
 * @param[in]  stop   the last index +1 for parsing
 * @param[out] ok     set to false if a parsing error occurred, otherwise true
 * @return  the parsed string
 */
QString parseString(const QString text, int &index, int stop, bool &ok) {
	if (parseSkip("\"'", text, index, stop, ok)) {
		if (text.mid(index, 3) == "\"\"\"") {
			return (parseMultiLineString(text, index, stop, ok));
		}
		QChar delimiter = text[index];
		index++;
		int start = index;
		if (parseSkip(QString(delimiter), text, index, stop, ok)) {
			index++;
			QString ret = text.mid(start, index - start - 1);

			// remove one level of escape characters
			QString::iterator s, d = ret.begin(), end = ret.end();
			int t = 0;
			for (s = ret.begin(); s != end; s++) {
				if (*s != '\\') {
					*d = *s;
					d++;
				} else if (s != end) {
					++s;
					*d = *s;
					d++;
					t++;
				}
			}
			ret.truncate(ret.length() - t);
			return (ret);

		}
	}
	return (QString(""));
}


/*!
 * Parses a JSON list of strings
 * @param[in]  text   ascii text containing the JSON chunk to be parsed
 * @param[out] index  index for parsing
 * @param[in]  stop   the last index +1 for parsing
 * @param[out] ok     set to false if a parsing error occurred, otherwise true
 * @return  the string list
 */
QStringList parseStringList(const QString text, int &index, int stop, bool &ok) {
	QStringList ret;

	if (parseSkip("[", text, index, stop, ok)) {
		index++;
		while (parseSkip(",]\"'", text, index, stop, ok) && text[index] != ']') {
			if (text[index] != ',') {
				QString str = parseString(text, index, stop, ok);
				if (ok) ret.append(str);
			} else index++;
		}
		index++;
	}
	return (ret);
}


/*!
 * Parses a JSON map of strings mapped to strings.
 * @param[in]  text   ascii text containing the JSON chunk to be parsed
 * @param[out] index  index for parsing
 * @param[in]  stop   the last index +1 for parsing
 * @param[out] ok     set to false if a parsing error occured, otherwise true
 * @return a string to string map containing the contents of the JSON map
 */
QMap<QString, QString> parseStringMap(const QString text, int &index, int stop, bool &ok) {
	QMap<QString, QString> ret;

	if (parseSkip("{", text, index, stop, ok)) {
		index++;
		while (parseSkip(",}\"'", text, index, stop, ok) && text[index] != '}') {
			if (text[index] != ',') {
				QString key = parseString(text, index, stop, ok);
				if (ok && parseSkip(":", text, index, stop, ok)) {
					index++;
					if (parseSkip("\"'", text, index, stop, ok)) {
						QString value = parseString(text, index, stop, ok);
						if (ok) ret[key] = value;
					}
				}
			} else index++;
		}
		index++;
	}
	return (ret);
}


/*!
 * Writes data in JSON format to a text stream.
 * @param[in]  stream  the stream where the data is written to
 * @param[in]  data    the data to be stored in JSON format
 * @param[out] ok      set to false if a parsing error occurred, otherwise true
 */
void writeJSON(QTextStream &stream, const QVariant data, bool &ok) {
	if (!ok) return;

	QVariant::Type type = data.type();

	if (type == QVariant::String) {
		QString str(data.toString().replace('"', "\\\""));
		stream << "\"" << str << "\"";

	} else if (type == QVariant::StringList) {
		QStringList strList(data.toStringList());
		stream << "[\n";
		for(int i = 0; i < strList.length()-1 ; i++) {
			stream << "\"" << strList[i].replace('"', "\\\"") << "\",\n";
		}
		if (strList.length() > 0) {
			stream << "\"" << strList.last().replace('"', "\\\"") << "\"\n";
		}
		stream << "]\n";

    } else if (type == QVariant::List) {
		QVariantList list = data.toList();
		stream << "[";
		for(int i = 0; i < list.length()-1 ; i++) {
			writeJSON(stream, list[i], ok);
			if (list[i].type() == QVariant::Map) {
				stream << ",\n";
			} else {
				stream << ", ";
			}
		}
		if (list.length() > 0) {
			writeJSON(stream, list.last(), ok);
		}
		stream << "]";

    } else if (type == QVariant::Size) {
    	QSize size(data.toSize());
		stream << "[" << size.width() << ", " << size.height() << "]";

	} else if (type == QVariant::Int) {
		stream << data.toInt();

	} else if (type == QVariant::Map) {
		QVariantMap map = data.toMap();
		QList<QString> keys = map.keys();
		int N = keys.length();
		stream << "{ ";
		for(int i = 0; i < N ; i++) {
			stream << "\"" << keys[i].replace('"', "\\\"") << "\": ";
			writeJSON(stream, map[keys[i]], ok);
			if (i < N-1) {
				stream << ",\n";
			}
		}
		stream << " }";

	}else {
		ok = false;
	}

	stream.flush();
}

