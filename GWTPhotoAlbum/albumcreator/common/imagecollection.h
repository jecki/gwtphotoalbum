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


#ifndef IMAGECOLLECTION_H
#define IMAGECOLLECTION_H

#include <QObject>
#include <QList>
#include <QString>
#include <QStringList>
#include <QTextStream>
#include <QSize>
#include <QMap>
#include <QFileInfo>
#include <QPointer>

#include "imageitem.h"
#include "htmlprocessing.h"
#include "zipfile.h"


//struct ICreateAlbumProgress {
//	virtual ~ICreateAlbumProgress() {};
//	virtual void progress(const QString &str, int nr, int all);
//};


// TODO: Add support for changing existing web albums

/*!
 *  \class ImageCollection
 *
 *  \brief ImageCollection manages a collection of ImageItem objects and
 *  generates all necessary image and control files for the online slide show.
 */
class ImageCollection : public QObject
{
    Q_OBJECT

public:
    static const QSize  Original_Size;
    static       QSize  Noscript_Size;
    static QList<QSize> Archive_Sizes;

//    static ICreateAlbumProgress ignore;

	QString	title;
	QString	subtitle;
	QString	bottom_line;
	QString	presentation_type;
	QString	layout_type, layout_data;
	QString panel_position, caption_position;;
	bool	disable_scrolling;
	bool	add_noscript_version;
	bool	add_offline_version;
	bool	add_mobile_layout;
	int     display_duration;
	int 	image_fading;
	int		thumbnail_width, thumbnail_height;
	int		gallery_horizontal_padding, gallery_vertical_padding;

	int		compression; /*!< jpeg compression 50 - 98, where 98 means best image quality and biggest files.*/

	QString archiveName; /*!< file name for an archive that contains all images for download */
	QSize   archiveSize; /*!< size of the images in the archive */

	// maybe make these variables private, channel access through functions like : add, remove, find, sort
    QList<ImageItem *>	imageList;  /*!< a list of images. The images are owned by this list! */


    ImageCollection(QObject *parent = NULL);
    virtual ~ImageCollection();

    ImageItem *search(const QString destName, int &continueWith);
    ImageItem *find(const QString destName);

    QList<QSize> const &sizes() { return sizesList; };
    void		 setSizes(const QList<QSize> &sizesList) { this->sizesList = sizesList; };

    int		addImageList(const QString &fileName);

	bool    fromJSON(const QString infoJSON);

	QString infoJSON() const;
	QString directoriesJSON();
	QString filenamesJSON() const;
	QString captionsJSON() const ;
	QString resolutionsJSON();

	bool    createAlbum(QString destinationPath);
	bool	isRunning() const {return running; } /*!< returns true, if album creation is under way, false otherwise */
	bool	rollback();

	QString errorString() { return errorMsg.join("\n"); }

private:
	HTMLProcessing genHTML;

	QList<QSize>				 sizesList;		/*!< list of sizes in which the images are to be stored in the album */
    QMap<QString, QList<QSize> > resList;
    QMap<ImageItem *, QString>	 resMap;

    bool		  ok;	/*!< flag that indicates that no error occured during album creation so far. */
    bool    	  running; /*!< flag that indicates that album creation is under way right now! */
    QStringList	  errorMsg;
    QMutex		  mutex;

    QString		  destPath;
    QFileInfoList deployedFiles;
    QStringList	  deployedImages;

    ZipFile		  *archive;
    int			  archiveImagePos;

    bool  validateSizes();
    //bool  validateImageList();
	QSize best_noscriptSize();
    void  createResolutionsMap();

    void deploy_GWTPhotoAlbumFiles(QDir location);
    void deploy_imageBunch(const QDir parent, QList<QImage> bunch,
    	     	  	  	   const QString name);
    void deploy_bunchPackage(QList<QFuture<QList<QImage> > > &imageBunches,
    						 QList<QString> &imageNames, QDir &directory,
    						 int &progressCounter);
//    QList<QImage> resizeAdaptor(ImageItem *img);
    void deploy_images(const QDir parent);

    bool terminateCreation(const QString error = QString());

public Q_SLOTS:
	void stopAlbumCreation();

Q_SIGNALS:
	/*!
	 * Informs about the porgress of an ongoing album creation process.
	 * (Useful, if album creation was started in a separate thread.)
	 *
	 * @param source  the calling ImageCollection object
	 * @param nr      the number of the image that is about to being processed
	 *                next.
	 */
	void progress(ImageCollection *source, int nr);

	/*!
	 * Informs that the album creation process is finished. Album creation
	 * can either have been finished regularly (completed == true,
	 * errorMsg.isEmpty() == true), stopped by a call to slot
	 * 'stopAlbumCreation' (completed = false, errorMsg.isEmpty() == true)
	 * or terminated because of an error (completed = false,
	 * errorMsg.isEmpty() == false).
	 *
	 * @param completed  true, if album creation has been fully successful
	 * @param errorMsg   contains the error message, in case album creation
	 *                   was terminated because of an error and not stopped
	 *                   by a call to 'stopAlbumCreation'.
	 */
	void finished(bool completed, const QString &errorMsg);
};



/*!
 * Adaptor class to allow catching the progress signal from non QtObjects.
 */
class ProgressAdaptor : public QObject {
	Q_OBJECT

public:
	typedef void (*FunctionPtr)(ImageCollection *, int);
	typedef void (QObject::*MethodPtr)(ImageCollection *, int);

	ProgressAdaptor(ImageCollection *ic, FunctionPtr function,
			        QPointer<QObject> object = NULL,
			        MethodPtr method = NULL, QObject *parent = NULL);

	virtual ~ProgressAdaptor();
	virtual void progress(ImageCollection *ic, int nr);

public Q_SLOTS:
	void progressSlot(ImageCollection *source, int nr);

private:
	ImageCollection   *ic;
	FunctionPtr		  progressFunction;
	QPointer<QObject> progressObject;
	MethodPtr         progressMethod;
};

#endif // IMAGECOLLECTION_H
