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

#ifndef HTMLPROCESSING_H
#define HTMLPROCESSING_H

#include <QObject>
#include <QList>
#include <QSize>
#include <QString>
#include <QMap>
#include <QTextStream>

/*!
 * \class HTMLProcessing contains functions for assembling and
 * writing the HTML pages of the album.
 */
class HTMLProcessing : public QObject {
	Q_OBJECT

public:
	enum { NOSCRIPT_PAGES = 1, GALLERY = 2, LAST_IMAGE = 4 }; // , WITH_IMAGE_LINK = 8
	enum { DOCTYPE_UNKNOWN = 0, DOCTYPE_HTML5 = 1, DOCTYPE_XHTML11 = 2 };

	static const QString startPageName, fatStartPageName,
	                     noscriptFirstPageName, noscriptImagePageName;
	static const QString templatenames[];

	QMap<QString, QString> htmlTemplates;
	QString				   doctype_string;

	HTMLProcessing(QObject *parent = NULL);
	HTMLProcessing(QTextStream &chunks_json, QObject *parent = NULL);

	bool readTemplates(QTextStream &chunks_json);
	void setDoctype(int doctype);
	int  doctype();

	QString indexPage(int flags = NOSCRIPT_PAGES|GALLERY);
	QString offlineIndex(int flags = NOSCRIPT_PAGES|GALLERY);

	QString fatStartPage(QString &startPage, const QString info_json,
			const QString directories_json, const QString filenames_json,
			const QString captions_json, const QString resolutions_json);

	QString noscriptGallery(const QString title, const QString subtitle,
			                const QString bottomline,
			                const QList<QString> thumbnailFileNames,
			                const QSize thumbnailSize);
	QString noscriptImagePage(int imageNr, const QString sizeDir,
			                  const QString fileName, const QString caption,
			                  int flags = 0);
};

#endif
