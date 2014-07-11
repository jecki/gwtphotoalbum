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


#ifndef FILTERPROXY_CLASS_H_
#define FILTERPROXY_CLASS_H_

#include "albumcreator.h"

#include <QImageReader>
#include <QMimeData>
#include <QList>
#include <QUrl>
#include <QFileInfo>
#include <QString>

#ifndef NDEBUG
#include <QDebug>
#include <iostream>
#endif

/*!
 *	\class FilterProxy
 *
 *	\brief FilterProxy filters directories and valid image files from the file system.
 *
 */
class AlbumCreator::FilterProxy : public QSortFilterProxyModel {
	Q_OBJECT
public:
    FilterProxy(QFileSystemModel *source, QObject *parent = 0) :
            QSortFilterProxyModel(parent) // , placeholder(":/:/images/image_placeholder.png")
    {
        this->source = source;
        setSourceModel(source);
    }

    /*!
     *  \fn QString fileName(const QModelIndex &index) const
     *
     *  Returns the file name corresponding to a particular model
     *  index as string.
     */
    inline QString fileName(const QModelIndex &index) const
    {
    	return (data(index, QFileSystemModel::FileNameRole).toString());
    }

    /*!
     * 	\fn bool hasSupportedFormat(const QString &fileName)
     *
     *  Returns true if the the suffix of the file name indicates a
     *  supported image file format.
     */
    static bool hasSupportedFormat(const QString &fileName)
    {
        QString name = fileName.toUpper();
    	foreach(QString supportedFormat, imageFormats) {
            if (name.endsWith(supportedFormat))
                return (true);
        }
        return (false);
    }

    /*!
     *  \fn QModelIndex indexOfPath(const QString &path)
     *
     *  Returns the model index corresponding to the given file system path.
     */
    inline QModelIndex indexOfPath(const QString &path)
    {
        return (this->mapFromSource(source->index(path)));
    }


    /*!
     *  \fn QString path(const QModelIndex &index)
     *
     *  Returns the path of the file system item at the given model index.
     */
    inline QString path(const QModelIndex &index)
    {
    	return (data(index, QFileSystemModel::FilePathRole).toString());

    }

    /*!
     *  \fn bool isDir(const QModelIndex &index)
     *
     *  Returns true, if the file system item at the given model index
     *  is a directory.
     */
    inline bool isDir(const QModelIndex &index)
    {
    	return (source->isDir(mapToSource(index)));
    }

    /*!
     * \fn void updateData(QModelIndex &topLeft, QModelIndex &bottomRight)
     *
     * emits the dataChanged(QModelIndex &, QModelIndex &) signal
     */
    inline void dataUpdated(QModelIndex &topLeft, QModelIndex &bottomRight) {
    	emit dataChanged(topLeft, bottomRight);
    }

    /*!
     * \fn Qt::ItemFlags flags (const QModelIndex & index) const
     *
     * returns the flags of the item at position 'index'. Qt:ItemIsDragEnabled
     * will be set if 'index' is valid and the item's filename is that of
     * a supported image format.
     */
    virtual Qt::ItemFlags flags (const QModelIndex & index) const {
    	Qt::ItemFlags defaultFlags = QSortFilterProxyModel::flags(index);
    	if (index.isValid() && hasSupportedFormat(fileName(index))) {
    		return (Qt::ItemIsDragEnabled | defaultFlags);
    	} else {
    		return (defaultFlags);
    	}
    }


    virtual QStringList mimeTypes() const {
    	QStringList types;
    	types << "text/uri-list" << "image/*";
    	return (types);
    }

    virtual QMimeData *mimeData(const QModelIndexList &indexes) const {
    	QMimeData *mimeData = new QMimeData();
    	QList<QUrl>	urls;
    	QIcon icon;
    	// QByteArray encodedData;
    	// add images here (AlbumCreator::iconCache.retrieveOriginal) or only fileNames?
    	foreach (QModelIndex idx, indexes) {
    		urls << QUrl(data(idx, QFileSystemModel::FilePathRole).toString());
    		//qDebug() << urls[urls.size()-1].toLocalFile();
    	}
    	mimeData->setUrls(urls);
//    	if (urls.size() > 0 && urls[0].isLocalFile()) {
//    		QString path = urls[0].toLocalFile();
//    		QFileInfo fi(path);
//    		if (fi.isFile()) {
//    			if (AlbumCreator::iconCache.get(path, icon)) {
//    				mimeData->setImageData(icon.pixmap(AlbumCreator::iconCache.getIconSize()).toImage());
//    			} else {
//    				mimeData->setImageData(placeholder);
//    			}
//    		}
//    	}
    	return mimeData;
    }

protected:
    /*!
     *  \fn filterAcceptsRow(int source_row, const QModelIndex & source_parent) const
     *
     *  Returns true (i.e. accepts) a row, if it is either a directory and
     *  not the "." directory or an image file of a supported image file type.
     */
    bool filterAcceptsRow(int source_row,
                          const QModelIndex & source_parent) const
    {
    	QModelIndex src_index = source->index(source_row, 0, source_parent);
        QString fileName =  source->data(src_index,
        		QFileSystemModel::FileNameRole).toString();

    	if (source->isDir(src_index)) {
    		if (fileName != ".")
    			return (true);
    		else
    			return (false);
        }

        return hasSupportedFormat(fileName);
    }

private:
    QFileSystemModel      *source;
    static QList<QString> imageFormats;
    //QImage                placeholder;
};


#endif /* FILTERPROXY_CLASS_H_ */
