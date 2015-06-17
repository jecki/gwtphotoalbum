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

#include "htmlprocessing.h"
#include "jsonsupport.h"

#include <QFile>
#include <QIODevice>

#ifndef NDEBUG
#include <QFileInfo>
#include "toolbox.h"
#endif


const QString HTMLProcessing::startPageName = "GWTPhotoAlbum.html";
const QString HTMLProcessing::fatStartPageName = "GWTPhotoAlbum_fat.html";
const QString HTMLProcessing::noscriptFirstPageName = "noscript_gallery.html"; // exchange gallery through image0 in case there is no gallery
const QString HTMLProcessing::noscriptImagePageName = "noscript_image$NUMBER.html";

const QString HTMLProcessing::templatenames[] = {"doctypeHTML5",
	"doctypeXHTML11", "doctypeWAP", "checkJSPageHTML", "redirectPageHTML",
	"galleryHTML", "galleryCellHTML", "galleryEmptyCellHTML", "imagePageHTML",
	"panelHTML", "galleryButtonHTML", "imageHTML"}; // "imageWithLinkHTML",



/*!
 * Constructor for HTMLProcessing, incidently tries to read the html templates from
 * ":/:/htmlchunks.json".
 * @param parent  the parent QObject
 */
HTMLProcessing::HTMLProcessing(QObject *parent) : QObject(parent)
{
//#ifndef NDEBUG
//		QFileInfoList tree = listTree(":/");
//		foreach(QFileInfo item, tree) {
//			std::cout << item.absoluteFilePath().toStdString() << std::endl;
//		}
//#endif
	QFile file(":/data/htmlchunks.json");
	if (file.open(QIODevice::ReadOnly|QIODevice::Text)) {
		QTextStream stream(&file);
		if (!readTemplates(stream)) {
			file.close();
			throw QString("HTMLProcessing:: resource corrupted: htmlchunks.json !");
		}
		file.close();
	} else {
		throw QString("HTMLProcessing:: missing resource: ':/htmlchunks.json' !");
	}
	doctype_string = htmlTemplates["doctypeHTML5"];
}


HTMLProcessing::HTMLProcessing(QTextStream &chunks_json, QObject *parent) : QObject(parent)
{
	if (!readTemplates(chunks_json)) {
		throw QString("HTMLProcessing:: error in resource!");
	}
	doctype_string = htmlTemplates["doctypeHTML5"];
}


/*!
 * Reads the html-templates from a text-stream
 * @param chunks_json  the text stream that contains the html templates
 *                     as a JSON dictionary. This functions adds or replaces
 *                     new templates to existing templates.
 * @return true, if all templates could be read without error.
 */
bool HTMLProcessing::readTemplates(QTextStream &chunks_json)
{
	// below: worst scanner / parser ever

	bool    ok = true;
	int     index = 0;
	QString text = chunks_json.readAll();
	QMap<QString, QString> map = parseStringMap(text, index, text.size()-1, ok);
	if (ok) {
		foreach(const QString &key, map.keys()) {
			htmlTemplates[key] = map[key];
		}
		int N = sizeof(templatenames) / sizeof(templatenames[0]);
		for (int i = 0; i < N; i++) {
			if (!htmlTemplates.contains(templatenames[i])) return (false);
		}
	}
	return (ok);
}


/*!
 * Sets the doctype for the html pages. (Default is html 5.)
 * @param doctype the doctype, either DOCTYPE_HTML5 or DOCTYPE_XHTML11
 */
void HTMLProcessing::setDoctype(int doctype)
{
	Q_ASSERT_X(doctype == DOCTYPE_HTML5 || doctype == DOCTYPE_XHTML11,
			   "HTMLProcessing::setDocType", "illegal doctype");
	switch (doctype) {
	case DOCTYPE_HTML5:
		doctype_string = htmlTemplates["doctypeHTML5"];
		break;
	case DOCTYPE_XHTML11:
		doctype_string = htmlTemplates["doctypeXHTML11"];
		break;
	}
}


/*!
 * Returns the selected doctype.
 * @return doctype, either DOCTYPE_HTML5 or DOCTYPE_XHTML11, or zero if
 *         doctype_string represents and unknown doctype.
 */
