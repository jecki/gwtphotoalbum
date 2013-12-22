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


#include "imagelistmodel.h"
#include "iconcache.h"

#include <QStringList>
#include <QMimeData>
#include <QUrl>
#include <QStringList>
#include <QSet>

#include "toolbox.h"
#include "../common/imageitem.h"


#ifndef NDEBUG
#include <QDebug>
#endif


ImageListModel::ImageListModel(QList<ImageItem *> &images, QWidget *parent)
    : QAbstractListModel(parent), imageList(images), imageNames()
{
	foreach (ImageItem *it, imageList) {
		imageNames.insert(it->path());
	}
}

ImageListModel::~ImageListModel()
{

}


QVariant ImageListModel::data(const QModelIndex &index, int role) const
{
	Q_ASSERT(index.row() < imageList.length());

	ImageItem *it = imageList.at(index.row());
	QStringList tip;

	switch (role) {
	case Qt::DisplayRole:
		return (QString());
		break;
	case Qt::DecorationRole:
		return (sizeGuard(QPixmap::fromImage(it->image(ImageItem::THUMBNAIL)),
				ImageItem::Thumbnail_Size, FRAME_SIZE));
		//return (QPixmap::fromImage(it->image(ImageItem::THUMBNAIL)));
		break;
	case Qt::ToolTipRole:
		if (!it->caption().isEmpty())
			tip << it->caption() << "\n";
		tip << it->path() << " (" << sizeStr(it->size(ImageItem::IMAGE)) << ") ";
		return (tip.join(""));
		break;
	case Qt::SizeHintRole:
		return (QSize(ImageItem::Thumbnail_Size.width() +  2 * FRAME_SIZE,
				      ImageItem::Thumbnail_Size.height() + 2 * FRAME_SIZE));
	default:
		return (QVariant());
	}
}


//bool ImageListModel::setData(const QModelIndex &index, const QVariant &value, int role)
//{
//
//}


Qt::ItemFlags ImageListModel::flags(const QModelIndex &index) const
{
	Qt::ItemFlags defaultFlags = QAbstractListModel::flags(index);
	return (Qt::ItemIsSelectable|Qt::ItemIsDragEnabled|Qt::ItemIsDropEnabled|
		    Qt::ItemIsEnabled|defaultFlags); // |Qt::IsEditable
}

//bool QAbstractItemModel::insertRows (int row, int count, const QModelIndex & parent = QModelIndex())
//{
//
//}
//
//bool ImageListModel::removeRows(int row, int count, const QModelIndex &parent)
//{
//
//}

bool ImageListModel::dropMimeData(const QMimeData *data, Qt::DropAction action,
                  int row, int column, const QModelIndex &parent)
{
	qDebug() << "dropMimeData: " << row << ", " << column << ";  " << parent.row();

	if (action == Qt::IgnoreAction || !data->hasUrls())  return (false);

	emit layoutAboutToBeChanged();
	if (row < 0 && column < 0)  row = parent.row();
	if (row < 0)  row = 0;
	Q_ASSERT_X(row <= imageList.size(),
			   "ImageListModel::dropMimeData", "Invalid drop index");

	foreach(QUrl url, data->urls()) {
		QString path = url.path();
		if (!imageNames.contains(path)) {
			qDebug() << path;
			ImageItem *item = new ImageItem(url.path());
			imageList.insert(row++, item);
			imageNames.insert(path);
		}
	}
	emit layoutChanged();

	return (true);
}


QMimeData *ImageListModel::mimeData(const QModelIndexList &indexes) const
{
	QMimeData *data = new QMimeData();
	Q_ASSERT_X(data != NULL, "ImageListModel::mimeData",
			   " could not allocate memory from heap");
	QList<QUrl> urls;
	foreach (QModelIndex idx, indexes) {
		urls.append(QUrl(imageList[idx.row()]->path()));
	}
	data->setUrls(urls);
	return (data);
}


QStringList ImageListModel::mimeTypes() const
{
	QStringList types = QAbstractListModel::mimeTypes();
	types.append(QString("text/uri-list"));
	return (types);
}


int ImageListModel::rowCount(const QModelIndex &parent) const
{
	(void) parent;
	return (imageList.size());
}


Qt::DropActions ImageListModel::supportedDropActions() const
{
	return (Qt::MoveAction|Qt::CopyAction);
}
