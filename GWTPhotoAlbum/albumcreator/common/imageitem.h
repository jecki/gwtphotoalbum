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


#ifndef IMAGEITEM_H
#define IMAGEITEM_H

#include <QObject>
#include <QCache>
#include <QString>
#include <QImage>
#include <QThread>
#include <QMutex>

#include <imageio.h>


/*!
 *  \class ImageItem
 *
 *  \brief ImageItem objects store path and caption information about images
 *         and can load, transform images etc.
 */

// TODO: add support for cutting images and equilizing colors!

class ImageItem : public QObject, protected ImageIO::CallbackInterface
{
    Q_OBJECT

public:
    enum ImageClass { IMAGE=1, PREVIEW=2, THUMBNAIL=4 };
    static const int ALL = IMAGE|PREVIEW|THUMBNAIL;

    static QSize	Thumbnail_Size;  /*!< size of thumbnails */
    static QSize	Preview_Size;    /*!< size of preview images */

    static void  setSize(ImageClass c, QSize size);
    QSize 		 size(ImageClass c = IMAGE);
    static void  setCacheSize(ImageClass c, int size);

    ImageItem(const QString path);
    virtual ~ImageItem();

    void    prefetchImage(int imageClass = IMAGE);
    QImage  image(ImageClass c = IMAGE);
    QString path() const { return filePath; }
    QString caption() const { return imgCaption; }

    QString destFileName() const { return destName; }
    void    modifyDestName(const QString newDestName) { destName = newDestName; }

    void 	setCaption(const QString caption) { imgCaption = caption; }
    void 	storeCaptionInOriginal();

    QList<QImage> resized(QList<QSize> sizes);

    bool 	noError() const { return error.isEmpty(); }
    QString getError() const { return error; }
    void	clearError() { error = QString(); }

    bool isValid();

    // TODO: persistence, i.e. store file path and caption in a stream
    // support for resolution arrays (standard resolution adjusted for the aspect ratio of the original)?

protected:
    static QList<ImageItem *> cachedImages;    /*!< list of image items with loaded image */
    static QList<ImageItem *> cachedPreviews;  /*!< list of image items with loaded preview */
    static int				  maxImages;	   /*!< maximum number of full images to be kept in memory */
    static int				  maxPreviews;     /*!< maximum number of preview images to be kept in memory */

    void   cache(ImageClass c);
    QImage fetch(ImageClass c) const;

 	virtual void receiveImage(const QString filePath, QImage image);
 	virtual void imageSaved(const QString filePath);
   	virtual void receiveErrorMsg(const QString filePath, const QString errorMsg);

private:
   	static	QThread *mainThread;
   	static  QImage  placeholder;

    QImage	original;
    QImage  previewImg;
    QImage  thumbnailImg;

    QSize	originalSize;

    QString	imgCaption;
    QString filePath;
    QString destName;
    QString	error;


    int		requestsIssued;   /*!< number of load or save requests currently issued */
    int		requestedClasses;
};

#endif // IMAGEITEM_H
