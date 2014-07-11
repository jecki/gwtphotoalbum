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


#include "imagecollection.h"
#include "jsonsupport.h"
#include "toolbox.h"

#include <QMap>
#include <QList>
#include <QVariant>
#include <QVariantMap>
#include <QVariantList>
#include <QString>
#include <QStringList>
#include <QTextStream>
#include <QDir>
#include <QFuture>
#include <QtAlgorithms>
#if QT_VERSION >= 0x050000
#include <QtConcurrent/QtConcurrentRun>
#else
#include <QtCore/QtConcurrentRun>
#endif

#ifndef NDEBUG
#include <iostream>
#endif

static QList<QSize> archiveSizes() {
	QList<QSize> asList;
	asList << QSize(-1, -1);
	asList << QSize(3000, 2250);
	asList << QSize(2048, 1200);
	return (asList);
}

const QSize  ImageCollection::Original_Size = QSize(-1, -1);
      QSize  ImageCollection::Noscript_Size = QSize(1024, 768);
QList<QSize> ImageCollection::Archive_Sizes = archiveSizes();
// HTMLProcessing ImageCollection::genHTML;
//ICreateAlbumProgress ImageCollection::ignore = ICreateAlbumProgress();
//
//void ICreateAlbumProgress::progress(const QString &str, int nr, int all)
//{ (void) str; (void) nr; (void) all; } // suppress unused parameter warnings


ImageCollection::ImageCollection(QObject *parent)
    : QObject(parent), archive(0), archiveImagePos(0)
{
	presentation_type = "gallery";
	layout_type       = "fullscreen";
	layout_data       = "IOF";
	// image_clickable   = false;
	panel_position    = "bottom";
	caption_position  = "bottom";
	disable_scrolling = true;
	add_noscript_version = true;
	add_offline_version = true;
	add_lowres_layout = true;
	display_duration  = 5000;
	image_fading 	  = -750; // negative value: fade in, fade out in sequence rather than at the same time
	thumbnail_width   = ImageItem::Thumbnail_Size.width();
	thumbnail_height  = ImageItem::Thumbnail_Size.height();
	gallery_horizontal_padding = 80;
	gallery_vertical_padding = 30;
	sizesList << ImageItem::Thumbnail_Size;
	sizesList << QSize(320,240);
	sizesList << QSize(640, 480);
	sizesList << QSize(1280, 800);
	sizesList << QSize(2048, 1200);
	// sizes << ImageCollection::Original_Size;

	compression = 90;

	archiveName = "";
	archiveSize = QSize(3000, 2250);

	running = false;
	ok      = true;
}


ImageCollection::~ImageCollection()
{
	foreach(ImageItem *it, imageList) {
		delete it;
	}
}



/*!
 * Finds the next image item the destination file name of which loosly fits
 * destName, starting at index continueWith.
 * @param destName     the name to be looked up.
 * @param continueWith the index to start with
 * @return either a pointer to the next found ImageItem object or NULL,
 *         if none was found.
 */
ImageItem *ImageCollection::search(const QString destName, int &continueWith)
{
	Q_ASSERT_X(continueWith >= 0, "ImageCollection::search",
			   "parameter 'continueWith' must be greater or equal 0!");

	while (continueWith < imageList.size()) {
		ImageItem *it = imageList[continueWith];
		continueWith++;
		if (it->destFileName().indexOf(destName, 0, Qt::CaseInsensitive) >= 0 ||
			destName.indexOf(it->destFileName(), 0, Qt::CaseInsensitive) >= 0) {
			return (it);
		}
	}

	return (NULL);
}



/*!
 * Returns the image with destination name "destName" or NULL if
 * no image with this name could be found.
 * @param destName	the destination name of the image
 * @return an ImageItem pointer or NULL
 */
ImageItem *ImageCollection::find(const QString destName)
{
	foreach(ImageItem *it, imageList) {
		if (it->destFileName() == destName) return (it);
	}
	return (NULL);
}


/*!
 * Adds images from a list in a text file to the image collection.
 * Accepted text file formats:
 * GQView, Geegie (.gqv) : list of filenames, each filename enclosed by
 *                         inverted commas, separated by line feeds.
 * @param fileName  name of a text file that contains a list of filenames,
 *                  e.g. "collection.gqv"
 * @return  the number of images that were successfully added to the list.
 */