int  HTMLProcessing::doctype()
{
	if (doctype_string == htmlTemplates["doctypeHTML5"]) {
		return (DOCTYPE_HTML5);
	} else if (doctype_string == htmlTemplates["doctypeXHTML11"]) {
		return (DOCTYPE_XHTML11);
	} else {
		return (DOCTYPE_UNKNOWN);
	}
}


/*!
 * Generates an index page for the photo-album
 * @param flags  any combination of NOSCRIPT_PAGES and GALLERY
 * @return a string containing the index page.
 */
QString HTMLProcessing::indexPage(int flags)
{
	QString idxPage;
	if (flags & NOSCRIPT_PAGES) {
		idxPage = htmlTemplates["redirectPageHTML"];
		if (flags & GALLERY) {
			idxPage.replace("$ENTRY", "gallery");
		} else {
			idxPage.replace("$ENTRY", "image0");
		}
	} else {
		idxPage = htmlTemplates["checkJSPageHTML"];
	}
	idxPage.replace("$ALBUM", startPageName)
	       .replace("$DOCTYPE", doctype_string);
	return (idxPage);
}


/*!
 * Generates an index page for offline browsing.
 * @param flags  any combination of NOSCRIPT_PAGES and GALLERY
 * @return a string containing the offline index page
 */
QString HTMLProcessing::offlineIndex(int flags)
{
	QString idxPage = indexPage(flags);
	idxPage.replace(startPageName, fatStartPageName);
	return (idxPage);
}


/*!
 * Creates a JSON block enclosed by <script>-tags.
 * @param name  the name of the block, which becomes the id of the script tag
 * @param block the block itself
 * @return
 */
QString createJSONBlock(QString name, QString block) {
	QString ret = QString("<script id=\"") + name +
			      QString("\" style=\"display:none;\" type=\"application/json\">\n");
	ret += block;
	ret += QString("</script>\n");
	return (ret);
}


/*!
 * Generates a start page for offline browsing. This page will include
 * the json code for the photo album enclosed in <script>-tags (thereby
 * breaking strict html rules.)
 * @param ***_json  the JSON files describing the album
 * @return
 */
QString HTMLProcessing::fatStartPage(QString &startPage, const QString info_json,
		const QString directories_json, const QString filenames_json,
		const QString captions_json, const QString resolutions_json)
{
	QTextStream input(&startPage);
	QString		result;
	QTextStream output(&result);
	bool		oldJSONBlock = false;

	while (!input.atEnd()) {
		QString line = input.readLine();
		if (oldJSONBlock) {
			if (line.indexOf("</script>") >= 0) {
				oldJSONBlock = false;
			}
		} else if (line.indexOf("<script") >= 0 &&
				   line.indexOf("display:none") >= 0) {
			oldJSONBlock = true;
		} else if (line.indexOf("src=\"GWTPhotoAlbum/GWTPhotoAlbum.nocache.js\"") >= 0) {
			output << line.replace("GWTPhotoAlbum/GWTPhotoAlbum.nocache.js",
					"GWTPhotoAlbum/GWTPhotoAlbum.nocache.js") << "\n";
		} else if (line.indexOf("<meta name=\"info\"") >= 0) {
			// pass
		} else if (line.indexOf("</head>") >= 0) {
			output << createJSONBlock("info.json", info_json);
			output << createJSONBlock("directories.json", directories_json);
			output << createJSONBlock("filenames.json", filenames_json);
			output << createJSONBlock("resolutions.json", resolutions_json);
			output << createJSONBlock("captions.json", captions_json);
			output << "<meta name=\"info\" content=\"info.json\">\n";
			output << line << "\n";
		} else {
			output << line << "\n";
		}
	}
	output.flush();
	return (result);
}


/*!
 * Creates a html gallery that does not contain javascript
 * @param title		 the gallery's title
 * @param subtitle	 the gallery's subtitle
 * @param bottomline  the bottomline of the gallery
 * @param thumbnailFileNames  a list of thumbnail file names (not path names!)
 * @return a html page containing the gallery
 */
