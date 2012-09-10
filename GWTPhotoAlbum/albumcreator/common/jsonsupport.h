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


#ifndef JSONSUPPORT_H_
#define JSONSUPPORT_H_

#include <QList>
#include <QString>
#include <QStringList>
#include <QMap>
#include <QVariant>
#include <QTextStream>

QString parseMultiLineString(const QString text, int &index, int stop, bool &ok);
QString parseString(const QString text, int &index, int stop, bool &ok);
QStringList parseStringList(const QString text, int &index, int stop, bool &ok);
QMap<QString, QString> parseStringMap(const QString text, int &index, int stop, bool &ok);

void writeJSON(QTextStream &stream, const QVariant data, bool &ok);

#endif /* JSONSUPPORT_H_ */