int ImageCollection::addImageList(const QString &fileName)
{
	QFile	file(fileName);
	int		nr = 0;

	if (file.open(QIODevice::ReadOnly)) {
		QTextStream stream(&file);
		QString 	line("");
		QStringList parts;
		QFileInfo	info;
		ImageItem   *item;
		int 		i1, i2;

		while (!line.isNull() && nr < 65536) {
			line = stream.readLine(16384);
			parts = line.split(",");
			foreach(QString name, parts) {
				i1 = name.indexOf('"');
				i2 = name.lastIndexOf('"');
				if (i2 > (i1+1)) {
					info.setFile(name.mid(i1+1, i2-i1-1));
					if (hasSupportedImageFormat(info.fileName()) && info.exists()) {
						item = new ImageItem(info.filePath());
						Q_ASSERT_X(item != NULL, "ImageCollection::addImages",
								   "heap full !?");
						if (item != NULL) {
							imageList.append(item);
							nr ++;
						}
					}
				}
			}
		}
	}

	return (nr);
}


/*!
 * Fills the InfoRecord with the data taken from a JSON text.
 * @param infoJSON  the JSON text chunk from the info is read
 * @return false, if an error occurred while parsing.
 */
bool ImageCollection::fromJSON(const QString infoJSON)
{
	bool ok = true;
	int  index = 0;
	QMap<QString, QString> map = parseStringMap(infoJSON, index, infoJSON.length(), ok);

	presentation_type = map["presentation type"];
	layout_type       = map["layout type"];
	layout_data       = map["layout data"];
	title             = map["title"];
	subtitle          = map["subtitle"];
	bottom_line       = map["bottom line"];
	display_duration  = map["display duration"].toInt();
	image_fading      = map["image fading"].toInt();
	thumbnail_width   = map["thumbnail width"].toInt();
	thumbnail_height  = map["thumbnail height"].toInt();
	gallery_horizontal_padding = map["gallery horizontal padding"].toInt();
	gallery_vertical_padding = map["gallery vertical padding"].toInt();

//	QString s = map["image clickable"].toLower();
//	if (s == "true" || s == "on" || s == "1") {
//		image_clickable = true;
//	} else {
//		image_clickable = false;
//	}

	QString s = map["disable scrolling"].toLower();
	if (s == "true" || s == "on" || s == "1") {
		disable_scrolling = true;
	} else {
		disable_scrolling = false;
	}

	return (ok);
}



/*!
 * Returns the info.json data for the image collection as text string
 * @return info.json as string
 */
QString ImageCollection::infoJSON() const
{
	QVariantMap map;
	QString     str;
	QTextStream stream(&str);
	bool		ok = true;

	map["title"]			 = title;
	map["subtitle"]			 = subtitle;
	map["bottom line"]		 = bottom_line;

	map["presentation type"] = presentation_type;
	map["layout type"]       = layout_type;
	map["layout data"]		 = layout_data;
	map["panel position"]	 = panel_position;
	map["caption position"]  = caption_position;

	map["display duration"]  = QString::number(display_duration);
	map["image fading"]      = QString::number(image_fading);
	map["thumbnail width"]   = QString::number(thumbnail_width);
	map["thumbnail height"]  = QString::number(thumbnail_height);
	map["gallery horizontal padding"] = QString::number(gallery_horizontal_padding);
	map["gallery vertical padding"]   = QString::number(gallery_vertical_padding);

	map["disable scrolling"] = QString(disable_scrolling ? "true" : "false");
	map["add lowres layout"] = QString(add_lowres_layout ? "true" : "false");

	writeJSON(stream, QVariant(map), ok);
	Q_ASSERT_X(ok, "ImageCollection::infoJSON", "error while constructing info JSON (this should never happen)!");
	stream << "\n";
	return (str);
}


/*!
 * Returns the directories.json for the image collection as text string.
 * @return string containing directories.json
 */
