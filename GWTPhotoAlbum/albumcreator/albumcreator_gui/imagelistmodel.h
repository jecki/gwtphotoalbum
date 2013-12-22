/*
 * Copyright 2012 Eckhart Arnold (eckhart_arnold@hotmail.com).
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

#ifndef IMAGELISTMODEL_H
#define IMAGELISTMODEL_H

#include "../common/imageitem.h"
#include <Qt>
#if QT_VERSION >= 0x050000
#include <QtWidgets/QWidget>
#else
#include <QtGui/QWidget>
#endif
#include <QAbstractListModel>
#include <QList>
#include <QSet>
#include <QString>


class ImageListModel : public QAbstractListModel
{
    Q_OBJECT

public:
    static const int	FRAME_SIZE = 10;

    ImageListModel(QList<ImageItem *> &images, QWidget *parent = 0);
    ~ImageListModel();

    virtual QVariant data(const QModelIndex &index, int role = Qt::DisplayRole) const;
//    virtual bool setData(const QModelIndex &index, const QVariant &value, int role);
    virtual Qt::ItemFlags flags(const QModelIndex &index) const;

//    virtual bool insertRows ( int row, int count, const QModelIndex & parent = QModelIndex());
//    virtual bool removeRows(int row, int count, const QModelIndex &parent);

    virtual bool dropMimeData(const QMimeData *data, Qt::DropAction action,
                              int row, int column, const QModelIndex &parent);
    virtual QMimeData *mimeData(const QModelIndexList &indexes) const;
    virtual QStringList mimeTypes() const;
    virtual int rowCount(const QModelIndex &parent) const;
    virtual Qt::DropActions supportedDropActions() const;

private:
    QList<ImageItem *> 	&imageList;
    QSet<QString>       imageNames;
};

#endif // IMAGELISTMODEL_H
