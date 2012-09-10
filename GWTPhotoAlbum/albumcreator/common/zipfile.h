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

#ifndef ZIPFILE_H_
#define ZIPFILE_H_

#include <QObject>
#include <QFile>
#include <QDateTime>
#include <QByteArray>
#include <QDataStream>
#include <QBuffer>

#include <boost/crc.hpp>


class ZipFile  {
public:
	ZipFile(const QString &zipFileName);
	virtual ~ZipFile();

	void open();
	void addData(const QString &fileName, const QByteArray &data,
			     const QDateTime &date_time = QDateTime::currentDateTime());
	void close();

private:
	QFile  	            zipFile;
	QByteArray          central_directory;
	QBuffer				dir_buffer;
	quint16				number_of_files;
	boost::crc_32_type  crc32;

	void writeHeader(const QString &fileName, const QDateTime &date_time,
			         const quint32 crc32, const quint32 size);
	void writeCentralDirectory();
};

#endif // ZIPFILE_H_