QString ImageCollection::directoriesJSON()
{
	Q_ASSERT_X(validateSizes(), "ImageCollection::directoriesJSON",
			   "sizes list is not properly formed!");

	QStringList sizesStrings;
	QString		output;
	QTextStream stream(&output);
	bool		ok = true;

	foreach(const QSize &s, sizesList) {
		sizesStrings.append(sizeStr(s));
	}

	writeJSON(stream, QVariant(sizesStrings), ok);
	Q_ASSERT_X(ok, "ImageCollection::directoriesJSON", "error while constructing JSON!");
	return (output);
}


/*!
 * Returns the filnames.json data.
 * Invalid image items will silently be ignored!
 * @return string containing filenames.json
 */
QString ImageCollection::filenamesJSON() const
{
	QStringList fileNames;
	QString		output;
	QTextStream	stream(&output);
	bool		ok = true;

	foreach(ImageItem *it, imageList) {
		if (it->isValid()) {
			fileNames.append(it->destFileName());
		}
	}
	writeJSON(stream, QVariant(fileNames), ok);
	Q_ASSERT_X(ok, "ImageCollection::filenamesJSON", "error while constructing JSON!");
	return (output);
}


/*!
 * Returns the captions.json data.
 * Invalid image items will silently be ignored!
 * @return string containing the content for the captions.json file
 */
QString ImageCollection::captionsJSON() const
{
	QVariantMap map;
	QString     str;
	QTextStream stream(&str);
	bool		ok = true;

	foreach(ImageItem *it, imageList) {
		if (it->isValid()) {
			map[it->destFileName()] = QVariant(it->caption());
		}
	}
	writeJSON(stream, QVariant(map), ok);
	Q_ASSERT_X(ok, "ImageCollection::captionsJSON", "error while constructing JSON!");
	stream << "\n";
	return (str);
}


/*!
 * Creates resolutions dictionary as JSON.
 * Invalid image items will silently be ignored!
 * @return string containing the resolutions.json data
 */
QString ImageCollection::resolutionsJSON()
{
	Q_ASSERT_X(validateSizes(), "ImageCollection::directoriesJSON",
			   "sizes list is not properly formed!");

	QVariantMap  resolutions;
	QVariantMap  imagemap;
	QVariantList datalist;
	QString 	 output;
	QTextStream  stream(&output);
	bool		 ok = true;

	// (re-)create the resolutions map only if needed
	if (!resMap.isEmpty()) {
		bool re_create = false;
		QList<ImageItem *> keys(resMap.keys());
		foreach(ImageItem *it, imageList) {
			if (!resMap.contains(it)) {
				re_create = true;
				break;
			} else {
				keys.removeOne(it);
			}
		}
		if (re_create or !keys.isEmpty()) createResolutionsMap();
	} else{
		createResolutionsMap();
	}

	foreach(const QString &key, resList.keys()) {
		QVariantList list;
		foreach(const QSize &s, resList[key]) {
			list.append(QVariant(s));
		}
		resolutions[key] = list;
	}
	datalist.append(resolutions);

	foreach(ImageItem *it, resMap.keys()) {
		if (it->isValid()) {
			imagemap[it->destFileName()] = resMap[it];
		}
	}
	datalist.append(imagemap);

	writeJSON(stream, datalist, ok);
	Q_ASSERT_X(ok, "ImageCollection::resolutionsJSON", "error while constructin JSON");
	stream << "\n";
	return (output);
}


/*!
 * Internal function: called by create album to do some necessary cleanup
 * before returning.
 */
bool ImageCollection::terminateCreation(const QString error)
{
	if (!error.isEmpty()) {
		errorMsg << error;
		ok = false;
	}
	if (destPath.isEmpty()) {
		deployedFiles.clear();
	} else {
		// This is quite dangerous, in case 'destPath' does have a wrong value for some reason
		// extra checks or completely different rollback concept recomendable
		deployedFiles = listTree(destPath);
	}
	emit finished(ok, errorString());
	running = false;
	return (ok);
}

/*!
 * Creates the photo album at the given location in the file system.
 * Invalid image items will silently be ignored!
 * @param destinationPath  name of a directory (to be created by createAlbum())
 *        where the album will be created.
 * @param  archiveName  name for an additional image archive
 * @return an error message if an error occurred, otherwise an empty string.
 */