QString HTMLProcessing::noscriptGallery(const QString title, const QString subtitle,
		                				const QString bottomline,
		                				const QList<QString> thumbnailFileNames,
		                				const QSize thumbnailSize)
{
	QList<QString> cells;

	int N = thumbnailFileNames.size();
	QString thumbPath = "slides/"+QString::number(thumbnailSize.width())+"x"
	 		                     +QString::number(thumbnailSize.width())+"/";
	for (int i = 0; i < N; i++) {
		QString imgPath(thumbPath);
		imgPath += thumbnailFileNames[i];
		QString link("noscript_image%i.html");
		link.replace("%i", QString::number(i+1));
		QString cell(htmlTemplates["galleryCellHTML"]);
		cell.replace("$IMG", imgPath).replace("$LINK", link);
		cells.append(cell);
	}

	QList<QString> rows;
	while (cells.size() > 0) {
		rows.append("<tr>");
		for (int i = 0; i < 4; i++) {
			if (cells.size() > 0) {
				rows.append(cells.takeFirst());
			} else {
				rows.append(htmlTemplates["galleryEmptyCellHTML"]);
			}
		}
		rows.append("</tr>");
	}

	QString galleryrows;
	foreach(const QString &row, rows) {
		galleryrows += row;
		galleryrows += "\n";
	}

	QString gallery(htmlTemplates["galleryHTML"]);
	gallery.replace("$TITLE", title)
		   .replace("$SUBTITLE", subtitle)
		   .replace("$BOTTOMLINE", bottomline)
		   .replace("$GALLERYROWS", galleryrows)
	       .replace("$DOCTYPE", doctype_string);

	return (gallery);
}


/*!
 * Creates an html page that contains one photo plus navigation buttons.
 * @param imageNr	number of the image
 * @param sizeDir   the name of the directory from which the image is
 *                  to be read, e.g. "800x600" if on the noscript page
 *                  images of the size 800,600 shall be used.
 * @param fileName  the file name of the image
 * @param caption   the image's caption
 * @param flags		the HTMLProcessing flags, e.g. LAST_IMAGE|GALLERY
 * @return          a string containing the noscript image page.
 */
QString HTMLProcessing::noscriptImagePage(int imageNr, const QString sizeDir,
		                                  const QString fileName, const QString caption,
		                                  int flags)
{
	QString backlink, nextlink, imageHTML,
	        panelHTML = htmlTemplates["panelHTML"],
	        galleryButtonHTML = htmlTemplates["galleryButtonHTML"],
			imagePageHTML = htmlTemplates["imagePageHTML"];

	Q_ASSERT_X(imageNr >= 1, "HTMLProcessing::noscriptImagePage", "image nr < 1");

	if (imageNr > 1) {
		backlink = QString("noscript_image%i.html")
				       .replace("%i", QString::number(imageNr-1));
	}
	if (!(LAST_IMAGE & flags)) {
		nextlink = QString("noscript_image%i.html")
						       .replace("%i", QString::number(imageNr+1));
	}
	imageHTML = htmlTemplates["imageHTML"];
//	if (WITH_IMAGE_LINK & flags) {
//		imageHTML = htmlTemplates["imageWithLinkHTML"];
//	} else {
//		imageHTML = htmlTemplates["imageHTML"];
//	}
	imageHTML.replace("$SIZE", "slides/" + sizeDir);
	imageHTML.replace("$IMG", fileName);

	if (GALLERY & flags) {
		panelHTML.replace("$GALLERYBUTTON", galleryButtonHTML);
	} else {
		panelHTML.replace("$GALLERYBUTTON", "");
	}
	panelHTML.replace("$BACKLINK", backlink);
	panelHTML.replace("$NEXTLINK", nextlink);

	imagePageHTML.replace("$TITLE", QString("Image_") + QString::number(imageNr))
	             .replace("$SLIDENR", QString::number(imageNr))
	             .replace("$PANEL", panelHTML)
	             .replace("$CAPTION", caption)
	             .replace("$IMAGE", imageHTML)
                 .replace("$DOCTYPE", doctype_string);

	return (imagePageHTML);
}



