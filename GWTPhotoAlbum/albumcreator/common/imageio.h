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


#ifndef IMAGEIO_H
#define IMAGEIO_H

#include <QObject>
#include <QThread>
#include <QMutex>
#include <QWaitCondition>
#include <QList>
#include <QSet>
#include <QSize>
#include <QImage>

#include "imageiocore.h"


/*!
 *  \class ImageIO
 *
 *  \brief ImageIO is a singleton class responsible for loading and storing images.
 */

class ImageIO : public QThread
{
    Q_OBJECT

public:
    class CallbackInterface {
    public:
     	virtual void receiveImage(const QString filePath, QImage image);
     	virtual void imageSaved(const QString filePath);
       	virtual void receiveErrorMsg(const QString filePath, const QString errorMsg) = 0;
    };

    enum RequestPriority { TopPriority, NormalPriority };
    enum RequestType {Load = 1, Save = 2};
    struct Request;

    static ImageIO &instance();
    virtual ~ImageIO();

    void registerServer(const QString name, QNetworkAccessManager *server, int maxConnections = 1);
    void deregisterServer(const QString name);

    Request *requestImage(CallbackInterface *callback, const QString filePath, const QSize size = QSize(),
    		              ImageIOCore::UseCache uc = ImageIOCore::Cache,
    		              RequestPriority priority = NormalPriority);

    bool quickRequest(CallbackInterface *callback, const QString filePath, QImage &image, const QSize = QSize(),
    		          ImageIOCore::UseCache uc = ImageIOCore::Cache,
                      RequestPriority priority = NormalPriority);

    QImage loadImmediately(const QString filePath, const QSize size = QSize());

    Request *saveImage(CallbackInterface *callback, const QString filePath, const QImage image,
    		           int quality = -1, ImageIOCore::UseCache uc = ImageIOCore::Cache,
 		               RequestPriority priority = NormalPriority);

    void waitForRequest(Request *request);
    bool cancelRequest(Request *&request);
    bool cancelAllRequests(CallbackInterface *callback);

protected:
    ImageIO(QObject *parent = 0);
    //ImageIO(const ImageIO &);

    virtual void run();

private:
    static QThread	*mainThread;	/*!< the thread that receives the events from iocore. */

    ImageIOCore		iocore;
    QList<Request *> pending, purgeList;
    QSet<QString> 	requestBook;
    QMutex 			mutex;
    QWaitCondition 	moreRequests;
    bool 			terminateIO;	/*!< indicates that the imageIO thread will be terminated */

    bool checkForDuplicate(Request *request);
    Request *findRequest(RequestType type, const QString filePath);

private Q_SLOTS:
	void imageLoaded(const QString filePath, const QImage image);
	void imageSaved(const QString filePath);
	void ioError(const QString filePath, const QString errorMsg);

    void cleanUp();
};


#endif // IMAGEIO_H