// TDOD: Fix Error Handling!!! Image load safe errors are currently not reported; add unit test!!!
bool ImageCollection::createAlbum(QString destinationPath)
{
	Q_ASSERT_X(validateSizes(), "ImageCollection::directoriesJSON",
			   "sizes list is not properly formed!");

	errorMsg.clear();
	ok = true;

	mutex.lock();
	if (running) {
		mutex.unlock();
		throw "Internal Error: ImageCollection::createAlbum called although it is already running!";
	} else {
		running = true;
		mutex.unlock();
	}

	destPath = QString();
	deployedFiles.clear();
	deployedImages.clear();

	destinationPath = QDir::cleanPath(QDir::fromNativeSeparators(destinationPath));
	if (destinationPath.isEmpty()) {
		return (terminateCreation("empty directory name!"));
	}
	if (destinationPath.at(destinationPath.length()-1) == '/') {
		destinationPath = destinationPath.left(destinationPath.length()-1);
	}
	QFileInfo destination(QDir::cleanPath(QDir::fromNativeSeparators(destinationPath)));
	QString dirName = destination.fileName();

	QDir parent(destination.path());
	if (parent.exists(dirName)) {
		return (terminateCreation("directory: " + dirName + " already exists!"));
	}

	if (!parent.mkdir(dirName)) {
		return (terminateCreation("could not create directory: " + destinationPath));
	}
	if (!parent.cd(dirName)) {
		return (terminateCreation("could not change to directory: "+dirName));
	}
	// just an extra check, to avoid involuntary deletion of files
	if (!QDir(destination.filePath()).count() == 0) {
		destPath = destination.filePath();
	} else {
		return (terminateCreation("strange path missmatch between "+destination.filePath()+" and " + parent.path()));
	}

	deploy_GWTPhotoAlbumFiles(parent);

	int flags = 0;
	if (presentation_type == "gallery") flags |= HTMLProcessing::GALLERY;
	if (add_noscript_version) flags |= HTMLProcessing::NOSCRIPT_PAGES;
	writeTextFile(genHTML.indexPage(flags), parent.path()+"/index.html", errorMsg, ok);
	QString startPage = readTextFile(parent.path()+"/GWTPhotoAlbum_xs.html",
						errorMsg, ok);
	if (!ok) {
		return (terminateCreation());
	}

	parent.mkdir("slides");
	foreach(const QSize &s, sizesList) {
		if (!parent.mkdir("slides/"+sizeStr(s))) {
			return (terminateCreation("Could not create dir: slides/"+sizeStr(s)));
		}
	}
	deploy_images(parent);
	if (!ok) {
		foreach(ImageItem *it, imageList) {
			if (!it->noError()) {
				errorMsg << it->getError();
			}
		}
		return (terminateCreation());
	}

	QString infojs = infoJSON(),
			directoriesjs = directoriesJSON(),
			filenamesjs = filenamesJSON(),
			captionsjs = captionsJSON(),
			resolutionsjs = resolutionsJSON();

	writeTextFile(genHTML.fatStartPage(startPage, infojs,
				directoriesjs, filenamesjs, captionsjs, resolutionsjs),
				parent.path()+"/GWTPhotoAlbum_fatxs.html", errorMsg, ok);
	if (add_offline_version) {
		writeTextFile(genHTML.offlineIndex(flags), parent.path()+"/index_offline.html", errorMsg, ok);
	}

	writeTextFile(infojs, parent.path()+"/slides/info.json", errorMsg, ok);
	writeTextFile(directoriesjs, parent.path()+"/slides/directories.json", errorMsg, ok);
	writeTextFile(filenamesjs, parent.path()+"/slides/filenames.json", errorMsg, ok);
	writeTextFile(captionsjs, parent.path()+"/slides/captions.json", errorMsg, ok);
	writeTextFile(resolutionsjs, parent.path()+"/slides/resolutions.json", errorMsg, ok);

	if (add_noscript_version) {
		writeTextFile(genHTML.noscriptGallery(title, subtitle, bottom_line,
											  deployedImages, ImageItem::Thumbnail_Size),
					  parent.path()+"/noscript_gallery.html", errorMsg, ok);
		int N = imageList.size()-1;
		QString sizeDir = sizeStr(best_noscriptSize());
		for (int i = 0; i <= N; i++) {
			if (i == N) flags |= HTMLProcessing::LAST_IMAGE;
			ImageItem *it = imageList[i];
			if (it->isValid()) {
				writeTextFile(genHTML.noscriptImagePage(i+1, sizeDir, it->destFileName(), it->caption(), flags),
						parent.path()+QString("/noscript_image")+QString::number(i+1)+QString(".html"), errorMsg, ok);
			}
		}
	}

	parent.cd("..");
	parent.cd("..");

	return (terminateCreation());
}


