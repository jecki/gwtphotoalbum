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


#include "albumview.h"
#include "toolbox.h"
#include "imageitem.h"
#include "imageitemdelegate.h"
#include "imagelistmodel.h"

#include <QString>
#include <QDrag>
#include <QMimeData>
#include <QDragEnterEvent>
#include <QDragLeaveEvent>
#include <QDragMoveEvent>
#include <QDropEvent>
#include <QUrl>
#include <QPainter>
#include <QSize>

#ifndef NDEBUG
#include <QDebug>
#endif

// For drawing custom selection: use QItemDelegate


AlbumView::AlbumView(QWidget *parent)
    : QListView(parent), itemDelegate(this)
{
	setMovement(QListView::Snap);
	setDropIndicatorShown(true);
	setItemDelegate(&itemDelegate);
	//setIconSize(ImageItem::Thumbnail_Size);
}

AlbumView::~AlbumView()
{

}


void AlbumView::startDrag(Qt::DropActions supportedActions)
{
	QAbstractItemModel  *itemModel = model();
	QItemSelectionModel *selection = selectionModel();
	QModelIndexList		selected = selection->selectedIndexes();
	QModelIndex			current = selection->currentIndex();

	if (!selected.contains(current))
		selected.append(current);

	QMimeData *mime = itemModel->mimeData(selected);
	if (!mime)
		return;

	QVariant decoration = itemModel->data(current, Qt::DecorationRole);
	Q_ASSERT(decoration.type() == QVariant::Pixmap);
	QPixmap pixmap = sizeGuard(qvariant_cast<QPixmap>(decoration), iconSize());

	QByteArray	imageData;
	QDataStream	stream(&imageData, QIODevice::WriteOnly);
	stream << pixmap;
	mime->setData("image/x-pixmap", imageData);

	QDrag *drag = new QDrag(this);
	drag->setPixmap(pixmap);
	drag->setMimeData(mime);
	drag->setHotSpot(QPoint(pixmap.width()/2, pixmap.height()/2));

	drag->exec(supportedActions, Qt::CopyAction);
}


void AlbumView::dragEnterEvent(QDragEnterEvent* event)
{
	foreach(QString type, event->mimeData()->formats()) {
		// qDebug() << type;
	}

	if (event->mimeData()->hasFormat("text/uri-list")) {
		foreach(QUrl url, event->mimeData()->urls()) {
			qDebug() << url.path() << event->mimeData()->hasImage();
		}
		itemDelegate.highlightDropPosition(myIndexAt(event->pos()));
		event->acceptProposedAction();
	} else {
		itemDelegate.highlightDropPosition(myIndexAt(event->pos()));
	}

}


void AlbumView::dragLeaveEvent(QDragLeaveEvent* event) {
	(void) event;
	itemDelegate.clearDropPosition();
}


void AlbumView::dragMoveEvent(QDragMoveEvent* event) {
    if (event->mimeData()->hasFormat("image/x-pixmap")) {
        event->setDropAction(Qt::CopyAction);
		// qDebug() << "AlbumView::dragMoveEvent index: " << myIndexAt(event->pos());
		itemDelegate.highlightDropPosition(myIndexAt(event->pos()));
        event->accept();
    } else
        event->ignore();
}


void AlbumView::dropEvent(QDropEvent* event)
{
	itemDelegate.clearDropPosition();
	if (event->proposedAction() == Qt::IgnoreAction) {
		event->acceptProposedAction();
	} else if (event->mimeData()->hasFormat("text/uri-list")) {
		qDebug() << event->pos();
		if (event->source() == this) {
//TODO: Implement moving of images inside the image view
			event->ignore();
		} else {

			qDebug() << "AlbumView::dropEvent index: " << myIndexAt(event->pos());
			model()->dropMimeData(event->mimeData(), Qt::CopyAction,
					-1, -1, myIndexAt(event->pos()));
		}
		if (event->proposedAction() != Qt::CopyAction) {
			event->setDropAction(Qt::CopyAction);
			event->accept();
		} else {
			event->acceptProposedAction();
		}
	}
	event->ignore();
}


QModelIndex AlbumView::myIndexAt(const QPoint &pos) {
	QModelIndex index = indexAt(pos);
	if (index.row() == -1 && model()->rowCount() > 0) {
//		int x = pos.x();
//		int y = pos.y();
//		int w = iconSize().width();
//		int h = iconSize().height();
//TODO: Return proper index if pos points to whitespace.

	}
	return (index);
}

