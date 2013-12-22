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


#ifndef ICONCACHE_H
#define ICONCACHE_H

#include <imageio.h>

#include <QString>
#include <QStringList>
#include <QCache>
#include <QHash>
#include <QSet>
#include <QPixmap>
#include <QIcon>

class IconCache : public QObject, public ImageIO::CallbackInterface
{
    Q_OBJECT

public:
    IconCache(QObject *parent = 0);
    virtual ~IconCache();

    bool get(const QString filePath, QIcon &icon);
    bool get(const QString filePath, QIcon &icon, const QSize size);
    bool readNextErrorMsg(QString &filePath, QString &errorMsg);
    void setIconSize(const QSize size);
    QSize getIconSize() { return (iconSize); }

    //static QPixmap sizeGuard(const QPixmap pixmap, const QSize size, const int frame = 0);

 	virtual void receiveImage(const QString filePath, QImage image);
   	virtual void receiveErrorMsg(const QString filePath, const QString errorMsg);

private:
    QSize						iconSize;
    QHash<QString, QPixmap *>	active;
    QList<QStringList> 			errorMessages;
    QString						currentDir;
    int 						pendingCounter;

public Q_SLOTS:
	void dirChanged(const QString &path);

Q_SIGNALS:
    void iconReady(const QString filePath, const QIcon icon);
    void finishedLoading();

};


#endif // ICONCACHE_H