bool cmpdirs(QFileInfo &i1, QFileInfo &i2) {
	return (i1.path().length() > i2.path().length());
}

/*!
 * Deletes all files and directories that have been created during the last
 * createAlbum call. This can be used to delete half complete albums if album
 * creation failed. Files and directories that were not created by create album
 * will remain intact.
 *
 * @param destinationPath the path to the album. If it does not match the
 *                        path with createAlbum() was called, nothing happens.
 * @return true, if all files and directories could be deleted, false if not.
 */
bool ImageCollection::rollback()
{
	QFileInfoList dirs;

	errorMsg.clear();
	bool ok = true;

	foreach(QFileInfo info, deployedFiles) {
		if (info.isDir()) {
			dirs.append(info);
		} else {
			QFile f(info.filePath());
			// std::cout << info.filePath().toStdString() << std::endl;
			if (!f.remove()) {
				ok = false;
				errorMsg << f.errorString();
			}
		}
	}

	if (!destPath.isEmpty()) {
		dirs.append(QFileInfo(destPath));
	}
	qSort(dirs.begin(), dirs.end(), cmpdirs);

	foreach(QFileInfo info, dirs) {
		// std::cout << info.fileName().toStdString() << std::endl;
		if (!info.dir().rmdir(info.fileName())) {
			// std::cout << "not deleted" << std::endl;
			ok = false;
			errorMsg << "could not remove dir "+info.filePath();
		}
	}

	deployedFiles.clear();
	destPath = QString();
	return (ok);
}


/*!
 * Checks if the sizes list is properly formed for the photoalbum
 * as decribed by the variables of the image collection.
 * i.e. if the photoalbum is to have a gallery then Thumbnail_Size
 * must be included in the sizes list.
 * @return true, if the sizes list is properly formed
 */
bool ImageCollection::validateSizes() {
	int   numInvalid = 0;
	bool  hasThumbnailSize = false;
	QSize lastSize(0, 0);

	// there must be at least one size in the list.
	if (sizesList.size() >= 1) {
		foreach(const QSize &s, sizesList) {
			// there must be no more than 1 invalid (i.e. original) size.
			if (s.isEmpty()) {
				if (numInvalid >= 1) return (false);
				numInvalid++;
			// all valid sizes must be larger or equal than Thumbnail_Size
			} else if (s.width() < ImageItem::Thumbnail_Size.width() ||
				       s.height() < ImageItem::Thumbnail_Size.height() ) {
				return (false);
			// sizes must be of increasing order, the same size
			// must not appear twice;
			} else if ((s.width() < lastSize.width() ||
					    s.height() < lastSize.height()) ||
					   (s == lastSize)) {
				return (false);
			} else if (s == ImageItem::Thumbnail_Size) {
				hasThumbnailSize = (true);
			}
			lastSize = s;
		}
		// thumbnail size must be present if the photoalbum has a gallery
		// or a slide show with a filmstrip
		if (!hasThumbnailSize &&
			(presentation_type == "gallery" || layout_data.indexOf('F') >= 0)) {
			return (false);
		}
		return (true);
	}
	return (false);
}

///*!
// * Checks the validity of the image list.
// * WARNING: As long as no attempt to load
// * an image has been made, the list may still contain entries that either
// * do not point to image files at all or to image files that may fail to load!
// * @return true, if the list is not empty and no image failed to load so far.
// */
//bool ImageCollection::validateImageList() {
//	if (imageList.size() >= 1) {
//		foreach(ImageItem *it, imageList) {
//			if (!it->isValid()) {
//				return false;
//			}
//		}
//		return true;
//	 } else {
//		 return false;
//	 }
//}

