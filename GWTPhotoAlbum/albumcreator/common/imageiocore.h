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


#ifndef IMAGEIOCORE_H
#define IMAGEIOCORE_H

#include <QObject>
#include <QString>
#include <QSize>
#include <QImage>
#include <QCache>
#include <QMutex>
#include <QFuture>
#include <QLinkedList>
#include <QtNetwork/QNetworkAccessManager>


/*!
 *  \class ImageIOCore
 *
 *  \brief ImageIOCore contains functions for loading, caching, storing and resizing images.
 *
 *  Because ImageIOCore implements core functionality for image loading, it can only
 *  be instantiated by its friend class ImageIO and does not contain a public constructor.
 *  The functions loadImage, fromCache, saveImage and resizeAndNotify should best called
 *  from another thread. The end of the operation is indicated through signals.
 *
 *  It is discouraged though to call loadImage and saveImage from several different threads,
 *  if loading or storing take place on the same device. The idea is that all actions
 *  that should take place sequentially (loading and storing to or from disk) will be
 *  called from one thread, albeit not the programs main thread. Resizing of images
 *  will be parallelized by resizeAndNotify through QtConcurrent::run
 */

class ImageIOCore : public QObject
{
    Q_OBJECT

    friend class ImageIO;

public:
    enum UseCache { Cache, DontCache };

    void loadImage(const QString filePath, const QSize size = QSize(), UseCache uc = Cache);
    bool fromCache(const QString filePath, const QSize size = QSize(), UseCache uc = Cache);
    void saveImage(const QString filePath, const QImage image, int quality = -1, UseCache uc = Cache);

	void resizeAndNotify(QImage img, const QString filePath, const QSize size, UseCache uc = Cache);

	bool cacheLookup(const QString filePath, const QSize size, QImage &image);
	void cacheStore(const QString filePath, const QImage image);

	void setCacheSizes(int bigKb, int mediumKb, int smallKb);
	void setCacheMetrics(int bigW, int bigH, int smallW, int smallH, bool intermediates);

	void waitForPendingResizeTasks();

protected:
    ImageIOCore(QObject *parent = 0);
    virtual ~ImageIOCore();

private:
	QMutex cacheMutex, resizeMutex;
	QCache<QString, QImage> big, medium, small;
	QLinkedList<QFuture<void> > resizeTasks;
	QMap<QString, QNetworkAccessManager *> registeredServers;
	QMap<QString, int> maxAllowedConnections;
	QMap<QString, int> currentConnections;

	int bigW, bigH, smallW, smallH;
	bool createIntermediateStage;
    int cost(QSize size)
    {
        return (size.width() * size.height() * 4 / 1024);
    };
    QCache<QString,QImage> *selectCache(QSize size);
    void intermediateStage(QImage img, const QString filePath, const QSize size);
    void resizeTask(const QFuture<void> task);

public:
Q_SIGNALS:
	void imageLoaded(const QString filePath, const QImage image);
	void imageSaved(const QString filePath);
	void ioError(const QString filePath, const QString errorMsg);
};

#endif // IMAGEIOCORE_H
