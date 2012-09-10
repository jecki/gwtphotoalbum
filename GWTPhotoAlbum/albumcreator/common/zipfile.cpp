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


#include "zipfile.h"

#include <QFile>
#include <QFileInfo>
#include <QDataStream>
#include <QDateTime>
#include <QDate>
#include <QTime>



ZipFile::ZipFile(const QString &zipFileName) : zipFile(zipFileName)
{
	number_of_files = 0;
}



ZipFile::~ZipFile()
{
	if (zipFile.isOpen()) {
		close();
	}
}



void ZipFile::open()
{
	zipFile.open(QIODevice::WriteOnly);
	dir_buffer.setBuffer(&central_directory);
	dir_buffer.open(QIODevice::WriteOnly);

	Q_ASSERT_X(zipFile.isOpen(), "ZipFile::ZipFile", "could not open file!");
}



void ZipFile::addData(const QString &fileName, const QByteArray &data,
					  const QDateTime &date_time)
{
	Q_ASSERT_X(zipFile.isOpen(), "ZipFile::addData", "file not open!");

	const char *raw_data(data.constData());
	int        size = data.size();

	crc32.reset();
	crc32.process_bytes(raw_data, size);
	writeHeader(fileName, date_time, crc32.checksum(), size);
	zipFile.write(raw_data, size);
}



void ZipFile::close()
{
	Q_ASSERT_X(zipFile.isOpen(), "ZipFile::close", "file not open!");

	writeCentralDirectory();
	dir_buffer.close();
	zipFile.write(central_directory);
	zipFile.close();
}



void ZipFile::writeHeader(const QString & fileName, const QDateTime &date_time,
		                  const quint32 crc32, const quint32 size)
{
	QByteArray  fileNameUTF8(fileName.toUtf8());
	QDataStream header(&zipFile);
	QDataStream directory(&dir_buffer);
	header.setByteOrder(QDataStream::LittleEndian);
	directory.setByteOrder(QDataStream::LittleEndian);

	QDate		date(date_time.date());
	QTime		time(date_time.time());

	quint16		dos_time = time.hour() << 11 | time.minute() << 5 | time.second() / 2;
	quint16		dos_date = (date.year() - 1980) << 9 | date.month() << 5 | date.day();
	quint32     offset = zipFile.pos();

	header << (quint32) 0x04034b50; // local file header signature
	header << (quint16) 10;         // version needed to extract (probably 1 suffices !?)
	header << (quint16) (1<<11);    // general purpose bit flag: UTF-8 flag (bit nr. 11) set
	header << (quint16) 0;          // compression method
	header << dos_time;             // last mod file time
	header << dos_date;             // last mod file date
	header << crc32;                // checksum
	header << size;					// compressed size (same as uncompressed size, because no compression used for jpegs
	header << size;					// uncompressed size
	header << (quint16) fileNameUTF8.length();
	header << (quint16) 0;          // extra field length
	header.writeRawData(fileNameUTF8.data(), fileNameUTF8.length());

	directory << (quint32) 0x02014b50; // central file header signature
	directory << (quint16) 10;		   // version made by
	directory << (quint16) 10;		   // version needed to extract
	directory << (quint16) (1<<11);    // general purpose bit flag: UTF-8 flag (bit nr. 11) set
	directory << (quint16) 0;          // compression method
	directory << dos_time;             // last mod file time
	directory << dos_date;             // last mod file date
	directory << crc32;                // checksum
	directory << size;					// compressed size (same as uncompressed size, because no compression used for jpegs
	directory << size;					// uncompressed size
	directory << (quint16) fileNameUTF8.length();
	directory << (quint16) 0;          // extra field length
	directory << (quint16) 0;          // file comment length
	directory << (quint16) 0;          // disk number start
	directory << (quint16) 0;          // internal file attributes
	directory << (quint32) 0;          // eternal file attributes
	directory << offset;               // relative offset of local header
	directory.writeRawData(fileNameUTF8.data(), fileNameUTF8.length());

	Q_ASSERT_X(number_of_files < 32767, "ZipFile::writeHeader", "too many files!");
	number_of_files += 1;
}



void ZipFile::writeCentralDirectory()
{
	quint32     offset = zipFile.pos();
	quint32		dir_size = dir_buffer.pos();
	QDataStream directory(&dir_buffer);
	directory.setByteOrder(QDataStream::LittleEndian);

	directory << (quint32) 0x06054b50; // end of central directory signature
	directory << (quint16) 0;          // number of this disk
	directory << (quint16) 0;          // number of disk with central directory
	directory << number_of_files;      // number of entries on this disk
	directory << number_of_files;      // total number of entries
	directory << dir_size;             // size of central directory
	directory << offset;			   // offset of central directory
	directory << (quint16) 0;          // comment length
}