/*!
 * Returns the best size (i.e. the size closest to 800x600) from
 * a list of sizes.
 * @param  sizesList  the list of available sizes
 * @return the "best" size of these for no script image pages.
 */
QSize ImageCollection::best_noscriptSize() {
	Q_ASSERT_X(sizesList.size() > 0, "HTMLProcessing::best_noscriptSize",
			   "sizesList must contain at least 1 element!");
	QSize bestsize = sizesList[0];
	int   cmp = 1000000000;
	int	  cmpArea = Noscript_Size.width() * Noscript_Size.height();
	foreach(const QSize &s, sizesList) {
		int d = abs(((s.width() * s.height() - cmpArea)));
		if (d < cmp) {
			bestsize = s;
			cmp = d;
		}
	}
	return (bestsize);
}



/*!
 * Builds the 'resList' and 'resMap' maps that will later be written into
 * the resolutions.json.
 */
void ImageCollection::createResolutionsMap() {
	Q_ASSERT_X(validateSizes(), "ImageCollection::createResolutionsMap",
			   "sizes list is not properly formed!");

	QMap<QString, QString> reverse; // maps string rep of sizes list to names
	resList.clear();
	resMap.clear();
	int keyNr = 0;

	foreach(ImageItem *it, imageList) {
		if (it->isValid()) {
			QList<QSize> sl;
			foreach(const QSize &s, sizesList) {
				sl.append(adjustSize(it->size(), s));
			}
			QString rep = strRepSize(sl);
			if (reverse.contains(rep)) {
				QString key = reverse[rep];
				resMap[it] = key;
			} else {
				keyNr++;
				QString key = QString("res") + QString::number(keyNr);
				reverse[rep] = key;
				resList[key] = sl;
				resMap[it] = key;
			}
		}
	}
}


/*!
 * Extracts the GWTPhotoAlbum scripts to the given location
 * @param location  the location where the scripts shall be extracted.
 * @param ok  only commits the command if ok is true; sets ok to false if
 *            any IO error occurred.
 */
void ImageCollection::deploy_GWTPhotoAlbumFiles(QDir location)
{
	bool conversionSuccess = true;

	if (ok) {
		QByteArray data;
		QFile archive(":/data/GWTPhotoAlbum-Deploy.qa");
		if (archive.open(QIODevice::ReadOnly)) {
			data = archive.readAll(); // qUncompress(archive.readAll());
			archive.close();
		}
		if (archive.error() != QFile::NoError) {
			errorMsg << archive.errorString();
			ok = false;
		}

		const char *dPtr = data.constData();
		int i = 0, end = data.size();

		while (i < end && ok) {
			int N = data.mid(i, 10).toInt(&conversionSuccess, 16);
			ok = ok && conversionSuccess;
			i += 10;
			if (ok) {
				QString path(data.mid(i, N));
				i += N;
				QString dirname = path.section('/', 0, -2);
				if (!dirname.isEmpty()) {
					if (!location.mkpath(dirname)) {
						errorMsg << "could not create path: "+dirname;
						ok = false;
					}
				}

				if (ok) {
					int L = data.mid(i, 10).toInt(&conversionSuccess, 16);
					ok = ok && conversionSuccess;
					i += 10;
					if (ok) {
						QFile file(location.path()+QString("/")+path);
						file.open(QIODevice::WriteOnly);
						file.write(dPtr + i, L);
						i += L;
						file.close();
						if (file.error() != QFile::NoError) {
							errorMsg << file.errorString();
							ok = false;
						}
					}
				}
			}
		}
		if (!conversionSuccess) {
			errorMsg << "Internal Error: deployment archive corrupted!";
		}
	}
}


/*!
 * Deploys a "bunch" of images. A "bunch" is a set of different sized versions
 * of one and the same image.
 * Invalid image items will silently be ignored!
 * @param parent  the slides-directory
 * @param bunch	  the list of different sized version of an image to deploy
 * @param name    the filename of the image
 * @param sizes	  the list of sizes
 * @param ok	  only commits the command if ok is true; sets ok to false if
 *                any IO error occurred.
 */
void ImageCollection::deploy_imageBunch(const QDir parent, QList<QImage> bunch,
					  	  	  	  	    const QString name)
{
	Q_ASSERT_X((archive == NULL && (bunch.size() == sizesList.size())) ||
			   (archive != NULL && ((bunch.size() == sizesList.size()+1) ||
					                (sizesList.contains(archiveSize)) )) ,      // bunch.isEmpty() ||
			   "imagecollection - deploy_imageBunch",
			   "size mismatch between image bunch and sizes list!");

	if (archive != NULL) {
		QImage img = bunch[archiveImagePos];
		if (!sizesList.contains(archiveSize)) {
			bunch.removeAt(archiveImagePos);
		}
		QByteArray data;
		QBuffer	   buffer(&data);
		buffer.open(QIODevice::WriteOnly);
		img.save(&buffer, "jpg");
		buffer.close();
		archive->addData(name, data);
	}

	for (int i = 0; i < bunch.size() && ok; i++) {
		QDir dir(parent.path() + QString("/") + sizeStr(sizesList[i]));
		Q_ASSERT_X(dir.exists(), "ImageCollection::deploy_imageBunch",
				   " wrong image size!?");
		QString path = dir.path() + QString("/") + name;
		// TODO: what about quality settings?
		// Don't use ImageIO here, because the function should not return
		// before all images have been written.
		if (!bunch[i].save(path)) { // , "JPG", compression);
			errorMsg << "could not write image "+path;
			ok = false;
		}
	}
}


/*!
 * Deploys a packages of image bunches. Each bunch contains on and the same
 * image in different sizes. A bunch package is (a future of) a list of such
 * bunches.
 * Updates a progress counter for each bunch to be deployed.
 *
 * @param imageBunches		a package of image bunches
 * @param imageNames		the corresponding image names
 * @param directoy			the deployment directory
 * @param callback			a callback to notify of progress counter updates
 * @param progressCounter	a reference to the progress counter
 * @param progressLimit     the maximum value of the progress counter
 * @param ok				the error flag.
 */
void ImageCollection::deploy_bunchPackage(QList<QFuture<QList<QImage> > > &imageBunches,
						 QList<QString> &imageNames, QDir &directory,
						 int &progressCounter)
{
	while(!imageBunches.isEmpty() && ok) {
		QFuture<QList<QImage> > bunch = imageBunches.takeFirst();
		QString name = imageNames.takeFirst();
		emit progress(this, ++progressCounter);
		QList<QImage> result = bunch.result();
		if (result.isEmpty()) {
			errorMsg << "could not resize image "+name;
			// ok = false;
		} else {
			deploy_imageBunch(directory, result, name);
			if (ok) {
				deployedImages.append(name);
			}
		}
	}
}


///*!
// * Resizes and image like ImageItem::resized, but appends and image that
// * is resized to 'this->archiveSize' to the returned image list, in case
// * this->archiveName and this->archiveSize are not empty.
// *
// * @param img  the image item to be resized
// * @return the list of resized images, including the image for the archive at
// *         the end.
// */
//QList<QImage> ImageCollection::resizeAdaptor(ImageItem *img)
//{
//	QList<QImage> imageList = img->resized(sizesList);
//	if (!archiveName.isEmpty() && !archiveSize.isEmpty()) {
//		imageList.append(smartResize(img->image(), archiveSize));
//	}
//	return imageList;
//}


/*!
 * Deploys the slides in the sizes directories.
 * Invalid image items will silently be ignored!
 * If archiveName and archiveSize are not empty, then an
 * zip archive containing all images for download will be added as
 * well.
 * It is assumed that sizes List is ordered from the smallest to
 * the largest!
 *
 * @param parent    the slides-directory
 * @param imageList list of images
 * @param sizes		list of image sizes
 * @param ok	    only commits the command if ok is true; sets ok to false if
 *                  any IO error occurred.
 */
void ImageCollection::deploy_images(const QDir parent)
{

#ifndef NDEBUG
	int l = sizesList.length()-1;
	if (sizesList[l].isEmpty()) l--;
	while (l > 0) {
		Q_ASSERT_X(sizesList[l].width() > sizesList[l-1].width() &&
				   sizesList[l].height() > sizesList[l-1].height(),
				   "ImageCollection::deploy_images",
				   "sizesList list wasn't ordered from smallest to largest!");
		l--;
	}
#endif // NDEBUG

	QDir dir(parent);
	if (!dir.cd("slides")) {
		errorMsg << "could not change to directory 'slides'";
		ok = false;
	}
	if (ok) {

		ZipFile archiveFile(parent.path()+"/"+archiveName);
		QList<QSize> *pSizesList = &sizesList;
		QList<QSize> extSizesList;

		if (!archiveName.isEmpty() && (!archiveSize.isEmpty() ||
				                       archiveSize == Original_Size) ) {
			if (!sizesList.contains(archiveSize)) {
				bool insert_flag = true;
				foreach(QSize size, sizesList) {
					if (insert_flag && archiveSize.width() <= size.width() &&
							archiveSize.height() <= size.height()) {
						extSizesList.append(archiveSize);
						insert_flag = false;
					}
					extSizesList.append(size);
				}

				// in case archive size was Original_Size it has mistakenly
				// been inserted at the beginning in the previous loop

				if (extSizesList.removeOne(Original_Size)) {
					extSizesList.append(Original_Size);
				}

//				if (archiveSize.width() > extSizesList.last().width() ||
//						archiveSize.height() > extSizesList.last().height()) {
				if (insert_flag) {
					if (extSizesList.last() == Original_Size) {
						extSizesList.insert(extSizesList.length()-1, archiveSize);
					} else {
						extSizesList.append(archiveSize);
					}
				}

				pSizesList = &extSizesList;
			}
			archiveImagePos = pSizesList->indexOf(archiveSize);
			archiveFile.open();
			archive = &archiveFile;
		} else {
			archive = NULL;
		}

		QList<QFuture<QList<QImage> > > imageBunches;
		QList<QString> imageNames;
		int N = imageList.size();
		int k = 0;
		for (int i = 0; i < N && ok; i++) {
			imageBunches.append(QtConcurrent::run(imageList[i],
					            &ImageItem::resized, *pSizesList));
			// imageBunches.append(QtConcurrent::run(this, &ImageCollection::resizeAdaptor, imageList[i]));
			imageNames.append(imageList[i]->destFileName());
			//std::cout << imageList[i]->destFileName().toStdString() << std::endl;
			if (i > 0 && i % 16 == 0) {
				deploy_bunchPackage(imageBunches, imageNames, dir, k);
			}
		}
		deploy_bunchPackage(imageBunches, imageNames, dir, k);

	}
}


/*!
 *	 Interrupts the creation of an album.
 */
void ImageCollection::stopAlbumCreation()
{
	ok = false;
}





ProgressAdaptor::ProgressAdaptor(ImageCollection *ic, FunctionPtr function,
		QPointer<QObject> object, MethodPtr method, QObject *parent) : QObject(parent)
{
	Q_ASSERT(!((function != NULL) && (method != NULL)));
	this->ic = ic;
	progressFunction = function;
	progressObject = object;
	progressMethod = method;
	QObject::connect(ic, SIGNAL(progress(ImageCollection *, int)),
					 this, SLOT(progressSlot(ImageCollection *, int)));
}

ProgressAdaptor::~ProgressAdaptor()
{
	QObject::disconnect(ic, SIGNAL(progress(ImageCollection *, int)),
				  	    this, SLOT(progressSlot(ImageCollection *, int)));
}

void ProgressAdaptor::progress(ImageCollection *ic, int nr)
{
	if (progressMethod != NULL) {
		(progressObject->*progressMethod)(ic, nr);
	} else if (progressFunction != NULL) {
		(*progressFunction)(ic, nr);
	} else {
		Q_ASSERT_X(false, "ProgressWrapper::progress", "ProgressWrapper must either be initialized with a progress function or method or its virtual progress method must be overwritten!");
	}
}

void ProgressAdaptor::progressSlot(ImageCollection *source, int nr)
{
	progress(source, nr);
}
